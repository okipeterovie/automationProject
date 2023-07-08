package org.tooling.core.user;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.tooling.core.user.SeoMonitorTestUser;
import org.tooling.util.TestUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log4j2
public class SeoMonitorTestUsers {

    private static final Gson gson = new Gson();

    private static final ConcurrentHashMap<Thread, SeoMonitorTestUser> currentlyLoggedInUserByThread = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Thread, List<SeoMonitorTestUser>> currentRegisteredUsersByThread = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Thread, List<SeoMonitorTestUser>> currentlyTakenUsersByThread = new ConcurrentHashMap<>();

    private static JsonObject jsonData;

    private static ConcurrentHashMap<SeoMonitorTestUser, Boolean> allUsersAndAvailability = new ConcurrentHashMap<>();

    private SeoMonitorTestUsers() {
    }

    private static synchronized void loadFromFile() {

        if (jsonData != null && allUsersAndAvailability != null && !allUsersAndAvailability.isEmpty()) {
            return;
        }

        if (jsonData == null) {
            log.info("getting data from json");
            jsonData = TestUtils.getJsonObjectFromResource("/seo_monitor_users.json");
        }

        if (allUsersAndAvailability == null) {
            allUsersAndAvailability = new ConcurrentHashMap<>();
        }

        log.info("populating map with all users from json");

        JsonElement testUserDetails = jsonData.get("users").getAsJsonObject();
        Map<String, SeoMonitorTestUser> users = gson.fromJson(testUserDetails, new TypeToken<Map<String, SeoMonitorTestUser>>() {
        }.getType());

        users.entrySet().stream()
                .filter(entry -> entry.getValue().getEmail() != null)
                .forEach(entry -> {
                    entry.getValue().setShortUsername(entry.getKey());
                });

        for (SeoMonitorTestUser user : users.values()) {
            allUsersAndAvailability.put(user, true);
        }

    }

    public static SeoMonitorTestUser getUser(String shortUsername) {
        SeoMonitorTestUser user = getUserWithoutChangingAvailability(shortUsername);
        takeUser(user);
        log.info("user selected: " + user);
        return user;
    }

    public static SeoMonitorTestUser getUserFromFile(String shortUsername) {
        SeoMonitorTestUser currentUser = getUserWithoutChangingAvailability(shortUsername);

        SeoMonitorTestUser originalUser = null;
        try {
            JsonElement testUserDetails = jsonData.get("users").getAsJsonObject().get(shortUsername);
            originalUser = gson.fromJson(testUserDetails, SeoMonitorTestUser.class);

            if (originalUser.getEmail() != null) {
                originalUser.setShortUsername(shortUsername);
            }

        } catch (NullPointerException e) {
            log.info("requested user data not found, username - '" + shortUsername + "'. check json file.", e);
        }

        if (originalUser != null) {
            allUsersAndAvailability.remove(currentUser);
            allUsersAndAvailability.put(originalUser, true);
            takeUser(originalUser);
        }

        return originalUser;
    }

    public static SeoMonitorTestUser getUserWithoutChangingAvailability(String shortUsername) {

        loadFromFile();

        return allUsersAndAvailability.entrySet().stream()
                .filter(entry -> shortUsername.equals(entry.getKey().getShortUsername()))
                .map(entry -> entry.getKey())
                .findFirst().orElse(null);
    }

    public static Set<SeoMonitorTestUser> getAllTakenUsers() {
        Set<SeoMonitorTestUser> users = allUsersAndAvailability.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(entry -> entry.getKey())
                .collect(Collectors.toSet());

        users.addAll(currentlyTakenUsersByThread.values().stream().flatMap(list -> list.stream()).collect(Collectors.toSet()));

        return users;
    }


    private static void updateAvailability(SeoMonitorTestUser user, boolean isAvailable) {
        if (allUsersAndAvailability.containsKey(user)) {
            allUsersAndAvailability.put(user, isAvailable);
        }
    }

    public static void takeUser(SeoMonitorTestUser user) {
        if (user == null) {
            return;
        }

        updateAvailability(user, false);
        addToTakenUsers(user);
    }

    private static void addToTakenUsers(SeoMonitorTestUser user) {
        List<SeoMonitorTestUser> users = currentlyTakenUsersByThread.computeIfAbsent(Thread.currentThread(),
                k -> new ArrayList<>());

        if (!users.contains(user)) {
            users.add(user);
        }

        if (user.getFeatures() != null) {
            user.getFeatures().add("used");
        }
    }

    public static SeoMonitorTestUser getCurrentUser() {
        return currentlyLoggedInUserByThread.get(Thread.currentThread());
    }

    public static void setCurrentUser(SeoMonitorTestUser user) {
        if (user == null) {
            currentlyLoggedInUserByThread.remove(Thread.currentThread());
        } else {
            currentlyLoggedInUserByThread.put(Thread.currentThread(), user);
            updateAvailability(user, false);
        }
    }

    public static Set<SeoMonitorTestUser> getAllFreeUsers() {
        return allUsersAndAvailability.entrySet().stream()
                .filter(entry -> entry.getValue())
                .map(entry -> entry.getKey())
                .collect(Collectors.toSet());
    }

    public static Set<SeoMonitorTestUser> getAllCurrentLoggedInUsers() {
        Set<SeoMonitorTestUser> users = new HashSet<>(currentlyLoggedInUserByThread.values());
        log.debug("all logged in users - " + users);
        return users;
    }

    public static boolean isLoggedIn(SeoMonitorTestUser user) {
        return currentlyLoggedInUserByThread.containsValue(user);
    }

    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }


}
