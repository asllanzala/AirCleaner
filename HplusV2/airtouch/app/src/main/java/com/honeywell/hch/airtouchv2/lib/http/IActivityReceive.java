package com.honeywell.hch.airtouchv2.lib.http;

import com.honeywell.hch.airtouchv2.framework.webservice.task.ResponseResult;

/**
 * Created by nan.liu on 2/2/15.
 */
public interface IActivityReceive
{
    /**
     * Called when response is received.
     *
     * @param responseResult ResponseResult from request.
     */
    public void onReceive(ResponseResult responseResult);
}
