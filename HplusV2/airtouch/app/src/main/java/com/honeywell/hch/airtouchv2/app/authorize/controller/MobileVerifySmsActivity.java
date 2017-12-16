package com.honeywell.hch.airtouchv2.app.authorize.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.app.setting.user.ForgetPasswordActivity;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.SmsValidRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.SmsValidResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.honeywell.hch.airtouchv2.framework.view.AirTouchEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class MobileVerifySmsActivity extends BaseActivity {
    private TextView resetPasswordTitle;
    private ImageView photoImageView;
    private FrameLayout backFramelayout;
    private AirTouchEditText mobilePhoneNumber;
    private EditText verifyCode;
    private TextView mResendTextView;
    private Button verifySmsButton;
    private static ProgressDialog mDialog;
    private Timer timer;
    private TimerTask timerTask;
    private int timerCount;
    private static final int TIME_COUNT = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verify_sms);

        initView();
        initTimer();

    }

    @Override
    protected void onResume() {
        super.onResume();

        decideButtonStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            Intent i = new Intent();
            if (getIntent().getBooleanExtra("forgetPassword", false)) {
                i.putExtra("forgetPassword", true);
            }
            i.setClass(MobileVerifySmsActivity.this, MobileGetSmsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            finish();
        }

        return false;
    }

    private void initView() {
        ColorStateList csl1 = (ColorStateList) getResources().getColorStateList(R.color.white);
        ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.login_hint_text);

        backFramelayout = (FrameLayout) findViewById(R.id.verify_sms_back_layout);
        backFramelayout.setOnClickListener(backOnClick);
        mobilePhoneNumber = (AirTouchEditText) findViewById(R.id.verify_sms_number);
        mobilePhoneNumber.getEditText().setTextColor(csl1);
        mobilePhoneNumber.getEditText().setHintTextColor(csl2);
        mobilePhoneNumber.getEditText().setGravity(Gravity.CENTER);
        mobilePhoneNumber.setImage(AirTouchEditText.ComponentType.CLEAN);
        mobilePhoneNumber.setEditorText(getIntent().getStringExtra("phoneNumber"));
        mobilePhoneNumber.getEditText().setEnabled(false);
        mobilePhoneNumber.setInputMaxLength(20);
        verifyCode = (EditText) findViewById(R.id.verify_sms_code);
        verifyCode.setTextColor(csl1);
        verifyCode.setHintTextColor(csl2);
        verifyCode.addTextChangedListener(mEditTextWatch);
        verifySmsButton = (Button) findViewById(R.id.verify_sms_button);
        verifySmsButton.setOnClickListener(verifySmsOnClick);
        mResendTextView = (TextView) findViewById(R.id.sms_resend);
        mResendTextView.setText(getString(R.string.sms_resend) + " " + TIME_COUNT);
        mResendTextView.setClickable(false);
        mResendTextView.setOnClickListener(resendOnClick);
        resetPasswordTitle = (TextView) findViewById(R.id.reset_password_title);
        photoImageView = (ImageView) findViewById(R.id.user_photo);
        if (getIntent().getBooleanExtra("forgetPassword", false)) {
            resetPasswordTitle.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.INVISIBLE);
        }
        if (AppConfig.isDebugMode) {
            ImageView userPhoto = (ImageView)findViewById(R.id.user_photo);
            userPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(MobileVerifySmsActivity.this, MobileDoneActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });
        }

    }

    private void initTimer() {
        timerCount = TIME_COUNT;
        timerTask = new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                timerCount--;
                msg.what = timerCount;
                handler.sendMessage(msg);
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            ColorStateList csl1 = (ColorStateList) getResources().getColorStateList(R.color.enroll_blue);
            ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.enroll_light_grey);

            mResendTextView.setText(getString(R.string.sms_resend) + " " + msg.what);
            mResendTextView.setTextColor(csl2);
            mResendTextView.setClickable(false);

            if (timerCount == 0) {
                mResendTextView.setText(getString(R.string.sms_resend));
                mResendTextView.setTextColor(csl1);
                mResendTextView.setClickable(true);

                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (timerTask != null) {
                    timerTask = null;
                }
            }
        };
    };

    private TextWatcher mEditTextWatch = new TextWatcher() {

        private android.graphics.drawable.Drawable Drawable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            decideButtonStatus();
        }

    };

    View.OnClickListener backOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            if (getIntent().getBooleanExtra("forgetPassword", false)) {
                i.putExtra("forgetPassword", true);
            }
            i.setClass(MobileVerifySmsActivity.this, MobileGetSmsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            finish();
        }
    };

    View.OnClickListener resendOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isPhoneNumberValid()) {
                mDialog = ProgressDialog.show(MobileVerifySmsActivity.this,
                        null, getString(R.string.enroll_loading));
                SmsValidRequest smsValidRequest
                        = new SmsValidRequest(0, mobilePhoneNumber.getEditorText(),"");
                TccClient.sharedInstance().getSmsCode(smsValidRequest, receivedResponse);
            }

        }
    };

    View.OnClickListener verifySmsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isPhoneNumberValid()) {
                mDialog = ProgressDialog.show(MobileVerifySmsActivity.this, null, getString(R.string.enroll_loading));
                String phoneNum = mobilePhoneNumber.getEditorText();
                String smsNum = verifyCode.getText().toString();
                TccClient.sharedInstance().verifySmsCode(phoneNum, smsNum, null, receivedResponse);
            }
        }
    };


    private boolean isPhoneNumberValid() {
        Matcher smsMatcher = Patterns.PHONE.matcher(mobilePhoneNumber.getEditorText());
        if (smsMatcher.matches() == false) {
            MessageBox.createSimpleDialog(MobileVerifySmsActivity.this, null,
                    getString(R.string.phone_format_wrong), null, null);
            return false;
        }

        return true;
    }

    IReceiveResponse receivedResponse = new IReceiveResponse() {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            if (mDialog != null) {
                mDialog.dismiss();
            }

            switch (httpRequestResponse.getRequestID()) {
                case VERIFY_SMS_VALID:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            SmsValidResponse smsValidResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    SmsValidResponse.class);

                            if (smsValidResponse.isValid()) {
                                Intent i = new Intent();
                                i.putExtra("phoneNumber", mobilePhoneNumber.getEditorText());
                                if (getIntent().getBooleanExtra("forgetPassword", false)) {
                                    i.setClass(MobileVerifySmsActivity.this, ForgetPasswordActivity.class);
                                    startActivity(i);
                                } else {
                                    i.setClass(MobileVerifySmsActivity.this, MobileDoneActivity.class);
                                    startActivity(i);
                                }
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();
                            } else {
                                MessageBox.createSimpleDialog(MobileVerifySmsActivity.this, null,
                                        getString(R.string.sms_code_wrong), null, null);
                            }
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                case GET_SMS_CODE:
                    if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                        initTimer();
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, httpRequestResponse.getData());
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void decideButtonStatus() {
        if ((mobilePhoneNumber.getEditorText().isEmpty())
                || (verifyCode.getText().toString().equals(""))) {
            disableRegisterButton();
        } else {
            enableRegisterButton();
        }

    }

    private void disableRegisterButton() {
        verifySmsButton.setClickable(false);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.login_button_text);
        verifySmsButton.setTextColor(csl);
        verifySmsButton.setBackgroundResource(R.color.login_button_background);

    }

    private void enableRegisterButton() {
        verifySmsButton.setClickable(true);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.white);
        verifySmsButton.setTextColor(csl);
        verifySmsButton.setBackgroundResource(R.drawable.enroll_next_button);
    }

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
            MessageBox.createSimpleDialog(MobileVerifySmsActivity.this, null,
                    getString(R.string.enroll_error), null, null);
            return;
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
//                MessageBox.createSimpleDialog(MobileVerifySmsActivity.this, null,
//                        errorResponse.getMessage(), null, null);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
                MessageBox.createSimpleDialog(MobileVerifySmsActivity.this, null,
                        getString(R.string.enroll_error), null, null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.createSimpleDialog(MobileVerifySmsActivity.this, null,
                    getString(R.string.enroll_error), null, null);
        }
    }

}
