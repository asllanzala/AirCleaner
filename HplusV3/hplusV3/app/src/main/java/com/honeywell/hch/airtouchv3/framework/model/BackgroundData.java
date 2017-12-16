package com.honeywell.hch.airtouchv3.framework.model;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.app.airtouch.model.BackgroundBitmap;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by wuyuan on 10/20/15.
 */
public class BackgroundData {

    private static final float FRONT_ALPHA = 1.0f;
    private static final float BACK_ALPHA = 1.0f;

    /**
     * all the path of the background
     */
    private List<String> mCityBackgroundPathList = new CopyOnWriteArrayList<>();

    /**
     * all the path of the background
     */
    private List<String> mBlurBackgroundPathList = new CopyOnWriteArrayList<>();

    /**
     * the background object UI will used
     */
    private List<BackgroundBitmap> mCityBackgroundObjectList = new CopyOnWriteArrayList<>();

    private int mFrontIndex;

    private int mWeatherCode = 0;


    public BackgroundData(){

    }

    public List<String> getCityBackgroundPathList() {
        return mCityBackgroundPathList;
    }

    public void addItemToCityPathList(String pathItem) {
        boolean isHasSame = false;
        for (String item : mCityBackgroundPathList){
            if (item.equals(pathItem)){
                isHasSame = true;
                break;
            }
        }
        if (!isHasSame){
            mCityBackgroundPathList.add(pathItem);
        }
    }

    public void addItemToBlurList(String blurPathItem) {
        boolean isHasSame = false;
        for (String item : mBlurBackgroundPathList){
            if (item.equals(blurPathItem)){
                isHasSame = true;
                break;
            }
        }
        if (!isHasSame){
            mBlurBackgroundPathList.add(blurPathItem);
        }
    }

    public void setCityBackgroundPathList(ArrayList<String> mCityBackgroundPathList) {
        this.mCityBackgroundPathList = mCityBackgroundPathList;
    }

    public List<BackgroundBitmap> getmCityBackgroundObjectList() {
        return mCityBackgroundObjectList;
    }

    public void setCityBackgroundObjectList(ArrayList<BackgroundBitmap> mCityBackgroundObjectList) {
        this.mCityBackgroundObjectList = mCityBackgroundObjectList;
    }

    public int getFrontIndex() {
        return mFrontIndex;
    }

    public void setFrontIndex(int mFrontIndex) {
        this.mFrontIndex = mFrontIndex;
    }


    /**
     * --------background
     */
    /**
     * call when
     * 1.view is created
     * 2.weather is update
     * 3.switch time between day and night
     * @param weatherCode
     */
    public void initmCityBackgroundObjectListList(int weatherCode,boolean isSwitchToDay){

        String weatherCondition = "bad";
        if (weatherCode <= 3){
            weatherCondition = "good";
        }
//        if (!AppConfig.shareInstance().isDaylight()){
//            weatherCondition = "night";
//        }


        //if the time switch night to day,in other words,if isSwitchToDay is true,
        //we need to clear the current list
        if ((!isSwitchToDay && mWeatherCode != weatherCode) || isSwitchToDay){
           mCityBackgroundObjectList.clear();
        }


        refreshCityBackgroundList(weatherCondition);
        mWeatherCode = weatherCode;
    }

    private void refreshCityBackgroundList(String backgroundFilter){
        boolean isFront = true;
        int backgroundSize = mCityBackgroundPathList.size();
        for (int i = 0; i < backgroundSize;i++){
            String pathItem = mCityBackgroundPathList.get(i);
            if (isContainCondition(pathItem,backgroundFilter) && !isGroundBitmapListContain(pathItem)){
                BackgroundBitmap backgroundBitmap = new BackgroundBitmap(pathItem, HPlusApplication
                        .getInstance().getApplicationContext(),isFront,mBlurBackgroundPathList.get(i));
                isFront = false;
                mCityBackgroundObjectList.add(backgroundBitmap);
            }
        }
        if (mFrontIndex >= mCityBackgroundObjectList.size()){
            mFrontIndex = 0;
        }
    }


    private boolean isContainCondition(String pathItem, String backgroundFilter){
        if (AppConfig.shareInstance().isDaylight()){
            return pathItem.contains(backgroundFilter) && pathItem.contains("day");
        }
        else{
            return pathItem.contains("night");
        }
    }

    private boolean isGroundBitmapListContain(String pathItem){
        for (BackgroundBitmap backgroundBitmap : mCityBackgroundObjectList){
            if (backgroundBitmap.getBackgroundPath().equals(pathItem)){
                return true;
            }
        }
        return false;
    }

    public BackgroundBitmap getFrontBackground(){
        if (mFrontIndex < mCityBackgroundObjectList.size()){
            BackgroundBitmap backgroundBitmap = mCityBackgroundObjectList.get(mFrontIndex);
            backgroundBitmap.setAlpha(FRONT_ALPHA);
            return backgroundBitmap;
        }
        mCityBackgroundObjectList.get(0).setAlpha(FRONT_ALPHA);
        return mCityBackgroundObjectList.get(0);
    }

    public BackgroundBitmap getNextBackground(){
        int nextIndex = (mFrontIndex + 1) % mCityBackgroundObjectList.size();
        if (nextIndex < mCityBackgroundObjectList.size()){
            BackgroundBitmap backgroundBitmap = mCityBackgroundObjectList.get(nextIndex);
            backgroundBitmap.setAlpha(BACK_ALPHA);
            return backgroundBitmap;
        }

        return mCityBackgroundObjectList.get(mFrontIndex);
    }

    public void resetFrontIndex(boolean isNeedRecycleCurrent){
        if (mCityBackgroundObjectList.size() > 0 && mFrontIndex < mCityBackgroundObjectList.size()){
            BackgroundBitmap backgroundBitmap = mCityBackgroundObjectList.get(mFrontIndex);
            if (!backgroundBitmap.ismIsFront()){
                mCityBackgroundObjectList.get(mFrontIndex).recycleBackgroundResource();
                mFrontIndex = (mFrontIndex + 1) % mCityBackgroundObjectList.size();
            }
        }
        if (isNeedRecycleCurrent){
            if (mCityBackgroundObjectList.size() > 0 && mFrontIndex < mCityBackgroundObjectList.size()){
                BackgroundBitmap backgroundBitmap = mCityBackgroundObjectList.get(mFrontIndex);
                if (backgroundBitmap != null){
                    backgroundBitmap.recycleBackgroundResource();
                }
            }
        }

    }



}
