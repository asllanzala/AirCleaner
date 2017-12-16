package com.honeywell.hch.airtouchv3.app.airtouch.controller.emotion;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;

/**
 * Created by wuyuan on 15/6/8.
 */
public class EmotionPagementOutdoorView extends RelativeLayout
{
    private Context mContext;

    public EmotionPagementOutdoorView(Context context)
    {
        super(context);
        mContext = context;
        initView();
    }

    public EmotionPagementOutdoorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        initView();

    }

    public EmotionPagementOutdoorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        initView();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private void initView()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.emotionpage_outdoor, this);


    }



}
