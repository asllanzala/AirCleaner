package com.honeywell.hch.airtouchv3.lib.http;

/**
 * Created by nan.liu on 2/2/15.
 */
public interface IReceiveResponse {
    /**
     * Called when response is received.
     *
     * @param httpRequestResponse The response that received from server.
     */
    public void onReceive(HTTPRequestResponse httpRequestResponse);
}
