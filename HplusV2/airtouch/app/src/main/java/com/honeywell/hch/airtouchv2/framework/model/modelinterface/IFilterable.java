package com.honeywell.hch.airtouchv2.framework.model.modelinterface;

import android.view.View;

/**
 * Created by wuyuan on 7/30/15.
 * according different Airtouch device.should generate different view of filter view
 *
 */
public interface IFilterable
{
    public View createFilterView();
}
