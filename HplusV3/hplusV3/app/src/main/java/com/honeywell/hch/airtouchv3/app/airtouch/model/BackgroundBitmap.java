package com.honeywell.hch.airtouchv3.app.airtouch.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.WindowManager;

import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.BlurImageUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

/**
 * Created by wuyuan on 8/30/15.
 * the model of background
 */
public class BackgroundBitmap {
    private int mBackgroundResourceId;

    private String  mBackgroundPath;

    private String mBlurBackgroundPath;

    private Bitmap mBackgroundBitmap;

    private Bitmap mBlurBitmap;

    private Context mContext;

    private float mAlpha = 1f;

    private float mBlurAlpha = 1f;

    private boolean mIsFront = false;

    /**
     * construct method
     * @param backgroundResourceId
     * @param context
     * @param isFront
     */
    public BackgroundBitmap(int backgroundResourceId, Context context, boolean isFront) {
        this.mBackgroundResourceId = backgroundResourceId;
        mContext = context;

        this.mIsFront = isFront;
        mAlpha = mIsFront ? 1 : 0;
    }


    /**
     * construct method
     * @param path
     * @param context
     * @param isFront
     */
    public BackgroundBitmap(String path, Context context,boolean isFront,String blurPath) {
        this.mBackgroundPath = path;
        mBlurBackgroundPath = blurPath;
        mContext = context;

        this.mIsFront = isFront;
        mAlpha = mIsFront ? 1 : 0;
    }


    public void blurBackgroundBitmap(int radious) {

        if (radious != BlurImageUtil.MAIN_ACTVITIY_BLUR_RADIO){
             blurSDcardBackground(radious);
        }
        else{
            mBlurBitmap = BitmapUtil.createBitmapEffectlyFromPath(mContext, mBlurBackgroundPath);

            if (mBlurBitmap == null){
                blurSDcardBackground(radious);
            }
        }

    }

    private void blurSDcardBackground(int radion){
        if (!StringUtil.isEmpty(mBackgroundPath)){
            mBackgroundBitmap = BitmapUtil.createBitmapEffectlyFromPath(mContext, mBackgroundPath);
            if (mBackgroundBitmap == null){
                setDefaultBlurBitmap(radion);
            }
            else{
                mBlurBitmap = BlurImageUtil.fastblur(mContext, mBackgroundBitmap, radion);
                if (mBlurBitmap == null){
                    setDefaultBlurBitmap(radion);
                }
            }
        }


        if (mBackgroundBitmap != null) {
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }

    }

    public void setDefaultBlurBitmap(int radion){
        if (radion == BlurImageUtil.MAIN_ACTVITIY_BLUR_RADIO){
            mBlurBitmap = BlurImageUtil.getDefaultBitmap();
        }
        else{
            mBackgroundBitmap = BlurImageUtil.getDefaultBitmap();
            mBlurBitmap = BlurImageUtil.fastblur(mContext, mBackgroundBitmap, radion);
            if (mBlurBitmap == null){
                setDefaultBlurBitmap(radion);
            }
        }

    }


    public float getAlpha() {
        return mAlpha;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public float getmBlurAlpha() {
        return mBlurAlpha;
    }

    public void setmBlurAlpha(float mBlurAlpha) {
        this.mBlurAlpha = mBlurAlpha;
    }

    public boolean ismIsFront() {
        return mIsFront;
    }

    public void setIsFront(boolean isFront) {
        this.mIsFront = isFront;
    }


    public Bitmap getBackgroundBitmap() {
        return mBackgroundBitmap;
    }

    public void setmBackgroundBitmap(Bitmap mBackgroundBitmap) {
        this.mBackgroundBitmap = mBackgroundBitmap;
    }

    public Bitmap getBlurBitmap() {
        return  mBlurBitmap;
    }

    public void setmBlurBitmap(Bitmap mBlurBitmap) {
        this.mBlurBitmap = mBlurBitmap;
    }


    /**
     * recycleBackground resource when activity is destroyed
     */
    public void recycleBackgroundResource() {
        if (mBlurBitmap != null) {
            mBlurBitmap.recycle();
            mBlurBitmap = null;
        }
    }

    /**
     * change alpha of the backgound bitmap .use dynamic background swtich
     * @param deltaAlpha
     */
    public void changeGroundAlpha(float deltaAlpha) {
        if (mIsFront) {
            mAlpha -= deltaAlpha;
            if (mAlpha <= 0) {
                mAlpha = 0;
                mIsFront = false;
            }
        }
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

    public String getBackgroundPath() {
        return mBackgroundPath;
    }

    public void setBackgroundPath(String mBackgroundPath) {
        this.mBackgroundPath = mBackgroundPath;
    }
}
