package com.duc.chatting.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppPreference {
    private static final String PREF_NAME = "chat_app_prefs";
    private static AppPreference instance;
    private final SharedPreferences sharedPreferences;
    private static final String IS_FIRST_TIME = "is_first_time";
    private final Gson gson;

    private AppPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    public void setFirstTimeLaunch(boolean isFirstTime) {
        sharedPreferences.edit().putBoolean(IS_FIRST_TIME, isFirstTime).apply();
    }
    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true);
    }
    public static synchronized AppPreference getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreference(context.getApplicationContext());
        }
        return instance;
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultVal) {
        return sharedPreferences.getString(key, defaultVal);
    }

    public void putStringSet(String key, Set<String> values) {
        sharedPreferences.edit().putStringSet(key, values).apply();
    }

    public Set<String> getStringSet(String key) {
        return sharedPreferences.getStringSet(key, new HashSet<>());
    }

    public void putMap(String key, Map<String, List<String>> map) {
        String json = gson.toJson(map);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public Map<String, List<String>> getMap(String key) {
        String json = sharedPreferences.getString(key, "{}");
        Type type = new TypeToken<HashMap<String, List<String>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
