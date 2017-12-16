package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseFragment;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.WeatherData;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by nan.liu on 2/13/15.
 */
public class HomeSkyFragment extends BaseFragment {
    private static final String TAG = "AirTouchHomeSkyFragment";
    private View[] mAirQualityViews = new View[AIR_QUALITY_COUNT];
    private TextView[] mAirQualityTextViews = new TextView[AIR_QUALITY_COUNT];
    private View mBackgroundView;

    private static final int AIR_QUALITY_COUNT = 6;
    private static final int BASE_POP_UP_DURATION = 750;
    private static final String[] AIR_QUALITY_KEY = {"aqi", "pm10", "so2", "no2", "co", "o3"};
    private int[] mAirQualityViewIds = {R.id.aqi_view, R.id.pm10_view, R.id.so2_view,
            R.id.no2_view, R.id.co_view, R.id.o3_view};
    private int[] mAirQualityTextViewIds = {R.id.aqi_text, R.id.pm10_text, R.id.so2_text,
            R.id.no2_text, R.id.co_text, R.id.o3_text};
    private int[] mPopUpDuration = {BASE_POP_UP_DURATION + 400, BASE_POP_UP_DURATION + 200,
            BASE_POP_UP_DURATION, BASE_POP_UP_DURATION - 100, BASE_POP_UP_DURATION - 200,
            BASE_POP_UP_DURATION + 500};

    private FragmentActivity mActivity;
    private View mView;
    private boolean isViewReady = false;

    private boolean isBubbleShow = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeCellFragment.
     */
    public static HomeSkyFragment newInstance(FragmentActivity activity) {
        HomeSkyFragment fragment = new HomeSkyFragment();
        fragment.setActivity(activity);
        return fragment;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_homesky, container, false);
        isViewReady = true;
        mBackgroundView = mView.findViewById(R.id.sky_background);
        for (int i = 0; i < AIR_QUALITY_COUNT; i++) {
            mAirQualityViews[i] = mView.findViewById(mAirQualityViewIds[i]);
            mAirQualityTextViews[i] = (TextView) mView.findViewById(mAirQualityTextViewIds[i]);
        }
        switchTimeView();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateViewData(WeatherData weatherData) {
        if (weatherData == null || weatherData.getWeather() == null || weatherData.getWeather()
                .size() == 0 || weatherData.getWeather().get(0) == null)
            return;
        if (weatherData.getWeather().get(0).getNow() != null) {
            for (int i = 0; i < AIR_QUALITY_COUNT; i++) {
                mAirQualityTextViews[i].setText(weatherData.getWeather().get(0).getNow()
                        .getAirQuality().getAirQualityIndex().getValue(AIR_QUALITY_KEY[i]));
            }
        }
    }

    public void showBubble() {
        isBubbleShow = true;
        for (int i = 0; i < AIR_QUALITY_COUNT; i++) {
            popUpBubble(mAirQualityViews[i], i);
        }
    }

    public void hideBubble() {
        isBubbleShow = false;
        for (int i = 0; i < AIR_QUALITY_COUNT; i++) {
            scaleHideBubble(mAirQualityViews[i], i);
        }
    }

    public boolean getIsBubbleShowStatus()
    {
        return isBubbleShow;
    }

    public void setIsBubbleShow(boolean isBShow)
    {
        this.isBubbleShow = isBShow;
    }


    protected void popUpBubble(View view, int index) {
        if (view == null)
            return;
        AnimatorSet popAnimation = new AnimatorSet();
        float positionY = ViewHelper.getY(view);
        int screenHeight = mView.getHeight();
        popAnimation.playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 0, 1),
                ObjectAnimator.ofFloat(view, "translationY", screenHeight - positionY, 0)
        );
        popAnimation.setInterpolator(new OvershootInterpolator());
        popAnimation.setDuration(mPopUpDuration[index]);
        popAnimation.start();
    }

    protected void scaleHideBubble(View view, int index) {
        if (view == null)
            return;
        AnimatorSet hideAnimation = new AnimatorSet();
        float positionY = ViewHelper.getY(view);
        int screenHeight = mView.getHeight();
        hideAnimation.playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 1, 0),
                ObjectAnimator.ofFloat(view, "translationY", 0, screenHeight - positionY),
                ObjectAnimator.ofFloat(view, "scaleX", 1.5f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.5f)
        );
        hideAnimation.setDuration(mPopUpDuration[index]);
//        hideAnimation.start();
    }

    public void switchTimeView() {
        if (!isViewReady)
            return;
        mBackgroundView.setBackgroundResource(AppConfig.shareInstance().isDaylight() ? R.drawable
                .half_sky_daylight : R.drawable.half_sky_night);
    }
}
