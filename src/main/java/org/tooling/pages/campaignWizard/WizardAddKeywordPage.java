package org.tooling.pages.campaignWizard;


import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.helper.Retry;

import java.util.Objects;


@Log4j2
public class WizardAddKeywordPage extends SeoMonitorBasePage {

    public WizardAddKeywordPage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> getHeader() != null && !Objects.equals(getHeader(), "")
        );
        getElementIfVisibleUsingProperty("seomonitor.wizard.addKeyword.header");
    }

    public String getHeader() {
        return getTextFromDivUsingProperty("seomonitor.wizard.addKeyword.header");
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

    public void selectWaysToAddKeyword(WizardKeyWordOption wizardKeyWordOption) {
        final String xpath = String.format(
                getXpath("seomonitor.wizard.addKeyword.keyWordOptions.byValue"),
                wizardKeyWordOption.getValue()
        );

        waitAndClickUsingXpath(xpath);
    }

    public void clickNextBtn() {
        waitAndClickUsingProperty("seomonitor.wizard.addKeyword.nextBtn");
        Retry.times(6).withTimeout(1).ignoringErrors().till(
                () -> isNextBtnLoading()
        );
        Retry.times(6).withTimeout(10).ignoringErrors().till(
                () -> !isNextBtnLoading()
        );
    }

    public boolean isNextBtnLoading() {
        return isElementVisibleUsingProperty("seomonitor.wizard.addKeyword.nextBtn.loading");
    }


    public enum WizardKeyWordOption {

        SEO_MONITOR("Let SEOmonitor identify the best keyword opportunities"),
        CUSTOM("I already have a list of keywords"),
        ;

        private final String value;

        WizardKeyWordOption(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


}
