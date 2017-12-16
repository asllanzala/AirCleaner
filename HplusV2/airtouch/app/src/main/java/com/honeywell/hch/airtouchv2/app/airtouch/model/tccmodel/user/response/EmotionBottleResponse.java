package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;

import java.io.Serializable;

/**
 * Created by Jin Qian on 7/2/2015.
 */
public class EmotionBottleResponse implements IRequestParams, Serializable {
    @SerializedName("cleanDust")
    private float mCleanDust;

    @SerializedName("paHs")
    private float mPahs;

    @SerializedName("heavyMetal")
    private float mHeavyMetal;

    public float getCleanDust() {
        return mCleanDust;
    }

    public float getPahs() {
        return mPahs;
    }

    public float getHeavyMetal() {
        return mHeavyMetal;
    }

    public void setCleanDust(float mCleanDust) {
        this.mCleanDust = mCleanDust;
    }

    public void setPahs(float mPahs) {
        this.mPahs = mPahs;
    }

    public void setHeavyMetal(float mHeavyMetal) {
        this.mHeavyMetal = mHeavyMetal;
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
