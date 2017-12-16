package com.honeywell.hch.airtouchv3.app.airtouch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

import java.util.ArrayList;

/**
 * Custom view for Air Touch series device control panel.
 * Created by Qian Jin on 9/24/15.
 */
public class AirTouchLedView extends RelativeLayout {

    private final static int AIRTOUCH_S_MAX_POINT = 14;
    private final static int AIRTOUCH_P_MAX_POINT = 18;
    private int[] mLedCheckBoxIds = {R.id.cb1, R.id.cb2, R.id.cb3, R.id.cb4, R.id.cb5, R.id.cb6,
            R.id.cb7, R.id.cb8, R.id.cb9, R.id.cb10, R.id.cb11, R.id.cb12, R.id.cb13, R.id.cb14};

    private int[] mLedCheckBoxIdp = {R.id.cb17, R.id.cb1, R.id.cb2, R.id.cb3, R.id.cb4, R.id.cb5, R.id.cb6,
            R.id.cb7, R.id.cb8, R.id.cb9, R.id.cb10, R.id.cb11, R.id.cb12, R.id.cb13, R.id.cb14, R.id.cb15
            , R.id.cb16, R.id.cb18};

    private RelativeLayout mCheckboxLayout;
    private ArrayList<CheckBox> ledCheckBox = new ArrayList<>();
    private int mMaxLed;
    private Context mContext;

    private int[] mLedCheckBoxList;

    private int mFirstPointX = 0;
    private int mFirstPointY = 0;
    private int mEndPointX = 0;
    private int mEndPointY = 0;

    public AirTouchLedView(Context context) {
        super(context);
        mContext = context;

        initView(context);
    }

    public AirTouchLedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }


    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.device_control_panel, this);

        mCheckboxLayout = (RelativeLayout) findViewById(R.id.checkbox_layout);
    }

    public void initLedPosition(int maxNumber) {
        double angel;
        int offset;

        mMaxLed = maxNumber;
        mLedCheckBoxList = maxNumber == AIRTOUCH_S_MAX_POINT ?  mLedCheckBoxIds : mLedCheckBoxIdp;

        for (int id : mLedCheckBoxList) {
            CheckBox led = (CheckBox) findViewById(id);
            ledCheckBox.add(led);
        }
        if (maxNumber == AIRTOUCH_S_MAX_POINT){
            findViewById(R.id.cb15).setVisibility(View.GONE);
            findViewById(R.id.cb16).setVisibility(View.GONE);
            findViewById(R.id.cb17).setVisibility(View.GONE);
            findViewById(R.id.cb18).setVisibility(View.GONE);

            angel = Math.PI / (mMaxLed - 1);
            offset = 0;
        } else {
            angel = Math.PI / 11;
            offset = 3;
        }

        int x = DensityUtil.px2dip(DensityUtil.getScreenWidth() / 2 - 5);
        int y = DensityUtil.px2dip(DensityUtil.getScreenHeight() / 2);
        int r = DensityUtil.px2dip(DensityUtil.getScreenWidth() / 2 - 75);

        for (int i = 0; i < mMaxLed; i++) {
            RelativeLayout.LayoutParams params
                    = new RelativeLayout.LayoutParams(mCheckboxLayout.getLayoutParams());

            params.leftMargin = DensityUtil.dip2px((int)(x + r * Math.cos(Math.PI - angel * (i - offset))));
            params.topMargin = DensityUtil.dip2px((int)(y - r * Math.sin(angel * (i - offset))));
            ledCheckBox.get(i).setLayoutParams(params);

            if (i == 0) {
                mFirstPointX = params.leftMargin;
                mFirstPointY = params.topMargin;
            }
            if (i == mMaxLed - 1) {
                mEndPointX = params.leftMargin;
                mEndPointY = params.topMargin;
            }
        }
    }

    public int getFirstPointX() {
        return mFirstPointX;
    }

    public void setFirstPointX(int mFirstPointX) {
        this.mFirstPointX = mFirstPointX;
    }

    public int getFirstPointY() {
        return mFirstPointY;
    }

    public void setFirstPointY(int mFirstPointY) {
        this.mFirstPointY = mFirstPointY;
    }

    public int getEndPointX() {
        return mEndPointX;
    }

    public void setEndPointX(int mEndPointX) {
        this.mEndPointX = mEndPointX;
    }

    public int getEndPointY() {
        return mEndPointY;
    }

    public void setEndPointY(int mEndPointY) {
        this.mEndPointY = mEndPointY;
    }

    public ArrayList<CheckBox> getLedCheckBox() {
        return ledCheckBox;
    }
}
