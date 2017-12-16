package com.honeywell.hch.airtouchv3.framework.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

/**
 * Created by wuyuan on 15/5/24.
 * haze animation surfaceView
 */
public class HazeView extends View
{

    private Thread hazeDrawTread;

    private boolean isRunning;

    private int windowsHeight;

    private int windowsWidth;

    private Bitmap mHazeImage;

    private Context mContext;


    private int x = -10;

    private Paint mPaint;

    /**
     * current alpha,used to fade
     */
    private int currentAlpha = 0;

    private int lastAlpha = 0;

    /**
     *  alpha difference between current page alpha and last page alpha,
     *  used to fade
     */
    private int alphaDle = 0;

    /**
     * lock object.used to lock alphaDle,currentAlpha and lastAlpha
     */
    private Object lockObj = new Object();


    /**
     * HazeSurfaceView construct
     * @param ctx Context
     */
    public HazeView(Context ctx)
    {
        this(ctx, null);
    }

    /**
     * HazeSurfaceView construct
     * @param ctx  Context
     * @param attrs AttributeSet
     */
    public HazeView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);

        mContext = ctx;
        windowsHeight = DensityUtil.getScreenHeight();
        windowsWidth = DensityUtil.getScreenWidth();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mHazeImage == null){
            Bitmap temp = BitmapUtil.createBitmapEffectly(mContext, R.drawable.fog);
            mHazeImage = zoomBitmap(temp,1080);
            if (temp != null){
                temp.recycle();
                temp = null;
            }
        }
        //start haze moving animation
        drawMoving(canvas);

    }



    public void destroyView()
    {
         isRunning = false;
         if (mHazeImage != null)
         {
             mHazeImage.recycle();
             mHazeImage = null;
         }
    }


    private class HazeRunable implements  Runnable
    {
        @Override
        public void run()
        {
            while(isRunning)
            {

                postInvalidate();
                try
                {
                    Thread.sleep(100);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    private void drawMoving(Canvas mCanvas)
    {
        if (-x > windowsWidth)
        {
            x = x % windowsWidth;
        }

        if (mCanvas != null)
        {
            mCanvas.translate(x, 0);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setAlpha(lastAlpha);

            Rect rect = new Rect(x,0,-x + windowsWidth,windowsHeight);
            mCanvas.drawBitmap(mHazeImage,null,rect, mPaint);

        }

        //move speed
        x -= DensityUtil.dip2px(6);

        //fade amination effect
        synchronized (lockObj)
        {
            if ((alphaDle > 0 && lastAlpha < currentAlpha) ||
                    (alphaDle < 0 && lastAlpha > currentAlpha))
            {
                lastAlpha += alphaDle;
            }
            if ((alphaDle > 0 && lastAlpha >= currentAlpha) ||
                    (alphaDle < 0 && lastAlpha < currentAlpha))
            {
                lastAlpha = currentAlpha;
            }
        }


    }

    /**
     * set
     * @param visible
     */
    public void setHazeViewVisible(int visible)
    {

        if (visible == View.GONE || visible == View.INVISIBLE)
        {
            isRunning = false;
        }
        else
        {
            //first haze  horizontal postion,this vaule will be changed every sleep time
            //used to move haze
            x = 0;
            isRunning = true;

            if (hazeDrawTread == null || !hazeDrawTread.isAlive())
            {
//                initView();
                hazeDrawTread = new Thread(new HazeRunable());
                hazeDrawTread.start();
            }

        }
        this.setVisibility(visible);
    }


    public void setHazeMove(int paramPM25)
    {

        synchronized (lockObj)
        {
            lastAlpha = currentAlpha;
            alphaDle = 0;
            currentAlpha = 0;
            if (paramPM25 > 50 && paramPM25 <= 100)
            {
                currentAlpha = (int)(255 * 0.55);

            } else if (paramPM25 > 100 && paramPM25 <= 150)
            {
                currentAlpha = (int)(255 * 0.75);

            } else if (paramPM25 > 150 && paramPM25 <= 200) {
                currentAlpha = (int)(255 * 0.9);
            }
            else if(paramPM25 > 200)
            {
                currentAlpha = 255;
            }

            if (lastAlpha > currentAlpha)
            {
                alphaDle = lastAlpha - currentAlpha > 40 ? -30 : -10;
            }
            if (lastAlpha < currentAlpha)
            {
                alphaDle = currentAlpha -  lastAlpha > 40 ? 20 : 10;
            }

        }

    }

    private  Bitmap zoomBitmap(Bitmap bitmap, int reqHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleHeight = ((float) reqHeight / height);
        matrix.postScale(scaleHeight, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newbmp;
    }
}
