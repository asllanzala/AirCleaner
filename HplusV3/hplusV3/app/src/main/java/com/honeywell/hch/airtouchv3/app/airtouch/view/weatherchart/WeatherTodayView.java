package com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Hour;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.lib.util.DrawableUtil;

/**
 * Created by lynnliu on 10/19/15.
 */
public class WeatherTodayView extends RelativeLayout {

    private static final String TAG = "WeatherTodayView";

    // height of chart is fixed 130
    public static final int BACKGROUND_HEIGHT = DensityUtil.dip2px(130);
    // the background image is from -50°C to 50°C
    public static final int DRAWABLE_TOTAL_DEGREE = 100;
    public static final int TEMP_MAX = 50;
    public static final int TEMP_MIN = -50;
    public static final int LINE_ICON_DISTANCE = DensityUtil.dip2px(5);
    public static final int LINE_UP_CHART_HEIGHT = DensityUtil.dip2px(10);
    public static final int TIME_LABEL_TEXT_SIZE = DensityUtil.dip2px(9);
    public static final int TIME_LABEL_MARGIN = DensityUtil.dip2px(5);
    public static final int TIME_LABEL_TOP_MARGIN = DensityUtil.dip2px(2);

    private WeatherTodayBackground mWeatherTodayBackground;
    private WeatherMomentView mWeatherMomentView;
    private Bitmap mBackgroundBitmap;
    private Bitmap mBackgroundScaledBitmap;

    // region parameters for gesture
    private float mPosX = 0f;
    private float mPosY = 0f;
    private float mCurrentPosX = 0f;
    private float mCurrentPosY = 0f;
    private int mCurrentPoint = 2;
    private boolean mIsClose2Start = false;
    // endregion

    private int mWidth;
    private int mHeight;
    private float mLinePadding;
    // the height of 1° at the image
    private float mHeightPerDegree;
    private int mIconDrawableHeight;

    private Context mContext;
    private WeatherPageData mWeatherPageData;
    private int mMaxTemp = TEMP_MIN;
    private int mMinTemp = TEMP_MAX;

    private WeatherChartView mWeatherChartView;

    public WeatherTodayView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public WeatherTodayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setWeather(WeatherPageData weatherPageData) {
        mWeatherTodayBackground.recycleBackgroundBitmap();

        mWeatherPageData = weatherPageData;
        mWeatherTodayBackground.setWeather(mWeatherPageData);
        mWeatherMomentView.setWeather(mWeatherPageData);
        Hour[] hours = weatherPageData.getHourlyData();
        if (hours != null && hours.length > 0) {
            for (Hour hour : hours) {
                if (hour != null && hour.getTemperature() > mMaxTemp) {
                    mMaxTemp = hour.getTemperature();
                }
                if (hour != null && hour.getTemperature() < mMinTemp) {
                    mMinTemp = hour.getTemperature();
                }
            }
        }

        if (mMinTemp != TEMP_MAX) {
            if (mBackgroundBitmap == null) {
                mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.today);
            }
            if (mBackgroundScaledBitmap != null) {
                mBackgroundScaledBitmap.recycle();
                mBackgroundScaledBitmap = null;
            }
            mHeightPerDegree = BACKGROUND_HEIGHT / 5f * 3 / Math.abs(mMaxTemp - mMinTemp);
            float srcHeightPerDegree = mBackgroundBitmap.getHeight() / 1f / DRAWABLE_TOTAL_DEGREE;
            mBackgroundScaledBitmap = DrawableUtil.zoomClipBitmap(mBackgroundBitmap, 0,
                    (TEMP_MAX - mMaxTemp) * srcHeightPerDegree, DensityUtil.getScreenWidth() / 1f
                            / mBackgroundBitmap.getWidth(), mHeightPerDegree / srcHeightPerDegree,
                    DensityUtil.getScreenWidth(), BACKGROUND_HEIGHT);

            if (mBackgroundBitmap != null) {
                mBackgroundBitmap.recycle();
                mBackgroundBitmap = null;
            }

            mWeatherTodayBackground.setWeatherParams(mBackgroundScaledBitmap, mHeightPerDegree);
            mWeatherMomentView.setWeatherParams(mHeightPerDegree);

            invalidate();
            showWeatherTutorial();
        }
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.weather_today, this);
        mWeatherTodayBackground = (WeatherTodayBackground) view.findViewById(R.id.weather_today_background);
        mWeatherMomentView = (WeatherMomentView) view.findViewById(R.id.weather_today_one_moment);

        mIconDrawableHeight = getResources().getDrawable(R.drawable.cloudy).getIntrinsicHeight();
        mWeatherTodayBackground.initView();
        mWeatherMomentView.initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = BACKGROUND_HEIGHT + mIconDrawableHeight + LINE_ICON_DISTANCE +
                LINE_UP_CHART_HEIGHT + TIME_LABEL_TEXT_SIZE * 2 + TIME_LABEL_MARGIN * 2;
        mLinePadding = mWidth / 27f;
        setMeasuredDimension(mWidth, mHeight);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mWeatherPageData == null)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPosX = event.getX();
                mPosY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentPosX = event.getX();
                mCurrentPosY = event.getY();
                if (Math.abs(mCurrentPosX - mLinePadding * 2) < (2 * mLinePadding)) {
                    mIsClose2Start = true;
                }
                if (Math.abs(mCurrentPosX - mPosX) > 5 && mIsClose2Start) {
                    int offset = (int) (mCurrentPosX / mLinePadding);
                    mCurrentPoint = Math.abs(offset);
                    if (mCurrentPoint < 0) {
                        mCurrentPoint = 0;
                    }
                    if (mCurrentPoint >= mWeatherPageData.getHourlyData().length) {
                        mCurrentPoint = mWeatherPageData.getHourlyData().length - 1;
                    }
                    updateDataByCurrentPoint();
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurrentPoint = 2;
                mIsClose2Start = false;
                updateDataByCurrentPoint();
                break;
        }
        return true;
    }

    private void updateDataByCurrentPoint() {
        mWeatherMomentView.setCurrentPoint(mCurrentPoint);
    }

    public void startAnimation() {
        mWeatherTodayBackground.startAnimation();
    }

    public void stopAnimation() {
        mWeatherTodayBackground.stopAnimation();
    }

    public void closeAndRecycle() {
        mWeatherTodayBackground.closeAndRecycle();

        if (mBackgroundBitmap != null){
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }

        if (mBackgroundScaledBitmap != null) {
            mBackgroundScaledBitmap.recycle();
            mBackgroundScaledBitmap = null;
        }
    }

    private void showWeatherTutorial(){

        if (!AppConfig.isWeatherTutorial && mHeight > 0) {
             mWeatherChartView.showTutorial(mHeight);
        }
    }

    public void setmWeatherChartView(WeatherChartView weatherChartView){
        this.mWeatherChartView = weatherChartView;
    }
}
