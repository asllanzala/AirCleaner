package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/16/2015.
 */
public class DeviceControlRequest implements IRequestParams, Serializable {
    @SerializedName("airCleanerFanModeSwitch")
    private String fanModeSwitch;

    public String getFanModeSwitch() {
        return fanModeSwitch;
    }

    public void setFanModeSwitch(String fanModeSwitch) {
        this.fanModeSwitch = fanModeSwitch;
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
