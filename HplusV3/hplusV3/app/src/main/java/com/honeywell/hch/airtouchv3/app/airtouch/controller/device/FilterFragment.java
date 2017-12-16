package com.honeywell.hch.airtouchv3.app.airtouch.controller.device;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.CapabilityResponse;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseRequestFragment;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.RunStatus;
import com.honeywell.hch.airtouchv3.framework.view.ExpandAnimation;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.nineoldandroids.view.ViewHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jin Qian on 2/13/2015.
 */
public class FilterFragment extends BaseRequestFragment {

    private static final String TAG = "AirTouchFilter";
    private static final String ARG_DEVICE = "device";
    private static final String NO_AUTHORIZE_STRING = "ActivationFailed";

    private final String BASE_PURCHASE = "http://hch.honeywell.com.cn/filter/filterpurchase.html?";
    private final String Air_PREMIUM_PURSUE = BASE_PURCHASE + "version=2&model=polytechfilter&product=KJ700G-PAC2127W&country=China";
    private String[] productUrls = {BASE_PURCHASE + "version=2&model=prefilter&product=PAC35M2101S&country=China",
            BASE_PURCHASE + "version=2&model=hepafilter&product=PAC35M2101S&country=China",
            BASE_PURCHASE + "version=2&model=chemicalfilter&product=PAC35M2101S&country=China"};
    private String[] AIR_TOUCH450_PURSUE = {BASE_PURCHASE + "version=2&model=prefilter&product=KJ450F-PAC2022S&country=China",
            BASE_PURCHASE + "version=2&model=hisivcompositefilter&product=KJ450F-PAC2022S&country=China"};
    
    private RelativeLayout tutorialMask;
    private TextView firmwareTextView;
    private TextView preFilterTextView, pm25FilterTextView, hisivFilterTextView;
    private FrameLayout preFilterPurchaseLayout, pm25FilterPurchaseLayout, hisivFilterPurchaseLayout;
    private ProgressBar preFilterProgressBar, pm25FilterProgressBar, hisivFilterProgressBar;
    private LinearLayout preFilterLayout, preFilterDescriptionLayout, pm25FilterLayout;
    private LinearLayout pm25FilterDescriptionLayout, hisivFilterLayout, hisivFilterDescriptionLayout;
    private TextView preFilterPercentTextView, pm25FilterPercentTextView, hisivFilterPercentTextView;
//    private static ProgressDialog mDialog;
    private Animation alphaOffAnimation;
    private OnControlTutorialRemovedListener mOnControlTutorialRemovedListener;

    private Timer preFilterTimer, pm25FilterTimer, hisivFilterTimer;
    private TimerTask preFilterTask, pm25FilterTask, hisivFilterTask;
    private ExpandAnimation preExpandAnimation, pm25ExpandAnimation, hisivExpandAnimation;

    private boolean isPreFilterExpand, isPm25FilterExpand, isHisivFilterExpand;
    private static int preFilterLife, pm25FilterLife, hisivFilterLife;
    private static int preFilterLifeRaw, pm25FilterLifeRaw, hisivFilterLifeRaw;
    private int bar1, bar2, bar3;
    private String firmwareVersion;

    private FragmentActivity mActivity;
    private String mSessionId;
    private int mDeviceId;
    private static HomeDevice mCurrentDevice = null;
    private OnFilterAnimationListener mOnFilterAnimationListener;
    private LinearLayout mSecondFilterLayout;
    private LinearLayout mThirdFilterLayout;

    private RelativeLayout mNoAuthorizeFilterLayout;
    private LinearLayout mPreFilterLinearLayout;
    private LinearLayout mPreFilterTopLinearLayout;
    private boolean isNeedToShowNoAuthorize = false;
    private LinearLayout progressLayout2;

    private LinearLayout progressLayout;

    private LinearLayout mPreFilterSecondTopLinearLayout;

    private TextView mFirstFilterNameText;
    private TextView mFirstFilterDesText;

    private TextView mHisivFilter450Title;
    private TextView mHisivFilter450Description;

    private TextView mPreDesTitleTextView;
    private TextView mPm25DesTitleTextView;
    private TextView mHisvDesTitleTextView;

    public static FilterFragment newInstance(HomeDevice homeDevice) {
        FilterFragment fragment = new FilterFragment();
        mCurrentDevice = homeDevice;

        if (((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus() != null){
            preFilterLifeRaw = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getFilter1Runtime();
            pm25FilterLifeRaw = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getFilter2Runtime();
            hisivFilterLifeRaw = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getFilter3Runtime();
        }

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;

        mDeviceId = mCurrentDevice.getDeviceInfo().getDeviceID();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, null);
        initView(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHasNullPoint()) {
            initFilter();
        }

        // India version
        if (!AppConfig.shareInstance().isIndiaAccount()) {
            preFilterPurchaseLayout.setVisibility(View.VISIBLE);
            pm25FilterPurchaseLayout.setVisibility(View.VISIBLE);
            hisivFilterPurchaseLayout.setVisibility(View.VISIBLE);
        } else {
            preFilterPurchaseLayout.setVisibility(View.INVISIBLE);
            pm25FilterPurchaseLayout.setVisibility(View.INVISIBLE);
            hisivFilterPurchaseLayout.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        stopFilterBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AppConfig.isFilterScrollPage = false;
    }

    private void initView(View view) {
        tutorialMask = (RelativeLayout) view.findViewById(R.id.tutorial_mask);
        firmwareTextView = (TextView) view.findViewById(R.id.firmware);
        preFilterTextView = (TextView) view.findViewById(R.id.pre_filter_tv);
        preFilterTextView.setOnClickListener(preFilterExpandClick);
        pm25FilterTextView = (TextView) view.findViewById(R.id.pm25_filter_tv);
        pm25FilterTextView.setOnClickListener(pm25FilterExpandClick);
        hisivFilterTextView = (TextView) view.findViewById(R.id.hisiv_filter_tv);
        hisivFilterTextView.setOnClickListener(hisivFilterExpandClick);
        preFilterPurchaseLayout = (FrameLayout) view.findViewById(R.id.pre_filter_purchase_layout);
        preFilterPurchaseLayout.setOnClickListener(preFilterPurchase);
        pm25FilterPurchaseLayout = (FrameLayout) view.findViewById(R.id.pm25_filter_purchase_layout);
        pm25FilterPurchaseLayout.setOnClickListener(pm25FilterPurchase);
        hisivFilterPurchaseLayout = (FrameLayout) view.findViewById(R.id.hisiv_filter_purchase_layout);
        hisivFilterPurchaseLayout.setOnClickListener(hisivFilterPurchase);
        preFilterProgressBar = (ProgressBar) view.findViewById(R.id.pre_filter);
        pm25FilterProgressBar = (ProgressBar) view.findViewById(R.id.pm25_filter);
        hisivFilterProgressBar = (ProgressBar) view.findViewById(R.id.hisiv_filter);
        preFilterPercentTextView = (TextView) view.findViewById(R.id.pre_filter_percent_tv);
        pm25FilterPercentTextView = (TextView) view.findViewById(R.id.pm25_filter_percent_tv);
        hisivFilterPercentTextView = (TextView) view.findViewById(R.id.hisiv_filter_percent_tv);
        preFilterLayout = (LinearLayout) view.findViewById(R.id.pre_filter_text_bar_layout);
        pm25FilterLayout = (LinearLayout) view.findViewById(R.id.pm25_filter_text_bar_layout);
        hisivFilterLayout = (LinearLayout) view.findViewById(R.id.hisiv_filter_text_bar_layout);
        preFilterLayout.setOnClickListener(preFilterExpandClick);
        pm25FilterLayout.setOnClickListener(pm25FilterExpandClick);
        hisivFilterLayout.setOnClickListener(hisivFilterExpandClick);
        preFilterDescriptionLayout = (LinearLayout) view.findViewById(R.id.pre_filter_desc_layout);
        pm25FilterDescriptionLayout = (LinearLayout) view.findViewById(R.id.pm25_filter_desc_layout);
        hisivFilterDescriptionLayout = (LinearLayout) view.findViewById(R.id.hisiv_filter_desc_layout);
        ((LinearLayout.LayoutParams) preFilterDescriptionLayout.getLayoutParams()).bottomMargin = -200;
        ((LinearLayout.LayoutParams) pm25FilterDescriptionLayout.getLayoutParams()).bottomMargin = -200;
        ((LinearLayout.LayoutParams) hisivFilterDescriptionLayout.getLayoutParams()).bottomMargin = -200;
        isPreFilterExpand = false;
        isPm25FilterExpand = false;
        isHisivFilterExpand = false;
        alphaOffAnimation = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim.control_alpha);

        mSecondFilterLayout = (LinearLayout)view.findViewById(R.id.pm25_filter_layout);
        mThirdFilterLayout = (LinearLayout)view.findViewById(R.id.hisiv_filter_layout);

        mNoAuthorizeFilterLayout = (RelativeLayout)view.findViewById(R.id.no_authorize_filter_layout);
        mNoAuthorizeFilterLayout.setVisibility(View.GONE);
        mPreFilterLinearLayout = (LinearLayout)view.findViewById(R.id.pre_filter_text_bar_layout);
        mPreFilterTopLinearLayout = (LinearLayout)view.findViewById(R.id.filter_layout);

        mPreFilterSecondTopLinearLayout = (LinearLayout)view.findViewById(R.id.pre_filter_layout);
        progressLayout2 = (LinearLayout)view.findViewById(R.id.pre_filter_main_layout);
        progressLayout = (LinearLayout)view.findViewById(R.id.pre_filter_bar_layout);

        mFirstFilterNameText = (TextView)view.findViewById(R.id.pre_filter_name_tv);
        mFirstFilterDesText = (TextView)view.findViewById(R.id.pre_filter_des_txt);

        mHisivFilter450Title = (TextView)view.findViewById(R.id.hisiv_filter_name_tv);
        mHisivFilter450Description = (TextView)view.findViewById(R.id.hisiv_filter_instruction);

        initDesTitleTextView(view);
        if (((DeviceActivity) getFragmentActivity()).isAirPremium()){
            initAirPremium();
        } else if (((DeviceActivity) getFragmentActivity()).isAirTouch450OrJD()) {
            initAir450 ();
        }
    }


    private void initDesTitleTextView(View view){
        mPreDesTitleTextView = (TextView)view.findViewById(R.id.pre_des_title);
        mPm25DesTitleTextView = (TextView)view.findViewById(R.id.pm25_filter_tv);
        mHisvDesTitleTextView = (TextView)view.findViewById(R.id.hisiv_des_title);
    }

    private void initAir450 () {

        mSecondFilterLayout.setVisibility(View.GONE);
        hisivFilterTextView.setText(getFragmentActivity().getString(R.string.enroll_two));

        SpannableString ssTitle = new SpannableString(getFragmentActivity().getString(R.string.hisiv_450_filter));
        ssTitle.setSpan(new SuperscriptSpan(), 5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssTitle.setSpan(new RelativeSizeSpan(0.6f), 5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString ssDescription = new SpannableString(getFragmentActivity().getString(R.string.hisiv_450_filter_instruction));
        ssDescription.setSpan(new SuperscriptSpan(), 5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssDescription.setSpan(new RelativeSizeSpan(0.6f), 5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mHisivFilter450Title.setText(ssTitle);
        mHisvDesTitleTextView.setText(ssTitle);

        mHisivFilter450Description.setText(ssDescription);
        int translateDistance = DensityUtil.getScreenHeight()/2 - DensityUtil.dip2px(165) -
                mThirdFilterLayout.getHeight() - progressLayout.getHeight() - DensityUtil.dip2px(25);
        ViewHelper.setTranslationY(mPreFilterTopLinearLayout, translateDistance);
        ViewHelper.setTranslationY(firmwareTextView, -translateDistance);
    }
    private void initAirPremium(){
        mSecondFilterLayout.setVisibility(View.GONE);
        mThirdFilterLayout.setVisibility(View.GONE);
        preFilterTextView.setVisibility(View.GONE);

        mFirstFilterDesText.setText(getFragmentActivity().getResources().getString(R.string.airpremium_filter_des));
        mFirstFilterNameText.setText( getFragmentActivity().getResources().getString(R.string.airpremium_filter_title));
        mPreDesTitleTextView.setText(getFragmentActivity().getResources().getString(R.string.airpremium_filter_title));

        LinearLayout.LayoutParams params3
                = new LinearLayout.LayoutParams(progressLayout2.getLayoutParams());
        params3.leftMargin = DensityUtil.dip2px(40);
        progressLayout2.setLayoutParams(params3);
//
        LinearLayout.LayoutParams params
                = new LinearLayout.LayoutParams(progressLayout.getLayoutParams());
        params.height = DensityUtil.dip2px(15);
//        params.bottomMargin = DensityUtil.dip2px(10);
        progressLayout.setLayoutParams(params);

        int translateDistance = DensityUtil.getScreenHeight()/2 - DensityUtil.dip2px(125);
        ViewHelper.setTranslationY(mPreFilterTopLinearLayout, translateDistance);

        ViewHelper.setTranslationY(firmwareTextView, -translateDistance);
    }

    /**
     * initFilter data and animation
     *
     * getFilterRunLife() => GET_DEVICE_STATUS => getFilterMaxLife()
     *      => GET_DEVICE_CAPABILITY => updateFilterLifeBar() => showFilterAnimation();
     *
     */
    private void initFilter() {
        getFilterRunLife();

        firmwareVersion = mCurrentDevice.getDeviceInfo().getFirmwareVersion();
        firmwareTextView.setText( getFragmentActivity().getString(R.string.firmware_version) + " " + firmwareVersion);
        ViewHelper.setAlpha(firmwareTextView, 0.5f);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
//            firmwareTextView.setAlpha(0.5f);
    }

    private void getFilterRunLife() {
        //LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "getFilterRunLife mThisHomeDevice："
          //      + ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getFilter1Runtime());

        if (mCurrentDevice instanceof AirTouchSeriesDevice){
            ((AirTouchSeriesDevice)mCurrentDevice).getFilterInfo(getDeviceCapabilityReceive);
        }

    }
    IActivityReceive getDeviceCapabilityReceive = new IActivityReceive() {
        @Override
        public void onReceive(ResponseResult responseResult) {
                switch (responseResult.getRequestId()) {
                    case GET_DEVICE_CAPABILITY:
                        processCapabilityResult(responseResult);
                        break;
            }
        }
    };

    private void processCapabilityResult(ResponseResult responseResult){
        if ((responseResult == null || responseResult.getResponseData() == null) && isAdded()) {
            MessageBox.createSimpleDialog(getFragmentActivity(), null,  getFragmentActivity().getString(R.string.enroll_error),
                    null, null);
        }
        if (responseResult.isResult()){
            CapabilityResponse capabilityResponse = (CapabilityResponse) responseResult.getResponseData().getSerializable(AirTouchConstants.DEVICE_CAPABILITY_KEY);
            updateFilterLifeBar(capabilityResponse.getFilter1ExpiredTime(),
                    capabilityResponse.getFilter2ExpiredTime(),
                    capabilityResponse.getFilter3ExpiredTime());
        }
        else{
            errorHandle(responseResult);
        }

    }

    private void startPreFilterBar() {
        bar1 = 100;
        if (preFilterTimer == null) {
            preFilterTimer = new Timer();
            preFilterTask = new TimerTask() {

                @Override
                public void run() {
                    preFilterProgressBar.setProgress(bar1);
                    if (bar1 == preFilterLife) {
                        preFilterTask.cancel();
                        preFilterTimer.cancel();
                    }
                    bar1--;
                }
            };
            preFilterTimer.schedule(preFilterTask, 0, 50);
        }
    }

    private void startPm25FilterBar() {
        bar2 = 100;
        if (pm25FilterTimer == null) {
            pm25FilterTimer = new Timer();
            pm25FilterTask = new TimerTask() {

                @Override
                public void run() {
                    pm25FilterProgressBar.setProgress(bar2);
                    if (bar2 == pm25FilterLife) {
                        pm25FilterTask.cancel();
                        pm25FilterTimer.cancel();
                    }
                    bar2--;
                }
            };
            pm25FilterTimer.schedule(pm25FilterTask, 0, 50);
        }
    }

    private void startHisivFilterBar() {
        bar3 = 100;
        if (hisivFilterTimer == null) {
            hisivFilterTimer = new Timer();
            hisivFilterTask = new TimerTask() {

                @Override
                public void run() {
                    hisivFilterProgressBar.setProgress(bar3);
                    if (bar3 == hisivFilterLife) {
                        hisivFilterTask.cancel();
                        hisivFilterTimer.cancel();
                    }
                    bar3--;
                }
            };
            hisivFilterTimer.schedule(hisivFilterTask, 0, 50);
        }
    }


    public void stopFilterBar() {
        if (preFilterTimer != null) {
            preFilterTask.cancel();
            preFilterTimer.cancel();
            preFilterTask = null;
            preFilterTimer = null;
        }
        if (pm25FilterTimer != null) {
            pm25FilterTask.cancel();
            pm25FilterTimer.cancel();
            pm25FilterTask = null;
            pm25FilterTimer = null;
        }
        if (hisivFilterTimer != null) {
            hisivFilterTask.cancel();
            hisivFilterTimer.cancel();
            hisivFilterTask = null;
            hisivFilterTimer = null;
        }
    }


    public View.OnClickListener preFilterExpandClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            preFilterLayout.setClickable(false);
            pm25FilterLayout.setClickable(false);
            hisivFilterLayout.setClickable(false);
            preExpandAnimation = new ExpandAnimation(preFilterDescriptionLayout, 500);
            preExpandAnimation.setAnimationListener(new expandAnimationListener());
            preFilterDescriptionLayout.startAnimation(preExpandAnimation);

            if (isPreFilterExpand) {
                isPreFilterExpand = false;
            } else {
                isPreFilterExpand = true;
            }

            // un-expand other layout
            if (isPm25FilterExpand) {
                isPm25FilterExpand = false;
                pm25ExpandAnimation = new ExpandAnimation(pm25FilterDescriptionLayout, 500);
                pm25FilterDescriptionLayout.startAnimation(pm25ExpandAnimation);
            }
            if (isHisivFilterExpand) {
                isHisivFilterExpand = false;
                hisivExpandAnimation = new ExpandAnimation(hisivFilterDescriptionLayout, 500);
                hisivFilterDescriptionLayout.startAnimation(hisivExpandAnimation);
            }
        }
    };

    public View.OnClickListener pm25FilterExpandClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            preFilterLayout.setClickable(false);
            pm25FilterLayout.setClickable(false);
            hisivFilterLayout.setClickable(false);
            pm25ExpandAnimation = new ExpandAnimation(pm25FilterDescriptionLayout, 500);
            pm25ExpandAnimation.setAnimationListener(new expandAnimationListener());
            pm25FilterDescriptionLayout.startAnimation(pm25ExpandAnimation);
            if (isPm25FilterExpand) {
                isPm25FilterExpand = false;
            } else {
                isPm25FilterExpand = true;
            }

            // un-expand other layout
            if (isPreFilterExpand) {
                isPreFilterExpand = false;
                preExpandAnimation = new ExpandAnimation(preFilterDescriptionLayout, 500);
                preFilterDescriptionLayout.startAnimation(preExpandAnimation);
            }
            if (isHisivFilterExpand) {
                isHisivFilterExpand = false;
                hisivExpandAnimation = new ExpandAnimation(hisivFilterDescriptionLayout, 500);
                hisivFilterDescriptionLayout.startAnimation(hisivExpandAnimation);
            }
        }
    };

    public View.OnClickListener hisivFilterExpandClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            preFilterLayout.setClickable(false);
            pm25FilterLayout.setClickable(false);
            hisivFilterLayout.setClickable(false);
            hisivExpandAnimation = new ExpandAnimation(hisivFilterDescriptionLayout, 500);
            hisivExpandAnimation.setAnimationListener(new expandAnimationListener());
            hisivFilterDescriptionLayout.startAnimation(hisivExpandAnimation);
            if (isHisivFilterExpand) {
                isHisivFilterExpand = false;
            } else {
                isHisivFilterExpand = true;
            }

            // un-expand other layout
            if (isPreFilterExpand) {
                isPreFilterExpand = false;
                preExpandAnimation = new ExpandAnimation(preFilterDescriptionLayout, 500);
                preFilterDescriptionLayout.startAnimation(preExpandAnimation);
            }
            if (isPm25FilterExpand) {
                isPm25FilterExpand = false;
                pm25ExpandAnimation = new ExpandAnimation(pm25FilterDescriptionLayout, 500);
                pm25FilterDescriptionLayout.startAnimation(pm25ExpandAnimation);
            }
        }
    };


    private class expandAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            preFilterLayout.setClickable(true);
            pm25FilterLayout.setClickable(true);
            hisivFilterLayout.setClickable(true);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation arg0) {
            // TODO Auto-generated method stub
        }
    }


    private void updateFilterLifeBar(int life1, int life2, int life3) {
        if (mCurrentDevice == null || ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus() == null){
            return;
        }

        if (((DeviceActivity) getFragmentActivity()).isAirTouch450OrJD()){

            if (life1 != 0) {
                preFilterLife = getPercentWithRuntime(preFilterLifeRaw,life1);
                preFilterPercentTextView.setText(String.valueOf(preFilterLife) + "%");
            }

            if (life2 != 0){
                hisivFilterLife = getPercentWithRuntime(pm25FilterLifeRaw,life2);

                hisivFilterPercentTextView.setText(String.valueOf(hisivFilterLife) + "%");
            }
        }
        else if (((DeviceActivity) getFragmentActivity()).isAirPremium()){
            if (life1 != 0){
                preFilterLife = getPercentWithRuntime(preFilterLifeRaw,life1);

                preFilterPercentTextView.setText(String.valueOf(preFilterLife) + "%");
            }
        } else {

            if (life1 != 0){
                preFilterLife = getPercentWithRuntime(preFilterLifeRaw,life1);
                preFilterPercentTextView.setText(String.valueOf(preFilterLife) + "%");
            }

            if (life2 != 0) {
                pm25FilterLife = getPercentWithRuntime(pm25FilterLifeRaw,life2);
                pm25FilterPercentTextView.setText(String.valueOf(pm25FilterLife) + "%");
            }

            if (life3 != 0) {
                hisivFilterLife = getPercentWithRuntime(hisivFilterLifeRaw,life3);
                hisivFilterPercentTextView.setText(String.valueOf(hisivFilterLife) + "%");
            }
        }
        if (setAirPremiumFilterPosition())
            return;

        showFilterAnimation(life1, life2, life3);

    }

    private int getPercentWithRuntime(int runtime,int lifeTime){
        int  filterLife = runtime >= 0 ? 100 - runtime * 100 / lifeTime : 0;
        return filterLife > 0 ? filterLife : 0;
    }

    public void showFilterAnimation(int life1, int life2, int life3) {
        if (isHasNullPoint()) {
            return;
        }

        if (preFilterLife > 0)
            startPreFilterBar();
        if (pm25FilterLife > 0)
            startPm25FilterBar();
        if (hisivFilterLife > 0)
            startHisivFilterBar();

        if (preFilterLife <= 20 && life1 > 0) {
            preFilterProgressBar.startAnimation(alphaOffAnimation);
            mOnFilterAnimationListener.onAnimation(1);
        }

        if (pm25FilterLife <= 20 && life2 > 0) {
            pm25FilterProgressBar.startAnimation(alphaOffAnimation);
            mOnFilterAnimationListener.onAnimation(2);
        }

        if (hisivFilterLife <= 20 && life3 > 0) {
            hisivFilterProgressBar.startAnimation(alphaOffAnimation);
            mOnFilterAnimationListener.onAnimation(3);
        }


    }



    public void showFilterTutorial() {
        if (AppConfig.isFilterTutorial) {
            tutorialMask.setVisibility(View.INVISIBLE);
        } else {
            tutorialMask.setVisibility(View.VISIBLE);
            tutorialMask.setOnClickListener(tutorialOnClick);
        }

    }

    public boolean setAirPremiumFilterPosition(){
        isNeedToShowNoAuthorize = false;
        if (((DeviceActivity) getFragmentActivity()).isAirPremium()){

            int translateDistance = DensityUtil.getScreenHeight()/2 - DensityUtil.dip2px(125);
//            ViewHelper.setTranslationY(mPreFilterTopLinearLayout, translateDistance);

            RunStatus runStatus = ((AirTouchSeriesDevice) mCurrentDevice).getDeviceRunStatus();
            if (runStatus != null && NO_AUTHORIZE_STRING.equals(runStatus.getFilter1RfidFlag())){
                mNoAuthorizeFilterLayout.setVisibility(View.VISIBLE);
                ViewHelper.setTranslationX(mNoAuthorizeFilterLayout, progressLayout2.getX());
                ViewHelper.setTranslationY(mNoAuthorizeFilterLayout, translateDistance + mPreFilterSecondTopLinearLayout.getHeight() + progressLayout.getHeight() + DensityUtil.dip2px(25));
                isNeedToShowNoAuthorize = true;

                mFirstFilterNameText.setText(getFragmentActivity().getString(R.string.no_authorize));
                mFirstFilterNameText.setTextColor(getFragmentActivity().getResources().getColor(R.color.filter_no_authorize));
                preFilterPercentTextView.setText("0%");
                preFilterPercentTextView.setTextColor(getFragmentActivity().getResources().getColor(R.color.filter_no_authorize));
                preFilterProgressBar.setProgress(0);
                preFilterLayout.setClickable(false);
                preFilterLife = 0;
                return true;

            }
            else{
                mNoAuthorizeFilterLayout.setVisibility(View.GONE);
                mFirstFilterNameText.setText(getFragmentActivity().getString(R.string.airpremium_filter_title));
                mFirstFilterNameText.setTextColor(getFragmentActivity().getResources().getColor(R.color.white));
                return false;
            }
        }
        return false;
    }


    private MessageBox.MyOnClick quit = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            getFragmentActivity().finish();
        }
    };

    private MessageBox.MyOnClick goToBuy = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
//            Uri uri = null;
//            if (((DeviceActivity) mActivity).isAirPremium()){
//                uri = Uri.parse(Air_PREMIUM_PURSUE);
//            }
//            else{
               Uri uri = Uri.parse(productUrls[0]);
//            }
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            FilterFragment.this.startActivity(it);
        }
    };

    View.OnClickListener preFilterPurchase = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (((DeviceActivity) getFragmentActivity()).isAirPremium()) {
                Uri uri = Uri.parse(Air_PREMIUM_PURSUE);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                FilterFragment.this.startActivity(it);
                return;
            }
            if(((DeviceActivity) getFragmentActivity()).isAirTouch450OrJD()) {
                Uri uri = Uri.parse(AIR_TOUCH450_PURSUE[0]);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                FilterFragment.this.startActivity(it);
                return;
            }
            MessageBox.createTwoButtonDialog(getFragmentActivity(),  getFragmentActivity().getString(R.string.no_need_to_buy),
                    getFragmentActivity().getString(R.string.pre_filter_suggestion),  getFragmentActivity().getString(R.string.buy_it),
                    goToBuy,  getFragmentActivity().getString(R.string.cancel), null);
        }
    };

    View.OnClickListener pm25FilterPurchase = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse(productUrls[1]);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            FilterFragment.this.startActivity(it);
        }
    };

    View.OnClickListener hisivFilterPurchase = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(((DeviceActivity) getFragmentActivity()).isAirTouch450OrJD()) {
                Uri uri = Uri.parse(AIR_TOUCH450_PURSUE[1]);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                FilterFragment.this.startActivity(it);
                return;
            }
            Uri uri = Uri.parse(productUrls[2]);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            FilterFragment.this.startActivity(it);
        }
    };

    View.OnClickListener tutorialOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tutorialMask.setVisibility(View.INVISIBLE);
            AppConfig appConfig = AppConfig.shareInstance();
            appConfig.setIsFilterTutorial(true);
            mOnControlTutorialRemovedListener.remove();
        }
    };

    private Boolean isHasNullPoint() {
        return mCurrentDevice == null
                || mCurrentDevice.getDeviceInfo() == null;
    }

    private void errorHandle(ResponseResult responseResult) {
        if (isAdded())
            return;

        if (responseResult.getResponseCode() == StatusCode.NETWORK_ERROR) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + responseResult.getExeptionMsg());
            MessageBox.createSimpleDialog(getFragmentActivity(), null,  getFragmentActivity().getString(R.string.no_network),
                    null, null);
            return;
        }

        if (!StringUtil.isEmpty(responseResult.getExeptionMsg())) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + responseResult.getExeptionMsg());
        }
        MessageBox.createSimpleDialog(getFragmentActivity(), null,  getFragmentActivity().getString(R.string.enroll_error),
                null, null);
    }

    /**
     * the listener of onControl clicked
     */
    public interface OnControlTutorialRemovedListener {
        public void remove();
    }

    public void setOnControlTutorialRemovedListener(OnControlTutorialRemovedListener onControlClickListener) {
        mOnControlTutorialRemovedListener = onControlClickListener;
    }

    /**
     * the listener of onFilter animation
     */
    public interface OnFilterAnimationListener {
        public void onAnimation(int filter);
    }

    public void setOnFilterAnimationListener(OnFilterAnimationListener onFilterAnimationListener) {
        mOnFilterAnimationListener = onFilterAnimationListener;
    }

    public FragmentActivity getFragmentActivity() {
        if (mActivity == null)
            mActivity = getActivity();
        return mActivity;
    }
}