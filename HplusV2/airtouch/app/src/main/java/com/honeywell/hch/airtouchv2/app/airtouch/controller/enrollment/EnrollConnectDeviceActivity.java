package com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.model.DeviceInfo;
import com.honeywell.hch.airtouchv2.framework.view.AirTouchEditText;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.honeywell.hch.airtouchv2.lib.util.UmengUtil;


import java.util.ArrayList;

/**
 * Enrollment Step 2 - SmartPhone communicate to Air Touch.
 * 1) sendPhoneName - send phone's BUILD.NO to Air Touch.
 * 2) getWapiKey -  get Air Touch  key for password encrypt.
 * 3) getWapiDevice - get Air Touch  mac and crc.
 * <p/>
 * Ask user to input device name, city name and home name
 * Store these data to DIYInstallationState
 */
public class EnrollConnectDeviceActivity extends EnrollBaseActivity {
    private TextView demoTextView;
    private FrameLayout cityNameLayout;
    private TextView cityNameTextView;
    private Button startConnectButton;
    private Context mContext;
    private static ProgressDialog mDialog;
//    private View connectPromptView;
//    private ImageView loadingImageView;
//    private TextView waitScanTextView;
    private AirTouchEditText mDeviceNameEditText;
    private AirTouchEditText mHomeNameEditText;
    private EnrollDeviceManager mEnrollDeviceManager;
    private City mSelectedGPSCity;
    public static final int SELECT_LOCATION_REQUEST = 10;
    // home name EditText and Spinner
    private boolean isHomeEditTextFocused = false;
    private Spinner homeSpinner;
    private HomeSpinnerAdapter<String> homeSpinnerTypeAdapter;
    private ArrayList<String> homeStringList = new ArrayList<>();

    private static final String TAG = "AirTouchEnrollConnectDevice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        setContentView(R.layout.activity_enrollconnectdevice);

        mContext = EnrollConnectDeviceActivity.this;
        initEnrollDeviceManager();
        initView();
        getCityName();
        getHomeName();
        disableConnectButton();

    }

    @Override
    protected void onResume() {
        super.onResume();

        decideButtonState();
    }

    @Override
    protected void onDestroy() {
        if (mEnrollDeviceManager == null)
            return;

        if (mEnrollDeviceManager.getScanResultsReceiver() != null
                && mEnrollDeviceManager.isRegistered()) {
            unregisterReceiver(mEnrollDeviceManager.getScanResultsReceiver());
        }
        super.onDestroy();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getSerializableExtra("city") == null)
            return;

        City city = (City) data.getSerializableExtra("city");
        switch (requestCode) {
            case SELECT_LOCATION_REQUEST:
                mSelectedGPSCity = city;
                cityNameTextView.setText(AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH) ?
                        city.getNameZh() : city.getNameEn());
                cityNameTextView.setTextColor(getResources().getColor(R.color.black));
                getHomeName();
                cityNameTextView.requestFocus();
                mHomeNameEditText.clearFocus();
                break;
            default:
                break;
        }
    }

    private void initView() {
        homeSpinner = (Spinner) findViewById(R.id.home_spinner);
        cityNameTextView = (TextView) findViewById(R.id.city_name_tv);
//        connectPromptView = findViewById(R.id.loading_image);
//        loadingImageView = (ImageView) findViewById(R.id.loading_image);
//        waitScanTextView = (TextView) findViewById(R.id.waitScanTv);
        startConnectButton = (Button) findViewById(R.id.startConnectBtn);
        startConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnrollDeviceManager.isConnectionAttempted())
                    return;

                // hide input keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(findViewById(R.id.enroll_home_et)
                            .getWindowToken(), 0);
                }

                if (mSelectedGPSCity == null
                        || StringUtil.isEmpty(mSelectedGPSCity.getNameEn())
                        || StringUtil.isEmpty(mSelectedGPSCity.getNameZh())) {
                    MessageBox.createSimpleDialog(EnrollConnectDeviceActivity.this, null,
                            getString(R.string.select_city), null, null);
                    return;
                }

                // if home number is above 5, stop to add home
                if (AuthorizeApp.shareInstance().getUserLocations().size() >= AirTouchConstants.MAX_HOME_NUMBER
                        && homeSpinner.getVisibility() == View.INVISIBLE) {
                    MessageBox.createSimpleDialog(EnrollConnectDeviceActivity.this, null,
                            getString(R.string.max_home), null, null);
                    return;
                }

                saveUserdataForFinalEnroll();
                mEnrollDeviceManager.connectDevice();

                disableConnectButton();
                mDialog = ProgressDialog.show(mContext, null, getString(R.string.enroll_connecting));
//                waitScanTextView.setText(getString(R.string.enroll_connecting));
//                connectPromptView.setVisibility(View.VISIBLE);
//                AnimationDrawable anim = (AnimationDrawable) loadingImageView.getBackground();
//                anim.start();
            }
        });
        cityNameLayout = (FrameLayout) findViewById(R.id.city_name_right_arrow_layout);
        cityNameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnrollConnectDeviceActivity.this, EditGPSActivity.class);
                intent.putExtra("currentGPS", mSelectedGPSCity);
                startActivityForResult(intent, SELECT_LOCATION_REQUEST);
            }
        });
        demoTextView = (TextView) findViewById(R.id.page2_title_tv2);
        demoTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.isDebugMode) {
                    Intent i = new Intent();
                    i.setClass(EnrollConnectDeviceActivity.this, EnrollConnectWifiActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();
                }
            }
        });
        initEditText();
    }

    private void initEditText() {
        mHomeNameEditText = (AirTouchEditText) findViewById(R.id.enroll_home_et);
        mHomeNameEditText.setInputMaxLength(14);
        mHomeNameEditText.getEditText().setTextColor(getResources().getColor(R.color.black));
        mHomeNameEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringUtil.specialCharacterFilter(mHomeNameEditText);
                StringUtil.maxCharacterFilter(mHomeNameEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                showEditTextOrSpinner(2);
                decideButtonState();
            }
        });
        mHomeNameEditText.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isHomeEditTextFocused = hasFocus;
                showEditTextOrSpinner(1);
                decideButtonState();
            }
        });
        mHomeNameEditText.getEditText().setTextSize(20);
        mDeviceNameEditText = (AirTouchEditText) findViewById(R.id.enroll_device_et);
        mDeviceNameEditText.setInputMaxLength(14);
        mDeviceNameEditText.getEditText().setTextSize(20);
        mDeviceNameEditText.getEditText().setTextColor(getResources().getColor(R.color.black));
        mDeviceNameEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringUtil.specialCharacterFilter(mDeviceNameEditText);
                StringUtil.maxCharacterFilter(mDeviceNameEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                decideButtonState();
            }
        });
        mDeviceNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                decideButtonState();
            }
        });
    }

    private void getCityName() {
        mSelectedGPSCity = getCityDBService().getCityByCode(AppConfig.shareInstance().getGpsCityCode());
        if (mSelectedGPSCity == null) {
            cityNameTextView.setText(getString(R.string.enroll_gps_fail));
            cityNameTextView.setTextColor(getResources().getColor(R.color.enroll_password_title));
            return;
        }

        if (mSelectedGPSCity.getNameZh() != null && mSelectedGPSCity.getNameEn() != null) {
            cityNameTextView.setText(AppConfig.shareInstance().getLanguage().equals(AppConfig
                    .LANGUAGE_ZH) ? mSelectedGPSCity.getNameZh() : mSelectedGPSCity.getNameEn());
            cityNameTextView.setTextColor(getResources().getColor(R.color.black));
        } else {
            cityNameTextView.setText(getString(R.string.enroll_gps_fail));
            cityNameTextView.setTextColor(getResources().getColor(R.color.enroll_password_title));
        }
    }

    private void getHomeName() {
        String[] homeStrings = getHomeStringArray();
        homeSpinnerTypeAdapter = new HomeSpinnerAdapter<>(this, homeStrings);
        homeSpinner.setAdapter(homeSpinnerTypeAdapter);
    }

    public String[] getHomeStringArray() {
        homeStringList.clear();

        ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();
        if ((userLocations != null) && (mSelectedGPSCity != null)) {
            for (int i = 0; i < userLocations.size(); i++) {
                if (userLocations.get(i).getCity().equals(mSelectedGPSCity.getCode()))
                    homeStringList.add(userLocations.get(i).getName());
            }
        }

        showEditTextOrSpinner(0);
        String[] stringsArray = new String[homeStringList.size()];
        return homeStringList.toArray(stringsArray);
    }

    private void initEnrollDeviceManager() {
        mEnrollDeviceManager = new EnrollDeviceManager(this, EnrollConnectDeviceActivity.this);
        mEnrollDeviceManager.setConnectionAttempted(false);
        mEnrollDeviceManager.processErrorCode();
        mEnrollDeviceManager.setFinishCallback(new EnrollDeviceManager.FinishCallback() {
            @Override
            public void onFinish() {
                if (mDialog != null)
                    mDialog.dismiss();

                enableConnectButton();
//                waitScanTextView.setText("");

                if (!checkIfDeviceAlreadyEnrolled()) {
                    if (mEnrollDeviceManager.isConnectionAttempted()) {
                        Intent i = new Intent();
                        i.setClass(EnrollConnectDeviceActivity.this, EnrollConnectWifiActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        finish();
                    }
                }
            }
        });
        mEnrollDeviceManager.setErrorCallback(new EnrollDeviceManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, String errorMsg) {
                if (mDialog != null)
                    mDialog.dismiss();

                MessageBox.createSimpleDialog(EnrollConnectDeviceActivity.this, null,
                        getResources().getString(R.string.enroll_error), null, quitEnroll);
            }
        });
    }

    /**
     * Show how to display EditText and Spinner of home name
     * There are 3 kind of situations
     *
     * @param flag flag == 0: User choose city so that EditText changes
     *             flag == 1: Focus on EditText or not (onFocusChange)
     *             flag == 2: User input home name on the EditText (afterTextChanged)
     */
    private void showEditTextOrSpinner(int flag) {
        switch (flag) {
            case 0:
                /*
                * If city is not available, spinner will not show automatically
                * the EditText need to be cleaned
                */
                if (homeStringList.isEmpty()) {
                    mHomeNameEditText.setEditorHint(getString(R.string.my_home));
                } else {
                    mHomeNameEditText.setEditorHint("");
                }
                break;

            case 1:
                if (isHomeEditTextFocused) {
                    homeSpinner.setVisibility(View.INVISIBLE);
                } else {
                    if (mHomeNameEditText.getEditorText().isEmpty()) {
                        homeSpinner.setVisibility(View.VISIBLE);
                        if (homeStringList.isEmpty()) {
                            mHomeNameEditText.setEditorHint(getString(R.string.my_home));
                        }
                    } else {
                        homeSpinner.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case 2:
                if (mHomeNameEditText.getEditorText().isEmpty()
                        && !isHomeEditTextFocused) {
                    homeSpinner.setVisibility(View.VISIBLE);
                } else {
                    homeSpinner.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void saveUserdataForFinalEnroll() {
        DIYInstallationState.setCityCode(mSelectedGPSCity.getCode());
        DIYInstallationState.setDeviceName(mDeviceNameEditText.getEditorText());
        if (mHomeNameEditText.getEditorText().isEmpty()) {
            DIYInstallationState.setHomeName(homeSpinnerTypeAdapter.getSelectedHome());
        } else {
            DIYInstallationState.setHomeName(mHomeNameEditText.getEditorText());
        }

    }

    private void decideButtonState() {
        if (mDeviceNameEditText.getEditorText().isEmpty()) {
            disableConnectButton();
        } else if (mHomeNameEditText.getEditorText().isEmpty()) {
            disableConnectButton();
            if (homeSpinnerTypeAdapter.getCount() > 0 && !isHomeEditTextFocused)
                enableConnectButton();
        } else {
            enableConnectButton();
        }

    }

    private void disableConnectButton() {
        startConnectButton.setClickable(false);
        startConnectButton.setTextColor(getResources().getColor(R.color.enroll_light_grey));
        startConnectButton.setBackgroundResource(R.drawable.enroll_back_button);
    }

    private void enableConnectButton() {
//        connectPromptView.setVisibility(View.INVISIBLE);
        startConnectButton.setVisibility(View.VISIBLE);
        startConnectButton.setClickable(true);
        startConnectButton.setTextColor(getResources().getColor(R.color.white));
        startConnectButton.setBackgroundResource(R.drawable.enroll_next_button);
    }

    private MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            UmengUtil.onEvent(EnrollConnectDeviceActivity.this,
                    UmengUtil.EventType.ENROLL_CANCEL.toString(), "page2");
            mEnrollDeviceManager.reconnectHomeWifi();

            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        finish();
                        overridePendingTransition(0, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    private MessageBox.MyOnClick continueEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            if (mEnrollDeviceManager.isConnectionAttempted()) {
                Intent i = new Intent();
                i.setClass(EnrollConnectDeviceActivity.this, EnrollConnectWifiActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
            }
        }
    };


    private Boolean checkIfDeviceAlreadyEnrolled() {
        String macId = "";
        DIYInstallationState.setIsDeviceAlreadyEnrolled(false);
        if (DIYInstallationState.getWAPIDeviceResponse() != null) {
            macId = DIYInstallationState.getWAPIDeviceResponse().getMacID();
        }
        ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();
        if ((userLocations != null) && (mSelectedGPSCity != null)) {
            for (int i = 0; i < userLocations.size(); i++) {
                ArrayList<DeviceInfo> deviceInfos = userLocations.get(i).getDeviceInfo();
                for (int j = 0; j < deviceInfos.size(); j++) {
                    if (deviceInfos.get(j).getMacID().equals(macId)) {
                        DIYInstallationState.setIsDeviceAlreadyEnrolled(true);
                        MessageBox.createTwoButtonDialog(this, null,
                                getString(R.string.device_already_registered), getString(R.string.no), quitEnroll,
                                getString(R.string.yes), continueEnroll);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
