package com.honeywell.hch.airtouchv3.app.dashboard.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Qian Jin on 10/17/15.
 */
public class ScenarioGroupResponse implements IRequestResponse, Serializable {
    public static final String SCENARIO_DATA = "scenario";

    @SerializedName("commonTaskResultList")
    private List<GroupCommTaskResponse> mGroupCommTaskResponse;

    public List<GroupCommTaskResponse> getGroupCommTaskResponse() {
        return mGroupCommTaskResponse;
    }

}
