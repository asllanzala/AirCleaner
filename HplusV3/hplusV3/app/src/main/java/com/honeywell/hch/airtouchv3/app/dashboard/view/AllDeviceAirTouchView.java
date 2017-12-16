package com.honeywell.hch.airtouchv3.app.dashboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.view.TypeTextView;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

/**
 * Custom view for AirTouchS in AllDevice.
 * Created by Qian Jin on 10/10/15.
 */
public class AllDeviceAirTouchView extends AirTouchView {
    private ImageView mDeviceImageView;
    private ImageView mFanImageView;
    private TextView mDeviceNameTextView;
    private TextView mDeviceStatusTextView;
    private TypeTextView mPm25TextView;
    private TypeTextView mTvocTextView;
    private LinearLayout mTvocLayout;

    private Context mContext;
    private HomeDevice mHomeDevice;
    // index in the list of group/un-group devices
    private int mGroupDeviceIndex;
    private int mUnGroupDeviceIndex;
    private final int WHERE = AirTouchConstants.FROM_DEVICE_PAGE;

    public AllDeviceAirTouchView(Context context) {
        super(context);
        mContext = context;

        initView(mContext);
    }

    public AllDeviceAirTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initView(mContext);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.all_device_airtouch, this);

        mDeviceNameTextView = (TextView) findViewById(R.id.device_name_tv);
        mDeviceStatusTextView = (TextView) findViewById(R.id.device_status_tv);
        mFanImageView = (ImageView) findViewById(R.id.fan_iv);
        mDeviceImageView = (ImageView) findViewById(R.id.air_touch_device_view);
        mPm25TextView = (TypeTextView) findViewById(R.id.pm25_value);
        mTvocTextView = (TypeTextView) findViewById(R.id.tvoc_value);
        mTvocLayout = (LinearLayout) findViewById(R.id.airP_device_layout);
        setPosition();
    }

    private void setPosition() {
        RelativeLayout.LayoutParams deviceParams
                = new RelativeLayout.LayoutParams(mDeviceImageView.getLayoutParams());
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mFanImageView.measure(width, height);
        deviceParams.leftMargin = mFanImageView.getMeasuredWidth() + DensityUtil.dip2px(5);
        mDeviceImageView.setLayoutParams(deviceParams);
    }

    public void updateView(HomeDevice homeDevice) {
        // store device data
        mHomeDevice = homeDevice;

        mDeviceNameTextView.setText(mHomeDevice.getDeviceInfo().getName());
        updateDeviceView();

        updateDeviceValue(mContext, mHomeDevice, mPm25TextView, WHERE);
        updateTvocValue(mContext, mHomeDevice, mTvocTextView, WHERE);
        updateRunStatusAndFan(mContext, mHomeDevice, mDeviceStatusTextView, mFanImageView);
    }

    private void updateDeviceView() {
        if (AppManager.shareInstance().isAirtouchs(mHomeDevice.getDeviceType())){
            mDeviceImageView.setBackgroundResource(R.drawable.all_device_airtouchs);
            mTvocLayout.setVisibility(View.GONE);
        }
        else if(AppManager.shareInstance().isAirtouchP(mHomeDevice.getDeviceType())){
            mDeviceImageView.setBackgroundResource(R.drawable.all_device_airtouchp);
            mTvocLayout.setVisibility(View.VISIBLE);
        }
        else if (AppManager.shareInstance().isAirtouch450(mHomeDevice.getDeviceType())){
            mDeviceImageView.setBackgroundResource(R.drawable.all_device_450);
            mTvocLayout.setVisibility(View.VISIBLE);
        }
    }

    public int getDeviceHeight() {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mDeviceImageView.measure(width, height);
        return mDeviceImageView.getMeasuredHeight();
    }

    public int getDeviceWidth() {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mDeviceImageView.measure(width, height);
        return mDeviceImageView.getMeasuredWidth()
                + mFanImageView.getMeasuredWidth() + DensityUtil.dip2px(5);
    }

    public HomeDevice getHomeDevice() {
        return mHomeDevice;
    }

    public ImageView getDeviceImageView() {
        return mDeviceImageView;
    }

    public int getUnGroupDeviceIndex() {
        return mUnGroupDeviceIndex;
    }

    public void setUnGroupDeviceIndex(int unGroupDeviceIndex) {
        mUnGroupDeviceIndex = unGroupDeviceIndex;
    }

    public int getGroupDeviceIndex() {
        return mGroupDeviceIndex;
    }

    public void setGroupDeviceIndex(int groupDeviceIndex) {
        mGroupDeviceIndex = groupDeviceIndex;
    }

    public TypeTextView getmPm25TextView(){
        return mPm25TextView;
    }

    public TypeTextView getmTvocTextView(){
        return mTvocTextView;
    }
}
