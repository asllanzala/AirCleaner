package com.honeywell.hch.airtouchv3.app.dashboard.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qian Jin on 10/30/15.
 */
public class MultiCommTaskRequest implements IRequestParams, Serializable {
    private List<Integer> mDeviceIdList = new ArrayList<>();

    public MultiCommTaskRequest(List<Integer> deviceIds) {
        mDeviceIdList = deviceIds;
    }

    @Override
    public String getRequest(Gson gson) {
        return gson.toJson(mDeviceIdList);
    }

    @Override
    public String getPrintableRequest(Gson gson) {
        return getRequest(gson);
    }

}
