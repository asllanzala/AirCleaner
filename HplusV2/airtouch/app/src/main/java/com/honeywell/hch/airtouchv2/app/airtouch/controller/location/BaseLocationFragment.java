package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseRequestFragment;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.ThinkPageClient;
import com.honeywell.hch.airtouchv2.framework.database.CityDBService;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv2.framework.model.xinzhi.WeatherData;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.honeywell.hch.airtouchv2.framework.view.CitySiteView;
import com.honeywell.hch.airtouchv2.framework.view.HomeSkyView;
import com.honeywell.hch.airtouchv2.app.airtouch.view.OutDoorWeather;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by nan.liu on 3/19/15.
 */
public class BaseLocationFragment extends BaseRequestFragment {

    protected ImageView mHomeBackgroundImageView = null;
    protected ImageView mHouseImageView = null;

    protected OutDoorWeather mOutDoorWeatherView = null;
    protected ImageView mFarawayMountainImageView = null;

    protected ImageView mStarImageView = null;
    protected ImageView mMoonImageView = null;
    protected CitySiteView mCitySiteView = null;

    private CityDBService mCityDBService;
    private float[] cityBottomDistance = {-4.1f, -4.2f, -4.5f, 0f, -1.5f, -2f};
    protected City mCity = null;
    protected WeatherData weatherData = null;


    private FragmentActivity mActivity;
    private OnWeatherClickListener mOnWeatherClickListener;

    protected int mHomeIndex = 0;
    private boolean isViewReady = false;

    protected int pm25Value = 0;


    private int[] farawayMountainDayIDs = {R.drawable.faraway_mountain_day0,
            R.drawable.faraway_mountain_day1, R.drawable.faraway_mountain_day2,
            R.drawable.faraway_mountain_day3, R.drawable.faraway_mountain_day4,
            R.drawable.faraway_mountain_day5};
    private int[] farawayMountainNightIDs = {R.drawable.faraway_mountain_night0,
            R.drawable.faraway_mountain_night1, R.drawable.faraway_mountain_night2,
            R.drawable.faraway_mountain_night3, R.drawable.faraway_mountain_night4,
            R.drawable.faraway_mountain_night5};

    protected HomeSkyView skyView;

    protected HomeHalfPageFragment halfPageFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param activity
     * @return A new instance of fragment HomeCellFragment.
     */
    public static BaseLocationFragment newInstance(FragmentActivity activity) {
        BaseLocationFragment fragment = new BaseLocationFragment();
        fragment.setActivity(activity);
        return fragment;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
        mCityDBService = new CityDBService(mActivity);
    }

    public FragmentActivity getFragmentActivity() {
        if (mActivity == null)
            mActivity = getActivity();
        return mActivity;
    }

    public void setDaylight() {
        if (isViewReady) {
            switchTimeView();
        }
    }

    public CityDBService getCityDBService() {
        if (mCityDBService == null) {
            mCityDBService = new CityDBService(getFragmentActivity());
        }
        return mCityDBService;
    }


    protected void initView(View view) {
        mHomeBackgroundImageView = (ImageView) view.findViewById(R.id.home_background);
        mMoonImageView = (ImageView) view.findViewById(R.id.moon_image);
        mStarImageView = (ImageView) view.findViewById(R.id.star_night);
        mFarawayMountainImageView = (ImageView) view.findViewById(R.id.faraway_mountain);
        mCitySiteView = (CitySiteView) view.findViewById(R.id.city_site_view);

        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        mFarawayMountainImageView.setImageResource(isDaylight ? farawayMountainDayIDs[mHomeIndex
                % 6] : farawayMountainNightIDs[mHomeIndex % 6]);
        mHomeBackgroundImageView.setImageResource(isDaylight ? R.drawable.day_bg : R.drawable
                .night_bg);
        mMoonImageView.setImageResource(isDaylight ? R.drawable.moon_day : R.drawable.moon_night);

        mStarImageView.setVisibility(isDaylight ? View.INVISIBLE : View.VISIBLE);
        ViewHelper.setTranslationY(mCitySiteView, DensityUtil.getScreenHeight() *
                cityBottomDistance[mHomeIndex % 6] / 100);

        skyView = (HomeSkyView)view.findViewById(R.id.sky_cell2);
        skyView.setParentFragment(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewReady = true;
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        if (!isDaylight){
            mStarImageView.startAnimation(AnimationUtils.loadAnimation(getFragmentActivity(),
                    R.anim.window_alpha));
        }
        else {
            mStarImageView.clearAnimation();
        }
    }

    protected void setHomeNameText(City city, String homeName) {
        this.mCity = city;
        updateWeatherData();
    }

    public void updateWeatherData() {
        if (mCity == null || mOutDoorWeatherView.isShown())
            return;
        RequestID requestID = RequestID.ALL_DATA;
        ThinkPageClient.sharedInstance().getWeatherData(mCity.getNameEn(), AppConfig
                .getLanguageXinzhi(), 'c', requestID, thickPageResponse);
    }

    protected void handleWeatherData(WeatherData weatherData) {
        if (weatherData == null || weatherData.getWeather() == null || weatherData.getWeather()
                .size() == 0 || weatherData.getWeather().get(0) == null)
            return;
        pm25Value = Integer.valueOf(weatherData.getWeather().get(0).getNow().getAirQuality()
                .getAirQualityIndex().getPm25());

        ((MainActivity)getFragmentActivity()).setCurrentHazeMoving(mHomeIndex, pm25Value);

    }

    private IReceiveResponse thickPageResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                    weatherData = new Gson().fromJson(httpRequestResponse.getData(),
                            WeatherData.class);
                    showAnimation(mOutDoorWeatherView, 1000);
                    mOutDoorWeatherView.setClickable(true);
                    mOutDoorWeatherView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            //1.hide outDoor weather
                            //2.show the sky bubble
                            outDoorWeatherClick();

                        }
                    });
                    mOutDoorWeatherView.updateView(weatherData);
                    ((HomeHalfPageFragment) getParentFragment()).updateWeatherData(weatherData);
                    skyView.updateViewData(weatherData);
                    handleWeatherData(weatherData);
                }
            } else {
                mOutDoorWeatherView.setClickable(false);
                if (httpRequestResponse.getException() != null) {
                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception："
                            + httpRequestResponse.getException().toString());
                }
            }
        }
    };




    protected void switchTimeView() {
        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        mFarawayMountainImageView.setImageResource(isDaylight ? farawayMountainDayIDs[mHomeIndex
                % 6] : farawayMountainNightIDs[mHomeIndex % 6]);
        mHomeBackgroundImageView.setImageResource(isDaylight ? R.drawable.day_bg : R.drawable
                .night_bg);
        if (mHouseImageView != null) {
            mHouseImageView.setImageResource(isDaylight ? R.drawable.big_house :
                    R.drawable.big_house_night);
        }
        mMoonImageView.setImageResource(isDaylight ? R.drawable.moon_day : R.drawable.moon_night);

        mStarImageView.setVisibility(isDaylight ? View.INVISIBLE : View.VISIBLE);
        if (!isDaylight){
            mStarImageView.startAnimation(AnimationUtils.loadAnimation(getFragmentActivity(),
                    R.anim.window_alpha));
        }
        else {
            mStarImageView.clearAnimation();
        }
        mCitySiteView.switchTimeView();
    }

    protected void hideAnimation(View view, long duration) {
        if (view == null || !view.isShown())
            return;
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(duration);
        animation.setInterpolator(new AccelerateInterpolator());
        view.setAnimation(animation);
        animation.start();
        view.setVisibility(View.INVISIBLE);
    }

    protected void showAnimation(View view, long duration) {
        if (view == null || view.isShown())
            return;
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(duration);
        animation.setInterpolator(new AccelerateInterpolator());
        view.setAnimation(animation);
        animation.start();
        view.setVisibility(View.VISIBLE);
    }

    protected void startAnimation(View view, Animation animation) {
        if (view == null || animation == null)
            return;
        view.startAnimation(animation);
    }

    protected void setVisibility(View view, int visibility) {
        if (view == null)
            return;
        view.setVisibility(visibility);
    }

    protected void setAlpha(View view, float alpha) {
        if (view == null)
            return;
        ViewHelper.setAlpha(view, alpha);
    }


    public int getPm25Value()
    {
        return pm25Value;
    }

    private void outDoorWeatherClick()
    {

        //show the sky view and show bubble
        skyView.setVisibility(View.VISIBLE);
        skyView.showBubble();

        //hide the outDoor Weather
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(300);
        animation.setInterpolator(new AccelerateInterpolator());
        mOutDoorWeatherView.setAnimation(animation);
        animation.start();
        mOutDoorWeatherView.setVisibility(View.INVISIBLE);

    }

    /**
     * when click the sky bubble ,
     * 1.hide the bubble
     * 2. show outdoorWeather
     */
    public void showOutDoorWeatherAnimation()
    {
        skyView.hideBubble();

        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateInterpolator());
        mOutDoorWeatherView.setAnimation(animation);
        animation.start();
        mOutDoorWeatherView.setVisibility(View.VISIBLE);
    }


    public void setHomeHalfFragment(HomeHalfPageFragment homeHalfFragment)
    {
        this.halfPageFragment = homeHalfFragment;
    }

    public HomeSkyView getSkyView()
    {
        return skyView;
    }

    /**
     * set the listener of outdoor view clicked
     *
     * @param listener
     */
    public void setOnWeatherClickListener(OnWeatherClickListener listener) {
        mOnWeatherClickListener = listener;
    }

    /**
     * the listener of outdoor view clicked
     */
    public interface OnWeatherClickListener {
        public void OnWeatherClick(int index);
    }

    /**
     * get current fragment weather data,like so,co,etc.
     * @return
     */
    public WeatherData getFragmentWeatherData(){
        return weatherData;
    }


}
