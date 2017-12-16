package com.honeywell.hch.airtouchv2.framework.model.xinzhi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 2/2/2015.
 */
public class Weather implements IRequestParams, Serializable {
    @SerializedName("city_name")
    private String mCityName;

    @SerializedName("last_update")
    private String mLastUpdate;

    @SerializedName("now")
    private Now mNow;

    @SerializedName("air_quality")
    private AirQuality mAirQuality;

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public String getLastUpdate() {
        return mLastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        mLastUpdate = lastUpdate;
    }

    public Now getNow() {
        return mNow;
    }

    public void setNow(Now now) {
        mNow = now;
    }

    public AirQuality getAirQuality() {
        return mAirQuality;
    }

    public void setAirQuality(AirQuality airQuality) {
        mAirQuality = airQuality;
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
