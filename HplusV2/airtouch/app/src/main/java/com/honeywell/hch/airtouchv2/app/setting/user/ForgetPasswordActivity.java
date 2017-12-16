package com.honeywell.hch.airtouchv2.app.setting.user;

import android.app.ProgressDialog;
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

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.app.authorize.controller.MobileVerifySmsActivity;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.config.UserConfig;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UpdatePasswordRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.honeywell.hch.airtouchv2.framework.view.AirTouchEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class ForgetPasswordActivity extends BaseActivity {
    private FrameLayout backFramelayout;
    private AirTouchEditText userPassword;
    private AirTouchEditText userPasswordConfirm;
    private ImageView lineView;
    private Button doneButton;
    private String mMobilePhone;
    private static ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

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
        backFramelayout = (FrameLayout) findViewById(R.id.mobile_done_back_layout);
        backFramelayout.setOnClickListener(backOnClick);
        userPassword = (AirTouchEditText) findViewById(R.id.mobile_done_password);
        userPasswordConfirm = (AirTouchEditText) findViewById(R.id.mobile_done_confirm_password);
        userPassword.getEditText().setTextColor(csl1);
        userPassword.getEditText().setHintTextColor(csl2);
        userPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        userPasswordConfirm.getEditText().setTextColor(csl1);
        userPasswordConfirm.getEditText().setHintTextColor(csl2);
        userPasswordConfirm.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        userPassword.getEditText().addTextChangedListener(mEditTextWatch);
        userPasswordConfirm.getEditText().addTextChangedListener(mEditTextWatch);
        userPassword.getEditText().setGravity(Gravity.CENTER);
        userPasswordConfirm.getEditText().setGravity(Gravity.CENTER);
        userPassword.setImage(AirTouchEditText.ComponentType.PASSWORD);
        userPasswordConfirm.setImage(AirTouchEditText.ComponentType.CLEAN);
        userPassword.setInputMaxLength(30);
        userPasswordConfirm.setInputMaxLength(30);
        lineView = (ImageView) findViewById(R.id.line_confirm_password);
        doneButton = (Button) findViewById(R.id.mobile_done);
        doneButton.setOnClickListener(doneOnClick);
        mMobilePhone = getIntent().getStringExtra("phoneNumber");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            Intent i = new Intent();
            i.putExtra("forgetPassword", true);
            i.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
            i.setClass(ForgetPasswordActivity.this, MobileVerifySmsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            finish();
        }

        return false;
    }

    View.OnClickListener backOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.putExtra("forgetPassword", true);
            i.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
            i.setClass(ForgetPasswordActivity.this, MobileVerifySmsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            finish();
        }
    };

    View.OnClickListener doneOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isUserInputValidate()) {
//                String username = AuthorizeApp.shareInstance().getMobilePhone();
                String newPassword = userPassword.getEditorText();

                mDialog = ProgressDialog.show(ForgetPasswordActivity.this, null, getString(R.string.enroll_loading));
                UpdatePasswordRequest resetPasswordRequest = new UpdatePasswordRequest(mMobilePhone, newPassword);
                TccClient.sharedInstance().updatePassword(resetPasswordRequest, receivedResponse);
            }
        }
    };

    IReceiveResponse receivedResponse = new IReceiveResponse() {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            switch (httpRequestResponse.getRequestID()) {
                case UPDATE_PASSWORD:
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    if (httpRequestResponse.getStatusCode() == StatusCode.SMS_OK) {
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Reset Success!");
                        UserConfig userConfig = new UserConfig(ForgetPasswordActivity.this);
                        userConfig.saveNewPassword(userPassword.getEditorText());
                        // Save user data to database
//                        UserDBService mUserDBService = new UserDBService(ForgetPasswordActivity.this);
//                        mUserDBService.insertUser(new User
//                                (AuthorizeApp.shareInstance().getUserID(),
//                                        AuthorizeApp.shareInstance().getMobilePhone(),
//                                        userPassword.getEditorText(),
//                                        AuthorizeApp.shareInstance().getNickname(), 1));

                        finish();
                        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                default:
                    break;

            }

            enableRegisterButton();
        }
    };


    private TextWatcher mEditTextWatch = new TextWatcher() {

        private Drawable Drawable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

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
        //Password is valid
        Matcher passwordMatcher = Pattern.compile("(?=.*?\\d)(?=.*?[a-z])(?=.*?[A-Z]).{3," +
                "}").matcher(userPassword.getEditorText());

        // Password match
        if (lineView.getVisibility() == View.VISIBLE) {
            if (!userPassword.getEditorText().equals(userPasswordConfirm.getEditorText())) {
                MessageBox.createSimpleDialog(ForgetPasswordActivity.this, null,
                        getString(R.string.password_not_match), null, null);
                return false;
            }
        }

        // Password is at least 8 characters
        if (userPassword.getEditorText().length() < 8) {
            MessageBox.createSimpleDialog(ForgetPasswordActivity.this, null,
                    getString(R.string.password_length_wrong), null, null);
            return false;
        }

        // Password does not match rules
        if (passwordMatcher.matches() == false) {
            MessageBox.createSimpleDialog(ForgetPasswordActivity.this, null,
                    getString(R.string.password_format_wrong), null, null);
            return false;
        }

        return true;
    }



    private void decideButtonStatus() {
        if ((userPassword.getEditorText().isEmpty())
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
            MessageBox.createSimpleDialog(ForgetPasswordActivity.this, null,
                    getString(R.string.no_network), null, null);
            return;
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
//                MessageBox.createSimpleDialog(ForgetPasswordActivity.this, null,
//                        errorResponse.getMessage(), null, null);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
                MessageBox.createSimpleDialog(ForgetPasswordActivity.this, null,
                        getString(R.string.enroll_error), null, null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.createSimpleDialog(ForgetPasswordActivity.this, null,
                    getString(R.string.enroll_error), null, null);
        }
    }

}