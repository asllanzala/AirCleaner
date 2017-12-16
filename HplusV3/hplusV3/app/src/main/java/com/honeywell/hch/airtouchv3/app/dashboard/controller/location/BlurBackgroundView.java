package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.honeywell.hch.airtouchv3.app.airtouch.controller.manager.DynamicGroundManager;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;

/**
 * Created by wuyuan on 8/14/15.
 * background view
 */
public class BlurBackgroundView extends View {

    private Thread mBackgroundThread;
    private Context mContext;

    private int clipHeight = 0;

    private int mRadiaus = 0;

    private DynamicGroundManager dynamicGroundManager;


    private BaseLocationFragment backgroundFragment;


    private BackgroundThread mAlphaTimer;

    /**
     * start timer for background switch
     */
    public void startTimer(int radio) {
        if (mAlphaTimer == null || !mAlphaTimer.mIsRunning) {
            dynamicGroundManager.setBlurRadio(radio);
            mAlphaTimer = new BackgroundThread();
            mAlphaTimer.start();
        }
    }


    public BlurBackgroundView(Context context) {
        super(context);
        initBackgroundView(context);
    }

    public BlurBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBackgroundView(context);
    }


    public BlurBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBackgroundView(context);
    }

    public void setFragment(BaseLocationFragment fragment) {
        backgroundFragment = fragment;
    }

    public void initDynmac(UserLocationData userLocationData) {
        dynamicGroundManager = new DynamicGroundManager(mContext, userLocationData);
    }

    public void destroyed() {
        stopSwitchBackground(true);
        mBackgroundThread = null;
        mRadiaus = 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        RectF dst = new RectF(0, 0, getScreenWidth(), getScreenHeight());

        dynamicGroundManager.switchBackGround(canvas, dst);

    }


    public void setClipHeight(int height) {
        clipHeight = height;
        invalidate();
    }


    private void initBackgroundView(Context context) {
        mContext = context;

        clipHeight = getScreenHeight();
//        startTimer();
    }

    public void initBackgroundResouce(int resourceId, int radio) {
        dynamicGroundManager.initBackgroundResouce(resourceId, radio);
    }


    private int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        @SuppressWarnings("deprecation")
        int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
        return width;
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        @SuppressWarnings("deprecation")
        int height = wm.getDefaultDisplay().getHeight();// 屏幕高度
        return height;
    }


    private class BackgroundThread extends Thread {

        private boolean mIsRunning = true;

        public BackgroundThread() {
            mIsRunning = true;
        }

        public boolean isRunning() {
            return mIsRunning;
        }

        public void setRunning(boolean isRunning) {
            mIsRunning = isRunning;
        }

        @Override
        public void run() {
            try {
                sleep(10000);
            } catch (Exception e) {

            }

            while (mIsRunning) {
                if (!dynamicGroundManager.isBeginChangeBg()) {
                    dynamicGroundManager.getChangeBackground();
                    dynamicGroundManager.setIsBeginChangeBg(true);
                    while (dynamicGroundManager.isBeginChangeBg()) {
                        postInvalidate();
                        try {
                            sleep(20);
                        } catch (Exception e) {

                        }
                    }
                }

                try {
                    sleep(10000);
                } catch (Exception e) {

                }
            }
            mIsRunning = false;
        }
    }


    public void stopSwitchBackground(boolean isNeedRecycleCurrent) {
        if (mAlphaTimer != null) {
            mAlphaTimer.mIsRunning = false;
            mAlphaTimer = null;
        }
        dynamicGroundManager.setIsBeginChangeBg(false);
        dynamicGroundManager.releaseDrawedBackground(isNeedRecycleCurrent);

    }

}
