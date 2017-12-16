package com.honeywell.hch.airtouchv3.framework.webservice;

import android.location.Location;

import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;

/**
 * Created by nan.liu on 2/2/15.
 */
public class GPSClient extends HTTPClient{
    private static final String TAG = "AirTouchGPSClient";

    private static GPSClient mGPSClient;

    public static GPSClient sharedInstance() {
        if (null == mGPSClient) {
            mGPSClient = new GPSClient();
        }
        return mGPSClient;
    }

    /**
     * get format baidu url, add longitude and latitude value
     *
     * @param lag_lon
     * @return
     */
    public String getAddressFromBaiduUrl(String lag_lon) {
        return String.format("http://api.map.baidu.com/geocoder/v2/?" +
                        "ak=v2Q0c6xdWD4qPuL09jPz9mu9&location=%s&output=json&pois=0", lag_lon);
//        return String.format("http://maps.google.com/maps/api/geocode/json?" +
//                "latlng=%s&language=en&sensor=true&result_type=country", lag_lon);
    }

    public String getAddressFromGoogleUrl(String lag_lon) {
        return String.format("http://maps.google.com/maps/api/geocode/json?" +
                        "latlng=%s&language=en&sensor=true&component=country", lag_lon);
    }

    /**
     * get city info from Location if it is valid
     *
     * @param location        longitude and latitude info
     * @param receiveResponse interface for handle result
     */
    public int getCityAddressDataByBaidu(final Location location, IReceiveResponse
            receiveResponse) {
        if (null == location || 0 == location.getLatitude() || 0 == location.getLongitude())
            return -1;
        String JsonURL = getAddressFromBaiduUrl(location.getLatitude() + "," + location.getLongitude());
        LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "JsonURL :" + JsonURL);
        HTTPRequestParams httpRequestParams = new HTTPRequestParams();
        httpRequestParams.setUrl(JsonURL);
        httpRequestParams.setType(HTTPRequestManager.RequestType.GET);
        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);
        httpRequestParams.setRandomRequestID(requestId);
        HTTPRequestManager httpRequestManager = new HTTPRequestManager(httpRequestParams);
        RequestTask requestTask = new RequestTask(httpRequestManager, receiveResponse);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
        mRequestTaskSparseArray.append(requestId, requestTask);
        return requestId;
    }

    public int getCityAddressDataByGoogle(final Location location, IReceiveResponse
            receiveResponse) {
        if (null == location || 0 == location.getLatitude() || 0 == location.getLongitude())
            return -1;
        String JsonURL = getAddressFromGoogleUrl(location.getLatitude() + "," + location.getLongitude());
        LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "JsonURL :" + JsonURL);
        HTTPRequestParams httpRequestParams = new HTTPRequestParams();
        httpRequestParams.setUrl(JsonURL);
        httpRequestParams.setType(HTTPRequestManager.RequestType.GET);
        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);
        httpRequestParams.setRandomRequestID(requestId);
        HTTPRequestManager httpRequestManager = new HTTPRequestManager(httpRequestParams);
        RequestTask requestTask = new RequestTask(httpRequestManager, receiveResponse);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
        mRequestTaskSparseArray.append(requestId, requestTask);
        return requestId;
    }
}
