package org.tooling.selenium;


import com.google.gson.JsonObject;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.tooling.util.TestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Log4j2
public class SeleniumUtils {

    private static final ConcurrentHashMap<Thread, WebDriver> drivers = new ConcurrentHashMap<>();

    private static final ThreadLocal<String> browserType = new ThreadLocal<>();

    private static final ThreadLocal<String> display = ThreadLocal.withInitial(() -> "1004");

    private static final ThreadLocal<Boolean> isMobile = ThreadLocal.withInitial(() -> false);

    private static final JsonObject jsonData;

    static {
        jsonData = TestUtils.getJsonObjectFromResource("/capabilities.json");
    }

    private SeleniumUtils() {
    }

    private static WebDriver createChromeDriver(boolean useCustomProxy, String customUA, Dimension dimension) {
        return createChromeDriver(useCustomProxy, customUA, dimension, false, false);
    }

    private static WebDriver createChromeDriver(boolean useCustomProxy, String customUA, Dimension dimension,
                                                boolean useActualUa, boolean shouldBeHeadless) {
        ChromeOptions options = new ChromeOptions();

        if (dimension == null) {
            dimension = getDimensionFor("1004");
        }

        options.addArguments("--remote-allow-origins=*");
        options.addArguments("window-size=" + dimension.getWidth() + "," + dimension.getHeight());


        if (shouldBeHeadless) {
            options.addArguments("headless");
        }

        if (!useActualUa) {
            setCommonPropsForChrome(options, null);
        } else {
            setCommonPropsForChrome(options, customUA);
        }

        return new ChromeDriver(options);
    }

    public static Dimension getDimensionFor(String displayType) {
        Dimension dimension;

        displayType = (displayType == null) ? "" : displayType;

        switch (displayType) {
            case "768":
                dimension = new Dimension(768, 1024);
                break;
            case "414":
                dimension = new Dimension(414, 736);
                break;
            case "320":
                dimension = new Dimension(320, 568);
                break;
            case "1900":
                dimension = new Dimension(1910, 900);
                break;
            case "1004":
            default:
                dimension = new Dimension(1366, 900);
                break;
        }

        return dimension;
    }

    private static void quitDriver(WebDriver driver) {
        if (driver != null) {
            log.info("quitting webdriver for current thread - " + Thread.currentThread().getId());
            try {
                driver.quit();
                display.remove();
                isMobile.remove();
                browserType.remove();
            } catch (Exception e) {
                log.warn("Error while trying to quit webdriver", e);
            }
        }
    }

    public static WebDriver getDriver(String testName, String browserTypeString) {
        return getDriver(testName, browserTypeString, null);
    }

    public static synchronized WebDriver getDriver(String testName, String browserTypeString, String ua) {

        WebDriver driver = drivers.get(Thread.currentThread());

        if (driver == null) {
            String type = (browserTypeString == null) ? "" : browserTypeString;
            browserType.set(type);

            log.info("creating webdriver of type " + type + " for current thread - " + Thread.currentThread().getId());


            switch (type.toLowerCase()) {
                case "chrome":
                    driver = createChromeDriver(false, null, null);
                    break;
                case "chrome with ua":
                    driver = createChromeDriver(false, TestUtils.getProperty("selenium.ua"), null, true, false);
                    break;
                default:
                    driver = null;
                    break;
            }

            if (driver != null) {
                drivers.put(Thread.currentThread(), driver);
            }
        }

        return driver;
    }

    private static void setCommonPropsForChrome(ChromeOptions options, String customUA) {
        log.info("Getting inside set common props for Chrome.");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.default_content_settings.geolocation", 2);
        prefs.put("download.default_directory", System.getProperty("java.io.tmpdir"));

        options.setExperimentalOption("prefs", prefs);

        options.setExperimentalOption("excludeSwitches",
                Collections.singletonList("enable-automation"));

        options.addArguments("enable-simple-cache-backend=true");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        System.out.println("customUA: " + customUA);
        if (customUA != null && !customUA.isEmpty()) {
            options.addArguments("user-agent=" + customUA);
        } else {
            String ua = TestUtils.getProperty("selenium.defaultUA");
            if (ua != null && !ua.trim().isEmpty()) {
                options.addArguments("user-agent=" + ua);
            }
        }


        Path cachePath = Paths.get(System.getProperty("java.io.tmpdir"), "selenium", ".cache");
        log.info("Cache path: " + cachePath);
        cachePath.toFile().mkdirs();
        WebDriverManager.chromedriver().cachePath(cachePath.toString()).setup();
        log.info("Setup WebDriverManager at {}", System.getProperty("webdriver.chrome.driver"));
    }

    public static String getDisplaySize() {
        return display.get();
    }

    public static String getDisplayForBrowser(String browserType) {
        String displayString = null;

        if (browserType != null && !browserType.isEmpty() && jsonData.get(browserType) != null) {

            JsonObject browserDetails = jsonData.get(browserType).getAsJsonObject();
            if (browserDetails.has("config1")) {
                browserDetails = browserDetails.getAsJsonObject("config1");
            }

            if (browserDetails.has("display")) {
                displayString = browserDetails.get("display").getAsString();
            }
        }

        return displayString;
    }

    public static synchronized void quitDriver() {
        WebDriver driver = drivers.get(Thread.currentThread());
        quitDriver(driver);
        drivers.remove(Thread.currentThread());
    }

    public static String getBrowserType() {
        return browserType.get();
    }

    public static void quitAllDrivers() {
        for (WebDriver driver : drivers.values()) {
            quitDriver(driver);
        }
        drivers.clear();
    }

}
