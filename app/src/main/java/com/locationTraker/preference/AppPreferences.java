package com.locationTraker.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences {
    private static SharedPreferences.Editor editor;
    private static SharedPreferences preferences;

    public static void setSharedPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public static void saveString(String pref_key, String value) {
        editor.putString(pref_key, value);
        editor.commit();
    }

    public static String getString(String pref_key) {
        return preferences.getString(pref_key, "");
    }

    public static void saveBool(String pref_key, Boolean value) {
        editor.putBoolean(pref_key, value);
        editor.commit();
    }

    public static Boolean getBool(String pref_key) {
        return preferences.getBoolean(pref_key, false);
    }

    public static void removePref(String preference_key) {
        editor.remove(preference_key).commit();  //Remove Spec key values
    }

    public static void saveLong(String pref_key, Long value) {
        editor.putLong(pref_key, value);
        editor.commit();
    }

    public static Long getLong(String pref_key) {
        return preferences.getLong(pref_key, 0);
    }
}
