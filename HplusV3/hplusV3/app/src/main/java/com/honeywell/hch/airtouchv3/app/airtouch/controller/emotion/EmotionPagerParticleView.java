package com.honeywell.hch.airtouchv3.app.airtouch.controller.emotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyuan on 15/6/8.
 */
public class EmotionPagerParticleView extends RelativeLayout
{
    private Context mContext;

    private Region region = new Region();

    private Matrix mMatrix = new Matrix();

    private List<EmontionParticle> particleList = new ArrayList<EmontionParticle>();

    private float mRotation = 0f;

    private boolean isFirstDraw = true;

    private boolean isRunning = true;

    private Thread particleThread;

    private int translateX = 0;

    private int translateY = 0;

    private ImageView bigBottleImageView;

//    private Bitmap bottleBitmap;

    public EmotionPagerParticleView(Context context)
    {
        super(context);
        mContext = context;
        setWillNotDraw(false);
    }

    public EmotionPagerParticleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        setWillNotDraw(false);

    }

    public EmotionPagerParticleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        setWillNotDraw(false);

    }


    /**
     * generate particle according level
     * @param particleLevel
     */
    public void generateParticle(int particleLevel)
    {

        initView(mContext);

        ParticleFactory.setBottleView(getWidth() / 2, getHeight() * 2 / 3);
        ParticleFactory.generateParticle(particleLevel,mContext.getApplicationContext(), getHeight() * 2 / 3);
        isRunning = true;
        particleThread = new Thread(new ParticleRunable());
        particleThread.start();
    }

    private int getBigBottleCapHeight()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cap2);
        int bigBottleCapImageHeight = bitmap.getHeight();
        bitmap.recycle();
        bitmap = null;
        return bigBottleCapImageHeight;
    }

    private void initView(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bigbottle_view, this);
        bigBottleImageView = (ImageView)view.findViewById(R.id.big_bottle_id);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

//        drawPath(canvas);


        ParticleFactory.setBottleView(getWidth() / 2, getHeight() * 2 / 3);
        ParticleFactory.drawParticle(canvas);


    }


    private class ParticleRunable implements  Runnable
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

    public void setParticleViewVisible(int isVisible)
    {
        if (isVisible == View.VISIBLE)
        {
            isRunning = true;
            if (particleThread == null || !particleThread.isAlive())
            {
                particleThread = new Thread(new ParticleRunable());
                particleThread.start();
            }
        }
        else
        {
            isRunning = false;
        }
        setVisibility(isVisible);
    }


    public void stopParticleMove()
    {
        isRunning = false;
        ParticleFactory.deleteParticleListAndRecyle();
        invalidate();
    }

    public void stopParticlThread()
    {
        isRunning = false;
    }


}
