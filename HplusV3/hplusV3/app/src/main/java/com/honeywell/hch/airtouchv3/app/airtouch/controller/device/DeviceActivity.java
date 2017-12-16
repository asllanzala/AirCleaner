package com.honeywell.hch.airtouchv3.app.airtouch.controller.device;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.AllDeviceActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.BlurBackgroundView;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseFragmentActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.view.ScrollLayout;
import com.honeywell.hch.airtouchv3.lib.util.BlurImageUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * The Vertical Fragment for Control and Filter
 * Created by nan.liu on 2/24/15.
 */
public class DeviceActivity extends BaseFragmentActivity {
    public static final String ARG_DEVICE = "device";
    public static final String ARG_LOCATION = "location";
    private static final String TAG = "AirTouchDevice";

    private ScrollLayout deviceScrollLayout;
    private DeviceControlFragment mControlFragment;
    private FilterFragment mFilterFragment;
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<>();
    private UserLocationData mUserLocation = null; // this location may not be current location

    private HomeDevice mThisHomeDevice;

    private boolean isAirPremium = false;

    public static final int HOME_DEVICE_REQUEST_CODE = 11;
    public static final int HOUSE_DEVICE_REQUEST_CODE = 12;
    public static final int HOME_HOUSE_REQUEST_CODE = 13;

    private BlurBackgroundView mBlurBackgroundView;
    private boolean isFirstTimeScroll = true;

    private AlertDialog mAlertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.TAG = TAG;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_device_control);
        deviceScrollLayout = (ScrollLayout) findViewById(R.id.control_filter_scroll);
        deviceScrollLayout.setOnViewChangeListener(scrollListener);
        deviceScrollLayout.setOnFirstPageDownListener(controlCloseListener);
        deviceScrollLayout.setIsScroll(true);
        deviceScrollLayout.setOnViewScrollingListener(scrollListener2);

        Bundle bundle = getIntent().getExtras();
        int locationId = bundle.getInt("location");
        mUserLocation = AppManager.shareInstance().getLocationWithId(locationId);

        if (mUserLocation == null){
            return;
        }

        int deviceId = bundle.getInt("deviceId");
        mThisHomeDevice = mUserLocation.getHomeDeviceWithId(deviceId);

        mBlurBackgroundView = (BlurBackgroundView) findViewById(R.id.home_background);
        mBlurBackgroundView.initDynmac(mUserLocation);
        mBlurBackgroundView.initBackgroundResouce(R.raw.default_city_day_blur1, BlurImageUtil.OTHER_ACTVITIY_BLUR_RADIO);

        if (mThisHomeDevice == null){
            mAlertDialog = MessageBox.createSimpleDialog(this, null, getString(R.string.enroll_error),
                    null, new MessageBox.MyOnClick() {
                        @Override
                        public void onClick(View v) {
                            dismissDialog();
                            finish();
                        }
                    });
            return;
        }

        loadFragment();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.startTimer(BlurImageUtil.OTHER_ACTVITIY_BLUR_RADIO);
        }
    }
    public UserLocationData getmUserLocation() {
        return mUserLocation;
    }

    public HomeDevice getThisHomeDevice(){

        isAirPremium = AppManager.shareInstance().isAirtouchP(mThisHomeDevice.getDeviceInfo().getmDeviceType());
        return mThisHomeDevice;
    }


    public boolean isAirPremium(){
        return isAirPremium;
    }
    public boolean isAirTouch450OrJD () {
        return AppManager.shareInstance().isAirtouch450(getThisHomeDevice().getDeviceType());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        destroyBlurBackground();
        Intent intent = new Intent();
        intent.putExtra(ARG_DEVICE, AppManager.shareInstance().getCurrentDeviceId());
        intent.putExtra(AllDeviceActivity.ARG_LOCATION_ID, mUserLocation.getLocationID());
        setResult(RESULT_OK, intent);
        super.finish();
        overridePendingTransition(R.anim.finish_zoomin, R.anim.finish_zoomout);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyBlurBackground();
        dismissDialog();
    }

    private void dismissDialog(){
        if (mAlertDialog != null){
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
    }

    private void destroyBlurBackground(){
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.destroyed();
            mBlurBackgroundView = null;
        }
    }


    public void loadFragment() {
        mControlFragment = DeviceControlFragment.newInstance(mThisHomeDevice);
        mFilterFragment = FilterFragment.newInstance(mThisHomeDevice);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.control_panel, mControlFragment);
        fragmentTransaction.replace(R.id.filter_panel, mFilterFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mControlFragment.setOnControlClickListener(new DeviceControlFragment.OnControlClickListener() {
            @Override
            public void onControl(boolean isOnControl) {
                deviceScrollLayout.setIsScroll(!isOnControl);
            }
        });
        mControlFragment.setOnFilterClickListener(new DeviceControlFragment.OnFilterClickListener() {
            @Override
            public void onClick() {
                // Umeng statistic
                MobclickAgent.onEvent(DeviceActivity.this, "filter_status_click_event");

                if (deviceScrollLayout.getCurScreen() == 0) {
                    deviceScrollLayout.snapToScreen(1);
                } else {
                    deviceScrollLayout.snapToScreen(0);
                }
            }
        });
        mFilterFragment.setOnControlTutorialRemovedListener(new FilterFragment.OnControlTutorialRemovedListener() {
            @Override
            public void remove() {
                mControlFragment.removeControlTutorial();
            }
        });

        mFilterFragment.setOnFilterAnimationListener(new FilterFragment.OnFilterAnimationListener() {
            @Override
            public void onAnimation(int filter) {
                mControlFragment.setFilterAnimation(filter);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface MyTouchListener {
        public void onTouchEvent(MotionEvent event);
    }

    /**
     * register touch listener for fragment
     *
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * unregister touch listener for fragment
     *
     * @param listener
     */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }


    /**
     * ScrollListener - to catch control page or filter page
     */
    ScrollLayout.OnViewChangeListener scrollListener = new ScrollLayout.OnViewChangeListener() {
        @Override
        public void OnViewChange(int view) {
            if (view == 0) {
                AppConfig.isFilterScrollPage = false;
            } else if (view == 1) {
                AppConfig.isFilterScrollPage = true;
                mFilterFragment.showFilterTutorial();
                mControlFragment.showControlTutorialForFilter();
            }
        }
    };

    ScrollLayout.OnFirstPageDownListener controlCloseListener = new ScrollLayout.OnFirstPageDownListener() {
        @Override
        public void onPageDwon() {
//            finish();
//            overridePendingTransition(0, 0);
        }
    };

    ScrollLayout.OnViewScrollingListener scrollListener2 = new ScrollLayout.OnViewScrollingListener() {
        @Override
        public void onSrcollY(float scrollY) {
            if (isFirstTimeScroll){
                mFilterFragment.setAirPremiumFilterPosition();
                isFirstTimeScroll = false;
            }
        }
    };

}
