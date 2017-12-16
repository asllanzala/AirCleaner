package com.honeywell.hch.airtouchv3.framework.model.xinzhi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 2/2/2015.
 */
public class AirQualityIndex implements IRequestParams, Serializable {
    @SerializedName("aqi")
    private String mAqi;

    @SerializedName("pm25")
    private String mPm25;

    @SerializedName("pm10")
    private String mPm10;

    @SerializedName("so2")
    private String mSo2;

    @SerializedName("no2")
    private String mNo2;

    @SerializedName("co")
    private String mCo;

    @SerializedName("o3")
    private String mO3;

    @SerializedName("quality")
    private String mQuality;

    @SerializedName("last_update")
    private String mLastUpdate;

    private static final String KEY_AQI = "aqi";
    private static final String KEY_PM25 = "pm25";
    private static final String KEY_PM10 = "pm10";
    private static final String KEY_SO2 = "so2";
    private static final String KEY_NO2 = "no2";
    private static final String KEY_CO = "co";
    private static final String KEY_O3 = "o3";

    public String getAqi() {
        return mAqi;
    }

    public void setAqi(String aqi) {
        mAqi = aqi;
    }

    public String getPm25() {
        return mPm25;
    }

    public void setPm25(String pm25) {
        mPm25 = pm25;
    }

    public String getPm10() {
        return mPm10;
    }

    public void setPm10(String pm10) {
        mPm10 = pm10;
    }

    public String getSo2() {
        return mSo2;
    }

    public void setSo2(String so2) {
        mSo2 = so2;
    }

    public String getNo2() {
        return mNo2;
    }

    public void setNo2(String no2) {
        mNo2 = no2;
    }

    public String getCo() {
        return mCo;
    }

    public void setCo(String co) {
        mCo = co;
    }

    public String getO3() {
        return mO3;
    }

    public void setO3(String o3) {
        mO3 = o3;
    }

    public String getQuality() {
        return mQuality;
    }

    public void setQuality(String quality) {
        mQuality = quality;
    }

    public String getLastUpdate() {
        return mLastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        mLastUpdate = lastUpdate;
    }

    public String getValue(String key) {
        String value = "";
        switch (key) {
            case KEY_AQI:
                value = getAqi();
                break;
            case KEY_PM25:
                value = getPm25();
                break;
            case KEY_PM10:
                value = getPm10();
                break;
            case KEY_SO2:
                value = getSo2();
                break;
            case KEY_NO2:
                value = getNo2();
                break;
            case KEY_CO:
                value = getCo();
                break;
            case KEY_O3:
                value = getO3();
                break;
            default:
                break;
        }
        return value;
    }

    @Override
    public String getRequest(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public String getPrintableRequest(Gson gson) {
        return getRequest(gson);
    }

}
