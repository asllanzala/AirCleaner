//package com.honeywell.hch.airtouch.app.airtouch.model.tccmodel.user.response;
//
//import com.google.gson.Gson;
//import com.honeywell.hch.airtouch.framework.model.DeviceInfo;
//import com.honeywell.hch.airtouch.lib.http.IRequestParams;
//
//import java.io.Serializable;
//
///**
// * Created by Jin Qian on 1/19/2015.
// */
//public class HomeDevice implements IRequestParams, Serializable {
//    private HomeDevicePM25 mHomeDevicePm25;
//    private DeviceInfo mDeviceInfo;
//
//    public HomeDevicePM25 getHomeDevicePm25() {
//        return mHomeDevicePm25;
//    }
//
//    public void setHomeDevicePm25(HomeDevicePM25 homeDevicePm25) {
//        mHomeDevicePm25 = homeDevicePm25;
//    }
//
//    public DeviceInfo getDeviceInfo() {
//        return mDeviceInfo;
//    }
//
//    public void setDeviceInfo(DeviceInfo deviceInfo) {
//        mDeviceInfo = deviceInfo;
//    }
//
//    @Override
//    public String getRequest(Gson gson) {
//        return gson.toJson(this);
//    }
//
//    @Override
//    public String getPrintableRequest(Gson gson) {
//        return getRequest(gson);
//    }
//
//
//}
//
//
