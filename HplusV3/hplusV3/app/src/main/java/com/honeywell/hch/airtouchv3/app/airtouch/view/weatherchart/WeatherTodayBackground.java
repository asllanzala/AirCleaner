package com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * today and future 24 hours temperature line chart
 * Created by lynnliu on 10/16/15.
 */
public class WeatherTodayBackground extends View {

    private static final String TAG = "WeatherTodayChart";
    private static final int TIME_ANNOTATION_TEXT_SIZE = DensityUtil.dip2px(9);
    private static final int BOTTOM_LINE_WIDTH = DensityUtil.dip2px(1);
    private static final int DASHED_LINE_WIDTH = DensityUtil.dip2px(1);
    private static final int ANIMATION_SLEEP_TIME = 100;

    private int mWidth;
    private int mHeight;

    private float mLinePadding;
    // the height of 1° at the image
    private float mHeightPerDegree;
    private float mTextMargin;
    private int mIconDrawableHeight;
    private int mRain2DrawableWidth;
    private int mRain2DrawableHeight;

    private Drawable mPointDrawable;
    private Bitmap mBackgroundBitmap;
    private Bitmap mRain1Bitmap;
    private Bitmap mRain2Bitmap;
    private Paint mPaint;
    private Path mPath;

    private Hour[] mHours = null;
    private List<RainBlock> mRainBlocks = new ArrayList<>();
    private List<List<RainPosition>> mRainPositions = new ArrayList<>();
    private int mMaxTemp = WeatherTodayView.TEMP_MIN;

    protected AnimThread mAnimThread;
    private boolean mIsAnimationRunning = false;
    private boolean mIsDrawing = false;

    private List<String> mHourTimeList = new ArrayList<>();
    private List<String> mDayTimeList = new ArrayList<>();

    public WeatherTodayBackground(Context context) {
        super(context);
    }

    public WeatherTodayBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initView() {
        mIconDrawableHeight = getResources().getDrawable(R.drawable.cloudy).getIntrinsicHeight();
        mPointDrawable = getResources().getDrawable(R.drawable.time_point);
        mRain1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.raindrop1);
        mRain2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.raindrop2);
        mRain2DrawableWidth = mRain2Bitmap.getWidth();
        mRain2DrawableHeight = mRain2Bitmap.getHeight();
        mTextMargin = WeatherTodayView.TIME_LABEL_TEXT_SIZE * 2 + WeatherTodayView
                .TIME_LABEL_MARGIN * 2;
        mPaint = new Paint();
        mPath = new Path();
    }

    public void setWeatherParams(Bitmap backgroundBitmap, float heightPerDegree) {
        mBackgroundBitmap = backgroundBitmap;
        mHeightPerDegree = heightPerDegree;
    }

    public void setWeather(WeatherPageData weatherPageData) {
        mHours = weatherPageData.getHourlyData();
        if (mHours != null && mHours.length > 0) {
            for (Hour hour : mHours) {
                if (hour != null && hour.getTemperature() > mMaxTemp) {
                    mMaxTemp = hour.getTemperature();
                }
            }
        }

        if (!mIsDrawing) {
            mRainBlocks.clear();
            boolean isRainStart = false;
            for (int i = 0; i < mHours.length; i++) {
                Hour hour = mHours[i];
                if (hour != null && hour.getCode() > 12 && hour.getCode() < 21) {
                    if (!isRainStart) {
                        generateRainBlock().rainStart = i;
                        isRainStart = true;
                    } else if (i == (mHours.length - 1) || (mHours[i + 1] != null && (mHours[i + 1]
                            .getCode() < 13 || mHours[i + 1].getCode() > 20))) {
                        generateRainBlock().rainEnd = i;
                        isRainStart = false;
                    }
                }
            }
            if (mLinePadding != 0) {
                generateRain();
            }
        }

        getWeatherCharCanvasHour();
        postInvalidate();
    }

    private void generateRain() {
        mRainPositions.clear();
        if (mRainBlocks != null && mRainBlocks.size() > 0) {
            for (int j = 0; j < mRainBlocks.size(); j++) {
                int rainStart = mRainBlocks.get(j).rainStart;
                int rainEnd = mRainBlocks.get(j).rainEnd;
                List<RainPosition> rainPositions = new ArrayList<>();
                int rainCount = 10 * (rainEnd - rainStart);
                for (int i = 0; i < rainCount; i++) {
                    rainPositions.add(new RainPosition(mLinePadding * (rainEnd - rainStart),
                            mHeight - (mMaxTemp - Math.max(mHours[rainStart].getTemperature(),
                                    mHours[rainEnd].getTemperature())) * mHeightPerDegree,
                            -mRain2DrawableWidth / 2, mRain2DrawableHeight / 2));
                }
                mRainPositions.add(rainPositions);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHours == null || mHours.length == 0 || mHours[0] == null || mIsDrawing || mBackgroundBitmap == null) {
            return;
        }
        mIsDrawing = true;
        drawLineChart(canvas);

        if (mRainBlocks != null && mRainBlocks.size() > 0) {
            mIsAnimationRunning = true;
            drawRainBlock(canvas);

            if (mAnimThread == null) {
                mAnimThread = new AnimThread();
                mAnimThread.start();
            }
        } else {
            mIsAnimationRunning = false;
        }

        mIsDrawing = false;
    }



    private void getWeatherCharCanvasHour(){
        mHourTimeList.clear();
        mDayTimeList.clear();
        for (int i = 0; i < 4; i++) {
            Hour hour = mHours[i * 8 + 1];
            if (hour == null) {
                continue;
            }
            Date date;
            if (i == 0) {
                HistoryHour historyHour = (HistoryHour) hour;
                date = historyHour.getDate();
                date.setMinutes(0);
            } else {
                FutureHour futureHour = (FutureHour) hour;
                date = futureHour.getDate();
            }
            if (date == null) {
                return;
            }
            String timeText = DateTimeUtil.getDateTimeString(date, DateTimeUtil
                    .WEATHER_CHART_TIME_FORMAT);

            mHourTimeList.add(timeText);

            String dayText = getResources().getString(DateTimeUtil.isTodayOrTomorrow(date));
            mDayTimeList.add(dayText);
        }
    }
    private void drawLineChart(Canvas canvas) {
        canvas.save();
        float lastX = 0;
        float lastY = 0;
        mPath.reset();
        mPath.moveTo(0, 0);
        float iconPadding = mIconDrawableHeight + WeatherTodayView.LINE_ICON_DISTANCE +
                WeatherTodayView.LINE_UP_CHART_HEIGHT;
        canvas.translate(0, iconPadding);
        for (int i = 0; i < mHours.length; i++) {
            Hour hour = mHours[i];
            if (hour != null) {
                float currentHourPadding = (mMaxTemp - hour.getTemperature()) * mHeightPerDegree;
                mPath.quadTo(lastX, lastY, mLinePadding * i, currentHourPadding);
                lastX = mLinePadding * i;
                lastY = currentHourPadding;
            }
        }
        mPath.quadTo(lastX, lastY, mWidth, lastY);
        float bottomPadding = mTextMargin + BOTTOM_LINE_WIDTH + mPointDrawable.getIntrinsicHeight();
        mPath.lineTo(mWidth, mHeight - bottomPadding);
        mPath.lineTo(0, mHeight - bottomPadding);
        mPath.lineTo(0, 0);
        mPath.close();
        canvas.clipPath(mPath);
        canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);

        // draw bottom line, white, alpha 50%, width 1
        canvas.restore();
        canvas.save();
        mPath.reset();
        mPaint.reset();
        mPath.moveTo(0, mHeight - mTextMargin);
        mPath.lineTo(mWidth, mHeight - mTextMargin);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(127);
        mPaint.setStrokeWidth(DASHED_LINE_WIDTH);
        canvas.drawPath(mPath, mPaint);

        canvas.translate(mLinePadding * 1.5f - mPointDrawable.getIntrinsicWidth() / 2f, mHeight -
                mTextMargin);
        mPointDrawable.setBounds(0, 0, mPointDrawable.getIntrinsicWidth(), mPointDrawable
                .getIntrinsicHeight());
        mPointDrawable.draw(canvas);
        for (int i = 0; i < 3; i++) {
            canvas.translate(mLinePadding * 8, 0);
            mPointDrawable.draw(canvas);
        }

        // draw time annotation
        canvas.restore();
        canvas.save();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(TIME_ANNOTATION_TEXT_SIZE);
        canvas.translate(0, mHeight - mTextMargin + WeatherTodayView.TIME_LABEL_MARGIN * 2);


        for (int i = 0; i < mHourTimeList.size(); i++) {
            String timeText = mHourTimeList.get(i);
            float[] timeWidths = new float[timeText.length()];
            mPaint.getTextWidths(timeText, timeWidths);
            float timeTextWidth = 0;
            for (float width : timeWidths) {
                timeTextWidth += width;
            }
            canvas.drawText(timeText, mLinePadding * (8 * i + 1.5f) - timeTextWidth / 2,
                    WeatherTodayView.TIME_LABEL_MARGIN, mPaint);

            String dayText = mDayTimeList.get(i);
            float[] dayWidths = new float[dayText.length()];
            mPaint.getTextWidths(dayText, dayWidths);
            float dayTextWidth = 0;
            for (float width : dayWidths) {
                dayTextWidth += width;
            }
            canvas.drawText(dayText, mLinePadding * (8 * i + 1.5f) - dayTextWidth / 2, mTextMargin /
                    2 + WeatherTodayView.TIME_LABEL_TOP_MARGIN, mPaint);
        }
        canvas.restore();
    }

    private void drawRainBlock(Canvas canvas) {
        canvas.save();
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(76);
        mPaint.setStrokeWidth(DASHED_LINE_WIDTH);
        PathEffect effect = new DashPathEffect(new float[]{16, 8, 16, 8}, 8);
        mPaint.setPathEffect(effect);
        for (int k = 0; k < mRainPositions.size(); k++) {
            RainBlock rainBlock = mRainBlocks.get(k);
            canvas.restore();
            if (k != (mRainBlocks.size() - 1)) {
                canvas.save();
            }
            if (rainBlock.rainStart != -1 && rainBlock.rainEnd != -1) {
                // clip rain region
                float iconPadding = mIconDrawableHeight + WeatherTodayView.LINE_ICON_DISTANCE +
                        WeatherTodayView.LINE_UP_CHART_HEIGHT;
                canvas.translate(0, iconPadding);
                float lastX = mLinePadding * rainBlock.rainStart;
                float lastY = (mMaxTemp - mHours[rainBlock.rainStart].getTemperature()) *
                        mHeightPerDegree;
                mPath.reset();
                mPath.moveTo(lastX, lastY);
                for (int i = rainBlock.rainStart; i < rainBlock.rainEnd; i++) {
                    Hour hour = mHours[i];
                    if (hour != null) {
                        float currentHourPadding = (mMaxTemp - hour.getTemperature()) * mHeightPerDegree;
                        mPath.quadTo(lastX, lastY, mLinePadding * i, currentHourPadding);
                        lastX = mLinePadding * i;
                        lastY = currentHourPadding;
                    }
                }
                mPath.lineTo(lastX, mHeight - mTextMargin - iconPadding);
                mPath.lineTo(mLinePadding * rainBlock.rainStart, mHeight - mTextMargin - iconPadding);
                mPath.close();
                canvas.clipPath(mPath);
                canvas.drawColor(getResources().getColor(R.color.white_10));

                // draw left and right border dashed lines
                mPath.reset();
                mPath.moveTo(mLinePadding * rainBlock.rainStart + 1, 0);
                mPath.lineTo(mLinePadding * rainBlock.rainStart + 1, mHeight - mTextMargin -
                        iconPadding);
                canvas.drawPath(mPath, mPaint);

                mPath.reset();
                mPath.moveTo(mLinePadding * (rainBlock.rainEnd - 1) - DASHED_LINE_WIDTH, 0);
                mPath.lineTo(mLinePadding * (rainBlock.rainEnd - 1) - DASHED_LINE_WIDTH, mHeight
                        - mTextMargin - iconPadding);
                canvas.drawPath(mPath, mPaint);

                Random random = new Random();
                canvas.translate(mLinePadding * rainBlock.rainStart, 0);
                for (RainPosition rainPosition : mRainPositions.get(k)) {
                    canvas.translate(rainPosition.getStartX(), rainPosition.getStartY());
                    if (random.nextBoolean()) {
                        canvas.drawBitmap(mRain1Bitmap, 0, 0, null);
                    } else {
                        canvas.drawBitmap(mRain2Bitmap, 0, 0, null);
                    }
                    canvas.translate(-rainPosition.getStartX(), -rainPosition.getStartY());
                }
                canvas.translate(-mLinePadding * rainBlock.rainStart, 0);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mLinePadding = mWidth / 27f;
        mHeight = WeatherTodayView.BACKGROUND_HEIGHT + mIconDrawableHeight + WeatherTodayView
                .LINE_ICON_DISTANCE + WeatherTodayView.LINE_UP_CHART_HEIGHT + WeatherTodayView
                .TIME_LABEL_TEXT_SIZE * 2 + WeatherTodayView.TIME_LABEL_MARGIN * 2;
        setMeasuredDimension(mWidth, mHeight);
    }

    private RainBlock generateRainBlock() {
        RainBlock rainBlock;
        if (mRainBlocks.size() > 0) {
            rainBlock = mRainBlocks.get(mRainBlocks.size() - 1);
            if (rainBlock.rainEnd != -1) {
                rainBlock = new RainBlock();
                mRainBlocks.add(rainBlock);
            }
        } else {
            rainBlock = new RainBlock();
            mRainBlocks.add(rainBlock);
        }
        return rainBlock;
    }

    public class RainBlock {
        int rainStart = -1;
        int rainEnd = -1;
    }

    public class RainPosition {
        private Random mRandom = new Random();

        private int mStartX;
        private int mStartY;

        private int mDeltaX;
        private int mDeltaY;

        private int mMaxX;
        private int mMaxY;


        public RainPosition(float maxX, float maxY, int deltaX, int deltaY) {
            mMaxX = (int) (maxX + 0.5f);
            mMaxY = (int) (maxY + 0.5f);
            mDeltaX = deltaX;
            mDeltaY = deltaY;
            if (maxX > 0 && maxY > 0) {
                initRandom();
            }
        }

        public void initRandom() {
            mStartX = mRandom.nextInt(mMaxX);
            mStartY = mRandom.nextInt(mMaxY);
        }

        public void resetRandom() {
            if (mRandom.nextBoolean() && mMaxX > 0) {
                mStartY = 0;
                mStartX = mRandom.nextInt(mMaxX);
            } else if(mMaxY > 0){
                mStartX = mMaxX;
                mStartY = mRandom.nextInt(mMaxY);
            }
        }

        public void move() {
            mStartX += mDeltaX;
            mStartY += mDeltaY;
            isOutOfBounds();
        }

        public boolean isOutOfBounds() {
            if (mStartY >= mMaxY || mStartX <= 0) {
                resetRandom();
                return true;
            }
            return false;
        }

        public int getStartX() {
            return mStartX;
        }

        public int getStartY() {
            return mStartY;
        }
    }

    protected void animLogic() {
        if (mRainPositions != null && mRainPositions.size() > 0)
            for (int i = 0; i < mRainPositions.size(); i++) {
                if (mRainPositions.get(i) != null)
                    for (RainPosition rainPosition : mRainPositions.get(i)) {
                        rainPosition.move();
                    }
            }
    }

    class AnimThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (mIsAnimationRunning) {
                    animLogic();
                    postInvalidate();
                }
                try {
                    Thread.sleep(ANIMATION_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startAnimation() {
        mIsAnimationRunning = true;
    }

    public void stopAnimation() {
        mIsAnimationRunning = false;
    }

    public void closeAndRecycle() {
        if (mBackgroundBitmap != null) {
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }
        if (mRain1Bitmap != null) {
            mRain1Bitmap.recycle();
            mRain1Bitmap = null;
        }
        if (mRain2Bitmap != null) {
            mRain2Bitmap.recycle();
            mRain2Bitmap = null;
        }
        if (mAnimThread != null){
            mAnimThread.interrupt();
            mAnimThread = null;
        }
    }

    public void recycleBackgroundBitmap() {
        if (mBackgroundBitmap != null) {
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }
    }

}
