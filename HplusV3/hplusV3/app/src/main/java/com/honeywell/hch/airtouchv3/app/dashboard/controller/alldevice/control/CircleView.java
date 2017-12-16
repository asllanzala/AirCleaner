package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.control;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Draw group control Circle
 */
public class CircleView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = CircleView.class.getSimpleName();

    public static final String BROADCAST_ACTION_GROUP_CONTROL = "BROADCAST_ACTION_GROUP_CONTROL";

    public static final String BROADCAST_ACTION_SUCCEED_DEVICESTATUSVIEW = "BROADCAST_ACTION_SUCCEED_DEVICESTATUSVIEW";

    public static final String BROADCAST_ACTION_STOP_FLASHINGTASK = "BROADCAST_ACTION_STOP_FLASHINGTASK";


    public static final String BROADCAST_INTENT_EXTRA_KEY_SUCCEED_DEVICESTATUSVIEW = "succeeddevice";

    public static final String BROADCAST_INTENT_EXTRA_KEY_GROUP_CONTROL_COMMAND = "";

    public static final int MODE_NAME_TEXT_SIZE_FOR_SCREEN_WIDTH_640_1136 = 24;

    public static final int MODE_TIME_TEXT_SIZE_FOR_SCREEN_WIDTH_640_1136 = 18;

    private int mCircleWidth = 100;

    private int mCenterX = 0;

    private int mCenterY = 0;

    private int mExternalCircleRadius = 0;

    private float mInnerCircleRadius = 0;

    public int mTempColorAlpha = 255; // Default not transparent.

    private boolean mIsTempColorAlphaIncressing = false; // Default not transparent.

    private int mModeNameTextColorSelected;

    private int mModeNameTextColorUnSelected;

    private int mTimeTextColor;

    private int mModeNameTextSize = MODE_NAME_TEXT_SIZE_FOR_SCREEN_WIDTH_640_1136;
    // For screen width 640 * 1136 px

    private int mTimeTextSize = MODE_TIME_TEXT_SIZE_FOR_SCREEN_WIDTH_640_1136;
    // For screen width 640 * 1136 px

    public Map<Integer, DeviceMode> mDeviceModeMap = new HashMap();

    private int mPreviousGroupRunningMode = DeviceMode.MODE_UNDEFINE;

    private int mCurrentGroupRunningMode = DeviceMode.MODE_UNDEFINE;

    private int mClickedGroupRunningMode = DeviceMode.MODE_UNDEFINE;

    private FlashAsyncTask mFlashAsyncTask;

    private boolean mIsPaintThreadRunning = false;

    private PaintThread mPaintThread;

    // surface holder for double buffering
    private SurfaceHolder mSurfaceHolder;

    private boolean mIsPainting = false;

    private static final String PAINT_THREAD_NAME = "PAINT_THREAD_NAME";

    private Long lastTimeUserInput = 0L;
    private boolean isUserInputTooQuick = false;
    static final int USER_INPUT_TOO_QUICK = 1 * 1000;
    private int tooQuickCount;


    private BlockingQueue<Object> mSyncCallsQueue = new LinkedBlockingDeque<Object>();

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public CircleView(Context context) {
        super(context);
        initData();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData();
    }

    public void initData() {
        DeviceMode deviceModeSleep = new DeviceMode(DeviceMode.MODE_SLEEP,
                DeviceMode.ANGLE_DEVIDE_255, DeviceMode.ANGLE_DEVIDE_15,
                DeviceMode.COLOR_SLEEP_NORMAL, DeviceMode.COLOR_SLEEP_CLICKED);

        DeviceMode deviceModeAway = new DeviceMode(DeviceMode.MODE_AWAY,
                DeviceMode.ANGLE_DEVIDE_15, DeviceMode.ANGLE_DEVIDE_180,
                DeviceMode.COLOR_AWAY_NORMAL, DeviceMode.COLOR_AWAY_CLICKED);

        DeviceMode deviceModeHome = new DeviceMode(DeviceMode.MODE_HOME,
                DeviceMode.ANGLE_DEVIDE_180, DeviceMode.ANGLE_DEVIDE_255,
                DeviceMode.COLOR_HOME_NORMAL, DeviceMode.COLOR_HOME_CLICKED);

        mDeviceModeMap.put(DeviceMode.MODE_HOME, deviceModeHome);
        mDeviceModeMap.put(DeviceMode.MODE_AWAY, deviceModeAway);
        mDeviceModeMap.put(DeviceMode.MODE_SLEEP, deviceModeSleep);

        mCurrentGroupRunningMode = DeviceMode.MODE_UNDEFINE;

        mModeNameTextColorSelected = getResources().getColor(R.color.white_80);
        mModeNameTextColorUnSelected = getResources().getColor(R.color.white_50);
        mTimeTextColor = getResources().getColor(R.color.white_30);

        int screenWidth = DensityUtil.getScreenWidth();
        mCenterX = screenWidth / 2;
        // 2.13 is the percent of the screenWidth/padding to top
        mExternalCircleRadius = (int)(screenWidth / 3.5);
        mCenterY = mExternalCircleRadius + (int) (screenWidth / 2.13);
        // 12.8 means the percent of the screenWidth/mCircleWidth
        mCircleWidth = (int) (screenWidth / 12.8);
        mInnerCircleRadius = mExternalCircleRadius - mCircleWidth;

        // 640 means the design UI
        mModeNameTextSize = MODE_NAME_TEXT_SIZE_FOR_SCREEN_WIDTH_640_1136 * screenWidth / 640;
        mTimeTextSize = MODE_TIME_TEXT_SIZE_FOR_SCREEN_WIDTH_640_1136 * screenWidth / 640;

        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        mIsPaintThreadRunning = true;
        mPaintThread = new PaintThread(PAINT_THREAD_NAME);
        mPaintThread.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = measureWidth(widthMeasureSpec);

        int height = mCenterX + mCenterY;
        setMeasuredDimension(measuredWidth, height);

        this.setOnTouchListener(onTouchListener);
    }

    /**
     * 测量宽度
     */
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == MeasureSpec.AT_MOST) {
            result = getWidth();
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == MeasureSpec.AT_MOST) {

            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mIsPainting) {
            mIsPainting = true;
            clearCanvas(canvas);
            drawCircle(canvas);
            drawCircleTime(canvas);
            drawOnOffIcon(canvas);
            mIsPainting = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void startDraw() {
        try {
            mSyncCallsQueue.put(new Object());
        } catch (InterruptedException e) {
            LogUtil.log(LogUtil.LogLevel.DEBUG, TAG,
                    "Exception in put sync object, " + e.getMessage());
        }
    }

    public void stopDraw() {
        mIsPaintThreadRunning = false;
        if (mPaintThread == null) {
            return;
        }
        mPaintThread.interrupt();
    }

    private void clearCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    private RectF getCircleRectF() {
        // Set rect to draw the circle
        int left = mCenterX - mExternalCircleRadius;
        int top = mCenterY - mExternalCircleRadius;
        int right = mCenterX + mExternalCircleRadius;
        int bottom = mCenterY + mExternalCircleRadius;
        return new RectF(left, top, right, bottom);
    }

    private void drawCircle(Canvas canvas) {
        RectF rectF = getCircleRectF();

        // Circle textPaint
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(mCircleWidth);

        // Text textPaint
        Paint textPaint = new Paint();
        textPaint.setTextSize(mModeNameTextSize);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // draw all circles.
        for (DeviceMode deviceMode : mDeviceModeMap.values()) {
            // select the color depend on device status
            if (deviceMode.getModeType() == mCurrentGroupRunningMode) {
                int selectedColor = deviceMode.getColorClicked();
                if (mFlashAsyncTask != null && mFlashAsyncTask.isFlashRunning()) {
                    int red = Color.red(selectedColor);
                    int green = Color.green(selectedColor);
                    int blue = Color.blue(selectedColor);
                    int newArcColor = Color.argb(mTempColorAlpha, red, green, blue);
                    circlePaint.setColor(newArcColor);
                } else {
                    circlePaint.setColor(selectedColor);
                }
                textPaint.setColor(mModeNameTextColorSelected);
//                setLayerType(LAYER_TYPE_SOFTWARE, null);
                /*
                 * radius:阴影的倾斜度
                 * dx:水平位移
                 * dy:垂直位移
                 */
//                circlePaint.setShadowLayer(30, 5, 2, Color.DKGRAY);

            } else {
                circlePaint.clearShadowLayer();
                circlePaint.setColor(deviceMode.getColorNormal());
                textPaint.setColor(mModeNameTextColorUnSelected);
            }
            canvas.drawArc(rectF, deviceMode.getStartAngle(), deviceMode.getSweepAngle(),
                    false, circlePaint);
        }

        // In order to draw the text at front of circle, draw it after circle. If no, the text color is not clear.
        canvas.save();
        canvas.rotate(-50, mCenterX, mCenterY);
        int x = mCenterX - mCircleWidth / 3;
        int y = mCenterY - mExternalCircleRadius + mCircleWidth / 3 - mModeNameTextSize / 3;
        String homeMode = getResources().getString(R.string.group_control_device_mode_home);
        canvas.drawText(homeMode, x, y, textPaint);
        canvas.restore();
        canvas.save();

        String awayMode = getResources().getString(R.string.group_control_device_mode_away);
        canvas.drawText(awayMode, mCenterX,
                mCenterY + mInnerCircleRadius + mCircleWidth + mModeNameTextSize / 3,
                textPaint);

        canvas.save();
        canvas.rotate(45, mCenterX, mCenterY);
        x = mCenterX + mCircleWidth / 3 - mModeNameTextSize / 3;
        y = mCenterY - mExternalCircleRadius + mCircleWidth / 3 - mModeNameTextSize / 3;
        String sleepMode = getResources().getString(R.string.group_control_device_mode_sleep);
        canvas.drawText(sleepMode, x, y, textPaint);
        canvas.restore();
        canvas.save();
    }

    private void drawCircleTime(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setTextSize(mTimeTextSize);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(mTimeTextColor);

        // The time will not been draw at this version, draw it at next version. The code can be keep here.
//        String time7 = "07:00";
//        String time18 = "18:00";
//        String time23 = "23:00";
//        canvas.drawText(time7, mCenterX + mExternalCircleRadius + getTextWidth(textPaint, time7),
//                mCenterY
//                        + mCenterX / 10, textPaint);
//        canvas.drawText(time18, mCenterX - mExternalCircleRadius - getTextWidth(textPaint, time18),
//                mCenterX, textPaint);
//        canvas.drawText(time23, mCenterY - mCenterX / 10, mCenterX - mExternalCircleRadius,
//                textPaint);

        String time12 = HPlusApplication.getInstance().getString(R.string.group_control_time_12_00);
        String time24 = HPlusApplication.getInstance().getString(R.string.group_control_time_24_00);
        // 12:00
        canvas.drawText(time12, mCenterX, mCenterY + mInnerCircleRadius, textPaint);
        // 24:00
        canvas.drawText(time24, mCenterX, mCenterY - mInnerCircleRadius + getTextHeight(textPaint,
                time24), textPaint);

        // The the white line for the time
        textPaint.setStrokeWidth(5);
        textPaint.setTextAlign(Paint.Align.LEFT);
        // 06:00 -
        canvas.drawLine(mCenterX + mInnerCircleRadius - 2 * getTextHeight(textPaint, time24) / 3,
                mCenterY,
                mCenterX + mInnerCircleRadius, mCenterY,
                textPaint);
        // 12:00 -
        canvas.drawLine(mCenterX, mCenterY + mInnerCircleRadius + getTextHeight(textPaint, time12),
                mCenterX, mCenterY + mInnerCircleRadius + getTextHeight(textPaint, time12) / 2,
                textPaint);
        // 18:00 -
        canvas.drawLine(mCenterX - mInnerCircleRadius - 2 * getTextHeight(textPaint, time24) / 3,
                mCenterY,
                mCenterX - mInnerCircleRadius, mCenterY,
                textPaint);
        // 24:00 -
        canvas.drawLine(mCenterX, mCenterY - mInnerCircleRadius - getTextHeight(textPaint, time24),
                mCenterX, mCenterY - mInnerCircleRadius - 1 * getTextHeight(textPaint, time24) / 3,
                textPaint);
    }

    private int getTextWidth(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    private int getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    private void drawOnOffIcon(Canvas canvas) {
        Bitmap onOffButton;
        if (mCurrentGroupRunningMode == DeviceMode.MODE_HOME) {
            onOffButton = BitmapFactory.decodeResource(getResources(),
                    R.drawable.group_control_onoff_button_on);
        } else {
            onOffButton = BitmapFactory.decodeResource(getResources(),
                    R.drawable.group_control_onoff_button_off);
        }

        canvas.drawBitmap(onOffButton, mCenterX - onOffButton.getWidth() / 2,
                mCenterY - onOffButton.getHeight() / 2, null);
        onOffButton.recycle();
        onOffButton = null;
    }

    OnTouchListener onTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int clickedDeviceMode = checkClickedMode(event.getX(), event.getY());
                    mClickedGroupRunningMode = clickedDeviceMode;
                    if (clickedDeviceMode != DeviceMode.MODE_UNDEFINE
                            && clickedDeviceMode != mCurrentGroupRunningMode) {

                        mPreviousGroupRunningMode = mCurrentGroupRunningMode;
                        mCurrentGroupRunningMode = clickedDeviceMode;
                        startDraw();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (mClickedGroupRunningMode != DeviceMode.MODE_UNDEFINE
                            && mPreviousGroupRunningMode != mCurrentGroupRunningMode) {

                        CalculateUserInputGap();

                        if (!isUserInputTooQuick) {
                            // Stop the previous animation.
                            if (mFlashAsyncTask != null && mFlashAsyncTask.isFlashRunning()) {
                                mFlashAsyncTask.stopRefresh(mPreviousGroupRunningMode);
                            }

                            // Start a new thread to show the button flashing animation
                            mFlashAsyncTask = new FlashAsyncTask(mCurrentGroupRunningMode);
                            AsyncTaskExecutorUtil.executeAsyncTask(mFlashAsyncTask);

                            processGroupControlCommand(mCurrentGroupRunningMode);
                        } else {
                            isUserInputTooQuick = false;
                        }
                    }
                    break;

                default:
                    break;
            }
            return true;
        }

        /**
         * 检测方向
         *
         * @param x
         * @param y
         * @return
         */
        private int checkClickedMode(float x, float y) {
            int mode = DeviceMode.MODE_UNDEFINE;

            float realX = x - mCenterX;
            float realY = y - mCenterY;

            double distanceToCenter = Math.sqrt(Math.pow(realY, 2) + Math.pow(realX, 2));

            if (distanceToCenter < (mCenterX - mInnerCircleRadius) / 3) {
                // Switch ON OFF button click event to HOME or AWAY.
                // The point in the inner circle.
                if ((mCurrentGroupRunningMode == DeviceMode.MODE_AWAY)
                    || (mCurrentGroupRunningMode == DeviceMode.MODE_UNDEFINE)) {
                    // Home means turn on
                    mode = DeviceMode.MODE_HOME;
                } else if (mCurrentGroupRunningMode == DeviceMode.MODE_HOME
                        || mCurrentGroupRunningMode == DeviceMode.MODE_SLEEP) {
                    // Turn off device
                    mode = DeviceMode.MODE_AWAY;
                }
            } else if (distanceToCenter >= mInnerCircleRadius
                    && distanceToCenter <= mExternalCircleRadius + mCircleWidth) {
                // The point are in the traffic circle now.
                double angle = Math.atan2(realY, realX) * 180 / 3.14;

                if (angle < 0) {
                    angle = angle + 360;
                } else if (angle > 360) {
                    while (angle > 360) {
                        angle -= 360;
                    }
                }
                Log.v(TAG, "angle=" + angle);

                if (angle < DeviceMode.ANGLE_DEVIDE_15) {
                    mode = DeviceMode.MODE_SLEEP;
                } else if (angle < DeviceMode.ANGLE_DEVIDE_180) {
                    mode = DeviceMode.MODE_AWAY;
                } else if (angle < DeviceMode.ANGLE_DEVIDE_255) {
                    mode = DeviceMode.MODE_HOME;
                } else {
                    mode = DeviceMode.MODE_SLEEP;
                }

            } else {
                // out of circle, do nothing
                mode = DeviceMode.MODE_UNDEFINE;
            }

            return mode;
        }

    };

    public int getCurrentGroupRunningMode() {
        return mCurrentGroupRunningMode;
    }

    public int getPreviousGroupRunningMode() {
        return mPreviousGroupRunningMode;
    }

    /**
     * Process group control command.
     * 1. process command
     * 2. send broadcast command to send the command to cloud.
     */
    public void processGroupControlCommand(int groupControlCommand) {
        Intent intent = new Intent(BROADCAST_ACTION_GROUP_CONTROL);
        intent.putExtra(BROADCAST_INTENT_EXTRA_KEY_GROUP_CONTROL_COMMAND, groupControlCommand);
        HPlusApplication.getInstance().sendBroadcast(intent);

        mCurrentGroupRunningMode = groupControlCommand;
    }

    public int getCenterY() {
        return mCenterY;
    }

    public int getCenterX() {
        return mCenterX;
    }

    public void setCurrentMode(int deviceMode) {
        mCurrentGroupRunningMode = deviceMode;
        startDraw();
    }

    public void stopFlashingTask(int groupMode) {
        if (mFlashAsyncTask != null && mFlashAsyncTask.isFlashRunning()) {
            mFlashAsyncTask.stopRefresh(groupMode);
        }
    }

    public void startFlashingTask() {
        if (mFlashAsyncTask == null) {
            mFlashAsyncTask = new FlashAsyncTask(mCurrentGroupRunningMode);
            AsyncTaskExecutorUtil.executeAsyncTask(mFlashAsyncTask);
        }
    }

    private class PaintThread extends Thread {

        PaintThread(String name) {
            super(name);
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            while (mIsPaintThreadRunning) {
                super.run();
                try {
                    mSyncCallsQueue.take();
                    Canvas canvas = null;
                    try {
                        canvas = mSurfaceHolder.lockCanvas();

                        synchronized (mSurfaceHolder) {
                            if (canvas != null) {
                                onDraw(canvas);
                            }
                        }
                    } finally {
                        if (canvas != null) {
                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

    }

    public class FlashAsyncTask extends AsyncTask<Object, Integer, String> {

        private boolean mIsFlashRunning = false;

        private int mFlashingGroupMode;

        public FlashAsyncTask(int flashingGroupMode) {
            mIsFlashRunning = true;
            mFlashingGroupMode = flashingGroupMode;
            if (mDeviceModeMap.get(flashingGroupMode) != null)
                mTempColorAlpha = Color.red(mDeviceModeMap.get(flashingGroupMode).getColorClicked());
        }

        public void stopRefresh(int flashingGroupMode) {
            if (flashingGroupMode == mFlashingGroupMode) {
                mIsFlashRunning = false;
                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "stopRefresh thread, mIsFlashRunning = false");
            }
        }

        public boolean isFlashRunning() {
            return mIsFlashRunning;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... params) {
            float diff = 255 / 60;
            while (mIsFlashRunning) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int times = (int) (mTempColorAlpha / diff);

                if (mTempColorAlpha <= 76) {
                    mIsTempColorAlphaIncressing = true;
                    mTempColorAlpha = (int) (76 + diff);
                } else if (mTempColorAlpha >= 255) {
                    mIsTempColorAlphaIncressing = false;
                    mTempColorAlpha = (int) (255 - diff);
                } else {
                    if (mIsTempColorAlphaIncressing) {
                        mTempColorAlpha = (int) diff * (times + 1);
                    } else {
                        mTempColorAlpha = (int) diff * (times - 1);
                    }
                }

                mTempColorAlpha = mTempColorAlpha > 255 ? 255 : mTempColorAlpha;

                publishProgress(mTempColorAlpha);
            }

            return "Success";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mTempColorAlpha = progress[0];
            // re-draw
            startDraw();
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mIsFlashRunning = false;
            mIsTempColorAlphaIncressing = false;
            startDraw();
            super.onPostExecute(result);
        }

    }

    /**
     * If user input is too quick,
     */
    private void CalculateUserInputGap() {
        long timeDeviation = System.currentTimeMillis() - lastTimeUserInput;

        if (timeDeviation < USER_INPUT_TOO_QUICK) {
            tooQuickCount++;
            if (tooQuickCount >= 2) {
                tooQuickCount = 0;
                isUserInputTooQuick = true;
            }
        }

        lastTimeUserInput = System.currentTimeMillis();
    }
}