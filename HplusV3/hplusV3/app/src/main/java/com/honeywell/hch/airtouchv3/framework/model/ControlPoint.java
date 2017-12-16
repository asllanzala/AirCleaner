package com.honeywell.hch.airtouchv3.framework.model;

/**
 * Created by wuyuan on 9/23/15.
 */
public class ControlPoint {

    /**
     * total point number in control panel
     */
    private int mTotalPointNumber;

    /**
     * point number of every level;
     */
    private int mPointNumberOfEveryLevel;

    public int getTotalPointNumber() {
        return mTotalPointNumber;
    }

    public void setTotalPointNumber(int totalPointNumber) {
        this.mTotalPointNumber = totalPointNumber;
    }

    public int getPointNumberOfEveryLevel() {
        return mPointNumberOfEveryLevel;
    }

    public void setPointNumberOfEveryLevel(int pointNumberOfEveryLevel) {
        this.mPointNumberOfEveryLevel = pointNumberOfEveryLevel;
    }
}
