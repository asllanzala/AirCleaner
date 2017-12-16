package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.EnrollWelcomeActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv3.framework.permission.HPlusPermission;

/**
 * Enrollment Step 2 - SmartPhone communicate to Air Touch.
 * 1) sendPhoneName - send phone's BUILD.NO to Air Touch.
 * 2) getWapiKey -  get Air Touch  key for password encrypt.
 * 3) getWapiDevice - get Air Touch  mac and crc.
 * <p/>
 * Ask user to input device name, city name and home name
 * Store these data to DIYInstallationState
 */
public class SmartlinkConnectTimeoutActivity extends EnrollBaseActivity implements View.OnClickListener {

    private static final String TAG = "ConnectTimeoutActivity";

    private static final String CONTACT_PHONE_NUMBER = "4007204321";

    private TextView mTitleNoUserTextView;

    private TextView mContactServicerText;

    private TextView mUserApModuleText;

    private Button mRetryBtn;

    private Button mContactServerBtn;

    private HPlusPermission mHPlusPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        setContentView(R.layout.activity_smartlink_connecttimeout);
        initView();

    }

    private void initView() {
        mTitleNoUserTextView = (TextView) findViewById(R.id.input_tip_id);
        mTitleNoUserTextView.setVisibility(View.GONE);

        mContactServicerText = (TextView) findViewById(R.id.title_content1_id);


        mUserApModuleText = (TextView) findViewById(R.id.content3_1);
        initClickView(mUserApModuleText, getString(R.string.connect_timeout_content5), 7, 13, 15, 28, new ClickOperator() {
            @Override
            public void dealClick() {

                SmartEnrollScanEntity.getEntityInstance().setFromTimeout(true);
                Intent intent3 = new Intent();
                intent3.setClass(SmartlinkConnectTimeoutActivity.this, EnrollWelcomeActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
                finish();
            }
        });


        mRetryBtn = (Button) findViewById(R.id.retrybtn_id);
        mRetryBtn.setOnClickListener(this);

        mContactServerBtn = (Button) findViewById(R.id.contactbtn_id);
        mContactServerBtn.setOnClickListener(this);

        mHPlusPermission = new HPlusPermission(this);
        mHPlusPermission.requestCallPhonePermission(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initClickView(mContactServicerText, getString(R.string.connect_timeout_content1), 9, 13, 38, 53, new ClickOperator() {
            @Override
            public void dealClick() {
                if (checkPhoneCallPermission()) {
                    callPhone();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.phone_call_permission_deny), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CONTACT_PHONE_NUMBER));
        startActivity(intent);
    }

    private boolean checkPhoneCallPermission() {
        boolean currentPhoneCallPermission = mHPlusPermission.isCallPhonePermissionGranted(this);
        if (currentPhoneCallPermission) {
            return true;
        }
        return false;
    }


    private void initClickView(TextView v, String str, int chineseStart, int chineseEnd, int englishStart, int englishEnd, final ClickOperator clickOperator) {
        SpannableString ssTitle = new SpannableString(str);
        v.setMovementMethod(LinkMovementMethod.getInstance());

        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.enroll_blue2));
                ds.setUnderlineText(false);    //去除超链接的下划线
            }

            @Override
            public void onClick(View widget) {
                clickOperator.dealClick();
            }
        };

        if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
            ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, chineseStart - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(clickableSpan, chineseStart, chineseEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (chineseEnd + 1 < str.length()) {
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), chineseEnd, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


        } else {
            ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, englishStart + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(clickableSpan, englishStart, englishEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (englishEnd + 1 < str.length()) {
                ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), englishStart + 1, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        v.setText(ssTitle);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.contactbtn_id:
                if (checkPhoneCallPermission()) {
                    callPhone();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.phone_call_permission_deny), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.retrybtn_id:

                Intent intent2 = new Intent();
                intent2.setClass(this, SmartlinkInputPasswordActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
                finish();
                break;
        }

    }

    interface ClickOperator {
        public void dealClick();
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.enroll_back_layout:
                goBackActivity();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // when the progress is finding the device , can not be back
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBackActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBackActivity() {
        Intent intent = new Intent(mContext, SmartlinkInputPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }
}
