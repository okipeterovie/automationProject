package org.tooling.core.actions;


import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.tooling.util.DataUtils;
import org.tooling.helper.Retry;
import org.tooling.util.TestUtils;
import org.tooling.helper.Waiter;
import org.tooling.exception.NotSupportedException;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.selenium.BrowserUtils;
import org.tooling.selenium.SeleniumUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

@Log4j2
public abstract class BaseActions {

    protected ThreadLocal<BrowserUtils> localBrowserUtils;

    protected BaseActions(ThreadLocal<BrowserUtils> browserUtils) {
        this.localBrowserUtils = browserUtils;
    }


    protected void verifyPage(SeoMonitorBasePage page, boolean reportSuccess) {
        verifyPage(page, reportSuccess, null);
    }

    protected void verifyPage(SeoMonitorBasePage page, boolean reportSuccess, String message) {
        String name = page.getClass().getSimpleName();
        try {
            page.waitTillLoaded();
            page.isCurrentPage(true);
            if (reportSuccess) {
                String actualMessage = (message != null && !message.isEmpty()) ? message : "Verify the page called [" + name + "]";
            }
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            String actualMessage = (message != null && !message.isEmpty()) ? message : "The page [" + name + "] could not be verified.";
            Assert.fail(actualMessage, e);
        }
    }

    protected void verifyPage(SeoMonitorBasePage page) {
        verifyPage(page, false);
    }

    protected void navigateTo(String urlOrPropertyKey, Boolean... toLookup) {

        String actualUrl = urlOrPropertyKey;

        if (TestUtils.getFirstValueOr(toLookup, false)) {
            actualUrl = TestUtils.getUrl(urlOrPropertyKey);
        }

        log.info("navigating to '" + actualUrl + "'");

        localBrowserUtils.get().getDriver().navigate().to(actualUrl);
    }

    protected void navigateTo(String urlKey, Boolean toLookup, SeoMonitorBasePage expectedPage) {
        navigateTo(urlKey, toLookup);
        verifyPage(expectedPage);
    }

    protected void reloadPage() {
        localBrowserUtils.get().getDriver().navigate().refresh();
    }

    protected void reloadPage(SeoMonitorBasePage expectedPage) {
        localBrowserUtils.get().getDriver().navigate().refresh();
        verifyPage(expectedPage);
    }

    protected void navigateBack() {
        localBrowserUtils.get().getDriver().navigate().back();
    }

    protected void navigateForward() {
        localBrowserUtils.get().getDriver().navigate().forward();
    }

    protected void openNewTab() {
        localBrowserUtils.get().getJsExecutor().executeScript("window.open();");
    }

    protected void switchToNewTab() {
        int count = getNumberOfOpenTabs();
        openNewTab();
        switchTabs(count);
    }


    protected void switchTabs(int tabNumber) {
        WebDriver driver = localBrowserUtils.get().getDriver();
        TestUtils.sleep(1);
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        if (tabNumber < 0 || tabNumber >= tabs.size()) {
            throw new NotSupportedException("invalid tab number");
        }
        driver.switchTo().window(tabs.get(tabNumber));
    }

    protected String getCurrentWindowHandle() {
        return localBrowserUtils.get().getDriver().getWindowHandle();
    }

    protected List<String> getAllWindowHandles() {
        WebDriver driver = localBrowserUtils.get().getDriver();
        TestUtils.sleep(1);
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        return tabs;
    }

    protected int getNumberOfOpenTabs() {
        WebDriver driver = localBrowserUtils.get().getDriver();
        return driver.getWindowHandles().size();
    }

    protected void waitAndSwitchTabs(int i) {
        Waiter.forSeconds(TestUtils.SHORT_WAIT).pollEvery(500)
                .waitFor(() -> localBrowserUtils.get().getDriver().getWindowHandles().size() >= (i + 1))
                .perform();
        switchTabs(i);
    }

    protected void closeCurrentTabAndGoToTab(int tabNumber) {
        closeCurrentTab();
        switchTabs(tabNumber);
    }

    private void closeCurrentTab() {
        localBrowserUtils.get().getDriver().close();
    }

    protected String getCurrentUrl() {
        BrowserUtils localUtils = localBrowserUtils.get();
        WebDriver driver = (localUtils != null) ? localUtils.getDriver() : null;
        String url = (driver != null) ? driver.getCurrentUrl() : null;

        TestUtils.setLastKnownUrl(url);
        return url;
    }

    protected void runJSSnippet(String jsCode) {
        String response = null;
        try {
            response =
                    (String) ((JavascriptExecutor) localBrowserUtils.get().getDriver()).executeScript(jsCode);
            log.warn("Response after script execution: " + response);
        } catch (ClassCastException e) {
            log.warn("Unable to cast", e);
        }

        log.debug(response);
    }

    protected String getPageTitle() {
        return localBrowserUtils.get().getDriver().getTitle();
    }

    protected void negateAndVerify(String actualValue, String expectedValue, String description) {
        String negatedValue = TestUtils.negateValue(expectedValue);
        Assert.assertEquals(actualValue, negatedValue, description);
    }

    protected void negateAndVerify(Object actualValue, Object expectedValue, String description) {
        String negatedValue = TestUtils.negateValue(expectedValue.toString());
        Assert.assertEquals(actualValue.toString(), negatedValue, description);
    }

    protected void negateAndVerify(Boolean actualValue, Boolean expectedValue, String description) {
        Boolean negatedValue = TestUtils.negateValue(expectedValue);
        Assert.assertEquals(actualValue, negatedValue, description);
    }

    protected void negateAndVerify(Integer actualValue, Integer expectedValue, String description) {
        Integer negatedValue = TestUtils.negateValue(expectedValue);
        Assert.assertEquals(actualValue, negatedValue, description);
    }

    protected void negateAndVerify(Double actualValue, Double expectedValue, String description) {
        Double negatedValue = TestUtils.negateValue(expectedValue);
        Assert.assertEquals(actualValue, negatedValue, description);
    }

    protected void verify(String actualValue, String expectedValue, String description) {
        Assert.assertEquals(actualValue, expectedValue, description);
    }


    protected void verify(Object actualValue, Object expectedValue, String description) {
        Assert.assertEquals(actualValue, expectedValue, description);
    }

    protected void verifySuccessOf(boolean expectSuccess, Runnable block, String description) {
        try {
            block.run();
        } catch (RuntimeException e) {
            log.error(e);
            if (expectSuccess) {
                throw e;
            }
        }
    }

    protected void verifySuccessOf(Runnable block, String description) {
        verifySuccessOf(true, block, description);
    }

    protected void verify(Boolean actualValue, Boolean expectedValue, String description) {
        Assert.assertEquals(actualValue, expectedValue, description);
    }

    protected void verify(Integer actualValue, Integer expectedValue, String description) {
        Assert.assertEquals(actualValue, expectedValue, description);
    }

    protected void verify(Double actualValue, Double expectedValue, String description) {
        Assert.assertEquals(actualValue, expectedValue, description);
    }


    protected void waitForUrlChange(String url, int seconds, Runnable failureHandler) {
        Waiter.WaitBuilder builder = Waiter.forSeconds(seconds).pollEvery(500)
                .waitFor(() -> !localBrowserUtils.get().getDriver().getCurrentUrl().equals(url));

        if (failureHandler != null) {
            builder = builder.onFailure(failureHandler);
        }

        builder.perform();
    }

    protected void waitForUrlChange(String url, int seconds) {
        waitForUrlChange(url, seconds, null);
    }

    protected boolean waitForUrlChange(int seconds) {
        String url = getCurrentUrl();
        final boolean[] result = {false};

        Waiter.forSeconds(seconds).pollEvery(500)
                .waitFor(() -> !localBrowserUtils.get().getDriver().getCurrentUrl().equals(url))
                .onSuccess(() -> result[0] = true).onFailure(() -> result[0] = false).perform();

        return result[0];
    }

    protected <T> void waitForValueChange(Supplier<T> supplier, T currentValue, int seconds) {
        Waiter.forSeconds(seconds).pollEvery(500).waitFor(() -> !currentValue.equals(supplier.get()));
    }

    protected <T> void waitFor(Supplier<T> supplier, Predicate<T> predicate, int seconds) {
        Waiter.forSeconds(seconds).pollEvery(500).waitFor(() -> predicate.test(supplier.get()));
    }

    protected void printConsoleErrors() {
        WebDriver driver = localBrowserUtils.get().getDriver();
        if (driver == null) {
            log.info("Console logs not available. Driver is null.");
            return;
        }

        if (SeleniumUtils.getBrowserType() == null
                || !SeleniumUtils.getBrowserType().toLowerCase().contains("chrome")) {
            log.warn("Current browser might not allow access to console logs ");
            return;
        }

        try {
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            if (logEntries != null) {
                if (!logEntries.getAll().isEmpty())
                    log.info("Printing browser console error messages...");
                else {
                    log.info("Log entries are empty. Nothing to print");
                }
                for (LogEntry logEntry : logEntries) {
                    if (logEntry.getMessage().toLowerCase().contains("error")
                            || Level.SEVERE.equals(logEntry.getLevel())) {
                        log.info("Found error logs in console.");
                        log.warn(("Error message in console: " + logEntry.getMessage()));
                    } else {
                        log.info("Error logs were not found in console.");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not generate browser console error logs", e);
        }
    }

    protected boolean isAlertPresent() {
        try {
            Retry.once(() -> localBrowserUtils.get().getMinimalWait()
                    .until(ExpectedConditions.alertIsPresent()));
            return true;
        } catch (NoAlertPresentException ex) {
        } catch (RuntimeException e) {
            log.error("Alert not present");
        }

        return false;
    }

    protected void acceptAlert() {
        Alert alert = localBrowserUtils.get().getDriver().switchTo().alert();
        alert.accept();
    }

    protected void scrollBy(int x, int y) {
        localBrowserUtils.get().getJsExecutor()
                .executeScript(DataUtils.getJSSnippetFor("scroll to coordinates"), x, y);
    }

    protected void scrollHorizontallyBy(int x) {
        localBrowserUtils.get().getJsExecutor()
                .executeScript(DataUtils.getJSSnippetFor("scroll to coordinates"), x, 0);
    }

    protected void scrollVerticallyBy(int y) {
        localBrowserUtils.get().getJsExecutor()
                .executeScript(DataUtils.getJSSnippetFor("scroll to coordinates"), 0, y);
    }

}
