package com.honeywell.hch.airtouchv3.framework.app.activity;

import android.content.Context;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.BlurBackgroundView;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.lib.util.BlurImageUtil;

/**
 * Base activity for which has background
 * Created by nan.liu on 1/19/15.
 */
public class BaseHasBackgroundActivity extends BaseActivity {

    protected  BlurBackgroundView mBlurBackgroundView;
    protected UserLocationData mUserLocationData;

    protected int mLocationId = 0;

    protected Context mContext = this;


    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.destroyed();
            mBlurBackgroundView = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startSwitchBackground();
    }

    @Override
    public void onStop(){
        super.onStop();
        stopSwitchBackground();
    }

    @Override
    public void finish() {
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.destroyed();
            mBlurBackgroundView = null;
        }

        super.finish();
        overridePendingTransition(R.anim.finish_zoomin, R.anim.finish_zoomout);
    }

    public void initDynamicBackground(){
        mLocationId = getIntent().getIntExtra(AirTouchConstants.LOCATION_ID,0);
        if (mLocationId != 0){
            mUserLocationData = AppManager.shareInstance().getLocationWithId(mLocationId);
        }
        if (mUserLocationData == null){
            mUserLocationData = AppManager.shareInstance().getGpsUserLocation();
        }

        mBlurBackgroundView = (BlurBackgroundView) findViewById(R.id.home_background);
        mBlurBackgroundView.initDynmac(mUserLocationData);
        mBlurBackgroundView.initBackgroundResouce(R.raw.default_city_day_blur1, BlurImageUtil.OTHER_ACTVITIY_BLUR_RADIO);
    }

    public void stopSwitchBackground() {
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.stopSwitchBackground(true);
        }
    }

    public void startSwitchBackground(){
        if (mBlurBackgroundView != null){
            mBlurBackgroundView.startTimer(BlurImageUtil.OTHER_ACTVITIY_BLUR_RADIO);
        }
    }

}
