package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.device.DeviceActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.EnrollAccessManager;
import com.honeywell.hch.airtouchv3.app.airtouch.view.AirTouchWorstDevice;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.app.authorize.controller.UserLoginActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.AllDeviceActivity;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.RunStatus;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Weather;
import com.honeywell.hch.airtouchv3.framework.webservice.task.LongTimerRefreshTask;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by nan.liu on 2/13/15.
 */
public class HomeCellFragment extends BaseLocationFragment{
    private static final String TAG = "AirTouchHomeCellFragment";

    private static final String ARG_HOME_INDEX = "homeIndex";
    private static final String ARG_LOCATION_INDEX = "locationIndex";

    private final static int SPEED_7 = 7;
    private final static int SPEED_9 = 9;

    private static final int OUTDOOR_OPEN_PM25_VALUE = 110;

    private final String AUTO = "Auto";
    private final String OFF = "Off";
    private final String SLEEP = "Sleep";
    private final String QUICK = "QuickClean";
    private final String SILENT = "Silent";
    private final String MANUAL = "Manual";

    private AirTouchWorstDevice mAirTouchWorstDevice = null;

    private boolean isOutdoorOpen = false;
    private View mHomeCellView = null;

    private ImageView mDeviceIconImage;

    private RelativeLayout mUnderLinedeviceLayout;

    private ImageView mDeviceDotImagView;

    private boolean isNeedRefreshByUpdateHomeCellData = true;

    private RelativeLayout mThreeButtonLayout;
    private RelativeLayout mIndiaLayout;
    private Button mEnrollButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param homeIndex
     * @return A new instance of fragment HomeCellFragment.
     */
    public static HomeCellFragment newInstance(FragmentActivity activity, int homeIndex,int locationIndex) {
        HomeCellFragment fragment = new HomeCellFragment();
        fragment.setActivity(activity);
        Bundle args = new Bundle();
        args.putInt(ARG_HOME_INDEX, homeIndex);
        args.putInt(ARG_LOCATION_INDEX, locationIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        if (getArguments() != null) {
            mHomeIndex = getArguments().getInt(ARG_HOME_INDEX);
            locationIndex = getArguments().getInt(ARG_LOCATION_INDEX);
        }

        List<UserLocationData> userLocations = AppManager.shareInstance().getUserLocationDataList();

        if (userLocations != null && locationIndex < userLocations.size()) {
            mUserLocation = userLocations.get(locationIndex);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mHomeCellView == null) {

            mHomeCellView = inflater.inflate(R.layout.fragment_homecell, container, false);
            initView(mHomeCellView);
            if (mUserLocation != null) {
                mCity = AppConfig.shareInstance().getCityFromDatabase(mUserLocation.getCity());
                setHomeNameText(mCity, mUserLocation.getName());
            }
            bindData2View();

            updateWeatherData();
        }
        if (mHomeCellView.getParent() != null) {
            LogUtil.log(LogUtil.LogLevel.INFO,"HomeCell","mHomeCellView.getParent() != null");
            ViewGroup p = (ViewGroup) mHomeCellView.getParent();
            p.removeAllViews();

            if (mAirTouchWorstDevice != null && mUserLocation != null) {
                mAirTouchWorstDevice.updateView(mUserLocation.getDefaultDevice());
            }

        }
        return mHomeCellView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mAirTouchWorstDevice = (AirTouchWorstDevice) view.findViewById(R.id.device_worst);
        mAirTouchWorstDevice.setClickable(true);
        mAirTouchWorstDevice.setOnClickListener(mWorseDeviceClickListener);

        mDeviceIconImage = (ImageView) view.findViewById(R.id.device_icon);
        mUnderLinedeviceLayout = (RelativeLayout) view.findViewById(R.id.main_device_layout);
        mDeviceIconImage.setOnClickListener(mWorseDeviceClickListener);

        mDeviceDotImagView = (ImageView)view.findViewById(R.id.device_dot_icon);
        mDeviceDotImagView.setOnClickListener(mDeviceClickListener);

        isViewInint = true;

        mThreeButtonLayout = (RelativeLayout) view.findViewById(R.id.four_btn_layout);
        mIndiaLayout = (RelativeLayout) view.findViewById(R.id.india_no_device);
        mEnrollButton = (Button) view.findViewById(R.id.india_enroll_btn);
        mEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decideWhichActivityGo();
            }
        });

        // India version
        if (!AppConfig.shareInstance().isIndiaAccount()) {
            mThreeButtonLayout.setVisibility(View.VISIBLE);
            mIndiaLayout.setVisibility(View.INVISIBLE);
        } else {
            mThreeButtonLayout.setVisibility(View.INVISIBLE);
            mIndiaLayout.setVisibility(View.VISIBLE);
        }


        startSwitchBackground();
    }

    @Override
    public void bindData2View() {
        if (mUserLocation != null) {
            showDefaultDevice();
        }
        if (mUnderLinedeviceLayout != null && mAirTouchWorstDevice != null){
            mUnderLinedeviceLayout.setClickable(true);
            mAirTouchWorstDevice.setClickable(true);
        }
    }

    @Override
    protected void handleWeatherData(Weather weatherData) {
        super.handleWeatherData(weatherData);
        if (mWeatherData != null){
            isOutdoorOpen = Integer.valueOf(weatherData.getNow().getAirQuality()
                    .getAirQualityIndex().getPm25()) > 110;

            showHomeCellWeatherLayout();
        }
    }

//    private void initDevice() {
//        ArrayList<UserLocationData> userLocations = AppManager.shareInstance().getUserLocationDataList();
//        if (userLocations != null && mHomeIndex > 0 && userLocations.size() > mHomeIndex - 1) {
//            userLocations.set(mHomeIndex - 1, mUserLocation);
//
//        }
//
//    }

    private void showDefaultDevice() {
        if (mAirTouchWorstDevice != null && isViewInint && mUserLocation.getDefaultDevice() != null) {
            removeWeatherTutorial();

            isNeedRefreshByUpdateHomeCellData  = true;
            // India version
            mWeatherChartView.setVisibility(View.GONE);
            if (mIndiaLayout != null)
                mIndiaLayout.setVisibility(View.INVISIBLE);

            HomeDevice homeDevice = mUserLocation.getDefaultDevice();
            mUnderLinedeviceLayout.setVisibility(View.VISIBLE);
            mAirTouchWorstDevice.setClickable(true);
            mAirTouchWorstDevice.setVisibility(View.VISIBLE);
            mAirTouchWorstDevice.updateView((AirTouchSeriesDevice) homeDevice);


            if(homeDevice != null && mDeviceIconImage != null) {
                if (AppManager.shareInstance().isAirtouchP(homeDevice.getDeviceType())){
                    mDeviceIconImage.setImageDrawable(getFragmentActivity().getResources().getDrawable(R.drawable.all_device_airtouchp));
                }
                else if(AppManager.shareInstance().isAirtouch450(homeDevice.getDeviceType())){
                    mDeviceIconImage.setImageDrawable(getFragmentActivity().getResources().getDrawable(R.drawable.all_device_450));

                }
                else if (AppManager.shareInstance().isAirtouchs(homeDevice.getDeviceType())){
                    mDeviceIconImage.setImageDrawable(getFragmentActivity().getResources().getDrawable(R.drawable.all_device_airtouchs));

                }
                displayCleanTime(homeDevice);

            }

        }
        else if(isNeedRefreshByUpdateHomeCellData){
            //避免每次location刷新的时候都去刷新天气。只有设备从有到没有才刷新一次。其他的天气刷新由刷新天气来刷新
            showHomeCellWeatherLayout();
        }
    }

    private void showHomeCellWeatherLayout(){
        //if no device or started to HouseActivity
        if (mUserLocation.getDefaultDevice() == null && isViewInint) {
            isNeedRefreshByUpdateHomeCellData = false;
            mUnderLinedeviceLayout.setVisibility(View.GONE);
            mAirTouchWorstDevice.setVisibility(View.GONE);

            // India version
            if (AppConfig.shareInstance().isIndiaAccount()) {
                mWeatherChartView.setVisibility(View.INVISIBLE);
                if (mIndiaLayout != null)
                    mIndiaLayout.setVisibility(View.VISIBLE);
                return;
            }
            showWeatherLayout();
        }
    }

    /**
     * Display clean time for back home.
     */
    private void displayCleanTime(HomeDevice homeDevice) {
        RunStatus runStatus = ((AirTouchSeriesDevice)homeDevice).getDeviceRunStatus();
        int speed = 0;
        if (runStatus == null || !runStatus.getIsAlive())
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

        int mMaxSpeed =  AppManager.shareInstance().isAirtouchP(homeDevice.getDeviceType()) ? SPEED_9 : SPEED_7;
        if (runStatus.getCleanTime().length == mMaxSpeed) {
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
        AirTouchSeriesDevice worstDevice = mUserLocation.getDefaultDevice();
        //add exception deal when runstatus is null
        if (worstDevice != null && worstDevice.getDeviceRunStatus() != null) {
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private View.OnClickListener mDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Umeng statistic
            MobclickAgent.onEvent(getFragmentActivity(), "home_house_event");

            AppConfig.shareInstance().setHomePageCover(true);
            mUnderLinedeviceLayout.setClickable(false);
            mAirTouchWorstDevice.setClickable(false);
//            Intent intent = new Intent(getFragmentActivity(), HouseActivity.class);
            Intent intent = new Intent(getFragmentActivity(), AllDeviceActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(AllDeviceActivity.ARG_HOME_INDEX, mHomeIndex);
            bundle.putInt(AirTouchConstants.LOCATION_ID, mUserLocation.getLocationID());
            intent.putExtras(bundle);

            startExactlyActivity(intent, DeviceActivity.HOME_HOUSE_REQUEST_CODE);
//            getFragmentActivity().startActivityForResult(intent, DeviceActivity
//                    .HOME_HOUSE_REQUEST_CODE);
        }
    };


    private View.OnClickListener mWorseDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Umeng statistic
            if (mUserLocation != null && mUserLocation.getDefaultDevice() != null){
                MobclickAgent.onEvent(getFragmentActivity(), "home_device_event");
                AppConfig.shareInstance().setHomePageCover(true);
                mUnderLinedeviceLayout.setClickable(false);
                mAirTouchWorstDevice.setClickable(false);
                AppManager.shareInstance().setCurrentDeviceId(mUserLocation.getDefaultDevice().getDeviceInfo().getDeviceID());
                Intent intent = new Intent(getFragmentActivity(), DeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("location", mUserLocation.getLocationID());
                bundle.putInt("deviceId", AppManager.shareInstance().getCurrentDeviceId());
                intent.putExtras(bundle);

                startExactlyActivity(intent,DeviceActivity.HOME_DEVICE_REQUEST_CODE);
            }

        }
    };

    private void startExactlyActivity(Intent intent,int resultCode){
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.stopSwitchBackground(true);
        }
        getFragmentActivity().startActivityForResult(intent, resultCode);
        getFragmentActivity().overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * get the location has the device
     *
     * @return boolean has device or not
     */
    public boolean getWorseDeviceIsShow() {
        boolean isHaveDevice = false;
        if (mAirTouchWorstDevice != null && mAirTouchWorstDevice.getVisibility() == View.VISIBLE) {
            isHaveDevice = true;
        }
        return isHaveDevice;
    }


    @Subscribe
    public void onLocationDataRefreshReceived(LongTimerRefreshTask.WeatherDataLoadedEvent
                                                      locationDataLoadedEvent) {
        if (mUserLocation == null) {
            return;
        }
        if (StringUtil.isEmpty(locationDataLoadedEvent.getCity())) {
            mWeatherChartView.setWeather(mUserLocation.getCityWeatherData());
        } else if (locationDataLoadedEvent.getCity().equals(mUserLocation.getCity())) {
            mWeatherChartView.setHourlyWeather(mUserLocation.getCityWeatherData());
        }
//        setWeatherIcon();
    }

    private void decideWhichActivityGo() {
        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();
        if (!authorizeApp.isLoginSuccess()) {
            authorizeApp.setIsUserWantToEnroll(true);
            Intent intent = new Intent();
            intent.setClass(getFragmentActivity(), UserLoginActivity.class);
            startActivity(intent);
        } else {
            EnrollAccessManager.startIntent(getFragmentActivity(), "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAirTouchWorstDevice != null) {
            mAirTouchWorstDevice.stopTyperTimer(mAirTouchWorstDevice.getmPm25TextView(), mAirTouchWorstDevice.getmTvocTextView());
        }
    }
}