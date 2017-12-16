package com.honeywell.hch.airtouchv3.framework.enrollment.models;

import java.io.Serializable;

public class DIYProgressionState implements Serializable {

    private static final long serialVersionUID = -6267328447029599942L;

    public static final int SKIP_REMOVE_OLD_THERMOSTAT_FLAG = 1 << 0;
    public static final int SKIP_CONNECT_AND_PERSONALIZE = 1 << 1;
    public static final int OTHER_WIFI = 1 << 2;
    public static final int PASSWORD_WIFI = 1 << 3;
    public static final int UNCOMMON_WIRING = 1 << 4;
    public static final int WIRING_ALERT = 1 << 5;
    public static final int USE_EXISTING_LOCATION = 1 << 6;
    public static final int SKIP_IRI = 1 << 7;
    public static final int IRI_ADVANCED = 1 << 8;
    public static final int VOLTAGE_WIRING = 1 << 9;
    public static final int DIY_MODE_INSTALL = 1 << 10;
    public static final int DIY_MODE_RESET_WIFI = 1 << 11;
    public static final int DIY_MODE_RECONFIGURE_THERMOSTAT = 1 << 12;
    public static final int DIY_MAC_CHECK_TIMEOUT = 1 << 13;
    public static final int DIY_MAC_CHECK_REGISTERED_TO_DIFFERENT_USER = 1 << 14;
    public static final int DIY_MAC_CHECK_ALREADY_REGISTERED = 1 << 15;
    public static final int DIY_MAC_INSTALL = 1 << 16;

    private int mDiyBranchStatus;
    private long mDiyIriBranchStatus;

    public DIYProgressionState() {

    }

    /**
     * Sets the flag for DIY Branching, use one of the flag ints contained in {@link DIYProgressionState}
     */
    public void setDiyBranchFlag(int diyBranchFlag, boolean flag) {
        int branchStatus = getDiyBranchStatus();

        if (flag) {
            branchStatus |= diyBranchFlag;
        } else {
            branchStatus &= ~diyBranchFlag;
        }
        setDiyBranchStatus(branchStatus);
    }

    public boolean getDiyBranchFlag(int diyBranchFlag) {
        return (getDiyBranchStatus() & diyBranchFlag) != 0;
    }

    public int getDiyBranchStatus() {
        return mDiyBranchStatus;
    }

    public void setDiyBranchStatus(int diyBranchStatus) {
        mDiyBranchStatus = diyBranchStatus;
    }

    public void clearIriBranchStatus() {
        mDiyIriBranchStatus = 0;
    }

    public long getDiyIriBranchStatus() {
        return mDiyIriBranchStatus;
    }

    public void setDiyIriBranchStatus(long diyIriBranchStatus) {
        mDiyIriBranchStatus = diyIriBranchStatus;
    }

    public void clearThermostatConnectionProgress() {
        setDiyBranchFlag(DIYProgressionState.SKIP_IRI, false);
        setDiyBranchFlag(DIYProgressionState.IRI_ADVANCED, false);
        setDiyBranchFlag(DIYProgressionState.PASSWORD_WIFI, false);
        setDiyBranchFlag(DIYProgressionState.OTHER_WIFI, false);
        setDiyBranchFlag(DIYProgressionState.SKIP_CONNECT_AND_PERSONALIZE, false);
        setDiyBranchFlag(DIYProgressionState.USE_EXISTING_LOCATION, false);
        clearIriBranchStatus();
    }
}
