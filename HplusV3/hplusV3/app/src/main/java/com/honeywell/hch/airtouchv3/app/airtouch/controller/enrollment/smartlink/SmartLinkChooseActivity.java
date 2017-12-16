package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.enrollment.activity.EnrollBaseActivity;

/**
 * Created by Vincent on 25/11/15.
 */
public class SmartLinkChooseActivity extends EnrollBaseActivity {
    private TextView mTitleVIewCn;
    private Context mContext;
    private ImageView mMachineView;
    private Button mNextButton;
    private final long delayTime = 3800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlink_choose);
        initView();
    }

    private void initView() {
        mContext = this;
        mTitleVIewCn = (TextView) findViewById(R.id.enroll_choose_please_tv_cn);
        mMachineView = (ImageView) findViewById(R.id.enroll_choose_machine_image);
        mNextButton = (Button)findViewById(R.id.enroll_choose_nextBtn);
        mNextButton.setClickable(false);
        mNextButton.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mNextButton.setClickable(true);
                mNextButton.setEnabled(true);
            }
        }, delayTime);
        initTextandImage();
    }

    private void initTextandImage() {
        SpannableString ssTitle = new SpannableString(getString(R.string.smart_choose_title));
        if (AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)) {
            ssTitle.setSpan(new RelativeSizeSpan(1.3f), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new RelativeSizeSpan(1.3f), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 10, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 3, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 12, ssTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ssTitle.setSpan(new RelativeSizeSpan(1.3f), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new RelativeSizeSpan(1.3f), 42, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.enroll_blue2)), 42, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 14, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssTitle.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 51, ssTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mTitleVIewCn.setText(ssTitle);
        mMachineView.setImageResource(SmartEnrollScanEntity.getEntityInstance().getDeviceImage());
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.enroll_choose_nextBtn:
                gotoActivity(SmartlinkInputPasswordActivity.class,false);
                break;
            case R.id.enroll_choose_back_layout:
                gotoActivity(SmartLinkEnrollScanActivity.class,true);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // when the progress is finding the device , can not be back
        if(keyCode == KeyEvent.KEYCODE_BACK ) {
            gotoActivity(SmartLinkEnrollScanActivity.class,true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void gotoActivity( Class<?> cls,boolean isBack){
        Intent intent2 = new Intent(mContext, cls);
        startActivity(intent2);
        if (isBack){
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
        else
        {
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
        finish();
    }

}
