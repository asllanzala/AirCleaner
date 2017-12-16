package com.honeywell.hch.airtouchv3.lib.http;

import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestManager.RequestType;

/**
 * Created by nan.liu on 1/13/15.
 */
public class HTTPRequestParams {

    private RequestType mType;
    private String mUrl;
    private String mSessionID;
    private RequestID mRequestID;
    private int mRandomRequestID;
    private IRequestParams mOtherParams;

    public HTTPRequestParams() {
    }

    public HTTPRequestParams(RequestType type, String url, String sessionID, RequestID requestID,
                             IRequestParams otherParams) {
        setType(type);
        setUrl(url);
        setSessionID(sessionID);
        setRequestID(requestID);
        setOtherParams(otherParams);
    }

    public RequestType getType() {
        return mType;
    }

    public void setType(RequestType type) {
        this.mType = type;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getSessionID() {
        return mSessionID;
    }

    public void setSessionID(String sessionID) {
        this.mSessionID = sessionID;
    }

    public RequestID getRequestID() {
        return mRequestID;
    }

    public void setRequestID(RequestID requestID) {
        this.mRequestID = requestID;
    }

    public int getRandomRequestID() {
        return mRandomRequestID;
    }

    public void setRandomRequestID(int randomRequestID) {
        mRandomRequestID = randomRequestID;
    }

    public IRequestParams getOtherParams() {
        return mOtherParams;
    }

    public void setOtherParams(IRequestParams otherParams) {
        this.mOtherParams = otherParams;
    }
}
