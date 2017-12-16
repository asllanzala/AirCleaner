package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.SmartEnrollScanEntity;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.view.AirTouchEditText;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.honeywell.hch.airtouchv3.lib.util.UmengUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Enrollment Step 2 - SmartPhone communicate to Air Touch.
 * 1) sendPhoneName - send phone's BUILD.NO to Air Touch.
 * 2) getWapiKey -  get Air Touch  key for password encrypt.
 * 3) getWapiDevice - get Air Touch  mac and crc.
 * <p/>
 * Ask user to input device name, city name and home name
 * Store these data to DIYInstallationState
 */
public class EnrollConnectDeviceBaseActivity extends EnrollBaseActivity {
    protected TextView demoTextView;
    protected FrameLayout cityNameLayout;
    protected TextView cityNameTextView;
    protected Button startConnectButton;
    protected Context mContext;
    protected static Dialog mDialog;

    protected AirTouchEditText mDeviceNameEditText;
    protected AirTouchEditText mHomeNameEditText;
    protected EnrollDeviceManager mEnrollDeviceManager;
    protected City mSelectedGPSCity;
    protected static final int SELECT_LOCATION_REQUEST = 10;
    // home name EditText and Spinner
    protected boolean isHomeEditTextFocused = false;
    protected Spinner homeSpinner;
    protected HomeSpinnerAdapter<String> homeSpinnerTypeAdapter;
    protected ArrayList<String> homeStringList = new ArrayList<>();

    protected final static int AIR_TOUCH_X_TYPE = 2;
    protected final static String AIR_TOUCH_X_STING = "Air Touch-X";


   protected AlertDialog mAlertDialog;

    protected void initWhenCreate(){
        mContext = EnrollConnectDeviceBaseActivity.this;
        initEnrollDeviceManager();
        initView();
        getCityName();
        getHomeName();
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




    private void initView() {
        homeSpinner = (Spinner) findViewById(R.id.home_spinner);
        cityNameTextView = (TextView) findViewById(R.id.city_name_tv);

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

                    showDialog(getString(R.string.select_city),false);
                    return;
                }

                // if home number is above 5, stop to add home
                if (AppManager.shareInstance().getUserLocationDataList().size() >= AirTouchConstants.MAX_HOME_NUMBER && isAddNewHome() && homeSpinner.getVisibility() == View.INVISIBLE) {
                    showDialog(getString(R.string.max_home),false);
                    return;
                }

                clickStartConnectBtn();

                disableConnectButton();
                mDialog = LoadingProgressDialog.show(mContext, getString(R.string.enroll_connecting));

            }
        });
        cityNameLayout = (FrameLayout) findViewById(R.id.city_name_right_arrow_layout);
        cityNameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnrollConnectDeviceBaseActivity.this, EditGPSActivity.class);
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
                    i.setClass(EnrollConnectDeviceBaseActivity.this, EnrollConnectWifiActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();
                }
            }
        });
//        if (DIYInstallationState.getDeviceType() == AIR_TOUCH_X_TYPE)
            demoTextView.setText(getResources().getString(SmartEnrollScanEntity.getEntityInstance().getDeviceName()));
        initEditText();
    }


    private void showDialog(String msg,final boolean isNeedFinish){
        if (mAlertDialog == null || !mAlertDialog.isShowing()){
            mAlertDialog = MessageBox.createSimpleDialog(EnrollConnectDeviceBaseActivity.this, null,
                    msg, null, new MessageBox.MyOnClick() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.cancel();
                            mAlertDialog = null;
                            if (isNeedFinish){
                                finishTheProcess();
                            }
                        }
                    });
            try{
                mAlertDialog.show();
            } catch (Exception e){

            }

        }
    }

    // used home name is not allowed to add again
    private boolean isAddNewHome() {
        for (int i = 0; i < AppManager.shareInstance().getUserLocationDataList().size(); i++) {
            UserLocationData userLocation = AppManager.shareInstance().getUserLocationDataList().get(i);
            if (mHomeNameEditText.getEditorText().equals(userLocation.getName())
                    && (userLocation.getCity().equals(mSelectedGPSCity.getCode()))) {
                return false;
            }
        }
        return true;
    }

    private void initEditText() {
        mHomeNameEditText = (AirTouchEditText) findViewById(R.id.enroll_home_et);
        mHomeNameEditText.getEditText().setTextColor(getResources().getColor(R.color.black));
        mHomeNameEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringUtil.maxCharacterFilter(mHomeNameEditText);
                StringUtil.addOrEditHomeFilter(mHomeNameEditText);
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
        mDeviceNameEditText.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        mDeviceNameEditText.getEditText().setTextSize(20);
        mDeviceNameEditText.getEditText().setTextColor(getResources().getColor(R.color.black));
        mDeviceNameEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringUtil.maxCharacterFilter(mDeviceNameEditText);
                StringUtil.addDeviceFilter(mDeviceNameEditText);
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
        mSelectedGPSCity = AppConfig.shareInstance().getCityFromDatabase(AppConfig.shareInstance().getGpsCityCode());
        if (mSelectedGPSCity.getNameEn() == null || mSelectedGPSCity.getNameZh() == null) {
            cityNameTextView.setText(getString(R.string.enroll_select_city));
            cityNameTextView.setTextColor(getResources().getColor(R.color.enroll_password_title));
            return;
        }

        // India Version
        if (mSelectedGPSCity.getNameZh() != null && mSelectedGPSCity.getNameEn() != null
                && isUserAccountCountryMatchLocatedCountry()) {
            cityNameTextView.setText(AppConfig.shareInstance().getLanguage().equals(AppConfig
                    .LANGUAGE_ZH) ? mSelectedGPSCity.getNameZh() : mSelectedGPSCity.getNameEn());
            cityNameTextView.setTextColor(getResources().getColor(R.color.black));
        } else {
            cityNameTextView.setText(getString(R.string.enroll_select_city));
            cityNameTextView.setTextColor(getResources().getColor(R.color.enroll_password_title));
        }
    }

    protected void getHomeName() {
        String[] homeStrings = getHomeStringArray();
        homeSpinnerTypeAdapter = new HomeSpinnerAdapter<>(this, homeStrings);
        homeSpinner.setAdapter(homeSpinnerTypeAdapter);
    }

    public String[] getHomeStringArray() {
        homeStringList.clear();

        List<UserLocationData> userLocations = AppManager.shareInstance().getUserLocationDataList();
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
        mEnrollDeviceManager.setConnectionAttempted(false);
        mEnrollDeviceManager.processErrorCode();
        mEnrollDeviceManager.setFinishCallback(new EnrollDeviceManager.FinishCallback() {
            @Override
            public void onFinish() {
                if (mDialog != null)
                    mDialog.dismiss();

                enableConnectButton();

                dealManagerFinishBackcall();
            }
        });
        mEnrollDeviceManager.setErrorCallback(new EnrollDeviceManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, String errorMsg) {
                if (mDialog != null)
                    mDialog.dismiss();

                String msg = errorMsg == null ? getResources().getString(R.string.enroll_error) : errorMsg;
                showDialog(msg,true);
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


    protected void decideButtonState() {
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

    protected MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            UmengUtil.onEvent(EnrollConnectDeviceBaseActivity.this,
                    UmengUtil.EventType.ENROLL_CANCEL.toString(), "page2");

            isNeedReConnectWifi();

            finishTheProcess();
        }
    };

    public void finishTheProcess(){
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                    finish();
//                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAlertDialog != null && mAlertDialog.isShowing()){
            mAlertDialog.cancel();
            mAlertDialog = null;
        }

    }




    protected  void dealManagerFinishBackcall(){
    }

    protected void clickStartConnectBtn(){
    }

    protected void isNeedReConnectWifi(){
    }

    private Boolean isUserAccountCountryMatchLocatedCountry() {
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();

        if (authorizeApp.getCountryCode() == null)
            return true;
        if (authorizeApp.getGPSCountry() == null)
            return true;

        // China account located in India
        if (!AppConfig.shareInstance().isIndiaAccount()
                && AppConfig.shareInstance().isLocatedInIndia())
            return false;

        // India account located in China
        if (AppConfig.shareInstance().isIndiaAccount()
                && !AppConfig.shareInstance().isLocatedInIndia())
            return false;

        return true;
    }

}
