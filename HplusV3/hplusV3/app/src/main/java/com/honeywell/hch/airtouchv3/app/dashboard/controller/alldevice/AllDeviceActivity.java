package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.device.DeviceActivity;
import com.honeywell.hch.airtouchv3.wxapi.WXEntryActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.view.LoadingProgressDialog;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.control.GroupControlActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.model.ComparatorMaster;
import com.honeywell.hch.airtouchv3.app.dashboard.model.DeviceListRequest;
import com.honeywell.hch.airtouchv3.app.dashboard.model.DevicesForGroupResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupData;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupDataResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.view.AllDeviceAirTouchView;
import com.honeywell.hch.airtouchv3.app.dashboard.view.AllDeviceArrow;
import com.honeywell.hch.airtouchv3.app.dashboard.view.AllDeviceGroupTitleView;
import com.honeywell.hch.airtouchv3.app.dashboard.view.AllDeviceSelectionView;
import com.honeywell.hch.airtouchv3.app.dashboard.view.DragAnimation;
import com.honeywell.hch.airtouchv3.app.dashboard.view.ShakeAnimation;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.database.DefaultDeviceDBService;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Qian Jin on 10/10/15.
 */
public class AllDeviceActivity extends BaseHasBackgroundActivity {
    private ScrollView mScrollView;
    private FrameLayout mBackLayout;
    private LinearLayout mGroupLayout;
    private LinearLayout mUnGroupLayout;
    private static Dialog mDialog;
    private AlertDialog mAlertDialog;
    private AllDeviceArrow mArrow;
    private Context mContext;

    private GroupManager mGroupManager;
    private ShakeAnimation mShakeAnimation;
    private DragAnimation mDragAnimation;
    private Boolean mIsEditMode = false;
    private int mUnGroupAirPremiumNumber = 0;
    private int mHomeIndex; // current home's index
    private BroadcastReceiver mRunStatusChangedReceiver;
    // group data
    private List<GroupDataResponse> mGroupLists = new ArrayList<>(); // group data from API
    private List<ArrayList<HomeDevice>> mGroupHomeDevicesList = new ArrayList<>(); // local groups data
    private List<HomeDevice> mUnGroupHomeDevices = new ArrayList<>();
    // group view
    private List<AllDeviceAirTouchView> mGroupHomeDeviceViews = new ArrayList<>();
    private List<AllDeviceAirTouchView> mUnGroupHomeDeviceViews = new ArrayList<>();
    private List<AllDeviceSelectionView> mGroupSelectionViews = new ArrayList<>();
    private List<AllDeviceSelectionView> mUnGroupSelectionViews = new ArrayList<>();
    private AllDeviceAirTouchView mSelectedDeviceView;

    private static final int DEVICE_NUMBER_PER_LINE = 4;
    private static final int MAX_GROUP_NUM = 100;
    public static final String ARG_GROUP = "group";
    public static final String ARG_HOME_INDEX = "homeIndex";
    public static final String ARG_LOCATION_ID = "location_id";
    public static final int GROUP_CONTROL_REQUEST_CODE = 11;
    public static final int DEVICE_CONTROL_REQUEST_CODE = 12;
    private int[] mUnGroupMasterIds = new int[MAX_GROUP_NUM];
    private DefaultDeviceDBService mDefaultDeviceDBService = null;
    private boolean isRefreshingData = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GROUP_CONTROL_REQUEST_CODE:
                updateDeviceDataAndView();
                break;

            case DEVICE_CONTROL_REQUEST_CODE:
                updateDeviceDataAndView();
            default:
                break;
        }
    }

    @Override
    public void finish() {
        Intent backIntent = new Intent();
        backIntent.putExtra(ARG_LOCATION_ID, mLocationId);
        setResult(RESULT_OK, backIntent);
        super.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alldevice);

        mContext = AllDeviceActivity.this;
        initView();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();

        registerRunStatusChangedReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();

        unRegisterRunStatusChangedReceiver();
    }

    private void initView() {
        mArrow = (AllDeviceArrow) findViewById(R.id.arrow);
        mScrollView = (ScrollView) findViewById(R.id.whole_view);
        mGroupLayout = (LinearLayout) findViewById(R.id.group_layout);
        mUnGroupLayout = (LinearLayout) findViewById(R.id.un_group_layout);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //quit edit mode
                        if (mIsEditMode)
                            setEditMode(false, false);
                        break;
                }
                return false;
            }
        });
        mBackLayout = (FrameLayout) findViewById(R.id.back_layout);
        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAlertDialog = new AlertDialog.Builder(AllDeviceActivity.this).create();
        initDynamicBackground();
    }

    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mShakeAnimation = new ShakeAnimation(dm.density);
        mGroupManager = new GroupManager();
        mDragAnimation = new DragAnimation();

        Bundle bundle = getIntent().getExtras();
        mHomeIndex = bundle.getInt(ARG_HOME_INDEX);
        mDefaultDeviceDBService = new DefaultDeviceDBService(this);

        mDialog = LoadingProgressDialog.show(mContext, getString(R.string.enroll_loading));
        updateDeviceDataAndView();
    }

    /**
     * Update device data and then update view
     * 1) Get raw device data from UserLocationData
     * 2) Sort raw data, AirTouchP go first
     * 3) Get group data by HTTP API
     * 4) Set group data into HomeDevice lists by deviceId
     * 4.1) mGroupDeviceData list
     * 4.2) mUnGroupDeviceData list
     * 5) Update device data view
     */
    private void updateDeviceDataAndView() {
//        clearAllData();
        mUserLocationData = AppManager.shareInstance().getUserLocationByID(mLocationId);
        if (mUserLocationData == null) {
            mDialog.dismiss();
            return;
        }

        final List<HomeDevice> mSortHomeDevices = sortAirTouchP(mUserLocationData.getHomeDevicesList());
        mGroupManager.getGroupByLocationId(mLocationId);
        mGroupManager.setErrorCallback(new GroupManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, int id) {
                mDialog.dismiss();
                if (!isRefreshingData) {
                    errorHandle(responseResult, getString(id));
                }
            }
        });
        mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
            @Override
            public void onSuccess(ResponseResult responseResult) {
                clearAllData();
                GroupData groupData = (GroupData) responseResult.getResponseData()
                        .getSerializable(GroupData.GROUP_DATA);
                if (groupData != null) {
                    List<DevicesForGroupResponse> unGroupDeviceList = groupData.getUnGroupDeviceList();
                    mGroupLists = groupData.getGroupList();
                    // bind groupList into mGroupHomeDevices data
                    for (GroupDataResponse oneGroup : mGroupLists) {
                        List<DevicesForGroupResponse> groupDeviceList = oneGroup.getGroupDeviceList();
                        ArrayList<HomeDevice> oneGroupHomeDevices = new ArrayList<>(); // devices in one group
                        for (HomeDevice homeDevice : mSortHomeDevices) {
                            for (DevicesForGroupResponse groupDevice : groupDeviceList) {
                                if (groupDevice.getDeviceId() == homeDevice.getDeviceInfo().getDeviceID()) {
                                    // store group Air Premium isMaster or not
                                    if (AppManager.shareInstance().isAirtouchP((int) groupDevice.getDeviceType()))
                                        homeDevice.setIsMasterDevice(groupDevice.getIsMasterDevice());
                                    oneGroupHomeDevices.add(homeDevice);
                                    Collections.sort(oneGroupHomeDevices, new ComparatorMaster());
                                }
                            }
                        }
                        mGroupHomeDevicesList.add(oneGroupHomeDevices);
                    }

                    mUnGroupHomeDevices.clear();
                    // bind unGroupList into mUnGroupHomeDevices data
                    for (HomeDevice homeDevice : mSortHomeDevices) {
                        for (DevicesForGroupResponse unGroupDevice : unGroupDeviceList) {
                            if (homeDevice.getDeviceInfo().getDeviceID() == unGroupDevice.getDeviceId()) {
                                homeDevice.setIsMasterDevice(0);
                                mUnGroupHomeDevices.add(homeDevice);
                                // store un-group Air Premium number
                                if (AppManager.shareInstance().isAirtouchP(homeDevice.getDeviceType())) {
                                    mUnGroupMasterIds[mUnGroupAirPremiumNumber] = homeDevice.getDeviceInfo().getDeviceID();
                                    mUnGroupAirPremiumNumber++;
                                }
                            }
                        }
                    }

                    // Refresh group data, if data changed, update view.
                    if (mIsEditMode)
                        return;
                }

                mIsEditMode = false;
                updateDeviceView();
            }
        });
    }

    /**
     * Update device view
     * Add group device first, then add un-group data.
     * 1) add devices in one line
     * 1.1) create device one by one
     * 1.2) set position
     * 2) add one line horizon layout into vertical layout.
     */
    private void updateDeviceView() {
        clearAllViews();
        mDialog.dismiss();
        // update group lists
        if (!mGroupHomeDevicesList.isEmpty()) {
            for (int i = 0; i < mGroupLists.size(); i++) {
                final GroupDataResponse oneGroup = mGroupLists.get(i);
                final ArrayList<HomeDevice> groupHomeDevices = mGroupHomeDevicesList.get(i);
                // update one group device lists
                LinearLayout oneGroupLayout = getOneGroup(true, groupHomeDevices, oneGroup.getGroupName());
                oneGroupLayout.setId(oneGroup.getGroupId());
                oneGroupLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mIsEditMode) {
                            setEditMode(false, false);
                            return;
                        }
                        Intent intent = new Intent(mContext, GroupControlActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ARG_GROUP, oneGroup);
                        bundle.putInt(ARG_HOME_INDEX, mHomeIndex);
                        intent.putExtras(bundle);
                        intent.putExtra(WXEntryActivity.LOCATION_ID, mLocationId);
                        startActivityForResult(intent, GROUP_CONTROL_REQUEST_CODE);
                        overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
                    }
                });

                // add one group to group list
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = DensityUtil.dip2px(20);
                oneGroupLayout.setLayoutParams(params);
                mGroupLayout.addView(oneGroupLayout);
            }
        }
        // update un-group device lists
        if (!mUnGroupHomeDevices.isEmpty()) {
            getOneGroup(false, mUnGroupHomeDevices, "");
        }
    }

    private List<HomeDevice> sortAirTouchP(List<HomeDevice> rawList) {
        List<HomeDevice> targetList = new ArrayList<>();
        for (HomeDevice homeDevice : rawList) {
            if (AppManager.shareInstance().isAirtouchP(homeDevice.getDeviceType()))
                targetList.add(homeDevice);
        }
        for (HomeDevice homeDevice : rawList) {
            if (AppManager.shareInstance().isAirtouch450(homeDevice.getDeviceType()))
                targetList.add(homeDevice);
        }
        for (HomeDevice homeDevice : rawList) {
            if (AppManager.shareInstance().isAirtouchs(homeDevice.getDeviceType()))
                targetList.add(homeDevice);
        }
        return targetList;
    }

    private LinearLayout getOneGroup(Boolean isInGroup, List<HomeDevice> devices, String groupName) {
        LinearLayout oneGroup = new LinearLayout(this);
        oneGroup.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout oneLine = null;

        if (isInGroup) {
            AllDeviceGroupTitleView groupTitleView = new AllDeviceGroupTitleView(mContext);
            groupTitleView.setGroupName(groupName);
            oneGroup.addView(groupTitleView);
        }

        // add one group to group list
        LinearLayout oneGroupExcludeTitle = new LinearLayout(this);
        oneGroupExcludeTitle.setOrientation(LinearLayout.VERTICAL);

        int deviceViewLines = (devices.size() - 1) / DEVICE_NUMBER_PER_LINE + 1;
        for (int i = 1; i <= deviceViewLines; i++) {
            if (i == deviceViewLines)
                oneLine = getOneLine(i, true, isInGroup, devices);
            else
                oneLine = getOneLine(i, false, isInGroup, devices);

            // add horizon layout to vertical layout
            if (isInGroup) {
                // add one group device list
                oneGroupExcludeTitle.addView(oneLine);
            } else {
                mUnGroupLayout.addView(oneLine);
            }
        }

        oneGroupExcludeTitle.setBackgroundResource(R.drawable.all_device_group_bg);
        oneGroup.addView(oneGroupExcludeTitle);

        return oneGroup;
    }

    private RelativeLayout getOneLine(int line, boolean isReachMaxLine,
                                      boolean isInGroup, List<HomeDevice> devices) {
        RelativeLayout deviceOneLineLayout = new RelativeLayout(this);
        List<AllDeviceAirTouchView> deviceViews = createDeviceViews(line, isInGroup, isReachMaxLine, devices);
        List<AllDeviceSelectionView> selectionViews = createSelectionViews(line, isInGroup, isReachMaxLine, devices);
        int maxHeight = getDeviceMaxHeight(line, isInGroup, isReachMaxLine, devices);
        int maxNumber = isReachMaxLine ? devices.size() : line * DEVICE_NUMBER_PER_LINE;
        // add devices and selections to layout one by one
        int fixDeviceWidth = DensityUtil.dip2px(15);
        int deviceWidth = 0;
        int spaceing = 0;
        for (int i = (line - 1) * DEVICE_NUMBER_PER_LINE; i < maxNumber; i++) {
            int deviceHeight = 0;
//            int viewsIndex = deviceViews.size() < i ? (i % DEVICE_NUMBER_PER_LINE) : i;
            int viewsIndex = i % DEVICE_NUMBER_PER_LINE;
            RelativeLayout.LayoutParams deviceParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams selectionParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            fixDeviceWidth += deviceWidth + spaceing;

            deviceParams.leftMargin = fixDeviceWidth;
            if (isInGroup) {
                deviceWidth = deviceViews.get(viewsIndex).getDeviceWidth();
                deviceHeight = deviceViews.get(viewsIndex).getDeviceHeight();
            } else {
                deviceWidth = mUnGroupHomeDeviceViews.get(i).getDeviceWidth();
                deviceHeight = mUnGroupHomeDeviceViews.get(i).getDeviceHeight();
            }
            spaceing = DensityUtil.dip2px(10);

            deviceParams.topMargin = DensityUtil.dip2px(40) + maxHeight - deviceHeight;
//            deviceParams.bottomMargin = DensityUtil.dip2px(20);
            if (isReachMaxLine) {
//                deviceParams.bottomMargin = DensityUtil.dip2px(30);
            }

            // selection leftMargin
            if (i % DEVICE_NUMBER_PER_LINE < 2) {
                selectionParams.leftMargin = deviceParams.leftMargin;
            } else {
                selectionParams.leftMargin = deviceParams.leftMargin - deviceWidth * 2;

                // no group button
                if (!isInGroup) {
                    if (AppManager.shareInstance().isAirtouchP(devices.get(i).getDeviceType())) {
                        if (mUnGroupAirPremiumNumber == 1) {
                            selectionParams.leftMargin = deviceParams.leftMargin - deviceWidth  * 3 / 2;
                        }
                    } else {
                        if (mUnGroupAirPremiumNumber == 0) {
                            selectionParams.leftMargin = deviceParams.leftMargin - deviceWidth  * 3 / 2;
                        }
                    }
                }
            }
            // selection topMargin
            selectionParams.topMargin = deviceParams.topMargin - DensityUtil.dip2px(40);

            // add device and selection to one line horizon layout
            if (isInGroup) {
                deviceViews.get(viewsIndex).setLayoutParams(deviceParams);
                selectionViews.get(viewsIndex).setLayoutParams(selectionParams);
                deviceOneLineLayout.addView(deviceViews.get(viewsIndex));
                deviceOneLineLayout.addView(selectionViews.get(viewsIndex));
            } else {
                mUnGroupHomeDeviceViews.get(i).setLayoutParams(deviceParams);
                mUnGroupSelectionViews.get(i).setLayoutParams(selectionParams);
                deviceOneLineLayout.addView(mUnGroupHomeDeviceViews.get(i));
                deviceOneLineLayout.addView(mUnGroupSelectionViews.get(i));
            }
        }
        return deviceOneLineLayout;
    }

    private List<AllDeviceAirTouchView> createDeviceViews(int line, boolean isInGroup,
                                                          boolean isReachMaxLine, List<HomeDevice> devices) {
        List<AllDeviceAirTouchView> views = new ArrayList<>();
        int maxNumber = isReachMaxLine ? devices.size() : line * DEVICE_NUMBER_PER_LINE;
        for (int i = (line - 1) * DEVICE_NUMBER_PER_LINE; i < maxNumber; i++) {
            // create device views
            AllDeviceAirTouchView deviceView = new AllDeviceAirTouchView(this);
            deviceView.updateView(devices.get(i));
            if (isInGroup) {
                mGroupHomeDeviceViews.add(deviceView);
                views.add(deviceView);
                // set index of group/un-group devices, no matter in which group.
                int index = mGroupHomeDeviceViews.size() - 1;
                mGroupHomeDeviceViews.get(index).setGroupDeviceIndex(index);
            } else {
                mUnGroupHomeDeviceViews.add(deviceView);
                mUnGroupHomeDeviceViews.get(i).setUnGroupDeviceIndex(i);
            }
            setDeviceOnClicks(deviceView, isInGroup);
        }
        return views;
    }

    private List<AllDeviceSelectionView> createSelectionViews(int line, boolean isInGroup,
                                                              boolean isReachMaxLine, List<HomeDevice> devices) {
        List<AllDeviceSelectionView> views = new ArrayList<>();
        int maxNumber = isReachMaxLine ? devices.size() : line * DEVICE_NUMBER_PER_LINE;
        for (int i = (line - 1) * DEVICE_NUMBER_PER_LINE; i < maxNumber; i++) {
            // create selection views
            AllDeviceSelectionView selectionView = new AllDeviceSelectionView(this);
            selectionView.setVisibility(View.INVISIBLE);

            // 1-2 set left background, 3-4 set right background
            if (i % DEVICE_NUMBER_PER_LINE < 2) {
                selectionView.setSelectionBackground(R.drawable.all_device_selection_bg_left);
            } else {
                selectionView.setSelectionBackground(R.drawable.all_device_selection_bg_right);
            }
            if (isInGroup) {
                mGroupSelectionViews.add(selectionView);
                views.add(selectionView);
            } else {
                mUnGroupSelectionViews.add(selectionView);
            }
            setSelectionOnClicks(selectionView);
        }
        return views;
    }

    private int getDeviceMaxHeight(int line, boolean isGroup, boolean isReachMaxLine, List<HomeDevice> devices) {
        int maxHeight = 0;
        int maxNumber = isReachMaxLine ? devices.size() : line * DEVICE_NUMBER_PER_LINE;
        for (int i = (line - 1) * DEVICE_NUMBER_PER_LINE; i < maxNumber; i++) {
            // calculate the max height of the device in one line
            int measuredHeight;
            if (isGroup)
                measuredHeight = mGroupHomeDeviceViews.get(i).getDeviceHeight();
            else
                measuredHeight = mUnGroupHomeDeviceViews.get(i).getDeviceHeight();
            maxHeight = (measuredHeight > maxHeight ?
                    measuredHeight : maxHeight);
        }
        return maxHeight;
    }

    private void setEditMode(Boolean isEditMode, Boolean isInGroup) {
        mIsEditMode = isEditMode;
        setDeviceShakable(isEditMode);
        setDeviceView(isEditMode);
        setSelectionView(isEditMode, isInGroup);
        // set big/small animation
        AnimatorSet animSet = new AnimatorSet();
        if (isEditMode) {
            animSet.setDuration(500);
            animSet.setInterpolator(new LinearInterpolator());
            animSet.playTogether(
                    ObjectAnimator.ofFloat(mSelectedDeviceView, "scaleX", 1.0f, 1.1f),
                    ObjectAnimator.ofFloat(mSelectedDeviceView, "scaleY", 1.0f, 1.1f)
            );
        } else {
            animSet.setDuration(500);
            animSet.setInterpolator(new LinearInterpolator());
            animSet.playTogether(
                    ObjectAnimator.ofFloat(mSelectedDeviceView, "scaleX", 1.1f, 1.0f),
                    ObjectAnimator.ofFloat(mSelectedDeviceView, "scaleY", 1.1f, 1.0f)
            );
        }
        animSet.start();
    }

    private void setDeviceShakable(Boolean isEditMode) {
        List<AllDeviceAirTouchView> shakeViews = new ArrayList<>();
        List<View> shakeDeviceViews = new ArrayList<>();
        for (AllDeviceAirTouchView view : mGroupHomeDeviceViews) {
            if (!view.equals(mSelectedDeviceView))
                shakeViews.add(view);
        }
        for (AllDeviceAirTouchView view : mUnGroupHomeDeviceViews) {
            if (!view.equals(mSelectedDeviceView))
                shakeViews.add(view);
        }
        for (AllDeviceAirTouchView view : shakeViews) {
            shakeDeviceViews.add(view);
        }
        if (isEditMode)
            mShakeAnimation.startShake(shakeDeviceViews);
        else
            mShakeAnimation.stopShake();
    }

    private void setDeviceOnClicks(final AllDeviceAirTouchView clickedView, final Boolean isInGroup) {
        clickedView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSelectedDeviceView = clickedView;

                if (AppConfig.shareInstance().isIndiaAccount()) {
                    MessageBox.createTwoButtonDialog(AllDeviceActivity.this, null,
                            getString(R.string.delete_device),
                            getString(R.string.yes), deleteDevice, getString(R.string.no), deleteDeviceCancel);
                } else {
                    if (!mIsEditMode) {
                        setEditMode(true, isInGroup);
                    }
                }
                return true;
            }
        });
        clickedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditMode) {
                    /*
                     * If clicked device is not selected device,
                     * change selection view and shake animation.
                     */
                    if (mSelectedDeviceView != clickedView) {
                        setEditMode(false, isInGroup);
                        mSelectedDeviceView = clickedView;
                        setEditMode(true, isInGroup);
                    }
                } else {
                    HomeDevice homeDevice = clickedView.getHomeDevice();
                    AppManager.shareInstance().setCurrentDeviceId
                            (homeDevice.getDeviceInfo().getDeviceID());
                    Intent intent = new Intent(mContext, DeviceActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("location", mLocationId);
                    bundle.putInt("deviceId", homeDevice.getDeviceInfo().getDeviceID());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, DEVICE_CONTROL_REQUEST_CODE);
                    overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
                }
            }
        });
    }

    private void setSelectionOnClicks(final AllDeviceSelectionView clickedView) {
        clickedView.getGroupButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectionView(false, false);
                HomeDevice homeDevice = mSelectedDeviceView.getHomeDevice();
                // click group
                if (clickedView.getIsGrouping()) {
                    // set drag properties
                    mDragAnimation.setDragEnable(true);
                    mDragAnimation.setDragView(mSelectedDeviceView);
                    mDragAnimation.setDragArea(mScrollView);
                    for (int i = 0; i < mGroupLayout.getChildCount(); i++) {
                        mDragAnimation.setTargetArea(mGroupLayout.getChildAt(i));
                        setupArrow(mGroupLayout.getChildAt(i));
                    }
                    for (AllDeviceAirTouchView view : mUnGroupHomeDeviceViews) {

                        if (AppManager.shareInstance().isAirtouchP(view.getHomeDevice().getDeviceType())) {
                            view.setId(view.getHomeDevice().getDeviceInfo().getDeviceID());
                            mDragAnimation.setTargetArea(view);
                            if (!view.equals(mSelectedDeviceView))
                                setupArrow(view);
                        }
                    }
                    mDragAnimation.setDragInCallback(new DragAnimation.DragCallback() {
                        @Override
                        public void dragInCallback(View view) {
                            for (GroupDataResponse oneGroup : mGroupLists) {
                                if (view.getId() == oneGroup.getGroupId()) {
                                    addDeviceToGroup(oneGroup.getGroupId());
                                    break;
                                }
                            }
                            for (int masterId : mUnGroupMasterIds) {
                                if (view.getId() == masterId) {
                                    createGroup(masterId);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void dragBeginCallback(View view) {
                            mArrow.clearCanvas();
                            mArrow.invalidate();
                        }
                    });
                } else {
                    // click unGroup
                    if (AppManager.shareInstance().isAirtouchP(homeDevice.getDeviceType())) {
                        if (homeDevice.getIsMasterDevice() == 0)
                            deleteDeviceFromGroup(getSelectGroup());
                        else
                            deleteGroup(getSelectGroup());
                    } else {
                        deleteDeviceFromGroup(getSelectGroup());
                    }
                }
            }
        });
        clickedView.getDefaultButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeDevice homeDevice = mSelectedDeviceView.getHomeDevice();
                mDefaultDeviceDBService.insertDefaultDevice(mLocationId, homeDevice.getDeviceInfo().getDeviceID());
                finish();
            }
        });
        clickedView.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditMode(false, false);

                // do not delete masterDevice
                if (mSelectedDeviceView.getHomeDevice().getIsMasterDevice() == 1) {
                    MessageBox.createSimpleDialog(AllDeviceActivity.this, null,
                            getString(R.string.group_master_not_delete), null, null);
                } else {
                    MessageBox.createTwoButtonDialog(AllDeviceActivity.this, null,
                            getString(R.string.delete_device),
                            getString(R.string.yes), deleteDevice, getString(R.string.no), null);
                }
            }
        });
    }

    private GroupDataResponse getSelectGroup() {
        for (GroupDataResponse oneGroup : mGroupLists) {
            List<DevicesForGroupResponse> groupDeviceList = oneGroup.getGroupDeviceList();
            for (DevicesForGroupResponse groupDevice : groupDeviceList) {
                if (mSelectedDeviceView.getHomeDevice().getDeviceInfo().getDeviceID()
                        == groupDevice.getDeviceId()) {
                    return oneGroup;
                }
            }
        }
        return null;
    }

    private void createGroup(int masterId) {
        String groupName;
        startGroupRequest();
        int[] deviceIds = new int[1];
        deviceIds[0] = mSelectedDeviceView.getHomeDevice().getDeviceInfo().getDeviceID();
        groupName = getString(R.string.group) + " 0" + (mGroupLists.size() + 1);
        if (isGroupNameUseOrNot(groupName))
            groupName = getString(R.string.group) + " 0" + (mGroupLists.size() + 2);
        DeviceListRequest request = new DeviceListRequest(deviceIds);
        mGroupManager.createGroup(groupName, masterId, mLocationId, request);
        mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
            @Override
            public void onSuccess(ResponseResult responseResult) {
                updateDeviceDataAndView();
            }
        });
        mGroupManager.setErrorCallback(new GroupManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, int id) {
                mDialog.dismiss();
                mSelectedDeviceView.setVisibility(View.VISIBLE);
                errorHandle(responseResult, getString(id));
            }
        });
    }

    private void addDeviceToGroup(int groupId) {
        startGroupRequest();
        int[] deviceIds = new int[1];
        deviceIds[0] = mSelectedDeviceView.getHomeDevice().getDeviceInfo().getDeviceID();
        DeviceListRequest deviceListRequest = new DeviceListRequest(deviceIds);
        mGroupManager.addDeviceToGroup(groupId, deviceListRequest);
        mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
            @Override
            public void onSuccess(ResponseResult responseResult) {
                updateDeviceDataAndView();
            }
        });
        mGroupManager.setErrorCallback(new GroupManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, int id) {
                mDialog.dismiss();
                mSelectedDeviceView.setVisibility(View.VISIBLE);
                errorHandle(responseResult, getString(id));
            }
        });
    }

    private void deleteGroup(GroupDataResponse oneGroup) {
        if (oneGroup == null){
            return;
        }
        startGroupRequest();
        mGroupManager.deleteGroup(oneGroup.getGroupId());
        mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
            @Override
            public void onSuccess(ResponseResult responseResult) {
                updateDeviceDataAndView();
            }
        });
        mGroupManager.setErrorCallback(new GroupManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, int id) {
                mDialog.dismiss();
                errorHandle(responseResult, getString(id));
            }
        });
    }

    private void deleteDeviceFromGroup(GroupDataResponse oneGroup) {
        startGroupRequest();
        int[] deviceIds = new int[1];

        if (oneGroup == null || mSelectedDeviceView == null || mSelectedDeviceView.getHomeDevice() == null
                || mSelectedDeviceView.getHomeDevice().getDeviceInfo() == null) {
            updateDeviceDataAndView();
            return;
        }

        deviceIds[0] = mSelectedDeviceView.getHomeDevice().getDeviceInfo().getDeviceID();
        DeviceListRequest deviceListRequest = new DeviceListRequest(deviceIds);
        mGroupManager.deleteDeviceFromGroup(oneGroup.getGroupId(), deviceListRequest);
        mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
            @Override
            public void onSuccess(ResponseResult responseResult) {
                updateDeviceDataAndView();
            }
        });
        mGroupManager.setErrorCallback(new GroupManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, int id) {
                mDialog.dismiss();
                errorHandle(responseResult, getString(id));
            }
        });
    }

    private void setDeviceView(Boolean isEditMode) {
        if (isEditMode) {
            for (AllDeviceAirTouchView deviceView : mGroupHomeDeviceViews) {
                deviceView.getDeviceImageView().setAlpha(0.7f);
            }
            for (AllDeviceAirTouchView deviceView : mUnGroupHomeDeviceViews) {
                deviceView.getDeviceImageView().setAlpha(0.7f);
            }
            mSelectedDeviceView.getDeviceImageView().setAlpha(1.0f);
        } else {
            for (AllDeviceAirTouchView deviceView : mGroupHomeDeviceViews) {
                deviceView.getDeviceImageView().setAlpha(1.0f);
            }
            for (AllDeviceAirTouchView deviceView : mUnGroupHomeDeviceViews) {
                deviceView.getDeviceImageView().setAlpha(1.0f);
            }
        }
    }

    private void setSelectionView(Boolean isEditMode, Boolean isInGroup) {
        AllDeviceSelectionView groupSelectionView;
        AllDeviceSelectionView unGroupSelectionView;
        int groupIndex = mSelectedDeviceView.getGroupDeviceIndex();
        int unGroupIndex = mSelectedDeviceView.getUnGroupDeviceIndex();
        if (isEditMode) {
            if (isInGroup) {
                groupSelectionView = mGroupSelectionViews.get(groupIndex);
                groupSelectionView.setVisibility(View.VISIBLE);
                groupSelectionView.setGroupButtonEnable(true);
                groupSelectionView.setGroupText(false);
            } else {
                unGroupSelectionView = mUnGroupSelectionViews.get(unGroupIndex);
                unGroupSelectionView.setVisibility(View.VISIBLE);
                unGroupSelectionView.setGroupText(true);
                // set group button enable/disable
                unGroupSelectionView.setGroupButtonEnable(true);
                if (mGroupSelectionViews.isEmpty()) {
                    if (AppManager.shareInstance().isAirtouchP(mSelectedDeviceView.getHomeDevice().getDeviceType())) {
                        if (mUnGroupAirPremiumNumber == 1)
                            unGroupSelectionView.setGroupButtonEnable(false);
                    } else {
                        if (mUnGroupAirPremiumNumber == 0)
                            unGroupSelectionView.setGroupButtonEnable(false);
                    }
                }
            }
        } else {
            /*
             * regardless of isInGroup, clear view whatsoever
             * setSelectionView(false, false);
             */
            if (!mGroupSelectionViews.isEmpty())
                mGroupSelectionViews.get(groupIndex).setVisibility(View.INVISIBLE);
            if (!mUnGroupSelectionViews.isEmpty())
                mUnGroupSelectionViews.get(unGroupIndex).setVisibility(View.INVISIBLE);
            mDragAnimation.setDragEnable(false);
            mArrow.clearCanvas();
            mArrow.invalidate();
        }
    }

    private void startGroupRequest() {
        mDialog = LoadingProgressDialog.show(mContext, getString(R.string.enroll_loading));
        setEditMode(false, false);
    }

    private void setupArrow(View targetView) {
        int[] src = new int[2];
        int[] dest = new int[2];
        mSelectedDeviceView.getLocationInWindow(src);
        mSelectedDeviceView.getLocationOnScreen(src);
        targetView.getLocationInWindow(dest);
        targetView.getLocationOnScreen(dest);
        int x1 = src[0] + mSelectedDeviceView.getDeviceWidth() / 3 * 2;
        int y1 = src[1] + mSelectedDeviceView.getDeviceHeight() / 2;
        int x2 = 0;
        if (targetView instanceof AllDeviceAirTouchView) {
            x2 = dest[0] + targetView.getWidth() / 3 * 2;
        } else {
            x2 = dest[0] + targetView.getWidth() / 2;
        }
        int y2 = dest[1] + targetView.getHeight() / 2;
        mArrow.setXY(x1, y1, x2, y2);
        mArrow.invalidate();
    }

    public void registerRunStatusChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AirTouchConstants.SHORTTIME_REFRESH_END_ACTION);
        mRunStatusChangedReceiver = new RunStatusChangedReceiver();
        registerReceiver(mRunStatusChangedReceiver, intentFilter);
    }

    public void unRegisterRunStatusChangedReceiver() {
        if (mRunStatusChangedReceiver != null) {
            unregisterReceiver(mRunStatusChangedReceiver);
        }
    }

    private class RunStatusChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isRefreshingData = true;
            String action = intent.getAction();
            if (AirTouchConstants.SHORTTIME_REFRESH_END_ACTION.equals(action)) {
                updateDeviceDataAndView();
            }
        }
    }

    private void clearAllData() {
        mUnGroupAirPremiumNumber = 0;
        mGroupHomeDevicesList.clear();
        mUnGroupHomeDevices.clear();
        mGroupLists.clear();
    }

    private void clearAllViews() {
        mGroupHomeDeviceViews.clear();
        mUnGroupHomeDeviceViews.clear();
        mGroupSelectionViews.clear();
        mUnGroupSelectionViews.clear();
        mGroupLayout.removeAllViews();
        mUnGroupLayout.removeAllViews();
    }

    private MessageBox.MyOnClick deleteDevice = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            mIsEditMode = true;
            deleteDeviceProcess();
        }
    };

    private MessageBox.MyOnClick deleteDeviceSuccess = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            mDialog = LoadingProgressDialog.show(mContext, getString(R.string.enroll_loading));
            updateDeviceDataAndView();
        }
    };

    private MessageBox.MyOnClick deleteDeviceCancel = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {

        }
    };

    private void deleteDeviceProcess() {
        final HomeDevice homeDevice = mSelectedDeviceView.getHomeDevice();
        mDialog = LoadingProgressDialog.show(mContext, getString(R.string.deleting_device));
        GroupManager groupManager = new GroupManager();
        groupManager.deleteDevice(homeDevice.getDeviceInfo().getDeviceID());
        groupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
            @Override
            public void onSuccess(ResponseResult responseResult) {
                mDialog.dismiss();
                mIsEditMode = false;
                if (mDefaultDeviceDBService.findDefaultByLocationID(mLocationId) == homeDevice.getDeviceInfo().getDeviceID()) {
                    mDefaultDeviceDBService.deleteDefaultDevice(homeDevice.getDeviceInfo().getDeviceID());
                }

                MessageBox.createSimpleDialog(AllDeviceActivity.this, null,
                        getString(R.string.delete_device_success), null, deleteDeviceSuccess);
            }
        });
        groupManager.setErrorCallback(new GroupManager.ErrorCallback() {
            @Override
            public void onError(ResponseResult responseResult, int id) {
                mIsEditMode = false;
                mDialog.dismiss();
                errorHandle(responseResult, getString(id));
            }
        });
    }

    private boolean isGroupNameUseOrNot(String groupName) {
        for (GroupDataResponse groupData: mGroupLists) {
            if (groupName.equalsIgnoreCase(groupData.getGroupName()))
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSelectedDeviceView != null) {
            mSelectedDeviceView.stopTyperTimer(mSelectedDeviceView.getmPm25TextView(), mSelectedDeviceView.getmTvocTextView());
        }
    }
}