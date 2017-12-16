package com.honeywell.hch.airtouchv2.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;
import com.nineoldandroids.view.ViewHelper;

import java.util.HashMap;

/**
 * Custom view for city landscape.
 * Created by nan.liu on 3/10/15.
 */
public class CitySiteView extends RelativeLayout {

    private static final String KEY_DEFAULT = "default";

    private ImageView landscapeImageView = null;
    private ImageView landmarkImageView = null;

    private Context mContext = null;
    private String cityCode = null;
    private boolean isDataReady = false;
    private boolean isFirstTimeLayout = true;
    private boolean isDefaultCity = false;
    private int landscapeWidth = 0;
    private int landscapeHeight = 0;
    private int landmarkWidth = 0;
    private int landmarkHeight = 0;

//    private HashMap<String, int[]> cityImageMap = new HashMap<String, int[]>() {
//        {
//            put("CHSH000000", new int[]{R.drawable.shanghai_landmark_daylight,
//                    R.drawable.shanghai_landscape_daylight});
//            put("CHBJ000000", new int[]{R.drawable.beijing_landmark_daylight,
//                    R.drawable.beijing_landscape_daylight});
//            put("CHGD000000", new int[]{R.drawable.guangzhou_landmark_daylight,
//                    R.drawable.guangzhou_landscape_daylight});
//            put("CHSC000000", new int[]{R.drawable.chengdu_landmark_daylight,
//                    R.drawable.chengdu_landscape_daylight});
//            put("CHSN000000", new int[]{R.drawable.xian_landmark_daylight,
//                    R.drawable.xian_landscape_daylight});
//            put(KEY_DEFAULT, new int[]{R.drawable.default_landscape_daylight});
//        }
//    };
//    private HashMap<String, int[]> cityImageMapNight = new HashMap<String, int[]>() {
//        {
//            put("CHSH000000", new int[]{R.drawable.shanghai_landmark_night,
//                    R.drawable.shanghai_landscape_night});
//            put("CHBJ000000", new int[]{R.drawable.beijing_landmark_night,
//                    R.drawable.beijing_landscape_night});
//            put("CHGD000000", new int[]{R.drawable.guangzhou_landmark_night,
//                    R.drawable.guangzhou_landscape_night});
//            put("CHSC000000", new int[]{R.drawable.chengdu_landmark_night,
//                    R.drawable.chengdu_landscape_night});
//            put("CHSN000000", new int[]{R.drawable.xian_landmark_night,
//                    R.drawable.xian_landscape_night});
//            put(KEY_DEFAULT, new int[]{R.drawable.default_landscape_night});
//        }
//    };
    private HashMap<String, int[]> cityImageMap = new HashMap<String, int[]>() {
        {
            put("CHSH000000", new int[]{R.drawable.shanghai_landmark,
                    R.drawable.shanghai_landscape});
            put("CHBJ000000", new int[]{R.drawable.beijing_landmark,
                    R.drawable.beijing_landscape});
            put("CHGD000000", new int[]{R.drawable.guangzhou_landmark,
                    R.drawable.guangzhou_landscape});
            put("CHSC000000", new int[]{R.drawable.chengdu_landmark,
                    R.drawable.chengdu_landscape});
            put("CHSN000000", new int[]{R.drawable.xian_landmark,
                    R.drawable.xian_landscape});
            put(KEY_DEFAULT, new int[]{R.drawable.default_landscape});
        }
    };
    private HashMap<String, int[]> cityImageMapNight = new HashMap<String, int[]>() {
        {
            put("CHSH000000", new int[]{R.drawable.shanghai_landmark_n,
                    R.drawable.shanghai_landscape_n});
            put("CHBJ000000", new int[]{R.drawable.beijing_landmark_n,
                    R.drawable.beijing_landscape_n});
            put("CHGD000000", new int[]{R.drawable.guangzhou_landmark_n,
                    R.drawable.guangzhou_landscape_n});
            put("CHSC000000", new int[]{R.drawable.chengdu_landmark_n,
                    R.drawable.chengdu_landscape_n});
            put("CHSN000000", new int[]{R.drawable.xian_landmark_n,
                    R.drawable.xian_landscape_n});
            put(KEY_DEFAULT, new int[]{R.drawable.default_landscape_n});
        }
    };

    public CitySiteView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CitySiteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.city_site, this);
        landscapeImageView = (ImageView) findViewById(R.id.landscape_image);
        landmarkImageView = (ImageView) findViewById(R.id.landmark_image);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);


    }

    private void setImageViewSize()
    {
        if (!isDefaultCity && isFirstTimeLayout)
        {
            ViewHelper.setScaleX(landmarkImageView, 0.85f);
            ViewHelper.setScaleY(landmarkImageView, 0.85f);
            ViewHelper.setScaleX(landscapeImageView, 0.85f);
            ViewHelper.setScaleY(landscapeImageView, 0.85f);


            if ("CHBJ000000".equals(cityCode))
            {
                ViewHelper.setTranslationY(landmarkImageView, DensityUtil.getScreenHeight() * 3 / 100 + DensityUtil.dip2px(5));
                ViewHelper.setTranslationY(landscapeImageView, DensityUtil.getScreenHeight() * 3 / 100 + DensityUtil.dip2px(5));
                ViewHelper.setTranslationX(landmarkImageView, -(int) (landmarkImageView.getWidth
                        () * 0.10) - DensityUtil.dip2px(15));
                ViewHelper.setTranslationX(landscapeImageView, -(int) (landscapeImageView.getWidth()
                        * 0.10) - DensityUtil.dip2px(15));
            }
            else
            {
                ViewHelper.setTranslationY(landmarkImageView, DensityUtil.dip2px(5));
                ViewHelper.setTranslationY(landscapeImageView, DensityUtil.dip2px(5));
                ViewHelper.setTranslationX(landmarkImageView, -(int) (landmarkImageView.getWidth
                        () * 0.10) - DensityUtil.dip2px(10));
                ViewHelper.setTranslationX(landscapeImageView, -(int) (landscapeImageView.getWidth()
                        * 0.10) - DensityUtil.dip2px(10));

            }

        }
        else if (isFirstTimeLayout)
        {
            landmarkImageView.setLayoutParams(new LayoutParams((int) (DensityUtil.dip2px(158) * 0.85),
                    (int) (DensityUtil.dip2px(140) * 0.85)));
            ViewHelper.setTranslationY(landmarkImageView, DensityUtil.getScreenHeight
                    () * 1 / 100 - DensityUtil.dip2px(3));

        }
    }


    private void startlandmarkViewAnimation()
    {
        Animation landmarkUp = AnimationUtils.loadAnimation(mContext, R.anim
                .in_from_bottom);
        landmarkUp.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });
        landmarkImageView.startAnimation(landmarkUp);
    }


    public void updateView(String cityCode) {
        this.cityCode = cityCode;
        isDataReady = true;
        landmarkImageView.setVisibility(View.INVISIBLE);
        landscapeImageView.setVisibility(View.INVISIBLE);

        switchTimeView();

        setImageViewSize();
        if (isFirstTimeLayout)
        {
            isFirstTimeLayout = false;
            Animation landscapeUp = AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom);
            landscapeImageView.setVisibility(View.VISIBLE);
            landscapeUp.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    landmarkImageView.setVisibility(View.VISIBLE);
                    startlandmarkViewAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {
                }
            });
            landscapeImageView.startAnimation(landscapeUp);
        }


    }

    public void switchTimeView() {
        if (!isDataReady)
            return;
        if (landmarkImageView == null || landscapeImageView == null)
            return;

        boolean isDaylight = AppConfig.shareInstance().isDaylight();
        if (cityImageMap.containsKey(cityCode)){
            landmarkImageView.setImageResource(isDaylight ? cityImageMap.get(cityCode)[0] :
                    cityImageMapNight.get(cityCode)[0]);
            landscapeImageView.setImageResource(isDaylight ? cityImageMap.get(cityCode)[1] :
                    cityImageMapNight.get(cityCode)[1]);

        }
        else
        {
            landmarkImageView.setImageResource(isDaylight ? cityImageMap.get(KEY_DEFAULT)[0] :
                    cityImageMapNight.get(KEY_DEFAULT)[0]);
            landmarkImageView.setLayoutParams(new LayoutParams((int) (DensityUtil.dip2px(158) * 0.85),
                    (int) (DensityUtil.dip2px(140) * 0.85)));
            isDefaultCity = true;

            ViewHelper.setTranslationY(landmarkImageView, DensityUtil.getScreenHeight() * 1 / 100 - DensityUtil.dip2px(3));
//            landscapeImageView.setImageResource(cityImageMap.get(KEY_DEFAULT)[1]);
        }

    }


    public void setCityView()
    {
        setImageViewSize();
        landscapeImageView.setVisibility(View.VISIBLE);
        landmarkImageView.setVisibility(View.VISIBLE);
    }

}
