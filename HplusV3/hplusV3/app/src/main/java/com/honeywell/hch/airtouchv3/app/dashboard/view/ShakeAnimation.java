package com.honeywell.hch.airtouchv3.app.dashboard.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.util.List;

/**
 * Created by Qian Jin on 10/18/15.
 */
public class ShakeAnimation {
    private List<View> mViews;

    private float mDensity;
    private int mCount = 0;
    private boolean mNeedShake = false;
    private boolean mStartShake = false;
    private static final int ICON_WIDTH = 80;
    private static final int ICON_HEIGHT = 94;
    private static final float DEGREE_0 = 1.8f;
    private static final float DEGREE_1 = -2.0f;
    private static final float DEGREE_2 = 2.0f;
    private static final float DEGREE_3 = -1.5f;
    private static final float DEGREE_4 = 1.5f;
    private static final int ANIMATION_DURATION = 80;

    public ShakeAnimation(float density) {
        mDensity = density;
    }

    public void startShake(List<View> views) {
        if (mStartShake)
            return;

        mViews = views;
        mStartShake = true;
        mNeedShake = true;

        float rotate = 0;
        int c = mCount++ % 5;
        if (c == 0) {
            rotate = DEGREE_0;
        } else if (c == 1) {
            rotate = DEGREE_1;
        } else if (c == 2) {
            rotate = DEGREE_2;
        } else if (c == 3) {
            rotate = DEGREE_3;
        } else {
            rotate = DEGREE_4;
        }
        final RotateAnimation mra = new RotateAnimation(rotate, -rotate,
                ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);
        final RotateAnimation mrb = new RotateAnimation(-rotate, rotate,
                ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);

        mra.setDuration(ANIMATION_DURATION);
        mrb.setDuration(ANIMATION_DURATION);

        mra.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mNeedShake) {
                    mra.reset();
                    for (View view : mViews)
                        view.startAnimation(mrb);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });

        mrb.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mNeedShake) {
                    mrb.reset();
                    for (View view : mViews)
                        view.startAnimation(mra);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });

        for (View view : mViews)
            view.startAnimation(mra);
    }

    public void stopShake() {
        mCount = 0;
        mNeedShake = false;
        mStartShake = false;
    }

}
