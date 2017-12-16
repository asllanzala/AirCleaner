package com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.enrollment.controls.EnrollmentClient;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.WAPIRouter;
import com.honeywell.hch.airtouchv2.framework.enrollment.utils.PasswordUtil;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;
import com.honeywell.hch.airtouchv2.framework.view.AirTouchEditText;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;

import java.net.ProtocolException;
import java.util.List;

/**
 * Enrollment Step 3 - User choose SSID and input password.
 * If press other SSID, user need to input SSID, password and select Wifi security.
 * Password will be encrypted with KEY retrieved at step 2.
 * Finally, Air Touch receive SSID and password sent from smart phone and reboot.
 * After reboot, Air Touch try to connect SSID and register on TCC.
 * User do not know whether Air Touch registered on TCC or not.
 * It will be checked in the polling thread.
 */
public class EnrollWifiPasswordActivity extends EnrollBaseActivity {

    private static final String TAG = "AirTouchEnrollWifiPassword";

    private Button nextButton;
    private FrameLayout mBackButton;
    private TextView mSSIDTextView;
    private TextView mOtherWifiTextView;
    private LinearLayout mSelectedTitleLinearLayout;
    private LinearLayout mSelectedWifiLinearLayout;
    private LinearLayout mOtherWifiLinearLayout;
    private LinearLayout mOtherWifiPasswordLinearLayout;
    private AirTouchEditText mSSIDEditText;
    private AirTouchEditText mPasswordEditText;
    private Spinner mSecuritySpinner;
    private RelativeLayout mConnectingLayout;
    private ImageView mLoadingImageView;
    private TextView mConnectingTextView;

    private String mUserPassword;
    private String mUserSSID;
    private SecuritySpinnerArrayAdapter<String> mSecurityTypeAdapter;
    private WAPIRouter mWAPIRouter;
    private InputMethodManager mInputMethodManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollwifipassword);

        super.TAG = TAG;
        mContext = this;
        mConnectingLayout = (RelativeLayout) findViewById(R.id.connect_prompt_view);
        mLoadingImageView = (ImageView) findViewById(R.id.loading_image);
        mConnectingTextView = (TextView) findViewById(R.id.connectTv);
        mConnectingTextView.setText(getString(R.string.enroll_connecting));
        mSSIDTextView = (TextView) findViewById(R.id.ssidTv);
        mSSIDTextView.setMaxWidth(DensityUtil.dip2px(150));
        mOtherWifiTextView = (TextView) findViewById(R.id.other_wifi_title);
        mSSIDEditText = (AirTouchEditText) findViewById(R.id.ssidEt);
        mSSIDEditText.getEditText().setTextColor(getResources().getColor(R.color.black));
        mSSIDEditText.getEditText().addTextChangedListener(mEditTextWatch);
        mSSIDEditText.getEditText().setOnFocusChangeListener(onSSIDFocusChanged);
        mPasswordEditText = (AirTouchEditText) findViewById(R.id.passwordEt);
        mPasswordEditText.getEditText().setTextColor(getResources().getColor(R.color.black));
        mPasswordEditText.getEditText().setOnKeyListener(immOnKey);
        mPasswordEditText.getEditText().addTextChangedListener(mEditTextWatch);
        nextButton = (Button) findViewById(R.id.nextBtn);
        nextButton.setOnClickListener(nextOnClick);
        mBackButton = (FrameLayout) findViewById(R.id.enroll_back_layout);
        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(EnrollWifiPasswordActivity.this, EnrollConnectWifiActivity.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mSecuritySpinner = (Spinner) findViewById(R.id.securitySpinner);

        mSelectedTitleLinearLayout = (LinearLayout) findViewById(R.id.enroll_password_title_layout);
        mSelectedWifiLinearLayout = (LinearLayout) findViewById(R.id.selectedWifiLl);
        mOtherWifiLinearLayout = (LinearLayout) findViewById(R.id.otherWifiLl);
        mOtherWifiPasswordLinearLayout = (LinearLayout) findViewById(R.id.otherWifiPasswordLl);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mWAPIRouter = DIYInstallationState.getWAPIRouter();
        if (mWAPIRouter != null) {
            if (mWAPIRouter.getSSID().equals(getString(R.string.enroll_other))) {
                mOtherWifiTextView.setVisibility(View.VISIBLE);
                mSelectedTitleLinearLayout.setVisibility(View.INVISIBLE);
                mSelectedWifiLinearLayout.setVisibility(View.INVISIBLE);
                mOtherWifiLinearLayout.setVisibility(View.VISIBLE);
                String[] securityTypes = WAPIRouter.RouterSecurity.getStringArray(this);
                mSecurityTypeAdapter = new SecuritySpinnerArrayAdapter<>(this, securityTypes);
                mSecuritySpinner.setAdapter(mSecurityTypeAdapter);
            } else {
                mSelectedWifiLinearLayout.setVisibility(View.VISIBLE);
                mOtherWifiTextView.setVisibility(View.INVISIBLE);
                mOtherWifiLinearLayout.setVisibility(View.INVISIBLE);
            }
            mSSIDTextView.setText(mWAPIRouter.getSSID());
        }

        if (DIYInstallationState.getErrorCode() == 0x10)
            MessageBox.createSimpleDialog(EnrollWifiPasswordActivity.this, null,
                    getString(R.string.error_code_10), null, null);
        if (DIYInstallationState.getErrorCode() == 0x20)
            MessageBox.createSimpleDialog(EnrollWifiPasswordActivity.this, null,
                    getString(R.string.error_code_20), null, null);
        if (DIYInstallationState.getErrorCode() == 0x30)
            MessageBox.createSimpleDialog(EnrollWifiPasswordActivity.this, null,
                    getString(R.string.error_code_30), null, null);

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

    OnClickListener nextOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                startConnect();
            } catch (Exception e) {
                e.printStackTrace();
                MessageBox.createSimpleDialog(EnrollWifiPasswordActivity.this, null,
                        getResources().getString(R.string.enroll_error), null, null);
            }
        }

    };

    OnKeyListener immOnKey = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
                        && mInputMethodManager.isActive()) {
                    if (!isPasswordEmpty() || (isSSIDSecurityOpen())) {
                        startConnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                MessageBox.createSimpleDialog(EnrollWifiPasswordActivity.this, null,
                        getResources().getString(R.string.enroll_error), null, null);
            }
            return false;
        }
    };

    private void disableNextButton() {
        nextButton.setVisibility(View.INVISIBLE);
    }

    private void enableNextButton() {
        nextButton.setVisibility(View.VISIBLE);
    }

    private void startConnect() throws Exception {
        if (mOtherWifiLinearLayout.getVisibility() == View.VISIBLE) {
            if (!isSSIDEmpty()) {
                mWAPIRouter.setSSID(mUserSSID);
                int position = mSecuritySpinner.getSelectedItemPosition();
                mWAPIRouter.setSecurity(WAPIRouter.RouterSecurity.values()[position]);
            } else {
                return;
            }
        }
        mWAPIRouter.setPassword(PasswordUtil.encryptString(mUserPassword,
                DIYInstallationState.getWAPIKeyResponse()));

        IReceiveResponse receiveResponse = new IReceiveResponse() {

            @Override
            public void onReceive(HTTPRequestResponse httpRequestResponse) {
//                decideNextButtonStatus();

                if (httpRequestResponse.getException() != null && httpRequestResponse
                        .getException() instanceof ProtocolException) {
                    // Note this call will probably always fail because we lose
                    // connection with the stat. But the request was sent so we
                    // continue as normal.
                    // We expect for this to fail with a ProtocolException
                    // Don't log out the ProtocolException because the user's wifi
                    // password will also be logged out.
                } else {
//                    MessageBox.createSimpleDialog(EnrollWifiPasswordActivity.this, null,
//                            getResources().getString(R.string.enroll_error), null, null);
                }

                Intent i = new Intent();
                i.setClass(mContext, EnrollLoadingActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();

            }
        };

        EnrollmentClient.sharedInstance()
                .connectStat(mWAPIRouter, RequestID.CONNECT_ROUTER, receiveResponse);

        disableNextButton();
        mConnectingLayout.setVisibility(View.VISIBLE);
        AnimationDrawable anim1 = (AnimationDrawable) mLoadingImageView.getBackground();
        anim1.start();
    }


    private TextWatcher mEditTextWatch = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            decideNextButtonStatus();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    View.OnFocusChangeListener onSSIDFocusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mSSIDEditText.setEditorHint("");
            } else {
                mSSIDEditText.setEditorHint(getString(R.string.enroll_hint_ssid));
            }
        }
    };

    View.OnFocusChangeListener onPassFocusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mPasswordEditText.setEditorHint("");
            } else {
                mPasswordEditText.setEditorHint(getString(R.string.enroll_hint_password));
            }
        }
    };

    private boolean isSSIDEmpty() {
        mUserSSID = mSSIDEditText.getEditorText();
        return mOtherWifiLinearLayout.isShown() && mUserSSID.isEmpty();

    }

    private boolean isPasswordEmpty() {
        mUserPassword = mPasswordEditText.getEditorText();
        return mUserPassword.isEmpty();
    }

    private boolean isSSIDSecurityOpen() {
        return mOtherWifiLinearLayout.isShown() && !mOtherWifiPasswordLinearLayout.isShown();
    }

    public class SecuritySpinnerArrayAdapter<T> extends ArrayAdapter<T> {

        public SecuritySpinnerArrayAdapter(Context context, List<T> objects) {
            super(context, 0, objects);
        }

        public SecuritySpinnerArrayAdapter(Context context, T[] objects) {
            super(context, 0, objects);
        }

        /**
         * Override this to return a custom string
         */
        protected String getItemValue(T item, Context context) {
            return item.toString();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout
                        .list_item_security_spinner_drop_down, parent, false);
            }

            TextView tv = (TextView) view.findViewById(R.id.list_item_security_drop_text);
            String value = getItemValue(getItem(position), getContext());
            tv.setText(value);

            return view;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout
                        .list_item_security_spinner, parent, false);
            }

            TextView tv = (TextView) view.findViewById(R.id.list_item_security_text);
            String value = getItemValue(getItem(position), getContext());
            tv.setText(value);

            if (value.equals(getString(R.string.wapi_router_security_open))) {
                mOtherWifiPasswordLinearLayout.setVisibility(View.INVISIBLE);
            } else {
                mOtherWifiPasswordLinearLayout.setVisibility(View.VISIBLE);

                mPasswordEditText = (AirTouchEditText) findViewById(R.id.securityPasswordEt);
                mPasswordEditText.getEditText().setOnKeyListener(immOnKey);
                mPasswordEditText.getEditText().addTextChangedListener(mEditTextWatch);
                mPasswordEditText.getEditText().setOnFocusChangeListener(onPassFocusChanged);
//                ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.password_text);
//                mPasswordEditText.getEditText().setTextColor(csl);
            }

            decideNextButtonStatus();
            return view;
        }

    }

    private void decideNextButtonStatus() {
        if (!isPasswordEmpty() && !isSSIDEmpty()) {
            enableNextButton();
        } else if (isSSIDSecurityOpen() && !isSSIDEmpty()) {
            enableNextButton();
        } else {
            disableNextButton();
        }
    }

    private MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            finish();
        }
    };
}