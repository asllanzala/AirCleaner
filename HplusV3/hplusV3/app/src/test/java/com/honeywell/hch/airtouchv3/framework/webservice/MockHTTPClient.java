package com.honeywell.hch.airtouchv3.framework.webservice;

import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;

import java.lang.Exception;

/**
 * Created by lynnliu on 9/17/15.
 */
public class MockHTTPClient {
    private static final String TAG = "AirTouchHTTPTestClient";
    private HTTPRequestResponse mHTTPRequestResponse;
    private Error mError;
    private String mData;

    public void reset() {
        mError = null;
        mHTTPRequestResponse = null;
    }

    public void setHTTPRequestResponseData(String data) {
        mData = data;
    }

    public void setError(Error error) {
        mError = error;
    }

    public HTTPRequestResponse getHTTPRequestSuccessResponse(HTTPRequestParams httpRequestParams) {
        return executeMethodHTTPRequestSuccess(httpRequestParams);
    }

    public HTTPRequestResponse getHTTPRequestFailureResponse(HTTPRequestParams httpRequestParams) {
        return executeMethodHTTPRequestFailure(httpRequestParams);
    }

    private HTTPRequestResponse executeMethodHTTPRequestSuccess(HTTPRequestParams httpRequestParams) {
        HTTPRequestManager httpRequestManager = new HTTPRequestManager(httpRequestParams);
        mHTTPRequestResponse = new HTTPRequestResponse();
        mHTTPRequestResponse.setRequestID(httpRequestParams.getRequestID());
        mHTTPRequestResponse.setStatusCode(StatusCode.OK);
        mHTTPRequestResponse.setData(mData);
        return mHTTPRequestResponse;
    }

    private HTTPRequestResponse executeMethodHTTPRequestFailure(HTTPRequestParams httpRequestParams) {
        HTTPRequestManager httpRequestManager = new HTTPRequestManager(httpRequestParams);
        mHTTPRequestResponse = new HTTPRequestResponse();
        mHTTPRequestResponse.setRequestID(httpRequestParams.getRequestID());
        mHTTPRequestResponse.setStatusCode(mError.mStatusCode);
        mHTTPRequestResponse.setException(new Exception(mError.getResponse()));
        return mHTTPRequestResponse;
    }

    public static class Error {

        private int mStatusCode;
        private String mResponse;

        public Error(int statusCode, String response) {
            mStatusCode = statusCode;
            mResponse = response;
        }

        public Error(int statusCode) {
            mStatusCode = statusCode;
        }

        public Error(String response) {
            mResponse = response;
        }

        public int getStatusCode() {
            return mStatusCode;
        }

        public String getResponse() {
            return mResponse;
        }
    }
}