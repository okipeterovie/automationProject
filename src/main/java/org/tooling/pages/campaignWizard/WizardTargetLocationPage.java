package org.tooling.pages.campaignWizard;


import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.helper.Retry;

import java.util.Objects;


@Log4j2
public class WizardTargetLocationPage extends SeoMonitorBasePage {

    public WizardTargetLocationPage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> getHeader() != null && !Objects.equals(getHeader(), "")
        );
        getElementIfVisibleUsingProperty("seomonitor.wizard.targetLocation.header");
    }

    public String getHeader() {
        return getTextFromDivUsingProperty("seomonitor.wizard.targetLocation.header");
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

    public void setLocationTab(WizardTargetLocation wizardTargetLocation) {
        final String xpath = String.format(
                getXpath("seomonitor.wizard.targetLocation.locationTabs.byName"),
                wizardTargetLocation.getName()
        );

        waitAndClickUsingXpath(xpath);
    }

    public void clickNextBtn() {
        waitAndClickUsingProperty("seomonitor.wizard.targetLocation.nextBtn");
        Retry.times(6).withTimeout(1).ignoringErrors().till(
                () -> isNextBtnLoading()
        );
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isNextBtnLoading()
        );
    }

    public boolean isNextBtnLoading() {
        return isElementVisibleUsingProperty("seomonitor.wizard.targetLocation.nextBtn.loading");
    }


    public enum WizardTargetLocation {

        GLOBAL("Global"),
        COUNTRY("Country"),
        REGION("Region"),
        CITY("City");

        private final String name;

        WizardTargetLocation(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


}
