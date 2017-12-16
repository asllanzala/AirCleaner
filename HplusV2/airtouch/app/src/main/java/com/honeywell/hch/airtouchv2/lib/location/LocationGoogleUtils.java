package com.honeywell.hch.airtouchv2.lib.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.honeywell.hch.airtouchv2.ATApplication;
import com.honeywell.hch.airtouchv2.lib.location.GetAddressFromGoogle.GetAddressDataListener;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.NetWorkUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

public class LocationGoogleUtils implements LocationListener {

    private android.location.LocationManager mLocationManager;
    protected Location mPreLocation;

    private BroadcastReceiver conBroadcastReceiver = null;

    private static LocationGoogleUtils mLocationGoogleUtils = null;

    public static LocationGoogleUtils getInstance() {
        if (mLocationGoogleUtils == null) {
            mLocationGoogleUtils = new LocationGoogleUtils();
        }
        return mLocationGoogleUtils;
    }

    private LocationGoogleUtils() {
        // empty
    }

    /**
     * 网络状态的广播接收
     *
     * @author admin
     */
    public class ConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) ATApplication.getInstance()
                    .getSystemService(ATApplication.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mobNetInfo != null && wifiNetInfo != null) {
                if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                    // unconnect network

                } else {
                    // connect network
                    beginRequestLocation();
                }
            }
        }
    }

    ;

    public void stopRequestLocation() {
        if (conBroadcastReceiver != null) {
            ATApplication.getInstance().unregisterReceiver(conBroadcastReceiver);
            mLocationManager.removeUpdates(this);
            conBroadcastReceiver = null;
        }
    }

    /**
     * 获得当前的Location对象
     */
    public void beginRequestLocation() {
        if (conBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            conBroadcastReceiver = new ConnectionReceiver();
            ATApplication.getInstance().registerReceiver(conBroadcastReceiver, intentFilter);
        }

        // 获取位置管理服务
        String serviceName = Context.LOCATION_SERVICE;
        mLocationManager = (android.location.LocationManager) ATApplication.getInstance().getSystemService(serviceName);
        Location location = mLocationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
        if (location != null) {
            onLocationChanged(location);
        } else {
            location = mLocationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
            onLocationChanged(location);
        }
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW); // 低精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider = null;
        int i = 0;
        while (provider == null) {
            provider = mLocationManager.getBestProvider(criteria, true); // 获取GPS信息
            i++;
            if (i > 30) {
                break;
            }
        }
        try {
            // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
            mLocationManager.requestLocationUpdates(provider, 1 * 1000, 500, this);
        } catch (Exception e) {
            LogUtil.log(LogUtil.LogLevel.ERROR, "location error ==== ", "location error!!");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // 应用每次启动之定位一次，不需要实时的去定位浪费用户流量
            mPreLocation = location;
            getCityNameByAddress(location);
        }
    }

    /**
     * 根据Address 获得省份名称
     *
     * @return
     */
    public void getCityNameByAddress(Location location) {
        LogUtil.log(LogUtil.LogLevel.VERBOSE, "MZ", "getCityNameByAddress");
        if (NetWorkUtil.isNetworkAvailable(ATApplication.getInstance())) {
            GetAddressFromGoogle addressTask = new GetAddressFromGoogle(new GetAddressDataListener() {
                @Override
                public void onGetAddressDataComplete(CityInfo cityLocation) {
                    if (cityLocation != null) {
                        LocationManager.getInstance().updateGPSLocation(getCityInfo(cityLocation));
                    }
                }
            }, location);
        }
    }

    public CityInfo getCityInfo(CityInfo cityLocation) {
        if (cityLocation != null) {
            // 获取省份信息
            String provName = cityLocation.getProvince();
            // 获取城市信息
            String cityName = cityLocation.getCity();
            // 获取区县信息
            String district = cityLocation.getDistrict();
            String street_number = cityLocation.getStreetNumber();
            String street = cityLocation.getStreet();
            double latitude = cityLocation.getLatitude();
            double longitude = cityLocation.getLongitude();

            if (StringUtil.isEmpty(cityName)) {
                LogUtil.log(LogUtil.LogLevel.VERBOSE, "MZ", "Google没有定位到了城市");
                return null;
            } else {
                CityInfo cityInfo = LocationManager.generateCityInfo(provName, cityName,
                        district, street_number, street, latitude, longitude);
                LogUtil.log(LogUtil.LogLevel.VERBOSE, "MZ", "Google定位到了城市：" + cityInfo.toString());
                return cityInfo;
            }
        } else {
            return null;
        }
    }

}