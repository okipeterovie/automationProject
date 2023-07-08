package org.tooling.core.actions;


import lombok.extern.log4j.Log4j2;
import org.tooling.core.base.SeoMonitorPageCollection;
import org.tooling.pages.base.SeoMonitorBasePage;
import org.tooling.selenium.BrowserUtils;
import org.tooling.core.user.SeoMonitorTestUser;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Log4j2
public class SeoMonitorCommonActions extends SeoMonitorBaseActions {

    public SeoMonitorCommonActions(ThreadLocal<BrowserUtils> localDriver,
                                   ThreadLocal<SeoMonitorPageCollection> seoMonitorPageCollection) {
        super(localDriver, seoMonitorPageCollection);
    }


    @Override
    public void verifyPage(SeoMonitorBasePage page, boolean reportSuccess, String message) {
        super.verifyPage(page, reportSuccess, message);
    }


    @Override
    public void verifyPage(SeoMonitorBasePage page, boolean reportSuccess) {
        super.verifyPage(page, reportSuccess);
    }


    @Override
    public void verifyPage(SeoMonitorBasePage page) {
        super.verifyPage(page);
    }

    @Override
    public void login(SeoMonitorTestUser seoMonitorTestUser) {
        super.login(seoMonitorTestUser);
    }

    @Override
    public void navigateTo(String urlOrPropertyKey, Boolean... toLookup) {
        super.navigateTo(urlOrPropertyKey, toLookup);
    }

    public void navigateTo(String urlKey, Boolean toLookup, SeoMonitorBasePage expectedPage) {
        super.navigateTo(urlKey, toLookup, expectedPage);
    }

    @Override
    public void reloadPage() {
        super.reloadPage();
    }

    @Override
    public void reloadPage(SeoMonitorBasePage expectedPage) {
        super.reloadPage(expectedPage);
    }

    @Override
    public void navigateBack() {
        super.navigateBack();
    }

    @Override
    public void navigateForward() {
        super.navigateForward();
    }

    @Override
    public void openNewTab() {
        super.openNewTab();
    }

    @Override
    public int getNumberOfOpenTabs() {
        return super.getNumberOfOpenTabs();
    }

    @Override
    public void switchToNewTab() {
        super.switchToNewTab();
    }

    @Override
    public void switchTabs(int tabNumber) {
        super.switchTabs(tabNumber);
    }

    @Override
    public void waitAndSwitchTabs(int i) {
        super.waitAndSwitchTabs(i);
    }

    @Override
    public void closeCurrentTabAndGoToTab(int tabNumber) {
        super.closeCurrentTabAndGoToTab(tabNumber);
    }

    @Override
    public String getCurrentWindowHandle() {
        return super.getCurrentWindowHandle();
    }

    @Override
    public List<String> getAllWindowHandles() {
        return super.getAllWindowHandles();
    }

    @Override
    public String getCurrentUrl() {
        return super.getCurrentUrl();
    }

    @Override
    public void runJSSnippet(String jsCode) {
        super.runJSSnippet(jsCode);
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    @Override
    public void verifySuccessOf(Runnable block, String description) {
        super.verifySuccessOf(block, description);
    }

    public void verifyFailureOf(Runnable block, String description) {
        super.verifySuccessOf(false, block, description);
    }

    @Override
    public void negateAndVerify(String actualValue, String expectedValue, String description) {
        super.negateAndVerify(actualValue, expectedValue, description);
    }

    @Override
    public void negateAndVerify(Object actualValue, Object expectedValue, String description) {
        super.negateAndVerify(actualValue, expectedValue, description);
    }

    @Override
    public void negateAndVerify(Boolean actualValue, Boolean expectedValue, String description) {
        super.negateAndVerify(actualValue, expectedValue, description);
    }

    @Override
    public void negateAndVerify(Integer actualValue, Integer expectedValue, String description) {
        super.negateAndVerify(actualValue, expectedValue, description);
    }

    @Override
    public void negateAndVerify(Double actualValue, Double expectedValue, String description) {
        super.negateAndVerify(actualValue, expectedValue, description);
    }

    @Override
    public void verify(String actualValue, String expectedValue, String description) {
        super.verify(actualValue, expectedValue, description);
    }

    @Override
    public void verify(Object actualValue, Object expectedValue, String description) {
        super.verify(actualValue, expectedValue, description);
    }


    @Override
    public void verify(Boolean actualValue, Boolean expectedValue, String description) {
        super.verify(actualValue, expectedValue, description);
    }

    @Override
    public void verify(Integer actualValue, Integer expectedValue, String description) {
        super.verify(actualValue, expectedValue, description);
    }


    @Override
    public void verify(Double actualValue, Double expectedValue, String description) {
        super.verify(actualValue, expectedValue, description);
    }

    @Override
    public void waitForUrlChange(String url, int seconds, Runnable failureHandler) {
        super.waitForUrlChange(url, seconds, failureHandler);
    }

    @Override
    public void waitForUrlChange(String url, int seconds) {
        super.waitForUrlChange(url, seconds);
    }

    @Override
    public boolean waitForUrlChange(int seconds) {
        return super.waitForUrlChange(seconds);
    }

    @Override
    public <T> void waitForValueChange(Supplier<T> supplier, T currentValue, int seconds) {
        super.waitForValueChange(supplier, currentValue, seconds);
    }

    @Override
    public <T> void waitFor(Supplier<T> supplier, Predicate<T> predicate, int seconds) {
        super.waitFor(supplier, predicate, seconds);
    }


    @Override
    public void printConsoleErrors() {
        super.printConsoleErrors();
    }

    @Override
    public boolean isAlertPresent() {
        return super.isAlertPresent();
    }

    @Override
    public void acceptAlert() {
        super.acceptAlert();
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
    }

    @Override
    public void scrollHorizontallyBy(int x) {
        super.scrollHorizontallyBy(x);
    }

    @Override
    public void scrollVerticallyBy(int y) {
        super.scrollVerticallyBy(y);
    }

}
