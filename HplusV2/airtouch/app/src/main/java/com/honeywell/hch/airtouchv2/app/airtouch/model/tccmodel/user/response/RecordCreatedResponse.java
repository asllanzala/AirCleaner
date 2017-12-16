package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestResponse;

/**
 * Created by Jin Qian on 1/16/2015.
 */
public class RecordCreatedResponse implements IRequestResponse {
    @SerializedName("id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
