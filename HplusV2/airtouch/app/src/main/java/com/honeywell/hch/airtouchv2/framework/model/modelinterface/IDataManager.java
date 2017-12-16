package com.honeywell.hch.airtouchv2.framework.model.modelinterface;

/**
 * Created by wuyuan on 7/31/15.
 * the base activity should implement this
 *
 *  * by Stephen(H127856)
 * data model reconstruction
 */
public interface IDataManager
{
    /**
     * get device data，use iRefreshEnd tell the caller what's the response
     */
    void loadData(IRefreshEnd iRefreshEnd);

    void refreshData();
}
