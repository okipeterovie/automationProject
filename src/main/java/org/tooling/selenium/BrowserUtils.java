package org.tooling.selenium;


import com.paulhammant.ngwebdriver.NgWebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.tooling.util.TestUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class BrowserUtils {

    protected Wait<WebDriver> minimalWait;
    private WebDriver driver;
    private NgWebDriver ngDriver;

    private Wait<WebDriver> defaultWait;

    private Wait<WebDriver> shortWait;

    private Wait<WebDriver> longWait;

    private Wait<WebDriver> noWait;

    private Actions actions;

    private JavascriptExecutor jsExecutor;

    public BrowserUtils(WebDriver driver) {
        this.driver = driver;
        defaultWait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.default.wait")));
        shortWait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.short.wait")));
        longWait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.long.wait")));
        minimalWait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.minimal.wait")));
        noWait = new FluentWait<WebDriver>(driver).withTimeout(Duration.of(200, ChronoUnit.MILLIS));
        actions = new Actions(driver);
        jsExecutor = (JavascriptExecutor) driver;
        ngDriver = new NgWebDriver(jsExecutor);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public NgWebDriver getNgDriver() {
        return ngDriver;
    }

    public void setNgDriver(NgWebDriver ngDriver) {
        this.ngDriver = ngDriver;
    }

    public Wait<WebDriver> getShortWait() {
        return shortWait;
    }

    public void setShortWait(Wait<WebDriver> shortWait) {
        this.shortWait = shortWait;
    }

    public Wait<WebDriver> getLongWait() {
        return longWait;
    }

    public void setLongWait(Wait<WebDriver> longWait) {
        this.longWait = longWait;
    }

    public Wait<WebDriver> getNoWait() {
        return noWait;
    }

    public void setNoWait(Wait<WebDriver> noWait) {
        this.noWait = noWait;
    }

    public Wait<WebDriver> getMinimalWait() {
        return minimalWait;
    }

    public void setMinimalWait(Wait<WebDriver> minimalWait) {
        this.minimalWait = minimalWait;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public Wait<WebDriver> getDefaultWait() {
        return defaultWait;
    }

    public void setDefaultWait(Wait<WebDriver> defaultWait) {
        this.defaultWait = defaultWait;
    }

    public JavascriptExecutor getJsExecutor() {
        return jsExecutor;
    }

    public void setJsExecutor(JavascriptExecutor jsExecutor) {
        this.jsExecutor = jsExecutor;
    }

}
