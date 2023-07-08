package org.tooling.core.base;


import org.tooling.core.actions.SeoMonitorCommonActions;
import org.tooling.selenium.BrowserUtils;
import org.tooling.core.user.SeoMonitorTestUser;

import java.util.HashMap;
import java.util.Map;

public class CurrentTest {

    private static final ThreadLocal<BrowserUtils> localBrowserUtils = new ThreadLocal<>();

    private static final ThreadLocal<Map<Class<?>, Object>> objects = ThreadLocal.withInitial(() -> new HashMap<>());

    private static final ThreadLocal<SeoMonitorPageCollection> seoMonitorPages = new ThreadLocal<>();

    private static final ThreadLocal<SeoMonitorTestUser> seoMonitorTestUsers = new ThreadLocal<>();

    private static SeoMonitorCommonActions seoMonitorActions;


    private CurrentTest() {
    }

    public static void include(BrowserUtils browserUtils) {
        localBrowserUtils.set(browserUtils);
        seoMonitorPages.set(new SeoMonitorPageCollection(browserUtils));

        if (seoMonitorActions == null) {
            seoMonitorActions = new SeoMonitorCommonActions(localBrowserUtils, seoMonitorPages);
        }
    }

    public static SeoMonitorPageCollection getSeoMonitorPages() {
        return seoMonitorPages.get();
    }

    public static SeoMonitorCommonActions getSeoMonitorActions() {
        return seoMonitorActions;
    }

    public static BrowserUtils getBrowserUtils() {
        return localBrowserUtils.get();
    }

    public static SeoMonitorTestUser getSeoMonitorTestUser() {
        return seoMonitorTestUsers.get();
    }

    public static void setSeoMonitorTestUser(SeoMonitorTestUser user) {
        seoMonitorTestUsers.set(user);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> class1) {
        return (T) objects.get().get(class1);
    }

    public static <T> void put(Class<T> class1, T object) {
        objects.get().put(class1, object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T remove(Class<T> class1) {
        return (T) objects.get().remove(class1);
    }

    public static void clearAllObjects() {
        objects.get().clear();
    }

    public static void clear() {
        seoMonitorTestUsers.remove();
        seoMonitorPages.remove();
        localBrowserUtils.remove();
        objects.remove();
    }

}
