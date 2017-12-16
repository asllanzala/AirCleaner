package com.honeywell.hch.airtouchv3.app.authorize.controller;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.app.authorize.model.CountryCodeAdapter;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.manual.ManualActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.SmsValidRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.SmsSendResponse;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.honeywell.hch.airtouchv3.framework.view.AirTouchEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class MobileGetSmsActivity extends BaseActivity {
    private TextView mResetPasswordTitle;
    private TextView mPasswordGuide;
    private ImageView mPhotoImageView;
    private CheckBox mAgreementCheckBox;
    private TextView mAgreementTextView;
    private FrameLayout mBackFramelayout;
    private AirTouchEditText mMobilePhoneNumber;
    private Button mGetSmsButton;
    private static Dialog mDialog;
    private Spinner mCountryCodeSpinner;
    private CountryCodeAdapter<String> countryCodeSpinnerTypeAdapter;
    private String[] mCountryCodeTypes = {AirTouchConstants.CHINA_CODE, AirTouchConstants.INDIA_CODE};
    private String mCountryCode;

    private final String DATABASE_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/HoneyWell";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_get_sms);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        decideButtonStatus();
    }

    private void initView() {
        ColorStateList csl1 = (ColorStateList) getResources().getColorStateList(R.color.white);
        ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.login_hint_text);

        mBackFramelayout = (FrameLayout) findViewById(R.id.mobile_get_sms_back_layout);
        mBackFramelayout.setOnClickListener(backOnClick);
        mAgreementCheckBox = (CheckBox) findViewById(R.id.agree_terms);
        mAgreementCheckBox.setChecked(true);
        mAgreementCheckBox.setOnClickListener(agreementOnClick);
        mAgreementTextView = (TextView) findViewById(R.id.agree_terms_tv);
        mAgreementTextView.setOnClickListener(agreementTextOnClick);
        mPasswordGuide = (TextView) findViewById(R.id.password_guide);
        mMobilePhoneNumber = (AirTouchEditText) findViewById(R.id.sms_number_edit_text);
        mMobilePhoneNumber.getEditText().setTextColor(csl1);
        mMobilePhoneNumber.getEditText().setHintTextColor(csl2);
        mMobilePhoneNumber.getEditText().setGravity(Gravity.CENTER);
        mMobilePhoneNumber.setImage(AirTouchEditText.ComponentType.CLEAN);
        mMobilePhoneNumber.getEditText().addTextChangedListener(mEditTextWatch);
        mMobilePhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        mMobilePhoneNumber.setInputMaxLength(20);
        mGetSmsButton = (Button) findViewById(R.id.mobile_get_sms_button);
        mGetSmsButton.setOnClickListener(getSmsOnClick);
        mResetPasswordTitle = (TextView) findViewById(R.id.reset_password_title);
        mPhotoImageView = (ImageView) findViewById(R.id.user_photo);
        mCountryCodeSpinner = (Spinner) findViewById(R.id.country_code_spinner);
        setupCountryCodeSpinner();

        if (getIntent().getBooleanExtra(AirTouchConstants.FORGET_PASSWORD, false)) {
            mResetPasswordTitle.setVisibility(View.VISIBLE);
            mPhotoImageView.setVisibility(View.INVISIBLE);
            mPasswordGuide.setVisibility(View.INVISIBLE);
            mAgreementCheckBox.setVisibility(View.INVISIBLE);
            mAgreementTextView.setVisibility(View.INVISIBLE);
        }
        if (AppConfig.isDebugMode) {
            ImageView userPhoto = (ImageView) findViewById(R.id.user_photo);
            userPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.putExtra("countryCode", mCountryCode);
                    i.putExtra("phoneNumber", mMobilePhoneNumber.getEditorText());
                    i.setClass(MobileGetSmsActivity.this, MobileVerifySmsActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            Intent i = new Intent();
            i.setClass(MobileGetSmsActivity.this, UserLoginActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        return false;
    }

    private TextWatcher mEditTextWatch = new TextWatcher() {

        private Drawable Drawable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            int a = 0;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int b = 0;
            StringUtil.isAlaboNumeric(mMobilePhoneNumber.getEditText());
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
            i.setClass(MobileGetSmsActivity.this, UserLoginActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    };

    View.OnClickListener getSmsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isPhoneNumberValid()) {
                mDialog = LoadingProgressDialog.show(MobileGetSmsActivity.this, getString(R.string.sending_sms));
                SmsValidRequest smsValidRequest
                        = new SmsValidRequest(0, mMobilePhoneNumber.getEditorText(), "", mCountryCode);
                TccClient.sharedInstance().getSmsCode(smsValidRequest, receivedResponse);
//                TccClient.sharedInstance().getSmsCode(smsValidRequest, null);
            }
        }
    };

    private boolean isPhoneNumberValid() {
        Matcher smsMatcher = Patterns.PHONE.matcher(mMobilePhoneNumber.getEditorText());
        if (smsMatcher.matches() == false) {
            MessageBox.createSimpleDialog(MobileGetSmsActivity.this, null,
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
                case GET_SMS_CODE:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            SmsSendResponse smsSendResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    SmsSendResponse.class);
                            if (smsSendResponse.isSend()) {
                                Intent i = new Intent();
                                if (getIntent().getBooleanExtra(AirTouchConstants.FORGET_PASSWORD, false)) {
                                    i.putExtra(AirTouchConstants.FORGET_PASSWORD, true);
                                } else if (getIntent().getBooleanExtra(AirTouchConstants.NEW_USER, false)) {
                                    i.putExtra(AirTouchConstants.NEW_USER, true);
                                }
                                i.putExtra("phoneNumber", mMobilePhoneNumber.getEditorText());
                                i.putExtra("countryCode", mCountryCode);
                                i.setClass(MobileGetSmsActivity.this, MobileVerifySmsActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();
                            } else {
                                MessageBox.createSimpleDialog(MobileGetSmsActivity.this, null,
                                        getString(R.string.sms_send_fail), null, null);
                            }
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    View.OnClickListener agreementOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            decideButtonStatus();
        }
    };

    View.OnClickListener agreementTextOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.setClass(MobileGetSmsActivity.this, ManualActivity.class);
            i.putExtra("eula", true);
            i.putExtra("countryCode", mCountryCode);
            startActivity(i);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    };

    private void decideButtonStatus() {
        if (mMobilePhoneNumber.getEditorText().isEmpty()) {
            disableRegisterButton();
            return;
        }

        // India version
        if (mCountryCode.equals(AirTouchConstants.CHINA_CODE)
                && (mMobilePhoneNumber.getEditorText().length() != 11)) {
            disableRegisterButton();
            return;
        }
        if (mCountryCode.equals(AirTouchConstants.INDIA_CODE)
            && (mMobilePhoneNumber.getEditorText().length() != 10)) {
            disableRegisterButton();
            return;
        }

        if (!mAgreementCheckBox.isChecked()
                && !getIntent().getBooleanExtra(AirTouchConstants.FORGET_PASSWORD, false)) {
            disableRegisterButton();
            return;
        }

        enableRegisterButton();
    }

    private void disableRegisterButton() {
        mGetSmsButton.setClickable(false);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.login_button_text);
        mGetSmsButton.setTextColor(csl);
        mGetSmsButton.setBackgroundResource(R.color.login_button_background);
    }

    private void enableRegisterButton() {
        mGetSmsButton.setClickable(true);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.white);
        mGetSmsButton.setTextColor(csl);
        mGetSmsButton.setBackgroundResource(R.drawable.enroll_next_button);
    }

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
            MessageBox.createSimpleDialog(MobileGetSmsActivity.this, null,
                    getString(R.string.no_network), null, null);
            return;
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
//                MessageBox.createSimpleDialog(MobileGetSmsActivity.this, null,
//                        errorResponse.getMessage(), null, null);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
                MessageBox.createSimpleDialog(MobileGetSmsActivity.this, null,
                        getString(R.string.enroll_error), null, null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.createSimpleDialog(MobileGetSmsActivity.this, null,
                    getString(R.string.enroll_error), null, null);
        }
    }

    private void setMobileMaxLength() {
        // India version
        switch (mCountryCode) {
            case AirTouchConstants.CHINA_CODE:
                mMobilePhoneNumber.setInputMaxLength(11);
                break;
            case AirTouchConstants.INDIA_CODE:
                mMobilePhoneNumber.setInputMaxLength(10);
                break;
            default:
                mMobilePhoneNumber.setInputMaxLength(20);
                break;
        }
    }

    private void setupCountryCodeSpinner() {
        countryCodeSpinnerTypeAdapter = new CountryCodeAdapter<>(this, mCountryCodeTypes);
        mCountryCodeSpinner.setAdapter(countryCodeSpinnerTypeAdapter);
        mCountryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMobilePhoneNumber.setEditorText("");
                mCountryCode = countryCodeSpinnerTypeAdapter
                        .getItemValue(countryCodeSpinnerTypeAdapter.getItem(position), MobileGetSmsActivity.this);
                // India version
                if (mCountryCode.equals(AirTouchConstants.CHINA_CODE)) {
                    mAgreementCheckBox.setChecked(true);
                } else {
                    mAgreementCheckBox.setChecked(false);
                }
                setMobileMaxLength();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // India version
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
        if (authorizeApp.getCountryCode() != null) {
            if (AppConfig.shareInstance().isIndiaAccount()) {
                mCountryCodeSpinner.setSelection(1);
            } else {
                mCountryCodeSpinner.setSelection(0);
            }
        } else if (AppConfig.shareInstance().isLocatedInIndia()) {
            mCountryCodeSpinner.setSelection(1);
        } else {
            mCountryCodeSpinner.setSelection(0);
        }
    }
}
