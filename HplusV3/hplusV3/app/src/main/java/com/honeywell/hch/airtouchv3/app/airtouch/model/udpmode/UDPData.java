package com.honeywell.hch.airtouchv3.app.airtouch.model.udpmode;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wuyuan on 11/19/15.
 */
public class UDPData implements Serializable {

    @SerializedName("accesskey")
    private String accessKey = "";
    @SerializedName("feedid")
    private String feedId = "";
    @SerializedName("mac")
    private String mac;
    @SerializedName("server")
    private String server = "";

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }


}
