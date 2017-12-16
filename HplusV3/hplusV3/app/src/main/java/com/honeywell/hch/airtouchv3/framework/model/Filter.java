package com.honeywell.hch.airtouchv3.framework.model;

/**
 * Created by wuyuan on 9/23/15.
 * The Filter contain data of filter.
 * it will be used to show the different UI of AirTouchS and AirTouch P
 */
public class Filter {

    private float mUsagePrecent;

    private String mName;

    private String mDesc;

    private String mRFIDStr;

    /**
     * when the filter is producted by Honeywell,is true.
     * else the value is false.
     */
    private boolean isAuthorizeUse;

    private int mNameResourceId;

    private int mDesResourceId;

    public float getUsagePrecent() {
        return mUsagePrecent;
    }

    public void setUsagePrecent(float mUsagePrecent) {
        this.mUsagePrecent = mUsagePrecent;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getRFIDStr() {
        return mRFIDStr;
    }

    public void setRFIDStr(String mRFIDStr) {
        this.mRFIDStr = mRFIDStr;
    }

    public boolean isAuthorizeUse() {
        return isAuthorizeUse;
    }

    public void setIsAuthorizeUse(boolean isAuthorizeUse) {
        this.isAuthorizeUse = isAuthorizeUse;
    }

    public int getNameResourceId() {
        return mNameResourceId;
    }

    public void setNameResourceId(int nameResourceId) {
        this.mNameResourceId = nameResourceId;
    }

    public int getDesResourceId() {
        return mDesResourceId;
    }

    public void setDesResourceId(int desResourceId) {
        this.mDesResourceId = desResourceId;
    }
}
