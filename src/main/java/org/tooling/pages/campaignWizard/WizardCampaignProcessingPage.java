package org.tooling.pages.campaignWizard;


import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.helper.Retry;

import java.util.Objects;


@Log4j2
public class WizardCampaignProcessingPage extends SeoMonitorBasePage {

    public WizardCampaignProcessingPage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> getHeader() != null && !Objects.equals(getHeader(), "")
        );
        getElementIfVisibleUsingProperty("seomonitor.wizard.campaignProcessing.header");
    }

    public String getHeader() {
        return getTextFromDivUsingProperty("seomonitor.wizard.campaignProcessing.header");
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

    public void clickGoToDashboardPageBtn() {
        waitAndClickUsingProperty("seomonitor.wizard.campaignProcessing.goToDashboardPageBtn");
        Retry.times(6).withTimeout(1).ignoringErrors().till(
                () -> isGoToDashboardPageBtnLoading()
        );
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isGoToDashboardPageBtnLoading()
        );
    }

    public boolean isGoToDashboardPageBtnLoading() {
        return isElementVisibleUsingProperty("seomonitor.wizard.campaignProcessing.goToDashboardPageBtn.loading");
    }

}
