package com.honeywell.hch.airtouchv3.app.dashboard.model;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Qian Jin on 10/14/15.
 * get group by group id
 */
public class GroupDataResponse implements IRequestResponse, Serializable {
    public static final String GROUP_LIST = "group_list_bundle";

    @SerializedName("groupId")
    private int mGroupId;

    @SerializedName("locationId")
    private int mLocationId;

    @SerializedName("groupName")
    private String mGroupName;

    @SerializedName("devices")
    private List<DevicesForGroupResponse> mGroupDeviceList;

    public int getGroupId() {
        return mGroupId;
    }

    public int getLocationId() {
        return mLocationId;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public List<DevicesForGroupResponse> getGroupDeviceList() {
        return mGroupDeviceList;
    }
}
