package org.tooling.core.base;

import lombok.extern.log4j.Log4j2;

/**
 * The Class LocalBaseTest.
 */
@Log4j2
public class LocalSeoMonitorBaseTest extends SeoMonitorBaseTest {

    static {
        if (log.isTraceEnabled()) {

        }
    }

    public LocalSeoMonitorBaseTest() {
    }

    public LocalSeoMonitorBaseTest(String browser) {
        super(browser);
    }


    public LocalSeoMonitorBaseTest(String browser, String environment) {
        super(browser, environment);
    }

    @Override
    public void localBeforeClass() {
    }

    @Override
    public void localBeforeMethod() {
    }

    @Override
    public void localAfterClass() {
    }

    @Override
    public void localAfterMethod() {
    }

    @Override
    public ReuseType shouldReuseBrowserBetweenTests() {
        return ReuseType.DO_NOT_RESUSE;
    }

    @Override
    public RunType getLocalBeforeClassRunType() {
        return RunType.ONLY_ONCE;
    }

}
