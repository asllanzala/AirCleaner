package com.honeywell.hch.airtouchv2.lib.location;

import android.location.Location;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.framework.webservice.GPSClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2014/8/28.
 */
public class GetAddressFromGoogle {
    private static final String TAG = "GetAddressFromGoogleTask";
    private GetAddressDataListener mCallback;
    private Location mLocation;
    private CityInfo cityLocation = null;

    public GetAddressFromGoogle(GetAddressDataListener callback) {
        mCallback = callback;
    }

    public GetAddressFromGoogle(GetAddressDataListener callback, Location location) {
        mCallback = callback;
        mLocation = location;
        getLocationInfo();
    }

    public void getLocationInfo() {
        IReceiveResponse receiveResponse = new IReceiveResponse() {
            @Override
            public void onReceive(HTTPRequestResponse httpRequestResponse) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG,
                        "jsonString :" + httpRequestResponse.getData());
                if (StringUtil.isEmpty(httpRequestResponse.getData()))
                    return;
                try {
                    JSONObject result = new JSONObject(httpRequestResponse.getData());
                    if (result.getInt("status") == 0) {
                        JSONObject addressComponents = result.getJSONObject("result")
                                .getJSONObject("addressComponent");
                        JSONObject location = result.getJSONObject("result").getJSONObject("location");
                        addressComponents.put("lat", location.get("lat"));
                        addressComponents.put("lng", location.get("lng"));
                        cityLocation = new Gson().fromJson(addressComponents.toString(), CityInfo.class);
                    } else {
                        cityLocation = new CityInfo();
                    }

                    mCallback.onGetAddressDataComplete(cityLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, httpRequestResponse.getData());
                }
            }
        };
        GPSClient.sharedInstance().getCityAddressData(mLocation, receiveResponse);
    }

    public static interface GetAddressDataListener {
        public void onGetAddressDataComplete(CityInfo cityLocation);
    }

}
