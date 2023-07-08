package org.tooling.pages.base;

import com.paulhammant.ngwebdriver.NgWebDriver;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.tooling.selenium.BrowserUtils;
import org.tooling.util.DataUtils;
import org.tooling.util.TestUtils;
import org.tooling.helper.Waiter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Log4j2
public abstract class SeoMonitorBasePage {


    protected WebDriver driver;

    protected NgWebDriver ngDriver;

    protected Wait<WebDriver> wait;

    protected Wait<WebDriver> minimalWait;

    protected Wait<WebDriver> shortWait;

    protected Wait<WebDriver> longWait;

    protected Wait<WebDriver> noWait;

    protected Actions actions;

    protected JavascriptExecutor js;

    /* Protected methods */
    protected SeoMonitorBasePage(WebDriver driver) {
        this.driver = driver;

        wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.default.wait")))
                .pollingEvery(Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.polling")))
                .ignoring(NoSuchElementException.class);

        minimalWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.minimal.wait")))
                .pollingEvery(Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.polling")))
                .ignoring(NoSuchElementException.class);

        shortWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.short.wait")))
                .pollingEvery(Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.polling")))
                .ignoring(NoSuchElementException.class);

        longWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.long.wait")))
                .pollingEvery(Duration.ofSeconds(TestUtils.getPropertyAsInt("selenium.polling")))
                .ignoring(NoSuchElementException.class);

        noWait = new FluentWait<>(driver).withTimeout(Duration.of(200, ChronoUnit.MILLIS));
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;

        ngDriver = new NgWebDriver(js);
    }

    protected SeoMonitorBasePage(BrowserUtils browserUtils) {
        this.driver = browserUtils.getDriver();
        wait = browserUtils.getDefaultWait();
        minimalWait = browserUtils.getMinimalWait();
        shortWait = browserUtils.getShortWait();
        longWait = browserUtils.getLongWait();
        noWait = browserUtils.getNoWait();
        actions = browserUtils.getActions();
        js = browserUtils.getJsExecutor();
        ngDriver = browserUtils.getNgDriver();
    }


    protected void setTextWithoutClear(WebElement el, String text) {
        bringElementIntoView(el);
        if (text != null && !text.isEmpty()) {
            TestUtils.sleep(0.5);
            el.sendKeys(text);
        }
    }

    protected void selectText(WebElement el) {
        selectText(el, false);
    }

    protected void selectText(WebElement el, boolean clickOnCorner) {
        waitAndClick(el, clickOnCorner);
        actions.sendKeys(el, Keys.HOME).keyDown(Keys.SHIFT).sendKeys(Keys.END).keyUp(Keys.SHIFT)
                .perform();
    }

    protected void setTextClearBySelection(WebElement el, String text) {
        selectText(el);
        setTextWithoutClear(el, text);
    }

    protected void setText(WebElement el, String text) {
        try {
            el.clear();
            setTextWithoutClear(el, text);
        } catch (InvalidElementStateException e) {
            setTextClearBySelection(el, text);
        }
    }

    protected String getTextFromDivUsingXpath(String xpath, Wait<WebDriver> localWait) {
        String text = null;
        if (xpath != null && !xpath.isEmpty() && isElementVisibleUsingXpath(xpath, localWait)) {

            WebElement el = getElementIfVisibleUsingXpath(xpath, localWait);

            bringElementIntoView(el);
            text = el.getText();
        }
        return text;
    }

    protected String getTextFromDivUsingProperty(String property) {
        return getTextFromDivUsingProperty(property, shortWait);
    }

    protected String getTextFromDivUsingProperty(String property, Wait<WebDriver> localWait) {
        if (property != null) {
            return getTextFromDivUsingXpath(getXpath(property), localWait);
        }
        return null;
    }

    @SafeVarargs
    protected final void waitAndClickUsingProperty(String property, Wait<WebDriver>... localWait) {
        String xpath = getXpath(property);
        waitAndClickUsingXpath(xpath, localWait);
    }


    @SafeVarargs
    protected final void waitAndClickUsingProperty(String property, Wait<WebDriver> localWait,
                                                   Object... values) {
        String xpath = getXpath(property);
        if (values != null && values.length > 0) {
            xpath = String.format(xpath, values);
        }
        waitAndClickUsingXpath(xpath, localWait);
    }

    @SafeVarargs
    protected final void waitAndClickUsingProperty(String property, boolean scrollType,
                                                   Wait<WebDriver>... localWait) {
        String xpath = getXpath(property);
        waitAndClickUsingXpath(xpath, scrollType, localWait);
    }

    @SafeVarargs
    protected final void waitAndClickUsingXpath(String xpath, boolean scrollType,
                                                Wait<WebDriver>... localWait) {
        Wait<WebDriver> tempWait = TestUtils.getFirstValueOr(localWait, wait);

        WebElement el = getElementIfVisibleUsingXpath(xpath, tempWait);

        bringElementIntoView(el);

        el = getElementIfVisibleUsingXpath(xpath, tempWait);

        el = tempWait.until(ExpectedConditions.elementToBeClickable(el));

        tryClickingOn(el, scrollType);
    }

    private void tryClickingOn(WebElement el, boolean scrollType) {
        tryClickingOn(el, scrollType, false);
    }

    private void tryClickingOn(WebElement el, boolean scrollType, boolean clickOnCorner) {
        try {
            bringElementIntoView(el, scrollType);
            if (clickOnCorner) {
                Point point = getTranslatedOffsetFromTopLeftOfElement(el, 1, 1);
                new Actions(driver).moveToElement(el, point.getX(), point.getY()).click().perform();
            } else {
                el.click();
            }
        } catch (WebDriverException e) {
            String errorMessage = e.getMessage() == null ? "" : e.getMessage();
            if (errorMessage.contains("Other element would receive the click:") // the message that
                    // chromedriver returns
                    // for this error
                    || (errorMessage.contains("another element") && errorMessage.contains("obscures it"))) { // the
                // message
                // that
                // geckodriver
                // returns
                // for
                // this
                // error

                log.debug("error trying the default click", e);
                bringElementIntoView(el, !scrollType);

                if (clickOnCorner) {
                    Point point = getTranslatedOffsetFromTopLeftOfElement(el, 1, 1);
                    new Actions(driver).moveToElement(el, point.getX(), point.getY()).click().perform();
                } else {
                    el.click();
                }
            } else {
                throw e;
            }
        }
    }

    @SafeVarargs
    protected final void waitAndClickUsingXpath(String xpath, Wait<WebDriver>... localWait) {
        waitAndClickUsingXpath(xpath, true, localWait);
    }

    @SafeVarargs
    protected final void waitAndClick(WebElement el, boolean clickOnCorner,
                                      Wait<WebDriver>... localWait) {
        Wait<WebDriver> tempWait = TestUtils.getFirstValueOr(localWait, wait);
        tempWait.until(ExpectedConditions.visibilityOf(el));
        tempWait.until(ExpectedConditions.elementToBeClickable(el));
        tryClickingOn(el, true, clickOnCorner);
    }


    protected String getXpath(String key, Object... modifiers) {
        String value = TestUtils.lookupAllFilesAndGetProperty(key);

        if (value != null && !value.isEmpty() && modifiers.length > 0) {
            value = String.format(value, modifiers);
        }

        return value;
    }


    @SafeVarargs
    protected final WebElement getElementIfVisibleUsingProperty(String keyToXpath,
                                                                Wait<WebDriver>... localWait) {
        String xpath = getXpath(keyToXpath);
        return getElementIfVisibleUsingXpath(xpath, localWait);
    }


    @SafeVarargs
    protected final WebElement getElementIfVisibleUsingXpath(String xpath,
                                                             Wait<WebDriver>... localWait) {
        Wait<WebDriver> tempWait = TestUtils.getFirstValueOr(localWait, longWait);

        return tempWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    @SafeVarargs
    protected final WebElement getElementIfVisibleUsingProperty(WebElement el, String keyToXpath,
                                                                Wait<WebDriver>... localWait) {
        String xpath = getXpath(keyToXpath);
        return getElementIfVisibleUsingXpath(el, xpath, localWait);
    }

    @SafeVarargs
    protected final WebElement getElementIfVisibleUsingXpath(WebElement el, String xpath,
                                                             Wait<WebDriver>... localWait) {
        Wait<WebDriver> tempWait = TestUtils.getFirstValueOr(localWait, wait);
        return tempWait
                .until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(el, By.xpath(xpath))).get(0);
    }


    @SafeVarargs
    protected final boolean isElementPresentUsingXpath(String xpath, Wait<WebDriver>... localWait) {
        Wait<WebDriver> tempWait = TestUtils.getFirstValueOr(localWait, wait);
        boolean result = false;
        try {
            List<WebElement> webElementList = tempWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));
            if (webElementList != null && webElementList.size() > 0) {
                result = true;
            }
        } catch (NoSuchElementException | TimeoutException e) {
            result = false;
        }

        return result;
    }


    @SafeVarargs
    protected final boolean isElementVisibleUsingXpath(String xpath, Wait<WebDriver>... localWait) {
        Wait<WebDriver> tempWait = TestUtils.getFirstValueOr(localWait, wait);
        boolean result = false;
        long startTime = System.currentTimeMillis();
        long waitTime;
        try {
            result = !tempWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)))
                    .isEmpty();
            waitTime = System.currentTimeMillis() - startTime;
        } catch (NoSuchElementException | TimeoutException e) {
            waitTime = System.currentTimeMillis() - startTime;
            result = false;
        }

        return result;
    }


    @SafeVarargs
    protected final boolean isElementVisibleUsingProperty(String keyToXpath,
                                                          Wait<WebDriver>... localWait) {
        String xpath = getXpath(keyToXpath);
        return isElementVisibleUsingXpath(xpath, localWait);
    }


    protected Point getTranslatedOffsetFromTopLeftOfElement(WebElement el, int x, int y) {
        Rectangle rect = el.getRect();
        // log.debug("rect - "+rect.getX()+","+rect.getY()+","+rect.getWidth()+","+rect.getHeight());

        // calculate the midpoint of the element
        int midX = rect.getWidth() / 2;
        int midY = rect.getHeight() / 2;
        // log.debug("mid - "+midX +","+midY);

        // assuming top left as the origin, translate as an offset from there
        int tempX = (-midX) + x;
        int tempY = (-midY) + y;
        // log.debug("offset - "+tempX +","+tempY);

        return new Point(tempX, tempY);
    }


    protected void bringElementIntoView(WebElement el) {
        bringElementIntoView(el, true);
    }


    protected void bringElementIntoView(WebElement el, boolean alignToTop) {
        if (alignToTop) {
            js.executeScript(DataUtils.getJSSnippetFor("scroll element to top"), el);
        } else {
            js.executeScript(DataUtils.getJSSnippetFor("scroll element to bottom"), el);
        }
    }


    public boolean isCurrentPage(boolean throwException) {
        String name = this.getClass().getSimpleName();
        log.info("trying to verify the page [" + name + "]");

        try {
            TestUtils.sleep(2);
            waitTillLoaded();
            initElements();
            waitTillLoaded();

            return true;
        } catch (RuntimeException e) {
            log.info("The page [" + name + "] could not be verified.", e);
            if (throwException) {
                throw e;
            }
            return false;
        }
    }

    public void waitTillLoaded() {
        Waiter.forSeconds(TestUtils.LONG_WAIT).pollEvery(1000).ignoringFailure()
                .waitFor(this::isLoadingComplete).perform();
    }

    public boolean isLoadingComplete() {
        return (boolean) js.executeScript(DataUtils.getJSSnippetFor("is page ready"));
    }


    protected void reloadPage() {
        driver.navigate().refresh();
    }

    /**
     * Initializes the necessary elements in a page.
     */
    public abstract void initElements();

    public Wait<WebDriver> getMinimalWait() {
        return minimalWait;
    }

    public Wait<WebDriver> getShortWait() {
        return shortWait;
    }

    public Wait<WebDriver> getLongWait() {
        return longWait;
    }

    public Wait<WebDriver> getNoWait() {
        return noWait;
    }

}
