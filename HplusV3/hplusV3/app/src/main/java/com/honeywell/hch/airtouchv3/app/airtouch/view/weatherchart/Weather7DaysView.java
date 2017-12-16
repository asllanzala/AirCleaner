package com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Future;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.lib.util.DrawableUtil;

import java.util.List;

/**
 * Created by lynnliu on 10/20/15.
 */
public class Weather7DaysView extends RelativeLayout {

    private static final String TAG = "Weather7DaysView";

    // height of chart is fixed 130
    public static final int BACKGROUND_HEIGHT = DensityUtil.dip2px(130);
    // the background image is from -50°C to 50°C
    public static final int DRAWABLE_TOTAL_DEGREE = 100;
    public static final int TEMP_MAX = 50;
    public static final int TEMP_MIN = -50;
    public static final int TIME_LABEL_TEXT_SIZE = DensityUtil.dip2px(9);
    public static final int TIME_LABEL_MARGIN = DensityUtil.dip2px(15);
    public static final int TEMPERATURE_TEXT_SIZE = DensityUtil.dip2px(12);
    public static final int TEXT_ICON_DISTANCE = DensityUtil.dip2px(5);

    private Weather7DaysBackground mWeather7DaysBackground;
    private Weather7DaysIcons mWeather7DaysIcons;

    private int mWidth;
    private int mHeight;
    // the height of 1° at the image
    private float mHeightPerDegree;
    private int mIconDrawableHeight;
    private Bitmap mBackgroundBitmap;
    private Bitmap mBackgroundScaledBitmap;

    private Context mContext;
    private WeatherPageData mWeatherPageData;
    private int mMaxTemp = TEMP_MIN;
    private int mMinTemp = TEMP_MAX;

    public Weather7DaysView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public Weather7DaysView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setWeather(WeatherPageData weatherPageData) {
        mWeather7DaysBackground.closeAndRecycle();

        mWeatherPageData = weatherPageData;
        mWeather7DaysBackground.setWeather(mWeatherPageData);
        mWeather7DaysIcons.setWeather(mWeatherPageData);

        List<Future> futures = weatherPageData.getWeather().getFutureList();
        if (futures == null) {
            return;
        }
        for (Future future : futures) {
            if (future.getHigh() > mMaxTemp) {
                mMaxTemp = future.getHigh();
            }
            if (future.getHigh() < mMinTemp) {
                mMinTemp = future.getHigh();
            }
        }

        if (mMinTemp != TEMP_MAX) {
            if (mBackgroundBitmap == null) {
                mBackgroundBitmap = BitmapUtil.createBitmapEffectly(mContext, R.drawable.sdays);
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

            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;

            mWeather7DaysBackground.setWeatherParams(mBackgroundScaledBitmap, mHeightPerDegree);
            mWeather7DaysIcons.initView(mHeightPerDegree);

            postInvalidate();
        }
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.weather_7day, this);
        mWeather7DaysBackground = (Weather7DaysBackground) view.findViewById(R.id.weather_7day_background);
        mWeather7DaysIcons = (Weather7DaysIcons) view.findViewById(R.id.weather_7day_icons);

        mIconDrawableHeight = getResources().getDrawable(R.drawable.cloudy).getIntrinsicHeight();

        mWeather7DaysBackground.initView(mIconDrawableHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = TEMPERATURE_TEXT_SIZE + TEXT_ICON_DISTANCE + mIconDrawableHeight
                + BACKGROUND_HEIGHT + TIME_LABEL_TEXT_SIZE * 2 + TIME_LABEL_MARGIN * 2;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec
                .getMode(heightMeasureSpec)));
    }

    public void closeAndRecycle() {
        mWeather7DaysBackground.closeAndRecycle();
        mWeather7DaysIcons.closeAndRecycle();

        if (mBackgroundBitmap != null){
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }

    }
}
