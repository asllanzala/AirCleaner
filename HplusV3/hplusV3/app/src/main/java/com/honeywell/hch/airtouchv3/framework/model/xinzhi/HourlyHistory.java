package com.honeywell.hch.airtouchv3.framework.model.xinzhi;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;

/**
 * Created by lynnliu on 10/17/15.
 */
public class HourlyHistory implements IRequestResponse, Serializable {

    @SerializedName("status")
    private String mStatus;

    @SerializedName("history")
    private HistoryHour[] mHours;

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public HistoryHour[] getHours() {
        return mHours;
    }

    public void setHours(HistoryHour[] hours) {
        mHours = hours;
    }
}
