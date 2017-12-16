package com.honeywell.hch.airtouchv3.framework.model;

import com.honeywell.hch.airtouchv3.framework.model.modelinterface.IDataManager;
import com.honeywell.hch.airtouchv3.framework.model.modelinterface.IRefreshEnd;

/**
 * Created by wuyuan on 7/30/15.
 * <p/>
 * by Stephen(H127856)
 * data model reconstruction
 */
public class HomeDevice implements IDataManager {

    protected DeviceInfo mDeviceInfo;

    protected IRefreshEnd notifyRefreshEnd;

    protected int isMasterDevice;

    public DeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }


    /**
     * get device type
     *
     * @return device type,like AirTouchConstants.AIRTOUCHS_TYPE or AirTouchConstants.AIRTOUCHP_TYPE
     */
    public int getDeviceType() {
        return mDeviceInfo.getmDeviceType();
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    public int getIsMasterDevice() {
        return isMasterDevice;
    }

    public void setIsMasterDevice(int isMasterDevice) {
        this.isMasterDevice = isMasterDevice;
    }


    @Override
    public void loadData(IRefreshEnd iRefreshEnd) {
    }
}