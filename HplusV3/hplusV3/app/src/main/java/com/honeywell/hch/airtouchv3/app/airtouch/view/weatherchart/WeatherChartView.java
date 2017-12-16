package com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.BaseLocationFragment;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.AirQualityIndex;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Future;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Now;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by lynnliu on 10/21/15.
 */
public class WeatherChartView extends RelativeLayout {

    private static final String TAG = "WeatherChartView";

    private TextView mChartTitleTextView;

    // weather value view
    private ImageView mWeatherIconImageView;
    private TextView mWeatherTextView;
    private TextView mAQITextView;
    private TextView mPM25TextView;
    private TextView mTemperatureTextView;

    // weather chart
    private LinearLayout mWeatherValueLayout;
    private WeatherTodayView mWeatherTodayView;
    private Weather7DaysView mWeather7DaysView;

    private Context mContext;
    private int mode = 0;
    private double oldDist;
    private boolean is7DaysChartShow = false;
    private WeatherPageData mWeatherPageData;

    private ChartSwitchListener mChartSwitchListener;

    private BaseLocationFragment mBaseLocationFragment;

    private boolean isCanScroll = true;

    public final static int PM25AQI_MEDIUM_LIMIT = 75;
    public final static int PM25AQI_HIGH_LIMIT = 150;

    private int[] mWeatherIconID = {R.drawable.sunny_big, R.drawable.sunny_big, R.drawable.sunny_big,
            R.drawable.sunny_big, R.drawable.heavycloudy_big, R.drawable.lightcloudy_big,
            R.drawable.lightcloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big,
            R.drawable.cloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big,
            R.drawable.rain_big, R.drawable.rain_big, R.drawable.rain_big, R.drawable.rain_big,
            R.drawable.rain_big, R.drawable.rain_big, R.drawable.rain_big, R.drawable.rainandsnow_big,
            R.drawable.snowy_big, R.drawable.snowy_big, R.drawable.snowy_big, R.drawable.snowy_big,
            R.drawable.snowy_big, R.drawable.cloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big,
            R.drawable.cloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big,
            R.drawable.cloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big,
            R.drawable.cloudy_big, R.drawable.cloudy_big, R.drawable.cloudy_big};

    public WeatherChartView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public WeatherChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public void initView() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.weather_chart, this);
        mWeatherTodayView = (WeatherTodayView) view.findViewById(R.id.weather_today_chart);
        mWeatherTodayView.setmWeatherChartView(this);

        mWeather7DaysView = (Weather7DaysView) view.findViewById(R.id.weather_7days_chart);
        mChartTitleTextView = (TextView) view.findViewById(R.id.weather_chart_title);

        mWeatherIconImageView = (ImageView) view.findViewById(R.id.weather_icon);
        mWeatherTextView = (TextView) view.findViewById(R.id.weather_text);
        mAQITextView = (TextView) view.findViewById(R.id.weather_aqi_value);
        mPM25TextView = (TextView) view.findViewById(R.id.weather_pm25_value);
        mTemperatureTextView = (TextView) view.findViewById(R.id.weather_temperature_range);
        mWeatherValueLayout = (LinearLayout) view.findViewById(R.id.weather_outdoor);

        mChartTitleTextView.setText(getResources().getString(R.string.weather_today));
        mChartTitleTextView.setAlpha(0.35f);
        mWeatherValueLayout.setAlpha(0.35f);
    }

    public void setWeather(WeatherPageData weatherPageData) {
        if (weatherPageData != null && weatherPageData.getWeather() != null) {
            mWeatherPageData = weatherPageData;
            if (mBaseLocationFragment != null){
                mBaseLocationFragment.setWeatherData(weatherPageData.getWeather());
            }
            Now thisNowWeather = weatherPageData.getWeather().getNow();
            if (thisNowWeather != null) {
                mChartTitleTextView.setAlpha(1);
                mWeatherValueLayout.setAlpha(1);
                int weatherCode = thisNowWeather.getCode();
                if (weatherCode == 99) {
                    weatherCode = mWeatherIconID.length - 1;
                }
                mWeatherTextView.setText(thisNowWeather.getText());
                mWeatherIconImageView.setImageResource(mWeatherIconID[weatherCode]);

                //start weather effect
                if (mBaseLocationFragment != null){
                    mBaseLocationFragment.setWeatherIconFromWeatherChart(weatherCode);
                }

                AirQualityIndex airQuality = thisNowWeather.getAirQuality().getAirQualityIndex();
                if (airQuality != null) {
                    mAQITextView.setText(airQuality.getAqi());
                    mPM25TextView.setText(airQuality.getPm25());
                    if (Integer.valueOf(airQuality.getAqi()) <= PM25AQI_MEDIUM_LIMIT ) {
                        mAQITextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_good));
                    } else if (Integer.valueOf(airQuality.getAqi()) <= PM25AQI_HIGH_LIMIT) {
                        mAQITextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_bad));
                    } else {
                        mAQITextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_worst));
                    }
                    if (Integer.valueOf(airQuality.getPm25()) <= PM25AQI_MEDIUM_LIMIT ) {
                        mPM25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_good));
                    } else if (Integer.valueOf(airQuality.getPm25()) <= PM25AQI_HIGH_LIMIT) {
                        mPM25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_bad));
                    } else {
                        mPM25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_worst));
                    }
                }
            }
            if (weatherPageData.getWeather().getFutureList() != null && weatherPageData.getWeather()
                    .getFutureList().size() > 0) {
                Future today = weatherPageData.getWeather().getFutureList().get(0);
                mTemperatureTextView.setText(getResources().getString(R.string
                        .weather_temperature, today.getLow(), today.getHigh()));
            }
        } else {
            mChartTitleTextView.setAlpha(0.35f);
            mWeatherValueLayout.setAlpha(0.35f);
        }
    }

    public void setHourlyWeather(WeatherPageData weatherPageData) {
        if (weatherPageData != null && weatherPageData.getWeather() != null) {
            mWeatherPageData = weatherPageData;
            mWeatherTodayView.setWeather(weatherPageData);
            mWeather7DaysView.setWeather(weatherPageData);
        }
    }

    private void switch27DaysChart() {
        if (is7DaysChartShow)
            return;
        is7DaysChartShow = true;
        mWeather7DaysView.setVisibility(VISIBLE);
        mChartTitleTextView.setText(getResources().getString(R.string.weather_recent));
        ObjectAnimator show7DaysObj = ObjectAnimator.ofFloat(mWeather7DaysView, "alpha", 0, 1);
        ObjectAnimator hideTodayObj = ObjectAnimator.ofFloat(mWeatherTodayView, "alpha", 1, 0);
        ObjectAnimator hideValueObj = ObjectAnimator.ofFloat(mWeatherValueLayout, "alpha", 1, 0);
        ObjectAnimator scaleXObj = ObjectAnimator.ofFloat(mWeatherTodayView, "scaleX", 1f, 1 / 9f);
        ObjectAnimator translateXObj = ObjectAnimator.ofFloat(mWeatherTodayView, "translationX",
                0, 0 - DensityUtil.getScreenWidth() / 9 * 3f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(1000);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.playTogether(show7DaysObj, hideTodayObj, hideValueObj, scaleXObj,
                translateXObj);
        animSet.start();

        if (mChartSwitchListener != null) {
            mChartSwitchListener.on7DaysChart();
        }
    }

    private void switch2TodayChart() {
        if (!is7DaysChartShow)
            return;
        is7DaysChartShow = false;
        mWeather7DaysView.setVisibility(GONE);
        mChartTitleTextView.setText(getResources().getString(R.string.weather_today));
        ObjectAnimator hide7DaysObj = ObjectAnimator.ofFloat(mWeather7DaysView, "alpha", 1, 0);
        ObjectAnimator showTodayObj = ObjectAnimator.ofFloat(mWeatherTodayView, "alpha", 0, 1);
        ObjectAnimator showValueObj = ObjectAnimator.ofFloat(mWeatherValueLayout, "alpha", 0, 1);
        ObjectAnimator scaleXObj = ObjectAnimator.ofFloat(mWeatherTodayView, "scaleX", 1 / 9f, 1);
        ObjectAnimator translateXObj = ObjectAnimator.ofFloat(mWeatherTodayView, "translationX",
                0 - DensityUtil.getScreenWidth() / 9 * 3f, 0);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(1000);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.playTogether(hide7DaysObj, showTodayObj, showValueObj, scaleXObj,
                translateXObj);
        animSet.start();

        if (mChartSwitchListener != null) {
            mChartSwitchListener.onTodayChart();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mWeatherPageData == null || !isCanScroll) {
            return false;
        }
        if (mBaseLocationFragment != null) {
            mBaseLocationFragment.setMainViewPagerScroll(false);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = 1;
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                mode += 1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode >= 2) {
                    double newDist = spacing(event);
                    if (newDist > oldDist + 1) {
                        switch2TodayChart();
                        oldDist = newDist;
                    }
                    if (newDist < oldDist - 1) {
                        switch27DaysChart();
                        oldDist = newDist;
                    }
                }
                break;
            default:
                break;
        }
        return ((is7DaysChartShow && isTouchInView(event)) || false);
    }

    private  boolean isTouchInView(MotionEvent ev) {
        int[] touchLocation = new int[2];
        this.getLocationOnScreen(touchLocation);
        float motionX = ev.getRawX();
        float motionY = ev.getRawY();

        int bottom = this.getBottom();
        return motionX >= touchLocation[0]
                && motionX <= (touchLocation[0] + this.getWidth())
                && motionY >= bottom - DensityUtil.dip2px(160)
                && motionY <= bottom;
    }

    private double spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    public void setChartSwitchListener(ChartSwitchListener chartSwitchListener) {
        mChartSwitchListener = chartSwitchListener;
    }

    public interface ChartSwitchListener {

        public void onTodayChart();

        public void on7DaysChart();
    }

    // for start animations like rain
    public void stopAnimation() {
        mWeatherTodayView.stopAnimation();
    }

    // for stop animations like rain
    public void startAnimation() {
        mWeatherTodayView.startAnimation();
    }

    // close weather view and recycle resource
    public void closeWeather() {
        mWeatherTodayView.closeAndRecycle();
        mWeather7DaysView.closeAndRecycle();
    }

    public void setBaseLocationFragment(BaseLocationFragment baseLocationFragment) {
        mBaseLocationFragment = baseLocationFragment;
    }

    public void setWeatherCharViewCanScroll(boolean isScroll) {
        isCanScroll = isScroll;
    }

    public void showTutorial(int height){
        mBaseLocationFragment.showWeatherTutorial(height);
    }

    public int getTodayViewHeight(){

        return mWeatherTodayView.getHeight();
    }
}
