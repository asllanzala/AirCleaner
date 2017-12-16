package com.honeywell.hch.airtouchv3.framework.model.modelinterface;

import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;

/**
 * Created by wuyuan on 7/30/15.
 * according different Airtouch device.should generate different view of filter view
 *
 */
public interface IFilterable
{
    /**
     * get filter information about different airtouch device,the date will be used in
     * control panel.
     * @return Filter filter information about different airtouch device
     */
    public void getFilterInfo(IActivityReceive getDeviceCapabilityReceive);
}
