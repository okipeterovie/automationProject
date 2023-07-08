package org.tooling.util;


import com.google.common.util.concurrent.AtomicLongMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.SeleniumUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utility class for all convenience methods and global methods that might be needed from test
 * classes.
 */
@Log4j2
public class TestUtils {

    /**
     * The Constant log.
     */

    private static final AtomicLongMap<String> counts = AtomicLongMap.create();
    /**
     * The Properties objects that contains all the properties defined in the files.
     */
    private static final Properties props = new Properties();
    private static final Properties props1900 = new Properties();
    private static final ThreadLocal<String> lastKnownUrl = new ThreadLocal<>();
    private static final Random random = new Random();
    private static final DateTimeFormatter dateTimeFormat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
    public static int LONG_WAIT;
    public static int SHORT_WAIT;
    public static int MINIMAL_WAIT;
    public static int DEFAULT_WAIT;

    static {
        init();
    }

    /**
     * Instantiates a new test utils.
     */
    private TestUtils() {
    }

    public static void init() {
        loadPropertiesFromResource("/test.properties", props);
        loadPropertiesFromResource("/xpath.properties", props1900);

        // Add system properties to props file
        System.getProperties().entrySet().forEach(entry -> {
            if (entry.getKey() != null && entry.getValue() != null) {
                props.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        });

        LONG_WAIT = getPropertyAsInt("selenium.long.wait");
        SHORT_WAIT = getPropertyAsInt("selenium.short.wait");
        MINIMAL_WAIT = getPropertyAsInt("selenium.minimal.wait");
        DEFAULT_WAIT = getPropertyAsInt("selenium.default.wait");
    }

    public static void loadPropertiesFromResource(String resourcePath, Properties propsObj) {
        log.info("Reading properties from {}", resourcePath);
        try (InputStream propsStream = TestUtils.class.getResourceAsStream(resourcePath)) {
            if (propsStream != null) {
                try (final InputStreamReader reader =
                             new InputStreamReader(propsStream, StandardCharsets.UTF_8)) {
                    propsObj.load(reader);
                }
            }
        } catch (IOException e) {
            log.error("unable to read properties file", e);
        }
    }

    private static JsonElement getJsonFromResource(String resourcePath) {
        InputStream resourceStream = TestUtils.class.getResourceAsStream(resourcePath);
        JsonElement jsonData = null;
        if (resourceStream != null) {
            try (final InputStreamReader reader =
                         new InputStreamReader(resourceStream, StandardCharsets.UTF_8)) {
                jsonData = JsonParser.parseReader(reader);
            } catch (IOException e) {
                log.error("unable to read properties file", e);
            }
        }
        return jsonData;
    }

    public static JsonObject getJsonObjectFromResource(String resourcePath) {
        JsonElement jsonElement = getJsonFromResource(resourcePath);
        return jsonElement == null ? new JsonObject() : jsonElement.getAsJsonObject();
    }

    public static JsonArray getJsonArrayFromResource(String resourcePath) {
        JsonElement jsonElement = getJsonFromResource(resourcePath);
        return jsonElement == null ? new JsonArray() : jsonElement.getAsJsonArray();
    }

    public static String getProperty(String key) {
        String value;

        value = props.getProperty(key);

        return value;
    }

    public static void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value == null) ? defaultValue : value;
    }

    public static String get1900Property(String key) {
        return props1900.getProperty(key);
    }


    public static String lookupAllFilesAndGetProperty(String key) {
        String result;

        switch (SeleniumUtils.getDisplaySize()) {
            case "1004":
            case "1900":
            default:
                result = get1900Property(key);
                break;
        }

        return result;
    }

    public static int getPropertyAsInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public static boolean getPropertyAsBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public static String getUrl(String key) {

        String url = getProperty("url." + key);

        return (url == null) ? url : (getBaseUrl() + url);
    }

    public static boolean isDev() {
        return (!"prod".equalsIgnoreCase(System.getProperty("ENV", System.getProperty("SM.env"))));
    }

    /**
     * Gets the environment.
     *
     * @return the environment
     */
    public static String getEnvironment() {
        return isDev() ? "dev" : "prod";
    }

    public static String getBaseUrl() {
        return getProperty("url.base");
    }
    

    public static String extractNumberFromString(String string) {
        return string == null ? null : string.replaceAll("[^0-9?!\\.]", "");
    }

    public static String stripAll(String actualString, String... stringsToRemove) {
        String stripped = actualString;

        for (String string : stringsToRemove) {
            stripped = stripped.replace(string, "");
            stripped = stripped.replaceAll(string, "");
        }

        return stripped;
    }
   

    public static boolean sleepInMillis(long timeInMillis) {
        if (timeInMillis > 0) {
            try {
                Thread.sleep(timeInMillis);
            } catch (InterruptedException e) {
            }
        }
        return true;
    }

    public static boolean sleep(double timeInSeconds) {
        return sleepInMillis((long) (timeInSeconds * 1000));
    }
    
    public static <T extends Object> T getRandomElementFromList(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static JsonElement getRandomJsonElementFromArray(JsonArray list) {
        if (list.size() > 0) {
            return list.get(random.nextInt(list.size()));
        }

        return null;
    }

    public static String getRandomStringFromJsonArray(JsonArray list) {
        if (list.size() > 0) {
            return list.get(random.nextInt(list.size())).getAsString();
        }

        return null;
    }

  
    public static Date addToDate(Date date, int durationUnit, int number) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(durationUnit, number);
        return cal.getTime();
    }

    public static Date addToCurrentDate(int durationUnit, int number) {
        return addToDate(new Date(), durationUnit, number);
    }
    
    public static String negateValue(String value) {
        return "--" + value + "--";
    }

    public static int negateValue(int value) {
        int newValue = value;
        return (newValue == 0) ? ++newValue : newValue * 2;
    }

    public static double negateValue(double value) {
        double newValue = value;
        return (newValue == 0) ? ++newValue : newValue * 2;
    }

    public static boolean negateValue(boolean value) {
        return !value;
    }

   
    public static <T> T getFirstValueOr(T[] array, T defaultValue) {
        if (array != null && array.length > 0 && array[0] != null) {
            return array[0];
        }
        return defaultValue;
    }

    public static Integer getCountFor(String key) {
        return (int) counts.get(key);
    }
    
    public static int getAndIncrementCountFor(String key) {
        return (int) counts.getAndIncrement(key);
    }
    
    public static int incrementCountFor(String key) {
        return (int) counts.incrementAndGet(key);
    }

    public static void deleteCountsFor(String key) {
        counts.remove(key);
    }


    public static void setLastKnownUrl(String url) {
        if (url != null) {
            lastKnownUrl.set(url);
        }
    }

    public enum Result {
        FAIL("FAIL"), SKIPPED("SKIPPED"), STARTED("STARTED"), PASS("PASS"), UNKNOWN("UNKNOWN");

        private final String filter;

        Result(String filter) {
            this.filter = filter;
        }

        @Override
        public String toString() {
            return filter;
        }
    }

}
