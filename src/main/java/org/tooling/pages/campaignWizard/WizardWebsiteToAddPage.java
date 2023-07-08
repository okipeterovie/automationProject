package org.tooling.pages.campaignWizard;


import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.helper.Retry;

import java.util.Objects;


@Log4j2
public class WizardWebsiteToAddPage extends SeoMonitorBasePage {

    public WizardWebsiteToAddPage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> getHeader() != null && !Objects.equals(getHeader(), "")
        );
        getElementIfVisibleUsingProperty("seomonitor.wizard.websiteToAdd.header");
    }

    public String getHeader() {
        return getTextFromDivUsingProperty("seomonitor.wizard.websiteToAdd.header");
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

    public boolean isAlreadyHaveACampaignConfigurationPopupDisplayed() {
        return isElementVisibleUsingProperty("seomonitor.wizard.websiteToAdd.alreadyHaveACampaignConfiguration.popup.youAlreadyHaveACampaign");
    }

    public void clickGoToDashboardOnAlreadyHaveACampaignConfigurationPopup() {
        waitAndClickUsingProperty("seomonitor.wizard.websiteToAdd.alreadyHaveACampaignConfiguration.popup.goToDashboard");
    }

    public void clickResumeOnAlreadyHaveACampaignConfigurationPopup() {
        waitAndClickUsingProperty("seomonitor.wizard.websiteToAdd.alreadyHaveACampaignConfiguration.popup.resume");
    }

    public void clickStartANewOneOnAlreadyHaveACampaignConfigurationPopup() {
        waitAndClickUsingProperty("seomonitor.wizard.websiteToAdd.alreadyHaveACampaignConfiguration.popup.startANewOne");
    }

    public void setClientInput(String client) {
        setText(
                getElementIfVisibleUsingProperty("seomonitor.wizard.websiteToAdd.clientWebsite.input"),
                client
        );

        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isClientInputLoading()
        );
    }

    public boolean isClientInputLoading() {
        return isElementVisibleUsingProperty("seomonitor.wizard.websiteToAdd.clientWebsite.input.loading");
    }

    public boolean isNextBtnDisabled() {
        WebElement webElement = getElementIfVisibleUsingProperty("seomonitor.wizard.websiteToAdd.nextBtn.active");
        String className = webElement.getAttribute("class");
        log.info("className: [{}]", className);
        return className.toLowerCase().contains("disabled");
    }

    public void clickNextBtn() {
        waitAndClickUsingProperty("seomonitor.wizard.websiteToAdd.nextBtn");
        Retry.times(6).withTimeout(1).ignoringErrors().till(
                () -> isNextBtnLoading()
        );
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isNextBtnLoading()
        );
    }

    public boolean isNextBtnLoading() {
        return isElementVisibleUsingProperty("seomonitor.wizard.websiteToAdd.nextBtn.loading");
    }

}
