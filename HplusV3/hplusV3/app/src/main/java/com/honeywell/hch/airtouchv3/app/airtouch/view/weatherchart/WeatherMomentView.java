package com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.FutureHour;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HistoryHour;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Hour;
import com.honeywell.hch.airtouchv3.lib.util.DateTimeUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

/**
 * Created by lynnliu on 10/20/15.
 */
public class WeatherMomentView extends View {

    private static final int DASH_LINE_WIDTH = 1;
    private static final int TEXT_SIZE = DensityUtil.dip2px(15);

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

    private int mWidth;
    private int mHeight;
    private int mLinePadding;
    // the height of 1° at the image
    private float mHeightPerDegree;
    private float mTextMargin;

    // region parameters for weather icon
    private Drawable mIconDrawable;
    // the intrinsic width on the basis of screen density
    private int mIconDrawableWidth;
    // the intrinsic height on the basis of screen density
    private int mIconDrawableHeight;
    // endregion

    private Paint mPaint;
    private Path mPath;

    private WeatherPageData mWeatherPageData;
    private Hour mCurrentHour = null;
    private int mCurrentPoint = 2;
    private int mMaxTemp = WeatherTodayView.TEMP_MIN;

    public WeatherMomentView(Context context) {
        super(context);
    }

    public WeatherMomentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setWeather(WeatherPageData weatherPageData) {
        mWeatherPageData = weatherPageData;
        if (weatherPageData.getHourlyData() != null && weatherPageData.getHourlyData().length > mCurrentPoint)
            mCurrentHour = weatherPageData.getHourlyData()[mCurrentPoint];
        if (weatherPageData.getHourlyData() != null && weatherPageData.getHourlyData().length > 0) {
            for (Hour hour : weatherPageData.getHourlyData()) {
                if (hour != null && hour.getTemperature() > mMaxTemp) {
                    mMaxTemp = hour.getTemperature();
                }
            }
        }
        if (mCurrentHour != null) {
            int weatherCode = mCurrentHour.getCode();
            if (weatherCode == 99) {
                weatherCode = mWeatherIconID.length - 1;
            }
            mIconDrawable = getResources().getDrawable(mWeatherIconID[weatherCode]);
            postInvalidate();
        }
    }

    public void initView() {
        mTextMargin = WeatherTodayView.TIME_LABEL_TEXT_SIZE * 2 + WeatherTodayView
                .TIME_LABEL_MARGIN * 2;
        mIconDrawable = getResources().getDrawable(mWeatherIconID[0]);
        mIconDrawableHeight = mIconDrawable.getIntrinsicHeight();
        mIconDrawableWidth = mIconDrawable.getIntrinsicWidth();
        mPaint = new Paint();
        mPath = new Path();
    }

    public void setWeatherParams(float heightPerDegree) {
        mHeightPerDegree = heightPerDegree;
    }

    public void setCurrentPoint(int currentPoint) {
        mCurrentPoint = currentPoint;
        if (mWeatherPageData.getHourlyData() != null && mWeatherPageData.getHourlyData().length > mCurrentPoint)
            mCurrentHour = mWeatherPageData.getHourlyData()[mCurrentPoint];

        if (mCurrentHour != null) {
            int weatherCode = mCurrentHour.getCode();
            if (weatherCode == 99) {
                weatherCode = mWeatherIconID.length - 1;
            }
            mIconDrawable = getResources().getDrawable(mWeatherIconID[weatherCode]);
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurrentHour == null)
            return;

        drawOneMomentData(canvas);
    }

    private void drawOneMomentData(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(DensityUtil.dip2px(DASH_LINE_WIDTH));
        PathEffect effect = new DashPathEffect(new float[]{16, 8, 16, 8}, 8);
        mPaint.setPathEffect(effect);

        // set start,end of line
        mPath.reset();
        float currentHourPadding = (mMaxTemp - mCurrentHour.getTemperature()) * mHeightPerDegree;
        mPath.moveTo(mCurrentPoint * mLinePadding, currentHourPadding + mIconDrawableHeight +
                WeatherTodayView.LINE_ICON_DISTANCE);
        mPath.lineTo(mCurrentPoint * mLinePadding, mHeight - mTextMargin);
        canvas.drawPath(mPath, mPaint);

        // draw weather icon
        if (mIconDrawable != null) {
            canvas.translate(mCurrentPoint * mLinePadding - mIconDrawableWidth / 2, currentHourPadding);
            mIconDrawable.setBounds(0, 0, mIconDrawableWidth, mIconDrawableHeight);
            mIconDrawable.draw(canvas);
        }

        // draw time point
        Drawable pointDrawable = getResources().getDrawable(R.drawable.current_temprature_point);
        int pointDrawableWidth = pointDrawable.getIntrinsicWidth();
        int pointDrawableHeight = pointDrawable.getIntrinsicHeight();
        canvas.translate(mIconDrawableWidth / 2 - pointDrawableWidth / 2, mIconDrawableHeight +
                WeatherTodayView.LINE_UP_CHART_HEIGHT + WeatherTodayView.LINE_ICON_DISTANCE -
                pointDrawableHeight / 2);
        pointDrawable.setBounds(0, 0, pointDrawable.getIntrinsicWidth(), pointDrawable.getIntrinsicHeight());
        pointDrawable.draw(canvas);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(TEXT_SIZE);
        canvas.drawText(mCurrentHour.getTemperature() + "°C", DensityUtil.dip2px(10), DensityUtil
                        .dip2px(10), mPaint);
        if (mCurrentPoint > 1) {
            FutureHour futureHour = (FutureHour) mCurrentHour;
            canvas.drawText(DateTimeUtil.getDateTimeString(futureHour.getDate(), DateTimeUtil
                    .WEATHER_CHART_TIME_FORMAT), DensityUtil.dip2px(10), DensityUtil.dip2px(30),
                    mPaint);
        } else {
            HistoryHour historyHour = (HistoryHour) mCurrentHour;
            canvas.drawText(DateTimeUtil.getDateTimeString(historyHour.getDate(), DateTimeUtil
                            .WEATHER_CHART_TIME_FORMAT), DensityUtil.dip2px(10), DensityUtil
                            .dip2px(30), mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mLinePadding = mWidth / 27;
        mHeight = WeatherTodayView.BACKGROUND_HEIGHT + mIconDrawableHeight + WeatherTodayView
                .LINE_ICON_DISTANCE + WeatherTodayView.LINE_UP_CHART_HEIGHT + WeatherTodayView
                .TIME_LABEL_TEXT_SIZE * 2 + WeatherTodayView.TIME_LABEL_MARGIN * 2;
        setMeasuredDimension(mWidth, mHeight);
    }
}
