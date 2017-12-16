package com.honeywell.hch.airtouchv2.app.airtouch.controller.device;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.CommTaskResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.HomeDevicePM25;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.RecordCreatedResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.airtouch.view.AirTouchDeviceView;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IRefreshEnd;
import com.honeywell.hch.airtouchv2.framework.sensor.ShakeListener;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Jin Qian on 3/3/2015.
 * 1) init all devices data
 * 2) init all devices view
 */
public class HouseActivity extends BaseActivity {

    private String TAG = "AirTouchHouse";
    public static final String ARG_HOME_INDEX = "homeIndex";
    public static final String ARG_LOCATION = "location";

    private int mHomeIndex = 0;
    private UserLocation mUserLocation = null; // this location may not be current location
    private static ProgressDialog mDialog;
    public ShakeListener mShakeListener = null;
    private Animation scaleBigAnimation;
    private ImageView bigHouseImageView;
    private RelativeLayout tutorialMask;
    private Bitmap bigHouseBitmap;
    private RelativeLayout oneDeviceLayout, twoDeviceLayout, threeDeviceLayout, fourDeviceLayout, fiveDeviceLayout;
    private int[] mResourceId = {R.id.one_device, R.id.first_of_two_device, R.id.second_of_two_device,
            R.id.first_of_three_device, R.id.second_of_three_device, R.id.third_of_three_device,
            R.id.first_of_four_device, R.id.second_of_four_device, R.id.third_of_four_device, R.id.forth_of_four_device,
            R.id.first_of_five_device, R.id.second_of_five_device, R.id.third_of_five_device, R.id.forth_of_five_device, R.id.fifth_of_five_device};
    private ArrayList<AirTouchDeviceView> mAirTouchDevices = new ArrayList<>();
//    private AirTouchDevice mAirTouchDevice;

    private boolean isShakeEnabled = false;
    private boolean isShaking = false;
    private String mSessionId;
    private int mCommTaskId;
    private String mUserId;
    private int mUserHomeNumber;
    private int mCommTaskCount = 0;
    private int getHomePm25Count = 0;
    private int testCount = 0;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private ArrayList<ArrayList<HomeDevicePM25>> homeDevicesList = new ArrayList<>();
    private static final int COMM_TASK_TIMEOUT_COUNT = 80;

    private float[] houseBottomDistance = {5.5f, 0.5f, 4f, 5.5f, 4.5f, 7.5f};

    private ImageView myHouseView = null;

    private RelativeLayout bigHouseLayout = null;
    private ImageView mNearbyMountainImageView = null;
    private int[] nearbyMountainDayIDs = {R.drawable.image01, R.drawable.image11,
            R.drawable.image21, R.drawable.image31, R.drawable.image41, R.drawable.image51};


    private AnimatorSet animatorSet = null;

    private int mHomeAirTouchSeriesDeviceNumber = 0;
    private int mHomeDeviceTotalNumber = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.TAG = TAG;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_house);

        Bundle bundle = getIntent().getExtras();
        mHomeIndex = bundle.getInt(ARG_HOME_INDEX);

        int locationId = bundle.getInt(ARG_LOCATION);
        mUserLocation = AuthorizeApp.shareInstance().getLocationWithId(locationId);
        mUserId = AuthorizeApp.shareInstance().getUserID();

        initView();

        //house zoom
        mNearbyMountainImageView = (ImageView) findViewById(R.id.nearby_mountain);
        mNearbyMountainImageView.setImageResource(nearbyMountainDayIDs[mHomeIndex % 6]);

        myHouseView = (ImageView) findViewById(R.id.my_house_button);
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        myHouseView.setImageResource(isDaylight ? R.drawable.big_house :
                R.drawable.big_house_night);


        bigHouseLayout = (RelativeLayout) findViewById(R.id.bighouse_layout);
        ViewHelper.setTranslationY(myHouseView, DensityUtil.getScreenHeight() *
                houseBottomDistance[mHomeIndex % 6] / 100);

        int houseWidth = DensityUtil.getScreenWidth() - DensityUtil.dip2px(151);
        ViewHelper.setTranslationX(myHouseView, houseWidth);

        // Shake handler
        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                if (isShakeEnabled && !isShaking) {
                    isShaking = true;

                    mSessionId = AuthorizeApp.shareInstance().getSessionId();
                    TccClient.sharedInstance().getLocation(mUserId, mSessionId, mReceiveResponse);
                }
            }
        });
        bigHouseImageView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        mUserId = AuthorizeApp.shareInstance().getUserID();
        mSessionId = AuthorizeApp.shareInstance().getSessionId();
        isShakeEnabled = true;


        myHouseView.postDelayed(new Runnable() {

            @Override
            public void run() {
                setSmallHouseAnimation();
                //  computerScale();
            }
        }, 300);

    }

    private void setSmallHouseAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bigHouseImageView.setVisibility(View.VISIBLE);
                myHouseView.setVisibility(View.GONE);
                showHouseTutorial();
                if (!isHasNullPoint()) {
                    initDeviceView(mUserLocation.getHomeDevices().size());
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ViewHelper.setPivotX(myHouseView, myHouseView.getWidth() * 0.48f);
        ViewHelper.setPivotY(myHouseView, myHouseView.getHeight() * 0.79f);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        myHouseView.getGlobalVisibleRect(startBounds);

        bigHouseLayout.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) finalBounds.width() / startBounds.width();

        } else {
            startScale = (float) finalBounds.height() / startBounds.height();

            // Extend start bounds vertically
        }

        animatorSet.playTogether(
                ObjectAnimator.ofFloat(myHouseView, "scaleX", startScale),
                ObjectAnimator.ofFloat(myHouseView, "scaleY", startScale));

        animatorSet.start();
    }


    @Override
    protected void onPause() {
        super.onPause();

        isShakeEnabled = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mShakeListener != null) {
            mShakeListener.stop();
        }
    }

    private void initView() {
        tutorialMask = (RelativeLayout) findViewById(R.id.tutorial_mask);
        tutorialMask.setMinimumWidth(DensityUtil.dip2px(450));
        bigHouseImageView = (ImageView) findViewById(R.id.big_house_iv);
        bigHouseImageView.setVisibility(View.INVISIBLE);

        bigHouseBitmap = ((BitmapDrawable) bigHouseImageView.getDrawable()).getBitmap();
        bigHouseImageView.setOnTouchListener(backOnClick);
        oneDeviceLayout = (RelativeLayout) findViewById(R.id.one_device_layout);
        twoDeviceLayout = (RelativeLayout) findViewById(R.id.two_device_layout);
        threeDeviceLayout = (RelativeLayout) findViewById(R.id.three_device_layout);
        fourDeviceLayout = (RelativeLayout) findViewById(R.id.four_device_layout);
        fiveDeviceLayout = (RelativeLayout) findViewById(R.id.five_device_layout);
        scaleBigAnimation = AnimationUtils.loadAnimation(this, R.anim.device_scale_big);
        for (int id : mResourceId) {
            AirTouchDeviceView device = (AirTouchDeviceView) findViewById(id);
            device.setResourceId(id);
            device.setAirTouchLongClick(handleDeleteDevice);
            device.setDeviceControlClick(mDeviceControlClick);
            mAirTouchDevices.add(device);
        }
    }

    private void initDeviceView(int deviceNumber) {
        oneDeviceLayout.setVisibility(View.INVISIBLE);
        twoDeviceLayout.setVisibility(View.INVISIBLE);
        threeDeviceLayout.setVisibility(View.INVISIBLE);
        fourDeviceLayout.setVisibility(View.INVISIBLE);
        fiveDeviceLayout.setVisibility(View.INVISIBLE);

        if ((mAirTouchDevices.size() == 0) || (mUserLocation.getHomeDevices().size() == 0))
            return;

        switch (deviceNumber) {
            case 1:
                oneDeviceLayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    oneDeviceLayout.setScaleX(1.15f);
                    oneDeviceLayout.setScaleY(1.15f);
                }
                mAirTouchDevices.get(0).updateView(mUserLocation.getHomeDevices().get(0));
                break;
            case 2:
                twoDeviceLayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    twoDeviceLayout.setScaleX(1.1f);
                    twoDeviceLayout.setScaleY(1.1f);
                }
                mAirTouchDevices.get(1).updateView(mUserLocation.getHomeDevices().get(0));
                mAirTouchDevices.get(2).updateView(mUserLocation.getHomeDevices().get(1));
                break;
            case 3:
                threeDeviceLayout.setVisibility(View.VISIBLE);
                mAirTouchDevices.get(3).updateView(mUserLocation.getHomeDevices().get(0));
                mAirTouchDevices.get(4).updateView(mUserLocation.getHomeDevices().get(1));
                mAirTouchDevices.get(5).updateView(mUserLocation.getHomeDevices().get(2));
                break;
            case 4:
                fourDeviceLayout.setVisibility(View.VISIBLE);
                mAirTouchDevices.get(6).updateView(mUserLocation.getHomeDevices().get(0));
                mAirTouchDevices.get(7).updateView(mUserLocation.getHomeDevices().get(1));
                mAirTouchDevices.get(8).updateView(mUserLocation.getHomeDevices().get(2));
                mAirTouchDevices.get(9).updateView(mUserLocation.getHomeDevices().get(3));
                break;
            case 5:
                fiveDeviceLayout.setVisibility(View.VISIBLE);
                mAirTouchDevices.get(10).updateView(mUserLocation.getHomeDevices().get(0));
                mAirTouchDevices.get(11).updateView(mUserLocation.getHomeDevices().get(1));
                mAirTouchDevices.get(12).updateView(mUserLocation.getHomeDevices().get(2));
                mAirTouchDevices.get(13).updateView(mUserLocation.getHomeDevices().get(3));
                mAirTouchDevices.get(14).updateView(mUserLocation.getHomeDevices().get(4));
            default:
                break;
        }
    }

    View.OnTouchListener backOnClick = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getY() >= bigHouseBitmap.getHeight()) {
                return false;

            }
            int eventX = (int) event.getX();
            int eventY = (int) event.getY();

            if (eventX >= 0 && eventX < bigHouseBitmap.getWidth() && eventY >= 0 && bigHouseBitmap.getPixel(eventX, eventY) == 0) {


                float getX = event.getX();
                float rawX = event.getRawX();
                float currentWidth = (getX - rawX) + DensityUtil.getScreenWidth() * 0.78f;

                animatorSet = new AnimatorSet();
                animatorSet.setDuration(500);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        for (int i = 0; i < mAirTouchDevices.size(); i++) {
                            mAirTouchDevices.get(i).setClickable(false);
                            mAirTouchDevices.get(i).setLongClickable(false);
                            mAirTouchDevices.get(i).setEnabled(false);
                        }

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        if (myHouseView != null) {
                            myHouseView.setVisibility(View.GONE);
                        }
                        finish();

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                ViewHelper.setPivotX(bigHouseLayout, currentWidth);
                ViewHelper.setPivotY(bigHouseLayout, DensityUtil.getScreenHeight() * 0.87f);
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(bigHouseLayout, "alpha", 1.0f, 0f),
                        ObjectAnimator.ofFloat(bigHouseLayout, "scaleX", 0.16f),
                        ObjectAnimator.ofFloat(bigHouseLayout, "scaleY", 0.16f));


                animatorSet.start();

                Intent backIntent = new Intent(AirTouchConstants.ANIMATION_SHOW_CITY_LAYOUT_ACTION);
                backIntent.putExtra(ARG_LOCATION, mUserLocation.getLocationID());
                sendBroadcast(backIntent);

//
//               Animation houseAnimation = AnimationUtils.loadAnimation(HouseActivity.this,
//                        R.anim.zoomin);

//                finish();
                // test temp
//                oneDeviceLayout.setVisibility(View.INVISIBLE);
//                twoDeviceLayout.setVisibility(View.INVISIBLE);
//                threeDeviceLayout.setVisibility(View.INVISIBLE);
//                fourDeviceLayout.setVisibility(View.INVISIBLE);
//                fiveDeviceLayout.setVisibility(View.INVISIBLE);
//                switch (testCount) {
//                    case 1:
//                        oneDeviceLayout.setVisibility(View.VISIBLE);
//                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
//                            oneDeviceLayout.setScaleX(1.1f);
//                            oneDeviceLayout.setScaleY(1.1f);
//                        }
//                        break;
//                    case 2:
//                        twoDeviceLayout.setVisibility(View.VISIBLE);
//                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
//                            twoDeviceLayout.setScaleX(1.1f);
//                            twoDeviceLayout.setScaleY(1.1f);
//                        }
//                        break;
//                    case 3:
//                        threeDeviceLayout.setVisibility(View.VISIBLE);
//                        break;
//                    case 4:
//                        fourDeviceLayout.setVisibility(View.VISIBLE);
//                        break;
//                    case 5:
//                        fiveDeviceLayout.setVisibility(View.VISIBLE);
//                        break;
//                    default:
//                        testCount = 0;
//                        break;
//                }
//                testCount++;

                return true;
            }

            return false;
        }
    };

    /**
     * fetch all devices data to home/location.
     */
    private void initAllDevices() {
//        int deviceNumber = mUserLocation.getHomeDevicesPM25().size();
//
//        if (deviceNumber > 0) {
//            ArrayList<HomeDevice> homeDevices = new ArrayList<>();
//            ArrayList<HomeDevicePM25> homeDevicesPM25 = mUserLocation.getHomeDevicesPM25();
//            ArrayList<DeviceInfo> devicesInfo = mUserLocation.getDeviceInfo();
//            for (int i = 0; i < devicesInfo.size(); i++) {
//                HomeDevice homeDevice = new HomeDevice();
//                homeDevice.setHomeDevicePm25(homeDevicesPM25.get(i));
//                homeDevice.setDeviceInfo(devicesInfo.get(i));
//                homeDevices.add(homeDevice);
//            }
//            mUserLocation.setHomeDevices(homeDevices);
//            initDeviceView(devicesInfo.size());
//        }

        initDeviceView(mUserLocation.getHomeDevices().size());
    }

    IReceiveResponse mReceiveResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            switch (httpRequestResponse.getRequestID()) {
                case DELETE_DEVICE:
                    if (httpRequestResponse.getStatusCode() == StatusCode.CREATE_OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            RecordCreatedResponse recordCreatedResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    RecordCreatedResponse.class);
                            mCommTaskId = recordCreatedResponse.getId();
                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "commTaskId：" + mCommTaskId);
                            TccClient.sharedInstance().getCommTask(mCommTaskId, mSessionId, mReceiveResponse);
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                case GET_LOCATION:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        mUserLocations.clear();
                        homeDevicesList.clear();
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            try {
                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                                mUserHomeNumber = responseArray.length();
                                getHomePm25Count = 0;

                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseJSON = responseArray.getJSONObject(i);
                                    UserLocation getLocationResponse = new Gson().fromJson(responseJSON.toString(), UserLocation.class);
                                    mUserLocations.add(getLocationResponse);
                                    mHomeDeviceTotalNumber += getLocationResponse.getAirTouchSDeviceNumber();

                                }

                                for (int i = 0; i < responseArray.length(); i++) {
                                    final UserLocation locationItem = mUserLocations.get(i);
                                    // get devices of each home
                                    locationItem.loadHomeDevicesData(new IRefreshEnd() {
                                        @Override
                                        public void notifyDataRefreshEnd() {
                                            mHomeAirTouchSeriesDeviceNumber++;

                                            if (mHomeAirTouchSeriesDeviceNumber == mHomeDeviceTotalNumber) {
                                                if (isShaking) {
                                                    isShaking = false;
                                                }
                                                afterGetLocationSuccess();
                                            }
                                        }
                                    });

                                    // restore mUserLocation
                                    if (mUserLocation.getLocationID() == mUserLocations.get(i).getLocationID()) {
                                        mUserLocation = mUserLocations.get(i);
                                    }
                                }

                                if (mHomeDeviceTotalNumber == 0){
                                    afterGetLocationSuccess();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;
                case COMM_TASK:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            CommTaskResponse commTaskResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    CommTaskResponse.class);

                            if (commTaskResponse.getState().equals("Succeeded")) {

                                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "Delete succeed!");
                                TccClient.sharedInstance().getLocation(mUserId, mSessionId,
                                        mReceiveResponse);
                                // stop commTaskPollingThread
                                mCommTaskCount = 100;

//                                mAirTouchDevice.setVisibility(View.INVISIBLE);
                            }

                            if (commTaskResponse.getState().equals("Failed")) {
                                if (mDialog != null) {
                                    mDialog.dismiss();
                                }

                                MessageBox.createSimpleDialog(HouseActivity.this, null,
                                        getString(R.string.delete_device_fail), null, null);
                                // stop commTaskPollingThread
                                mCommTaskCount = 100;
                            }

                            mCommTaskCount++;
                            if (mCommTaskCount < COMM_TASK_TIMEOUT_COUNT) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        TccClient.sharedInstance().getCommTask(mCommTaskId,
                                                mSessionId, mReceiveResponse);
                                    }
                                }).start();
                            } else {
                                if (mCommTaskCount == COMM_TASK_TIMEOUT_COUNT) {
                                    if (mDialog != null) {
                                        mDialog.dismiss();
                                    }
                                    MessageBox.createSimpleDialog(HouseActivity.this, null,
                                            getString(R.string.control_timeout), null, null);
                                }
                                mCommTaskCount = 0;
                            }
                        } else {
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void afterGetLocationSuccess(){
        if (mDialog != null) {
            mDialog.dismiss();
        }
        initDeviceView(mUserLocation.getDeviceInfo().size());

        // Important! Save all devices info.
        AuthorizeApp.shareInstance().setUserLocations(mUserLocations);

        //old location has been cleared,so need to reset one as current home
        AuthorizeApp.shareInstance().resetCurrentHome();
    }
    private AirTouchDeviceView.DeviceControlClick mDeviceControlClick = new AirTouchDeviceView
            .DeviceControlClick() {
        @Override
        public void onClick(HomeDevice homeDevice) {
            // Umeng statistic
            MobclickAgent.onEvent(HouseActivity.this, "house_device_event");

            AuthorizeApp.shareInstance().setCurrentDeviceId(homeDevice.getDeviceInfo().getDeviceID());
            Intent intent = new Intent(HouseActivity.this, DeviceActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(HouseActivity.ARG_LOCATION, mUserLocation.getLocationID());
            intent.putExtras(bundle);
            startActivityForResult(intent, DeviceActivity.HOUSE_DEVICE_REQUEST_CODE);
        }
    };

    private AirTouchDeviceView.LongClick handleDeleteDevice = new AirTouchDeviceView.LongClick() {
        @Override
        public void handle() {
//            mAirTouchDevice.startAnimation(scaleBigAnimation);
            MessageBox.createTwoButtonDialog(HouseActivity.this, null,
                    getString(R.string.delete_device),
                    getString(R.string.yes), deleteDevice, getString(R.string.no), null);
        }
    };

    private MessageBox.MyOnClick deleteDevice = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            mSessionId = AuthorizeApp.shareInstance().getSessionId();
            mDialog = ProgressDialog.show(HouseActivity.this, null, getString(R.string.deleting_device));
            int deviceId = AuthorizeApp.shareInstance().getCurrentDeviceId();
            TccClient.sharedInstance().deleteDevice(deviceId, mSessionId, mReceiveResponse);
        }
    };

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
            MessageBox.createSimpleDialog(HouseActivity.this, null,
                    getString(R.string.no_network), null, null);
            return;
        }

        if (isShaking)
            isShaking = false;

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
//                MessageBox.createSimpleDialog(HouseActivity.this, null,
//                        errorResponse.getMessage(), null, null);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
                MessageBox.createSimpleDialog(HouseActivity.this, null,
                        getString(R.string.enroll_error), null, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.createSimpleDialog(HouseActivity.this, null,
                    getString(R.string.enroll_error), null, null);
        }
    }

    private Boolean isHasNullPoint() {
        return mUserLocation == null || mUserLocation.getHomeDevices() == null
                || mUserLocation.getDeviceInfo() == null;
    }

    public void showHouseTutorial() {
        if (AppConfig.isHouseTutorial) {
            tutorialMask.setVisibility(View.INVISIBLE);
        } else {
            tutorialMask.setVisibility(View.VISIBLE);
            tutorialMask.setOnClickListener(tutorialOnClick);
        }
    }

    View.OnClickListener tutorialOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tutorialMask.setVisibility(View.INVISIBLE);
            AppConfig appConfig = AppConfig.shareInstance();
            appConfig.setIsHouseTutorial(true);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DeviceActivity.HOUSE_DEVICE_REQUEST_CODE:
                if (data != null && data.getSerializableExtra(DeviceActivity.ARG_DEVICE) !=
                        null) {
                    int controlDeviceIndex = data.getIntExtra(DeviceActivity.ARG_DEVICE, -1);
                    HomeDevice controlDevice = mUserLocation.getHomeDeviceWithDeviceId(controlDeviceIndex);
                    if (controlDevice != null) {
                        for (int i = 0; i < mUserLocation.getDeviceInfo().size(); i++) {
                            if (controlDevice.getDeviceInfo().getDeviceID() == mUserLocation
                                    .getDeviceInfo().get(i).getDeviceID()) {
                                mUserLocation.getHomeDevices().set(i, controlDevice);
                                AuthorizeApp.shareInstance().getUserLocations().set(mHomeIndex -
                                        1, mUserLocation);
                                initDeviceView(mUserLocation.getHomeDevices().size());
                                break;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        AirTouchConstants.houseActivityList.clear();
        Intent backIntent = new Intent();
        backIntent.putExtra(ARG_LOCATION, mUserLocation.getLocationID());
        setResult(RESULT_OK, backIntent);
        super.finish();
    }
}
