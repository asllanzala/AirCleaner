package com.honeywell.hch.airtouchv3.framework.webservice;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestManager;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by nan.liu on 1/15/15.
 */
public class HTTPClient {

    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型 固定值
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型 固定值

    private static final String TAG = "AirTouchHTTPClient";
    private Long timeDeviation = 0L;
    private OkHttpClient mOkHttpClient;
    protected SparseArray<RequestTask> mRequestTaskSparseArray = new SparseArray<>();

    private static final int SESSION_TIMEOUT = 15 * 60;
    protected static final int MAX_RANDOM_REQUEST_ID = 1000000;

    protected static Random sRandom = new Random();

    protected  SSLContext sslContext;

    public int executeHTTPRequest(HTTPRequestParams httpRequestParams,
                                  IReceiveResponse receiveResponse) {
        /*
         * TCC session will be expired in 15 minutes.
         * Save timestamps each time when HTTP request to TCC.
         * Use current timestamps minus saved time.
         * If deviation is between 10-15 minutes, need update session
         * If deviation is above 15 minutes, need login again at TccClient.
         */
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
        if (authorizeApp.isLoginSuccess()) {
            timeDeviation = System.currentTimeMillis()
                    - authorizeApp.getSessionLastUpdated();
            timeDeviation /= 1000;
            if ((timeDeviation > SESSION_TIMEOUT)
                    && (authorizeApp.getSessionLastUpdated() != 0)) {
                authorizeApp.setSessionLastUpdated(System.currentTimeMillis());
                authorizeApp.currentUserLogin();
            }
            authorizeApp.setSessionLastUpdated(System.currentTimeMillis());
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);
        httpRequestParams.setRandomRequestID(requestId);
        HTTPRequestManager httpRequestManager = new HTTPRequestManager(httpRequestParams);
        RequestTask requestTask = new RequestTask(httpRequestManager, receiveResponse);
        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
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

        OkHttpClient okHttpClient = getUnsafeOkHttpClient(httpRequestParams);
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

        AsyncTaskExecutorUtil.executeAsyncTask(requestTask);
        mRequestTaskSparseArray.append(requestId, requestTask);
        return requestId;
    }


    protected OkHttpClient getUnsafeOkHttpClientNoCer() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {

                        /* 覆盖google默认的证书检查机制（X509TrustManager）的方式
                        * 这段代码存在如下问题：
                        * 覆盖默认的证书检查机制后，检查证书是否合法的责任，就落到了我们自己的代码上。
                        * 但绝大多数app在选择覆盖了默认安全机制后，却没有对证书进行应有的安全性检查，
                        * 直接接受了所有异常的https证书，不提醒用户存在安全风险，也不终止这次危险的连接。
                        *
                        * google的API会检查https证书进行合法性。而开发或者测试环境的https证书，基本上都无法通过合法性检查。
                        * API的检查内容包括以下4方面的内容：
                        * 1. 签名CA是否合法；
                        * 2. 域名是否匹配；
                        * 3. 是不是自签名证书；
                        * 4. 证书是否过期。
                        */
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


    public void setProductCertificates()
    {
        InputStream ksIn = null;

        try
        {
            sslContext = SSLContext.getInstance("TLS");
            ksIn = HPlusApplication.getInstance().getResources().getAssets().open("DigiCertHighAssuranceEVRootca.cer");
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509","BC");
            Certificate cer = cerFactory.generateCertificate(ksIn);


            //创建一个证书库，并将证书导入证书库
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", cer);


            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            sslContext.init(null,trustManagerFactory.getTrustManagers(), new SecureRandom());
            mOkHttpClient.setSslSocketFactory(sslContext.getSocketFactory());


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("haha","Exception e = " + e.toString());
        }
        finally {
            try{
                if (ksIn != null){
                    ksIn.close();
                }
            }catch (Exception e){

            }

        }

    }

//    public void setQACertificates()
//    {
//        InputStream ksIn = null;
//
//        try
//        {
//            sslContext = SSLContext.getInstance("TLS");
//            ksIn = HPlusApplication.getInstance().getResources().getAssets().open("1111.cer");
//            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
//            Certificate cer = cerFactory.generateCertificate(ksIn);
//
//
//            //创建一个证书库，并将证书导入证书库
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null, null);
//            keyStore.setCertificateEntry("trust", cer);
//
//
//            TrustManagerFactory trustManagerFactory =
//                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//
//            sslContext.init(null,trustManagerFactory.getTrustManagers(), new SecureRandom());
//            mOkHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "setCertificates  exception : " + e.toString());
//        }
//        finally {
//            try{
//                if (ksIn != null){
//                    ksIn.close();
//                }
//            }catch (Exception e){
//
//            }
//
//        }
//
//    }

    protected OkHttpClient getUnsafeOkHttpClientWithCer(HTTPRequestParams httpRequestParams) {
        try {

//            if (mOkHttpClient == null) {
                mOkHttpClient = new OkHttpClient();

                setProductCertificates();

                mOkHttpClient.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
//            }
            return mOkHttpClient.clone();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected OkHttpClient getUnsafeOkHttpClient(HTTPRequestParams httpRequestParams) {
        if (httpRequestParams != null && httpRequestParams.getUrl() != null && httpRequestParams.getUrl().contains("mservice.honeywell.com.cn")) {
           return getUnsafeOkHttpClientWithCer(httpRequestParams);
        }
        else{
            return getUnsafeOkHttpClientNoCer();
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
            OkHttpClient okHttpClient = getUnsafeOkHttpClient(mHttpRequestManager.getHttpRequestParams());
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