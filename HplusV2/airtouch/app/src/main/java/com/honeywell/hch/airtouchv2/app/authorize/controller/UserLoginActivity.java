package com.honeywell.hch.airtouchv2.app.authorize.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.ATApplication;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment.EnrollWelcomeActivity;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.User;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.HomeDevicePM25;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLoginResponse;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.config.UserConfig;
import com.honeywell.hch.airtouchv2.framework.database.UserDBService;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IRefreshEnd;
import com.honeywell.hch.airtouchv2.framework.view.AirTouchEditText;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Jin Qian on 1/22/2015.
 */
public class UserLoginActivity extends BaseActivity {

    private TextView userNickname;
    private EditText mobilePhoneEditText;
    private AirTouchEditText userPasswordComponent;
    private FrameLayout backFramelayout;
    private Button newUserButton;
    private Button forgetPasswordButton;
    private Button loginButton;
    private static ProgressDialog mDialog;
    private String mUserNickname;
    private String mMobilePhone;
    private String mUserPassword;
    private Spinner emailSpinner;
    private CheckBox rememberCheckBox;
    private TextView rememberCheckBoxTextView;

    private List<User> mUserList = new ArrayList<>();
    private ArrayList<UserLocation> locationResponses = new ArrayList<>();
    private ArrayList<ArrayList<HomeDevicePM25>> homeDevicesList = new ArrayList<>();
    private EmailSpinnerArrayAdapter<String> emailSpinnerTypeAdapter;
    private UserConfig userConfig;
    private Boolean rememberChecked;
    private String mUserId;
    private String mSessionId;
    private String[] mEmailTypes;
    private UserDBService mUserDBService = null;

    private Context mContext = this;
    private int mUserHomeNumber;
    private int getHomePm25Count = 0;
    private boolean isMobileEditTextFocused = false;
    private boolean isPasswordEditTextFocused = false;
    private boolean isUserWillRegister = false;
    private InputMethodManager inputMethodManager;
    private static String TAG = "AirTouchUserLogin";

    private int mHomeAirTouchSeriesDeviceNumber = 0;
    private int mHomeDeviceTotalNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        super.TAG = TAG;
        mUserDBService = new UserDBService(this);
        initView();
        userConfig = new UserConfig(UserLoginActivity.this);
        rememberChecked = AuthorizeApp.shareInstance().isRemember();
    }

    @Override
    protected void onResume() {
        super.onResume();

        decideButtonStatus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
//            mDialog = ProgressDialog.show(UserLoginActivity.this, null,
//                    getString(R.string.enroll_loading));
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        return false;
    }

    private void initView() {
        userNickname = (TextView) findViewById(R.id.user_nickname);
        userNickname.setText(AuthorizeApp.shareInstance().getNickname());
        mobilePhoneEditText = (EditText) findViewById(R.id.et_login_mobile);
        mobilePhoneEditText.addTextChangedListener(mEditTextWatch);
        mobilePhoneEditText.setOnFocusChangeListener(mobilOnFocusChanged);
        userPasswordComponent = (AirTouchEditText) findViewById(R.id.et_login_password);
        ColorStateList csl1 = (ColorStateList) getResources().getColorStateList(R.color.white);
        ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.login_hint_text);
        userPasswordComponent.setEditorHint(getString(R.string.enroll_password));
        userPasswordComponent.getEditText().setTextColor(csl1);
        userPasswordComponent.getEditText().setHintTextColor(csl2);
        userPasswordComponent.getEditText().setGravity(Gravity.CENTER);
//        userPasswordComponent.getEditText().setOnKeyListener(immOnKey);
        userPasswordComponent.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        userPasswordComponent.getEditText().addTextChangedListener(mEditTextWatch2);
        userPasswordComponent.setImage(AirTouchEditText.ComponentType.CLEAN);
        userPasswordComponent.setInputMaxLength(30);
        userPasswordComponent.getEditText().setOnFocusChangeListener(passwordOnFocusChanged);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        backFramelayout = (FrameLayout) findViewById(R.id.login_back_layout);
        backFramelayout.setOnClickListener(backOnClick);
        newUserButton = (Button) findViewById(R.id.new_user);
        newUserButton.setOnClickListener(newUserOnClick);
        forgetPasswordButton = (Button) findViewById(R.id.forget_password);
        forgetPasswordButton.setOnClickListener(forgetPasswordOnClick);
        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(loginOnClick);
        emailSpinner = (Spinner) findViewById(R.id.email_spinner);
        mEmailTypes = getMobileStringArray();
        emailSpinnerTypeAdapter = new EmailSpinnerArrayAdapter<>(this, mEmailTypes);
        emailSpinner.setAdapter(emailSpinnerTypeAdapter);
        rememberCheckBox = (CheckBox) findViewById(R.id.remember_checkbox);
        rememberCheckBox.setOnClickListener(rememberOnClick);
        rememberCheckBoxTextView = (TextView) findViewById(R.id.remember_checkbox_tv);
        rememberCheckBoxTextView.setOnClickListener(rememberTextOnClick);
        if (AuthorizeApp.shareInstance().isRemember()) {
            rememberCheckBox.setChecked(true);
//            userPasswordComponent.setEditorText
//                    (AuthorizeApp.shareInstance().getPassword());
        } else {
            rememberCheckBox.setChecked(false);
        }
    }

    IReceiveResponse mReceiveResponse = new IReceiveResponse() {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            switch (httpRequestResponse.getRequestID()) {
                case USER_LOGIN:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            UserLoginResponse userLoginResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    UserLoginResponse.class);

                            if (userLoginResponse.getUserInfo() != null) {
                                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Login Success, userId："
                                        + userLoginResponse.getUserInfo().getUserID());

                                // Save user data to sharedPreference
                                mUserId = userLoginResponse.getUserInfo().getUserID();
                                mSessionId = userLoginResponse.getSessionId();
                                mUserNickname = userLoginResponse.getUserInfo().getFirstName();
                                userConfig.saveUserInfo(mUserNickname, mMobilePhone, mUserPassword,
                                        mUserId, mSessionId, true);

                                TccClient.sharedInstance().getLocation(mUserId, mSessionId, mReceiveResponse);
                            }
                        }
                    } else if (httpRequestResponse.getStatusCode() == StatusCode.UNAUTHORIZED) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                                getString(R.string.password_invalid), null, null);
                    } else {
                        errorHandle(httpRequestResponse);
                    }

                    enableLoginButton();
                    break;

                case GET_LOCATION:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            try {
                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());

                                mHomeDeviceTotalNumber = 0;
                                mHomeAirTouchSeriesDeviceNumber = 0;
                                locationResponses.clear();
                                homeDevicesList.clear();
                                getHomePm25Count = 0;
                                mUserHomeNumber = responseArray.length();

                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseJSON = responseArray.getJSONObject(i);
                                    UserLocation getLocationResponse = new Gson().fromJson(responseJSON.toString(), UserLocation.class);
                                    locationResponses.add(getLocationResponse);
                                    mHomeDeviceTotalNumber += getLocationResponse.getAirTouchSDeviceNumber();

                                }
                                for (int i = 0; i < responseArray.length(); i++) {
                                        final UserLocation localtionItem = locationResponses.get(i);
                                        // get devices of each home
                                        localtionItem.loadHomeDevicesData(new IRefreshEnd()
                                        {
                                            @Override
                                            public void notifyDataRefreshEnd()
                                            {
                                                mHomeAirTouchSeriesDeviceNumber++;

                                                if (mHomeAirTouchSeriesDeviceNumber == mHomeDeviceTotalNumber){
                                                    if (mDialog != null) {
                                                        mDialog.dismiss();
                                                    }

                                                    AuthorizeApp.shareInstance().setCurrentHome(AuthorizeApp.
                                                            shareInstance().getUserLocations().get(0),0);

                                                    Intent boradIntent = new Intent("loginChanged");
                                                    ATApplication.getInstance().getApplicationContext().sendBroadcast(boradIntent);

                                                    if (AuthorizeApp.shareInstance().isUserWantToEnroll()) {
                                                        Intent intent = new Intent();
                                                        intent.setClass(UserLoginActivity.this, EnrollWelcomeActivity.class);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                                        finish();
                                                    } else {
                                                        finish();
                                                        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                                                    }
                                                }
                                            }
                                        });
                                }


                                AuthorizeApp.shareInstance().setUserLocations(locationResponses);

                                if (mUserHomeNumber == 0 || mHomeDeviceTotalNumber == 0) {
                                    if (AuthorizeApp.shareInstance().isUserWantToEnroll()) {
                                        Intent intent = new Intent();
                                        intent.setClass(UserLoginActivity.this, EnrollWelcomeActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                    }
                                    if (mDialog != null) {
                                        mDialog.dismiss();
                                    }
                                    loginFinish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        errorHandle(httpRequestResponse);
                    }

                    break;

//                case GET_HOME_PM25:
//                    ArrayList<HomeDevicePM25> homeDevices = new ArrayList<>();
//                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
//                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
//                            try {
//                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
//                                for (int i = 0; i < responseArray.length(); i++) {
//                                    JSONObject responseJSON = responseArray.getJSONObject(i);
//                                    HomeDevicePM25 device = new Gson().fromJson(responseJSON.toString(),
//                                            HomeDevicePM25.class);
//                                    homeDevices.add(device);
//                                }
//                                // sometimes there is no device in home, fake one.
//                                if (responseArray.length() == 0) {
//                                    HomeDevicePM25 device = new HomeDevicePM25();
//                                    device.setDeviceID(0);
//                                    device.setPM25Value(0);
//                                    device.setAirCleanerFanModeSwitch("Off");
//                                    homeDevices.add(device);
//                                }
//                                homeDevicesList.add(homeDevices);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        /*
//                         * 1) GET_LOCATION - get all homes
//                         * 2) GET_HOME_PM25 - get all devices in each home
//                         * 3) save data of devices into each home
//                         */
//                        getHomePm25Count++;
//                        if (getHomePm25Count == mUserHomeNumber) {
//                            if (mDialog != null) {
//                                mDialog.dismiss();
//                            }
//
//                            for (int i = 0; i < mUserHomeNumber; i++) {
//                                AuthorizeApp.shareInstance().getUserLocations().get(i).
//                                        setHomeDevicesPM25(homeDevicesList.get(i));
//                            }
//                            AuthorizeApp.shareInstance().setCurrentHome(AuthorizeApp.
//                                    shareInstance().getUserLocations().get(0),0);
//
//                            Intent boradIntent = new Intent("loginChanged");
//                            ATApplication.getInstance().getApplicationContext().sendBroadcast(boradIntent);
//
//                            if (AuthorizeApp.shareInstance().isUserWantToEnroll()) {
//                                Intent intent = new Intent();
//                                intent.setClass(UserLoginActivity.this, EnrollWelcomeActivity.class);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                                finish();
//                            } else {
//                                finish();
//                                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//                            }
//                        }
//                    } else {
//                        errorHandle(httpRequestResponse);
//                    }
//                    break;

                default:
                    break;
            }

        }
    };

    private void loginFinish(){
        Intent intent = new Intent("loginChanged");
        ATApplication.getInstance().getApplicationContext().sendBroadcast(intent);
        finish();
    }

    View.OnClickListener rememberOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (rememberCheckBox.isChecked()) {
                rememberChecked = true;
            } else {
                rememberChecked = false;
            }

        }
    };

    View.OnClickListener rememberTextOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (rememberCheckBox.isChecked()) {
                rememberCheckBox.setChecked(false);
                rememberChecked = false;
            } else {
                rememberCheckBox.setChecked(true);
                rememberChecked = true;
            }

        }
    };

    View.OnFocusChangeListener mobilOnFocusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                emailSpinner.setVisibility(View.INVISIBLE);
                userNickname.setText("");
                userPasswordComponent.setEditorText("");
                mobilePhoneEditText.setHint(getString(R.string.mobile_phone_number));
                isMobileEditTextFocused = true;
            } else {
                if (mobilePhoneEditText.getText().toString().isEmpty()) {
                    emailSpinner.setVisibility(View.VISIBLE);
//                    userNickname.setText(mUserDBService.getDefaultUser().getNickName());

                    if (mEmailTypes.length == 0) {
                        mobilePhoneEditText.setHint(getString(R.string.mobile_phone_number));
                    } else {
                        mobilePhoneEditText.setHint("");
                    }

//                    userPasswordComponent.setEditorText
//                            (mUserDBService.getDefaultUser().getPassword());
                } else {
                    emailSpinner.setVisibility(View.INVISIBLE);
                    userNickname.setText("");
                    mobilePhoneEditText.setHint(getString(R.string.mobile_phone_number));
                    userPasswordComponent.setEditorText("");
                }
                isMobileEditTextFocused = false;
            }
        }
    };

    View.OnFocusChangeListener passwordOnFocusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                isPasswordEditTextFocused = true;
            } else {
                isPasswordEditTextFocused = false;
            }
        }
    };

    View.OnClickListener backOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mDialog = ProgressDialog.show(UserLoginActivity.this, null,
//                    getString(R.string.enroll_loading));
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

    };

    View.OnClickListener newUserOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isUserWillRegister = true;

            Intent i = new Intent();
            i.setClass(UserLoginActivity.this, MobileGetSmsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        }

    };

    View.OnClickListener forgetPasswordOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.putExtra("forgetPassword", true);
            i.setClass(UserLoginActivity.this, MobileGetSmsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        }

    };

    public View.OnClickListener loginOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Umeng statistic
            MobclickAgent.onEvent(mContext, "login_button_event");

            if (isUserInputValidate()) {
                mDialog = ProgressDialog.show(UserLoginActivity.this, null, getString(R.string.log_in));
                userLogin(mReceiveResponse);
            }

        }

    };

    private void userLogin(IReceiveResponse recordCreatedResponse) {
        /*
         * If there are one more user accounts exist,
         * but user input another email
         * It need user email in EditText rather than spinner
         */
        if (!mobilePhoneEditText.getText().toString().isEmpty()) {
            mMobilePhone = mobilePhoneEditText.getText().toString();
        }

        mUserPassword = userPasswordComponent.getEditorText();
        UserLoginRequest userLoginRequest = new UserLoginRequest(mMobilePhone, mUserPassword,
                AppConfig.APPLICATION_ID);
        TccClient.sharedInstance().userLogin(userLoginRequest, recordCreatedResponse);
    }

    private boolean isUserInputValidate() {
        if (!mobilePhoneEditText.getText().toString().isEmpty()) {
            mMobilePhone = mobilePhoneEditText.getText().toString();
        }

        Matcher smsMatcher = Patterns.PHONE.matcher(mMobilePhone);
        if (smsMatcher.matches() == false) {
            MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                    getString(R.string.phone_format_wrong), null, null);
            return false;
        }

        return true;
    }


    /**
     * get email which already registered on TCC
     *
     * @return string array of user emails
     */
    public String[] getMobileStringArray() {
        // get all users from database
//        mUserList.clear();
        mUserList.addAll(mUserDBService.findAllUsers());

        String[] stringsArray = new String[0];
        if (mUserList != null && mUserList.size() > 0) {
            stringsArray = new String[mUserList.size()];

            // get current user mobile at the first place of spinner
            stringsArray[0] = AuthorizeApp.shareInstance().getMobilePhone();

            // get others mobile at the second place of spinner
            int minus = 0;
            for (int i = 0; i < mUserList.size(); i++) {
                if (mUserList.get(i).getPhoneNumber()
                        .equals(AuthorizeApp.shareInstance().getMobilePhone())) {
                    minus = 1;
                } else {
                    stringsArray[i + 1 - minus] = mUserList.get(i).getPhoneNumber();
                }
            }

            /*
             * If mobile is available, spinner will not show automatically
             * the editText hint need to be cleaned
             */
            mobilePhoneEditText.setHint("");
        }

        return stringsArray;
    }

    /**
     * SpinnerArrayAdapter for Email selection
     */
    public class EmailSpinnerArrayAdapter<T> extends ArrayAdapter<T> {

        public EmailSpinnerArrayAdapter(Context context, List<T> objects) {
            super(context, 0, objects);
        }

        public EmailSpinnerArrayAdapter(Context context, T[] objects) {
            super(context, 0, objects);
        }

        protected String getItemValue(T item, Context context) {
            return item.toString();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout
                        .list_item_email_spinner_drop_down, parent, false);
            }

            TextView tv = (TextView) view.findViewById(R.id.list_item_email_drop_text);
            String email = getItemValue(getItem(position), getContext());
            tv.setText(email);

            return view;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout
                        .list_item_email_spinner, parent, false);
            }

            TextView tv = (TextView) view.findViewById(R.id.list_item_email_title);
            mMobilePhone = getItemValue(getItem(position), getContext());
            tv.setText(mMobilePhone);

            // change nickname which gets from database
            for (int i = 0; i < mUserList.size(); i++) {
                if (mUserList.get(i).getPhoneNumber().equals(mMobilePhone)) {
                    if (emailSpinner.getVisibility() == View.VISIBLE) {
                        userNickname.setText(mUserList.get(i).getNickName());
                    }
                    break;
                }
            }

            // show password or not
            if (AuthorizeApp.shareInstance().isRemember()) {
                if (!isPasswordEditTextFocused && !isMobileEditTextFocused) {
                    for (int i = 0; i < mUserList.size(); i++) {
                        if (mUserList.get(i).getPhoneNumber().equals(mMobilePhone)) {
                            if (emailSpinner.getVisibility() == View.VISIBLE) {
                                userPasswordComponent.setEditorText(mUserList.get(i).getPassword());
                            }
                            break;
                        }
                    }
                }
            }

            return view;
        }

    }

    View.OnKeyListener immOnKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
                        && inputMethodManager.isActive()) {
                    if (isUserInputValidate()) {
                        mDialog = ProgressDialog.show(UserLoginActivity.this, null, getString(R.string.log_in));
                        userLogin(mReceiveResponse);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                        getResources().getString(R.string.enroll_error), null, null);
            }
            return false;
        }
    };

    private TextWatcher mEditTextWatch = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // display EditText or Spinner
            if (mobilePhoneEditText.getText().toString().isEmpty()
                    && (!isMobileEditTextFocused)) {
                emailSpinner.setVisibility(View.VISIBLE);
//                userNickname.setText(AuthorizeApp.shareInstance().getNickname());
                mobilePhoneEditText.setHint("");
            } else {
                emailSpinner.setVisibility(View.INVISIBLE);
                userNickname.setText("");
                mobilePhoneEditText.setHint(getString(R.string.mobile_phone_number));
            }

            decideButtonStatus();
        }
    };

    private TextWatcher mEditTextWatch2 = new TextWatcher() {
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

    private void disableLoginButton() {
        loginButton.setClickable(false);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.login_button_text);
        loginButton.setTextColor(csl);
        loginButton.setBackgroundResource(R.color.login_button_background);

    }

    private void enableLoginButton() {
        loginButton.setClickable(true);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.white);
        loginButton.setTextColor(csl);
        loginButton.setBackgroundResource(R.drawable.enroll_next_button);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rememberChecked) {
            userConfig.saveRemember(true);
        } else {
            userConfig.saveRemember(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isUserWillRegister) {
            return;
        }

        if (AuthorizeApp.shareInstance().isUserWantToEnroll()) {
            AuthorizeApp.shareInstance().setIsUserWantToEnroll(false);
        }

//        if (mDialog != null)
//            mDialog.dismiss();
    }

    private void decideButtonStatus() {
        if ((mobilePhoneEditText.getText().toString().isEmpty())
                || (userPasswordComponent.getEditorText().isEmpty())) {
            disableLoginButton();
        } else {
            enableLoginButton();
        }

        // If spinner display and password is empty, disable button
        if (!AuthorizeApp.shareInstance().getMobilePhone().isEmpty()) {
            if (userPasswordComponent.getEditorText().isEmpty()) {
                disableLoginButton();
            } else {
                enableLoginButton();
            }
        }
    }


    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
            MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                    getString(R.string.no_network), null, null);
            return;
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
//                MessageBox.createSimpleDialog(UserLoginActivity.this, null,
//                        errorResponse.getMessage(), null, null);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
                MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                        getString(R.string.enroll_error), null, null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                    getString(R.string.enroll_error), null, null);
        }
    }

}
