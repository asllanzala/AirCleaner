package com.honeywell.hch.airtouchv3.app.dashboard.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Qian Jin on 10/13/15.
 */
public class ScenarioGroupRequest implements IRequestParams, Serializable {

    /**
     * AWAY == OFF
     */
    public static final int SCENARIO_AWAY_OFF = 0;

    /**
     * HOME == AUTO == ON
     */
    public static final int SCENARIO_HOME_AUTO = 1;
    public static final int SCENARIO_SLEEP = 2;

    @SerializedName("groupScenario")
    private int mGroupScenario;

    public ScenarioGroupRequest(int groupScenario) {
        mGroupScenario = groupScenario;
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
