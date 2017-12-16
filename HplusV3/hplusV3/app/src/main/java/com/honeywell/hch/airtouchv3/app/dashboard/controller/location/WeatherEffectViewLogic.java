package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.MainActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.lib.weather.HaloView;
import com.honeywell.hch.airtouchv3.lib.weather.HazeView;
import com.honeywell.hch.airtouchv3.lib.weather.initializers.ParticleSystem;
import com.honeywell.hch.airtouchv3.lib.weather.snow.SnowView;

/**
 * Created by wuyuan on 10/21/15.
 */
public class WeatherEffectViewLogic {

    private static final int DEFAULT_WEATHER_CODE = 6;

    private MainActivity mMainActivity;
    private HazeView mHazeView;
    private HaloView mHaloView;
    private SnowView mSnowView;

    private RelativeLayout mParentView;

    private ParticleSystem rainSystemTop;
    private ParticleSystem rainSystemLeft;
    private ParticleSystem rainDropSystem;

    private static final int WEATHER_RAIN_MIN = 13;

    private static final int WEATHER_RAIN_MAX = 19;

    private static final int WEATHER_HALO_MIN = 0;
    private static final int WEATHER_HALO_MAX = 3;

    private static final int WEATHER_SNOW_MIN = 20;
    private static final int WEATHER_SNOW_MAX = 25;

    private static final int PM_HAZE_MINI = 75;


    public WeatherEffectViewLogic(MainActivity mainActivity,RelativeLayout parentView){
        mMainActivity = mainActivity;
        mParentView = parentView;
    }

    public void startShowHazeView(){
        int currentPmValue = getCurrentHomeIndexPmValue();
        if (currentPmValue > PM_HAZE_MINI && mHazeView == null){
            mHazeView = new HazeView(mMainActivity);
            mHazeView.setVisibility(View.VISIBLE);
            mHazeView.setHazeMove(currentPmValue);
            mParentView.addView(mHazeView);
        }
    }


    public void startOtherWeatherEffect(){
        dicideShowEffect();
    }

    public void dicideShowEffect(){
        int weatherCode = getCurrentWeatherCode();
        if (weatherCode >= WEATHER_HALO_MIN && weatherCode <= WEATHER_HALO_MAX){
            stopSnow();
            stopRainEffect();
            if (mHaloView == null && AppConfig.shareInstance().isDaylight()){
                mHaloView =  new HaloView(mMainActivity);
                mParentView.addView(mHaloView);
            }
        }
        else if (weatherCode >= WEATHER_RAIN_MIN && weatherCode <= WEATHER_RAIN_MAX){
            stopHalo();
            stopSnow();
            startRainEffect();
        }
        else if (weatherCode >= WEATHER_SNOW_MIN && weatherCode <= WEATHER_SNOW_MAX){
            stopRainEffect();
            stopHalo();
            if (mSnowView == null){
                mSnowView = new SnowView(mMainActivity);
                mParentView.addView(mSnowView);
            }
        } else{
            stopSnow();
            stopRainEffect();
            stopHalo();
        }

    }


    private int getCurrentHomeIndexPmValue(){
        int currentPmValue = 0;
        if (mMainActivity.getCurrentHomeIndex() < mMainActivity.getHomeList().size()){
            BaseLocationFragment currentLocation= mMainActivity.getHomeList().get(mMainActivity.getCurrentHomeIndex());
            if (currentLocation != null){
                currentPmValue =  currentLocation.getPm25Value();
            }
        }

        return currentPmValue;
    }

    private int getCurrentWeatherCode(){
        int weatherCode = DEFAULT_WEATHER_CODE;
        int currentIndex = mMainActivity.getCurrentHomeIndex();
        if (currentIndex < mMainActivity.getHomeList().size()){
            BaseLocationFragment currentLocation= mMainActivity.getHomeList().get(currentIndex);
            if (currentLocation != null){
                weatherCode =  currentLocation.getmWeatherCode();
            }
        }


        return weatherCode;
    }



    public void setCurrentHazeMoving() {
        startShowHazeView();
    }

    public void startRainEffect(){
        if (mParentView == null){
            return;
        }

        //rain
        if (rainSystemLeft == null){
            rainSystemLeft = new ParticleSystem(mMainActivity, 200, R.drawable.rainline, 10000,R.id.parent_view_id);
            rainSystemLeft = rainSystemLeft.setExactlyScale(0.4f)
                    .setAcceleration(0.000013f, 90)
                    .setInitialRotationRange(true,150)
                    .setSpeedByComponentsRange(0.8f, 0.8f, 0.8f, 0.8f)
                    .setParticleAlpha(0.8f);
            //set gravity = 10 make sure the emit covery all the screen.
            rainSystemLeft.emitWithGravity(mParentView, Gravity.LEFT, 20);

        }
        if (rainSystemTop == null)
        {
            rainSystemTop = new ParticleSystem(mMainActivity, 200, R.drawable.rainline, 10000,R.id.parent_view_id);
            rainSystemTop = rainSystemTop.setExactlyScale(0.4f)
                    .setAcceleration(0.000013f, 90)
                    .setInitialRotationRange(true, 150)
                    .setSpeedByComponentsRange(0.8f, 0.8f, 0.8f, 0.8f)
                    .setParticleAlpha(0.8f);
            //set gravity = 10 make sure the emit covery all the screen.
            rainSystemTop.emitWithGravity(mParentView, Gravity.TOP, 20);

        }

        //rain drop

        if (rainDropSystem == null){
            rainDropSystem = new ParticleSystem(mMainActivity, 200, R.drawable.raindrop, 30000,R.id.parent_view_id);
            rainDropSystem = rainDropSystem.setSpeedRelatedSize(0.05f,0.1f, 0.5f,0.000013f)
                    .setFadeOut(200).setIsNeedMerge(true);
            //set gravity = 10 make sure the emit covery all the screen.
            rainDropSystem.emitWithGravity(mParentView, 10, 20);
        }
    }

    public void stopRainEffect(){
        if (rainSystemLeft != null){
            rainSystemLeft.stopEmitting();
            rainSystemTop.stopEmitting();

            rainSystemLeft.recycleUsedParticle();
            rainSystemTop.recycleUsedParticle();
            rainSystemLeft = null;
            rainSystemTop = null;
        }
        if (rainDropSystem != null){
            rainDropSystem.stopEmitting();
            rainDropSystem.recycleUsedParticle();
            rainDropSystem = null;
        }
    }


    public void stopAllWeatherEffect(){
        stopRainEffect();
        if (mSnowView != null) {
            mSnowView.setVisibility(View.GONE);
            mSnowView.recycleSnow();
        }
        if (mHaloView != null) {
            mHaloView.setVisibility(View.GONE);
            mHaloView.destroyView();
        }
        if (mHazeView != null) {
            mHazeView.setVisibility(View.GONE);
            mHazeView.destroyView();
        }

        new Handler().post(new Runnable() {
            public void run() {
                if (mSnowView != null) {

                    mParentView.removeView(mSnowView);
                    mSnowView = null;
                }
                if (mHaloView != null) {

                    mParentView.removeView(mHaloView);
                    mHaloView = null;
                }
                if (mHazeView != null) {

                    mParentView.removeView(mHazeView);
                    mHazeView = null;
                }
            }
        });
    }

    public void stopHalo(){
        if (mHaloView != null) {
            mHaloView.setVisibility(View.GONE);
            mHaloView.destroyView();
            new Handler().post(new Runnable() {
                public void run() {
                    if (mHaloView != null) {
                        mParentView.removeView(mHaloView);
                        mHaloView = null;
                    }
                }
            });
        }
    }

    public void stopSnow(){
        if (mSnowView != null) {
            mSnowView.setVisibility(View.GONE);
            mSnowView.recycleSnow();

            new Handler().post(new Runnable() {
                public void run() {
                    if (mSnowView != null) {
                        mParentView.removeView(mSnowView);
                        mSnowView = null;
                    }
                }
            });
        }

    }

}
