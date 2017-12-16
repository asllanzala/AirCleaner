package com.honeywell.hch.airtouchv3.app.airtouch.controller.manager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.BackgroundBitmap;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.BlurImageUtil;

import java.util.List;

/**
 * Created by wuyuan on 8/30/15.
 * use to manage the background logic
 */
public class DynamicGroundManager {
    private static final float FRONT_ALPHA = 1f;

    private static final float BACK_ALPHA = 0f;

    private boolean isBeginChangeBg = false;

    private BackgroundBitmap mFrontBackground = null;
    private BackgroundBitmap mNextBackground = null;

    private Context mContext;

    private UserLocationData mUserLocation;

    private int mBlurRadio = BlurImageUtil.MAIN_ACTVITIY_BLUR_RADIO;

    public DynamicGroundManager(Context context) {
        mContext = context;
    }

    public DynamicGroundManager(Context context, UserLocationData userLocationData) {
        mContext = context;
        mUserLocation = userLocationData;
    }

    private static Object object = new Object();

    public boolean isBeginChangeBg() {
        return isBeginChangeBg;
    }

    public void setIsBeginChangeBg(boolean isBeginChangeBg) {
        this.isBeginChangeBg = isBeginChangeBg;
    }

    private int mFrontBackgroundIndex = 0;

    private Object mLockObject = new Object();

    /**
     * init the background resource
     *
     * @param resourceId
     */
    public void initBackgroundResouce(int resourceId,int radio) {
        mBlurRadio = radio;
        if (mUserLocation == null || mUserLocation.getCityBackgroundDta() == null
                || mUserLocation.getCityBackgroundDta().getmCityBackgroundObjectList().size() == 0) {
            setDefaultFrontBackground(resourceId);
        } else {
            mFrontBackground = mUserLocation.getCityBackgroundDta().getFrontBackground();
        }
        mFrontBackground.setAlpha(FRONT_ALPHA);
        if (mFrontBackground.getBlurBitmap() == null) {
            blurBackground(mFrontBackground, mBlurRadio);
        }

    }

    private void setDefaultFrontBackground(int resourceId){
        mFrontBackground = new BackgroundBitmap(resourceId, mContext, true);
        blurDefaultBackground(mFrontBackground,mBlurRadio);
    }



    public void switchBackGround(Canvas canvas, RectF dst) {
        synchronized (mLockObject) {
            try {
                if (mUserLocation != null && isBeginChangeBg && mUserLocation.getCityBackgroundDta().getmCityBackgroundObjectList().size() > 1) {
                    changeAlphaVaule(canvas, dst);
                } else {
                    drawFrontBlurBitmap2(canvas, dst);
                }
            } catch (Exception e) {
              Log.e("DownLoad","switchBackGround exception =" + e.toString());

            }


        }

    }


    private void changeAlphaVaule(Canvas canvas, RectF dst) {

        Paint paint = new Paint();
        if (mNextBackground != null && mFrontBackground != null) {

            canvas.save();

            drawDifferentAlpha(mNextBackground, paint, canvas, dst);
            drawDifferentAlpha(mFrontBackground, paint, canvas, dst);

            canvas.restore();

            canvas.drawBitmap(BitmapUtil.mGlassBitmap, null, dst, null);

            if (mFrontBackground.getAlpha() <= 0f || mFrontBackground.getAlpha() >= 1f) {
                mNextBackground.setIsFront(true);
                isBeginChangeBg = false;
                if (mUserLocation != null){
                    mUserLocation.getCityBackgroundDta().resetFrontIndex(false);

                }
            }
        }

    }

    private void drawDifferentAlpha(BackgroundBitmap backgroundBitmap, Paint paint, Canvas canvas, RectF dst) {
        backgroundBitmap.changeGroundAlpha(0.01f);
        paint.setAlpha((int) (backgroundBitmap.getAlpha() * 255));

        canvas.drawBitmap(backgroundBitmap.getBlurBitmap(), null, dst, paint);

    }

    public void releaseDrawedBackground(boolean isNeedRecycleCurrent) {
        if (mUserLocation != null && mUserLocation.getCityBackgroundDta() != null){
            mUserLocation.getCityBackgroundDta().resetFrontIndex(isNeedRecycleCurrent);
        }
    }

    public void getChangeBackground() {
        if (mUserLocation != null){
            List<BackgroundBitmap> backgroundBitmapArrayList = mUserLocation.getCityBackgroundDta().getmCityBackgroundObjectList();
            if (backgroundBitmapArrayList.size() > 1) {
                mFrontBackground = mUserLocation.getCityBackgroundDta().getFrontBackground();
                mFrontBackground.setAlpha(FRONT_ALPHA);
                if (mFrontBackground.getBlurBitmap() == null) {
                    blurBackground(mFrontBackground, mBlurRadio);
                }
                mNextBackground = mUserLocation.getCityBackgroundDta().getNextBackground();
                if (mNextBackground.getBlurBitmap() == null) {
                    blurBackground(mNextBackground, mBlurRadio);
                }
            }
        }

    }


    /**
     * blur the background
     *
     * @param radiaus
     */
    public void blurBackground(BackgroundBitmap backgroundBitmap, int radiaus) {
        backgroundBitmap.blurBackgroundBitmap(radiaus);

    }

    /**
     * blur the default background
     *
     * @param radiaus
     */
    public void blurDefaultBackground(BackgroundBitmap backgroundBitmap, int radiaus) {
        backgroundBitmap.setDefaultBlurBitmap(radiaus);

    }

    /**
     * use to draw the static background bitmap
     *
     * @param canvas
     * @param dst
     */
    public void drawFrontBlurBitmap2(Canvas canvas, RectF dst) {
        initBackgroundResouce(R.raw.default_city_day_blur1, mBlurRadio);
        Paint paint = new Paint();
        paint.setAlpha((int) (mFrontBackground.getAlpha() * 255));
        canvas.drawBitmap(mFrontBackground.getBlurBitmap(), null, dst, paint);
        canvas.drawBitmap(BitmapUtil.mGlassBitmap, null, dst, null);
        setIsBeginChangeBg(false);
    }

    public void setBlurRadio(int blurRadio){
       mBlurRadio = blurRadio;
    }

}
