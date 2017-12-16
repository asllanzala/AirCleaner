package com.honeywell.hch.airtouchv2;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.config.UserConfig;
import com.honeywell.hch.airtouchv2.framework.database.CityDBService;
import com.honeywell.hch.airtouchv2.lib.location.CityInfo;
import com.honeywell.hch.airtouchv2.lib.location.LocationManager;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv2.framework.notification.MorningAlarmReceiver;
import com.honeywell.hch.airtouchv2.framework.notification.NightAlarmReceiver;
import com.honeywell.hch.airtouchv2.lib.util.DateTimeUtil;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Initial application
 * Created by nan.liu on 1/14/15.
 */
public class ATApplication extends Application {

    private static String TAG = "AirTouchATApp";

    private static ATApplication application = null;

    private AppConfig mAppConfig;
    private UserConfig mUserConfig;
    private City mSelectedGPSCity;

    private static int localVersion = 0;// 本地安装版本
    private static VersionCollector mVersionCollector = null;

    private static final int GPS_TIMEOUT = 30 * 1000;
    private static final int INTERVAL = 1000 * 60 * 60 * 24;

    public static ATApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mUserConfig = new UserConfig(this);
        mAppConfig = AppConfig.shareInstance();

        initLogSettings();
        initAppConfig();
        initUserConfig();
        initGps();
        initAlarm();
        new MyPlistThread().start();
    }

    /**
     * initial application log setting
     */
    private void initLogSettings() {
        LogUtil.setIsLogEnabled(true);
//        LogUtil.setIsLogSaved(true);
//        LogUtil.setLogFileLevel(LogUtil.LogLevel.ERROR);
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
        mUserConfig.loadUserInfo();

        // deprecated
//        if (AuthorizeApp.shareInstance().isAutoLogin()) {
//            AuthorizeApp.shareInstance().currentUserLogin();
//        }

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
                CityDBService cityDBService = new CityDBService(ATApplication.this);
                cityDBService.insertAllCity(citiesList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initGps() {
        LocationManager.getInstance()
                .registerGPSLocationListener(mMessageHandler);

        //Start GPS timeout thread
        TimeoutCheckThread timeoutCheckThread = new TimeoutCheckThread();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            timeoutCheckThread
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            timeoutCheckThread.execute("");
        }
    }

    /**
     * Receive GPS location update
     */
    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null)
                return;
            switch (msg.what) {
                //GPS find the city
                case LocationManager.HANDLER_GPS_LOCATION:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        CityInfo cityLocation = (CityInfo) bundle
                                .getSerializable(LocationManager.HANDLER_MESSAGE_KEY_GPS_LOCATION);
                        if (cityLocation != null) {
                            processLocation(cityLocation);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void processLocation(CityInfo cityLocation) {
        CityDBService cityDBService = new CityDBService(this);
        if (cityLocation != null) {
            City city = cityDBService.getCityByName(cityLocation.getCity());
            if (city != null) {
                mSelectedGPSCity = city;
                mAppConfig.setGpsCityCode(mSelectedGPSCity.getCode());
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "GPS city: " + mSelectedGPSCity.getNameZh());
            }
        }
    }

    /**
     * 20S GPS timeout
     */
    private class TimeoutCheckThread extends
            AsyncTask<String, Integer, String> {

        public TimeoutCheckThread() {

        }

        @Override
        protected String doInBackground(String... params) {
            Date date1 = new Date();
            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "GPS locating start：" + date1.toLocaleString());

            try {
                Thread.sleep(GPS_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Date date2 = new Date();
            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "GPS locating end：" + date2.toLocaleString());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mSelectedGPSCity == null) {
                LocationManager.getInstance().unRegisterGPSLocationListener(mMessageHandler);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "GPS locating timeout");
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

}
