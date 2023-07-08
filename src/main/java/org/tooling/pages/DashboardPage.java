package org.tooling.pages;


import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.helper.Retry;


@Log4j2
public class DashboardPage extends SeoMonitorBasePage {

    public DashboardPage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        getElementIfVisibleUsingProperty("seomonitor.dashboard.trackedCampaign.span");
        getElementIfVisibleUsingProperty("seomonitor.dashboard.draftCampaign.span");
    }

    public void hasLoaded() {
        waitTillLoaded();
        Retry.times(6).withTimeout(1).ignoringErrors().till(
                () -> isElementVisibleUsingProperty("seomonitor.loadingLogo")
        );
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isElementVisibleUsingProperty("seomonitor.loadingLogo")
        );
        waitTillLoaded();
    }

    public void clickAddNewCampaignBtn() {
        waitAndClickUsingProperty("seomonitor.dashboard.addNewCampaign");
    }

}
