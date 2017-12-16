package com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Future;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

import java.util.List;

/**
 * today and future 24 hours temperature line chart
 * Created by lynnliu on 10/16/15.
 */
public class Weather7DaysBackground extends View {

    private static final String TAG = "WeatherTodayChart";
    private static final int BOTTOM_LINE_WIDTH = DensityUtil.dip2px(1);

    private int mWidth;
    private int mHeight;
    private int mTextMargin;
    private int mIconDrawableHeight;

    private float mLinePadding;
    // the height of 1° at the image
    private float mHeightPerDegree;

    private Bitmap mBackgroundBitmap;
    private Paint mPaint;
    private Path mPath;

    private List<Future> mFutures = null;
    private int mMaxTemp = WeatherTodayView.TEMP_MIN;

    public Weather7DaysBackground(Context context) {
        super(context);
    }

    public Weather7DaysBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initView(int iconDrawableHeight) {
        mIconDrawableHeight = iconDrawableHeight;
        mTextMargin = Weather7DaysView.TIME_LABEL_TEXT_SIZE * 2 + Weather7DaysView
                .TIME_LABEL_MARGIN * 2;
        mPaint = new Paint();
        mPath = new Path();
    }

    public void setWeatherParams(Bitmap backgroundBitmap, float heightPerDegree) {
        mBackgroundBitmap = backgroundBitmap;
        mHeightPerDegree = heightPerDegree;
    }

    public void setWeather(WeatherPageData weatherPageData) {
        if (weatherPageData.getWeather().getFutureList() == null) {
            return;
        }
        mFutures = weatherPageData.getWeather().getFutureList();
        for (Future future : mFutures) {
            if (future.getHigh() > mMaxTemp) {
                mMaxTemp = future.getHigh();
            }
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFutures == null || mFutures.size() == 0 || mBackgroundBitmap == null) {
            return;
        }
        drawBarChart(canvas);
    }

    private void drawBarChart(Canvas canvas) {
        canvas.save();
        canvas.translate(0, Weather7DaysView.TEMPERATURE_TEXT_SIZE + Weather7DaysView
                .TEXT_ICON_DISTANCE + mIconDrawableHeight);
        mPath.reset();
        mPath.moveTo(0, 0);
        float xOffset = mLinePadding - 0.7f;
        float yOffset = 0;
        for (int i = 0; i < 7; i++) {
            if (mFutures.size() <= i)
                break;
            Future future = mFutures.get(i);
            if (future != null) {
                yOffset = (mMaxTemp - future.getHigh()) * mHeightPerDegree;
            }
            mPath.lineTo(xOffset, yOffset);
            mPath.lineTo(xOffset + mLinePadding, yOffset);
            xOffset += mLinePadding + 1;
        }
        mPath.lineTo(mWidth, yOffset);
        mPath.lineTo(mWidth, mHeight - mTextMargin);
        mPath.lineTo(0, mHeight - mTextMargin);
        mPath.lineTo(0, 0);
        mPath.close();
        canvas.clipPath(mPath);
        canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);

        // draw bottom line, white, alpha 50%, width 1
        canvas.restore();
        mPath.reset();
        mPath.moveTo(0, mHeight - mTextMargin);
        mPath.lineTo(mWidth, mHeight - mTextMargin);
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(127);
        mPaint.setStrokeWidth(BOTTOM_LINE_WIDTH);
        canvas.drawPath(mPath, mPaint);
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
        if (mBackgroundBitmap != null) {
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }
    }
}
