package com.honeywell.hch.airtouchv2.app.airtouch.controller.emotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wuyuan on 15/6/19.
 */
public class ParticleFactory
{
    private static Bitmap bigBottleImage = null;

    private static List<EmontionParticle> particleList = new ArrayList<EmontionParticle>();

    private  static final int NO_MOVE = -1;

    private  static final int LEFT_DIRECT = 0;

    private static final int LEFT_BOTTOM_DIRECT = 1;

    private static final int BOTTOM_DIRECT = 2;

    private static final int BOTTOM_RIGHT_DIRECT  = 3;

    private static final int RIGHT_DIRECT  = 4;

    private static final int RIGHT_TOP_DIRECT  = 5;

    private static final int TOP_DIRECT  = 6;

    private static final int TOP_LEFT_DIRECT  = 7;

    private static String lockObject = "lock_str";

    private static int bottleViewWidht;

    private static int bottleViewHeight;

    private static int mRotation = 0;

    public static void generateParticle(int level,Context mContext,int capHeight)
    {

        particleList.clear();


        bottleViewHeight = capHeight;

        generateLevelSeven(mContext,level);

        bigBottleImage =  BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                .bottle_big);

//        bigBottleImage = bBottleImage;

    }


    private static void generateLevelSeven(Context mContext,int level)
    {
        Bitmap particleBitmap;
        deleteParticleListAndRecyle();
        if (level == AirTouchConstants.PARTICLE_LEVEL_NONE)
        {
            return;
        }
        if (level > AirTouchConstants.PARTICLE_LEVEL_SIX)
        {
            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_2);
            EmontionParticle particle = new EmontionParticle(particleBitmap, DensityUtil.dip2px(85),DensityUtil.dip2px(61),false);
            particleList.add(particle);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_4);
            EmontionParticle particle4 = new EmontionParticle(particleBitmap, DensityUtil.dip2px(45),DensityUtil.dip2px(135),false);
            particleList.add(particle4);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_11);
            EmontionParticle particle18 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(90) ,DensityUtil.dip2px(310),false);
            particleList.add(particle18);
        }
        if (level > AirTouchConstants.PARTICLE_LEVEL_FIVE)
        {
            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_8);
            EmontionParticle particle8 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(72) ,DensityUtil.dip2px(185),false);
            particleList.add(particle8);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_11);
            EmontionParticle particle12 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(100) ,DensityUtil.dip2px(240),false);
            particleList.add(particle12);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_10);
            EmontionParticle particle10 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(150) ,DensityUtil.dip2px(220),false);
            particleList.add(particle10);
        }


        if (level > AirTouchConstants.PARTICLE_LEVEL_FOUR)
        {
            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_1);
            EmontionParticle particle2 = new EmontionParticle(particleBitmap, DensityUtil.dip2px(92),DensityUtil.dip2px(100),false);
            particleList.add(particle2);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_7);
            EmontionParticle particle14 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(30) ,DensityUtil.dip2px(220),false);
            particleList.add(particle14);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_6);
            EmontionParticle particle16 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(60) ,DensityUtil.dip2px(267),false);
            particleList.add(particle16);
        }

        if (level > AirTouchConstants.PARTICLE_LEVEL_THREE)
        {
            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_6);
            EmontionParticle particle6 = new EmontionParticle(particleBitmap, DensityUtil.dip2px(130),DensityUtil.dip2px(160),false);
            particleList.add(particle6);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_9);
            EmontionParticle particle17 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(45) ,DensityUtil.dip2px(310),false);
            particleList.add(particle17);
        }
//
        if (level > AirTouchConstants.PARTICLE_LEVEL_TWO)
        {
            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_3);
            EmontionParticle particle3 = new EmontionParticle(particleBitmap, DensityUtil.dip2px(55),DensityUtil.dip2px(95),false);
            particleList.add(particle3);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_9);
            EmontionParticle particle9 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(113) ,DensityUtil.dip2px(205),false);
            particleList.add(particle9);
        }


        if (level > AirTouchConstants.PARTICLE_LEVEL_ONE)
        {
            particleBitmap= BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_5);
            EmontionParticle particle5 = new EmontionParticle(particleBitmap, DensityUtil.dip2px(85),DensityUtil.dip2px(145),false);
            particleList.add(particle5);

            particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                    .icon_7);
            EmontionParticle particle7 = new EmontionParticle(particleBitmap, DensityUtil.dip2px(35),DensityUtil.dip2px(175),false);
            particleList.add(particle7);
        }


        particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                .icon_pahs_big);
        EmontionParticle particle11 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(135) ,DensityUtil.dip2px(256),true);
        particleList.add(particle11);

        particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                .icon_1);
        EmontionParticle particle13 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(65) ,DensityUtil.dip2px(230),false);
        particleList.add(particle13);


        particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                .icon_lead_en);
        EmontionParticle particle15 = new EmontionParticle(particleBitmap,DensityUtil.dip2px(10) ,DensityUtil.dip2px(260),true);
        particleList.add(particle15);

    }



    public static void setBottleView(int viewWidth,int viewHeight)
    {
        bottleViewWidht = viewWidth;
        bottleViewHeight = viewHeight;
    }


    public static void drawParticle(Canvas canvas)
    {

        mRotation += 2;
        if (mRotation > 360)
        {
            mRotation = mRotation % 360;
        }
        synchronized (lockObject)
        {
            for (int i = 0; i < particleList.size();i++)
            {

                EmontionParticle particle = particleList.get(i);
                Paint paint=new Paint();
                particle.resetDirectValid();
                if (particle.getDirect() == NO_MOVE)
                {
                    particle.setDirect(particle.getRandomDirection(-1));
                }
                getParticleNextPosition(0, particle, i);
                paint.setAlpha(particle.getAlpha());
                Rect rectDir = new Rect();
                rectDir.set(particle.getPositionX(), particle.getPositionY(), particle
                        .getPositionX() + particle.getParticleWidth(), particle.getPositionY() +
                        particle.getParticleHeight());

                canvas.drawBitmap(particle.getParticleImage(), null, rectDir, paint);
            }


        }

    }


    private static boolean isCollison(EmontionParticle particle,int curentId)
    {
        boolean isCollison = false;
        for (int i = 0; i < particleList.size();i++)
        {
            if (i != curentId)
            {
                isCollison = isCollison || isRegionOverLap(particle, particleList.get(i));
            }
        }
        return isCollison;
    }


    private static boolean isRegionOverLap(EmontionParticle particleOne,EmontionParticle particleTwo)
    {
        int[] particleOnePosition = getNextPosition(particleOne);
        int[] particleTwoPosition = getNextPosition(particleTwo);

        boolean isColsion = isCollsion(particleOnePosition[0], particleTwoPosition[0], particleOnePosition[1],
                particleTwoPosition[1], particleOne.getParticleWidth(), particleTwo
                        .getParticleWidth(), particleOne.getParticleHeight(), particleTwo
                        .getParticleHeight());

        if (isColsion)
        {
            particleOne.setDirectInvalid(particleOne.getDirect());
            particleTwo.setDirectInvalid(particleTwo.getDirect());
        }
        return isColsion;
    }


    private static boolean isCollsion(int x1, int x2, int y1, int y2, int w1,
                              int w2, int h1, int h2)
    {
        if(x1 < x2 && x1 + w1 <= x2)
        {
            return false;
        }
        else if(x1 > x2 && x1 >= x2 + w2)
        {
            return false;
        }
        else if (y1 < y2 && y1 + h1 <= y2)
        {
            return false;
        }
        else if(y1 > y2 && y1 >= y2 + h2)
        {
            return false;
        }
        return true;
    }

    public static void getParticleNextPosition(int retryCount,EmontionParticle particle,int curIndex)
    {
        int nextPositionX = particle.getPositionX();
        int nextPositionY = particle.getPositionY();
        int positionX = particle.getPositionX();
        int positionY = particle.getPositionY();
        int speed = particle.getSpeed();
        int direct = particle.getDirect();
        if (direct == LEFT_DIRECT)
        {
            nextPositionX = positionX - speed;
        }
        else if (direct == LEFT_BOTTOM_DIRECT)
        {
            nextPositionX = positionX - speed;
            nextPositionY = positionY + speed;
        }
        else if (direct == BOTTOM_DIRECT)
        {
            nextPositionY = positionY + speed;
        }
        else if (direct == BOTTOM_RIGHT_DIRECT)
        {
            nextPositionX = positionX + speed;
            nextPositionY = positionY + speed;
        }
        else if (direct == RIGHT_DIRECT)
        {
            nextPositionX = positionX + speed;
        }
        else if (direct == RIGHT_TOP_DIRECT)
        {
            nextPositionX = positionX + speed;
            nextPositionY = positionY - speed;
        }
        else if (direct == TOP_DIRECT)
        {
            nextPositionY = positionY - speed;
        }
        else if (direct == TOP_LEFT_DIRECT)
        {
            nextPositionX = positionX - speed;
            nextPositionY = positionY - speed;
        }
        //adjust the direct and position if the position is out of the region
        boolean isCollison = isCollison(particle, curIndex);
        if (isParticleInBottle(nextPositionX, nextPositionY, particle) && !isCollison)
        {
            particle.setPositionX(nextPositionX);
            particle.setPositionY(nextPositionY);
            particle.setScaleAndApla(bottleViewWidht,bottleViewHeight);
        }
        else
        {
//            //adjust the direct and position
            if (retryCount > 7)
            {
                Log.e("hehe", "retryCount > 7");
                particle.setDirect(particle.getRandomDirection(-1));
            }
            else
            {
                retryCount++;

                if (isCollison)
                {
                    particle.setDirect(particle.getValidDirect());
                }
                else
                {
                    int newDirect = particle.getRandomDirection(direct);
                    particle.setDirect(newDirect);
                }
                getParticleNextPosition(retryCount, particle, curIndex);


            }

        }

    }

    /**
     * get nextPosition for collision check
     * @return
     */
    public static int[] getNextPosition(EmontionParticle particle)
    {
        int nextPositionX = particle.getPositionX();
        int nextPositionY = particle.getPositionY();
        int positionX = particle.getPositionX();
        int positionY = particle.getPositionY();
        int speed = particle.getSpeed();
        int direct = particle.getDirect();
        if (direct == LEFT_DIRECT)
        {
            nextPositionX = positionX - speed;
        }
        else if (direct == LEFT_BOTTOM_DIRECT)
        {
            nextPositionX = positionX - speed;
            nextPositionY = positionY + speed;
        }
        else if (direct == BOTTOM_DIRECT)
        {
            nextPositionY = positionY + speed;
        }
        else if (direct == BOTTOM_RIGHT_DIRECT)
        {
            nextPositionX = positionX + speed;
            nextPositionY = positionY + speed;
        }
        else if (direct == RIGHT_DIRECT)
        {
            nextPositionX = positionX + speed;
        }
        else if (direct == RIGHT_TOP_DIRECT)
        {
            nextPositionX = positionX + speed;
            nextPositionY = positionY - speed;
        }
        else if (direct == TOP_DIRECT)
        {
            nextPositionY = positionY - speed;
        }
        else if (direct == TOP_LEFT_DIRECT)
        {
            nextPositionX = positionX - speed;
            nextPositionY = positionY - speed;
        }

        return new int[]{nextPositionX,nextPositionY};
    }



    private static boolean  isParticleInBottle(int nextPositionX,int nextPositionY,EmontionParticle particle)
    {
        if (nextPositionX > 0 && nextPositionX +  particle.getParticleWidth() < bigBottleImage.getWidth()
                &&  nextPositionY > DensityUtil.dip2px(60)
                &&  nextPositionY  + particle.getParticleHeight() < bigBottleImage.getHeight()
                && bigBottleImage.getPixel(nextPositionX,nextPositionY) != 0
                && bigBottleImage.getPixel(nextPositionX + particle.getParticleWidth(),nextPositionY + particle.getParticleHeight()) != 0)
        {
            return true;
        }
        return false;
    }


//    private static void resetDirect(int nextPositionX,int nextPositionY,EmontionParticle particle)
//    {
//        int direct = particle.getDirect();
//        //if current position is region one,just adjust direct ad BOTTOM_DIRECT
//        if (isInFirstRegion(nextPositionX,nextPositionY,particle))
//        {
//            direct = BOTTOM_DIRECT;
//        }
//        //if current position is belong to region two,if the current direct is LEFT,TOP_LEFT,or LEFT_BOTTOM
//        //just adjust the direct to the right.otherwise to left
//        if (isInSecondRegion(nextPositionX,nextPositionY,particle))
//        {
//            if (direct == LEFT_DIRECT || direct == TOP_LEFT_DIRECT || direct == LEFT_BOTTOM_DIRECT)
//            {
//                direct = RIGHT_DIRECT;
//            }
//            else
//            {
//                direct = LEFT_DIRECT;
//            }
//        }
//        else
//        {
//            //adjust opposite direct
//            if (direct == LEFT_BOTTOM_DIRECT)
//            {
//
//                direct = RIGHT_TOP_DIRECT;
//            }
//            else if ( direct == BOTTOM_RIGHT_DIRECT)
//            {
//                direct = TOP_LEFT_DIRECT;
//            }
//            else
//            {
//                direct = TOP_DIRECT;
//
//            }
//        }
//        particle.setDirect(direct);
//    }


//    private static boolean isInBootle(int nextPositionX,int nextPositionY,EmontionParticle particle)
//    {
//        if (nextPositionX >= DensityUtil.dip2px(50) && nextPositionX <= DensityUtil.dip2px(115) - particle.getParticleImage().getWidth()
//                && nextPositionY >= DensityUtil.dip2px(50) && nextPositionY < DensityUtil.dip2px(280) - particle.getParticleImage().getHeight())
//        {
//            return true;
//        }
//
//        if (nextPositionY >= (DensityUtil.dip2px(267) - 177/45 * nextPositionX) && nextPositionY >= (177/42 * (nextPositionX +  particle.getParticleImage().getWidth())  - DensityUtil.dip2px(415))
//                && nextPositionY >= DensityUtil.dip2px(70) && nextPositionY <= DensityUtil.dip2px(247))
//        {
//            return true;
//        }
//        if (nextPositionY <= (33/18 * nextPositionX + DensityUtil.dip2px(238)) && nextPositionY <= (DensityUtil.dip2px(439) - 33/27 * nextPositionX - particle.getParticleImage().getWidth())
//                &&  nextPositionY >= DensityUtil.dip2px(247) && nextPositionY < DensityUtil.dip2px(280) - particle.getParticleImage().getHeight())
//        {
//            return true;
//        }
//        return false;
//    }

//    private static boolean isInFirstRegion(int nextPositionX,int nextPositionY,EmontionParticle particle)
//    {
//        if (nextPositionX >= DensityUtil.dip2px(50) && nextPositionX <= DensityUtil.dip2px(115) - particle.getParticleImage().getWidth()
//                && nextPositionY >= DensityUtil.dip2px(50) && nextPositionY <= DensityUtil.dip2px(70))
//        {
//            return true;
//        }
//        return false;
//    }
//
//    private static boolean isInSecondRegion(int nextPositionX,int nextPositionY,EmontionParticle particle)
//    {
//        if (nextPositionY >= (DensityUtil.dip2px(267) - 177/45 * nextPositionX) && nextPositionY >= (177/42 * (nextPositionX +  particle.getParticleImage().getWidth())  - DensityUtil.dip2px(415))
//                && nextPositionY >= DensityUtil.dip2px(70) && nextPositionY <= DensityUtil.dip2px(247))
//        {
//            return true;
//        }
//        return false;
//    }


    public static void deleteParticleListAndRecyle()
    {
        synchronized (lockObject)
        {
            if (particleList != null && particleList.size() > 0)
            {
                for (EmontionParticle emontionParticle: particleList)
                {
                    emontionParticle.recycleParticleBitmap();
                }
                particleList.clear();
            }

            if (bigBottleImage != null)
            {
                bigBottleImage.recycle();
                bigBottleImage = null;
            }
        }
    }


}
