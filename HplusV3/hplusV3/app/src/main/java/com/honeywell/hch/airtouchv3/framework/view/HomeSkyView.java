//package com.honeywell.hch.airtouch.framework.view;
//
//import android.content.Context;
//import android.support.v4.app.FragmentActivity;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.animation.OvershootInterpolator;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.honeywell.hch.airtouch.R;
//import com.honeywell.hch.airtouch.app.dashboard.controller.location.BaseLocationFragment;
//import com.honeywell.hch.airtouch.framework.model.xinzhi.WeatherData;
//import com.nineoldandroids.animation.Animator;
//import com.nineoldandroids.animation.AnimatorSet;
//import com.nineoldandroids.animation.ObjectAnimator;
//import com.nineoldandroids.view.ViewHelper;
//
///**
// * Created by wuyuan on 15/6/16.
// */
//public class HomeSkyView extends RelativeLayout
//{
//
//    private static final String TAG = "AirTouchHomeSkyFragment";
//    private View[] mAirQualityViews = new View[AIR_QUALITY_COUNT];
//    private TextView[] mAirQualityTextViews = new TextView[AIR_QUALITY_COUNT];
//
//    private static final int AIR_QUALITY_COUNT = 6;
//    private static final int BASE_POP_UP_DURATION = 550;
//    private static final String[] AIR_QUALITY_KEY = {"aqi", "pm10", "so2", "no2", "co", "o3"};
//    private int[] mAirQualityViewIds = {
//            R.id.aqi_view, R.id.pm10_view, R.id.so2_view,
//            R.id.no2_view, R.id.co_view, R.id.o3_view};
//    private int[] mAirQualityTextViewIds = {R.id.aqi_text, R.id.pm10_text, R.id.so2_text,
//            R.id.no2_text, R.id.co_text, R.id.o3_text};
//    private int[] mPopUpDuration = {BASE_POP_UP_DURATION + 400, BASE_POP_UP_DURATION + 200,
//            BASE_POP_UP_DURATION, BASE_POP_UP_DURATION - 100, BASE_POP_UP_DURATION - 200,
//            BASE_POP_UP_DURATION + 500};
//
//    private FragmentActivity mActivity;
//    private View mView;
//    private boolean isViewReady = false;
//
//    private boolean isBubbleShow = false;
//
//    private Context mContext;
//
//    private BaseLocationFragment parentFragment;
//
//    public HomeSkyView(Context context)
//    {
//        super(context);
//        mContext = context;
//        initView();
//    }
//
//    public HomeSkyView(Context context, AttributeSet attrs)
//    {
//        super(context, attrs);
//        mContext = context;
//        initView();
//    }
//
//    public HomeSkyView(Context context, AttributeSet attrs, int defStyle)
//    {
//        super(context, attrs, defStyle);
//        mContext = context;
//        initView();
//    }
//
//
//    public void setParentFragment(BaseLocationFragment baseLocationFragment)
//    {
//        parentFragment = baseLocationFragment;
//    }
//
//
//    private void initView()
//    {
//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mView = inflater.inflate(R.layout.fragment_homesky,this);
//
//        isViewReady = true;
//        for (int i = 0; i < AIR_QUALITY_COUNT; i++) {
//            mAirQualityViews[i] = mView.findViewById(mAirQualityViewIds[i]);
//            mAirQualityViews[i].setVisibility(View.INVISIBLE);
//            mAirQualityTextViews[i] = (TextView) mView.findViewById(mAirQualityTextViewIds[i]);
//
//            mAirQualityViews[i].setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v)
//                {
//
//                    //1. enlarge the bubble and dismiss
//                    //2. show the ourdoor weather
//                    bubbleClickEvent();
//                }
//            });
//        }
//    }
//
//    public void updateViewData(WeatherData mWeatherData) {
//        if (mWeatherData == null || mWeatherData.getWeather() == null || mWeatherData.getWeather()
//                .size() == 0 || mWeatherData.getWeather().get(0) == null)
//            return;
//        if (mWeatherData.getWeather().get(0).getNow() != null) {
//            for (int i = 0; i < AIR_QUALITY_COUNT; i++) {
//                mAirQualityTextViews[i].setText(mWeatherData.getWeather().get(0).getNow()
//                        .getAirQuality().getAirQualityIndex().getValue(AIR_QUALITY_KEY[i]));
//            }
//        }
//    }
//
//    public void showBubble() {
//        isBubbleShow = true;
//        //start a runnable to avoid ViewHelper.getY(view) and mView.getHeight() is 0 when the first time
//        post(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                for (int i = 0; i < AIR_QUALITY_COUNT; i++) {
//                    popUpBubble(mAirQualityViews[i], i);
//                }
//            }
//        });
//
//    }
//
//    public void hideBubble() {
//        isBubbleShow = false;
//        for (int i = 0; i < AIR_QUALITY_COUNT; i++) {
//            scaleHideBubble(mAirQualityViews[i], i);
//        }
//    }
//
//    public void bubbleClickEvent()
//    {
//        parentFragment.showOutDoorWeatherAnimation();
//    }
//
//
//    public boolean getIsBubbleShowStatus()
//    {
//        return isBubbleShow;
//    }
//
//    public void setIsBubbleShow(boolean isBShow)
//    {
//        this.isBubbleShow = isBShow;
//    }
//
//
//    protected void popUpBubble(final View view, final int index) {
//        if (view == null)
//            return;
//
//        AnimatorSet popAnimation = new AnimatorSet();
//        float positionY = ViewHelper.getY(view);
//        int screenHeight = mView.getHeight();
//        popAnimation.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0, 1), ObjectAnimator
//                .ofFloat(view, "translationY", screenHeight - positionY, 0));
//        popAnimation.setInterpolator(new OvershootInterpolator());
//        popAnimation.setDuration(mPopUpDuration[index]);
//        popAnimation.start();
//        view.setVisibility(View.VISIBLE);
//
//
//    }
//
//    protected void scaleHideBubble(final View view, int index)
//    {
//        if (view == null)
//            return;
//        final float positionY = ViewHelper.getY(view);
//        final int screenHeight = mView.getHeight();
//        AnimatorSet hideAnimation = new AnimatorSet();
//        hideAnimation.addListener(new Animator.AnimatorListener()
//        {
//
//            @Override
//            public void onAnimationStart(Animator animation)
//            {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation)
//            {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation)
//            {
//               view.setVisibility(View.INVISIBLE);
//               ViewHelper.setScaleX(view, 1);
//               ViewHelper.setScaleY(view ,1);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation)
//            {
//
//            }
//        });
//        hideAnimation.playTogether(
//                ObjectAnimator.ofFloat(view, "alpha", 1, 0),
//                ObjectAnimator.ofFloat(view, "scaleX", 1.5f),
//                ObjectAnimator.ofFloat(view, "scaleY", 1.5f)
//        );
//        hideAnimation.setDuration(300);
//        hideAnimation.start();
//    }
//
//
//}
