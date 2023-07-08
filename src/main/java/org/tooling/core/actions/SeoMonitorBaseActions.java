package org.tooling.core.actions;


import lombok.extern.log4j.Log4j2;
import org.tooling.core.base.SeoMonitorPageCollection;
import org.tooling.pages.DashboardPage;
import org.tooling.pages.LoginPage;
import org.tooling.core.user.SeoMonitorTestUsers;
import org.tooling.util.TestUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.selenium.BrowserUtils;
import org.tooling.core.user.SeoMonitorTestUser;

/**
 * The Class BaseActions.
 */
@Log4j2
public abstract class SeoMonitorBaseActions extends BaseActions {


    private final ThreadLocal<SeoMonitorPageCollection> seoMonitorPages;

    protected SeoMonitorBaseActions(ThreadLocal<BrowserUtils> browserUtils,
                                    ThreadLocal<SeoMonitorPageCollection> seoMonitorPageCollection) {
        super(browserUtils);
        this.seoMonitorPages = seoMonitorPageCollection;
    }

    protected SeoMonitorPageCollection seoMonitorPages() {
        return seoMonitorPages.get();
    }

    protected void login(SeoMonitorTestUser seoMonitorTestUser) {
        LoginPage loginPage = seoMonitorPages().getLoginPage();
        DashboardPage dashboardPage = seoMonitorPages().getDashboardPage();

        navigateTo("url.base.login", true);
        loginPage.waitTillLoaded();
        verifyPage(loginPage);

        loginPage.setEmailInput(seoMonitorTestUser.getEmail());
        loginPage.setPasswordInput(seoMonitorTestUser.getPassword());
        loginPage.clickLoginButton();

        dashboardPage.hasLoaded();
        verifyPage(dashboardPage);

        SeoMonitorTestUsers.setCurrentUser(seoMonitorTestUser);
    }


    protected void navigateTo(String urlOrPropertyKey, Boolean... toLookup) {

        String actualUrl = urlOrPropertyKey;

        if (TestUtils.getFirstValueOr(toLookup, false)) {
            actualUrl = TestUtils.getProperty(urlOrPropertyKey);
        }

        log.info("navigating to '" + actualUrl + "'");

        localBrowserUtils.get().getDriver().navigate().to(actualUrl);

//        if (isAlertPresent()) {
//            localBrowserUtils.get().getDriver().switchTo().alert().dismiss();
//        }

    }

    protected void navigateTo(String urlKey, Boolean toLookup, SeoMonitorBasePage expectedPage) {
        navigateTo(urlKey, toLookup);
        verifyPage(expectedPage);
    }


}
