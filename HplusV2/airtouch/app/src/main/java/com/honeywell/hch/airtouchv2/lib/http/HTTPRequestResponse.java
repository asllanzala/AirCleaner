package com.honeywell.hch.airtouchv2.lib.http;

/**
 * Created by liunan on 1/13/15.
 */
public class HTTPRequestResponse {
    private RequestID mRequestID;
    private int mRandomRequestID;
    private int mStatusCode;
    private String mData;
    private Exception mException;

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

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int statusCode) {
        this.mStatusCode = statusCode;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        this.mData = data;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception exception) {
        mException = exception;
    }
}
