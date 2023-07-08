package org.tooling;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.tooling.core.base.SeoMonitorQaTestCase;
import org.tooling.core.base.SearchingTestId;
import org.tooling.util.TestUtils;

import java.io.IOException;
import java.util.*;

@Log4j2
public class Main {

    private static final String DEFAULT_BROWSER = "chrome";

    private static final String DEFAULT_RESOLUTION = "1004";

    private static final Map<Integer, ITestContext> TEST_RESULT = new HashMap<>();

    private static final long SLEEP_IN_MILLIS_BETWEEN_FAILURES = 60 * 1000L;

    private static final int MAX_RETRIES = 0;

    static void runTest(String testId) throws IOException {

        List<SeoMonitorQaTestCase> testCases = SearchingTestId.findQaTestCase(Collections.emptySet(),
                new HashSet<>(Collections.singletonList(testId)));
        if (ObjectUtils.isEmpty(testCases)) {
            log.error("Invalid Test Case");
            return;
        }

        System.setProperty("env", TestUtils.getEnvironment());
        System.setProperty("browser.type", "local chrome - 1004");
        System.setProperty("SingleTestCaseRun", "true");

        TestUtils.init();

        SeoMonitorQaTestCase testCase = testCases.get(0);
        TestListenerAdapter tla = new CustomTestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{testCases.get(0).getTestClass()});
        testng.setGroups(testCases.get(0).getTestId());
        testng.addListener(tla);

        ITestContext testContext = null;
        ITestResult result = null;

        boolean success = false;
        for (int attempt = 1; attempt < 2 + MAX_RETRIES; attempt++) {
            testng.run();

            testContext = TEST_RESULT.get(1);
            result = testContext == null ? null
                    : getTestResult(testContext, testCase.getTestMethod().getName());

            if (result != null && (result.getStatus() == ITestResult.SUCCESS
                    || result.getStatus() == ITestResult.SKIP)) {
                success = (result.getStatus() == ITestResult.SUCCESS);
                break;
            }

            sleepBetweenFailures();

        }

        String testStatus = result == null ? "ITestContext NULL" : statusText(result.getStatus());

        log.info("Time Elapsed: {}, Category: {}, ID: {}, Test Status: {}",
                result == null ? 0 : (result.getEndMillis() - result.getStartMillis()),
                testCase.getTestCategory(), testCase.getTestId(), testStatus);

    }

    private static String statusText(int status) {
        switch (status) {
            case ITestResult.CREATED:
                return "CREATED";
            case ITestResult.FAILURE:
                return "FAILURE";
            case ITestResult.SKIP:
                return "SKIP";
            case ITestResult.STARTED:
                return "STARTED";
            case ITestResult.SUCCESS:
                return "SUCCESS";
            case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
                return "SUCCESS_PERCENTAGE_FAILURE";
            default:
                return "Unknown";
        }
    }

    private static ITestResult getTestResult(ITestContext testContext, String methodName) {
        ITestResult testResult = null;
        if (!ObjectUtils.isEmpty(testContext.getFailedTests())
                && !ObjectUtils.isEmpty(testContext.getFailedTests().getAllResults())) {
            testResult = testContext.getFailedTests().getAllResults().stream()
                    .filter(result -> result.getMethod().getMethodName().equals(methodName)).findFirst()
                    .orElse(null);
        }

        if (testResult == null && !ObjectUtils.isEmpty(testContext.getSkippedTests())
                && !ObjectUtils.isEmpty(testContext.getSkippedTests().getAllResults())) {
            testResult = testContext.getSkippedTests().getAllResults().stream()
                    .filter(result -> result.getMethod().getMethodName().equals(methodName)).findFirst()
                    .orElse(null);
        }

        if (testResult == null && !ObjectUtils.isEmpty(testContext.getPassedTests())
                && !ObjectUtils.isEmpty(testContext.getPassedTests().getAllResults())) {
            testResult = testContext.getPassedTests().getAllResults().stream()
                    .filter(result -> result.getMethod().getMethodName().equals(methodName)).findFirst()
                    .orElse(null);
        }

        return testResult;
    }

    private static void sleepBetweenFailures() {
        try {
            Thread.sleep(SLEEP_IN_MILLIS_BETWEEN_FAILURES);
        } catch (InterruptedException e) {
            if (log.isTraceEnabled()) {
                log.trace("Erro sleeping between failures", e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        String testId = System.getenv("CURRENT_TEST_ID");
        if (ObjectUtils.isEmpty(testId)) {
            log.error("Test ID not provided");
            return;
        }

        runTest(testId);
    }

    private static class CustomTestListenerAdapter extends TestListenerAdapter {

        public CustomTestListenerAdapter() {
            super();
        }

        @Override
        public void onFinish(ITestContext testContext) {
            TEST_RESULT.put(1, testContext);
        }

    }
}