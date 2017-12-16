package com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv2.lib.util.UmengUtil;

/**
 * Created by Qian Jin on 9/21/15.
 */
public class EnrollLoadingActivity extends EnrollBaseActivity {
    private static final String TAG = "AirTouchEnrollLoading";
    private ImageView loadingImageView;
    private TextView loadingTextView;
    private AnimationDrawable animationDrawable;
    private EnrollDeviceManager mEnrollDeviceManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollloading);
        mContext = EnrollLoadingActivity.this;

        initView();
        initEnrollManager();
    }

    private void initView() {
        loadingTextView = (TextView) findViewById(R.id.enroll_loading_tv);
        loadingTextView.setText(getString(R.string.enroll_wait));
        loadingImageView = (ImageView) findViewById(R.id.enroll_loading_iv);
        animationDrawable = (AnimationDrawable) loadingImageView.getDrawable();
        animationDrawable.start();
    }

    private void initEnrollManager() {
        mEnrollDeviceManager = new EnrollDeviceManager(mContext, EnrollLoadingActivity.this);
        mEnrollDeviceManager.setLoadingCallback(new EnrollDeviceManager.LoadingCallback() {
            @Override
            public void onLoad(String msg) {
                loadingTextView.setText(msg);
            }
        });
        mEnrollDeviceManager.setFinishCallback(new EnrollDeviceManager.FinishCallback() {
            @Override
            public void onFinish() {
                UmengUtil.onEvent(EnrollLoadingActivity.this,
                        UmengUtil.EventType.ENROLL_SUCCESS.toString());

                animationDrawable.stop();
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
        mEnrollDeviceManager.setErrorCallback(new EnrollDeviceManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, String errorMsg) {
                UmengUtil.onEvent(EnrollLoadingActivity.this,
                        UmengUtil.EventType.ENROLL_FAIL.toString(), errorMsg);

                animationDrawable.stop();
                errorHandle(responseResult, errorMsg);
            }
        });

        mEnrollDeviceManager.reconnectHomeWifi();
        mEnrollDeviceManager.startConnectServer();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mEnrollDeviceManager == null)
            return;

        if (mEnrollDeviceManager.getScanResultsReceiver() != null
                && mEnrollDeviceManager.isRegistered()) {
            unregisterReceiver(mEnrollDeviceManager.getScanResultsReceiver());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            MessageBox.createTwoButtonDialog(this, null,
                    getString(R.string.enroll_quit), getString(R.string.no), null,
                    getString(R.string.yes), quitEnroll);
        }

        return false;
    }

    private MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            finish();
        }
    };

    protected void turnBack() {
        Intent i = new Intent();
        i.setClass(mContext, EnrollWifiPasswordActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }
}
