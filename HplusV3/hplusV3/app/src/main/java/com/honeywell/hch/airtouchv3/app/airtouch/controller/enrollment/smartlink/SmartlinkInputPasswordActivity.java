package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv3.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.view.AirTouchEditText;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.lib.util.NetWorkUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyuan on 11/23/15.
 */
public class SmartlinkInputPasswordActivity  extends EnrollBaseActivity {

    private AirTouchEditText mWifiPassword;

    private Button mNextButton;

    private String mSsid;

    private String mUserPassword;

    private TextView mSsidTextView;

    private boolean isResume = false;

    private ImageView mLoadingImageView;

    private AnimationDrawable animationDrawable;

    private TextView mConnectingTextView;

    private ConnectAndFindDeviceManager mConnectAndFindDeviceManager;

    private String mDeviceMacWithcolon;

    private String mDeviceMacNocolon;

    private int mLocalIp;

    private AlertDialog mAlertDialog;

    private boolean mWholeFindingProcessIsRunning = false;

    private FrameLayout mBackImageView;

    private boolean isRegesteredByThisUser = false;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlink_inputpassword);


        mDeviceMacNocolon = SmartEnrollScanEntity.getEntityInstance().getmMacID();

        mDeviceMacWithcolon = StringUtil.getStringWithColon(mDeviceMacNocolon, 2);

        isRegesteredByThisUser = checkIfDeviceAlreadyEnrolled();

        initView();

        mConnectAndFindDeviceManager = new ConnectAndFindDeviceManager(this,mDeviceMacWithcolon,mDeviceMacNocolon);
    }

    private void initView(){
        mWifiPassword = (AirTouchEditText) findViewById(R.id.ssid_password_id);
        mWifiPassword.getEditText().setTextColor(getResources().getColor(R.color.black));
        mWifiPassword.setEditorHint(getString(R.string.enroll_password));
        mWifiPassword.getEditText().setGravity(Gravity.CENTER);
        mWifiPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mWifiPassword.getEditText().addTextChangedListener(mEditTextWatch2);
        mWifiPassword.setPasswordImage();
        mWifiPassword.setPasswordEyeOn(true);
        mWifiPassword.getEditText().setOnFocusChangeListener(onPassFocusChanged);

        mNextButton = (Button)findViewById(R.id.nextBtn_id);
        
        if (isRegesteredByThisUser){
            mNextButton.setText(getResources().getString(R.string.enroll_done));
        }

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isHasWifi = NetWorkUtil.isWifiAvailable(SmartlinkInputPasswordActivity.this);

                if (isHasWifi) {
                    startFindingProcess();
                } else {
                    showAlertDialog(R.string.smartlink_no_wifi);
                }

            }
        });

        mButton = (Button)findViewById(R.id.click_bg_id);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiPassword.clickEyeBtn();
            }
        });

        mSsidTextView = (TextView)findViewById(R.id.wifi_ssid_id);

        mLoadingImageView = (ImageView)findViewById(R.id.enroll_loading_iv);

        mConnectingTextView = (TextView)findViewById(R.id.connecting_txt_id);


    }

    private void startFindingProcess(){
        mWholeFindingProcessIsRunning = true;
        disableComponent();
        //show animation
        showConnecting();
        mConnectAndFindDeviceManager.startCheckNetworkConnectingThread();
    }

    public void startFindThread(){
        //send cooee and send udp
        mConnectAndFindDeviceManager.startConnectingAndFinding(mSsid, mUserPassword, mLocalIp);

    }

    private TextWatcher mEditTextWatch2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!StringUtil.isEmpty(mSsid) && mLocalIp != 0){
                decideNextButtonStatus();
            }
        }
    };


    private void decideNextButtonStatus() {
        if (!isPasswordEmpty() && !isSSIDEmpty()) {
            enableNextButton();
        } else {
            disableNextButton();
        }
    }

    private boolean isSSIDEmpty() {
        return StringUtil.isEmpty(mSsid);
    }

    private boolean isPasswordEmpty() {
         mUserPassword = mWifiPassword.getEditorText();
        return StringUtil.isEmpty(mUserPassword);
    }

    View.OnFocusChangeListener onPassFocusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mWifiPassword.setEditorHint("");
            } else {
                mWifiPassword.setEditorHint(getString(R.string.enroll_hint_password));
            }
        }
    };

    private void disableNextButton() {
        mNextButton.setVisibility(View.INVISIBLE);
    }

    private void enableNextButton() {
        mNextButton.setVisibility(View.VISIBLE);
    }




    @Override
    protected void onResume(){
        super.onResume();

        checkWifiStatusAndUpdate();
        isResume = true;
    }

    @Override
    protected  void onStop(){
        super.onStop();
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isResume = false;
        hideConnecting();

        if (mAlertDialog != null && mAlertDialog.isShowing()){
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
    }

    @Override
    protected  void dealNetworkConnect(){
        if (isResume){
            String mNewSid = NetWorkUtil.updateWifiInfo(this);
            if (!StringUtil.isEmpty(mNewSid) && !mNewSid.equals(mSsid)){
                updateSsidAndLocalIp(mNewSid);
            }
        }

    }

    @Override
    protected void dealNoNetwork(){
        if (isResume){
            showAlertDialog(R.string.smartlink_no_wifi);
        }
    }

    private void checkWifiStatusAndUpdate(){
        String mNewSid = NetWorkUtil.updateWifiInfo(this);
        if (!StringUtil.isEmpty(mNewSid) && !mNewSid.equals(mSsid)){
            updateSsidAndLocalIp(mNewSid);
        }
        else if(StringUtil.isEmpty(mNewSid)){
            showAlertDialog(R.string.smartlink_no_wifi);
        }
    }

    public void showAlertDialog(int messageStrId){
        if (mAlertDialog == null){
            mAlertDialog = MessageBox.createSimpleDialog(this, "", getResources().getString(messageStrId),
                    getResources().getString(R.string.ontravel_yes), new MessageBox.MyOnClick() {
                        @Override
                        public void onClick(View v) {
                            if (mAlertDialog != null){
                                mAlertDialog.cancel();
                                mAlertDialog = null;
                            }
                        }
                    });
            try{
                mAlertDialog.show();
            } catch (Exception e){

            }

        }

        enableComponent();
    }

    private void updateSsidAndLocalIp(String mNewSid){

        if (mAlertDialog != null && mAlertDialog.isShowing()){
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
        mSsid = mNewSid;
        mSsidTextView.setText(mNewSid);
        mWifiPassword.setEditorText(null);
        mWifiPassword.setEditorHint(getString(R.string.smartlink_password_str));

        mLocalIp = NetWorkUtil.getNetworkIp(this);
        if (mLocalIp == 0){
            showAlertDialog(R.string.smartlink_no_wifi);
        }
    }


    private void showConnecting(){

        mConnectingTextView.setVisibility(View.VISIBLE);
        mLoadingImageView.setVisibility(View.VISIBLE);

        if (animationDrawable == null){
            animationDrawable = (AnimationDrawable) mLoadingImageView.getDrawable();
        }
        animationDrawable.start();
    }

    private void hideConnecting(){

        mConnectingTextView.setVisibility(View.INVISIBLE);
        mLoadingImageView.setVisibility(View.INVISIBLE);

        if (animationDrawable != null && animationDrawable.isRunning()){
            animationDrawable.stop();
        }

    }

    /**
     * when finding the device,we should disable all the component
     */
    private void disableComponent(){
         mNextButton.setClickable(false);
         mNextButton.setEnabled(false);

        mWifiPassword.getEditText().setEnabled(false);
        mWifiPassword.getEditText().setFocusableInTouchMode(false);
    }

    public void enableComponent(){

        //the finding process is done
        mWholeFindingProcessIsRunning = false;

        mNextButton.setClickable(true);
        mNextButton.setEnabled(true);

        mWifiPassword.getEditText().setEnabled(true);
        mWifiPassword.getEditText().setFocusableInTouchMode(true);

        hideConnecting();
    }

    public void connectingTimeout(){
        enableComponent();

        // go to the timeout activity
        gotoOtherActivity(SmartlinkConnectTimeoutActivity.class);
    }

    public void findDeviceSuccess(){
        enableComponent();

        if (!isRegesteredByThisUser){
            //check the device registered by the user
            gotoOtherActivity(SmartlinkRegisterDeviceActivity.class);
        }
        else{
            SmartEnrollScanEntity.getEntityInstance().setIsRegisteredByThisUser(isRegesteredByThisUser);
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

    }

    private void gotoOtherActivity(Class<?> cls){
        Intent i = new Intent();
        i.setClass(this, cls);
        startActivity(i);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // when the progress is finding the device , can not be back
        if(keyCode == KeyEvent.KEYCODE_BACK && mWholeFindingProcessIsRunning) {
            return false;
        }
        else if(keyCode == KeyEvent.KEYCODE_BACK && !mWholeFindingProcessIsRunning){
            goBackActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private Boolean checkIfDeviceAlreadyEnrolled() {
        DIYInstallationState.setIsDeviceAlreadyEnrolled(false);
        List<UserLocationData> userLocations = AppManager.shareInstance().getUserLocationDataList();
        if (userLocations != null) {
            for (int i = 0; i < userLocations.size(); i++) {
                ArrayList<HomeDevice> homeDevicesList = userLocations.get(i).getHomeDevicesList();
                for (int j = 0; j < homeDevicesList.size(); j++) {
                    if (homeDevicesList.get(j).getDeviceInfo().getMacID().equalsIgnoreCase(mDeviceMacNocolon)) {
                        DIYInstallationState.setIsDeviceAlreadyEnrolled(true);

                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void goBackActivity(){
        Intent intent = new Intent(mContext, SmartLinkChooseActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.enroll_back_layout:
                if (!mWholeFindingProcessIsRunning){
                    goBackActivity();
                }
                break;
        }
    }

}
