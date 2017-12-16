package com.honeywell.hch.airtouchv2.framework.webservice;

import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;

/**
 * Created by Jin Qian on 2/2/15
 */
public class ThinkPageClient extends HTTPClient {
    private static final String TAG = "AirTouchThinkPageClient";
    private static ThinkPageClient mThinkPageClient;

    private static final String KEY = "BNNB44SG1G";
    private static final String ALL = "all.json?"; // get all data from target city
    private static final String NOW = "now.json?"; // get today's temperature and weather
    private static final String AIR = "air.json?"; // get Air quality index
    private String mBaseLocalUrl;
    private String mBasePostfixUrl;

    public static ThinkPageClient sharedInstance() {
        if (null == mThinkPageClient) {
            mThinkPageClient = new ThinkPageClient();
        }
        return mThinkPageClient;
    }

    public ThinkPageClient() {
        mBaseLocalUrl = "https://api.thinkpage.cn/v2/weather/";
        mBasePostfixUrl = "city=%1$s&language=%2$s&unit=%3$c&aqi=city&key=%4$s";
    }

    private String getLocalUrl(String request, Object... params) {
        String baseUrl = mBaseLocalUrl + request + mBasePostfixUrl;
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        return String.format(baseUrl, params);
    }


    public void getWeatherData(String city, String lang, char temperatureUnit, RequestID requestID, IReceiveResponse
            receiveResponse) {
        HTTPRequestParams httpRequestParams;
        switch (requestID) {
            case ALL_DATA:
                httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                        .RequestType.GET, getLocalUrl(ALL, city, lang, temperatureUnit, KEY), null, requestID, null);
                executeHTTPRequest(httpRequestParams, receiveResponse);
                break;
            case NOW_DATA:
                httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                        .RequestType.GET, getLocalUrl(NOW, city, lang, temperatureUnit, KEY), null, requestID, null);
                executeHTTPRequest(httpRequestParams, receiveResponse);
                break;
            case AIR_DATA:
                httpRequestParams = new HTTPRequestParams(HTTPRequestManager
                        .RequestType.GET, getLocalUrl(AIR, city, lang, temperatureUnit, KEY), null, requestID, null);
                executeHTTPRequest(httpRequestParams, receiveResponse);
                break;
            default:
                break;
        }
    }

}