package com.honeywell.hch.airtouchv2.framework.enrollment.models;

import com.honeywell.hch.airtouchv2.framework.enrollment.models.http.WAPIDeviceResponse;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.http.WAPIKeyResponse;

import java.io.Serializable;

public class DIYInstallationState implements Serializable {

    private static final long serialVersionUID = -2083291746396398400L;

    private static WAPIKeyResponse mWAPIKeyResponse;
    private static WAPIDeviceResponse mWAPIDeviceResponse;
    private static WAPIRouter mWAPIRouter;
    private static String mDeviceName;
    private static String mHomeName;
    private static String mCityCode;
    private static int errorCode;
    private static String mHomeConnectedSsid;
    private static Boolean isDeviceAlreadyEnrolled;

    public static String getmHomeConnectedSsid() {
        return mHomeConnectedSsid;
    }

    public static void setmHomeConnectedSsid(String mHomeConnectedSsid) {
        DIYInstallationState.mHomeConnectedSsid = mHomeConnectedSsid;
    }

    public static String getCityCode() {
        return mCityCode;
    }

    public static void setCityCode(String cityCode) {
        DIYInstallationState.mCityCode = cityCode;
    }

    public static String getDeviceName() {
        return mDeviceName;
    }

    public static void setDeviceName(String deviceName) {
        DIYInstallationState.mDeviceName = deviceName;
    }

    public static String getHomeName() {
        return mHomeName;
    }

    public static void setHomeName(String mHomeName) {
        DIYInstallationState.mHomeName = mHomeName;
    }

    public static int getErrorCode() {
        return errorCode;
    }

    public static void setErrorCode(int errorCode) {
        DIYInstallationState.errorCode = errorCode;
    }

    public static WAPIKeyResponse getWAPIKeyResponse() {
        return mWAPIKeyResponse;
    }

    public static void setWAPIKeyResponse(WAPIKeyResponse wapiKeyResponse) {
        mWAPIKeyResponse = wapiKeyResponse;
    }

    public static WAPIDeviceResponse getWAPIDeviceResponse() {
        return mWAPIDeviceResponse;
    }

    public static void setWAPIDeviceResponse(WAPIDeviceResponse wapiDeviceResponse) {
        mWAPIDeviceResponse = wapiDeviceResponse;
    }

    public static WAPIRouter getWAPIRouter() {
        return mWAPIRouter;
    }

    public static void setWAPIRouter(WAPIRouter wapiRouter) {
        mWAPIRouter = wapiRouter;
    }

    public static Boolean getIsDeviceAlreadyEnrolled() {
        return isDeviceAlreadyEnrolled;
    }

    public static void setIsDeviceAlreadyEnrolled(Boolean isDeviceAlreadyEnrolled) {
        DIYInstallationState.isDeviceAlreadyEnrolled = isDeviceAlreadyEnrolled;
    }
}