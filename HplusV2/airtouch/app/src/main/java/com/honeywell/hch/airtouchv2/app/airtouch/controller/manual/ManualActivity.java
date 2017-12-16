package com.honeywell.hch.airtouchv2.app.airtouch.controller.manual;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;

/**
 * Created by Jin Qian on 3/30/2015.
 */
public class ManualActivity extends BaseActivity {
    private static final String TAG = "AirTouchManual";
    private WebView mWebView;
    private TextView mTitleTextView;
    private FrameLayout backFrameLayout;
//    private final static String WEB_URL = "http://portalvhds3z0q255367l7.blob.core.chinacloudapi.cn/airtouchs/Usermanual.htm";
    private final static String WEB_URL = "https://hch.blob.core.chinacloudapi.cn/airtouch/Usermanual.htm";
    private final static String EULA_ZH_URL = "file:///android_asset/eula_zh.htm";
    private final static String EULA_EN_URL = "file:///android_asset/eula_en.htm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        super.TAG = TAG;
        mTitleTextView = (TextView) findViewById(R.id.html_title);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

        if (getIntent().getBooleanExtra("eula", false)) {
            mTitleTextView.setText(getString(R.string.eula));
            if (AppConfig.shareInstance().getLanguage().equals("zh")) {
                mWebView.loadUrl(EULA_ZH_URL);
            } else {
                mWebView.loadUrl(EULA_EN_URL);
            }
        } else {
            mTitleTextView.setText(getString(R.string.user_guide));
            mWebView.loadUrl(WEB_URL);

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
                mWebView.goBack();

                if (mWebView.getUrl() == null) {
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    return;
                }

                if (mWebView.getUrl().equals(WEB_URL)) {
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }

                if (getIntent().getBooleanExtra("eula", false)) {
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        return false;
    }

}
