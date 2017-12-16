package com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response;

import android.view.View;

import com.honeywell.hch.airtouchv2.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IControllable;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IFilterable;

/**
 * Created by wuyuan on 7/30/15.
 * AirTouchS device,a subclass of AirTouchSeries
 */
public class AirTouchSDevice extends AirTouchSeriesDevice implements IControllable, IFilterable
{

    /**
     * abstract method from parent's class
     * @return
     */
    @Override
    public View createFilterView()
    {
        return null;
    }

    /**
     * the method from IControllable
     */
    @Override
    public void controlDevice()
    {

    }
}
