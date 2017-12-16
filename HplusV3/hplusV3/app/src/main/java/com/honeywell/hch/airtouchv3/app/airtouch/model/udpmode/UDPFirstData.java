package com.honeywell.hch.airtouchv3.app.airtouch.model.udpmode;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wuyuan on 11/19/15.
 */
public class UDPFirstData implements Serializable {

    @SerializedName("productuuid")
    private String productUUid = "";


    public String getProductUUid() {
        return productUUid;
    }

    public void setProductUUid(String productUUid) {
        this.productUUid = productUUid;
    }
}
