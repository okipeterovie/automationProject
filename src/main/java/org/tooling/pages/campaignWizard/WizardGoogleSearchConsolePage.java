package org.tooling.pages.campaignWizard;


import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.helper.Retry;

import java.util.Objects;


@Log4j2
public class WizardGoogleSearchConsolePage extends SeoMonitorBasePage {

    public WizardGoogleSearchConsolePage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> getHeader() != null && !Objects.equals(getHeader(), "")
        );
        getElementIfVisibleUsingProperty("seomonitor.wizard.googleSearchConsole.header");
    }

    public String getHeader() {
        return getTextFromDivUsingProperty("seomonitor.wizard.googleSearchConsole.header");
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

    public void clickSkipBtn() {
        waitAndClickUsingProperty("seomonitor.wizard.googleSearchConsole.skipBtn");
        Retry.times(6).withTimeout(1).ignoringErrors().till(
                () -> isSkipBtnLoading()
        );
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isSkipBtnLoading()
        );
    }

    public boolean isSkipBtnLoading() {
        return isElementVisibleUsingProperty("seomonitor.wizard.googleSearchConsole.skipBtn.loading");
    }

}
