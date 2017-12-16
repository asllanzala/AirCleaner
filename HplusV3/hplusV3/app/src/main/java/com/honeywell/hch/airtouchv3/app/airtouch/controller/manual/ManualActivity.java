package com.honeywell.hch.airtouchv3.app.airtouch.controller.manual;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;

/**
 * Created by Jin Qian on 3/30/2015.
 */
public class ManualActivity extends BaseActivity {
    private static final String TAG = "AirTouchManual";
    private WebView mWebView;
    private TextView mTitleTextView;
    private FrameLayout backFrameLayout;
    private final static String WEB_URL_ROOT = "https://hch.blob.core.chinacloudapi.cn/airtouch/Usermanu";
    private final static String WEB_URL_CN = "https://hch.blob.core.chinacloudapi.cn/airtouch/Usermanual.htm?country=CN";
    private final static String WEB_URL_IN = "https://hch.blob.core.chinacloudapi.cn/airtouch/Usermanual.htm?country=IN";
    private final static String EULA_ZH_URL = "file:///android_asset/eula_zh.htm";
    private final static String EULA_EN_URL = "file:///android_asset/eula_en.htm";
    private final static String EULA_INDIA_URL = "file:///android_asset/eula_india.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        super.TAG = TAG;
        mTitleTextView = (TextView) findViewById(R.id.html_title);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent().getBooleanExtra("eula", false)) {
            mTitleTextView.setText(getString(R.string.eula));

            // India version
            if (getIntent().getStringExtra("countryCode")
                    .equals(AirTouchConstants.CHINA_CODE)) {
                if (AppConfig.shareInstance().getLanguage().equals("zh")) {
                    mWebView.loadUrl(EULA_ZH_URL);
                } else {
                    mWebView.loadUrl(EULA_EN_URL);
                }
            } else {
                mWebView.loadUrl(EULA_INDIA_URL);
            }
        } else {
            mTitleTextView.setText(getString(R.string.user_guide));

            // India version
            AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
            if (AppConfig.shareInstance().isIndiaAccount() && authorizeApp.isLoginSuccess()) {
                mWebView.loadUrl(WEB_URL_IN);
            } else {
                if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
                    mWebView.loadUrl(WEB_URL_CN);
                } else {
                    mWebView.loadUrl(WEB_URL_IN);
                }
            }

            mWebView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }

        backFrameLayout = (FrameLayout) findViewById(R.id.back_layout);
        backFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mWebView.goBack();

                if (mWebView.getUrl().contains(WEB_URL_ROOT)){
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    return;
                }
                else{
                    mWebView.goBack();
                }
                if (getIntent().getBooleanExtra("eula", false)) {
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }

            }
        });
    }
}
