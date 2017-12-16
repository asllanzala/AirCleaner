package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wuyuan on 15/5/12.
 */
public class CustomTopViewPager extends TopViewPager
{

    private boolean isCanScroll = true;

    public CustomTopViewPager(Context context)
    {
        super(context);
    }

    public CustomTopViewPager(Context context, AttributeSet attrs)
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
            return super.onInterceptTouchEvent(arg0);
        }

    }
}