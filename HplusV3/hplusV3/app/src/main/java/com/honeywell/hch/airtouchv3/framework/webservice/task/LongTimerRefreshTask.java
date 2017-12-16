package com.honeywell.hch.airtouchv3.framework.webservice.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.database.CityChinaDBService;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HourlyFuture;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HourlyHistory;
import com.honeywell.hch.airtouchv3.framework.webservice.ThinkPageClient;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wuyuan on 13/10/2015.
 * this task is used for refresh the datas that need to refresh not frequently
 * like weather data,emotional data.
 */
public class LongTimerRefreshTask extends BaseRequestTask {

    private static final int POLLING_GAP = 30 * 3 * 20 * 1000;

    private boolean isRunning = true;

    private CityChinaDBService mCityChinaDBService;

    private static final int BASE_WEATHER_DATA = -1;
    private static final int HOURLY_WEATHER_DATA = -2;

    private static final String BUNDLE_KEY = "city";


    // Weather data map
    private HashMap<String, WeatherPageData> mWeatherDataHashMap = new HashMap<>();

    public LongTimerRefreshTask() {

    }

    @Override
    protected ResponseResult doInBackground(Object... params) {
        //get weather data and send broad cast to refresh weather data
            try{
                getCityWeatherData();
            }catch (Exception e){
                LogUtil.log(LogUtil.LogLevel.ERROR, "LongTimerRefresh", "doInBackground exception =" + e.toString());
            }


        return null;
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {
        Intent boradIntent = new Intent(AirTouchConstants.LONG_REFRESH_END_ACTION);
        HPlusApplication.getInstance().getApplicationContext().sendBroadcast(boradIntent);

        super.onPostExecute(responseResult);
    }


    private void getCityWeatherData() {
        if (mCityChinaDBService == null) {
            mCityChinaDBService = new CityChinaDBService(HPlusApplication.getInstance().getApplicationContext());
        }

        List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
        StringBuffer stringBuffer = new StringBuffer();
        String gpsLocationCity = AppManager.shareInstance().getGpsUserLocation().getCity();
        if (!StringUtil.isEmpty(gpsLocationCity)) {
            stringBuffer.append(gpsLocationCity + ",");
        }
        for (UserLocationData userLocationData : userLocationDataList) {
            if (stringBuffer.indexOf(userLocationData.getCity()) == -1) {
                stringBuffer.append(userLocationData.getCity());
                stringBuffer.append(",");
            }
        }
        String cityList = "";
        if (stringBuffer.length() > 0) {
            cityList = stringBuffer.substring(0, stringBuffer.length() - 1);
        }

        ResponseResult responseResult = ThinkPageClient.sharedInstance().getWeatherDataNew
                (cityList, AppConfig.getLanguageXinzhi(), 'c', RequestID.ALL_DATA);
        if (responseResult != null && responseResult.isResult()) {
            mWeatherDataHashMap = (HashMap<String, WeatherPageData>) responseResult
                    .getResponseData().getSerializable(AirTouchConstants.WEATHER_DATA_KEY);
            AppManager.shareInstance().setWeatherPageDataHashMap(mWeatherDataHashMap);
            Message message1 = Message.obtain();
            message1.what = BASE_WEATHER_DATA;
            mHandler.sendMessage(message1);

            String[] cityArray = cityList.split(",");
            if (cityArray != null && cityArray.length > 0) {
                for (String cityName : cityArray) {
                    HourlyHistory hourlyHistoryData = ThinkPageClient.sharedInstance()
                            .getHistoryWeather(cityName, AppConfig.getLanguageXinzhi());
                    HourlyFuture hourlyFutureData = ThinkPageClient.sharedInstance()
                            .getFutureWeather(cityName, AppConfig.getLanguageXinzhi());
                    AppManager.shareInstance().setWeatherHourlyData(cityName, ResponseParseManager
                            .parseHourlyData(hourlyHistoryData, hourlyFutureData));
                    Message message2 = Message.obtain();
                    message2.what = HOURLY_WEATHER_DATA;
                    Bundle bundle = new Bundle();
                    bundle.putString(BUNDLE_KEY, cityName);
                    message2.setData(bundle);
                    mHandler.sendMessage(message2);
                }
            }
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BASE_WEATHER_DATA:
                    AppManager.shareInstance().postBus(new WeatherDataLoadedEvent());
                    break;
                case HOURLY_WEATHER_DATA:
                    Bundle bundle = msg.getData();
                    String cityName = bundle.getString(BUNDLE_KEY);
                    AppManager.shareInstance().postBus(new WeatherDataLoadedEvent(cityName));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class WeatherDataLoadedEvent {
        private String mCity;

        public WeatherDataLoadedEvent() {
        }

        public WeatherDataLoadedEvent(String city) {
            mCity = city;
        }

        public String getCity() {
            return mCity;
        }

        public void setCity(String city) {
            mCity = city;
        }
    }

}
