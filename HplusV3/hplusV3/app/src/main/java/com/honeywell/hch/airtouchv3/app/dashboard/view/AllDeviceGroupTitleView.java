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
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

/**
 * Created by Qian Jin on 10/15/15.
 */
public class AllDeviceGroupTitleView extends RelativeLayout {
    private TextView mGroupNameTextView;
    private ImageView mGroupImageView;
    private LinearLayout mTitleLayout;

    public AllDeviceGroupTitleView(Context context) {
        super(context);
        initView(context);
    }

    public AllDeviceGroupTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.group_title, this);

        mTitleLayout = (LinearLayout) findViewById(R.id.title_layout);
        mGroupNameTextView = (TextView) findViewById(R.id.group_name_tv);
        mGroupImageView = (ImageView) findViewById(R.id.group_iv);
    }

    public void setGroupName(String groupName) {
        mGroupNameTextView.setText(groupName);

        // set layout width and then set background
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mGroupNameTextView.measure(width, height);
        mGroupImageView.measure(width, height);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (mGroupNameTextView.getMeasuredWidth() + mGroupImageView.getMeasuredWidth()
                        + DensityUtil.dip2px(30), RelativeLayout.LayoutParams.WRAP_CONTENT);
        mTitleLayout.setLayoutParams(params);
        mTitleLayout.setBackgroundResource(R.drawable.all_device_group_title_bg);
    }
}
