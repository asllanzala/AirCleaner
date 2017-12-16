package com.honeywell.hch.airtouchv2.framework.enrollment.controls;

import android.os.AsyncTask;

import com.honeywell.hch.airtouchv2.framework.enrollment.models.WAPIRouter;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.http.PhoneNameRequest;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;

/**
 * Execute http request
 * Created by nan.liu on 1/20/15.
 */
public class EnrollmentClient {
    private static final String TAG = "AirTouchEnrollmentClient";
    private static EnrollmentClient mEnrollmentClient;

    private static final String REQUEST_PHONE_NAME = "phonename";
    private static final String REQUEST_WAPI_KEY = "key";
    private static final String REQUEST_DEVICE_INFO = "maccrc";
    private static final String REQUEST_SCAN_ROUTERS = "scan";
    private static final String REQUEST_CONNECT = "connect";
    private static final String REQUEST_ERROR = "error";
    private String mBaseLocalUrl;

    public static EnrollmentClient sharedInstance() {
        if (null == mEnrollmentClient) {
            mEnrollmentClient = new EnrollmentClient();
        }
        return mEnrollmentClient;
    }

    public EnrollmentClient() {
        mBaseLocalUrl = "http://192.168.1.1/wapi/";
    }

    private String getLocalUrl(String request, Object... params) {
        String baseUrl = mBaseLocalUrl + request;
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        return String.format(baseUrl, params);
    }

    public void getErrorCode(RequestID requestID, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(REQUEST_ERROR), null, requestID, null);
        executeHTTPRequest(httpRequestParams, receiveResponse);
    }


    public void sendPhoneName(String phoneName, RequestID requestID, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.PUT, getLocalUrl(REQUEST_PHONE_NAME), null, requestID,
                new PhoneNameRequest(phoneName));
        executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    public void getWAPIKey(RequestID requestID, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(REQUEST_WAPI_KEY), null, requestID, null);
        executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    public void getDeviceInfo(RequestID requestID, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(REQUEST_DEVICE_INFO), null, requestID, null);
        executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    public void getWAPIRouters(RequestID requestID, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.GET, getLocalUrl(REQUEST_SCAN_ROUTERS), null, requestID, null);
        executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    public void connectStat(WAPIRouter wapiRouter, RequestID requestID, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                .RequestType.PUT, getLocalUrl(REQUEST_CONNECT), null, requestID, wapiRouter);
        executeHTTPRequest(httpRequestParams, receiveResponse);
    }

    public void executeHTTPRequest(HTTPRequestParams httpRequestParams,
                                   IReceiveResponse receiveResponse) {
        EnrollHTTPRequestManager httpRequestManager = new EnrollHTTPRequestManager(httpRequestParams);
        new requestTask(httpRequestManager, receiveResponse).execute();
    }

    protected class requestTask extends AsyncTask<Object, Object, HTTPRequestResponse> {
        private EnrollHTTPRequestManager mHttpRequestManager;
        private IReceiveResponse mIReceiveResponse;

        public requestTask(EnrollHTTPRequestManager httpRequestManager, IReceiveResponse iReceiveResponse) {
            this.mHttpRequestManager = httpRequestManager;
            this.mIReceiveResponse = iReceiveResponse;
        }

        @Override
        protected HTTPRequestResponse doInBackground(Object... params) {
            return mHttpRequestManager.sendRequest();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(HTTPRequestResponse httpRequestResponse) {
            if (mIReceiveResponse != null && httpRequestResponse != null) {
                mIReceiveResponse.onReceive(httpRequestResponse);
            }
            super.onPostExecute(httpRequestResponse);
        }
    }
}
