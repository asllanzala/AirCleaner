package com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/19/2015.
 */
public class GatewayAliveResponse implements IRequestParams, Serializable {
    @SerializedName("gatewayID")
    private int mGatewayID;

    @SerializedName("isAlive")
    private boolean mIsAlive;

    public int getGatewayID() {
        return mGatewayID;
    }

    public void setGatewayID(int gatewayID) {
        mGatewayID = gatewayID;
    }

    public boolean isAlive() {
        return mIsAlive;
    }

    public void setAlive(boolean isAlive) {
        mIsAlive = isAlive;
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


