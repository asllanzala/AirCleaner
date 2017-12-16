package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.device.ArriveHomeActivity;
import com.honeywell.hch.airtouchv3.wxapi.WXEntryActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.weather.WeatherActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart.WeatherChartView;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.MainActivity;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseRequestFragment;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.database.CityChinaDBService;
import com.honeywell.hch.airtouchv3.framework.database.CityIndiaDBService;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Now;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Weather;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.BlurImageUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by nan.liu on 3/19/15.
 */
public class BaseLocationFragment extends BaseRequestFragment{

    public final static int CURRENT_GPS_LOCATION_ID = 0;

    protected TextView mHomeNameTextView;
    protected TextView mCityNameTextView;

    private CityChinaDBService mCityChinaDBService;
    private CityIndiaDBService mCityIndiaDBService;
    protected City mCity = null;
    protected Weather mWeatherData = null;

    protected FragmentActivity mActivity;
    protected int mHomeIndex = 0;
    protected int mPm25Value = 0;

    protected int locationIndex;

    protected UserLocationData mUserLocation = null;
    protected BlurBackgroundView mBlurBackgroundView;

    protected ImageView mWeatherIconImageView;
    protected ImageView mEmotionIconImageView;
    protected ImageView mScheduleIconImageView;

    protected  int mWeatherCode = 6;


    protected WeatherChartView mWeatherChartView;

    protected boolean isViewInint = false;

    protected RelativeLayout mTopFragmentView;

    private Bitmap mTutorialBitmap;
    private ImageView mTutorialImageView;


    /**
     * only when the main activity is scrolling, should it set to false;
     */
    private boolean isNeedWeatherChartOnTouch = true;


    private int[] mWeatherIconID = {R.drawable.sunny, R.drawable.sunny, R.drawable.sunny,
            R.drawable.sunny, R.drawable.heavycloudy, R.drawable.lightcloudy,
            R.drawable.lightcloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy,
            R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.rainy,
            R.drawable.rainy, R.drawable.rainy, R.drawable.rainy, R.drawable.rainy,
            R.drawable.rainy, R.drawable.rainy, R.drawable.rainandsnow, R.drawable.snow,
            R.drawable.snow, R.drawable.snow, R.drawable.snow, R.drawable.snow,
            R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy,
            R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy,
            R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy,
            R.drawable.cloudy, R.drawable.cloudy};

    private Dialog mDialog;

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
        mCityChinaDBService = new CityChinaDBService(mActivity);
        mCityIndiaDBService = new CityIndiaDBService(mActivity);
    }

    public FragmentActivity getFragmentActivity() {
        if (mActivity == null)
            mActivity = getActivity();
        return mActivity;
    }

    public void setDaylight() {
        switchTimeView();
    }

    public CityChinaDBService getCityChinaDBService() {
        if (mCityChinaDBService == null) {
            mCityChinaDBService = new CityChinaDBService(getFragmentActivity());
        }
        return mCityChinaDBService;
    }

    public CityIndiaDBService getCityIndiaDBService() {
        if (mCityIndiaDBService == null) {
            mCityIndiaDBService = new CityIndiaDBService(getFragmentActivity());
        }
        return mCityIndiaDBService;
    }

    public void initCity(City city) {
        this.mCity = city;
    }

    /**
     * base class init view both in the homecell and CurrentLocation
     *
     * @param view
     */
    protected void initView(View view) {
        mHomeNameTextView = (TextView) view.findViewById(R.id.home_name);
        mCityNameTextView = (TextView) view.findViewById(R.id.home_location);


        mBlurBackgroundView = (BlurBackgroundView) view.findViewById(R.id.home_background);
        mBlurBackgroundView.setFragment(this);
        mBlurBackgroundView.initDynmac(mUserLocation);
        mWeatherIconImageView = (ImageView)view.findViewById(R.id.weather_icon);
        mWeatherIconImageView.setOnClickListener(mHomeIconClickListener);
        setWeatherIcon();

        mBlurBackgroundView.initBackgroundResouce(R.raw.default_city_day_blur1, BlurImageUtil.MAIN_ACTVITIY_BLUR_RADIO);

        mEmotionIconImageView = (ImageView)view.findViewById(R.id.emotion_icon);
        mScheduleIconImageView = (ImageView)view.findViewById(R.id.schedule_icon);

        mScheduleIconImageView.setOnClickListener(mHomeIconClickListener);
        mEmotionIconImageView.setOnClickListener(mHomeIconClickListener);

        mWeatherChartView = (WeatherChartView) view.findViewById(R.id.weather_chart);
        mWeatherChartView.setBaseLocationFragment(this);

        mTopFragmentView = (RelativeLayout)view.findViewById(R.id.top_fragment_view);
        mTopFragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (mTutorialImageView != null && mTutorialImageView.getVisibility() == View.VISIBLE){
                    ((MainActivity)mActivity).removeWeatherTutorial();
                }
                if (mWeatherChartView != null &&
                        mWeatherChartView.getVisibility() == View.VISIBLE &&
                        isTouchInView(motionEvent, mWeatherChartView) && isNeedWeatherChartOnTouch) {
                    setMainViewPagerScroll(false);
                } else {
                    setMainViewPagerScroll(true);
                }
                return true;
            }
        });

    }

    private  boolean isTouchInView(MotionEvent ev, View view) {
         int[] touchLocation = new int[2];
        view.getLocationOnScreen(touchLocation);
        float motionX = ev.getRawX();
        float motionY = ev.getRawY();

        int bottom = view.getBottom();
        return motionX >= touchLocation[0]
                && motionX <= (touchLocation[0] + view.getWidth())
                && motionY >= bottom - DensityUtil.dip2px(160)
                && motionY <= bottom;
    }

    protected void showWeatherLayout() {

        if (isViewInint  && mUserLocation != null) {
            mWeatherChartView.setVisibility(View.VISIBLE);
            mWeatherChartView.setBaseLocationFragment(this);
            WeatherPageData weatherPageData = mUserLocation.getCityWeatherData();
            if (weatherPageData == null || weatherPageData.getWeather() == null)
                return;
            mWeatherChartView.setWeather(weatherPageData);
            if (weatherPageData.getHourlyData() != null && weatherPageData.getHourlyData().length > 0
                    && weatherPageData.getHourlyData()[0] != null) {
                mWeatherChartView.setHourlyWeather(weatherPageData);
            }
        }
    }


    /**
     * set weather icon
     */
    public void setWeatherIcon() {
        if (mUserLocation != null && mWeatherIconImageView != null && mUserLocation.getCityWeatherData() != null){
            Now thisNowWeather = mUserLocation.getCityWeatherData().getWeather().getNow();
            if (thisNowWeather != null){
                mWeatherCode = thisNowWeather.getCode();
                if (mWeatherCode == 99){
                    mWeatherCode = mWeatherIconID.length - 1;
                }
                mWeatherIconImageView.setImageResource(mWeatherIconID[mWeatherCode]);
                mUserLocation.getCityBackgroundDta().initmCityBackgroundObjectListList(mWeatherCode, false);

                ((MainActivity) getFragmentActivity()).startOtherWeatherEffect();
            }
        }
    }

    public void setWeatherIconFromWeatherChart(int weatherCode){
        mWeatherCode = weatherCode;
        if (mWeatherIconImageView != null && mUserLocation != null){
            mWeatherIconImageView.setImageResource(mWeatherIconID[mWeatherCode]);
            mUserLocation.getCityBackgroundDta().initmCityBackgroundObjectListList(mWeatherCode, false);
        }
        ((MainActivity) getFragmentActivity()).startOtherWeatherEffect();
    }


    /**
     * set home name and city name
     *
     * @param city
     * @param homeName
     */
    protected void setHomeNameText(City city, String homeName) {
        this.mCity = city;
        if (mHomeNameTextView != null){
            mCityNameTextView.clearAnimation();
            mHomeNameTextView.setText(homeName);
            if (city.getNameZh() != null && city.getNameZh() != null) {
                String cityText = "(" + (AppConfig.shareInstance().getLanguage().equals(AppConfig
                        .LANGUAGE_ZH) ? city.getNameZh() : city.getNameEn()) + ")";

                mCityNameTextView.setText(cityText);
            } else {
                mCityNameTextView.setText("(" + getFragmentActivity().getString(R.string.enroll_gps_fail) + ")");
            }

        }

    }

    protected void setHomeNameText(String gpsResultStr, String homeName) {
        if (mHomeNameTextView != null){
            mCityNameTextView.clearAnimation();
            mHomeNameTextView.setText(homeName);
            mCityNameTextView.setText("(" + gpsResultStr + ")");
        }

    }

    public void updateWeatherData() {
        if (mUserLocation == null || mUserLocation.getCityWeatherData() == null)
            return;
        mWeatherData = mUserLocation.getCityWeatherData().getWeather();
        setWeatherIcon();
        handleWeatherData(mWeatherData);
    }

    protected void handleWeatherData(Weather weatherData) {
//        if (weatherData == null)
//            return;
//        mPm25Value = Integer.valueOf(weatherData.getNow().getAirQuality()
//                .getAirQualityIndex().getPm25());
//        ((MainActivity) getFragmentActivity()).setCurrentHazeMoving();
    }


    protected void switchTimeView() {
        boolean isDaylight = AppConfig.shareInstance().isDaylight();

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

    public int getPm25Value() {
        return mPm25Value;
    }

    public void recycleBackground(){
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.destroyed();
        }
        if (mDialog != null){
            mDialog.cancel();
            mDialog = null;
        }
    }

    public void updateHomeCellData(int homeIndex, UserLocationData userLocationData){
        mHomeIndex = homeIndex;
        mUserLocation = userLocationData;
        bindData2View();
    }

    public void bindData2View(){
    }

    /**
     * update home name
     * @param homeName
     */
    public void updateHomeName(String homeName){
        if (mHomeNameTextView != null){
            mHomeNameTextView.setText(homeName);
        }
    }

    public UserLocationData getUserLocationData(){
        return mUserLocation;
    }


    private View.OnClickListener mHomeIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTutorialImageView != null && mTutorialImageView.getVisibility() == View.VISIBLE){
                ((MainActivity)mActivity).removeWeatherTutorial();
            }
            Intent intent = null;
            switch (v.getId()) {
                case R.id.weather_icon:
                    intent = new Intent(getFragmentActivity(), WeatherActivity.class);
                    if (mWeatherChartView != null && mWeatherChartView.getVisibility() == View.VISIBLE){
                        //just refresh
                        mDialog = LoadingProgressDialog.show(getFragmentActivity(), getString(R.string.enroll_loading));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Thread.sleep(1000);
                                    if (mDialog != null){
                                        mDialog.cancel();
                                        mDialog = null;
                                    }
                                }catch (Exception e){

                                }
                            }
                        }).start();

                        //no need to start new activity
                        intent = null;
                    }
                    else if (mUserLocation != null) {
                            intent.putExtra(WeatherActivity.LOCATION_ID, mUserLocation.getLocationID());
                    }
                    break;
                case R.id.emotion_icon:
                    if (mUserLocation != null){
                        intent = new Intent(getFragmentActivity(), WXEntryActivity.class);
                        intent.putExtra(AirTouchConstants.LOCATION_ID,mUserLocation.getLocationID());
                        if (mUserLocation.isHaveDeviceInThisLocation()) {
                            boolean isHasDevice = false;
                            if (mUserLocation.isHaveDeviceInThisLocation()) {
                                isHasDevice = true;
                            }
                            intent.putExtra(WXEntryActivity.IS_HAVE_DEVICE, isHasDevice);
                        }
                    }

                    break;
                case R.id.schedule_icon:

                    if (mUserLocation != null){
                        intent = new Intent();
                        intent.setClass(getFragmentActivity(), ArriveHomeActivity.class);
                        intent.putExtra(AirTouchConstants.LOCATION_ID,mUserLocation.getLocationID());
                        boolean isHasDevice = false;
                        if (mUserLocation.isHaveDeviceInThisLocation()){
                            isHasDevice = true;
                        }
                        intent.putExtra(ArriveHomeActivity.HAS_DEVICE_FLAG,isHasDevice);
                    }

                    break;
                default:
                    break;
            }
            if (intent != null) {
                ((MainActivity)mActivity).stopAllAnimation();
                startActivity(intent);
                getFragmentActivity().overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
            }
        }
    };

    public void stopSwitchBackground(boolean isNeedRecycle) {
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.stopSwitchBackground(isNeedRecycle);
        }
    }

    public void startSwitchBackground(){
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.startTimer(BlurImageUtil.MAIN_ACTVITIY_BLUR_RADIO);
        }
    }


    public int getmWeatherCode(){
        return mWeatherCode;
    }

    public void stopChartWeatherAnimation(){
        if (mWeatherChartView != null) {
            mWeatherChartView.stopAnimation();
        }
        isNeedWeatherChartOnTouch = false;
    }

    public void startCharWeatherAnimation(){
        if (mWeatherChartView != null) {
            mWeatherChartView.startAnimation();
        }
        isNeedWeatherChartOnTouch = true;
    }

    public void setMainViewPagerScroll(boolean isCanScroll){
        ((MainActivity)getActivity()).setViewPagerScroll(isCanScroll);
    }

    public void showWeatherTutorial(int tutorialHeight){
        if (mTutorialBitmap == null && (mWeatherChartView != null && mWeatherChartView.getVisibility() == View.VISIBLE)){
            AppConfig.shareInstance().setIsWeatherTutorial(true);

            mTutorialImageView = new ImageView(getFragmentActivity());

            mTutorialImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)mActivity).removeWeatherTutorial();
                }
            });

            mTutorialBitmap = BitmapUtil.createBitmapEffectly(getFragmentActivity(), R.drawable.course_weather);
            mTutorialImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mTutorialImageView.setImageBitmap(mTutorialBitmap);
            RelativeLayout.LayoutParams layoutParamsPa = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT, tutorialHeight + DensityUtil.dip2px(28));
            mTutorialImageView.setLayoutParams(layoutParamsPa);

            mTopFragmentView.addView(mTutorialImageView);

            int imageViewY = DensityUtil.getScreenHeight() - DensityUtil.dip2px(60) - DensityUtil.dip2px(28) - mWeatherChartView.getTodayViewHeight();

            ViewHelper.setTranslationY(mTutorialImageView,imageViewY);
        }
    }

    public void removeWeatherTutorial(){
        if (mTutorialBitmap != null){
            mTutorialBitmap.recycle();
            mTutorialBitmap = null;
        }
        if (mTutorialImageView != null){
            mTutorialImageView.setVisibility(View.GONE);
        }
        if (mTopFragmentView != null){
            mTopFragmentView.removeView(mTutorialImageView);
        }
    }

    public View getTopView(){
        return mTopFragmentView;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mWeatherChartView != null){
            mWeatherChartView.closeWeather();
        }
        stopSwitchBackground(true);

        if (mTutorialBitmap != null){
            mTutorialBitmap.recycle();
            mTutorialBitmap = null;
        }
    }

    public void setWeatherData(Weather weatherData){
        mWeatherData = weatherData;
        if (mWeatherData != null){
            mPm25Value = Integer.valueOf(weatherData.getNow().getAirQuality()
                    .getAirQualityIndex().getPm25());
            ((MainActivity) getFragmentActivity()).setCurrentHazeMoving();
        }

    }
}
