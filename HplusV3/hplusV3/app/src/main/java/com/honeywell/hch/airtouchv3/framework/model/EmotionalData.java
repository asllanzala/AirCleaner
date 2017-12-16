package com.honeywell.hch.airtouchv3.framework.model;

/**
 * Created by wuyuan on 10/10/15.
 */
public class EmotionalData {

    private int mPeriodType;

    private float mPm25Value;

    private float mLeadValue;

    private double mCigerateValue;

    private float mPAHsValue;

    private float mFumeSecondValue;

    public int getPeriodType() {
        return mPeriodType;
    }

    public void setPeriodType(int mPeriodType) {
        this.mPeriodType = mPeriodType;
    }

    public float getPm25Value() {
        return mPm25Value;
    }

    public void setPm25Value(float mPm25Value) {
        this.mPm25Value = mPm25Value;
    }

    public float getLeadValue() {
        return mLeadValue;
    }

    public void setLeadValue(float mLeadValue) {
        this.mLeadValue = mLeadValue;
    }

    public double getCigerateValue() {
        return mCigerateValue;
    }

    public void setCigerateValue(double mCigerateValue) {
        this.mCigerateValue = mCigerateValue;
    }

    public float getPAHsValue() {
        return mPAHsValue;
    }

    public void setPAHsValue(float mPAHsValue) {
        this.mPAHsValue = mPAHsValue;
    }

    public float getFumeSecondValue() {
        return mFumeSecondValue;
    }

    public void setFumeSecondValue(float mFumeSecondValue) {
        this.mFumeSecondValue = mFumeSecondValue;
    }
}
