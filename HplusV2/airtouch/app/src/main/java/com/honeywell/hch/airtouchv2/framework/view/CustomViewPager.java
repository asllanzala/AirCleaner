package com.honeywell.hch.airtouchv2.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.honeywell.hch.airtouchv2.app.airtouch.controller.location.ViewPager;

/**
 * Created by wuyuan on 15/5/12.
 */
public class CustomViewPager extends ViewPager
{

    private boolean isCanScroll = true;

    public CustomViewPager(Context context)
    {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll)
    {
        this.isCanScroll = isCanScroll;
    }


    @Override
    public void scrollTo(int x, int y)
    {
        if (isCanScroll)
        {
            super.scrollTo(x, y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
		/* return false;//super.onTouchEvent(arg0); */
        if (!isCanScroll)
            return false;
        else
        {
            return super.onTouchEvent(arg0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (!isCanScroll)
            return false;
        else
        {
            Log.e("hehe","onInterceptTouchEvent = " + super.onInterceptTouchEvent(arg0));
            return super.onInterceptTouchEvent(arg0);
        }

    }
}