package com.honeywell.hch.airtouchv3.app.airtouch.controller.emotion;

import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * Created by wuyuan on 15/6/26.
 */
public class PathAnimation  extends Animation
{
    public interface IAnimationUpdateListener {
        public void onAnimUpdate(int index);
    }

    private int mFromIndex;
    private int mEndIndex;
    private boolean mReverse;
    private IAnimationUpdateListener mListener;

    public PathAnimation(int fromIndex, int endIndex, boolean reverse,
                         IAnimationUpdateListener listener) {
        mFromIndex = fromIndex;
        mEndIndex = endIndex;
        mReverse = reverse;
        mListener = listener;
    }

    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        return super.getTransformation(currentTime, outTransformation);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        Interpolator interpolator = this.getInterpolator();
        if (null != interpolator) {
            float value = interpolator.getInterpolation(interpolatedTime);
            interpolatedTime = value;
        }
        if (mReverse) {
            interpolatedTime = 1.0f - interpolatedTime;
        }
        int currentIndex = (int) (mFromIndex + (mEndIndex - mFromIndex) * interpolatedTime);

        if (null != mListener) {
            mListener.onAnimUpdate(currentIndex);
        }
    }
}
