package com.honeywell.hch.airtouchv3.framework.model.xinzhi;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jin Qian on 2/2/2015.
 */
public class Weather implements IRequestResponse, Serializable {
    @SerializedName("city_name")
    private String mCityName;

    @SerializedName("city_id")
    private String mCityID;

    @SerializedName("last_update")
    private String mLastUpdate;

    @SerializedName("now")
    private Now mNow;

    @SerializedName("future")
    private List<Future> mFutureList;

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public String getCityID() {
        return mCityID;
    }

    public void setCityID(String cityID) {
        mCityID = cityID;
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

    public List<Future> getFutureList() {
        return mFutureList;
    }

    public void setFutureList(List<Future> futureList) {
        mFutureList = futureList;
    }
}
