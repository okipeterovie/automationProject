package org.tooling.core.base;


import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.DashboardPage;
import org.tooling.pages.LoginPage;
import org.tooling.pages.campaignWizard.*;

@Log4j2
public class SeoMonitorPageCollection {

    @Getter
    private LoginPage loginPage;

    @Getter
    private DashboardPage dashboardPage;

    @Getter
    private WizardWebsiteToAddPage wizardWebsiteToAddPage;

    @Getter
    private WizardTargetLocationPage wizardTargetLocationPage;

    @Getter
    private WizardAddKeywordPage wizardAddKeywordPage;

    @Getter
    private WizardOrganizeKeywordPage wizardOrganizeKeywordPage;

    @Getter
    private WizardCompetitorPage wizardCompetitorPage;

    @Getter
    private WizardGoogleSearchConsolePage wizardGoogleSearchConsolePage;

    @Getter
    private WizardOtherKeywordsNavigationPage wizardOtherKeywordsNavigationPage;

    @Getter
    private WizardCampaignProcessingPage wizardCampaignProcessingPage;

    public SeoMonitorPageCollection(BrowserUtils browserUtils) {
        initPagesCommonForAllDisplays(browserUtils);
    }

    private void initPagesCommonForAllDisplays(BrowserUtils browserUtils) {
        loginPage = new LoginPage(browserUtils);
        dashboardPage = new DashboardPage(browserUtils);
        wizardWebsiteToAddPage = new WizardWebsiteToAddPage(browserUtils);
        wizardTargetLocationPage = new WizardTargetLocationPage(browserUtils);
        wizardAddKeywordPage = new WizardAddKeywordPage(browserUtils);
        wizardOrganizeKeywordPage = new WizardOrganizeKeywordPage(browserUtils);
        wizardCompetitorPage = new WizardCompetitorPage(browserUtils);
        wizardGoogleSearchConsolePage = new WizardGoogleSearchConsolePage(browserUtils);
        wizardOtherKeywordsNavigationPage= new WizardOtherKeywordsNavigationPage(browserUtils);
        wizardCampaignProcessingPage = new WizardCampaignProcessingPage(browserUtils);
    }

}
