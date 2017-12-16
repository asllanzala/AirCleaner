package com.honeywell.hch.airtouchv3.framework.webservice;

import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HourlyFuture;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HourlyHistory;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseParseManager;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestManager.RequestType;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

/**
 * Created by Jin Qian on 2/2/15
 */
public class ThinkPageClient extends HTTPClient implements ThinkPageWebService {
    private static final String TAG = "AirTouchThinkPageClient";
    private static ThinkPageClient mThinkPageClient;

    private static final String KEY = "BNNB44SG1G";
    private static final String ALL = "all.json?"; // get all data from target city
    private static final String NOW = "now.json?"; // get today's temperature and weather
    private static final String AIR = "air.json?"; // get Air quality index
    private static final String FUTURE = "future24h.json?";
    private static final String HISTORY = "history24h.json?";
    private String mBaseLocalUrl;
    private String mBasePostfixUrl;
    private String mHourlyPostfixUrl;

    private static final int READ_TIMEOUT = 3000;
    private static final int CONNECT_TIMEOUT = 3000;

    public static ThinkPageClient sharedInstance() {
        if (null == mThinkPageClient) {
            mThinkPageClient = new ThinkPageClient();
        }
        return mThinkPageClient;
    }

    public ThinkPageClient() {
        mBaseLocalUrl = "https://api.thinkpage.cn/v2/weather/";
        mBasePostfixUrl = "city=%1$s&language=%2$s&unit=%3$c&aqi=city&key=%4$s";
        mHourlyPostfixUrl = "city=%1$s&language=%2$s&key=%3$s";
    }

    private String getLocalUrl(String request, Object... params) {
        String baseUrl = mBaseLocalUrl + request + mBasePostfixUrl;
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        return String.format(baseUrl, params);
    }

    private String getHourlyUrl(String request, Object... params) {
        String baseUrl = mBaseLocalUrl + request + mHourlyPostfixUrl;
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        return String.format(baseUrl, params);
    }

    public void getWeatherData(String city, String lang, char temperatureUnit, RequestID
            requestID, IReceiveResponse receiveResponse) {
        HTTPRequestParams httpRequestParams;
        switch (requestID) {
            case ALL_DATA:
                httpRequestParams = new HTTPRequestParams(RequestType.GET, getLocalUrl(ALL, city,
                        lang, temperatureUnit, KEY), null, requestID, null);
                executeHTTPRequest(httpRequestParams, receiveResponse);
                break;
            case NOW_DATA:
                httpRequestParams = new HTTPRequestParams(RequestType.GET, getLocalUrl(NOW, city,
                        lang, temperatureUnit, KEY), null, requestID, null);
                executeHTTPRequest(httpRequestParams, receiveResponse);
                break;
            case AIR_DATA:
                httpRequestParams = new HTTPRequestParams(RequestType.GET, getLocalUrl(AIR, city,
                        lang, temperatureUnit, KEY), null, requestID, null);
                executeHTTPRequest(httpRequestParams, receiveResponse);
                break;
            default:
                break;
        }
    }


    public ResponseResult getWeatherDataNew(String city, String lang, char temperatureUnit,
                                            RequestID requestID) {
        if (StringUtil.isEmpty(city)) {
            return null;
        }
        HTTPRequestParams httpRequestParams;
        ResponseResult result = null;
        HTTPRequestResponse baseWeatherData = null;
        switch (requestID) {
            case ALL_DATA:
                httpRequestParams = new HTTPRequestParams(RequestType.GET, getLocalUrl(ALL, city,
                        lang, temperatureUnit, KEY), null, requestID, null);
                baseWeatherData = executeMethodHTTPRequest(httpRequestParams, null,
                        CONNECT_TIMEOUT, READ_TIMEOUT);
                break;
            case NOW_DATA:
                httpRequestParams = new HTTPRequestParams(RequestType.GET, getLocalUrl(NOW, city,
                        lang, temperatureUnit, KEY), null, requestID, null);
                baseWeatherData = executeMethodHTTPRequest(httpRequestParams, null,
                        CONNECT_TIMEOUT, READ_TIMEOUT);
                break;
            case AIR_DATA:
                httpRequestParams = new HTTPRequestParams(RequestType.GET, getLocalUrl(AIR, city,
                        lang, temperatureUnit, KEY), null, requestID, null);
                baseWeatherData = executeMethodHTTPRequest(httpRequestParams, null,
                        CONNECT_TIMEOUT, READ_TIMEOUT);
                break;
            default:
                break;
        }
       try {
           result = ResponseParseManager.parseAllWeatherResponse(baseWeatherData, RequestID.GET_WEATHER);
       } catch (NumberFormatException nfExcetpin) {
           nfExcetpin.printStackTrace();
       } catch (Exception ex) {
           ex.printStackTrace();
       }

        return result;
    }

    @Override
    public HourlyHistory getHistoryWeather(String city, String lang) {
        HTTPRequestParams hourlyHistoryRequestParams = new HTTPRequestParams(RequestType.GET,
                getHourlyUrl(HISTORY, city, lang, KEY), null, RequestID.HOURLY_HISTORY, null);
        HourlyHistory hourlyHistoryData = ResponseParseManager.parseHourlyHistoryResponse
                (executeMethodHTTPRequest(hourlyHistoryRequestParams, null, CONNECT_TIMEOUT,
                        READ_TIMEOUT));
        return hourlyHistoryData;
    }

    @Override
    public HourlyFuture getFutureWeather(String city, String lang) {
        HTTPRequestParams hourlyHistoryRequestParams = new HTTPRequestParams(RequestType.GET,
                getHourlyUrl(FUTURE, city, lang, KEY), null, RequestID.HOURLY_FUTURE, null);
        HourlyFuture hourlyFutureData = ResponseParseManager.parseHourlyFutureResponse
                (executeMethodHTTPRequest(hourlyHistoryRequestParams, null, CONNECT_TIMEOUT,
                        READ_TIMEOUT));
        return hourlyFutureData;
    }
}