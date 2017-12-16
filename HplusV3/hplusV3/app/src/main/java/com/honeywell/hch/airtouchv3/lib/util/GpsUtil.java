package com.honeywell.hch.airtouchv3.lib.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.database.CityChinaDBService;
import com.honeywell.hch.airtouchv3.framework.database.CityIndiaDBService;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.lib.location.CityInfo;
import com.honeywell.hch.airtouchv3.lib.location.LocationManager;

/**
 * Created by wuyuan on 1/19/16.
 */
public class GpsUtil {

    private static final String TAG = "GpsUtil";

    private CityChinaDBService mCityChinaDBService = null;
    private CityIndiaDBService mCityIndiaDBService = null;

    private AppConfig mAppConfig;

    private static final int GPS_TIMEOUT = 30 * 1000;

    private City mSelectedGPSCity;

    public GpsUtil(CityChinaDBService cityChinaDBService, CityIndiaDBService cityIndiaDBService){
        mCityChinaDBService = cityChinaDBService;
        mCityIndiaDBService = cityIndiaDBService;
        mAppConfig = AppConfig.shareInstance();
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
                        if (StringUtil.isEmpty(mAppConfig.getGpsCityCode()) || isNeedUpdateGpsInfo(cityLocation)) {
                          processLocation(cityLocation);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * when the current location is located success and is different with the last .need to update
     * @param cityLocation
     * @return
     */
    private boolean isNeedUpdateGpsInfo(CityInfo cityLocation){
        if (cityLocation == null || cityLocation.getCity() == null){
            return false;
        }
        else if (!StringUtil.isEmpty(cityLocation.getCity()) && !cityLocation.getCity().equals(mAppConfig.getGpsCityCode())){
            return true;
        }

        return false;
    }

    public void initGps() {
        LocationManager.getInstance()
                .registerGPSLocationListener(mMessageHandler);

        //Start GPS timeout thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(GPS_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mSelectedGPSCity == null) {
                    LocationManager.getInstance().unRegisterGPSLocationListener(mMessageHandler);
                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "GPS locating timeout");
                    processLocation(null);
                }
            }
        }).start();

    }

//    /**
//     * 20S GPS timeout
//     */
//    private class TimeoutCheckThread extends
//            AsyncTask<String, Integer, String> {
//
//        public TimeoutCheckThread() {
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            Date date1 = new Date();
////            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "GPS locating start：" + date1.toLocaleString());
//
//            try {
//                Thread.sleep(GPS_TIMEOUT);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            Date date2 = new Date();
////            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "GPS locating end：" + date2.toLocaleString());
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (mSelectedGPSCity == null) {
//                LocationManager.getInstance().unRegisterGPSLocationListener(mMessageHandler);
//                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "GPS locating timeout");
//                processLocation(null);
//            }
//        }
//    }


    private void processLocation(CityInfo cityLocation) {

        if (cityLocation != null) {
            City city = mCityChinaDBService.getCityByName(cityLocation.getCity());
            // India version
            if (city.getNameEn() != null) {
                AppManager.shareInstance().getAuthorizeApp()
                        .setGPSCountry(AirTouchConstants.CHINA_CODE);
            } else {
                city = mCityIndiaDBService.getCityByKey(cityLocation.getCity());
                if (city.getNameEn() != null) {
                    AppManager.shareInstance().getAuthorizeApp()
                            .setGPSCountry(AirTouchConstants.INDIA_CODE);
                }
            }
            if (cityLocation.getCity() != null) {
                //gps success
                if (city.getNameEn() != null && city.getCode() != null) {
                    mSelectedGPSCity = city;
                    mAppConfig.setGpsCityCode(mSelectedGPSCity.getCode());
                    AppManager.shareInstance().getGpsUserLocation().setCity(mSelectedGPSCity.getCode());
                } else {
                    // The located city is not in database.
                    mAppConfig.setGpsCityCode(cityLocation.getCity());
                }
            } else {
                mAppConfig.setGpsCityCode(AppConfig.LOCATION_FAIL);
            }
        } else {
            mAppConfig.setGpsCityCode(AppConfig.LOCATION_FAIL);
        }

        Intent intent = new Intent();
        intent.setAction(AirTouchConstants.GPS_RESULT);
        HPlusApplication.getInstance().getApplicationContext().sendBroadcast(intent);
    }


}
