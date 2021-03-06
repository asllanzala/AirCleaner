package com.honeywell.hch.airtouchv3.app.authorize.controller;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.EnrollAccessManager;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.ChangePasswordRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.RecordCreatedResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.UserLoginResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.view.AirTouchEditText;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class MobileDoneActivity extends BaseActivity {
    private final String TAG = "MobileDoneActivity";
    private TextView resetPasswordTitle;
    private ImageView photoImageView;
    private FrameLayout backFramelayout;
    private AirTouchEditText userNickname;
    private AirTouchEditText userPassword;
    private AirTouchEditText userPasswordConfirm;
    private ImageView lineView;
    private Button doneButton;
    private String mMobilePhone;
    private String mCountryCode;
    private static Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_done);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        decideButtonStatus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            if (getIntent().getBooleanExtra("isChangePassword", false)) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            } else {
                Intent i = new Intent();
                if (getIntent().getBooleanExtra(AirTouchConstants.FORGET_PASSWORD, false)) {
                    i.putExtra(AirTouchConstants.FORGET_PASSWORD, true);
                } else if (getIntent().getBooleanExtra(AirTouchConstants.NEW_USER, false)) {
                    i.putExtra(AirTouchConstants.NEW_USER, true);
                }
                i.putExtra(AirTouchConstants.MOBILE_DONE_BACK, true);
                i.putExtra("phoneNumber", mMobilePhone);
                i.putExtra("countryCode", mCountryCode);
                i.setClass(MobileDoneActivity.this, MobileVerifySmsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        }

        return false;
    }

    private void initView() {
        ColorStateList csl1 = (ColorStateList) getResources().getColorStateList(R.color.white);
        ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.login_hint_text);
        backFramelayout = (FrameLayout) findViewById(R.id.mobile_done_back_layout);
        backFramelayout.setOnClickListener(backOnClick);
        userNickname = (AirTouchEditText) findViewById(R.id.mobile_done_nick);
        userPassword = (AirTouchEditText) findViewById(R.id.mobile_done_password);
        userPasswordConfirm = (AirTouchEditText) findViewById(R.id.mobile_done_confirm_password);
        userNickname.getEditText().setTextColor(csl1);
        userNickname.getEditText().setHintTextColor(csl2);

        if (getIntent().getBooleanExtra("isChangePassword", false)) {
            userNickname.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            userNickname.setInputMaxLength(30);

        } else {
            userNickname.setInputMaxLength(9);
        }
        userPassword.getEditText().setTextColor(csl1);
        userPassword.getEditText().setHintTextColor(csl2);
        userPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        userPasswordConfirm.getEditText().setTextColor(csl1);
        userPasswordConfirm.getEditText().setHintTextColor(csl2);
        userPasswordConfirm.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        userNickname.getEditText().addTextChangedListener(mEditTextWatch);
        userPassword.getEditText().addTextChangedListener(mEditTextWatch);
        userPasswordConfirm.getEditText().addTextChangedListener(mEditTextWatch);
        userNickname.getEditText().setGravity(Gravity.CENTER);
        userPassword.getEditText().setGravity(Gravity.CENTER);
        userPasswordConfirm.getEditText().setGravity(Gravity.CENTER);
        userNickname.setImage(AirTouchEditText.ComponentType.CLEAN);
        userPassword.setImage(AirTouchEditText.ComponentType.PASSWORD);
        userPasswordConfirm.setImage(AirTouchEditText.ComponentType.CLEAN);
        lineView = (ImageView) findViewById(R.id.line_confirm_password);

        userPassword.setInputMaxLength(30);
        userPasswordConfirm.setInputMaxLength(30);
        doneButton = (Button) findViewById(R.id.mobile_done);
        doneButton.setOnClickListener(doneOnClick);
        resetPasswordTitle = (TextView) findViewById(R.id.reset_password_title);
        photoImageView = (ImageView) findViewById(R.id.user_photo);
        mCountryCode = getIntent().getStringExtra("countryCode");
        mMobilePhone = getIntent().getStringExtra("phoneNumber");
        if (getIntent().getBooleanExtra("isChangePassword", false)) {
            userNickname.setEditorHint(getString(R.string.old_password));
            userPassword.setEditorHint(getString(R.string.new_password));
            resetPasswordTitle.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.INVISIBLE);
        }
    }

    View.OnClickListener backOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getIntent().getBooleanExtra("isChangePassword", false)) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            } else {
                Intent i = new Intent();
                if (getIntent().getBooleanExtra(AirTouchConstants.FORGET_PASSWORD, false)) {
                    i.putExtra(AirTouchConstants.FORGET_PASSWORD, true);
                } else if (getIntent().getBooleanExtra(AirTouchConstants.NEW_USER, false)) {
                    i.putExtra(AirTouchConstants.NEW_USER, true);
                }
                i.putExtra(AirTouchConstants.MOBILE_DONE_BACK, true);
                i.putExtra("phoneNumber", mMobilePhone);
                i.putExtra("countryCode", mCountryCode);
                i.setClass(MobileDoneActivity.this, MobileVerifySmsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        }
    };

    View.OnClickListener doneOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isUserInputValidate()) {
                String userId = AppManager.shareInstance().getAuthorizeApp().getUserID();
                String sessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
                String nickname = userNickname.getEditorText();
                String password = userPassword.getEditorText();

                mDialog = LoadingProgressDialog.show(MobileDoneActivity.this, getString(R.string.enroll_loading));
                if (getIntent().getBooleanExtra("isChangePassword", false)) {
                    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(nickname, password);
                    TccClient.sharedInstance().changePassword(userId, sessionId,
                            changePasswordRequest, receivedResponse);
                } else {
                    UserRegisterRequest userRegisterRequest
                            = new UserRegisterRequest(nickname, mMobilePhone, password, mCountryCode);
                    TccClient.sharedInstance().userRegister(userRegisterRequest, receivedResponse);
                }
            }
        }
    };

    IReceiveResponse receivedResponse = new IReceiveResponse() {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            if (mDialog != null) {
                mDialog.dismiss();
            }

            switch (httpRequestResponse.getRequestID()) {
                case USER_REGISTER:
                    if ((httpRequestResponse.getStatusCode() == StatusCode.OK)
                            || (httpRequestResponse.getStatusCode() == StatusCode.CREATE_OK)) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            RecordCreatedResponse recordCreatedResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    RecordCreatedResponse.class);

                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Register Success, userId："
                                    + recordCreatedResponse.getId());

                            // Umeng statistic
                            MobclickAgent.onEvent(MobileDoneActivity.this, "register_success");

                            userLogin();
                        } else {
                            errorHandle(httpRequestResponse);
                        }
                    } else if (httpRequestResponse.getStatusCode() == StatusCode.BAD_REQUEST) {
                        MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                                getString(R.string.already_registed), null, null);
                        return;
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                case USER_LOGIN:
                    if ((httpRequestResponse.getStatusCode() == StatusCode.OK)
                            || (httpRequestResponse.getStatusCode() == StatusCode.CREATE_OK)) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            UserLoginResponse userLoginResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    UserLoginResponse.class);

                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Login Success, userId："
                                    + userLoginResponse.getUserInfo().getUserID());

                            // Save user data
                            AppManager.shareInstance().getAuthorizeApp().saveUserInfo
                                    (userNickname.getEditorText(), mMobilePhone, userPassword.getEditorText(),
                                            userLoginResponse.getUserInfo().getUserID(),
                                            userLoginResponse.getSessionId(),
                                            userLoginResponse.getUserInfo().getCountryPhoneNum());

                            //send loginChanged Broadcast
                            Intent boradIntent = new Intent(AirTouchConstants.AFTER_USER_LOGIN);
                            HPlusApplication.getInstance().getApplicationContext().sendBroadcast(boradIntent);

                            if (AppManager.shareInstance().getAuthorizeApp().isUserWantToEnroll()) {
                                finish();
                                EnrollAccessManager.startIntent(MobileDoneActivity.this, "");
                            } else {
                                finish();
                                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                            }
                        } else {
                            errorHandle(httpRequestResponse);
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                case CHANGE_PASSWORD:
                    if ((httpRequestResponse.getStatusCode() == StatusCode.OK)
                            || (httpRequestResponse.getStatusCode() == StatusCode.CREATE_OK)
                            || (httpRequestResponse.getStatusCode() == StatusCode.SMS_OK)) {
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Change password Success");

                        AppManager.shareInstance().getAuthorizeApp().saveNewPassword(userPassword.getEditorText());

                        MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                                getString(R.string.change_password_success), null, finishAndQuit);

                    } else if (httpRequestResponse.getStatusCode() == StatusCode.BAD_REQUEST) {
                        MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                                getString(R.string.change_password_invalid), null, null);
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void userLogin() {
        UserLoginRequest userLoginRequest = new UserLoginRequest(mMobilePhone,
                userPassword.getEditorText(), AppConfig.APPLICATION_ID);
        TccClient.sharedInstance().userLogin(userLoginRequest, receivedResponse);
    }


    private TextWatcher mEditTextWatch = new TextWatcher() {

        private Drawable Drawable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            StringUtil.specialCharacterFilter(userNickname);
            StringUtil.specialCharacterFilter(userPassword);
            StringUtil.specialCharacterFilter(userPasswordConfirm);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (userPassword.getCleanImg().getDrawable().isVisible()) {
                decidePasswordEditTextStatus();
            }

            decideButtonStatus();
        }

        private void decidePasswordEditTextStatus() {
            if (userPassword.getEditText().getInputType() == InputType.TYPE_CLASS_TEXT) {
                userPasswordConfirm.setVisibility(View.INVISIBLE);
                lineView.setVisibility(View.INVISIBLE);
            } else {
                userPasswordConfirm.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
            }
        }
    };

    private boolean isUserInputValidate() {
//        // Nickname does not match rules
//        if (!getIntent().getBooleanExtra("isChangePassword", false)) {
//            Matcher nickMatcher = Pattern.compile("[a-zA-Z0-9_]{4,20}").matcher(userNickname.getEditorText());
//            if (nickMatcher.matches() == false) {
//                MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
//                        getString(R.string.nickname_format_wrong), null, null);
//                return false;
//            }
//        }

        //Password is valid
//        Matcher passwordMatcher = Pattern.compile("(?=.*?\\d)(?=.*?[a-z])(?=.*?[A-Z]).{3," +
//                "}").matcher(userPassword.getEditorText());
        Matcher passwordMatcher = Pattern.compile("^[A-z0-9]{6,30}+$").matcher(userPassword.getEditorText());
        // Password match
        if (lineView.getVisibility() == View.VISIBLE) {
            if (!userPassword.getEditorText().equals(userPasswordConfirm.getEditorText())) {
                MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                        getString(R.string.password_not_match), null, null);
                return false;
            }
        }

        // Password is at least 6 characters
        if (userPassword.getEditorText().length() < AirTouchConstants.MIN_USER_PASSWORD) {
            MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                    getString(R.string.password_length_wrong), null, null);
            return false;
        }

        // Password does not match rules
        if (passwordMatcher.matches() == false) {
            MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                    getString(R.string.password_format_wrong), null, null);
            return false;
        }

        return true;
    }


    private void decideButtonStatus() {
        if ((userNickname.getEditorText().isEmpty())
                || (userPassword.getEditorText().isEmpty())
                || ((userPasswordConfirm.getEditorText().isEmpty()) && (lineView.getVisibility() == View.VISIBLE))) {
            disableRegisterButton();
        } else {
            enableRegisterButton();
        }

    }

    private void disableRegisterButton() {
        doneButton.setClickable(false);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.login_button_text);
        doneButton.setTextColor(csl);
        doneButton.setBackgroundResource(R.color.login_button_background);

    }

    private void enableRegisterButton() {
        doneButton.setClickable(true);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.white);
        doneButton.setTextColor(csl);
        doneButton.setBackgroundResource(R.drawable.enroll_next_button);
    }

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
            MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                    getString(R.string.no_network), null, null);
            return;
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());

                MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                        getString(R.string.enroll_error), null, null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.createSimpleDialog(MobileDoneActivity.this, null,
                    getString(R.string.enroll_error), null, null);
        }
    }

    private MessageBox.MyOnClick finishAndQuit = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    };
}