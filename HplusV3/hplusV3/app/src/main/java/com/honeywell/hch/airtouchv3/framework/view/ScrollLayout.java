package com.honeywell.hch.airtouchv3.framework.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;

/**
 * Created by nan.liu on 3/20/15.
 */
public class ScrollLayout extends ViewGroup {
    private static final String TAG = "AirTouchScrollLayout";

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mCurScreen = 0;
    private int mDefaultScreen = 0;
    private int mCustomScreen = 0;
    private int mCustomScreenHeight = 0;
    private float mCustomScreenScale = 1;
    private int[] mScreenHeights =  {0};

    private int mTouchSlop;
    private float mLastMotionX;
    private float mLastMotionY;
    private OnViewChangeListener mOnViewChangeListener;
    private OnFirstPageDownListener mOnFirstPageDownListener;
    private OnViewTouchListener mOnViewTouchListener;
    private OnViewScrollingListener mScrollingListener;

    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int SNAP_VELOCITY = 600;
    private int mTouchState = TOUCH_STATE_REST;

    private boolean isScroll = true;

    public void setIsScroll(boolean b) {
        this.isScroll = b;
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        int customScreenScalePercent = 100;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollLayout);
            mCurScreen = typedArray.getInt(R.styleable.ScrollLayout_defaultScreen, mDefaultScreen);
            mCustomScreen = typedArray.getInt(R.styleable.ScrollLayout_customScreen, 0);
            mCustomScreenHeight = typedArray.getInt(R.styleable.ScrollLayout_customScreenHeight, 0);
            customScreenScalePercent = typedArray.getInt(R.styleable
                    .ScrollLayout_customScreenScale, 100);
            typedArray.recycle();
        } else {
            mCurScreen = mDefaultScreen;
            mCustomScreen = 0;
            mCustomScreenHeight = 0;
        }
        mCustomScreenScale = customScreenScalePercent / 100f;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childTop = 0;
        final int childCount = getChildCount();
        mScreenHeights = new int[childCount];
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childHeight = childView.getMeasuredHeight();
                mScreenHeights[i] = childHeight;
                childView.layout(0, childTop, childView.getMeasuredWidth(),
                        childTop + childHeight);
                childTop += childHeight;
            }
        }
        int initHeight = 0;
        for (int i = 0; i < mCurScreen; i++) {
            initHeight += mScreenHeights[i];
        }
        if (mCurScreen == mCustomScreen)
            initHeight -= DensityUtil.dip2px(mCustomScreenHeight);
        scrollTo(0, initHeight);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only can run at EXACTLY mode!");
        }

        // The children are given the same width and height as the scrollLayout
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (i != mCustomScreen) {
                getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
            } else {
                int realHeightMeasureSpec = (heightMeasureSpec - DensityUtil.dip2px
                        (mCustomScreenHeight));
                if (mCustomScreenScale < 1)
                    realHeightMeasureSpec *= mCustomScreenScale;
                getChildAt(i).measure(widthMeasureSpec, realHeightMeasureSpec);
            }
        }
    }

    /**
     * According to the position of current layout scroll to the destination
     * page.
     */
    public void snapToDestination() {
//        int averageHeight = 0;
//        for (int i = 0; i < mCurScreen; i++) {
//            averageHeight += mScreenHeights[i];
//        }
//        averageHeight /= getChildCount();
//        if (averageHeight != 0) {
//            final int destScreen = (getScrollY() + averageHeight / 2) / averageHeight;
//            snapToScreen(destScreen);
//        }
        final int screenHeight = getHeight();
        final int destScreen = (getScrollY() + screenHeight / 2) / screenHeight;
        snapToScreen(destScreen);
    }

    public void snapToScreen(int whichScreen) {
        if (!isScroll) {
            this.setToScreen(whichScreen);
            return;
        }
        scrollToScreen(whichScreen);
    }

    public void scrollToScreen(int whichScreen) {
        // get the valid layout page
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        int screenTop = 0;
        for (int i = 0; i < whichScreen; i++) {
            screenTop += mScreenHeights[i];
        }
        if (getScrollY() != screenTop) {
            final int delta = screenTop - getScrollY() - ((whichScreen != mCustomScreen) ? 0 :
                    DensityUtil.dip2px(mCustomScreenHeight));
            mScroller.startScroll(0, getScrollY(), 0, delta,
                    Math.abs(delta) * 1);
            mCurScreen = whichScreen;
            invalidate(); // Redraw the layout

            if (mOnViewChangeListener != null) {
                mOnViewChangeListener.OnViewChange(mCurScreen);
            }
        }
    }

    public void setToScreen(int whichScreen) {
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        mCurScreen = whichScreen;
        int initHeight = 0;
        for (int i = 0; i < mCurScreen; i++) {
            initHeight += mScreenHeights[i];
        }
        if (mCurScreen == mCustomScreen)
            initHeight -= DensityUtil.dip2px(mCustomScreenHeight);
        scrollTo(0, initHeight);
        if (mOnViewChangeListener != null) {
            mOnViewChangeListener.OnViewChange(mCurScreen);
        }
    }

    public int getCurScreen() {
        return mCurScreen;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isScroll) {
            if (mOnViewTouchListener != null) {
                mOnViewTouchListener.OnViewTouch(mCurScreen);
            }
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastMotionX - x);
                int deltaY = (int) (mLastMotionY - y);
                LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "event : move deltaX" + deltaX + " " +
                        "deltaY" + deltaY);
                if (Math.abs(deltaY) < 200 && Math.abs(deltaX) > 10)
                    break;
                if ((deltaY < 0 && getCurScreen() == 0) || (deltaY > 0 && getCurScreen() ==
                        (getChildCount() - 1)))
                    break;
                mLastMotionY = y;
                mLastMotionX = x;
                if (mScrollingListener != null)
                {
                    mScrollingListener.onSrcollY(getScrollY() + deltaY);
                }
                scrollBy(0, deltaY);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityY = (int) velocityTracker.getYVelocity();
                if (velocityY > SNAP_VELOCITY && mCurScreen > 0) {
                    // Fling enough to move left
                    snapToScreen(mCurScreen - 1);
                } else if (velocityY < -SNAP_VELOCITY
                        && mCurScreen < getChildCount() - 1) {
                    // Fling enough to move right

                    snapToScreen(mCurScreen + 1);
                } else if (velocityY > SNAP_VELOCITY && mCurScreen == 0) {
                    if (mOnFirstPageDownListener != null)
                        mOnFirstPageDownListener.onPageDwon();
                    // when in the control page, scroll down to exit.
                } else {

                    snapToDestination();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isScroll) {
            return false;
        }
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "onInterceptTouchEvent:move");
                float dy = mLastMotionY - y;
                final int xDiff = (int) Math.abs(mLastMotionX - x);
                final int yDiff = (int) Math.abs(mLastMotionY - y);
                LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, dy + "ddddd" + mCurScreen);
                if (yDiff > mTouchSlop && xDiff < mTouchSlop && !(dy < 0 && mCurScreen == 0)) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "onInterceptTouchEvent:down");
                mLastMotionX = x;
                mLastMotionY = y;
                LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "onInterceptTouchEvent : mScroller" +
                        ".isFinished()" + mScroller.isFinished());
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        return mTouchState != TOUCH_STATE_REST;
    }

    /**
     * set the listener of view change
     *
     * @param listener
     */
    public void setOnViewChangeListener(OnViewChangeListener listener) {
        mOnViewChangeListener = listener;
    }

    /**
     * set the listener of device control close
     *
     * @param listener
     */
    public void setOnFirstPageDownListener(OnFirstPageDownListener listener) {
        mOnFirstPageDownListener = listener;
    }

    /**
     * the listener of view scrolling
     */
    public interface OnViewChangeListener {
        public void OnViewChange(int index);
    }

    /**
     * set the listener of view scrolling
     *
     * @param listener
     */
    public void setOnViewScrollingListener(OnViewScrollingListener listener) {
        mScrollingListener = listener;
    }

    /**
     * the listener of view change
     */
    public interface OnViewScrollingListener {
        public void onSrcollY(float scrollY);
    }

    /**
     * the listener of device control close
     */
    public interface OnFirstPageDownListener {
        public void onPageDwon();
    }

    /**
     * set the listener of view change
     *
     * @param listener
     */
    public void setOnViewTouchListener(OnViewTouchListener listener) {
        mOnViewTouchListener = listener;
    }

    /**
     * the listener of view change
     */
    public interface OnViewTouchListener {
        public void OnViewTouch(int index);
    }
}
