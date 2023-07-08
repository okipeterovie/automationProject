package org.tooling.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class DataUtils {

    private static final JsonObject jsonData;

    private static final Gson gson = new Gson();

    static {
        jsonData = TestUtils.getJsonObjectFromResource("/data.json");
    }

    private DataUtils() {
    }

    public static String getJSSnippetFor(String name) {
        return jsonData.get("js snippets").getAsJsonObject().get(name).getAsString();
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static JsonElement fromJson(String jsonString) {
        return JsonParser.parseString(jsonString);
    }

    public static <T> T fromJson(JsonElement el, Class<T> class1) {
        return gson.fromJson(el, class1);
    }

    public static <T> T fromJson(String jsonString, Class<T> class1) {
        return gson.fromJson(jsonString, class1);
    }

}
