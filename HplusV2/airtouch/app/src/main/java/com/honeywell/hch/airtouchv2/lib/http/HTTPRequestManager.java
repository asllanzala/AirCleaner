package com.honeywell.hch.airtouchv2.lib.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Manage http request
 * Created by nan.liu on 1/13/15.
 */
public class HTTPRequestManager {

    public enum RequestType {
        DELETE,
        POST,
        PUT,
        GET
    }

    private static final String TAG = "AirTouchHTTPRequestManager";

    private HTTPRequestParams mHttpRequestParams;

    private long mConnectTimeout = 15 * 1000;
    private long mReadTimeout = 15 * 1000;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public HTTPRequestManager(HTTPRequestParams httpRequestParams) {
        this.mHttpRequestParams = httpRequestParams;
    }

    /**
     * Sets the default read timeout for new connections.
     *
     * @param readTimeout unit:second
     */
    public void setReadTimeout(long readTimeout) {
        mReadTimeout = readTimeout * 1000;
    }

    /**
     * Sets the connect timeout for new connections.
     *
     * @param connectTimeout unit:second
     */
    public void setConnectTimeout(long connectTimeout) {
        mConnectTimeout = connectTimeout * 1000;
    }

    /**
     * execute request
     */
    public HTTPRequestResponse sendRequest(OkHttpClient okHttpClient) {
        okHttpClient.setConnectTimeout(mConnectTimeout, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(mReadTimeout, TimeUnit.MILLISECONDS);
        Request.Builder requestBuilder = new Request.Builder()
                .url(mHttpRequestParams.getUrl());

        if (mHttpRequestParams.getRequestID() == RequestID.COMM_TASK) {
            LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "request [type]:" + mHttpRequestParams.getType().name()
                    + " [url]:" + mHttpRequestParams.getUrl()
                    + " [session]:" + mHttpRequestParams.getSessionID());
        } else {
            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "request [type]:" + mHttpRequestParams.getType().name()
                    + " [url]:" + mHttpRequestParams.getUrl()
                    + " [session]:" + mHttpRequestParams.getSessionID());
        }

        RequestBody body = null;
        if (mHttpRequestParams.getOtherParams() != null) {
            Gson gson = new Gson();
            if (mHttpRequestParams.getRequestID() == RequestID.COMM_TASK) {
                LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, " [data]:" + mHttpRequestParams.getOtherParams().getPrintableRequest(gson));
            } else {
                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, " [data]:" + mHttpRequestParams.getOtherParams().getPrintableRequest(gson));
            }
            body = RequestBody.create(JSON, mHttpRequestParams.getOtherParams()
                    .getRequest(gson));
        }
        switch (mHttpRequestParams.getType()) {
            case DELETE:
                requestBuilder.delete();
                break;
            case POST:
                requestBuilder.post(body);
                break;
            case PUT:
                requestBuilder.put(body);
                break;
            default:
                break;
        }
        if (!StringUtil.isEmpty(mHttpRequestParams.getSessionID())) {
            requestBuilder.addHeader("Cookie", "sessionId=" + mHttpRequestParams
                    .getSessionID());
        }
        Request request = requestBuilder.build();

        HTTPRequestResponse httpRequestResponse = new HTTPRequestResponse();
        httpRequestResponse.setRandomRequestID(mHttpRequestParams.getRandomRequestID());
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response != null) {
                httpRequestResponse.setStatusCode(response.code());
                String result = response.body().string();
                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "response [data]:" + result);
                JsonElement jsonElem = new JsonParser().parse(result);
                if (jsonElem.isJsonArray() || jsonElem.isJsonObject()) {
                    httpRequestResponse.setData(result);
//                    if (mHttpRequestParams.getRequestID() == RequestID.COMM_TASK) {
//                        LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "response [data]:" + result);
//                    } else {

//                    }
                } else {
                    httpRequestResponse.setException(new JSONException(""));
                }
            }
        } catch (IOException e) {
            httpRequestResponse.setStatusCode(StatusCode.NETWORK_ERROR);
            httpRequestResponse.setException(e);
            e.printStackTrace();
        } catch (Exception e) {
            httpRequestResponse.setException(e);
        }
        httpRequestResponse.setRequestID(mHttpRequestParams.getRequestID());
        return httpRequestResponse;
    }
}
