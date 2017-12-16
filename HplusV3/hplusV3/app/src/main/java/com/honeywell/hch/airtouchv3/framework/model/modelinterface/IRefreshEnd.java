package com.honeywell.hch.airtouchv3.framework.model.modelinterface;

import com.honeywell.hch.airtouchv3.lib.http.RequestID;

/**
 * Created by wuyuan on 7/31/15.
 */
public interface IRefreshEnd
{
    void notifyDataRefreshEnd(RequestID requestId);
}
