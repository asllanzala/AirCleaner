package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.ErrorResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseRequestFragment;
import com.honeywell.hch.airtouchv2.framework.model.modelinterface.IRefreshEnd;
import com.honeywell.hch.airtouchv2.framework.model.xinzhi.WeatherData;
import com.honeywell.hch.airtouchv2.framework.sensor.ShakeListener;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.view.ScrollLayout;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Use the {@link HomeHalfPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeHalfPageFragment extends BaseRequestFragment {
    public static final String ARG_HOME_INDEX = "homeIndex";
    private static final String TAG = "AirTouchHomePage";

    private int mHomeIndex = 0;
    private UserLocation mUserLocation = null;
    private FragmentActivity mActivity;

    private BaseLocationFragment mLocationFragment;

    // shake to refresh data (both device and outdoor data)
    private ShakeListener mShakeListener = null;
    private boolean isShaking = false;
    private View halfPageView = null;

    private boolean isShakeEnabled = false;

    private LinearLayout nodeviceTipLayout;
    private ScrollLayout mScrollLayout;

    //when the home cell has no device,it will need show tip to user when user
    //scroll down the pager
    private Animation translateInAnimation;
    private Animation translateOutAnimation;

    private TextView scrollTopHit;

    private int mHomeAirTouchSeriesDeviceNumber = 0;

    private String[] titlesString ;

    public static HomeHalfPageFragment newInstance(FragmentActivity activity, int homeIndex) {
        HomeHalfPageFragment fragment = new HomeHalfPageFragment();
        fragment.setActivity(activity);
        Bundle args = new Bundle();
        args.putInt(ARG_HOME_INDEX, homeIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public static HomeHalfPageFragment newInstance(FragmentActivity activity) {
        HomeHalfPageFragment fragment = new HomeHalfPageFragment();
        fragment.setActivity(activity);
        return fragment;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    public void updateData(int homeIndex, UserLocation userLocation) {
        mHomeIndex = homeIndex;
        mUserLocation = userLocation;
        initAllDevices();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        if (getArguments() != null) {
            mHomeIndex = getArguments().getInt(ARG_HOME_INDEX);
        }

        if (mHomeIndex == 0){
            loadFragmentList();

            return;
        }
        ArrayList<UserLocation> localList = AuthorizeApp.shareInstance().getUserLocations();
        if (localList != null && mHomeIndex > 0 && localList.size() > mHomeIndex - 1) {
            mUserLocation = localList.get(mHomeIndex - 1);

        }
        mShakeListener = new ShakeListener(getFragmentActivity());
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener()
        {
            @Override
            public void onShake()
            {
                if (isShakeEnabled && !isShaking && mHomeIndex == ((MainActivity)
                        getFragmentActivity()).getCurrentHomeIndex())
                {
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "shaking " + mHomeIndex);
                    isShaking = true;
                    mLocationFragment.updateWeatherData();
                    int getLocationRequestId = getRequestClient().getLocation(AuthorizeApp
                            .shareInstance().getUserID(), AuthorizeApp.shareInstance()
                            .getSessionId(), mReceiveResponse);
                    addRequestId(getLocationRequestId);
                }
            }
        });
        loadFragmentList(mHomeIndex);
        titlesString = new String[]{getResources().getString(R.string.emotion_no_network),getResources().getString(R.string.emotion_no_device_tip)};

    }


    @Override
    public void onPause() {
        super.onPause();

        isShakeEnabled = false;
    }

    public FragmentActivity getFragmentActivity() {
        if (mActivity == null)
            mActivity = getActivity();
        return mActivity;
    }

    @Override
    public void onResume() {
        super.onResume();

        isShakeEnabled = true;

        if (getArguments() != null) {
            mHomeIndex = getArguments().getInt(ARG_HOME_INDEX);
        }

        addFragmentView();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (halfPageView == null){
            halfPageView = inflater.inflate(R.layout.fragment_home_page_temp, container, false);
//            emotionPagerMainView = (EmotionPagerMainView)halfPageView.findViewById(R.id.emotion_page);
//            if (mHomeIndex != 0)
//            {
//                emotionPagerMainView.setLocationId(mUserLocation.getLocationID());
//            }
            initScrollLayout(halfPageView);
        }
        if (halfPageView.getParent() != null){
            ViewGroup p = (ViewGroup)halfPageView.getParent();
            p.removeAllViews();
        }
        return halfPageView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        titlesString = new String[]{getResources().getString(R.string.emotion_no_network),getResources().getString(R.string.emotion_no_device_tip)};

    }


    IReceiveResponse mReceiveResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            removeRequestId(httpRequestResponse.getRandomRequestID());
            switch (httpRequestResponse.getRequestID()) {
                case GET_LOCATION:
                    LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "get all location end");
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            try {
                                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());

                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseJSON = responseArray.getJSONObject(i);
                                    UserLocation userLocation = new Gson().fromJson(responseJSON.toString(),
                                            UserLocation.class);
                                    if (userLocation.getLocationID() == mUserLocation
                                            .getLocationID()) {
                                        mUserLocation = userLocation;
                                        ArrayList<UserLocation> userLocations = AuthorizeApp
                                                .shareInstance().getUserLocations();
                                        if (userLocations != null && mHomeIndex > 0 && mHomeIndex - 1 > 0) {
                                            userLocations.set(mHomeIndex - 1, userLocation);

                                        }

                                        // get devices of current home
//                                        int getHomePm25RequestId = getRequestClient()
//                                                .getHomePm25(userLocation.getLocationID(),
//                                                        AuthorizeApp.shareInstance().getSessionId(),
//                                                        RequestID.GET_HOME_PM25,
//                                                        mReceiveResponse);
//                                        addRequestId(getHomePm25RequestId);
                                        mUserLocation.loadHomeDevicesData(new IRefreshEnd()
                                        {
                                            @Override
                                            public void notifyDataRefreshEnd()
                                            {
                                                mHomeAirTouchSeriesDeviceNumber++;
                                                if (mHomeAirTouchSeriesDeviceNumber == mUserLocation.getAirTouchSDeviceNumber()) {
                                                    if (isShaking) {
                                                        isShaking = false;
                                                        if (!isHasNullPoint()) {
                                                            initAllDevices();
                                                        }
                                                    }
                                                }

                                            }
                                        });
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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

    private Boolean isHasNullPoint() {
        return mUserLocation == null || mUserLocation.getHomeDevices() == null
                || mUserLocation.getDeviceInfo() == null;
    }

    /**
     * fetch all devices data to home/location.
     */
    private void initAllDevices() {
//        int deviceNumber = mUserLocation.getHomeDevicesPM25().size();
//        if (deviceNumber > 0) {
//            ArrayList<HomeDevice> homeDevices = new ArrayList<>();
//            ArrayList<HomeDevicePM25> homeDevicesPM25 = mUserLocation.getHomeDevicesPM25();
//            ArrayList<DeviceInfo> devicesInfo = mUserLocation.getDeviceInfo();
//            for (int i = 0; i < devicesInfo.size(); i++) {
//                HomeDevice homeDevice = new HomeDevice();
//                homeDevice.setHomeDevicePm25(homeDevicesPM25.get(i));
//                homeDevice.setDeviceInfo(devicesInfo.get(i));
//                homeDevices.add(homeDevice);
//            }
//            mUserLocation.setHomeDevices(homeDevices);
//
//            ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();
//
//            if (userLocations != null && mHomeIndex > 0 && userLocations.size() > mHomeIndex - 1) {
//                userLocations.get(mHomeIndex - 1).setHomeDevicesPM25(homeDevicesPM25);
//            }
//
//        }
        updateLocation();

    }

    public void updateLocation() {
        if (mHomeIndex != 0 && mLocationFragment != null && mLocationFragment != null) {
            ((HomeCellFragment) mLocationFragment).updateViewData(mHomeIndex, mUserLocation);
            ((HomeCellFragment) mLocationFragment).bindData2View();
        }
    }

    public void switchTimeView() {
        if (mLocationFragment != null) {
            mLocationFragment.setDaylight();
        }

    }

    public void loadFragmentList(int homeIndex) {
        mLocationFragment = HomeCellFragment.newInstance(getFragmentActivity(), homeIndex);
        mLocationFragment.setHomeHalfFragment(this);
    }

    public void loadFragmentList() {
        mLocationFragment = CurrentGpsFragment.newInstance(getFragmentActivity());
    }

    private void addFragmentView() {
        mLocationFragment.setDaylight();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (!mLocationFragment.isAdded()){
            fragmentTransaction.add(R.id.home_cell, mLocationFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();
            if (mHomeIndex != 0 && userLocations != null && userLocations.size() > mHomeIndex - 1) {
                mUserLocation = AuthorizeApp.shareInstance().getUserLocations().get(mHomeIndex - 1);
                updateData(mHomeIndex, mUserLocation);
            }
        }
        else{
            updateWeatherData(mLocationFragment.getFragmentWeatherData());
        }
    }
    public HomeCellFragment getHomeCellFragment() {
        return (HomeCellFragment) mLocationFragment;
    }

    public CurrentGpsFragment getGpsFragment() {
        return (CurrentGpsFragment) mLocationFragment;
    }

    public BaseLocationFragment getBaseLocationFragment() {
        return  mLocationFragment;
    }

    public void updateWeatherData(WeatherData weatherData) {
        mLocationFragment.getSkyView().updateViewData(weatherData);
    }

    private void errorHandle(HTTPRequestResponse httpRequestResponse) {
        if (httpRequestResponse.getException() != null) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + httpRequestResponse.getException());
            MessageBox.createSimpleDialog(getFragmentActivity(), null,
                    getString(R.string.no_network), null, null);
            return;
        }

        if (isShaking)
            isShaking = false;

        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
            try {
                JSONArray responseArray = new JSONArray(httpRequestResponse.getData());
                JSONObject responseJSON = responseArray.getJSONObject(0);
                ErrorResponse errorResponse = new Gson().fromJson(responseJSON.toString(),
                        ErrorResponse.class);
                MessageBox.createSimpleDialog(getFragmentActivity(), null,
                        getString(R.string.no_network), null, null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MessageBox.createSimpleDialog(getFragmentActivity(), null, getString(R.string.enroll_error),
                    null, null);
        }
    }


    private void initScrollLayout(View view)
    {
        nodeviceTipLayout = (LinearLayout) view.findViewById(R.id.nodevice_tip_layout);
        mScrollLayout = (ScrollLayout) view.findViewById(R.id.half_scroll);
        mScrollLayout.setOnFirstPageDownListener(noDevieTipListener);

        mScrollLayout.setIsScroll(mHomeIndex != 0 ? true : false);

        translateInAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.control_translate_in);
        translateOutAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.control_translate_out);
        translateInAnimation.setAnimationListener(new translateInAnimationListener());
        translateOutAnimation.setAnimationListener(new translateOutAnimationListener());

        scrollTopHit = (TextView)view.findViewById(R.id.nodevice_emotion_tip);

//        mScrollLayout = (ScrollLayout) view.findViewById(R.id.home_scroll_layout);
//
//        mScrollLayout.setIsScroll(false);
//
//        mScrollLayout.setOnViewChangeListener(new ScrollLayout.OnViewChangeListener()
//        {
//            @Override
//            public void OnViewChange(int index)
//            {
//                if (index == 0)
//                {
//                    //tanslate the nearMoutain to invisible
//                    ((MainActivity) mActivity).setmTopViewPagerDistance(DensityUtil
//                            .getScreenHeight() + 200);
//
//                    //if first time to scroll emotionpager,need to request yesterday data
//                    //because the ParticleFactory is static class
//                    if (isFirstScrollToEmotionPage)
//                    {
//                        isFirstScrollToEmotionPage = false;
//                        emotionPagerMainView.requestYesterdayFirstTime();
//                    }
//                }
//                else
//                {
//                    //tanslate the nearMoutain to visible
//                    ((MainActivity) mActivity).setmTopViewPagerDistance(0);
//                    ((MainActivity) getFragmentActivity()).setmViewPagerScroll(true);
//                }
//            }
//        });
//        mScrollLayout.setOnViewScrollingListener(new ScrollLayout.OnViewScrollingListener()
//        {
//            @Override
//            public void onSrcollY(float scrollY)
//            {
//                ((MainActivity) getFragmentActivity()).setmViewPagerScroll(false);
//                ((MainActivity) mActivity).setmTopViewPagerDistance(DensityUtil.getScreenHeight() - scrollY);
//            }
//        });

    }

    public void setScrollViewCanScroll(boolean isCanScroll)
    {
//        mScrollLayout.setIsScroll(isCanScroll);
    }

    /**
     *clear partilce list,stop particle moving thread,and recyle bitmap
     */
    public void stopEmtionParticle()
    {
//        if (emotionPagerMainView != null)
//        {
//            emotionPagerMainView.stopParticleMoving();
//        }
    }

    /**
     * Animation helper
     */
    private class translateInAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            nodeviceTipLayout.startAnimation(translateOutAnimation);
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

    /**
     * Animation helper
     */
    private class translateOutAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            ((MainActivity)getFragmentActivity()).setMenuBtnVisible(View.VISIBLE);
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

    ScrollLayout.OnFirstPageDownListener noDevieTipListener = new ScrollLayout.OnFirstPageDownListener() {
        @Override
        public void onPageDwon() {

            ((MainActivity)getFragmentActivity()).setMenuBtnVisible(View.INVISIBLE);
            nodeviceTipLayout.startAnimation(translateInAnimation);


        }
    };

    /**
     * set hint scroll can be scrolled or not and set the hint according if it has network or not
     * @param isCanScroll
     * @param isNoNetwork
     */
    public void setIsScroll(boolean isCanScroll,boolean isNoNetwork)
    {
        if (isNoNetwork && scrollTopHit != null)
        {
            scrollTopHit.setText(titlesString[0]);
        }
        else if (scrollTopHit != null)
        {
            scrollTopHit.setText(titlesString[1]);
        }
        if (mScrollLayout != null)
        {
            mScrollLayout.setIsScroll(isCanScroll);
        }
    }

    public void showHasDeviceButNoNetwork()
    {
        if (scrollTopHit != null)
        {
            scrollTopHit.setText(titlesString[0]);
        }
        if (translateInAnimation != null && !translateInAnimation.hasStarted() &&
                translateOutAnimation != null && !translateOutAnimation.hasStarted())
        {
            ((MainActivity)getFragmentActivity()).setMenuBtnVisible(View.INVISIBLE);
            nodeviceTipLayout.startAnimation(translateInAnimation);
        }
    }

    /**
     * get location of the homePager
     * @return
     */
    public UserLocation getHomeHalfPageLocation(){
        return mUserLocation;
    }



}
