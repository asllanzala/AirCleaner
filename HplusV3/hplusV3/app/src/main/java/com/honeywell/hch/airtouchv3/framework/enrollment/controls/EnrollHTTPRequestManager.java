package com.honeywell.hch.airtouchv3.framework.enrollment.controls;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nan.liu on 2/12/15.
 */
public class EnrollHTTPRequestManager {

    private static final String TAG = EnrollHTTPRequestManager.class.getSimpleName();

    private HTTPRequestParams mHttpRequestParams;

    private int mConnectTimeout = 15 * 1000;
    private int mReadTimeout = 15 * 1000;

    public EnrollHTTPRequestManager(HTTPRequestParams httpRequestParams) {
        this.mHttpRequestParams = httpRequestParams;
    }

    /**
     * Sets the default read timeout for new connections.
     *
     * @param readTimeout unit:second
     */
    public void setReadTimeout(int readTimeout) {
        mReadTimeout = readTimeout * 1000;
    }

    /**
     * Sets the connect timeout for new connections.
     *
     * @param connectTimeout unit:second
     */
    public void setConnectTimeout(int connectTimeout) {
        mConnectTimeout = connectTimeout * 1000;
    }

    /**
     * execute request
     */
    public HTTPRequestResponse sendRequest() {
        HTTPRequestResponse httpRequestResponse = new HTTPRequestResponse();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(mHttpRequestParams.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(mConnectTimeout);
            connection.setReadTimeout(mReadTimeout);
            connection.setRequestMethod(mHttpRequestParams.getType().name());

            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "request url:" + mHttpRequestParams.getUrl());
            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "request session:" + mHttpRequestParams
                    .getSessionID());
            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "request type:" + mHttpRequestParams.getType
                    ().name());

            connection.setDoInput(true);
            String body = "";
            if (mHttpRequestParams.getOtherParams() != null) {
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                Gson gson = new Gson();
                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "request data:" + mHttpRequestParams
                        .getOtherParams().getPrintableRequest(gson));
                body = mHttpRequestParams.getOtherParams().getRequest(gson);
            }
            connection.setRequestProperty("Content-Length", body.getBytes().length + "");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if (!StringUtil.isEmpty(mHttpRequestParams.getSessionID())) {
                connection.setRequestProperty("Cookie", "SessionID=" + mHttpRequestParams
                        .getSessionID());
            }

            OutputStream outStream = null;
            InputStream inputStream = null;
            if (mHttpRequestParams.getOtherParams() != null) {
                outStream = connection.getOutputStream();
                outStream.write(body.getBytes());
            }
            int responseCode = connection.getResponseCode();

            if (isValidResponseCode(responseCode)) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder resultBuilder = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                resultBuilder.append(line);
            }
            String resultString = resultBuilder.toString();

            httpRequestResponse.setStatusCode(responseCode);
            JsonElement jsonElem = new JsonParser().parse(resultString);
            if (jsonElem.isJsonArray() || jsonElem.isJsonObject()) {
                httpRequestResponse.setData(resultString);
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "response [data]:" + resultString);
            } else {
                httpRequestResponse.setException(new JSONException(""));
            }
            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "response data:" + resultString);
        } catch (MalformedURLException e) {
            httpRequestResponse.setException(e);
        } catch (IOException e) {
            httpRequestResponse.setStatusCode(StatusCode.NETWORK_ERROR);
            httpRequestResponse.setException(e);
            e.printStackTrace();
        } catch (Exception e) {
            httpRequestResponse.setException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            httpRequestResponse.setRequestID(mHttpRequestParams.getRequestID());
            return httpRequestResponse;
        }
    }

    private boolean isValidResponseCode(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }
}

