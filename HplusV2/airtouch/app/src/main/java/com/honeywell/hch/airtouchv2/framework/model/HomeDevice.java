package com.honeywell.hch.airtouchv2.framework.model;

import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IDataManager;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IRefreshEnd;

/**
 * Created by wuyuan on 7/30/15.
 *
 * by Stephen(H127856)
 * data model reconstruction
 */
public class HomeDevice implements IDataManager {

    protected DeviceInfo mDeviceInfo;

    protected IRefreshEnd notifyRefreshEnd;

    public DeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
    }

    @Override
    public void loadData(IRefreshEnd iRefreshEnd){
    }

    @Override
    public void refreshData(){}
}