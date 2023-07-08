package org.tooling.pages.campaignWizard;


import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.helper.Retry;

import java.util.Objects;


@Log4j2
public class WizardOtherKeywordsNavigationPage extends SeoMonitorBasePage {

    public WizardOtherKeywordsNavigationPage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> getHeader() != null && !Objects.equals(getHeader(), "")
        );
        getElementIfVisibleUsingProperty("seomonitor.wizard.otherKeywordsNavigation.header");
    }

    public String getHeader() {
        return getTextFromDivUsingProperty("seomonitor.wizard.otherKeywordsNavigation.header");
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

    public void clickFinishBtn() {
        waitAndClickUsingProperty("seomonitor.wizard.otherKeywordsNavigation.finishBtn");
        Retry.times(6).withTimeout(1).ignoringErrors().till(
                () -> isFinishBtnLoading()
        );
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isFinishBtnLoading()
        );
    }

    public boolean isFinishBtnLoading() {
        return isElementVisibleUsingProperty("seomonitor.wizard.otherKeywordsNavigation.finishBtn.loading");
    }

}
