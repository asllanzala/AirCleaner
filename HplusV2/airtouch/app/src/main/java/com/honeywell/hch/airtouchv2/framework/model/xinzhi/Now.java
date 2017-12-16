package com.honeywell.hch.airtouchv2.framework.model.xinzhi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by e573227 on 2/2/2015.
 */
public class Now implements IRequestParams, Serializable {
    @SerializedName("text")
    private String mText;

    @SerializedName("code")
    private int mCode;

    @SerializedName("temperature")
    private int mTemperature;

    @SerializedName("air_quality")
    private AirQuality mAirQuality;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int temperature) {
        mTemperature = temperature;
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
