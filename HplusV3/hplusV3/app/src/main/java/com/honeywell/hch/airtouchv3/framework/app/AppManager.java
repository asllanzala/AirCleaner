package com.honeywell.hch.airtouchv3.framework.app;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.framework.model.DeviceInfo;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Hour;
import com.honeywell.hch.airtouchv3.framework.webservice.task.DownloadBackgroundTask;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by allanhwmac on 7/27/15.
 * <p/>
 * UI和 后台数据的唯一接口
 */
public class AppManager {

    public static final int maxHomeCount = 5;
    private List<UserLocationData> mUserLocationDataList = new CopyOnWriteArrayList<>();
    private HashMap<String, WeatherPageData> mWeatherPageDataHashMap = new HashMap<>();
    private AuthorizeApp mAuthorizeApp;
    private static AppManager mAppManagerInstance;
    private Bus mBus;

    private int mCurrentDeviceId;

    private UserLocationData mGpsUserLocation = new UserLocationData();

    private DownloadBackgroundTask mDownloadBackgroundTask = null;
    private boolean isNeedStartDownTask = true;

    public static AppManager shareInstance() {
        if (mAppManagerInstance == null) {
            mAppManagerInstance = new AppManager();
        }
        return mAppManagerInstance;
    }

    public AppManager() {
        mBus = new Bus();
    }

    public void postBus(Object event) {
        mBus.post(event);
    }

    public void registerBus(Object listener) {
        mBus.register(listener);
    }

    public void unregisterBus(Object listener) {
        mBus.unregister(listener);
    }

    /**
     * set the user login name and password.this method is only called in  when user
     * input user name and password for login.
     *
     * @param mobilePhone
     * @param userPassword
     */
    public void setLoginInfo(String mobilePhone, String userPassword) {
        mAuthorizeApp.setMobilePhone(mobilePhone);
        mAuthorizeApp.setPassword(userPassword);
    }

    /**
     * use login
     */
    public void userRelogin() {
        mAuthorizeApp.currentUserLogin();
    }

    public void clearUserLocationListWhenLogin() {
        mUserLocationDataList.clear();
    }


    /**
     * @param tempList
     */
    public void updateUsrdataList(List<UserLocationData> tempList) {
        //check the location will delete
        List<UserLocationData> deleteList = new ArrayList<>();
        for (UserLocationData userLocationData : mUserLocationDataList) {
            int locationId = userLocationData.getLocationID();
            boolean isHasSame = false;
            for (UserLocationData tempItem : tempList) {
                if (tempItem.getLocationID() == locationId) {
                    updateUserLocationDataItem(userLocationData, tempItem);
                    isHasSame = true;
                    break;
                }
            }
            if (!isHasSame) {
                deleteList.add(userLocationData);
            }
        }

        if (deleteList.size() > 0){
            mUserLocationDataList.removeAll(deleteList);
        }

        //add new
        List<UserLocationData> addList = new ArrayList<>();
        for (UserLocationData tempItem : tempList) {
            int locationId = tempItem.getLocationID();
            tempItem.setCityWeatherData(mWeatherPageDataHashMap.get(tempItem.getCity()));
            boolean isHasSame = false;
            for (UserLocationData userLocationData : mUserLocationDataList) {
                if (userLocationData.getLocationID() == locationId) {
                    isHasSame = true;
                    break;
                }
            }
            if (!isHasSame) {
                addList.add(tempItem);
            }
        }

        if (addList.size() > 0) {
            int needAddHome = Math.min(maxHomeCount - mUserLocationDataList.size(), addList.size());
            for (int i = 0; i < needAddHome; i++) {
                mUserLocationDataList.add(addList.get(i));
            }
            isNeedStartDownTask = true;
        }
        if (isNeedStartDownTask){
            startDownBackgroundTask(true);
        }
    }

    public void startDownBackgroundTask(boolean isLogin){
        if (mDownloadBackgroundTask == null || !mDownloadBackgroundTask.isRunning()){
            mDownloadBackgroundTask = new DownloadBackgroundTask();
            AsyncTaskExecutorUtil.executeAsyncTask(mDownloadBackgroundTask);
            if (isLogin){
                isNeedStartDownTask = false;
            }
        }
    }

    private void updateUserLocationDataItem(UserLocationData srcData, UserLocationData destData) {
        srcData.setName(destData.getName());
        srcData.clearHomeDeviceList();
        srcData.setHomeDevicesList(destData.getHomeDevicesList());
    }

    /**
     * @param userLocation
     */
    public void addLocationDataFromGetLocationAPI(UserLocation userLocation, List<UserLocationData> tempList) {
        UserLocationData userLocationDataItem = new UserLocationData();
        if (userLocation == null || userLocation.getDeviceInfo() == null) {
            return;
        }
        for (DeviceInfo deviceInfoItem : userLocation.getDeviceInfo()) {
            if (AppManager.shareInstance().isAirtouchSeries(deviceInfoItem.getmDeviceType())) {
                HomeDevice homeDevice = new AirTouchSeriesDevice();
                homeDevice.setDeviceInfo(deviceInfoItem);
                userLocationDataItem.addHomeDeviceItemToList(homeDevice);
            }
        }

        userLocationDataItem.setName(userLocation.getName());
        userLocationDataItem.setCity(userLocation.getCity());
        userLocationDataItem.setLocationID(userLocation.getLocationID());
        userLocationDataItem.setCityWeatherData(mWeatherPageDataHashMap.get(userLocation.getCity()));
        tempList.add(userLocationDataItem);
    }

    public List<UserLocationData> getUserLocationDataList() {
        return mUserLocationDataList;
    }

    public void setUserLocationDataList(ArrayList<UserLocationData> userLocationDataList) {
        mUserLocationDataList.clear();
        this.mUserLocationDataList = userLocationDataList;
    }

    public UserLocationData getUserLocationByID(int locationID) {
        UserLocationData userLocation = null;
        if (locationID == 0) {
            userLocation = mGpsUserLocation;
        }
        if (mUserLocationDataList != null) {
            for (int i = 0; i < mUserLocationDataList.size(); i++) {
                if (mUserLocationDataList.get(i).getLocationID() == locationID) {
                    userLocation = mUserLocationDataList.get(i);
                    break;
                }
            }
        }
        return userLocation;
    }

    public HashMap<String, WeatherPageData> getWeatherPageDataHashMap() {
        return mWeatherPageDataHashMap;
    }

    public void setWeatherPageDataHashMap(HashMap<String, WeatherPageData> weatherPageDataHashMap) {
        Iterator iterator = weatherPageDataHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            WeatherPageData weatherPageData = weatherPageDataHashMap.get(entry.getKey());
            if (mWeatherPageDataHashMap.get(entry.getKey()) != null) {
                mWeatherPageDataHashMap.get(entry.getKey()).setWeather(weatherPageData.getWeather());
            } else {
                mWeatherPageDataHashMap.put((String) entry.getKey(), weatherPageData);
            }
        }
        
        if (mUserLocationDataList != null) {
            for (UserLocationData userLocationData : mUserLocationDataList) {
                userLocationData.setCityWeatherData(mWeatherPageDataHashMap.get(userLocationData.getCity()));
            }
        }
        if (mGpsUserLocation != null) {
            mGpsUserLocation.setCityWeatherData(mWeatherPageDataHashMap.get(mGpsUserLocation.getCity()));
        }
    }

    public void setWeatherHourlyData(String city, Hour[] hours) {
        WeatherPageData weatherPageData = mWeatherPageDataHashMap.get(city);
        if (weatherPageData != null) {
            weatherPageData.setHourlyData(hours);
        }
    }


    /**
     * get locaiton object using locaiton id
     *
     * @param locationId
     * @return
     */
    public UserLocationData getLocationWithId(int locationId) {
        for (UserLocationData locationItem : mUserLocationDataList) {
            if (locationItem.getLocationID() == locationId) {
                return locationItem;
            }
        }
        LogUtil.log(LogUtil.LogLevel.ERROR, "AuthorizeApp", "getLocationWithId location id = " + locationId + " ,is return null");
        return null;
    }



    public AuthorizeApp getAuthorizeApp() {
        if (mAuthorizeApp == null) {
            mAuthorizeApp = new AuthorizeApp(HPlusApplication.getInstance().getApplicationContext());
        }
        return mAuthorizeApp;
    }

    public void setAuthorizeApp(AuthorizeApp mAuthorizeApp) {
        this.mAuthorizeApp = mAuthorizeApp;
    }

    public int getCurrentDeviceId() {
        return mCurrentDeviceId;
    }

    public void setCurrentDeviceId(int mCurrentDeviceId) {
        this.mCurrentDeviceId = mCurrentDeviceId;
    }

    public UserLocationData getGpsUserLocation() {
        return mGpsUserLocation;
    }

    public void setGpsUserLocation(UserLocationData mGpsUserLocation) {
        this.mGpsUserLocation = mGpsUserLocation;
    }

    public ArrayList<Integer> getNeedOpenDevice(){
        ArrayList<Integer> deviceIdList = new ArrayList<>();
        if (mUserLocationDataList != null && mUserLocationDataList.size() > 0){
            for (UserLocationData userLocationData : mUserLocationDataList){
                if (userLocationData != null){
                    deviceIdList.addAll(userLocationData.getOffDeviceIdList());
                }
            }
        }
        return deviceIdList;
    }

    public HomeDevice getDeviceWithDeviceId(int deviceId){
        if (mUserLocationDataList != null && mUserLocationDataList.size() > 0){
            for (UserLocationData userLocationData : mUserLocationDataList){
                if (userLocationData != null) {
                    for (HomeDevice device : userLocationData.getHomeDevicesList()) {
                        if (deviceId == device.getDeviceInfo().getDeviceID())
                            return userLocationData.getHomeDeviceWithId(deviceId);
                    }
                }
            }
        }
        return null;
    }

    public boolean isAirtouchs(int type){
        return type == AirTouchConstants.AIRTOUCHJD_TYPE ||
                type == AirTouchConstants.AIRTOUCHS_TYPE;
    }

    public boolean isAirtouchP(int type){
        return type == AirTouchConstants.AIRTOUCHP_TYPE;
    }

    public boolean isAirtouch450(int type){
        return type == AirTouchConstants.AIRTOUCH450_TYPE;
    }

    public boolean isAirtouchSeries(int type){
        return type == AirTouchConstants.AIRTOUCH450_TYPE ||
                type == AirTouchConstants.AIRTOUCHP_TYPE ||
                type == AirTouchConstants.AIRTOUCHJD_TYPE ||
                type == AirTouchConstants.AIRTOUCHS_TYPE;
    }
}
