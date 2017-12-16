package com.honeywell.hch.airtouchv2.lib.location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.honeywell.hch.airtouchv2.ATApplication;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

//假如用到位置提醒功能，需要import该类

public class LocationBaiduUtils {

    public static final String LOG_TAG = "LocationBaiduUtils";

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private static LocationBaiduUtils mLocationBaiduUtils = null;

    public static LocationBaiduUtils getInstance() {
        if (mLocationBaiduUtils == null) {
            mLocationBaiduUtils = new LocationBaiduUtils();
        }
        return mLocationBaiduUtils;
    }

    private LocationBaiduUtils() {
        // empty
    }

    /**
     * 发起定位请求, 请求过程是异步的，定位结果在上面的监听函数onReceiveLocation中获取。
     */
    public void beginRequestLocation() {
        mLocationClient = new LocationClient(ATApplication.getInstance()); // 声明LocationClient类
        setLocOption(mLocationClient);
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener); // 注册监听函数

        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    public void stopRequestLocation() {
        if (mLocationClient != null) {
            LogUtil.log(LogUtil.LogLevel.DEBUG, LOG_TAG, "locClient stopLocaitonClient");
            mLocationClient.unRegisterLocationListener(mMyLocationListener);
            mLocationClient.stop();
            mLocationClient = null;
        }
    }

    /**
     * 设置定位参数包括：定位模式（单次定位，定时定位），返回坐标类型，是否打开GPS等等。
     *
     * @param locationClient
     */
    private void setLocOption(LocationClient locationClient) {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setAddrType("all"); // 返回的定位结果包含地址信息
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
        option.setLocationMode(LocationMode.Hight_Accuracy);
//		option.disableCache(true); // 禁止启用缓存定位
//		option.setPoiNumber(5); // 最多返回POI个数
//		option.setPoiDistance(1000); // poi查询距离
//		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
//		option.setPriority(LocationClientOption.GpsFirst); // 不设置，默认是gps优先
        locationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        /**
         * 发起定位请求, 请求过程是异步的，定位结果在上面的监听函数onReceiveLocation中获取。
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            CityInfo cityInfo = getCityInfo(location);
            onLocationChanged(cityInfo);
        }

        /**
         * 发起POI查询请求。请求过程是异步的，定位结果在上面的监听函数onReceivePoi中获取。
         */
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
            CityInfo cityInfo = getCityInfo(poiLocation);
            onLocationChanged(cityInfo);
        }

    }

    private CityInfo getCityInfo(BDLocation poiLocation) {
        // 获取省份信息
        String provName = poiLocation.getProvince();
        // 获取城市信息
        String cityName = poiLocation.getCity();
        // 获取区县信息
        String district = poiLocation.getDistrict();
        String street_number = poiLocation.getStreetNumber();
        String street = poiLocation.getStreet();
        double latitude = poiLocation.getLatitude();
        double longitude = poiLocation.getLongitude();

        if (StringUtil.isEmpty(cityName)) {
            LogUtil.log(LogUtil.LogLevel.VERBOSE, "MZ", "Baidu没有定位到了城市");
            return null;
        } else {
            CityInfo cityInfo = LocationManager.generateCityInfo(provName, cityName, district,
                    street_number, street, latitude, longitude);
            LogUtil.log(LogUtil.LogLevel.VERBOSE, "MZ", "Baidu定位到了城市：" + cityInfo.toString());
            return cityInfo;
        }

    }

    public void onLocationChanged(CityInfo cityLocation) {
        if (cityLocation != null) {
            // 应用每次启动之定位一次，不需要实时的去定位浪费用户流量
            LocationManager.getInstance().updateGPSLocation(cityLocation);
        }
    }

}
