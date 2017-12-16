package com.honeywell.hch.airtouchv2.framework.config;

import android.content.Context;
import android.content.res.Configuration;

import com.honeywell.hch.airtouchv2.ATApplication;

import java.util.Calendar;

/**
 * configuration for app
 * Created by nan.liu on 1/15/15.
 */
public class AppConfig extends BaseConfig {
    public static Boolean isDebugMode;
    public static Boolean isLauchendFirstTime;
    public static Boolean isHouseTutorial;
    public static Boolean isControlTutorial;
    public static Boolean isFilterTutorial;
    public static Boolean isHomeTutorial;
    private static String language = null;
    private static String mGpsCityCode = null;
    public static Boolean isFilterScrollPage = false;
    private static boolean isDaylight = false;
    private static boolean isHomePageCover = false;

    public static final String LANGUAGE_ZH = "zh";
    public static final String LANGUAGE_XINZHI = "zh-chs";
    public static final String LANGUAGE_EN = "en";
    public static final String APPLICATION_ID = "1237b42b-0ce7-4582-830c-34d930b1fd52";

    private static final int MORNING_TIME = 6;
    private static final int NIGHT_TIME = 18;

    private static AppConfig appConfig = null;

    public AppConfig(Context context) {
        super(context);
    }

    public static AppConfig shareInstance() {
        if (appConfig == null) {
            appConfig = new AppConfig(ATApplication.getInstance());
        }
        return appConfig;
    }

    // load sharedPreference data to CurrentApp
    public void loadAppInfo() {
        isDebugMode = false;
        isLauchendFirstTime = true;
        isHouseTutorial = getSharedPreferences().getBoolean("isHouseTutorial", false);
        isControlTutorial = getSharedPreferences().getBoolean("isControlTutorial", false);
        isFilterTutorial = getSharedPreferences().getBoolean("isFilterTutorial", false);
        isHomeTutorial = getSharedPreferences().getBoolean("isHomeTutorial", false);
        mGpsCityCode = getSharedPreferences().getString("gpsCityCode", "");
        Calendar calendar = Calendar.getInstance();
        isDaylight = calendar.get(Calendar.HOUR_OF_DAY) >= MORNING_TIME && calendar.get(Calendar
                .HOUR_OF_DAY) < NIGHT_TIME;
    }

    public void setIsHouseTutorial(Boolean isTutorial) {
        isHouseTutorial = isTutorial;
        getSharedPreferencesEditor().putBoolean("isHouseTutorial", isTutorial);
        getSharedPreferencesEditor().commit();
    }

    public void setIsControlTutorial(Boolean isTutorial) {
        isControlTutorial = isTutorial;
        getSharedPreferencesEditor().putBoolean("isControlTutorial", isTutorial);
        getSharedPreferencesEditor().commit();
    }

    public void setIsFilterTutorial(Boolean isTutorial) {
        isFilterTutorial = isTutorial;
        getSharedPreferencesEditor().putBoolean("isFilterTutorial", isTutorial);
        getSharedPreferencesEditor().commit();
    }

    public void setIsHomeTutorial(Boolean isTutorial) {
        isHomeTutorial = isTutorial;
        getSharedPreferencesEditor().putBoolean("isHomeTutorial", isTutorial);
        getSharedPreferencesEditor().commit();
    }

    public String getLanguage() {
        Configuration config = mContext.getResources().getConfiguration();
        language = config.locale.getLanguage();
        return language;
    }

    public static String getLanguageXinzhi() {
        String language_xinzhi = language;
        if (LANGUAGE_ZH.equals(language))
            language_xinzhi = LANGUAGE_XINZHI;
        return language_xinzhi;
    }

    public String getGpsCityCode() {
        return mGpsCityCode;
    }

    public void setGpsCityCode(String gpsCity) {
        mGpsCityCode = gpsCity;
        getSharedPreferencesEditor().putString("gpsCityCode", gpsCity);
        getSharedPreferencesEditor().commit();
    }

    public boolean isDaylight() {
        return isDaylight;
    }

    public void setDaylight(boolean isDaylight) {
        appConfig.isDaylight = isDaylight;
    }

    public boolean getCurrentDaylight() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) >= MORNING_TIME && calendar.get(Calendar
                .HOUR_OF_DAY) < NIGHT_TIME;
    }

    public void refreshDaylight() {
        Calendar calendar = Calendar.getInstance();
        isDaylight = calendar.get(Calendar.HOUR_OF_DAY) >= MORNING_TIME && calendar.get(Calendar
                .HOUR_OF_DAY) < NIGHT_TIME;
    }

    public boolean isHomePageCover() {
        return isHomePageCover;
    }

    public void setHomePageCover(boolean isHomePageCover) {
        appConfig.isHomePageCover = isHomePageCover;
    }
}
