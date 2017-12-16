package com.honeywell.hch.airtouchv3.app.authorize.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.User;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.app.authorize.model.CountryCodeAdapter;
import com.honeywell.hch.airtouchv3.framework.database.UserDBService;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.view.AirTouchEditText;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.honeywell.hch.airtouchv3.lib.util.TripleDES;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jin Qian on 1/22/2015.
 * <p/>
 * change by Stephen(H127856)
 * call another login task for login action
 */
public class UserLoginActivity extends UserLoginBaseActivity {

    private TextView mUserNicknameTextView;
    private EditText mMobilePhoneEditText;
    private AirTouchEditText mUserPasswordComponent;
    private FrameLayout backFramelayout;
    private LinearLayout loginInputLayout;
    private Button newUserButton;
    private Button forgetPasswordButton;
    private Button loginButton;
    private static Dialog mDialog;
    private String mUserNickname;
    private String mMobilePhone;
    private String mUserPassword;
    private Spinner mMobileSpinner;
    private Spinner mCountryCodeSpinner;
    private CheckBox rememberCheckBox;
    private TextView rememberCheckBoxTextView;
    private Boolean mLaunchFirstTime = true;
    private Boolean mNeedShowPhoneSpinnerContent = false;
    private Boolean mChangeCountryCodeDueToAccount = false;

    private List<User> mUserList = new ArrayList<>();
    private EmailSpinnerArrayAdapter<String> mMobileSpinnerTypeAdapter;
    private CountryCodeAdapter<String> mCountryCodeSpinnerTypeAdapter;
    private String[] mCountryCodeTypes = {AirTouchConstants.CHINA_CODE, AirTouchConstants.INDIA_CODE};
    private String mUserId;
    private String mSessionId;
    private String mCountryCode;
    private String[] mMobileTypes;

    private UserDBService mUserDBService = null;

    private boolean isMobileEditTextFocused = false;
    private boolean isPasswordEditTextFocused = false;
    private boolean isUserWillRegister = false;
    private InputMethodManager inputMethodManager;
    private static String TAG = "AirTouchUserLogin";
    private final int MIN_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        super.TAG = TAG;
        mUserDBService = new UserDBService(this);
        initView();
//        registerUserLoginEndReceiver();
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
            if (mDialog != null) {
                mDialog.cancel();
            }
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        return false;
    }

    private void initView() {
        mUserNicknameTextView = (TextView) findViewById(R.id.user_nickname);
        mUserNicknameTextView.setText(mAuthorizeApp.getNickname());
        mMobilePhoneEditText = (EditText) findViewById(R.id.et_login_mobile);
        mMobilePhoneEditText.addTextChangedListener(mEditTextWatch);
        mMobilePhoneEditText.setOnFocusChangeListener(mobilOnFocusChanged);
        mUserPasswordComponent = (AirTouchEditText) findViewById(R.id.et_login_password);
        mUserPasswordComponent.getEditText().addTextChangedListener(mEditTextWatch2);
        ColorStateList csl1 = (ColorStateList) getResources().getColorStateList(R.color.white);
        ColorStateList csl2 = (ColorStateList) getResources().getColorStateList(R.color.login_hint_text);
        mUserPasswordComponent.setEditorHint(getString(R.string.enroll_password));
        mUserPasswordComponent.getEditText().setTextColor(csl1);
        mUserPasswordComponent.getEditText().setHintTextColor(csl2);
        mUserPasswordComponent.getEditText().setGravity(Gravity.CENTER);
//        mUserPasswordComponent.getEditText().setOnKeyListener(immOnKey);
        mUserPasswordComponent.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mUserPasswordComponent.setImage(AirTouchEditText.ComponentType.CLEAN);
        mUserPasswordComponent.setInputMaxLength(30);
        mUserPasswordComponent.getEditText().setOnFocusChangeListener(passwordOnFocusChanged);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        backFramelayout = (FrameLayout) findViewById(R.id.login_back_layout);
        backFramelayout.setOnClickListener(backOnClick);
        loginInputLayout = (LinearLayout) findViewById(R.id.login_input);
        newUserButton = (Button) findViewById(R.id.new_user);
        newUserButton.setOnClickListener(newUserOnClick);
        forgetPasswordButton = (Button) findViewById(R.id.forget_password);
        forgetPasswordButton.setOnClickListener(forgetPasswordOnClick);
        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(loginOnClick);
        mCountryCodeSpinner = (Spinner) findViewById(R.id.country_code_spinner);
        setupCountryCodeSpinner();
        mMobileSpinner = (Spinner) findViewById(R.id.email_spinner);
        setupMobileSpinner();
        mMobileTypes = getMobileStringArray();
        mMobileSpinnerTypeAdapter = new EmailSpinnerArrayAdapter<>(this, mMobileTypes);
        mMobileSpinner.setAdapter(mMobileSpinnerTypeAdapter);
        rememberCheckBox = (CheckBox) findViewById(R.id.remember_checkbox);
        rememberCheckBox.setOnClickListener(rememberOnClick);
        rememberCheckBoxTextView = (TextView) findViewById(R.id.remember_checkbox_tv);
        rememberCheckBoxTextView.setOnClickListener(rememberTextOnClick);
        rememberCheckBox.setChecked(mAuthorizeApp.isRemember());
    }

//    private void registerUserLoginEndReceiver() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(AirTouchConstants.HOME_CHANGED);
//        userLoginEndReceiver = new UserLoginEndReceiver();
//        registerReceiver(userLoginEndReceiver, intentFilter);
//    }

//    private class UserLoginEndReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (AppManager.shareInstance().getAuthorizeApp().isLoginSuccess()) {
//                afterLoginSuccess();
//            }
//        }
//    }

    View.OnClickListener rememberOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mAuthorizeApp.setIsRemember(rememberCheckBox.isChecked());
        }
    };

    View.OnClickListener rememberTextOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rememberCheckBox.setChecked(!rememberCheckBox.isChecked());
            mAuthorizeApp.setIsRemember(rememberCheckBox.isChecked());
        }
    };

    View.OnFocusChangeListener mobilOnFocusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mNeedShowPhoneSpinnerContent = false;
                mMobileSpinner.setVisibility(View.INVISIBLE);
                mUserNicknameTextView.setText("");
                mUserPasswordComponent.setEditorText("");
                mMobilePhoneEditText.setHint(getString(R.string.mobile_phone_number));
                isMobileEditTextFocused = true;
            } else {
                if (mMobilePhoneEditText.getText().toString().isEmpty()
                        && mUserDBService != null && mUserDBService.getDefaultUser() != null) {
                    mNeedShowPhoneSpinnerContent = true;
                    mMobileSpinner.setVisibility(View.VISIBLE);
                    mUserNicknameTextView.setText(mUserDBService.getDefaultUser().getNickName());
                    if (mMobileTypes.length == 0) {
                        mMobilePhoneEditText.setHint(getString(R.string.mobile_phone_number));
                    } else {
                        mMobilePhoneEditText.setHint("");
                    }

                    // countryCode setup
                    if (mUserDBService.getUserByPhoneNumber(mMobilePhone) != null) {
                        switch (mUserDBService.getUserByPhoneNumber(mMobilePhone).getCountryCode()) {
                            case AirTouchConstants.CHINA_CODE:
                                if (mCountryCode != null && !mCountryCode.equals(AirTouchConstants.CHINA_CODE)) {
                                    mCountryCodeSpinner.setSelection(0);
                                    mChangeCountryCodeDueToAccount = true;
                                }
                                break;

                            case AirTouchConstants.INDIA_CODE:
                                if (mCountryCode != null && !mCountryCode.equals(AirTouchConstants.INDIA_CODE)) {
                                    mCountryCodeSpinner.setSelection(1);
                                    mChangeCountryCodeDueToAccount = true;
                                }
                                break;
                        }
                    }
                } else {
                    mNeedShowPhoneSpinnerContent = false;
                    mMobileSpinner.setVisibility(View.VISIBLE);
                    mUserNicknameTextView.setText("");
                    mMobilePhoneEditText.setHint(getString(R.string.mobile_phone_number));
                    mUserPasswordComponent.setEditorText("");
                }
                isMobileEditTextFocused = false;
            }
            mMobileSpinnerTypeAdapter.notifyDataSetChanged();
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
            i.putExtra(AirTouchConstants.NEW_USER, true);
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
            i.putExtra(AirTouchConstants.FORGET_PASSWORD, true);
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
                mDialog = LoadingProgressDialog.show(UserLoginActivity.this, getString(R.string.log_in));
                userLogin();
            } else {

            }

        }

    };

    private void userLogin() {
        /*
         * If there are one more user accounts exist,
         * but user input another email
         * It need user email in EditText rather than spinner
         */
        if (!mMobilePhoneEditText.getText().toString().equals("")) {
            mMobilePhone = mMobilePhoneEditText.getText().toString();
        }


        mUserPassword = mUserPasswordComponent.getEditorText();
//        AppManager.shareInstance().setLoginInfo(mMobilePhone, mUserPassword);
        IActivityReceive loginReceiver =  userLogin(mDialog, (Activity) mContext, mMobilePhone, mUserPassword);
        beginUserLogin(mMobilePhone, mUserPassword, loginReceiver);

    }

    private boolean isUserInputValidate() {
        if (!mMobilePhoneEditText.getText().toString().isEmpty()) {
            mMobilePhone = mMobilePhoneEditText.getText().toString();
        }

        Matcher smsMatcher = Patterns.PHONE.matcher(mMobilePhone);
        if (smsMatcher.matches() == false) {
            MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                    getString(R.string.phone_format_wrong), null, null);
            return false;
        }
        // India version
        if (mCountryCode.equals(AirTouchConstants.CHINA_CODE)) {
            if (mMobilePhone.length() != 11) {
                MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                        getString(R.string.phone_format_wrong), null, null);
                return false;
            }

        }
        if (mCountryCode.equals(AirTouchConstants.INDIA_CODE)) {
            if (mMobilePhone.length() != 10) {
                MessageBox.createSimpleDialog(UserLoginActivity.this, null,
                        getString(R.string.phone_format_wrong), null, null);
                return false;
            }
        }
        mUserPassword = mUserPasswordComponent.getEditorText();

        Matcher passwordMatcher = Pattern.compile("^[A-z0-9]{6,30}+$").matcher(mUserPassword);
        // Password is at least 6 characters
        if (mUserPassword.length() < AirTouchConstants.MIN_USER_PASSWORD) {
            showToast(getString(R.string.password_length_wrong));
            return false;
        }
        // Password does not match rules
        if (passwordMatcher.matches() == false) {
            showToast(getString(R.string.password_format_wrong));
            return false;
        }
//        if(!mUserPassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])[a-zA-Z0-9]{8,30}$")){
//        if (!mUserPassword.matches("^[A-z0-9]{6,30}+$")) {
//            showToast(getString(R.string.password_format_wrong));
//            return false;
//        }
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
            stringsArray[0] = mAuthorizeApp.getMobilePhone();

            // get others mobile at the second place of spinner
            int minus = 0;
            for (int i = 0; i < mUserList.size(); i++) {
                if (mUserList.get(i).getPhoneNumber().equals(mAuthorizeApp.getMobilePhone())) {
                    minus = 1;
                } else {
                    stringsArray[i + 1 - minus] = mUserList.get(i).getPhoneNumber();
                }
            }

            /*
             * If mobile is available, spinner will not show automatically
             * the editText hint need to be cleaned
             */
            mMobilePhoneEditText.setHint("");
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
            if (item != null) {
                return item.toString();
            }
            return "";
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout
                        .list_item_email_spinner_drop_down, parent, false);
            }

            TextView tv = (TextView) view.findViewById(R.id.list_item_email_drop_text);
            String mobile = getItemValue(getItem(position), getContext());
            tv.setText(mobile);

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
            String mobile = getItemValue(getItem(position), getContext());
            if (mNeedShowPhoneSpinnerContent) {
                tv.setText(mobile);
            } else {
                tv.setText("");
            }

            User user = mUserDBService.getUserByPhoneNumber(mobile);
            if (user != null) {
                if (mNeedShowPhoneSpinnerContent) {
                    mUserNicknameTextView.setText(user.getNickName());
                } else {
                    mUserNicknameTextView.setText("");
                }

                // show password or not
                if (!isPasswordEditTextFocused && !isMobileEditTextFocused && !StringUtil.isEmpty
                        (user.getPassword())) {
                    try {
                        TripleDES tripleDES = new TripleDES("ECB");
                        mUserPasswordComponent.setEditorText(user.getIsEncrypted() == 1 ? new
                                String(tripleDES.decrypt(user.getPassword().getBytes("ISO-8859-1"))) :
                                user.getPassword());
                    } catch (Exception e) {
                        e.printStackTrace();
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
                        mDialog = LoadingProgressDialog.show(UserLoginActivity.this, getString(R.string.log_in));
                        userLogin();
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
            StringUtil.isAlaboNumeric(mMobilePhoneEditText);
        }

        @Override
        public void afterTextChanged(Editable s) {
            // display EditText or Spinner
            if (mMobilePhoneEditText.getText().toString().isEmpty()
                    && (!isMobileEditTextFocused)) {
                mNeedShowPhoneSpinnerContent = true;
                mMobilePhoneEditText.setHint("");
            } else {
                mNeedShowPhoneSpinnerContent = false;
                mUserNicknameTextView.setText("");
                mMobilePhoneEditText.setHint(getString(R.string.mobile_phone_number));
            }
            mMobileSpinnerTypeAdapter.notifyDataSetChanged();
            decideButtonStatus();
        }
    };

    private TextWatcher mEditTextWatch2 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            StringUtil.specialCharacterFilter(mUserPasswordComponent);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!mLaunchFirstTime)
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
    protected void onDestroy() {
        super.onDestroy();

        if (isUserWillRegister) {
            return;
        }

        if (mAuthorizeApp.isUserWantToEnroll()) {
            mAuthorizeApp.setIsUserWantToEnroll(false);
        }
//        unregisterReceiver(userLoginEndReceiver);

    }

    private void decideButtonStatus() {
        if ((mMobilePhoneEditText.getText().toString().isEmpty())
                || (mUserPasswordComponent.getEditorText().length() < MIN_LENGTH)) {
            disableLoginButton();
        } else {
            enableLoginButton();
        }

        // If spinner display and password is empty, disable button
        if (!mAuthorizeApp.getMobilePhone().isEmpty()) {
            if (mUserPasswordComponent.getEditorText().length() < MIN_LENGTH) {
                disableLoginButton();
            } else {
                enableLoginButton();
            }
        }
    }

    @Override
    public void errorHandle(ResponseResult responseResult, String errorMsg) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.errorHandle(responseResult, errorMsg);
    }

    private void setMobileMaxLength() {
        switch (mCountryCode) {
            case AirTouchConstants.CHINA_CODE:
                mMobilePhoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                break;
            case AirTouchConstants.INDIA_CODE:
                mMobilePhoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                break;
            default:
                mMobilePhoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                break;
        }
    }

    private void setupMobileSpinner() {
        mMobileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loginInputLayout.requestFocus();
                mMobilePhoneEditText.setText("");
                mMobilePhone = mMobileSpinnerTypeAdapter
                        .getItemValue(mMobileSpinnerTypeAdapter.getItem(position), UserLoginActivity.this);
                User user = mUserDBService.getUserByPhoneNumber(mMobilePhone);
                if (user != null) {
                    mUserNicknameTextView.setText(user.getNickName());
                    try {
                        TripleDES tripleDES = new TripleDES("ECB");
                        mUserPasswordComponent.setEditorText(user.getIsEncrypted() == 1 ? new
                                String(tripleDES.decrypt(user.getPassword().getBytes("ISO-8859-1"))) :
                                user.getPassword());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    // countryCode setup
                    switch (user.getCountryCode()) {
                        case AirTouchConstants.CHINA_CODE:
                            if (mLaunchFirstTime)
                                break;
                            if (mCountryCode != null && mCountryCode.equals(AirTouchConstants.CHINA_CODE))
                                break;

                            mCountryCodeSpinner.setSelection(0);
                            mChangeCountryCodeDueToAccount = true;
                            break;

                        case AirTouchConstants.INDIA_CODE:
                            if (mCountryCode != null && mCountryCode.equals(AirTouchConstants.INDIA_CODE)) {
                                break;
                            } else {
                                mCountryCodeSpinner.setSelection(1);
                                mChangeCountryCodeDueToAccount = true;
                            }
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    private void setupCountryCodeSpinner() {
        mCountryCodeSpinnerTypeAdapter = new CountryCodeAdapter<>(this, mCountryCodeTypes);
        mCountryCodeSpinner.setAdapter(mCountryCodeSpinnerTypeAdapter);

        mCountryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLaunchFirstTime) {
                    mLaunchFirstTime = false;
                    if (mChangeCountryCodeDueToAccount && mCountryCode != null) {
                        mChangeCountryCodeDueToAccount = false;
                    }
                } else {
                    if (mChangeCountryCodeDueToAccount) {
                        mChangeCountryCodeDueToAccount = false;
                    } else {
                        mMobilePhoneEditText.requestFocus();
                        mMobilePhoneEditText.setText("");
                    }
                }

                mCountryCode = mCountryCodeSpinnerTypeAdapter
                        .getItemValue(mCountryCodeSpinnerTypeAdapter.getItem(position), UserLoginActivity.this);
                setMobileMaxLength();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<User> userList = new ArrayList<>();
        userList.addAll(mUserDBService.findAllUsers());
        if (userList.size() == 0) {
            if (mAuthorizeApp.getGPSCountry() != null && mAuthorizeApp.getGPSCountry()
                    .equals(AirTouchConstants.INDIA_CODE)) {
                mCountryCodeSpinner.setSelection(1);
            } else {
                mCountryCodeSpinner.setSelection(0);
            }
        }
    }

}
