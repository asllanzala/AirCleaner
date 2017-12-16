package com.honeywell.hch.airtouchv2.framework.webservice;

import android.os.AsyncTask;
import android.util.SparseArray;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.squareup.okhttp.OkHttpClient;

import java.security.cert.CertificateException;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by nan.liu on 1/15/15.
 */
public class HTTPClient {
    private static final String TAG = "AirTouchHTTPClient";
    private Long timeDeviation = 0L;
    private OkHttpClient mOkHttpClient;
    protected SparseArray<RequestTask> mRequestTaskSparseArray = new SparseArray<>();

    private static final int SESSION_TIMEOUT = 15 * 60;
    protected static final int MAX_RANDOM_REQUEST_ID = 1000000;

    protected static Random sRandom = new Random();

    public int executeHTTPRequest(HTTPRequestParams httpRequestParams,
                                  IReceiveResponse receiveResponse) {
        /*
         * TCC session will be expired in 15 minutes.
         * Save timestamps each time when HTTP request to TCC.
         * Use current timestamps minus saved time.
         * If deviation is between 10-15 minutes, need update session
         * If deviation is above 15 minutes, need login again at TccClient.
         */
        if (AuthorizeApp.shareInstance().isLoginSuccess()) {
            timeDeviation = System.currentTimeMillis()
                    - AuthorizeApp.shareInstance().getSessionLastUpdated();
            timeDeviation /= 1000;
            if ((timeDeviation > SESSION_TIMEOUT)
                    && (AuthorizeApp.shareInstance().getSessionLastUpdated() != 0)) {
                AuthorizeApp.shareInstance().setSessionLastUpdated(System.currentTimeMillis());
                AuthorizeApp.shareInstance().currentUserLogin();
            }
            AuthorizeApp.shareInstance().setSessionLastUpdated(System.currentTimeMillis());
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);
        httpRequestParams.setRandomRequestID(requestId);
        HTTPRequestManager httpRequestManager = new HTTPRequestManager(httpRequestParams);
        RequestTask requestTask = new RequestTask(httpRequestManager, receiveResponse);
        requestTask.execute();
        mRequestTaskSparseArray.append(requestId, requestTask);
        return requestId;
    }

    public HTTPRequestResponse executeMethodHTTPRequest(HTTPRequestParams httpRequestParams,
                                                        IActivityReceive receiveResponse, int connectTimeout, int readTimeout)
    {
        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);
        httpRequestParams.setRandomRequestID(requestId);
        HTTPRequestManager httpRequestManager = new HTTPRequestManager(httpRequestParams);
        httpRequestManager.setConnectTimeout(connectTimeout);
        httpRequestManager.setReadTimeout(readTimeout);

        OkHttpClient okHttpClient = getUnsafeOkHttpClient();
        return httpRequestManager.sendRequest(okHttpClient);
    }

    public int executeHTTPRequest(HTTPRequestParams httpRequestParams,
                                  IReceiveResponse receiveResponse, int connectTimeout,
                                  int readTimeout) {
        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);
        httpRequestParams.setRandomRequestID(requestId);
        HTTPRequestManager httpRequestManager = new HTTPRequestManager(httpRequestParams);
        httpRequestManager.setConnectTimeout(connectTimeout);
        httpRequestManager.setReadTimeout(readTimeout);
        RequestTask requestTask = new RequestTask(httpRequestManager, receiveResponse);
        requestTask.execute();
        mRequestTaskSparseArray.append(requestId, requestTask);
        return requestId;
    }

    protected OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            if (mOkHttpClient == null) {
                mOkHttpClient = new OkHttpClient();
                mOkHttpClient.setSslSocketFactory(sslSocketFactory);
                mOkHttpClient.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
            return mOkHttpClient.clone();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return whether a request (specified by its id) is still in progress or not
     *
     * @param requestId
     *            The request id
     * @return whether the request is still in progress or not.
     */
    public boolean isRequestInProgress(final int requestId) {
        return (mRequestTaskSparseArray.indexOfKey(requestId) >= 0);
    }

    public void cancelRequest(final int requestId) {
        mRequestTaskSparseArray.remove(requestId);
    }

    protected class RequestTask extends AsyncTask<Object, Object, HTTPRequestResponse> {
        private HTTPRequestManager mHttpRequestManager;
        private IReceiveResponse mIReceiveResponse;

        public RequestTask(HTTPRequestManager httpRequestManager,
                           IReceiveResponse iReceiveResponse){
            this.mHttpRequestManager = httpRequestManager;
            this.mIReceiveResponse = iReceiveResponse;
        }

        @Override
        protected HTTPRequestResponse doInBackground(Object... params) {
            OkHttpClient okHttpClient = getUnsafeOkHttpClient();
            return mHttpRequestManager.sendRequest(okHttpClient);
        }

        @Override
        protected void onPostExecute(HTTPRequestResponse httpRequestResponse) {
            if (httpRequestResponse == null)
                return;
            int requestId = httpRequestResponse.getRandomRequestID();
            if (mIReceiveResponse != null && isRequestInProgress (requestId)) {
                mIReceiveResponse.onReceive(httpRequestResponse);
            }
            mRequestTaskSparseArray.remove(requestId);
            super.onPostExecute(httpRequestResponse);
        }
    }
}