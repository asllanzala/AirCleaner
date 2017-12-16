package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.dashboard.view.AirTouchView;
import com.honeywell.hch.airtouchv3.app.dashboard.view.AllDeviceAirTouchView;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.webservice.task.TurnOnAllDeviceTask;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyuan on 10/16/15.
 */
public class TurnOnAllDeviceActivity extends BaseHasBackgroundActivity {

    public final static String IS_NEED_TO_CURRENT = "is_need_to_current";

    private final static int DEVICE_NUMBER_PER_LINE = 3;

    private TextView mTurnOnTextView;
    private TextView mNoTextView;

    private List<AllDeviceAirTouchView> mUnGroupHomeDeviceViews = new ArrayList<>();
    private LinearLayout mUnGroupLayout;
    private RelativeLayout mLessThanFourLayout;
    private List<Integer> mHomeDeviceIdList = new ArrayList<>();

    private List<HomeDevice> mHomeDeviceList = new ArrayList<>();

    @Override
    public void onDestroy() {
        super.onDestroy();
        sotpTyperTimer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.turn_on_all_device);

        mHomeDeviceIdList = getIntent().getIntegerArrayListExtra(AirTouchConstants.DEVICE_ID_LIST);

        for (Integer deviceId : mHomeDeviceIdList){
            HomeDevice homeDevice = AppManager.shareInstance().getDeviceWithDeviceId(deviceId);
            if (homeDevice != null){
                mHomeDeviceList.add(homeDevice);
            }
        }

        initDynamicBackground();
        initView();
        layoutDevice();
    }

    private void initView(){
        mTurnOnTextView = (TextView)findViewById(R.id.turn_on_text);
        mTurnOnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TurnOnAllDeviceTask turnOnAllDeviceTask = new TurnOnAllDeviceTask(mHomeDeviceIdList, AirTouchView.MODE_AUTO,null);
                AsyncTaskExecutorUtil.executeAsyncTask(turnOnAllDeviceTask);
                finish();
            }
        });

        mNoTextView = (TextView)findViewById(R.id.no_text);
        mNoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUnGroupLayout = (LinearLayout)findViewById(R.id.all_need_open_device_layout);
        mLessThanFourLayout = (RelativeLayout)findViewById(R.id.less_than_four_layout);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showAnimation(View viewLayout){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        viewLayout.startAnimation(alphaAnimation);
        viewLayout.setVisibility(View.VISIBLE);
    }

    private void layoutDevice(){
        RelativeLayout oneLine = null;
        boolean isLessThanFour = mHomeDeviceIdList.size() <= 3 ? true : false ;
        if (isLessThanFour){
            mLessThanFourLayout.setVisibility(View.VISIBLE);
            mUnGroupLayout.setVisibility(View.GONE);
        }
        else{
            mLessThanFourLayout.setVisibility(View.GONE);
            mUnGroupLayout.setVisibility(View.VISIBLE);
        }
        int deviceViewLines = (mHomeDeviceIdList.size() - 1) / DEVICE_NUMBER_PER_LINE + 1;
        for (int i = 1; i <= deviceViewLines; i++) {
            if (i == deviceViewLines){
                oneLine = getOneLine(i, true,  mHomeDeviceList,isLessThanFour);
            }
            else{
                oneLine = getOneLine(i, false, mHomeDeviceList,isLessThanFour);
            }
            if (isLessThanFour){
                mLessThanFourLayout.addView(oneLine);
            }
            else{
                mUnGroupLayout.addView(oneLine);
            }
        }
    }


    private RelativeLayout getOneLine(int line, boolean isReachMaxLine, List<HomeDevice> devices,boolean isLessThanFour) {
        RelativeLayout deviceOneLineLayout = new RelativeLayout(this);
        createDeviceViews(line, isReachMaxLine, devices);
        int maxHeight = getDeviceMaxHeight(line,  isReachMaxLine, devices);
        int maxNumber = isReachMaxLine ? devices.size() : line * DEVICE_NUMBER_PER_LINE;
        // add devices and selections to layout one by one
        for (int i = (line - 1) * DEVICE_NUMBER_PER_LINE; i < maxNumber; i++) {
            int deviceWidth = 0;
            int deviceHeight = 0;

            deviceWidth = mUnGroupHomeDeviceViews.get(i).getDeviceWidth();
            deviceHeight = mUnGroupHomeDeviceViews.get(i).getDeviceHeight();

            RelativeLayout.LayoutParams deviceParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            // device leftMargin, topMargin
            if (isLessThanFour){
                deviceParams.leftMargin = (deviceWidth + DensityUtil.dip2px(10)) * (i % DEVICE_NUMBER_PER_LINE);
            }
            else{
                deviceParams.leftMargin = DensityUtil.dip2px(30)
                        + (deviceWidth + DensityUtil.dip2px(10)) * (i % DEVICE_NUMBER_PER_LINE);
            }
            deviceParams.topMargin = DensityUtil.dip2px(30)
                    + maxHeight - deviceHeight;
            // device bottomMargin
            if (isReachMaxLine) {
                deviceParams.bottomMargin = DensityUtil.dip2px(30);
            }

            mUnGroupHomeDeviceViews.get(i).setLayoutParams(deviceParams);
            deviceOneLineLayout.addView(mUnGroupHomeDeviceViews.get(i));

        }
        return deviceOneLineLayout;
    }

    private void createDeviceViews(int line,boolean isReachMaxLine, List<HomeDevice> devices) {
        int maxNumber = isReachMaxLine ? devices.size() : line * DEVICE_NUMBER_PER_LINE;
        for (int i = (line - 1) * DEVICE_NUMBER_PER_LINE; i < maxNumber; i++) {
            if (devices.get(i) != null){
                // create device views
                AllDeviceAirTouchView deviceView = new AllDeviceAirTouchView(this);
                deviceView.updateView(devices.get(i));

                mUnGroupHomeDeviceViews.add(deviceView);
                mUnGroupHomeDeviceViews.get(i).setUnGroupDeviceIndex(i);
            }

        }

    }

    private int getDeviceMaxHeight(int line, boolean isReachMaxLine, List<HomeDevice> devices) {
        int maxHeight = 0;
        int maxNumber = isReachMaxLine ? devices.size() : line * DEVICE_NUMBER_PER_LINE;
        for (int i = (line - 1) * DEVICE_NUMBER_PER_LINE; i < maxNumber; i++) {
            // calculate the max height of the device in one line
            int measuredHeight;

            measuredHeight = mUnGroupHomeDeviceViews.get(i).getDeviceHeight();
            maxHeight = (measuredHeight > maxHeight ?
                    measuredHeight : maxHeight);
        }
        return maxHeight;
    }

    private void sotpTyperTimer() {
        if (mUnGroupHomeDeviceViews != null) {
            for (AllDeviceAirTouchView allDeviceAirTouchView : mUnGroupHomeDeviceViews) {
                allDeviceAirTouchView.stopTyperTimer( allDeviceAirTouchView.getmPm25TextView(), allDeviceAirTouchView.getmTvocTextView());
            }
        }
    }
}
