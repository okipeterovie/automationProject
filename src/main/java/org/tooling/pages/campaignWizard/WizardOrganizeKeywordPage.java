package org.tooling.pages.campaignWizard;


import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.helper.Retry;

import java.util.Objects;


@Log4j2
public class WizardOrganizeKeywordPage extends SeoMonitorBasePage {

    public WizardOrganizeKeywordPage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> getHeader() != null && !Objects.equals(getHeader(), "")
        );
        getElementIfVisibleUsingProperty("seomonitor.wizard.organizeKeyword.header");
    }

    public String getHeader() {
        return getTextFromDivUsingProperty("seomonitor.wizard.organizeKeyword.header");
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

    public void clickNextBtn() {
        waitAndClickUsingProperty("seomonitor.wizard.organizeKeyword.nextBtn");
        Retry.times(6).withTimeout(1).ignoringErrors().till(
                () -> isNextBtnLoading()
        );
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isNextBtnLoading()
        );
    }

    public boolean isNextBtnLoading() {
        return isElementVisibleUsingProperty("seomonitor.wizard.organizeKeyword.nextBtn.loading");
    }

}
