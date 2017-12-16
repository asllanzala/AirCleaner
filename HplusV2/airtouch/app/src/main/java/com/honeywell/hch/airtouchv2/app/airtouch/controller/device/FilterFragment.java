package com.honeywell.hch.airtouchv2.app.airtouch.controller.device;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.CapabilityResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseRequestFragment;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv2.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv2.framework.model.RunStatus;
import com.honeywell.hch.airtouchv2.framework.view.ExpandAnimation;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jin Qian on 2/13/2015.
 */
public class FilterFragment extends BaseRequestFragment {

    private static final String TAG = "AirTouchFilter";
    private static final String ARG_DEVICE = "device";

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
    private int preFilterLife, pm25FilterLife, hisivFilterLife;
    private int bar1, bar2, bar3;
    private String firmwareVersion;

    private FragmentActivity mActivity;
    private String mSessionId;
    private int mDeviceId;
    private HomeDevice mCurrentDevice = null;
    private OnFilterAnimationListener mOnFilterAnimationListener;
    private String[] productUrls = {"http://hch.honeywell.com/Pages/default.aspx?version=1&model=prefilter",
            "http://hch.honeywell.com/Pages/default.aspx?version=1&model=hepafilter",
            "http://hch.honeywell.com/Pages/default.aspx?version=1&model=chemicalfilter"};

    public static FilterFragment newInstance(FragmentActivity activity) {
        FilterFragment fragment = new FilterFragment();
        fragment.setActivity(activity);
        return fragment;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        mCurrentDevice = AuthorizeApp.shareInstance().getCurrentDevice();
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

        if (isHasNullPoint()) {
            MessageBox.createSimpleDialog(mActivity, null,
                    getString(R.string.no_data), getString(R.string.ok), quit);
        } else {
            initFilter();
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
        alphaOffAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.control_alpha);
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
        firmwareTextView.setText(getString(R.string.firmware_version) + " " + firmwareVersion);
        ViewHelper.setAlpha(firmwareTextView, 0.5f);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
//            firmwareTextView.setAlpha(0.5f);
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

    private void getFilterRunLife() {
//        mDialog = ProgressDialog.show(mActivity, null, getString(R.string.enroll_loading));
        mSessionId = AuthorizeApp.shareInstance().getSessionId();
        if (((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus() == null) {
            int getDeviceStatusRequestId = getRequestClient().getDeviceStatus(mDeviceId,
                    mSessionId, mReceiveResponse);
            addRequestId(getDeviceStatusRequestId);
        } else {
            getFilterMaxLife();
        }

    }

    private void getFilterMaxLife() {
        int getDeviceCapabilityRequestId = getRequestClient().getDeviceCapability(mDeviceId,
                mSessionId, mReceiveResponse);
        addRequestId(getDeviceCapabilityRequestId);
    }

    IReceiveResponse mReceiveResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            removeRequestId(httpRequestResponse.getRandomRequestID());
            switch (httpRequestResponse.getRequestID()) {
                case GET_DEVICE_STATUS:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            RunStatus runStatusResponse = new Gson().fromJson(httpRequestResponse.getData(),
                                    RunStatus.class);
                            ((AirTouchSeriesDevice)mCurrentDevice).setDeviceRunStatus(runStatusResponse);

                            getFilterMaxLife();
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                case GET_DEVICE_CAPABILITY:
//                    if (mDialog != null) {
//                        mDialog.dismiss();
//                    }
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            CapabilityResponse capabilityResponse
                                    = new Gson().fromJson(httpRequestResponse.getData(),
                                    CapabilityResponse.class);

                            if (capabilityResponse.getFilter1ExpiredTime() == 0
                                    || capabilityResponse.getFilter2ExpiredTime() == 0
                                    || capabilityResponse.getFilter3ExpiredTime() == 0) {
                                MessageBox.createSimpleDialog(mActivity, null, getString(R.string.enroll_error),
                                        null, null);
                            } else {
                                updateFilterLifeBar(capabilityResponse.getFilter1ExpiredTime(),
                                        capabilityResponse.getFilter2ExpiredTime(),
                                        capabilityResponse.getFilter3ExpiredTime());
                            }
                        }
                    } else {
                        errorHandle(httpRequestResponse);
                    }
                    break;

                default:
                    break;
            }

        }
    };

    private void updateFilterLifeBar(int life1, int life2, int life3) {
        preFilterLife = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getFilter1Runtime();
        pm25FilterLife = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getFilter2Runtime();
        hisivFilterLife = ((AirTouchSeriesDevice)mCurrentDevice).getDeviceRunStatus().getFilter3Runtime();

        preFilterLife = 100 - preFilterLife * 100 / life1;
        preFilterLife = preFilterLife > 0 ? preFilterLife : 0;
        pm25FilterLife = 100 - pm25FilterLife * 100 / life2;
        pm25FilterLife = pm25FilterLife > 0 ? pm25FilterLife : 0;
        hisivFilterLife = 100 - hisivFilterLife * 100 / life3;
        hisivFilterLife = hisivFilterLife > 0 ? hisivFilterLife : 0;
        preFilterPercentTextView.setText(String.valueOf(preFilterLife) + "%");
        pm25FilterPercentTextView.setText(String.valueOf(pm25FilterLife) + "%");
        hisivFilterPercentTextView.setText(String.valueOf(hisivFilterLife) + "%");

        showFilterAnimation();
    }

    public void showFilterAnimation() {
        if (isHasNullPoint()) {
            return;
        }

        if (preFilterLife > 0)
            startPreFilterBar();
        if (pm25FilterLife > 0)
            startPm25FilterBar();
        if (hisivFilterLife > 0)
            startHisivFilterBar();

        if (preFilterLife <= 20) {
            preFilterProgressBar.startAnimation(alphaOffAnimation);
            mOnFilterAnimationListener.onAnimation(1);
        }

        if (pm25FilterLife <= 20) {
            pm25FilterProgressBar.startAnimation(alphaOffAnimation);
            mOnFilterAnimationListener.onAnimation(2);
        }

        if (hisivFilterLife <= 20) {
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


    private MessageBox.MyOnClick quit = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            mActivity.finish();
        }
    };

    private MessageBox.MyOnClick goToBuy = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse(productUrls[0]);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            FilterFragment.this.startActivity(it);
        }
    };

    View.OnClickListener preFilterPurchase = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MessageBox.createTwoButtonDialog(mActivity, getString(R.string.no_need_to_buy),
                    getString(R.string.pre_filter_suggestion), getString(R.string.buy_it),
                    goToBuy, getString(R.string.cancel), null);
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

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
//        if (mDialog != null) {
//            mDialog.dismiss();
//        }

        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
            MessageBox.createSimpleDialog(mActivity, null, getString(R.string.no_network),
                    null, null);
            return;
        }

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
//                MessageBox.createSimpleDialog(mActivity, null, errorResponse.getMessage(), null,
//                        null);
                LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Error：" + errorResponse.getMessage());
                MessageBox.createSimpleDialog(mActivity, null, getString(R.string.enroll_error), null,
                        null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.createSimpleDialog(mActivity, null, getString(R.string.enroll_error),
                    null, null);
        }
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

}