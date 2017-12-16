package com.honeywell.hch.airtouchv3;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.config.UserConfig;
import com.honeywell.hch.airtouchv3.framework.database.CityChinaDBService;
import com.honeywell.hch.airtouchv3.framework.database.CityIndiaDBService;
import com.honeywell.hch.airtouchv3.framework.notification.MorningAlarmReceiver;
import com.honeywell.hch.airtouchv3.framework.notification.NightAlarmReceiver;
import com.honeywell.hch.airtouchv3.framework.share.ShareUtility;
import com.honeywell.hch.airtouchv3.lib.util.DateTimeUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.umeng.socialize.PlatformConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Initial mHPlusApplication
 * Created by nan.liu on 1/14/15.
 */
public class HPlusApplication extends Application {

    private static String TAG = HPlusApplication.class.getSimpleName();

    private static HPlusApplication mHPlusApplication = null;

    private AppConfig mAppConfig;
    private UserConfig mUserConfig;
    private City mSelectedGPSCity;

    private static int localVersion = 0;// 本地安装版本
    private static VersionCollector mVersionCollector = null;

    private static final int GPS_TIMEOUT = 30 * 1000;
    private static final int INTERVAL = 1000 * 60 * 60 * 24;

    public static HPlusApplication getInstance() {
        return mHPlusApplication;
    }

    public static void setHPlusApplication(HPlusApplication application) {
        mHPlusApplication = application;
    }

    private  CityChinaDBService mCityChinaDBService = null;
    private  CityIndiaDBService mCityIndiaDBService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mHPlusApplication = this;
        mUserConfig = new UserConfig(this);
        mAppConfig = AppConfig.shareInstance();

        initLogSettings();
        initAppConfig();
        initUserConfig();

        initAlarm();
        new MyPlistThread().start();

        AppManager.shareInstance().getGpsUserLocation().setLocationID(0);
        mCityChinaDBService = new CityChinaDBService(this);
        mCityIndiaDBService = new CityIndiaDBService(this);

    }

    {
        PlatformConfig.setSinaWeibo(ShareUtility.WB_APPID, ShareUtility.WB_APPSECRET);
    }
    /**
     * initial mHPlusApplication log setting
     */
    private void initLogSettings() {
        LogUtil.setIsLogEnabled(true);
//        LogUtil.setIsLogSaved(true);
//        LogUtil.setLogFileLevel(LogUtil.LogLevel.INFO);
        LogUtil.setLogFileName(DateTimeUtil.getNowDateTimeString(DateTimeUtil.LOG_TIME_FORMAT) + ".log");
    }

    /**
     * initial app information
     */
    private void initAppConfig() {
        mAppConfig.loadAppInfo();
    }

    /**
     * initial user information logged in last time
     */
    private void initUserConfig() {
        AppManager.shareInstance().getAuthorizeApp().loadUserInfo();
        mUserConfig.loadDefaultHome();
        mUserConfig.loadDefaultHomeId();
    }


    class MyPlistThread extends Thread {
        @Override
        public void run() {
            try {
                InputStream is = getResources().openRawResource(R.raw.citylist);
                NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(is);
                List<City> citiesList = new ArrayList<>();
                NSObject[] cities = ((NSArray) rootDict.objectForKey("citylist")).getArray();
                for (NSObject cityObject : cities) {
                    NSDictionary cityDictionary = (NSDictionary) cityObject;
                    City city = new City();
                    city.setNameZh(cityDictionary.objectForKey("cityNameZh").toString());
                    city.setNameEn(cityDictionary.objectForKey("cityNameEn").toString());
                    city.setCode(cityDictionary.objectForKey("cityCode").toString());
                    citiesList.add(city);
                }
                mCityChinaDBService.insertAllCity(citiesList);

                InputStream is2 = getResources().openRawResource(R.raw.citylist_91);
                NSDictionary rootDict2 = (NSDictionary) PropertyListParser.parse(is2);
                List<City> citiesList2 = new ArrayList<>();
                NSObject[] cities2 = ((NSArray) rootDict2.objectForKey("citylist")).getArray();
                for (NSObject cityObject : cities2) {
                    NSDictionary cityDictionary = (NSDictionary) cityObject;
                    City city = new City();
                    city.setNameZh(cityDictionary.objectForKey("cityNameZh").toString());
                    city.setNameEn(cityDictionary.objectForKey("cityNameEn").toString());
                    city.setCode(cityDictionary.objectForKey("cityCode").toString());
                    citiesList2.add(city);
                }
                mCityIndiaDBService.insertAllCity(citiesList2);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private void initAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Morning alarm
        Intent intent1 = new Intent(this, MorningAlarmReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, intent1,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar1 = Calendar.getInstance();
//        calendar1.setTimeInMillis(System.currentTimeMillis());
        calendar1.set(Calendar.HOUR_OF_DAY, 6);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC, calendar1.getTimeInMillis(), INTERVAL,
                pendingIntent1);

        // Night alarm
        Intent intent2 = new Intent(this, NightAlarmReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this,
                0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        calendar2.set(Calendar.HOUR_OF_DAY, 18);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
                INTERVAL, pendingIntent2);

    }

    public static VersionCollector getVersionCollector() {
        if(mVersionCollector == null)
        {
            mVersionCollector = new VersionCollector();
        }
        return mVersionCollector;
    }

    public static void setVersionCollector(VersionCollector versionCollector) {
        mVersionCollector = versionCollector;
//        persistVersionCollector();
    }


    public  CityChinaDBService getCityChinaDBService(){
        return mCityChinaDBService;
    }

    public  CityIndiaDBService getCityIndiaDBService(){
        return mCityIndiaDBService;
    }

}
