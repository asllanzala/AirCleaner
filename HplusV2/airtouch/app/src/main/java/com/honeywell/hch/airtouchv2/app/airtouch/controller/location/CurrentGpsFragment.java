package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.BackHomeRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.HomeDevicePM25;
import com.honeywell.hch.airtouchv2.app.airtouch.view.OutDoorWeather;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv2.framework.model.xinzhi.WeatherData;
import com.honeywell.hch.airtouchv2.framework.view.wheelView.ArrayWheelAdapter;
import com.honeywell.hch.airtouchv2.framework.view.wheelView.NumericWheelAdapter;
import com.honeywell.hch.airtouchv2.framework.view.wheelView.WheelView;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nan.liu on 2/13/15.
 */
public class CurrentGpsFragment extends BaseLocationFragment {
    private static final String TAG = "AirTouchCurrentGPS";
    private LinearLayout errorLayout;
    private TextView errorTextView;
    private Animation translateInAnimation;
    private Animation translateOutAnimation;
    private View peopleView;
    private View smallPopView;
    private View bigPopView;
    private TextView maskTextView;
    private TextView outdoorTextView;
    private TextView elderTextView;
    private TextView smallPopTextView;
    private TextView arriveHomeTextView;
    private ImageView bigPopBackground;
    private FrameLayout bigPopMinLayout;
    private LinearLayout arriveHomeLayout;
    private LinearLayout timerLayout;
    private WheelView mHourWheel;
    private WheelView mMinuteWheel;
    private TextView tellAirTouchTextView;
    private ImageView clockImageView;
    private ImageView lineImageView;
    private Animation alphaOffAnimation;
    private String[] minuteArray = {"00", "30"};

    private int mWeatherCode = -1;
    private int mAqi;
    private int isCleanTimeEnabled = -1;
    private int mPeopleViewCount = 0;

    // getString message
    private String mSuggestMask;
    private String mSuggestNoMask;
    private String mSuggestOutdoor;
    private String mSuggestOutdoorBad;
    private String mSuggestElderly;
    private String mSuggestElderlyBad;
    private String mSuugestGpsFail;
    private String mLoginOngoing;
    private String mUserNotLogin;
    private String mLoadWeatherData;
    private String mWeatherDataFail;
    private String mSmallPopGoodMessage;
    private String mSmallPopBadMessage;

    private View gpsFragmentView = null;

    //flag to big bubble which showed,if show should update when onResume
    //avoid to not update arrive home setting status when current home device is deleted
    //
    private boolean isBigBubbleShow = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param activity
     * @return A new instance of fragment HomeCellFragment.
     */
    public static CurrentGpsFragment newInstance(FragmentActivity activity) {
        CurrentGpsFragment fragment = new CurrentGpsFragment();
        fragment.setActivity(activity);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (gpsFragmentView == null){
            gpsFragmentView = inflater.inflate(R.layout.fragment_currentgps, container, false);
            initView(gpsFragmentView);

            updateGpsFragment();

        }
        if (gpsFragmentView.getParent() != null){
            ViewGroup p = (ViewGroup)gpsFragmentView.getParent();
            p.removeAllViews();

        }
        return gpsFragmentView;
    }

    public void updateGpsFragment(){
        if (gpsFragmentView != null)
        {
            showPeopleView();
            updateSmallPopUpData();
            showBigPopUp();
            updateCleanTime();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        errorLayout = (LinearLayout) view.findViewById(R.id.control_error_layout);
        errorTextView = (TextView) view.findViewById(R.id.control_error_tv);
        maskTextView = (TextView) view.findViewById(R.id.suggestion_mask);
        outdoorTextView = (TextView) view.findViewById(R.id.suggestion_outside_activity);
        elderTextView = (TextView) view.findViewById(R.id.suggestion_child_elderly);
        mOutDoorWeatherView = (OutDoorWeather) view.findViewById(R.id.outdoor_weather_view);
        mOutDoorWeatherView.setVisibility(View.INVISIBLE);
        peopleView = view.findViewById(R.id.people_view);
        smallPopView = view.findViewById(R.id.small_pop_view);
        bigPopView = view.findViewById(R.id.big_pop_view);
        arriveHomeLayout = (LinearLayout) view.findViewById(R.id.arrive_home_layout);
        timerLayout = (LinearLayout) view.findViewById(R.id.timer_layout);
        smallPopTextView = (TextView) view.findViewById(R.id.small_pop_tv);
        arriveHomeTextView = (TextView) view.findViewById(R.id.clock_tv);
        bigPopBackground = (ImageView) view.findViewById(R.id.big_pop_background);
        bigPopMinLayout = (FrameLayout) view.findViewById(R.id.big_pop_cancel_layout);
        mHourWheel = (WheelView) view.findViewById(R.id.hour_wheel);
        mMinuteWheel = (WheelView) view.findViewById(R.id.minute_wheel);
        tellAirTouchTextView = (TextView) view.findViewById(R.id.tell_air_touch_tv);
        tellAirTouchTextView.setOnClickListener(tellAirTouchOnClick);
        clockImageView = (ImageView) view.findViewById(R.id.clock_iv);
        lineImageView = (ImageView) view.findViewById(R.id.halving_line);

        bigPopMinLayout.setOnClickListener(bigPopOnClick);
        smallPopView.setOnClickListener(smallPopOnClick);
        translateInAnimation = AnimationUtils.loadAnimation(getFragmentActivity(),
                R.anim.control_translate_in);
        translateOutAnimation = AnimationUtils.loadAnimation(getFragmentActivity(),
                R.anim.control_translate_out);
        translateInAnimation.setAnimationListener(new translateInAnimationListener());
        translateOutAnimation.setAnimationListener(new translateOutAnimationListener());

//        ViewHelper.setTranslationX(peopleView, DensityUtil.getScreenWidth() * 0.55f);
//        ViewHelper.setTranslationY(peopleView, DensityUtil.getScreenHeight() * 5.5f / 100);
//        ViewHelper.setTranslationX(smallPopView, DensityUtil.getScreenWidth() * 0.35f);
//        ViewHelper.setTranslationY(smallPopView, DensityUtil.getScreenHeight() * -18f / 100);
//        ViewHelper.setTranslationY(bigPopView, DensityUtil.getScreenHeight() * -18f / 100);
        ViewHelper.setAlpha(smallPopView, 0.9f);
        ViewHelper.setAlpha(bigPopBackground, 0.3f);
        smallPopView.setVisibility(View.VISIBLE);
        bigPopView.setVisibility(View.INVISIBLE);

        mSuggestNoMask = getString(R.string.suggest_no_mask);
        mSuggestMask = getString(R.string.suggest_mask);
        mSuggestOutdoor = getString(R.string.suggest_outdoor);
        mSuggestOutdoorBad = getString(R.string.suggest_outdoor_bad);
        mSuggestElderly = getString(R.string.suggest_elderly);
        mSuggestElderlyBad = getString(R.string.suggest_elderly_bad);
        mSuugestGpsFail = getString(R.string.enroll_gps_fail);
        mLoginOngoing = getString(R.string.login_ongoing);
        mUserNotLogin = getString(R.string.user_not_login);
        mLoadWeatherData = getString(R.string.load_weather_data);
        mWeatherDataFail = getString(R.string.weather_data_fail);

        MainActivity.setChangeDefaultHomeListener(new MainActivity.ChangeDefaultHomeListener()
        {
            @Override
            public void onChangeHomeListener()
            {
                bigPopView.setVisibility(View.INVISIBLE);
                smallPopView.setVisibility(View.VISIBLE);
            }
        });

        AppConfig appConfig = AppConfig.shareInstance();
        mCitySiteView.updateView(appConfig.getGpsCityCode());

        setHomeNameText(getCityDBService().getCityByCode(appConfig.getGpsCityCode()),
                getResources().getString(R.string.current_location));
        mCity = getCityDBService().getCityByCode(appConfig.getGpsCityCode());

        if (appConfig.isDaylight())
            lineImageView.setImageResource(R.drawable.back_home_line);
        else
            lineImageView.setImageResource(R.drawable.back_home_line_night);

        initTimeWheel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
    }


    @Override
    public void onStart() {
        super.onStart();
//        AppConfig appConfig = AppConfig.shareInstance();
//        mCitySiteView.updateView(appConfig.getGpsCityCode());
//        setHomeNameText(getCityDBService().getCityByCode(appConfig.getGpsCityCode()),
//                getResources().getString(R.string.current_location));
//        mCity = getCityDBService().getCityByCode(appConfig.getGpsCityCode());
//
//        if (appConfig.isDaylight())
//            lineImageView.setImageResource(R.drawable.back_home_line);
//        else
//            lineImageView.setImageResource(R.drawable.back_home_line_night);
//
//        initTimeWheel();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isBigBubbleShow)
        {
            showBigPopUp();
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

//        updateGpsFragment();
    }

    @Override
    public void onPause() {
        super.onPause();

        isCleanTimeEnabled = -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPeopleViewCount = 0;
    }

    private void initTimeWheel() {
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
        mMinuteWheel.setAdapter(new ArrayWheelAdapter<>(minuteArray, 2));
        mMinuteWheel.setCurrentItem(0);
        mMinuteWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCleanTimeEnabled != 1) {
                    mMinuteWheel.showItem();
                }
            }
        });
    }

    public void updateSmallPopUpData() {
        if (!AuthorizeApp.shareInstance().isLoginSuccess())
            return;

        // GPS fail
        if (TextUtils.isEmpty(AppConfig.shareInstance().getGpsCityCode())) {
            peopleView.setBackgroundResource(R.drawable.people_not_login);
            smallPopTextView.setText(mSuugestGpsFail);
            return;
        }

        // Weather data ongoing
        if (mWeatherCode == -1) {
            peopleView.setBackgroundResource(R.drawable.people_not_login);
            smallPopTextView.setText(mLoadWeatherData);
            return;
        }

        // Weather data fail
        if (mWeatherCode == 99) {
            peopleView.setBackgroundResource(R.drawable.people_not_login);
            smallPopTextView.setText(mWeatherDataFail);
            return;
        }

        // show 3 weather suggestion one by one
        mPeopleViewCount++;
        if (mPeopleViewCount % 3 == 1) {
            mSmallPopGoodMessage = mSuggestElderly;
            mSmallPopBadMessage = mSuggestElderlyBad;
            if (mWeatherCode >= 10)
                mSmallPopGoodMessage = mSuggestElderlyBad;
        } else if (mPeopleViewCount % 3 == 2) {
            mSmallPopGoodMessage = mSuggestOutdoor;
            mSmallPopBadMessage = mSuggestOutdoorBad;
            if (mWeatherCode >= 10)
                mSmallPopGoodMessage = mSuggestOutdoorBad;
        } else if (mPeopleViewCount % 3 == 0) {
            mPeopleViewCount = 0;
            mSmallPopGoodMessage = mSuggestNoMask;
            mSmallPopBadMessage = mSuggestMask;
        }

        if (mAqi < 100) {
            smallPopTextView.setText(mSmallPopGoodMessage);
        } else {
            smallPopTextView.setText(mSmallPopBadMessage);
        }

    }

    public void showBigPopUp() {
        isBigBubbleShow = true;
        if (!AuthorizeApp.shareInstance().isLoginSuccess()
                || isDeviceNotAvailable()) {
            timerLayout.setAlpha(0.6f);
//            mHourWheel.setClickable(false);
//            mMinuteWheel.setClickable(false);
            tellAirTouchTextView.setVisibility(View.INVISIBLE);
            arriveHomeLayout.setVisibility(View.INVISIBLE);

        } else {
            timerLayout.setAlpha(1.0f);
            tellAirTouchTextView.setVisibility(View.VISIBLE);
            arriveHomeLayout.setVisibility(View.VISIBLE);

            updateCleanTime();
//            if (isCleanTimeEnabled == 1) {
//                clockImageView.setImageResource(R.drawable.clock_blue);
//                arriveHomeTextView.setText(getString(R.string.arriving_home));
//                tellAirTouchTextView.setText(getString(R.string.cancel));
//            } else if (isCleanTimeEnabled == 0) {
//                clockImageView.setImageResource(R.drawable.clock_white);
//                arriveHomeTextView.setText(String.format
//                        (getString(R.string.set_arrive_home),
//                                AuthorizeApp.shareInstance().getCurrentHome().getName()));
//                tellAirTouchTextView.setText(getString(R.string.tell_air_touch));
//            }
        }
    }

    private boolean isDeviceNotAvailable() {
        if (AuthorizeApp.shareInstance().getUserLocations().size() == 0)
            return true;
        if (AuthorizeApp.shareInstance().getCurrentHome() == null
                || AuthorizeApp.shareInstance().getCurrentHome().getHomeDevices() == null)
            return true;
        ArrayList<HomeDevice> homeDevices = AuthorizeApp.shareInstance().getCurrentHome().getHomeDevices();
        if (homeDevices.size() == 0)
            return true;
        for (int i = 0; i < homeDevices.size(); i++) {
            if (homeDevices.get(i).getDeviceInfo() != null && homeDevices.get(i).getDeviceInfo().getIsAlive()) {
                return false;
            }
        }
        return true;
    }

    public void showPeopleView() {
        // Login in progress
        if (!AuthorizeApp.shareInstance().isLoginSuccess()) {
            peopleView.setBackgroundResource(R.drawable.people_not_login);
            if (AuthorizeApp.shareInstance().isAutoLoginOngoing()) {
                smallPopTextView.setText(mLoginOngoing);
            } else {
                smallPopTextView.setText(mUserNotLogin);
            }
            return;
        }

        if (isTravel()) {
            if (suggestOutdoor() == 1) {
                peopleView.setBackgroundResource(R.drawable.people_travel_out);
            } else if (suggestOutdoor() == 0) {
                peopleView.setBackgroundResource(R.drawable.people_travel_rain);
            } else {
                peopleView.setBackgroundResource(R.drawable.people_not_login);
            }
            if (isAqiBad()) {
                peopleView.setBackgroundResource(R.drawable.people_travel_in);
            }
        } else {
            if (suggestOutdoor() == 1) {
                peopleView.setBackgroundResource(R.drawable.people_local_out);
            } else if (suggestOutdoor() == 0) {
                peopleView.setBackgroundResource(R.drawable.people_local_rain);
            } else {
                peopleView.setBackgroundResource(R.drawable.people_not_login);
            }
            if (isAqiBad()) {
                peopleView.setBackgroundResource(R.drawable.people_local_in);
            }
        }
    }

    public void updateCleanTime() {
        if (AuthorizeApp.shareInstance().getCurrentHome() == null) {
            isCleanTimeEnabled = -1;
            return;
        }

        arriveHomeTextView.setText(getString(R.string.enroll_loading));
        tellAirTouchTextView.setVisibility(View.INVISIBLE);
        clockImageView.setVisibility(View.INVISIBLE);
//        mHourWheel.hideItem();
//        mMinuteWheel.hideItem();

//        isCleanTimeEnabled = 0;
        bigPopView.setClickable(false);

        String sessionId = AuthorizeApp.shareInstance().getSessionId();
        int locationId = AuthorizeApp.shareInstance().getCurrentHome().getLocationID();

        int getHomePm25RequestId = getRequestClient().getHomePm25(locationId, sessionId,
                mReceiveResponse);
        addRequestId(getHomePm25RequestId);
    }

    IReceiveResponse mReceiveResponse = new IReceiveResponse() {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            removeRequestId(httpRequestResponse.getRandomRequestID());
            switch (httpRequestResponse.getRequestID()) {
                case GET_HOME_PM25:
                    /**
                     * fix AIRQA-580
                     [Testin]Java Runtime error: Process: com.honeywell.hch.airtouch, PID: 28084
                     Fragment CurrentGpsFragment{4ef3f8}
                     not attached to Activity
                     */
                    if(!isAdded())
                    {
                        return;
                    }

                    String timeToHome = "";
                    if (!isDeviceNotAvailable())
                        tellAirTouchTextView.setVisibility(View.VISIBLE);
                    clockImageView.setVisibility(View.VISIBLE);
                    bigPopView.setClickable(true);
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            try {
                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseJSON = responseArray.getJSONObject(i);
                                    HomeDevicePM25 device = new Gson().fromJson(responseJSON.toString(),
                                            HomeDevicePM25.class);
                                    if (device.getCleanBeforeHomeEnable()) {
                                        timeToHome = device.getTimeToHome();
                                        isCleanTimeEnabled = 1;
                                        clockImageView.setImageResource(R.drawable.clock_blue);
                                        arriveHomeTextView.setText(getString(R.string.arriving_home));
                                        tellAirTouchTextView.setText(getString(R.string.cancel));
                                    } else {
                                        isCleanTimeEnabled = 0;
                                        clockImageView.setImageResource(R.drawable.clock_white);
                                        arriveHomeTextView.setText(String.format
                                                (getString(R.string.set_arrive_home),
                                                        AuthorizeApp.shareInstance().getCurrentHome().getName()));
                                        tellAirTouchTextView.setText(getString(R.string.tell_air_touch));
                                    }
                                }
                                saveTimeToHome(timeToHome);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
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

    @Override
    protected void handleWeatherData(WeatherData weatherData) {
        super.handleWeatherData(weatherData);

        if (weatherData == null || weatherData.getWeather() == null || weatherData.getWeather()
                .size() == 0 || weatherData.getWeather().get(0) == null) {
            mWeatherCode = 99;
        } else {
            mWeatherCode = weatherData.getWeather().get(0).getNow().getCode();
            mAqi = Integer.parseInt(weatherData.getWeather().get(0).getNow().getAirQuality().getAirQualityIndex().getAqi());
            showSuggestion();
        }

        showPeopleView();
        updateSmallPopUpData();
    }

    private void showSuggestion() {
        if (this.isDetached())
            return;

        if (TextUtils.isEmpty(AppConfig.shareInstance().getGpsCityCode())) {
            maskTextView.setText("");
            outdoorTextView.setText("");
            elderTextView.setText("");
            return;
        }

        if (mAqi < 100) {
            maskTextView.setText(mSuggestNoMask);
            outdoorTextView.setText(mSuggestOutdoor);
            elderTextView.setText(mSuggestElderly);
        } else {
            maskTextView.setText(mSuggestMask);
            outdoorTextView.setText(mSuggestOutdoorBad);
            elderTextView.setText(mSuggestElderlyBad);
        }
        if (mWeatherCode >= 10) {
            outdoorTextView.setText(mSuggestOutdoorBad);
            elderTextView.setText(mSuggestElderlyBad);
        }

    }

    /**
     * @return 1 - good weather, good for outdoor
     * 0 - bad weather, bad for outdoor
     * -1 - very bad weather, bad for outdoor
     */
    private int suggestOutdoor() {
        if (mWeatherCode < 10) {
            return 1;
        } else if (mWeatherCode < 26) {
            return 0;
        } else {
            return -1;
        }
    }

    private boolean isAqiBad() {
        if (mAqi < 100) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isTravel() {
        if (AuthorizeApp.shareInstance().getCurrentHome() == null)
            return false;

        if (mCity == null || mCity.getCode() == null)
            return false;

        if (AuthorizeApp.shareInstance().getCurrentHome() == null)
            return false;

        if (mCity.getCode().equals(AuthorizeApp.shareInstance().getCurrentHome().getCity())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Back home
     */
    View.OnClickListener tellAirTouchOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mHourWheel.hideItem();
            mMinuteWheel.hideItem();

            if (isCleanTimeEnabled == -1)
                updateCleanTime();

            Calendar calendar = Calendar.getInstance();
            int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
            int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
            calendar.set(Calendar.HOUR_OF_DAY, mHourWheel.getCurrentItem());
            calendar.set(Calendar.MINUTE, Integer.parseInt(minuteArray[mMinuteWheel.getCurrentItem()]));
            calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");
            String settingTime = sdf.format(calendar.getTime());

            tellAirTouchTextView.setClickable(false);
            int locationId = AuthorizeApp.shareInstance().getCurrentHome().getLocationID();
            String sessionId = AuthorizeApp.shareInstance().getSessionId();
            BackHomeRequest backHomeRequest = new BackHomeRequest();
            backHomeRequest.setTimeToHome(settingTime);
            backHomeRequest.setDeviceString("");

            if (isCleanTimeEnabled == 1) {
                backHomeRequest.setIsEnableCleanBeforeHome(false);
                isCleanTimeEnabled = 0;
            } else if (isCleanTimeEnabled == 0) {
                backHomeRequest.setIsEnableCleanBeforeHome(true);
                isCleanTimeEnabled = 1;
            }
            int cleanTimeRequestId = getRequestClient().cleanTime(locationId, sessionId,
                    backHomeRequest, backHomeResponse);
            addRequestId(cleanTimeRequestId);
            alphaOffAnimation = AnimationUtils.loadAnimation(getFragmentActivity(),
                    R.anim.control_alpha);
            tellAirTouchTextView.startAnimation(alphaOffAnimation);
        }
    };

    final IReceiveResponse backHomeResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            tellAirTouchTextView.clearAnimation();
            tellAirTouchTextView.setClickable(true);
            removeRequestId(httpRequestResponse.getRandomRequestID());
            switch (httpRequestResponse.getRequestID()) {
                case CLEAN_TIME:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        LogUtil.log(LogUtil.LogLevel.INFO, TAG, "back home success!");
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayout.startAnimation(translateInAnimation);

                        if (isCleanTimeEnabled == 1) {
                            clockImageView.setImageResource(R.drawable.clock_blue);
                            arriveHomeTextView.setText(getString(R.string.arriving_home));
                            tellAirTouchTextView.setText(getString(R.string.cancel));
                            errorTextView.setText(getString(R.string.tell_success));
                        } else if (isCleanTimeEnabled == 0) {
                            clockImageView.setImageResource(R.drawable.clock_white);
                            arriveHomeTextView.setText(String.format
                                    (getString(R.string.set_arrive_home),
                                            AuthorizeApp.shareInstance().getCurrentHome().getName()));
                            tellAirTouchTextView.setText(getString(R.string.tell_air_touch));
                            errorTextView.setText(getString(R.string.cancel_success));
                        }
                    } else {
                        if (isCleanTimeEnabled == 1) {
                            isCleanTimeEnabled = 0;
                            errorLayout.setVisibility(View.VISIBLE);
                            errorLayout.startAnimation(translateInAnimation);
                            errorTextView.setText(getString(R.string.tell_fail));
                        } else if (isCleanTimeEnabled == 0) {
                            isCleanTimeEnabled = 1;
                            errorLayout.setVisibility(View.VISIBLE);
                            errorLayout.startAnimation(translateInAnimation);
                            errorTextView.setText(getString(R.string.cancel_fail));
                        }

                        if (httpRequestResponse.getStatusCode() == StatusCode.BAD_REQUEST) {
//                            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, httpRequestResponse.getData());
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Animation helper
     */
    private class translateInAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            errorLayout.startAnimation(translateOutAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class translateOutAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            errorLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub
        }
    }

    View.OnClickListener smallPopOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//            if (isDeviceNotAvailable())
//                return;

            smallPopView.setVisibility(View.INVISIBLE);
            bigPopView.setVisibility(View.VISIBLE);
            showBigPopUp();
        }
    };

    View.OnClickListener bigPopOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            bigPopView.setVisibility(View.INVISIBLE);
            isBigBubbleShow = false;
            smallPopView.setVisibility(View.VISIBLE);
            updateSmallPopUpData();
        }
    };



}
