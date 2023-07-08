package org.tooling.core.base;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.tooling.core.actions.SeoMonitorCommonActions;
import org.tooling.selenium.BrowserUtils;
import org.tooling.selenium.SeleniumUtils;
import org.tooling.helper.Retry;
import org.tooling.core.user.SeoMonitorTestUsers;
import org.tooling.util.TestUtils;

import java.lang.reflect.Method;


@Log4j2
public abstract class SeoMonitorBaseTest {

    private static final int passedTests = 0;
    private static int finishedTests = 0;

    private final ThreadLocal<Method> currentMethod = new ThreadLocal<>();
    private final String className = this.getClass().getSimpleName();
    protected SeoMonitorCommonActions seoMonitorActions;
    private String currentDisplayType;
    private String browserType;

    public SeoMonitorBaseTest(String browserType) {
        setupBeforeClass(browserType, "dev");
        setupBeforeMethod(null);
    }

    public SeoMonitorBaseTest(String browserType, String env) {
        setupBeforeClass(browserType, env);
        setupBeforeMethod(null);
    }

    public SeoMonitorBaseTest() {
    }

    public abstract void localBeforeClass();

    public abstract void localBeforeMethod();

    public abstract void localAfterClass();

    public abstract void localAfterMethod();

    public abstract ReuseType shouldReuseBrowserBetweenTests();

    public abstract RunType getLocalBeforeClassRunType();

    protected SeoMonitorPageCollection seoMonitorPages() {
        return CurrentTest.getSeoMonitorPages();
    }

    protected BrowserUtils getBrowserUtils() {
        return CurrentTest.getBrowserUtils();
    }


    protected void closeDriver() {
        CurrentTest.clear();

        SeoMonitorTestUsers.setCurrentUser(null);
        if (TestUtils.getPropertyAsBoolean("selenium.closeBrowser")) {
            log.info("Quitting driver");
            SeleniumUtils.quitDriver();
        }
    }

    private boolean isSeleniumSessionActive() {
        if (getBrowserUtils() == null) {
            return false;
        }
        try {
            getBrowserUtils().getDriver().findElements(By.tagName("h1"));
            return true;
        } catch (Exception e) {
            log.warn("Selenium session is not active", e);
            return false;
        }
    }

    private String getCustomUserAgent() {
        String uaPrefix = TestUtils.getProperty("selenium.defaultUA");

        if (uaPrefix == null || uaPrefix.trim().isEmpty()) {
            return null;
        }

        String name = this.getClass().getSimpleName();
        if (currentMethod.get() != null
                && ReuseType.DO_NOT_RESUSE.equals(shouldReuseBrowserBetweenTests())) {
            name = currentMethod.get().getName();
        }

        String ua = uaPrefix + " - " + name + " - " + SeleniumUtils.getDisplayForBrowser(browserType);

        return ua;
    }

    private void setEnvironment(String env) {
        System.setProperty("SM.env", env);
    }

    private boolean handleFailures(ITestResult result, TestUtils.Result testResult, String className,
                                   String methodName) {
        boolean isBrowserActive = true;

        try {

            log.info("exception - " + (result.getThrowable() != null ? result.getThrowable().getMessage()
                    : "none"));
            log.info(ExceptionUtils.getStackTrace(result.getThrowable()));
            if (result.getThrowable() != null
                    && result.getThrowable().getClass().isInstance(UnhandledAlertException.class)) {
                String exMessage = result.getThrowable().getMessage();
            }
        } catch (Exception e) {
            log.error("Exception while trying to get exception info", e);
        }

        try {
            log.warn("Test (" + methodName + "-" + SeleniumUtils.getBrowserType()
                    + ") has failed and will be checked for retrying. Current url is - "
                    + seoMonitorActions.getCurrentUrl());
        } catch (Exception e) {
            log.info("Error while trying to get URL from webdriver", e);
            isBrowserActive = false;
            log.warn("Test (" + methodName + "-" + SeleniumUtils.getBrowserType()
                    + ") has failed and will be checked for retrying.");
        }

        if (isBrowserActive) {

            seoMonitorActions.printConsoleErrors();

        }

        return isBrowserActive;
    }

    private void reuseBrowser(boolean isBrowserActive) {
    }

    private String getMethodName(ITestResult result) {

        String methodName = result == null ? null : result.getTestName();
        if (methodName == null) {
            log.debug("got method name '" + methodName + "' from testng");
        }
        if (currentMethod.get() == null) {
            log.warn(
                    "no method stored from before method and no info in result object provided by testng");
            return null;
        } else {
            return currentMethod.get().getName();
        }

    }

    private void initializeBrowserUtils(String className) {

        final BrowserUtils[] browserUtils = {null};
        try {
            Retry.times(2).withTimeout(30).run(() -> {
                browserUtils[0] =
                        new BrowserUtils(SeleniumUtils.getDriver(className, browserType, getCustomUserAgent()));
            });
        } catch (Exception e) {
            log.warn("Unable to create webdriver for {}", browserType, e);
            Retry.runAndIgnoreErrors(this::closeDriver);
        }

        if (browserUtils[0] != null) {
            CurrentTest.include(browserUtils[0]);
        } else {
            String tempMethod =
                    (this.currentMethod.get() == null) ? null : this.currentMethod.get().getName();
            log.warn("Test (" + tempMethod + "-" + browserType
                    + ") has failed while trying to create a new selenium session.");

            synchronized (TestUtils.class) {
                if (TestUtils.getAndIncrementCountFor("WebDriver creation errors") >= TestUtils
                        .getPropertyAsInt("selenium.reset.threshold")) {
                    log.warn(
                            "Selenium session creation errors crossed threshold. closing all existing selenium sessions in effort to recover");
                    TestUtils.deleteCountsFor("WebDriver creation errors");
                    SeleniumUtils.quitAllDrivers();
                    TestUtils.sleep(30);
                }
            }

            Assert.fail("Error creating a webdriver");
        }
    }

    private void initializePagesAndActions() {
        seoMonitorActions = CurrentTest.getSeoMonitorActions();
    }

    private void checkLocalBeforeClassMethod(String className, Boolean... skip) {
        if (!TestUtils.getFirstValueOr(skip, false)) {
            String key = "Class " + this.getClass().getSimpleName() + " " + currentDisplayType;
            log.debug(key + " - " + getLocalBeforeClassRunType() + " - " + TestUtils.getCountFor(key));
            log.debug("to skip local beforeclass method - "
                    + (RunType.ONLY_ONCE.equals(getLocalBeforeClassRunType())
                    && (TestUtils.getCountFor(key) > 0)));

            if (RunType.ONLY_ONCE.equals(getLocalBeforeClassRunType())
                    && (TestUtils.getCountFor(key) > 0)) {
                return;
            }

            try {
                localBeforeClass();
            } finally {
                TestUtils.incrementCountFor(key);
            }

        }
    }

    private void init(Boolean... skip) {
        try {
            log.info("browser type is '" + browserType + "'.");


            log.info("Creating a new browser session");
            initializeBrowserUtils(className);
            log.info("Creating page and actions objects");
            initializePagesAndActions();

            currentDisplayType = SeleniumUtils.getDisplaySize();

            log.info("Checking if any local beforeClass method needs to be executed");
            checkLocalBeforeClassMethod(className, skip);
        } catch (RuntimeException e) {
            log.debug("Error while initializating pages and actions", e);
        }

    }

    private TestUtils.Result getTestResult(int status) {
        switch (status) {
            case ITestResult.FAILURE:
                return TestUtils.Result.FAIL;
            case ITestResult.SKIP:
                return TestUtils.Result.SKIPPED;
            case ITestResult.STARTED:
                return TestUtils.Result.STARTED;
            case ITestResult.SUCCESS:
                return TestUtils.Result.PASS;
            default:
                log.warn("unknown status - " + status);
                return TestUtils.Result.UNKNOWN;
        }
    }

    @Parameters({"BrowserType", "env"})
    @BeforeClass(alwaysRun = true)
    public void setupBeforeClass(@Optional("chrome") String browserType,
                                 @Optional("dev") String environment) {
        try {

            this.browserType = browserType;
            String defaultBrowserType = TestUtils.getProperty("selenium.default.browserType");
            if (defaultBrowserType != null && !defaultBrowserType.isEmpty()) {
                this.browserType = defaultBrowserType;
            }

            String envToUse = TestUtils.getProperty("selenium.default.env");
            if (envToUse != null && !envToUse.isEmpty()) {
                setEnvironment(envToUse);
            } else if (!"dev".equalsIgnoreCase(environment)) {
                setEnvironment(environment);
            }

        } catch (RuntimeException e) {
            log.debug("error before class", e);
        }

    }

    @BeforeMethod(alwaysRun = true)
    public void setupBeforeMethod(Method method) {
        try {
            this.currentMethod.set(method);

            String methodName = (method == null) ? null : method.getName();
            log.info("Current method - '" + methodName + "-" + SeleniumUtils.getBrowserType() + "'.");

            if (!isSeleniumSessionActive()) {
                log.info("there is no active selenium session");
                closeDriver();
                log.info("initializing new selenium session for the current test");
                init();
            }

            log.info("Triggering local beforeMethod for the current test");
            localBeforeMethod();
        } catch (RuntimeException e) {
            log.debug("Error before method", e);
        }
    }


    private void tearDownAfterMethodFinally(boolean isBrowserActive, String methodName,
                                            TestUtils.Result testResult) {
        try {
            if (isBrowserActive) {
                try {
                    localAfterMethod();
                } catch (Exception e) {
                    log.error("Exception in local after method", e);
                }

            }

            CurrentTest.clearAllObjects();

            reuseBrowser(isBrowserActive);

            currentMethod.remove();
        } catch (RuntimeException e) {
            log.error("Something went wrong in the after method finally block", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void teardownAfterMethod(ITestResult result) {

        boolean isBrowserActive = true;
        String methodName = null;
        TestUtils.Result testResult = getTestResult(result.getStatus());

        try {

            log.info("{} tests passed out of {} tests", passedTests, ++finishedTests);

            methodName = getMethodName(result);

            if (!result.isSuccess()) {
                isBrowserActive = handleFailures(result, testResult, className, methodName);
            }

        } catch (Exception e) {
            log.error("Error in after method", e);
        } finally {
            tearDownAfterMethodFinally(isBrowserActive, methodName, testResult);
        }
    }

    private void tearDownAfterClassFinally() {
        try {
            Retry.runAndIgnoreErrors(this::closeDriver);
        } catch (RuntimeException e) {
            log.error("Something went wrong in the after class finally block", e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownAfterClass() {
        log.debug("after class - " + className + " - " + currentDisplayType);
        try {
            localAfterClass();
        } catch (RuntimeException e) {
            log.debug("Error in local after class", e);
        } finally {
            tearDownAfterClassFinally();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        try {
            if (TestUtils.getPropertyAsBoolean("selenium.closeBrowser")) {
                SeleniumUtils.quitAllDrivers();
            }

        } catch (RuntimeException e) {
            log.debug("Error in after suite", e);
        }

    }

    protected enum ReuseType {
        DO_NOT_RESUSE, CLEAR_COOKIES_ONLY, CLEAR_COOKIES_AND_UNBLOCK, RETAIN_COOKIES_NO_UNBLOCK
    }

    protected enum RunType {
        ONLY_ONCE, ONCE_EACH_BROWSER_SESSION
    }

}
