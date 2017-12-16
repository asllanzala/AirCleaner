package com.honeywell.hch.airtouchv2.app.airtouch.controller.emotion;

import android.graphics.Bitmap;

import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by wuyuan on 15/6/17.
 */
public class EmontionParticle
{

    class Direct
    {
        int direct;

        //if the direct is out of the bottle ,set valid false
        //defualt value is true
        boolean isvalid;

        public Direct(int direct,boolean isVailid)
        {
            this.direct = direct;
            this.isvalid = isVailid;
        }

    }

    private   Direct NO_MOVE = new Direct(-1,true);

    private   Direct LEFT_DIRECT = new Direct(0,true);

    private   Direct LEFT_BOTTOM_DIRECT = new Direct(1,true);;

    private   Direct BOTTOM_DIRECT = new Direct(2,true);;

    private   Direct BOTTOM_RIGHT_DIRECT  = new Direct(3,true);;

    private   Direct RIGHT_DIRECT  = new Direct(4,true);;

    private   Direct RIGHT_TOP_DIRECT  = new Direct(5,true);;

    private   Direct TOP_DIRECT  = new Direct(6,true);;

    private   Direct TOP_LEFT_DIRECT  = new Direct(7,true);;

//    private  static final int LEFT_DIRECT = 0;
//
//    private static final int LEFT_BOTTOM_DIRECT = 1;
//
//    private static final int BOTTOM_DIRECT = 2;
//
//    private static final int BOTTOM_RIGHT_DIRECT  = 3;
//
//    private static final int RIGHT_DIRECT  = 4;
//
//    private static final int RIGHT_TOP_DIRECT  = 5;
//
//    private static final int TOP_DIRECT  = 6;
//
//    private static final int TOP_LEFT_DIRECT  = 7;

    private  Direct[] directInt = new Direct[]{LEFT_DIRECT,LEFT_BOTTOM_DIRECT,BOTTOM_DIRECT,BOTTOM_RIGHT_DIRECT,RIGHT_DIRECT,
            RIGHT_TOP_DIRECT,TOP_DIRECT,TOP_LEFT_DIRECT};

    private List<Direct> directList = new ArrayList<Direct>();

    private Bitmap particleImage;

    private int positionX;

    private int positionY;

    private double scale = 0.7f;

    private int speed;

    private float scalePercent;

    private int alpha = 255;

    private Direct directObJ;

    private boolean isBigParticle;


    public EmontionParticle(Bitmap resourceImage, int startX, int startY,boolean bigParticle)
    {
        particleImage = resourceImage;
        directList.addAll(Arrays.asList(directInt));;

        speed = 1;
        directObJ = new Direct(getRandomDirection(-1),true);

        positionX = startX + 0;
        positionY = startY + 0;

        isBigParticle = bigParticle;

        if (isBigParticle)
        {
            scale = 0.4;
            alpha = (int)(0.9 * 255);
        }

    }

    private int getRandomSpeed()
    {
        Random random = new Random();
        int randomSpeed = random.nextInt(6);
        if (randomSpeed <= 2)
        {
            return 2;
        }

        return  randomSpeed;

    }

    public void setScaleAndApla(int centerX,int centerY)
    {

        double distance = Math.sqrt(Math.pow(positionX - centerX, 2) + Math.pow(positionY -
                centerY, 2));
        if (distance >= DensityUtil.dip2px(50) && !isBigParticle)
        {
//            scale = 0.5f;
            alpha = (int)(0.3 * 255);
        }
        else if (!isBigParticle)
        {

//            scale = 1.0 - (distance * 0.5)/DensityUtil.dip2px(50);
            alpha = (int)(1.0 - (distance * 0.7)/DensityUtil.dip2px(50) * 255);
        }

    }

    //
    public int getRandomDirection(int needMoveDirect)
    {
        Random random = new Random();
        int randomInt = random.nextInt(8);

        if (needMoveDirect == -1 || directList.size() <= 0)
        {
            resetDirectValid();
            return directList.get(randomInt).direct;
        }
        else
        {
            directList.get(needMoveDirect).isvalid = false;
        }

        if (directList.get(randomInt).isvalid)
        {
            return directList.get(randomInt).direct;
        }

        for (int i = 0; i < directList.size();i++)
        {
            if (directList.get(i).isvalid)
            {
                return directList.get(i).direct;
            }
        }

//        //if all directList is valid,reset and return first one
//        resetDirectValid();

//        return directList.get(0).direct;
        return NO_MOVE.direct;

    }

    public void setDirectInvalid(int direct)
    {
        if (direct != NO_MOVE.direct)
        {
            directList.get(direct).isvalid = false;

        }
    }



    public void resetDirectValid()
    {
        for(int i = 0;i < directList.size();i++)
        {
            directList.get(i).isvalid = true;
        }
    }

    public int getValidDirect()
    {
        for (int i = 0; i < directList.size();i++)
        {
            if (directList.get(i).isvalid)
            {
                return directList.get(i).direct;
            }
        }
        return NO_MOVE.direct;
    }



    public Bitmap getParticleImage()
    {
        return particleImage;
    }

    public void setParticleImageRes(Bitmap particleImageRes)
    {
        this.particleImage = particleImageRes;
    }

    public int getPositionX()
    {
        return positionX;
    }

    public void setPositionX(int positionX)
    {
        this.positionX = positionX;
    }

    public int getPositionY()
    {
        return positionY;
    }

    public void setPositionY(int positionY)
    {
        this.positionY = positionY;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public float getScalePercent()
    {
        return scalePercent;
    }

    public void setScalePercent(float scalePercent)
    {
        this.scalePercent = scalePercent;
    }

    public int getAlpha()
    {
        return alpha;
    }

    public void setAlpha(int alpha)
    {
        this.alpha = alpha;
    }

    public int getDirect()
    {
        return directObJ.direct;
    }

    public void setDirect(int direct)
    {
        this.directObJ.direct = direct;
    }

    public int getParticleWidth()
    {
        return (int)(particleImage.getWidth() * scale);
    }

    public int getParticleHeight()
    {
        return (int)(particleImage.getHeight() * scale);
    }

    public double getScale()
    {
        return scale;
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }

    public void recycleParticleBitmap()
    {
        if (particleImage != null)
        {
            particleImage.recycle();
            particleImage = null;
        }
    }
}
