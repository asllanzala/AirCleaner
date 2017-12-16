package com.honeywell.hch.airtouchv2.framework.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.database.CityDBService;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.UmengUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Base activity, implement some common function
 * Created by nan.liu on 1/19/15.
 */
public class BaseActivity extends Activity {
    protected String TAG = "AirTouchBaseActivity";
    private Long timeDeviation = 0L;
    private CityDBService mCityDBService;

    private static final int SESSION_TIMEOUT = 15 * 60;
    private static final int SESSION_TIMEOUT_WARNING = 10 * 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UmengUtil.onActivityCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UmengUtil.onActivityResume(this, TAG);

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
            if ((timeDeviation > SESSION_TIMEOUT_WARNING) && (timeDeviation < SESSION_TIMEOUT)) {
                AuthorizeApp.shareInstance().updateSession();
                AuthorizeApp.shareInstance().setSessionLastUpdated(System.currentTimeMillis());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        UmengUtil.onActivityPause(this, TAG);
    }

    protected void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT)
                .show();
    }

    public CityDBService getCityDBService() {
        if (mCityDBService == null) {
            mCityDBService = new CityDBService(this);
        }
        return mCityDBService;
    }

    public void errorHandle(ResponseResult responseResult, String errorMsg) {
        if (responseResult.getResponseCode() == StatusCode.EXCEPTION) {
            MessageBox.createSimpleDialog(this, null,
                    getString(R.string.no_network), null, null);
            return;
        }

        if (responseResult.getExeptionMsg() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "ErrorMsg：" + responseResult.getExeptionMsg());
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "ErrorCode：" + responseResult.getResponseCode());
            if (responseResult.getResponseCode() == StatusCode.BAD_REQUEST) {
                MessageBox.createSimpleDialog(this, null,
                        errorMsg, null, null);
            } else {
                MessageBox.createSimpleDialog(this, null,
                        getString(R.string.enroll_error), null, null);
            }
        }
    }
}
