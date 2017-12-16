package com.honeywell.hch.airtouchv3.framework.enrollment.models.http;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;

public class WAPIKeyResponse implements IRequestResponse, Serializable {

    private static final long serialVersionUID = 2360989918177959925L;

    @SerializedName("Modulus")
    private String mModulus;

    @SerializedName("Exponent")
    private String mExponent;

    public String getModulus() {
        return mModulus;
    }

    public void setModulus(String modulus) {
        mModulus = modulus;
    }

    public String getExponent() {
        return mExponent;
    }

    public void setExponent(String exponent) {
        mExponent = exponent;
    }
}
