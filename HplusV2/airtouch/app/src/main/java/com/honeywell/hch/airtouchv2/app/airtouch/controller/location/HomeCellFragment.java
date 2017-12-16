package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.device.DeviceActivity;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.device.HouseActivity;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.airtouch.view.AirTouchWorstDevice;
import com.honeywell.hch.airtouchv2.app.airtouch.view.OutDoorWeather;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv2.framework.model.RunStatus;
import com.honeywell.hch.airtouchv2.framework.model.xinzhi.WeatherData;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
/**
 * Created by nan.liu on 2/13/15.
 */
public class HomeCellFragment extends BaseLocationFragment {
    private static final String TAG = "AirTouchHomeCellFragment";

    private static final String ARG_HOME_INDEX = "homeIndex";

    private final String AUTO = "Auto";
    private final String OFF = "Off";
    private final String SLEEP = "Sleep";
    private final String QUICK = "QuickClean";
    private final String SILENT = "Silent";
    private final String MANUAL = "Manual";

    private View myHouseView = null;
    private AirTouchWorstDevice mAirTouchWorstDevice = null;
    private View mHomeReminderView = null;
    private View mWindowLightView = null;

    private UserLocation mUserLocation = null;

    private boolean isOutdoorOpen = false;
    private boolean isReminderEverShowed = false;
    private View mHomeCellView = null;

    private RelativeLayout mWindowLayout;
    private float[] mHouseBottomDistance = {5.5f, 0.5f, 4f, 5.5f, 4.5f, 7.5f};



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param homeIndex
     * @return A new instance of fragment HomeCellFragment.
     */
    public static HomeCellFragment newInstance(FragmentActivity activity, int homeIndex) {
        HomeCellFragment fragment = new HomeCellFragment();
        fragment.setActivity(activity);
        Bundle args = new Bundle();
        args.putInt(ARG_HOME_INDEX, homeIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateViewData(int homeIndex, UserLocation userLocation) {
        mHomeIndex = homeIndex;
        mUserLocation = userLocation;
    }

    public void bindData2View() {
        if (mUserLocation != null && mUserLocation.getDeviceInfo() != null &&
                mUserLocation.getHomeDevices().size() > 0
                && !AppConfig.shareInstance().isHomePageCover()) {
            initDevice();
            showWorstDevice();
            showReminder();
        } else {
            if (mHomeIndex == ((MainActivity)getFragmentActivity()).getCurrentHomeIndex())
            {
                ((MainActivity)getFragmentActivity()).setScrollViewCanScroll(false);

            }
            setVisibility(mAirTouchWorstDevice, View.INVISIBLE);
            setVisibility(mHomeReminderView, View.INVISIBLE);
        }
    }

    private void showReminder() {

        if (isReminderEverShowed || mHomeReminderView == null || (myHouseView != null && !myHouseView.isClickable()))
            return;

        AirTouchSeriesDevice worstDevice = mUserLocation.getWorstDevice();


        boolean isIndoorOpen = (worstDevice != null &&
                worstDevice.getDeviceRunStatus() != null && worstDevice.getDeviceRunStatus().getmPM25Value() > AirTouchConstants.MAX_PMVALUE_LOW);

        //give priority to the value of getRunStatusResponse.if it is not null.use the value of scenariomode
        //otherwise use getAirCleanerFanModeSwitch

        if (worstDevice != null) {

            boolean isAlive = false;
            if (worstDevice.getDeviceInfo() != null) {
                isAlive = worstDevice.getDeviceInfo().getIsAlive();
            }
            if (worstDevice.getDeviceRunStatus() != null && worstDevice.getDeviceRunStatus().getScenarioMode().equals
                    ("Off") && isAlive){

                if (isOutdoorOpen || isIndoorOpen)
                {
                    setVisibility(mHomeReminderView, View.VISIBLE);
                    mHomeReminderView.requestLayout();
                    isReminderEverShowed = true;
                }
            }
            else if (worstDevice.getDeviceRunStatus() == null && isAlive)
            {
                if (isOutdoorOpen || isIndoorOpen)
                {
                    setVisibility(mHomeReminderView, View.VISIBLE);
                    mHomeReminderView.requestLayout();
                    isReminderEverShowed = true;
                }
            }
        }

    }

    @Override
    protected void handleWeatherData(WeatherData weatherData) {
        super.handleWeatherData(weatherData);
        isOutdoorOpen = Integer.valueOf(weatherData.getWeather().get(0).getNow().getAirQuality()
                .getAirQualityIndex().getPm25()) > 110;
        showReminder();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        if (getArguments() != null) {
            mHomeIndex = getArguments().getInt(ARG_HOME_INDEX);
        }

        ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();

        if (userLocations != null && mHomeIndex > 0 && userLocations.size() > mHomeIndex - 1){
            mUserLocation = userLocations.get(mHomeIndex - 1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mHomeCellView == null || myHouseView == null){

            mHomeCellView = inflater.inflate(R.layout.fragment_homecell, container, false);
            initView(mHomeCellView);
            bindData2View();

            if (mHomeIndex > 0) {
                ViewHelper.setTranslationY(myHouseView,
                        DensityUtil.getScreenHeight() * mHouseBottomDistance[mHomeIndex % 6] / 100 + DensityUtil.dip2px(7));
                ViewHelper.setTranslationY(mAirTouchWorstDevice,
                        DensityUtil.getScreenHeight() * mHouseBottomDistance[mHomeIndex % 6] / 100);
                ViewHelper.setTranslationY(mHomeReminderView,
                        DensityUtil.getScreenHeight() * mHouseBottomDistance[mHomeIndex % 6] / 100);
            } else {
                myHouseView.setVisibility(View.GONE);
            }
            if (mUserLocation != null && mUserLocation.getName() != null) {
                mCitySiteView.updateView(mUserLocation.getCity());
                mCity = getCityDBService().getCityByCode(mUserLocation.getCity());
                setHomeNameText(getCityDBService().getCityByCode(mCity.getCode()),
                        mUserLocation.getName());
            }
            myHouseView.setOnClickListener(houseClickListener);
            mAirTouchWorstDevice.setOnClickListener(deviceClickListener);
            mHomeReminderView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mHomeReminderView.setVisibility(View.GONE);
                }
            });
        }
        if (mHomeCellView.getParent() != null){
            ViewGroup p = (ViewGroup) mHomeCellView.getParent();
            p.removeAllViews();

            if (mAirTouchWorstDevice != null && mUserLocation != null){
                mAirTouchWorstDevice.updateView(mUserLocation.getWorstDevice());
            }

        }
        return mHomeCellView;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        myHouseView = view.findViewById(R.id.my_house_view);
        myHouseView.setClickable(true);
        mHouseImageView = (ImageView) view.findViewById(R.id.my_house_image_view);
        mAirTouchWorstDevice = (AirTouchWorstDevice) view.findViewById(R.id.device_worst);
        mAirTouchWorstDevice.setClickable(true);
        mHomeReminderView = view.findViewById(R.id.home_reminder_view);
        mOutDoorWeatherView = (OutDoorWeather) view.findViewById(R.id.outdoor_weather_view);

        mWindowLightView = view.findViewById(R.id.window_light_view);
        mWindowLayout = (RelativeLayout)view.findViewById(R.id.window_layout);

        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        if (mHouseImageView != null) {
            mHouseImageView.setImageResource(isDaylight ? R.drawable.big_house :
                    R.drawable.big_house_night);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume(){
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        mWindowLightView.setVisibility(isDaylight ? View.INVISIBLE : View.VISIBLE);
        if (!isDaylight) {
            mWindowLightView.startAnimation(AnimationUtils.loadAnimation(getFragmentActivity(),
                    R.anim.window_alpha));
        } else {
            mWindowLightView.clearAnimation();
        }
        mCitySiteView.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                mCitySiteView.setCityView();
                //  computerScale();
            }
        }, 1000);

        super.onResume();
    }

    private void initDevice() {

//        int mDeviceNumber = mUserLocation.getDeviceInfo().size() * mUserLocation
//                .getHomeDevicesPM25().size();
//        if (mDeviceNumber > 0) {
//            int worstPM25 = -1;
//            ArrayList<HomeDevice> homeDevices = new ArrayList<>();
//            ArrayList<HomeDevicePM25> homeDevicesPM25 = mUserLocation.getHomeDevicesPM25();
//            ArrayList<DeviceInfo> deviceInfos = mUserLocation.getDeviceInfo();
//
//            for (int i = 0; i < deviceInfos.size(); i++) {
//                HomeDevice homeDevice = new HomeDevice();
//                HomeDevicePM25 homeDevicePM25 = homeDevicesPM25.get(i);
//                homeDevice.setHomeDevicePm25(homeDevicePM25);
//                homeDevice.setDeviceInfo(deviceInfos.get(i));
//                homeDevices.add(homeDevice);
//                if (worstPM25 < homeDevicePM25.getPM25Value()) {
//                    worstPM25 = homeDevicePM25.getPM25Value();
//                    mUserLocation.setWorstDevice(homeDevice);
//                }
//            }
//            mUserLocation.setHomeDevices(homeDevices);

            ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();
            if (userLocations != null && mHomeIndex > 0 && userLocations.size() > mHomeIndex - 1){
                userLocations.set(mHomeIndex - 1, mUserLocation);

            }

        }


    private void showWorstDevice() {
        //if no device or started to HouseActivity
        if (mUserLocation.getWorstDevice() == null || mUserLocation.getWorstDevice().getDeviceRunStatus()== null
                || mUserLocation.getWorstDevice().getDeviceInfo() == null
                || (myHouseView != null && !myHouseView.isClickable())) {
            return;
        }
        if (mAirTouchWorstDevice != null) {

            if (mHomeIndex == ((MainActivity)getFragmentActivity()).getCurrentHomeIndex())
            {
                ((MainActivity)getFragmentActivity()).setEmotionPagerLocalId(mUserLocation.getLocationID());
                ((MainActivity)getFragmentActivity()).setScrollViewCanScroll(true);
            }
            mAirTouchWorstDevice.setVisibility(View.VISIBLE);
            mAirTouchWorstDevice.updateView(mUserLocation.getWorstDevice());
//            getDeviceInfo();
            displayCleanTime(mUserLocation.getWorstDevice().getDeviceRunStatus());
        }

    }

    /**
     * get clean time info.
     */
    private void getDeviceInfo() {
        String sessionId = AuthorizeApp.shareInstance().getSessionId();
        int deviceId = mUserLocation.getWorstDevice().getDeviceInfo().getDeviceID();
        int getDeviceStatusRequestId = getRequestClient().getDeviceStatus(deviceId, sessionId,
                mReceiveResponse);
        addRequestId(getDeviceStatusRequestId);
    }

    IReceiveResponse mReceiveResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            removeRequestId(httpRequestResponse.getRandomRequestID());
            switch (httpRequestResponse.getRequestID()) {
                case GET_DEVICE_STATUS:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            RunStatus runStatusResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    RunStatus.class);
                            displayCleanTime(runStatusResponse);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Display clean time for back home.
     */
    private void displayCleanTime(RunStatus runStatus) {
        int speed = 0;
        if (runStatus == null || !runStatus.getIsAlive() || (runStatus.getMobileCtrlFlags() != null
                && runStatus.getMobileCtrlFlags().equals("DISABLED")))
            return;

        // Parse device status to speed and mode.
        String speedString = runStatus.getFanSpeedStatus();
        String modeString = runStatus.getScenarioMode();
        if (speedString != null) {
            if (speedString.contains("Speed")) {
                speed = Integer.parseInt(speedString.substring(6, 7));
            }
        }

        // show mode view
        switch (modeString) {
            case AUTO:
                speed = decideAutoSpeed();
                break;
            case SLEEP:
                speed = 1;
                break;
            case QUICK:
                speed = 7;
                break;
            case SILENT:
                speed = 2;
                break;
            default:
                break;
        }
        if (runStatus.getCleanTime() == null)
            return;

        if (runStatus.getCleanTime().length == 7 && runStatus.isCleanBeforeHomeEnable()) {
            if (speed > 0) {
                int cleanTime = runStatus.getCleanTime()[speed - 1];
                if (cleanTime > 60) {
                    cleanTime /= 60;
                    mAirTouchWorstDevice.updateCleanTime(String.format
                            (getString(R.string.clean_time_hour), cleanTime));
                } else if (cleanTime > 0) {
                    mAirTouchWorstDevice.updateCleanTime(String.format
                            (getString(R.string.clean_time_minute), cleanTime));
                }
                return;
            }
        }
        mAirTouchWorstDevice.updateCleanTime("");
    }

    private int decideAutoSpeed() {
        int speed = 0;
        AirTouchSeriesDevice worstDevice = mUserLocation.getWorstDevice();
        //add exception deal when runstatus is null
        if (worstDevice != null && worstDevice.getDeviceRunStatus() != null)
        {
            int pmValue = worstDevice.getDeviceRunStatus().getmPM25Value();
            if (pmValue < AirTouchConstants.MAX_PMVALUE_LOW) {
                speed = 1;
                return 1;
            } else if (pmValue < AirTouchConstants.MAX_PMVALUE_MIDDLE) {
                speed = 3;
            } else {
                speed = 6;
            }
        }
        return speed;

    }

    @Override
    protected void switchTimeView() {
        super.switchTimeView();
        if (mWindowLightView == null)
            return;
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        mWindowLightView.setVisibility(isDaylight ? View.INVISIBLE : View.VISIBLE);
        if (!isDaylight) {
            mWindowLightView.startAnimation(AnimationUtils.loadAnimation(getFragmentActivity(), R
                    .anim.window_alpha));
        } else {
            mWindowLightView.clearAnimation();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    private View.OnClickListener houseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Umeng statistic
            MobclickAgent.onEvent(getFragmentActivity(), "home_house_event");

            AppConfig.shareInstance().setHomePageCover(true);

            ((MainActivity) getFragmentActivity()).setmViewPagerScroll(false);
            myHouseView.setClickable(false);
            mAirTouchWorstDevice.setClickable(false);
            Intent intent = new Intent(getFragmentActivity(), HouseActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(HouseActivity.ARG_HOME_INDEX, mHomeIndex);
            bundle.putInt(HouseActivity.ARG_LOCATION, mUserLocation.getLocationID());
            intent.putExtras(bundle);
            AppConfig.shareInstance().setHomePageCover(true);
            getFragmentActivity().startActivityForResult(intent, DeviceActivity
                    .HOME_HOUSE_REQUEST_CODE);
            hideHomePageView();

        }
    };


    private View.OnClickListener deviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Umeng statistic
            MobclickAgent.onEvent(getFragmentActivity(), "home_device_event");
            AppConfig.shareInstance().setHomePageCover(true);
            myHouseView.setClickable(false);
            mAirTouchWorstDevice.setClickable(false);
            ((MainActivity) getFragmentActivity()).setmViewPagerScroll(false);
            AuthorizeApp.shareInstance().setCurrentDeviceId(mUserLocation.getWorstDevice().getDeviceInfo().getDeviceID());
            hideHomePageView();
            Intent intent = new Intent(getFragmentActivity(), DeviceActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(HouseActivity.ARG_LOCATION, mUserLocation.getLocationID());
            intent.putExtras(bundle);
            getFragmentActivity().startActivityForResult(intent, DeviceActivity.HOME_DEVICE_REQUEST_CODE);
        }
    };

    private void hideHomePageView() {
        hideAnimation(myHouseView, 1000);
        if (mUserLocation != null) {
            int mDeviceNumber = mUserLocation.getAirTouchSDeviceNumber();
            if (mDeviceNumber > 0) {
                hideAnimation(mAirTouchWorstDevice, 1000);
            }
        }

        ((MainActivity)getFragmentActivity()).hideNearHillAnimation(mHomeIndex);

        Animation hideToBottom = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim.out_to_bottom);
        hideToBottom.setStartOffset(600);
        startAnimation(mFarawayMountainImageView, hideToBottom);
        startAnimation(mCitySiteView, hideToBottom);
        setVisibility(mFarawayMountainImageView, View.INVISIBLE);
        setVisibility(mCitySiteView, View.INVISIBLE);
        setVisibility(mHomeReminderView, View.INVISIBLE);
        isReminderEverShowed = false;
        setAlpha(mOutDoorWeatherView, 0.3f);
        if (getFragmentActivity() != null) {
            ((MainActivity) getFragmentActivity()).setMenuButtonAlpha(0.3f);
        }
    }

    public void showHomePageViewNoAnimation() {
        setVisibility(myHouseView, View.VISIBLE);
        myHouseView.setClickable(true);

        if (mUserLocation != null ) {
            int mDeviceNumber = mUserLocation.getAirTouchSDeviceNumber();
            if (mDeviceNumber > 0) {
                setVisibility(mAirTouchWorstDevice, View.VISIBLE);
                mAirTouchWorstDevice.setClickable(true);
            }
        }
        ((MainActivity)getFragmentActivity()).showNearHillNoAnimation(mHomeIndex);

        setVisibility(mFarawayMountainImageView, View.VISIBLE);

        setVisibility(mCitySiteView, View.VISIBLE);
        setAlpha(mOutDoorWeatherView, 1f);
        if (getFragmentActivity() != null) {
            ((MainActivity) getFragmentActivity()).setMenuButtonAlpha(1f);
        }
    }

//    public void showHomePageView() {
//        showAnimation(myHouseView, 400);
//        if (mUserLocation != null && mUserLocation.getDeviceInfo() != null && mUserLocation
//                .getHomeDevicesPM25() != null) {
//            int mDeviceNumber = mUserLocation.getDeviceInfo().size() * mUserLocation
//                    .getHomeDevicesPM25().size();
//            if (mDeviceNumber > 0) {
//                showAnimation(mAirTouchWorstDevice, 400);
//            }
//        }
//        Animation showFromBottom = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim.in_from_bottom);
//        startAnimation(mFarawayMountainImageView, showFromBottom);
//        startAnimation(mNearbyMountainImageView, showFromBottom);
//        startAnimation(mHomeNameTextView, showFromBottom);
//        startAnimation(mCitySiteView, showFromBottom);
//        setVisibility(mFarawayMountainImageView, View.VISIBLE);
//        setVisibility(mNearbyMountainImageView, View.VISIBLE);
//        setVisibility(mHomeNameTextView, View.VISIBLE);
//        setVisibility(mHomeLocationTextView, View.VISIBLE);
//        setVisibility(mCitySiteView, View.VISIBLE);
//        setAlpha(mOutDoorWeatherView, 1f);
//        if (getFragmentActivity() != null) {
//            ((MainActivity) getFragmentActivity()).setMenuButtonAlpha(1f);
//        }
//
//    }

    public void showHomePageViewNoCityAnimation() {
        showAnimation(myHouseView, 400);
        if (mUserLocation != null ) {
            int mDeviceNumber = mUserLocation.getAirTouchSDeviceNumber();
            if (mDeviceNumber > 0) {
                showAnimation(mAirTouchWorstDevice, 400);
            }
        }
        ((MainActivity)getFragmentActivity()).showNearHillNoAnimation(mHomeIndex);

        setVisibility(mFarawayMountainImageView, View.VISIBLE);

        setVisibility(mCitySiteView, View.VISIBLE);
        setAlpha(mOutDoorWeatherView, 1f);
        if (getFragmentActivity() != null) {
            ((MainActivity) getFragmentActivity()).setMenuButtonAlpha(1f);
        }

    }
//    private void houseScaleUp()
//    {
//        AnimatorSet popAnimation = new AnimatorSet();
//        float positionY = ViewHelper.getY(view);
//        int screenHeight = DensityUtil.getScreenHeight();
//        popAnimation.playTogether(
//                ObjectAnimator.ofFloat(view, "alpha", 0, 1),
//                ObjectAnimator.ofFloat(view, "translationY", screenHeight - positionY, 0)
//        );
//        popAnimation.setInterpolator(new OvershootInterpolator());
//        popAnimation.setDuration(mPopUpDuration[index]);
//        popAnimation.start();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * get the location has the device
     * @return boolean has device or not
     */
    public boolean getWorseDeviceIsShow()
    {
        boolean isHaveDevice = false;
        if (mAirTouchWorstDevice != null && mAirTouchWorstDevice.getVisibility() == View.VISIBLE)
        {
            isHaveDevice = true;
        }
        return isHaveDevice;
    }

}