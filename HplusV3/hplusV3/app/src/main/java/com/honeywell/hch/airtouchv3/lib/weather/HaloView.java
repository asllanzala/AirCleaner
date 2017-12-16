package com.honeywell.hch.airtouchv3.lib.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

/**
 * Created by wuyuan on 15/5/24.
 * haze animation surfaceView
 */
public class HaloView extends View
{

    private Thread hazeDrawTread;

    private boolean isRunning = false;

    private int windowsHeight;

    private int windowsWidth;

    private Bitmap mHaloImageHigh;

    private Bitmap mHaloImage;

    private Context mContext;


    private int clipHeigh;

    private int haloFirstPostionY;
    private int haloHeightFirstPostionY;
    private int degree;
    private float scale = 1.1f;
    private int alpha = 0;
    private int maxDegree = 25;

    /**
     * HazeSurfaceView construct
     * @param ctx Context
     */
    public HaloView(Context ctx)
    {
        this(ctx, null);
    }

    /**
     * HazeSurfaceView construct
     * @param ctx  Context
     * @param attrs AttributeSet
     */
    public HaloView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);

        mContext = ctx;
        windowsHeight = DensityUtil.getScreenHeight();
        windowsWidth = DensityUtil.getScreenWidth();
        initView();
        clipHeigh = windowsHeight;

        mHaloImageHigh = BitmapUtil.createBitmapEffectly(mContext, R.drawable.halo_high2);
        mHaloImage = BitmapUtil.createBitmapEffectly(mContext, R.drawable.halo);

        haloFirstPostionY = mHaloImage.getHeight();
//        haloHeightFirstPostionY = -mHaloImageHigh.getHeight();

        HaloThread haloThread =  new HaloThread();
        haloThread.start();

        if (windowsHeight > 1280){
            maxDegree = 20;
        }
    }

    private void initView()
    {

    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        Matrix matrix = new Matrix();
        matrix.postTranslate(windowsWidth - (int) (1.5 * mHaloImageHigh.getWidth()),
                -mHaloImageHigh.getHeight() / 6);
        matrix.postRotate(degree, windowsWidth - scale * mHaloImageHigh.getWidth() / 2, scale *
                mHaloImageHigh.getHeight());
        matrix.postScale(scale, scale);
        paint.setAlpha(alpha);
        canvas.drawBitmap(mHaloImageHigh, matrix, paint);

        Paint paint2 = new Paint();
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        Matrix matrix2 = new Matrix();
        matrix2.postTranslate(0, haloFirstPostionY);
        canvas.drawBitmap(mHaloImage, matrix2, paint2);

        if(degree >= maxDegree){
            isRunning = false;
        }
    }



    public void destroyView()
    {
         if (mHaloImageHigh != null)
         {
             mHaloImageHigh.recycle();
             mHaloImageHigh = null;
         }
    }

    private class HaloThread extends Thread {
        public HaloThread() {
            isRunning = true;
        }

        @Override
        public void run()
        {
            while(isRunning)
            {
                degree += 1;
                scale += 0.01;
                alpha += 10;
                haloFirstPostionY -= mHaloImage.getHeight()/25;
                if (alpha >= 255){
                    alpha = 255;
                }
                postInvalidate();
                try
                {
                    sleep(20);
                }
                catch (Exception e){

                }
            }
        }
    }


    public Bitmap getBitmap(){
        return mHaloImage;
    }

}
