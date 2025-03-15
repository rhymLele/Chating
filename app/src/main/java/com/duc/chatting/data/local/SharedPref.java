package com.duc.chatting.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    final Context context;
    final SharedPreferences sharedPref;
    final SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("le-sserafim", 0);
        editor = sharedPref.edit();
    }

    public String getStringData(String key) {
        return sharedPref.getString(key, null);
    }

    public void setStringData(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public Boolean getBooleanData(String key) {
        return sharedPref.getBoolean(key, false);
    }

    public void setBooleanData(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

}
