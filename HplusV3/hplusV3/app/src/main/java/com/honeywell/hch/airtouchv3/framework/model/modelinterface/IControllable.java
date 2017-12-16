package com.honeywell.hch.airtouchv3.framework.model.modelinterface;

import com.honeywell.hch.airtouchv3.framework.model.ControlPoint;

/**
 * Created by wuyuan on 7/30/15.
 * if the device can be controllable,need to implemts the interface
 */
public interface IControllable
{
    /**
     * control operate of the device
     * @return ControlPoint the point information used in control panel
     */
    public ControlPoint getDeviceControlInfo();
}
