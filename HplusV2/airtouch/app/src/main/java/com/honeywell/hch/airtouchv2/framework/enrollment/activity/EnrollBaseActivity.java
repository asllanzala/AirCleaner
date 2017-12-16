package com.honeywell.hch.airtouchv2.framework.enrollment.activity;

import android.os.Bundle;
import android.view.View;

import com.honeywell.hch.airtouchv2.ATApplication;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Special base activity for enrollment
 * Created by nan.liu on 1/26/15.
 */
public class EnrollBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Umeng statistic
         */
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Umeng statistics
        MobclickAgent.onPageStart(TAG); // ͳ��ҳ��
        MobclickAgent.onResume(this); // ͳ��ʱ��
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Umeng statistics
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    public void errorHandle(ResponseResult responseResult, String errorMsg) {
        // do not quitEnroll if wifi issue.
        if (responseResult.getFlag() == AirTouchConstants.CHECK_MAC_OFFLINE) {
            MessageBox.createSimpleDialog(this, null, errorMsg, null, quitEnroll2);
            return;
        }

        if (responseResult.getResponseCode() == StatusCode.EXCEPTION) {
            MessageBox.createSimpleDialog(this, null,
                    ATApplication.getInstance().getApplicationContext().getString(R.string.no_network), null, quitEnroll);
            return;
        }

        if (responseResult.getExeptionMsg() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "ExceptionMsg：" + responseResult.getExeptionMsg());
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "ErrorCode：" + responseResult.getResponseCode());
        }

        MessageBox.createSimpleDialog(this, null, errorMsg, null, quitEnroll);

    }

    private MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    };

    private MessageBox.MyOnClick quitEnroll2 = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            turnBack();
        }
    };

    protected void turnBack() {}

}


