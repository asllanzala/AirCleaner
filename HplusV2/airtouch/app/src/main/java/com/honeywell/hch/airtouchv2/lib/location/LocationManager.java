package com.honeywell.hch.airtouchv2.lib.location;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage baidu and google locate service. Set the first received value as the location info.
 */
public class LocationManager {

    public static final int HANDLER_GPS_LOCATION = 0X01000000;
    public static final String HANDLER_MESSAGE_KEY_GPS_LOCATION =
            "HANDLER_MESSAGE_KEY_GPS_LOCATION";

    // all receiver list
    private List<Handler> mGpsLocationListenerList = new ArrayList<Handler>();
    private static LocationManager mLocationManager = null;

    public static LocationManager getInstance() {
        if (mLocationManager == null) {
            mLocationManager = new LocationManager();
        }
        return mLocationManager;
    }

    private LocationManager() {

    }

    public void registerGPSLocationListener(Handler gpsReciveListener) {
        mGpsLocationListenerList.add(gpsReciveListener);
        // 初始化GPS定位组件
        LocationGoogleUtils.getInstance().beginRequestLocation();
        LocationBaiduUtils.getInstance().beginRequestLocation();
    }

    /**
     * 在定位成功后关闭GPS功能，给用户省点电！
     *
     * @param gpsReciveListener
     */
    public void unRegisterGPSLocationListener(Handler gpsReciveListener) {
        for (int i = mGpsLocationListenerList.size() - 1; i >= 0; i--) {
            if (gpsReciveListener == mGpsLocationListenerList.get(i)) {
                mGpsLocationListenerList.remove(i);
            }
        }

        if (mGpsLocationListenerList.size() == 0) {
            // 关闭GPS定位组件
            LocationGoogleUtils.getInstance().stopRequestLocation();
            LocationBaiduUtils.getInstance().stopRequestLocation();
        }
    }

    public synchronized void updateGPSLocation(CityInfo cityLocation) {

        if (cityLocation != null && !StringUtil.isEmpty(cityLocation.getCity())) {
            LogUtil.log(LogUtil.LogLevel.VERBOSE, "MZ", "updateGPSLocation");
            for (int i = mGpsLocationListenerList.size() - 1; i >= 0; i--) {
                Handler handler = mGpsLocationListenerList.get(i);

                if (handler != null) {
                    Message message = Message.obtain();
                    message.what = HANDLER_GPS_LOCATION;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(HANDLER_MESSAGE_KEY_GPS_LOCATION, cityLocation);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }

                // Remove this handler
                unRegisterGPSLocationListener(handler);
            }
        }

    }

    /**
     * 得到干净的省市区
     *
     * @param province
     * @param city
     * @param district
     * @param street_number
     * @param street
     * @param latitude
     * @param longitude
     * @return
     */
    public static CityInfo generateCityInfo(String province, String city, String district, String street_number, String street, double latitude,
                                            double longitude) {
        CityInfo cityInfo = new CityInfo();

        // 获取省份信息
        if (!StringUtil.isEmpty(province)) {
            // 移除省、市
            cityInfo.setProvince(replaceCityAndProviceText(province));
        }

        // 获取城市信息
        if (!StringUtil.isEmpty(city)) {
            // 移除省、市
            cityInfo.setCity(replaceCityAndProviceText(city));
        }

        // 获取区县信息
        if (!StringUtil.isEmpty(district)) {
            district = replaceCityAndProviceText(district);
            district = StringUtil.replace(district, "新区", "");
            district = StringUtil.replace(district, "区", "");
            cityInfo.setDistrict(district);
        }

        // 获取区县信息
        cityInfo.setStreetNumber(street_number);
        cityInfo.setStreet(street);

        cityInfo.setLatitude(latitude);
        cityInfo.setLongitude(longitude);

        return cityInfo;
    }

    /**
     * 移除省、市
     *
     * @param name
     * @return
     */
    private static String replaceCityAndProviceText(String name) {
        if (name.endsWith("省")) {
            name = name.substring(0, name.indexOf("省"));
        } else if (name.endsWith("市")) {
            name = name.substring(0, name.indexOf("市"));
        }
        return name;
    }

}
