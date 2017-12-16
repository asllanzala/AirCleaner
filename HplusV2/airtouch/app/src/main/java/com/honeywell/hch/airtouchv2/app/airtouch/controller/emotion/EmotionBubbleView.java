package com.honeywell.hch.airtouchv2.app.airtouch.controller.emotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;

/**
 * Created by wuyuan on 15/6/26.
 */
public class EmotionBubbleView extends View
{

    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;

    private Bitmap mBitmap;
    private Paint mPaint;
    private float[] mInhalePoint;
    private InhaleMesh mInhaleMesh;
    private int resourceId;

    private int mBitmapHeight = 0;
    private int mBitmapWidth = 0;

    private ShowOrHideBubbleContentListener bubbleContentListener;

    public EmotionBubbleView(Context context)
    {
        super(context);
        setWillNotDraw(false);
        initMesh();
    }

    public EmotionBubbleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setWillNotDraw(false);
        initMesh();
    }

    public EmotionBubbleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        initMesh();
    }

    /**
     * set bubble mesh animation end listener
     * @param listener
     */
    public void setBubbleContentListener(ShowOrHideBubbleContentListener listener)
    {
        bubbleContentListener = listener;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        float bitmapWidth = mBitmap.getWidth();
        float bitmapHeight = mBitmap.getHeight();

        buildPaths(bitmapWidth / 2 + DensityUtil.dip2px(120), bitmapHeight + DensityUtil.dip2px(10));
        buildMesh(bitmapWidth, bitmapHeight);
    }

    private void initMesh()
    {
        resourceId = R.drawable.emotion_bubble;
        if (DensityUtil.getScreenHeight() == 1800)
        {
            resourceId = R.drawable.meizu_bubble_closedangle;
        }
        mBitmap = BitmapFactory.decodeResource(getResources(),resourceId);
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mPaint = new Paint();
        mInhalePoint = new float[]{0, 0};
        mInhaleMesh = new InhaleMesh(WIDTH, HEIGHT);
        mInhaleMesh.setBitmapSize(mBitmap.getWidth(), mBitmap.getHeight());
    }

    public boolean startBubbleAnimation(final boolean reverse,int duration)
    {
        Animation anim = this.getAnimation();
        if (null != anim && !anim.hasEnded())
        {
            return false;
        }

        PathAnimation animation = new PathAnimation(0, HEIGHT + 1, reverse,
                new PathAnimation.IAnimationUpdateListener()
                {
                    @Override
                    public void onAnimUpdate(int index)
                    {
                        mInhaleMesh.buildMeshes(index);
                        invalidate();
                    }
                });
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (reverse)
                {
                    bubbleContentListener.showBubbleContent();
                }
                else
                {
                    bubbleContentListener.afterHideBubble();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        if (null != animation)
        {
            animation.setDuration(duration);
            this.startAnimation(animation);
        }
        return true;
    }

    private void buildMesh(float w, float h)
    {
        mInhaleMesh.buildMeshes(w, h);
    }

    private void buildPaths(float endX, float endY)
    {
        mInhalePoint[0] = endX;
        mInhalePoint[1] = endY;
        mInhaleMesh.buildPaths(endX, endY);
    }

    public void setBuildPaths(float ypositonDelta)
    {
        float bitmapWidth = mBitmap.getWidth();
        float bitmapHeight = mBitmap.getHeight();

        buildPaths(bitmapWidth / 2 + DensityUtil.dip2px(120), bitmapHeight + ypositonDelta);
        buildMesh(bitmapWidth, bitmapHeight);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmapMesh(mBitmap, mInhaleMesh.getWidth(), mInhaleMesh.getHeight(),
                mInhaleMesh.getVertices(), 0, null, 0, mPaint);

//        mPaint.setColor(Color.RED);
//        mPaint.setStrokeWidth(2);
//        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(mInhalePoint[0], mInhalePoint[1], 5, mPaint);
    }

    public int getBubbleViewHeigh()
    {
        return mBitmapHeight;
    }

    public int getBubbleViewWidth()
    {
        return mBitmapWidth;
    }


    /**
     * when mesh animation end,we need to show bubble content or after hide bubble
     */
    public interface ShowOrHideBubbleContentListener
    {
        public void showBubbleContent();

        public void afterHideBubble();
    }
    

}
