package org.tooling.tests;


import lombok.extern.log4j.Log4j2;
import org.testng.annotations.CustomAttribute;
import org.testng.annotations.Test;
import org.tooling.core.base.LocalSeoMonitorBaseTest;
import org.tooling.core.user.SeoMonitorTestUser;
import org.tooling.pages.DashboardPage;
import org.tooling.pages.campaignWizard.*;
import org.tooling.helper.Retry;
import org.tooling.core.user.SeoMonitorTestUsers;
import org.tooling.util.TestUtils;

@Log4j2
public class SeoMonitorCampaignTests extends LocalSeoMonitorBaseTest {

    public SeoMonitorCampaignTests() {
        super("chrome with ua");
    }

    public DashboardPage getDashboardPage() {
        return seoMonitorPages().getDashboardPage();
    }

    public WizardWebsiteToAddPage getWizardWebsiteToAddPage() {
        return seoMonitorPages().getWizardWebsiteToAddPage();
    }

    public WizardTargetLocationPage getWizardTargetLocationPage() {
        return seoMonitorPages().getWizardTargetLocationPage();
    }

    public WizardAddKeywordPage getWizardAddKeywordPage() {
        return seoMonitorPages().getWizardAddKeywordPage();
    }

    public WizardOrganizeKeywordPage getWizardOrganizeKeywordPage() {
        return seoMonitorPages().getWizardOrganizeKeywordPage();
    }

    public WizardCompetitorPage getWizardCompetitorPage() {
        return seoMonitorPages().getWizardCompetitorPage();
    }

    public WizardGoogleSearchConsolePage getWizardGoogleSearchConsolePage() {
        return seoMonitorPages().getWizardGoogleSearchConsolePage();
    }

    public WizardOtherKeywordsNavigationPage getWizardOtherKeywordsNavigationPage() {
        return seoMonitorPages().getWizardOtherKeywordsNavigationPage();
    }

    public WizardCampaignProcessingPage getWizardCampaignProcessingPage() {
        return seoMonitorPages().getWizardCampaignProcessingPage();
    }


    @Test(testName = "1 - test Campaign",
            groups = {"SeoMonitor", "testCampaign", "1"},
            attributes = {@CustomAttribute(name = "Category", values = {"SeoMonitor"}),
                    @CustomAttribute(name = "ID", values = {"1"})})
    public void testCampaign() {
        SeoMonitorTestUser seoMonitorTestUser = SeoMonitorTestUsers.getUser("ogheneovie");
        seoMonitorActions.login(seoMonitorTestUser);
        getDashboardPage().clickAddNewCampaignBtn();

        handleWizardWebsiteToAddPage();
        handleWizardTargetLocationPage();
        handleWizardAddKeywordPage();
        handleWizardOrganizeKeywordPage();
        handleWizardCompetitorPage();
        handleWizardGoogleSearchConsolePage();
        handleWizardOtherKeywordsNavigationPage();
        handleWizardCampaignProcessingPage();

        getDashboardPage().hasLoaded();
        seoMonitorActions.verifyPage(getDashboardPage());
    }

    public void handleWizardWebsiteToAddPage() {
        getWizardWebsiteToAddPage().hasLoaded();
        seoMonitorActions.verifyPage(getWizardWebsiteToAddPage());

        if (getWizardWebsiteToAddPage().isAlreadyHaveACampaignConfigurationPopupDisplayed()) {
            getWizardWebsiteToAddPage().clickStartANewOneOnAlreadyHaveACampaignConfigurationPopup();
            Retry.times(6).withTimeout(10).ignoringErrors().till(
                    () -> !getWizardWebsiteToAddPage().isAlreadyHaveACampaignConfigurationPopupDisplayed()
            );
        }

        seoMonitorActions.verify(
                getWizardWebsiteToAddPage().isNextBtnDisabled(),
                true,
                "Verify that [Next Button] is disabled"
        );

        getWizardWebsiteToAddPage().setClientInput("seomonitor.com");

        seoMonitorActions.verify(
                getWizardWebsiteToAddPage().isNextBtnDisabled(),
                false,
                "Verify that [Next Button] is no longer disabled"
        );

        getWizardWebsiteToAddPage().clickNextBtn();
    }

    public void handleWizardTargetLocationPage() {
        getWizardTargetLocationPage().waitTillLoaded();
        seoMonitorActions.verifyPage(getWizardTargetLocationPage());

        getWizardTargetLocationPage().setLocationTab(WizardTargetLocationPage.WizardTargetLocation.GLOBAL);
        getWizardTargetLocationPage().waitTillLoaded();
        getWizardTargetLocationPage().clickNextBtn();
    }

    public void handleWizardAddKeywordPage() {
        getWizardAddKeywordPage().waitTillLoaded();
        seoMonitorActions.verifyPage(getWizardAddKeywordPage());

        getWizardAddKeywordPage().selectWaysToAddKeyword(WizardAddKeywordPage.WizardKeyWordOption.SEO_MONITOR);
        getWizardAddKeywordPage().waitTillLoaded();
        getWizardAddKeywordPage().clickNextBtn();
    }

    public void handleWizardOrganizeKeywordPage() {
        getWizardOrganizeKeywordPage().waitTillLoaded();
        seoMonitorActions.verifyPage(getWizardOrganizeKeywordPage());

        getWizardOrganizeKeywordPage().clickNextBtn();
    }

    public void handleWizardCompetitorPage() {
        getWizardCompetitorPage().waitTillLoaded();
        seoMonitorActions.verifyPage(getWizardCompetitorPage());

        getWizardCompetitorPage().setCompetitor("www.facebook.com");
        TestUtils.sleep(5);
        getWizardCompetitorPage().setCompetitor("www.google.com");
        TestUtils.sleep(5);
        getWizardCompetitorPage().setCompetitor("www.whatsapp.com");
        TestUtils.sleep(5);

        getWizardCompetitorPage().waitTillLoaded();
        getWizardCompetitorPage().clickNextBtn();
    }

    public void handleWizardGoogleSearchConsolePage() {
        getWizardGoogleSearchConsolePage().waitTillLoaded();
        seoMonitorActions.verifyPage(getWizardGoogleSearchConsolePage());

        getWizardGoogleSearchConsolePage().waitTillLoaded();
        getWizardGoogleSearchConsolePage().clickSkipBtn();
    }

    public void handleWizardOtherKeywordsNavigationPage() {
        getWizardOtherKeywordsNavigationPage().waitTillLoaded();
        seoMonitorActions.verifyPage(getWizardOtherKeywordsNavigationPage());

        getWizardOtherKeywordsNavigationPage().waitTillLoaded();
        getWizardOtherKeywordsNavigationPage().clickFinishBtn();
    }

    public void handleWizardCampaignProcessingPage() {
        getWizardCampaignProcessingPage().waitTillLoaded();
        seoMonitorActions.verifyPage(getWizardCampaignProcessingPage());

        getWizardCampaignProcessingPage().waitTillLoaded();
        getWizardCampaignProcessingPage().clickGoToDashboardPageBtn();
    }

    @Test(testName = "2 - test Campaign",
            groups = {"SeoMonitor", "testCampaign", "2"},
            attributes = {@CustomAttribute(name = "Category", values = {"SeoMonitor"}),
                    @CustomAttribute(name = "ID", values = {"2"})})
    public void test() {
        seoMonitorActions.navigateTo("https://www.google.com");
        TestUtils.sleep(5);
    }

}
