package com.honeywell.hch.airtouchv3.framework.model;

import android.content.Context;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupData;
import com.honeywell.hch.airtouchv3.app.dashboard.view.AirTouchView;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.database.DefaultDeviceDBService;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyuan on 10/10/15.
 * by Stephen(H127856)
 */
public class UserLocationData {
    private final String TAG = "UserLocationData";
    private int mLocationID;

    private String mName = "";

    // city code (i.e. CHSH00000)
    private String mCity;

    private ArrayList<HomeDevice> mHomeDevicesList = new ArrayList<>();

    private BackgroundData mCityBackgroundDta;

    private WeatherPageData mCityWeatherData;

    private GroupData mGroupData;
    /**
     * include yesterday,this week, so far
     */
    private List<EmotionalData> mEmotionalData;

    /**
     * use get weather data
     */
    private String mNameEn;


    private static Context mContext = HPlusApplication.getInstance().getApplicationContext();
    private DefaultDeviceDBService mDefaultDB = new DefaultDeviceDBService(mContext);

    public UserLocationData(){
        mCityBackgroundDta = new BackgroundData();
    }



    /**
     * get the Airtouch series device list
     *
     * @return
     */
    public ArrayList<AirTouchSeriesDevice> getAirTouchSeriesList() {
        ArrayList<AirTouchSeriesDevice> airTouchSDevicesList = new ArrayList<>();
        for (HomeDevice homeDevice : mHomeDevicesList) {
            if (AppManager.shareInstance().isAirtouchSeries(homeDevice.getDeviceInfo().getmDeviceType())) {
                airTouchSDevicesList.add((AirTouchSeriesDevice) homeDevice);
            }
        }
        return airTouchSDevicesList;
    }

    /**
     * get device with device id
     *
     * @param deviceID
     * @return
     */
    public HomeDevice getHomeDeviceWithDeviceId(int deviceID) {
        for (HomeDevice homeDevice : mHomeDevicesList) {
            if (homeDevice.getDeviceInfo().getDeviceID() == deviceID) {
                return homeDevice;
            }
        }
        return null;
    }


    /**
     * get the child class of base class: HomeDevice according deviceId
     *
     * @param deviceId
     * @return
     */
    public HomeDevice getHomeDeviceWithId(int deviceId) {
        for (HomeDevice homeDevice : mHomeDevicesList) {
            if (homeDevice.getDeviceInfo().getDeviceID() == deviceId) {
                if (homeDevice instanceof AirTouchSeriesDevice) {
                    return (AirTouchSeriesDevice) homeDevice;
                }
            }
        }
        return null;
    }


    /**
     * if the location contain the airtouchs device,return true,else return false;
     *
     * @return
     */
    public boolean isHaveDeviceInThisLocation() {
        if (mHomeDevicesList != null && mHomeDevicesList.size() > 0) {
            for (HomeDevice homeDevice : mHomeDevicesList) {
                if (homeDevice instanceof AirTouchSeriesDevice) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getLocationID() {
        return mLocationID;
    }

    public void setLocationID(int mLocationID) {
        this.mLocationID = mLocationID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public ArrayList<HomeDevice> getHomeDevicesList() {
        return mHomeDevicesList;
    }

    public void setHomeDevicesList(ArrayList<HomeDevice> homeDevicesList) {
        for (HomeDevice homeDevice : homeDevicesList) {
            this.mHomeDevicesList.add(homeDevice);
        }
    }

    public void addHomeDeviceItemToList(HomeDevice mHomeDevicesItem) {
        this.mHomeDevicesList.add(mHomeDevicesItem);
    }

    public void clearHomeDeviceList() {
        this.mHomeDevicesList.clear();
    }


    public BackgroundData getCityBackgroundDta() {
        return mCityBackgroundDta;
    }

    public void setCityBackgroundDta(BackgroundData mCityBackgroundDta) {
        this.mCityBackgroundDta = mCityBackgroundDta;
    }

    public WeatherPageData getCityWeatherData() {
        return mCityWeatherData;
    }

    public void setCityWeatherData(WeatherPageData mCityWeatherData) {
        this.mCityWeatherData = mCityWeatherData;
    }

    public List<EmotionalData> getEmotionalData() {
        return mEmotionalData;
    }

    public void setEmotionalData(List<EmotionalData> mEmotionalData) {
        this.mEmotionalData = mEmotionalData;
    }

    public void resetEmotionalDataList() {
        if (mEmotionalData != null) {
            mEmotionalData.clear();
            mEmotionalData = null;
        }
    }

    public void addEmotionalData(EmotionalData emotionalDataItem) {
        if (mEmotionalData == null) {
            mEmotionalData = new ArrayList<>();
        }
        mEmotionalData.add(emotionalDataItem);
    }

    public GroupData getGroupData() {
        return mGroupData;
    }

    public void setGroupData(GroupData groupData) {
        mGroupData = groupData;
    }

    public AirTouchSeriesDevice getDefaultDevice() {
        if (mHomeDevicesList != null && mHomeDevicesList.size() > 0) {
            int mDeviceID = mDefaultDB.findDefaultByLocationID(mLocationID);
            if (mDeviceID == 0) {
//                mDefaultDB.insertDefaultDevice(mLocationID, mHomeDevicesList.get(0).getDeviceInfo().getDeviceID());
//                return (AirTouchSeriesDevice) mHomeDevicesList.get(0);
                return getWorstDevice();
            } else {
                for (int i = 0; i < mHomeDevicesList.size(); i++) {
                    if (mHomeDevicesList.get(i).getDeviceInfo().getDeviceID() == mDeviceID) {
                        return (AirTouchSeriesDevice) mHomeDevicesList.get(i);
                    }
                }
//                mDefaultDB.insertDefaultDevice(mLocationID, mHomeDevicesList.get(0).getDeviceInfo().getDeviceID());
//                return (AirTouchSeriesDevice) mHomeDevicesList.get(0);
            }
        }
        return getWorstDevice();
    }

    public boolean isOutDoorPmWorse() {
        if (mCityWeatherData != null && mCityWeatherData.getWeather() != null && mCityWeatherData.getWeather().getNow() != null) {
            return Integer.valueOf(mCityWeatherData.getWeather().getNow().getAirQuality()
                    .getAirQualityIndex().getPm25()) > AirTouchConstants.OUTDOOR_PM25_MAX;
        }
        return false;
    }

    public List<Integer> getOffDeviceIdList() {
        // out door pm25 value is not considered from time being.
        boolean isOutdoorOpen = isOutDoorPmWorse();
//        boolean isOutdoorOpen = false;

        List<Integer> deviceIdList = new ArrayList<>();
        if (mHomeDevicesList != null && mHomeDevicesList.size() > 0) {
            for (HomeDevice homeDevice : mHomeDevicesList) {
                if (homeDevice instanceof AirTouchSeriesDevice) {

                    boolean isIndoorOpen = (homeDevice != null &&
                            ((AirTouchSeriesDevice) homeDevice).getDeviceRunStatus() != null
                            && ((AirTouchSeriesDevice) homeDevice).getDeviceRunStatus().getmPM25Value() > AirTouchConstants.MAX_PMVALUE_LOW)
                            && (((AirTouchSeriesDevice) homeDevice).getDeviceRunStatus().getmPM25Value() < 9999);

                    boolean isAlive = false;
                    if (homeDevice.getDeviceInfo() != null) {
                        isAlive = homeDevice.getDeviceInfo().getIsAlive();
                    }

                    if (((AirTouchSeriesDevice) homeDevice).getDeviceRunStatus() != null && ((AirTouchSeriesDevice) homeDevice).getDeviceRunStatus().getScenarioMode().equals
                            ("Off") && isAlive) {

                        if (isOutdoorOpen || isIndoorOpen) {
                            deviceIdList.add(homeDevice.getDeviceInfo().getDeviceID());
                        }
                    } else if (((AirTouchSeriesDevice) homeDevice).getDeviceRunStatus() == null && isAlive) {
                        if (isOutdoorOpen || isIndoorOpen) {
                            deviceIdList.add(homeDevice.getDeviceInfo().getDeviceID());
                        }
                    }
                }

            }
        }
        return deviceIdList;
    }
    /**
     //     * get worse device from device list
     //     * @return
     //     */
    public AirTouchSeriesDevice getWorstDevice() {
        int worstPM25 = -1;
        AirTouchSeriesDevice worstDevice = null;
        boolean isAllDeviceStatusNull = true;
        for (HomeDevice homeDevice : mHomeDevicesList) {
            if (AppManager.shareInstance().isAirtouchSeries(homeDevice.getDeviceInfo().getmDeviceType())) {
                AirTouchSeriesDevice airTouchSeriesDevice = ((AirTouchSeriesDevice)homeDevice);
                if (airTouchSeriesDevice.getDeviceRunStatus() != null){
                    int pmValue = airTouchSeriesDevice.getDeviceRunStatus().getmPM25Value();
                    if (worstPM25 < pmValue) {
                        worstPM25 = pmValue;
                        worstDevice = airTouchSeriesDevice;
                        isAllDeviceStatusNull = false;
                    }
                }
                else{
                    airTouchSeriesDevice.setDeviceRunStatus(getRunStatusWhenReturnNull());
                }
            }
        }
        if (isAllDeviceStatusNull && mHomeDevicesList.size() > 0 && AppManager.shareInstance().isAirtouchSeries(((HomeDevice) mHomeDevicesList.get(0)).getDeviceInfo().getmDeviceType())){
            worstDevice = (AirTouchSeriesDevice) mHomeDevicesList.get(0);
        }
        return worstDevice;
    }

    /**
     * set the default runstatus when server return null
     */
    public RunStatus getRunStatusWhenReturnNull(){
        RunStatus runStatus = new RunStatus();
        runStatus.setmPM25Value(AirTouchView.ERROR_MAX_VALUE);
        runStatus.setIsAlive(false);
        runStatus.setScenarioMode("Off");
        runStatus.setTvocValue(AirTouchView.ERROR_MAX_VALUE);
        runStatus.setFilter1Runtime(AirTouchConstants.ERROR_FILTER_RUNTIME);
        runStatus.setFilter2Runtime(AirTouchConstants.ERROR_FILTER_RUNTIME);
        runStatus.setFilter3Runtime(AirTouchConstants.ERROR_FILTER_RUNTIME);
        return runStatus;
    }
}
