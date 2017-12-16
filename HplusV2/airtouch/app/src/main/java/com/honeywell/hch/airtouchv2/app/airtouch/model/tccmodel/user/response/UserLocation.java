package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv2.framework.model.DeviceInfo;
import com.honeywell.hch.airtouchv2.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IRefreshEnd;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jin Qian on 1/19/2015.
 * GET api/locations?userId={userId}
 *
 * 2015-7-31 changed by Stephen(H127856)
 * data model reconstruction
 */
public class UserLocation implements IRequestParams, Serializable {
//    private HomeDevice mWorstDevice = new HomeDevice();
     private ArrayList<HomeDevice> mHomeDevices = new ArrayList<>();
//
//    // GET api/locations/{locationId}/AirCleaner/PM25
//    private ArrayList<HomeDevicePM25> mHomeDevicesPM25 = new ArrayList<>();

    @SerializedName("devices")
    private ArrayList<DeviceInfo> mDeviceInfo;

    @SerializedName("locationID")
    private int mLocationID;

    @SerializedName("name")
    private String mName;

    // city code (i.e. CHSH00000)
    @SerializedName("city")
    private String mCity;

    @SerializedName("isLocationOwner")
    private Boolean mIsLocationOwner;

    @SerializedName("locationOwnerName")
    private Boolean mLocationOwnerName;


    public int getLocationID() {
        return mLocationID;
    }

    public void setLocationID(int locationID) {
        mLocationID = locationID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Boolean getIsLocationOwner() {
        return mIsLocationOwner;
    }

    public void setIsLocationOwner(Boolean isLocationOwner) {
        mIsLocationOwner = isLocationOwner;
    }

    public Boolean getLocationOwnerName() {
        return mLocationOwnerName;
    }

    public void setLocationOwnerName(Boolean locationOwnerName) {
        mLocationOwnerName = locationOwnerName;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public ArrayList<DeviceInfo> getDeviceInfo() {
        return mDeviceInfo;
    }

    public void setDeviceInfo(ArrayList<DeviceInfo> deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

//    public ArrayList<HomeDevicePM25> getHomeDevicesPM25() {
//        return mHomeDevicesPM25;
//    }
//
//    public void setHomeDevicesPM25(ArrayList<HomeDevicePM25> homeDevices) {
//        mHomeDevicesPM25 = homeDevices;
//    }
//
//    public HomeDevice getWorstDevice() {
//        return mWorstDevice;
//    }
//
//    public void setWorstDevice(HomeDevice worstDevice) {
//        mWorstDevice = worstDevice;
//    }

    public ArrayList<HomeDevice> getHomeDevices() {
        return mHomeDevices;
    }

    public void setHomeDevices(ArrayList<HomeDevice> homeDevices) {
        mHomeDevices = homeDevices;
    }

    @Override
    public String getRequest(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public String getPrintableRequest(Gson gson) {
        return getRequest(gson);
    }

    public void addHomeDeviceToList(HomeDevice homeDevice){
        mHomeDevices.add(homeDevice);
    }

    /**
     * get the device data,if it's Air Touch, get the run status
     * by Stephen(H127856)
     */
    public void loadHomeDevicesData(IRefreshEnd iRefreshEnd){
        if (mDeviceInfo != null) {
            for (DeviceInfo deviceInfo : mDeviceInfo) {
                if (deviceInfo.getmDeviceType() == AirTouchConstants.AIRTOUCHS_TYPE) {
                    com.honeywell.hch.airtouchv2.framework.model.HomeDevice homeDevice = new AirTouchSeriesDevice();
                    homeDevice.setDeviceInfo(deviceInfo);
                    homeDevice.loadData(iRefreshEnd);

                    mHomeDevices.add(homeDevice);
                }
            }
        }
    }

    /**
     * get worse device from device list
     * @return
     */
   public AirTouchSeriesDevice getWorstDevice() {
       int worstPM25 = -1;
       AirTouchSeriesDevice worstDevice = null;
       for (HomeDevice homeDevice : mHomeDevices) {
           if (homeDevice.getDeviceInfo().getmDeviceType() == AirTouchConstants.AIRTOUCHS_TYPE &&
                   ((AirTouchSeriesDevice)homeDevice).getDeviceRunStatus() != null) {
               AirTouchSeriesDevice airTouchSeriesDevice = ((AirTouchSeriesDevice)homeDevice);
               int pmValue = airTouchSeriesDevice.getDeviceRunStatus().getmPM25Value();
               if (worstPM25 < pmValue) {
                   worstPM25 = pmValue;
                   worstDevice = airTouchSeriesDevice;
               }
           }

       }
      return worstDevice;
   }

    /**
     * get the number of AirTouch series device
     * @return number of device
     */
   public int getAirTouchSDeviceNumber(){
       int number = 0;
       for (DeviceInfo deviceInfo : mDeviceInfo) {
           if (deviceInfo.getmDeviceType() == AirTouchConstants.AIRTOUCHS_TYPE) {
               number++;
           }
       }
       return number;
   }


    /**
     * get the Airtouch series device list
     * @return
     */
    public ArrayList<AirTouchSeriesDevice> getAirTouchSeriesList() {
        ArrayList<AirTouchSeriesDevice> airTouchSDevicesList = new ArrayList<>();
        for (HomeDevice homeDevice : mHomeDevices) {
            if (homeDevice.getDeviceInfo().getmDeviceType() == AirTouchConstants.AIRTOUCHS_TYPE) {
                airTouchSDevicesList.add((AirTouchSeriesDevice)homeDevice);
            }
        }
        return airTouchSDevicesList;
    }

    public HomeDevice getHomeDeviceWithDeviceId(int deviceID){
        for (HomeDevice homeDevice : mHomeDevices) {
            if (homeDevice.getDeviceInfo().getDeviceID() == deviceID) {
                return homeDevice;
            }
        }
        return null;
    }


    /**
     * get the child class of base class: HomeDevice according deviceId
     * @param deviceId
     * @return
     */
    public HomeDevice getHomeDeviceWithId(int deviceId){
        for (HomeDevice homeDevice : mHomeDevices){
            if (homeDevice.getDeviceInfo().getDeviceID() == deviceId){
                if (homeDevice.getDeviceInfo().getmDeviceType() == AirTouchConstants.AIRTOUCHS_TYPE){
                    return (AirTouchSDevice)homeDevice;
                }
            }
        }
        return null;
    }

    /**
     * get the child class of base class: HomeDevice according list index
     * @param index
     * @return
     */
    public HomeDevice getHomeDeviceWithIndex(int index){
        HomeDevice homeDevice = mHomeDevices.get(index);
        if (homeDevice.getDeviceInfo().getmDeviceType() == AirTouchConstants.AIRTOUCHS_TYPE){
            return (AirTouchSDevice)homeDevice;
        }
       return homeDevice;
    }
}


