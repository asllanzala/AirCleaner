package com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 1/16/2015.
 */
public class SwapLocationRequest implements IRequestParams, Serializable {
    @SerializedName("name")
    private String mName;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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
