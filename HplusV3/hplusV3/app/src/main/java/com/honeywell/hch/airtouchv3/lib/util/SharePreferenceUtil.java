package com.honeywell.hch.airtouchv3.lib.util;

import com.honeywell.hch.airtouchv3.HPlusApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by allanhwmac on 15/10/22.
 */
public class SharePreferenceUtil {

    private static SharedPreferences mSharePreference;

    private static final String GROUPCONRROL = "group_control";

    public static SharedPreferences getSharedPreferencesInstance() {
        if (mSharePreference == null) {
            mSharePreference = HPlusApplication.getInstance().getSharedPreferences(GROUPCONRROL, Context.MODE_PRIVATE);
        }
        return mSharePreference;
    }

    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(HPlusApplication.getInstance());
    }

    public static String getPrefString(String key,
            final String defaultValue) {

        return getSharedPreferences().getString(key, defaultValue);
    }

    public static void setPrefString(final String key,
            final String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

    public static boolean getPrefBoolean(final String key,
            final boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    public static boolean getMyPrefBoolean(final String key,
                                           final boolean defaultValue) {
        return getSharedPreferencesInstance().getBoolean(key, defaultValue);
    }

    public static boolean hasKey(final String key) {
        return getSharedPreferences().contains(key);
    }

    public static void setPrefBoolean(final String key,
            final boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    public static void setMyPrefBoolean(final String key,
                                        final boolean value) {
        getSharedPreferencesInstance().edit().putBoolean(key, value).commit();
    }

    public static void setPrefInt(final String key,
            final int value) {
        getSharedPreferences().edit().putInt(key, value).commit();
    }

    public static int getPrefInt(final String key,
            final int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    public static void setPrefFloat(final String key,
            final float value) {
        getSharedPreferences().edit().putFloat(key, value).commit();
    }

    public static float getPrefFloat(final String key,
            final float defaultValue) {
        return getSharedPreferences().getFloat(key, defaultValue);
    }

    public static void setSettingLong(final String key,
            final long value) {
        getSharedPreferences().edit().putLong(key, value).commit();
    }

    public static long getPrefLong(final String key,
            final long defaultValue) {
        return getSharedPreferences().getLong(key, defaultValue);
    }

    public static void clearPreference(Context context,
            final SharedPreferences p) {
        final SharedPreferences.Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }

    public static void clearMyPreference(Context context,
                                         final SharedPreferences p) {
        final SharedPreferences.Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }
}
