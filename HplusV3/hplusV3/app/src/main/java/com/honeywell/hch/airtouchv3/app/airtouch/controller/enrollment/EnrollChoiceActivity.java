package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv3.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.lib.util.UmengUtil;

/**
 * Created by Qian Jin on 10/26/15.
 */
public class EnrollChoiceActivity extends EnrollBaseActivity {
    private Button mConfirmButton;
    private LinearLayout mAirPremiumLayout;
    private LinearLayout mAirTouchLayout;
    private ImageView mAirPremiumImageView;
    private ImageView mAirTouchImageView;
    private TextView mAirPremiumTextView;
    private TextView mAirTouchTextView;

    private final static int AIR_TOUCH_NONE = 0;
    private final static int AIR_TOUCH_S = 1;
    private final static int AIR_TOUCH_X = 2;
    private int mSelectedDeviceType = AIR_TOUCH_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollchoice);

        // India version
        if (AppConfig.shareInstance().isIndiaAccount()) {
            DIYInstallationState.setDeviceType(AIR_TOUCH_S);
            Intent i = new Intent();
            i.setClass(EnrollChoiceActivity.this, EnrollWelcomeActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        }

        initView();
        disableConnectButton();
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
            UmengUtil.onEvent(EnrollChoiceActivity.this,
                    UmengUtil.EventType.ENROLL_CANCEL.toString(), "page0");

            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    };

    private void initView() {
        mAirPremiumImageView = (ImageView) findViewById(R.id.air_premium_iv);
        mAirTouchImageView = (ImageView) findViewById(R.id.air_touch_iv);
        mAirPremiumTextView = (TextView) findViewById(R.id.air_premium_tv);
        mAirTouchTextView = (TextView) findViewById(R.id.air_touch_tv);
        mConfirmButton = (Button) findViewById(R.id.confirmBtn);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DIYInstallationState.setDeviceType(mSelectedDeviceType);

                Intent i = new Intent();
                i.setClass(EnrollChoiceActivity.this, EnrollWelcomeActivity.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mAirPremiumLayout = (LinearLayout) findViewById(R.id.air_premium_layout);
        mAirPremiumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedDeviceType != AIR_TOUCH_X)
                    setupDeviceType(AIR_TOUCH_X);
            }
        });
        mAirTouchLayout = (LinearLayout) findViewById(R.id.air_touch_layout);
        mAirTouchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedDeviceType != AIR_TOUCH_S)
                    setupDeviceType(AIR_TOUCH_S);
            }
        });
    }

    private void setupDeviceType(int type) {
        enableConnectButton();

        switch (type) {
            case AIR_TOUCH_S:
                mAirPremiumImageView.setImageResource(R.drawable.air_touch_x_normal);
                mAirTouchImageView.setImageResource(R.drawable.air_touch_s_click);
                mAirPremiumTextView.setAlpha(0.5f);
                mAirTouchTextView.setAlpha(1.0f);
                break;

            case AIR_TOUCH_X:
                mAirPremiumImageView.setImageResource(R.drawable.air_touch_x_click);
                mAirTouchImageView.setImageResource(R.drawable.air_touch_s_normal);
                mAirPremiumTextView.setAlpha(1.0f);
                mAirTouchTextView.setAlpha(0.5f);
                break;
        }

        mSelectedDeviceType = type;
    }

    private void disableConnectButton() {
        mConfirmButton.setClickable(false);
        mConfirmButton.setTextColor(getResources().getColor(R.color.enroll_light_grey));
        mConfirmButton.setBackgroundResource(R.drawable.enroll_back_button);
    }

    private void enableConnectButton() {
        mConfirmButton.setClickable(true);
        mConfirmButton.setTextColor(getResources().getColor(R.color.white));
        mConfirmButton.setBackgroundResource(R.drawable.enroll_next_button);
    }

}
