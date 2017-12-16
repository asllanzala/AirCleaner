package com.honeywell.hch.airtouchv3.app.airtouch.controller.device;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.EnrollAccessManager;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.BackHomeRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.app.authorize.controller.UserLoginActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.BlurBackgroundView;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.RunStatus;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.view.wheelView.ArrayWheelAdapter;
import com.honeywell.hch.airtouchv3.framework.view.wheelView.NumericWheelAdapter;
import com.honeywell.hch.airtouchv3.framework.view.wheelView.WheelView;
import com.honeywell.hch.airtouchv3.framework.webservice.task.CleanTimeTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.BlurImageUtil;
import com.honeywell.hch.airtouchv3.lib.util.NetWorkUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by wuyuan on 10/23/15.
 */
public class ArriveHomeActivity extends BaseHasBackgroundActivity {

    public static final String HAS_DEVICE_FLAG = "has_device_flag";
    private static final String TAG = "ArriveHomeActivity";
    private final String CLASSFULLNAME = "com.honeywell.hch.airtouch.app.airtouch.controller.device.ArriveHomeActivity";
    private WheelView mHourWheel;
    private WheelView mMinuteWheel;
    private TextView mTellAirTouchTextView;
    private ImageView mClockImageView;
    private String[] mMinuteArray = {"00", "30"};
    private int isCleanTimeEnabled = -1;

    private Animation mAlphaOffAnimation;
    private TextView mArriveHomeTxt;

    private Dialog mDialog;

    private RelativeLayout mNoDeviceLayout;
    private RelativeLayout mHasDeviceLayout;

    private boolean isHasDevce = false;

    private TextView mEnrollNowTextView;
    private TextView mCancelTextView;
    private RelativeLayout mCancelImageView;
    private ImageView mNoDeviceImageView;
    private List<HomeDevice> homeDeviceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrive_home);


        isHasDevce = getIntent().getBooleanExtra(HAS_DEVICE_FLAG,false);
        initDynamicBackground();

        initView();
    }

    private void initView(){

        mBlurBackgroundView = (BlurBackgroundView) findViewById(R.id.home_background);
        mBlurBackgroundView.initDynmac(mUserLocationData);
        mBlurBackgroundView.initBackgroundResouce(R.raw.default_city_day_blur1, BlurImageUtil.OTHER_ACTVITIY_BLUR_RADIO);

        mNoDeviceLayout = (RelativeLayout)findViewById(R.id.no_device_layoutid);
        mHasDeviceLayout = (RelativeLayout)findViewById(R.id.has_deivce_layout_id);

        mCancelImageView = (RelativeLayout)findViewById(R.id.cancel_btn);
        mCancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (isHasDevce){
            mNoDeviceLayout.setVisibility(View.GONE);
            mHasDeviceLayout.setVisibility(View.VISIBLE);
            initHasDeviceLayout();
        }
        else{
            mNoDeviceLayout.setVisibility(View.VISIBLE);
            mHasDeviceLayout.setVisibility(View.GONE);
            initNoDeviceLayout();
        }


    }

    private void initNoDeviceLayout(){
        mEnrollNowTextView = (TextView)findViewById(R.id.begin_enroll);
        mNoDeviceImageView = (ImageView)findViewById(R.id.bottle_image_id);
        mNoDeviceImageView.setImageDrawable(getResources().getDrawable(R.drawable.bottle_nodevice));

        mNoDeviceImageView.setImageDrawable(getResources().getDrawable(R.drawable.group_setting));


        mEnrollNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decideWhichActivityGo();
            }
        });

        mCancelTextView = (TextView)findViewById(R.id.cancel_id);
        mCancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void decideWhichActivityGo(){
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
        if (!authorizeApp.isLoginSuccess()){
            MessageBox.createTwoButtonDialog(this, null,
                    getString(R.string.not_login), getString(R.string.yes),
                    enrollLoginButton, getString(R.string.no), null);
        }
        else{
            EnrollAccessManager.startIntent(ArriveHomeActivity.this, CLASSFULLNAME);
            finish();
        }
    }
    private MessageBox.MyOnClick enrollLoginButton = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            AppManager.shareInstance().getAuthorizeApp().setIsUserWantToEnroll(true);
            Intent intent = new Intent();
            intent.setClass(ArriveHomeActivity.this, UserLoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private void initHasDeviceLayout(){
        mArriveHomeTxt = (TextView)findViewById(R.id.clock_tv);
        mHourWheel = (WheelView) findViewById(R.id.hour_wheel);
        mMinuteWheel = (WheelView) findViewById(R.id.minute_wheel);
        mTellAirTouchTextView = (TextView) findViewById(R.id.tell_air_touch_tv);

        mTellAirTouchTextView.setOnClickListener(tellAirTouchOnClick);

        mHourWheel.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        mHourWheel.setCurrentItem(0);
        mHourWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCleanTimeEnabled != 1) {
                    mHourWheel.showItem();
                }
            }
        });
        mMinuteWheel.setAdapter(new ArrayWheelAdapter<>(mMinuteArray, 2));
        mMinuteWheel.setCurrentItem(0);
        mMinuteWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCleanTimeEnabled != 1) {
                    mMinuteWheel.showItem();
                }
            }
        });
        mClockImageView = (ImageView)findViewById(R.id.clock_iv);
        mArriveHomeTxt.setText(getResources().getString(R.string.arrive_home_text, mUserLocationData.getName()));
        showTimeAfterGetRunstatus();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * Back home
     */
    View.OnClickListener tellAirTouchOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (NetWorkUtil.isNetworkAvailable(ArriveHomeActivity.this) == false ) {
                MessageBox.createSimpleDialog(ArriveHomeActivity.this, null, getString(R.string.no_network), null, null);
                return;
            }
            mHourWheel.hideItem();
            mMinuteWheel.hideItem();

            Calendar calendar = Calendar.getInstance();
            int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
            int dstOffset = calendar.get(Calendar.DST_OFFSET);
            calendar.set(Calendar.HOUR_OF_DAY, mHourWheel.getCurrentItem());
            calendar.set(Calendar.MINUTE, Integer.parseInt(mMinuteArray[mMinuteWheel.getCurrentItem()]));
            calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            boolean isBeforeNow = calendar.getTime().before(currentCalendar.getTime());
            String settingTime = "";
            //判断设置时间是否早于当前
            if(isBeforeNow) {
                calendar.add(Calendar.DATE, 1);
                settingTime = sdf.format(calendar.getTime());
            } else {
                long time = calendar.getTime().getTime() - currentCalendar.getTime().getTime();
                long second = time/1000/60;
                if (second < 30) {
                    MessageBox.createSimpleDialog(ArriveHomeActivity.this, null, getString(R.string.arrive_time_in30min), null, null);
                    return;
                }
                settingTime = sdf.format(calendar.getTime());
            }
            mTellAirTouchTextView.setClickable(false);
            BackHomeRequest backHomeRequest = new BackHomeRequest();
            backHomeRequest.setTimeToHome(settingTime);
            backHomeRequest.setDeviceString("");

            if (isCleanTimeEnabled == 1) {
                backHomeRequest.setIsEnableCleanBeforeHome(false);
                isCleanTimeEnabled = 0;
                mDialog = LoadingProgressDialog.show(ArriveHomeActivity.this,getResources().getString(R.string.canceling_clock));
            } else if (isCleanTimeEnabled == 0) {
                backHomeRequest.setIsEnableCleanBeforeHome(true);
                isCleanTimeEnabled = 1;
                mDialog = LoadingProgressDialog.show(ArriveHomeActivity.this,getResources().getString(R.string.setting_clock));
            }
            CleanTimeTask cleanTimeTask = new CleanTimeTask(mLocationId,backHomeRequest,backHomeResponse);
            AsyncTaskExecutorUtil.executeAsyncTask(cleanTimeTask);


            mAlphaOffAnimation = AnimationUtils.loadAnimation(ArriveHomeActivity.this,
                    R.anim.control_alpha);
            mTellAirTouchTextView.startAnimation(mAlphaOffAnimation);
        }
    };


    final IActivityReceive backHomeResponse = new IActivityReceive() {

        @Override
        public void onReceive(ResponseResult resultResponse) {
            mTellAirTouchTextView.clearAnimation();
            mTellAirTouchTextView.setClickable(true);
            if (mDialog != null){
                mDialog.cancel();
            }
            if (resultResponse == null){
                return;
            }
            switch (resultResponse.getRequestId()) {
                case CLEAN_TIME:
                    if (resultResponse.isResult()) {
                        if (isCleanTimeEnabled == 1) {
                            mClockImageView.setImageResource(R.drawable.clock_blue);
                            mTellAirTouchTextView.setText(getString(R.string.cancel));
                        } else if (isCleanTimeEnabled == 0) {
                            mClockImageView.setImageResource(R.drawable.clock_white);
                            mTellAirTouchTextView.setText(getString(R.string.tell_air_touch));
                        }
                    } else {
                        if (isCleanTimeEnabled == 1) {
                            showToast(getString(R.string.tell_fail));
                            isCleanTimeEnabled = 0;
                        } else if (isCleanTimeEnabled == 0) {
                            showToast(getString(R.string.cancel_fail));
                            isCleanTimeEnabled = 1;
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };


    private void saveTimeToHome(String timeToHome) {
        if (timeToHome.equals(""))
            return;

        Calendar calendar = Calendar.getInstance();
        int hour = Integer.parseInt(timeToHome.substring(11, 13));
        int minute = Integer.parseInt(timeToHome.substring(14, 16));
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.add(Calendar.MILLISECOND, (zoneOffset + dstOffset));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");
        String settingTime = sdf.format(calendar.getTime());
        hour = Integer.parseInt(settingTime.substring(11, 13));
        minute = Integer.parseInt(settingTime.substring(14, 16));
        minute = (minute == 0 ? 0 : 1);
        mHourWheel.setCurrentItem(hour);
        mMinuteWheel.setCurrentItem(minute);
    }

    private void showTimeAfterGetRunstatus(){
        boolean isHasSetArriveTime = false;
        String arriveTime = "";
        homeDeviceList = mUserLocationData.getHomeDevicesList();
        if (homeDeviceList != null && homeDeviceList.size() > 0){
            for (int i = 0; i < homeDeviceList.size(); i++){
                HomeDevice homeDeviceItem = homeDeviceList.get(i);
                if (homeDeviceItem != null && homeDeviceItem instanceof AirTouchSeriesDevice){
                    RunStatus runStatus = ((AirTouchSeriesDevice)homeDeviceItem).getDeviceRunStatus();
                    if (runStatus != null && runStatus.isCleanBeforeHomeEnable()){
                        isHasSetArriveTime = true;
                        arriveTime = runStatus.getTimeToHome();
                        break;
                    }
                }
            }
        }
        if (isHasSetArriveTime) {
            isCleanTimeEnabled = 1;
            mClockImageView.setImageResource(R.drawable.clock_blue);
            mTellAirTouchTextView.setText(getString(R.string.cancel));
        } else {
            isCleanTimeEnabled = 0;
            mClockImageView.setImageResource(R.drawable.clock_white);
            mTellAirTouchTextView.setText(getString(R.string.tell_air_touch));
        }
        initAlpha();
        saveTimeToHome(arriveTime);
    }
    //set layout halfAlpha
    private void initAlpha () {
        if (! hasDeviceOnlineHome()) {
            mHasDeviceLayout.setAlpha(0.35f);
            mHourWheel.setClickable(false);
            mMinuteWheel.setClickable(false);
            mTellAirTouchTextView.setClickable(false);
        }
    }

    private boolean hasDeviceOnlineHome () {
        if (homeDeviceList != null && homeDeviceList.size() > 0){
            for (int i = 0; i < homeDeviceList.size(); i++) {
                HomeDevice homeDeviceItem = homeDeviceList.get(i);
                if (homeDeviceItem != null && homeDeviceItem instanceof AirTouchSeriesDevice){
                    if (homeDeviceItem.getDeviceInfo().getIsAlive()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void finish() {
        if (mDialog != null){
            mDialog.cancel();
        }
        super.finish();

    }
}
