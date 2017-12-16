package com.honeywell.hch.airtouchv3.framework.config;

import android.content.Context;
import android.content.res.Configuration;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.database.CityChinaDBService;
import com.honeywell.hch.airtouchv3.framework.database.CityIndiaDBService;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import java.util.Calendar;

/**
 * configuration for app
 * Created by nan.liu on 1/15/15.
 */
public class AppConfig extends BaseConfig {

    public static final String LOCATION_FAIL = "fail";

    public static boolean isTestMode;
    public static Boolean isDebugMode;
    public static boolean isChangeEnv = false;

    public static Boolean isLauchendFirstTime;
    public static Boolean isHouseTutorial;
    public static Boolean isControlTutorial;
    public static Boolean isFilterTutorial;
    public static Boolean isHomeTutorial;
    public static Boolean isWeatherTutorial;
    private static String language = null;
    private static String mGpsCityCode = "";
    private static String mLastGpsCityCode = null;

    public static Boolean isFilterScrollPage = false;
    private static boolean isDaylight = false;
    private static boolean isHomePageCover = false;

    public static final String LANGUAGE_ZH = "zh";
    public static final String LANGUAGE_XINZHI = "zh-chs";
    public static final String LANGUAGE_EN = "en";
    public static final String APPLICATION_ID = "1237b42b-0ce7-4582-830c-34d930b1fd52";

    private static final int MORNING_TIME = 6;
    private static final int NIGHT_TIME = 18;

    public static int mInitTime = 0;

    private static AppConfig appConfig = null;

    private boolean isFirstLogin = true;

    private boolean  isDifferent = false;

    public AppConfig(Context context) {
        super(context);
    }

    public static AppConfig shareInstance() {
        if (appConfig == null) {
            appConfig = new AppConfig(HPlusApplication.getInstance());
        }
        return appConfig;
    }

    // load sharedPreference data to CurrentApp
    public void loadAppInfo() {
        isDebugMode = false;
        isLauchendFirstTime = true;
        isHouseTutorial = getSharedPreferences().getBoolean("isHouseTutorial", false);
        isControlTutorial = getSharedPreferences().getBoolean("isControlTutorial", false);
        isWeatherTutorial = getSharedPreferences().getBoolean("isWeatherTutorial", false);
        isFilterTutorial = getSharedPreferences().getBoolean("isFilterTutorial", false);
        isHomeTutorial = getSharedPreferences().getBoolean("isHomeTutorial", false);
        mLastGpsCityCode = getSharedPreferences().getString("gpsCityCode", "");
//        mLastGpsCityCode = "";
        mInitTime = getSharedPreferences().getInt("updateVersionTime",0);
        Calendar calendar = Calendar.getInstance();
        isDaylight = calendar.get(Calendar.HOUR_OF_DAY) >= MORNING_TIME && calendar.get(Calendar
                .HOUR_OF_DAY) < NIGHT_TIME;
    }

    public void setUpdateVersionTime(int time) {
        mInitTime = time;
        getSharedPreferencesEditor().putInt("updateVersionTime", time);
        getSharedPreferencesEditor().commit();
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

    public  void setIsWeatherTutorial(Boolean isWeatherTutorial) {
        AppConfig.isWeatherTutorial = isWeatherTutorial;
        getSharedPreferencesEditor().putBoolean("isWeatherTutorial", isWeatherTutorial);
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
        if (!StringUtil.isEmpty(gpsCity) && !AppConfig.LOCATION_FAIL.equals(gpsCity)
                && !gpsCity.equals(mLastGpsCityCode)){
            isDifferent = true;
            mLastGpsCityCode = gpsCity;
        }
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

    public String getLastGpsCityCode(){
        return mLastGpsCityCode;
    }

    public boolean isDifferent() {
        return isDifferent;
    }

    public void setIsDifferent(boolean isDifferent) {
        this.isDifferent = isDifferent;
    }

    public void resetLastGpsWithNowVaule(){
        mLastGpsCityCode = mGpsCityCode;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public boolean isIndiaAccount() {
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
        if (authorizeApp != null) {
            if (authorizeApp.getCountryCode() != null) {
                if (authorizeApp.getCountryCode().equals(AirTouchConstants.INDIA_CODE))
                    return true;
            }
        }

        return false;
    }

    public boolean isLocatedInIndia() {
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
        if (authorizeApp != null) {
            if (authorizeApp.getGPSCountry() != null) {
                if (authorizeApp.getGPSCountry().equals(AirTouchConstants.INDIA_CODE))
                    return true;
            }
        }

        return false;
    }

    public City getCityFromDatabase(String cityCode) {
        CityChinaDBService cityChinaDBService = new CityChinaDBService(mContext);
        CityIndiaDBService cityIndiaDBService = new CityIndiaDBService(mContext);

        City city = cityChinaDBService.getCityByCode(cityCode);
        if (city.getNameEn() == null) {
            city = cityIndiaDBService.getCityByCode(cityCode);
        }

        return city;
    }
}
