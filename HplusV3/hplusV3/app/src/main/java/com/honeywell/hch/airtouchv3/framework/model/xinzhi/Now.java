package com.honeywell.hch.airtouchv3.framework.model.xinzhi;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;

/**
 * Created by e573227 on 2/2/2015.
 */
public class Now implements IRequestResponse, Serializable {
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

}
