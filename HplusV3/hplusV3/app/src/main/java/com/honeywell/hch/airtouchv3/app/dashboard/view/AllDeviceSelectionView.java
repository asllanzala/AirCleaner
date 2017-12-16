package com.honeywell.hch.airtouchv3.app.dashboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

/**
 * Created by Qian Jin on 10/15/15.
 */
public class AllDeviceSelectionView extends RelativeLayout {
    private Button mGroupButton;
    private Button mDefaultButton;
    private Button mDeleteButton;
    private Boolean mIsGrouping;
    private ImageView mLine1;
    private RelativeLayout mWholeLayout;
    private int m3SelectionWidth = 0;
    private int m2SelectionWidth = 0;
    private Context mContext;

    public AllDeviceSelectionView(Context context) {
        super(context);
        initView(context);

        mContext = context;
    }

    public AllDeviceSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        mContext = context;
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.group_selection, this);

        mWholeLayout = (RelativeLayout) findViewById(R.id.selection_layout);
        measureWholeWidth();
        mGroupButton = (Button) findViewById(R.id.group_btn);
        mDefaultButton = (Button) findViewById(R.id.default_btn);
        mDeleteButton = (Button) findViewById(R.id.delete_btn);
        mLine1 = (ImageView) findViewById(R.id.line1);
    }

    public Button getGroupButton() {
        return mGroupButton;
    }

    public Button getDefaultButton() {
        return mDefaultButton;
    }

    public Button getDeleteButton() {
        return mDeleteButton;
    }

    public void setGroupButtonEnable(Boolean isEnable) {
        mGroupButton.setClickable(isEnable);
        if (isEnable) {
            mGroupButton.setVisibility(View.VISIBLE);
            mLine1.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    (m3SelectionWidth, mWholeLayout.getMeasuredHeight());
            mWholeLayout.setLayoutParams(params);
        } else {
            mGroupButton.setVisibility(View.GONE);
            mLine1.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    (m2SelectionWidth, mWholeLayout.getMeasuredHeight());
            mWholeLayout.setLayoutParams(params);
        }
    }

    public void setGroupText(Boolean isGrouping) {
        mIsGrouping = isGrouping;
        if (isGrouping) {
            mGroupButton.setText(getResources().getString(R.string.selection_group));
        } else {
            mGroupButton.setText(getResources().getString(R.string.selection_unGroup));
        }
    }

    public Boolean getIsGrouping() {
        return mIsGrouping;
    }

    public void setSelectionBackground(int id) {
        mWholeLayout.setBackgroundResource(id);
    }

    private void measureWholeWidth() {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mWholeLayout.measure(width, height);

        m3SelectionWidth = mWholeLayout.getMeasuredWidth() + DensityUtil.dip2px(25);
        m2SelectionWidth = m3SelectionWidth * 2 / 3;
    }
}
