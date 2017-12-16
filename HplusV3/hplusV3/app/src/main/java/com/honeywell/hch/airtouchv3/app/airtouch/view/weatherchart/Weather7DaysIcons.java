package com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Future;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.DateTimeUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

import java.util.List;

/**
 * Created by lynnliu on 10/20/15.
 */
public class Weather7DaysIcons extends View {

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

    private int[] mWeekStringID = {R.string.weather_7day_sun, R.string.weather_7day_mon, R.string
            .weather_7day_tue, R.string.weather_7day_wed, R.string.weather_7day_thu, R.string
            .weather_7day_fri, R.string.weather_7day_sat};

    private int mWidth;
    private int mHeight;
    private float mLinePadding;
    // the height of 1° at the image
    private float mHeightPerDegree;
    private int mTextMargin;

    // region parameters for weather icon
    // the intrinsic width on the basis of screen density
    private int mIconDrawableWidth;
    // the intrinsic height on the basis of screen density
    private int mIconDrawableHeight;
    private Bitmap[] mIconsBitmap;
    // endregion

    private List<Future> mFutures = null;
    private int mMaxTemp = Weather7DaysView.TEMP_MIN;
    private int mMinTemp = Weather7DaysView.TEMP_MAX;
    private Paint mPaint;

    private Context mContext;

    public Weather7DaysIcons(Context context) {
        super(context);
        mContext = context;
    }

    public Weather7DaysIcons(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setWeather(WeatherPageData weatherPageData) {
        if (weatherPageData.getWeather().getFutureList() == null) {
            return;
        }
        mFutures = weatherPageData.getWeather().getFutureList();
        if (mFutures == null) {
            return;
        }
        for (Future future : mFutures) {
            if (future.getHigh() > mMaxTemp) {
                mMaxTemp = future.getHigh();
            }
            if (future.getHigh() < mMinTemp) {
                mMinTemp = future.getHigh();
            }
        }
        postInvalidate();
    }

    public void initView(float heightPerDegree) {
        mHeightPerDegree = heightPerDegree;
        mIconsBitmap = new Bitmap[7];
        mIconsBitmap[0] = BitmapFactory.decodeResource(getResources(), mWeatherIconID[0]);
        mIconDrawableWidth = mIconsBitmap[0].getWidth();
        mIconDrawableHeight = mIconsBitmap[0].getHeight();
        mTextMargin = Weather7DaysView.TIME_LABEL_TEXT_SIZE * 2 + Weather7DaysView
                .TIME_LABEL_MARGIN * 2;
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFutures == null || mFutures.size() == 0)
            return;

        drawHighLowTemperature(canvas);
    }

    private void drawHighLowTemperature(Canvas canvas) {
        canvas.save();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(Weather7DaysView.TEMPERATURE_TEXT_SIZE);
        float xOffset = mLinePadding - 0.7f;
        for (int i = 0; i < mIconsBitmap.length; i++) {
            Future future = mFutures.get(i);
            if (future != null) {
                canvas.save();
                float currentHourPadding = (mMaxTemp - future.getHigh()) * mHeightPerDegree;

                // draw high temperature
                String highTemp = future.getHigh() + "°C";
                float[] highTempWidths = new float[highTemp.length()];
                mPaint.getTextWidths(highTemp, highTempWidths);
                float highTempWidth = 0;
                for (float width : highTempWidths) {
                    highTempWidth += width;
                }
                canvas.translate(0, currentHourPadding + Weather7DaysView.TEMPERATURE_TEXT_SIZE);
                mPaint.setAlpha(255);
                canvas.drawText(highTemp, xOffset + (mLinePadding - highTempWidth) / 2, 0, mPaint);

                // draw weather icon
                int weatherCode = future.getCode1();
                if (weatherCode == 99) {
                    weatherCode = mWeatherIconID.length - 1;
                }
                mIconsBitmap[i] = BitmapUtil.createBitmapEffectly(mContext, mWeatherIconID[weatherCode]);
                canvas.translate(xOffset + (mLinePadding - mIconDrawableWidth) / 2,
                        Weather7DaysView.TEXT_ICON_DISTANCE);
                RectF dstRectF = new RectF(0, 0, mIconDrawableWidth, mIconDrawableHeight);
                canvas.drawBitmap(mIconsBitmap[i], null, dstRectF, null);

                // draw low temperature
                String lowTemp = future.getLow() + "°C";
                float[] lowTempWidths = new float[lowTemp.length()];
                mPaint.getTextWidths(lowTemp, lowTempWidths);
                float lowTempWidth = 0;
                for (float width : lowTempWidths) {
                    lowTempWidth += width;
                }
                canvas.translate(-(mLinePadding - mIconDrawableWidth) / 2, mIconDrawableHeight * 2);
                mPaint.setAlpha(127);
                canvas.drawText(lowTemp, (mLinePadding - lowTempWidth) / 2, 0, mPaint);

                // draw week day
                canvas.restore();
                canvas.save();
                String weekString = getResources().getString(mWeekStringID[DateTimeUtil
                        .getDateTimeFromString(DateTimeUtil.THINKPAGE_DATE_FORMAT, future.getDate
                                ()).getDay()]);
                float[] weekStringWidths = new float[weekString.length()];
                mPaint.getTextWidths(weekString, weekStringWidths);
                float weekStringWidth = 0;
                for (float width : weekStringWidths) {
                    weekStringWidth += width;
                }
                mPaint.setAlpha(255);
                canvas.drawText(weekString, xOffset + (mLinePadding - weekStringWidth) / 2,
                        mHeight - mTextMargin / 2 - Weather7DaysView.TIME_LABEL_MARGIN / 2, mPaint);

                xOffset += mLinePadding + 1;
            }
            canvas.restore();
            String todayString = getResources().getString(R.string.weather_today);
            float[] todayStringWidths = new float[todayString.length()];
            mPaint.getTextWidths(todayString, todayStringWidths);
            float todayStringWidth = 0;
            for (float width : todayStringWidths) {
                todayStringWidth += width;
            }
            canvas.drawText(getResources().getString(R.string.weather_today), mLinePadding +
                            (mLinePadding - todayStringWidth) / 2, mHeight - Weather7DaysView
                    .TIME_LABEL_MARGIN, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mLinePadding = (mWidth - DensityUtil.px2dip(8)) / 9f;
        mHeight = Weather7DaysView.TEMPERATURE_TEXT_SIZE + Weather7DaysView.TEXT_ICON_DISTANCE +
                mIconDrawableHeight + Weather7DaysView.BACKGROUND_HEIGHT + mTextMargin;
        setMeasuredDimension(mWidth, mHeight);
    }

    public void closeAndRecycle() {
        if (mIconsBitmap == null || mIconsBitmap.length == 0) {
            return;
        }
        for (int i = 0; i < mIconsBitmap.length; i++) {
            if (mIconsBitmap[i] != null) {
                mIconsBitmap[i].recycle();
                mIconsBitmap[i] = null;
            }
        }
    }
}
