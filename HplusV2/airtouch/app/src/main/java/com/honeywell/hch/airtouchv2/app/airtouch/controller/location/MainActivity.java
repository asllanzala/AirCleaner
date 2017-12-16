package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.TestActivity;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.device.DeviceActivity;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.device.HouseActivity;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.emotion.EmotionPagerMainView;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment.EnrollWelcomeActivity;
import com.honeywell.hch.airtouchv2.app.airtouch.controller.manual.ManualActivity;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.app.authorize.controller.MobileDoneActivity;
import com.honeywell.hch.airtouchv2.app.authorize.controller.UserEditHomeActivity;
import com.honeywell.hch.airtouchv2.app.authorize.controller.UserLoginActivity;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseFragmentActivity;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.config.UserConfig;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.share.ShareUtility;
import com.honeywell.hch.airtouchv2.framework.view.CustomViewPager;
import com.honeywell.hch.airtouchv2.framework.view.HazeView;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.view.ScrollLayout;
import com.honeywell.hch.airtouchv2.framework.view.residemenu.ResideMenu;
import com.honeywell.hch.airtouchv2.framework.view.residemenu.ResideMenuItem;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.NetWorkUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.honeywell.hch.airtouchv2.lib.util.UmengUtil;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Jin Qian on 1/22/2015.
 */
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    private static final String TAG = "AirTouchMain";
    public static final String TIME_CHANGE_ACTION = "TimeChange";

    private ResideMenu resideMenu;
    private ResideMenuItem itemLine0, itemLine1, itemLine2, itemLine3;
    private ResideMenuItem itemNick;
    private ResideMenuItem placesCare;
    private ResideMenuItem editPlaces;
    private ArrayList<ResideMenuItem> itemUserHomeList = new ArrayList<>();
    private ResideMenuItem itemAddDevice;
    private ResideMenuItem itemUserGuide;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemLogin;
    private ResideMenuItem itemTest;
    private CustomViewPager mViewPager = null;
    private CustomTopViewPager mTopViewPager = null;

    private ImageButton menuButton = null; // menu button on left upper side of home page
    protected Button mSwitchTimeButton = null;

    private int mCurrentHomeIndex = 0;
    private boolean isHideHomeList = false;
    private List<HomeHalfPageFragment> mHomeList = new ArrayList<>();
    private List<TopViewFragment> mTopList = new ArrayList<>();

    private HomePageAdapter mHomePageAdapter = null;
    private TopViewPageAdapter mTopPageAdapter = null;

    private BroadcastReceiver userLoginChangedReceiver;
    private TimeChangeReceiver mTimeChangeReceiver;
    private static ChangeDefaultHomeListener changeDefaultHomeListener;

    //set false status when receive  loginchange broadcast if homePage is covered
    //otherwise set true status
    private boolean isUpdateHomePage = true;

    private boolean isAddHome = false;

    private boolean isAddDevice = false;

    private static final int ADD_HOME_STATUS = 1;
    private static final int LOGIN_STATUS = 2;

    private HazeView mHazeView;

    private Map<Integer, Integer> localPmValueMap = new HashMap<Integer, Integer>();

    private int localId;

    private RelativeLayout tutorialMask;

    private ScrollLayout mScrollLayout;

    //the home index before the scroll
    private int preScrollHomeIndex = 0;

    private RelativeLayout mTopViewLayout;

    private EmotionPagerMainView emotionPagerMainView;

    private boolean isNeedToRequest = true;

    private boolean isActivityResume = false;


    private BroadcastReceiver networkChangeBroadcastRec = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                if (!NetWorkUtil.isNetworkAvailable(context) && isActivityResume) {
                    dealNoNetwork();
                } else {
                    setScrollStatusWhenNetworkChange(true);

                }
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        super.TAG = TAG;
        mHazeView = (HazeView) findViewById(R.id.haze_id);
        mViewPager = (CustomViewPager) findViewById(R.id.home_viewPager);
        mTopViewPager = (CustomTopViewPager) findViewById(R.id.top_viewpager);
        mTopViewPager.setFollowViewPager(mViewPager);
        mViewPager.setFollowViewPager(mTopViewPager);

        menuButton = (ImageButton) findViewById(R.id.side_menu);
        menuButton.setOnClickListener(this);

        tutorialMask = (RelativeLayout) findViewById(R.id.tutorial_mask);
        emotionPagerMainView = (EmotionPagerMainView) findViewById(R.id.emotion_page);

        mTopViewLayout = (RelativeLayout) findViewById(R.id.viewpager_cont);

        setUpResideMenu();
        registerUserAliveChangedReceiver();
        registerTimeChangeReceiver();

        registerNetworkChangeBroadcast();

        // for test day or night
        mSwitchTimeButton = (Button) findViewById(R.id.switch_time);
        mSwitchTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.isDebugMode) {
                    AppConfig.shareInstance().setDaylight(!AppConfig.shareInstance().isDaylight());
                    emotionPagerMainView.switchBgAccordingTime();
                    for (int i = 0; i < mHomeList.size(); i++) {
                        mHomeList.get(i).switchTimeView();
                    }
                    mHazeView.setHazeViewVisible(View.VISIBLE);

                }
            }
        });
        updateUserLogin();
        updateMenuItems();
        initScrollLayout();
//        if (!AppConfig.shareInstance().isHomePageCover()) {
        updateViewPagerWhenCreate();
//        }
        setmViewPagerScroll(true);

        emotionPagerMainView.switchBgAccordingTime();
    }

    View.OnClickListener tutorialOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tutorialMask.setVisibility(View.INVISIBLE);
            AppConfig appConfig = AppConfig.shareInstance();
            appConfig.setIsHomeTutorial(true);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "onStart home clear");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHazeView != null) {
            mHazeView.destroyView();
        }
        unRegisterLocationChangedReceiver();
        unRegisterTimeChangeReceiver();
        unregisterNetworkBroadcast();

        if (AuthorizeApp.shareInstance().isAutoLogin() && !StringUtil.isEmpty(AuthorizeApp
                .shareInstance().getMobilePhone())) {
            AuthorizeApp.shareInstance().setIsGetAllData(false);
        }
        // for a special case
        if (!AuthorizeApp.shareInstance().isRemember()) {
            AuthorizeApp.shareInstance().setIsLoginSuccess(false);
        }

        //stop partilce moving and recycle bitmap when activity destory
        /**
         *clear partilce list,stop particle moving thread,and recyle bitmap
         */
        if (emotionPagerMainView != null) {
            emotionPagerMainView.stopParticleMoving();
            emotionPagerMainView.recycleShareBitmap();
        }


        isActivityResume = false;

        // release application's RAM
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        System.exit(0);
    }

    public int getCurrentHomeIndex() {
        return mCurrentHomeIndex;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    private void initScrollLayout() {
        mScrollLayout = (ScrollLayout) findViewById(R.id.home_scroll_layout);
        mScrollLayout.setIsScroll(false);
        mScrollLayout.setOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
            @Override
            public void OnViewChange(int index) {
                if (index == 0) {
                    if (isNeedToRequest) {
                        isNeedToRequest = false;
                        emotionPagerMainView.requestYesterdayFirstTime();
                    }

                    //when emotionpager is shown,reside menu can not allow to scroll
                    if (resideMenu != null) {
                        resideMenu.setResideMenuScroll(false);
                    }
                } else if (resideMenu != null) {
                    //when emotionpager is not shown,reside menu  allow to scroll
                    resideMenu.setResideMenuScroll(true);
                }
            }
        });
        mScrollLayout.setOnViewTouchListener(new ScrollLayout.OnViewTouchListener() {
            @Override
            public void OnViewTouch(int index) {
            }
        });
        mScrollLayout.setOnViewScrollingListener(new ScrollLayout.OnViewScrollingListener()
        {
            @Override
            public void onSrcollY(float scrollY)
            {
                emotionPagerMainView.hideEmotionShareDummyLayout();
            }
        });
    }


    /**
     * init and update home page fragment
     */
    private void updateHomePage(int status) {
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "update home fragment");

        if (AuthorizeApp.shareInstance().isLoginSuccess()) {
            loadHomeList();

            if (mHomeList.size() == 1) {
                mViewPager.setCurrentItem(0);
                mTopViewPager.setCurrentItem(0);
            }

            int defaultHomeNumber = AuthorizeApp.shareInstance().getDefaultHomeNumber();
            if (defaultHomeNumber < mHomeList.size()) {
                mViewPager.setCurrentItem(defaultHomeNumber + 1);
                mTopViewPager.setCurrentItem(defaultHomeNumber + 1);
                mCurrentHomeIndex = status == ADD_HOME_STATUS ? mHomeList.size() - 1 : defaultHomeNumber + 1;
            }

            if (!AppConfig.isHomeTutorial) {
                tutorialMask.setVisibility(View.VISIBLE);
                tutorialMask.setOnClickListener(tutorialOnClick);
            } else {
                tutorialMask.setVisibility(View.INVISIBLE);
            }
        } else if (!AppConfig.shareInstance().isHomePageCover()) {
            logout();
        }

    }

    private void logoutUpdateHomePage() {
        mHomePageAdapter.logoutClear();
        mHomeList.clear();
        mHomeList.add(HomeHalfPageFragment.newInstance(this));

        mTopPageAdapter.logoutClear();
        mTopList.clear();
        mTopList.add(TopViewFragment.newInstance(this, 0));

        mCurrentHomeIndex = 0;
        mHomePageAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mCurrentHomeIndex);

        mTopPageAdapter.notifyDataSetChanged();
        mTopViewPager.setCurrentItem(mCurrentHomeIndex);

//        CurrentGpsFragment cgpsFragment = mHomeList.get(0).getGpsFragment();
//        if (cgpsFragment != null) {
//            cgpsFragment.updateGpsFragment();
//
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppConfig.shareInstance().setHomePageCover(false);
        mHazeView.setHazeViewVisible(View.VISIBLE);

        //use FragmentManager.remove should be Make sure that activity is onResume
        //otherwise activity will be black
        if (!AuthorizeApp.shareInstance().isLoginSuccess()) {
            logout();
            return;
        }
        //receive loginChange broadcast when homePage is covered,need to update
        //homelist if the activity return homepage from another activity
        if (!isUpdateHomePage) {
            isUpdateHomePage = true;
            updateMainActivityDisplay();
            updateHomeDeviceInfo();
        }

        if (isAddDevice) {

            addDeviceToHome();
        }

        if (!NetWorkUtil.isNetworkAvailable(this)) {
            dealNoNetwork();
        } else {
            setScrollStatusWhenNetworkChange(true);
        }


        isActivityResume = true;

    }

    private void addDeviceToHome(){
        isAddDevice = false;
        ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();

        if (userLocations != null) {
            for (int i = 0; i < userLocations.size(); i++) {
                if (userLocations.get(i).getLocationID() == localId) {
                    mCurrentHomeIndex = i + 1;

                    UserLocation mUserLocation = AuthorizeApp.shareInstance().getUserLocations().get(i);
                    mHomeList.get(mCurrentHomeIndex).updateData(mCurrentHomeIndex, mUserLocation);
                    mViewPager.setCurrentItem(mCurrentHomeIndex);
                    mTopViewPager.setCurrentItem(mCurrentHomeIndex);

                    break;
                }
            }
        }
    }

    private void updateMainActivityDisplay(){
        updateUserLogin();
        updateMenuItems();
        if (isAddHome) {
            isAddHome = false;
            updateHomePage(ADD_HOME_STATUS);
            mCurrentHomeIndex = mHomeList.size() - 1;
            mViewPager.setCurrentItem(mCurrentHomeIndex);

            mTopViewPager.setCurrentItem(mCurrentHomeIndex);
        } else {
            updateHomePage(LOGIN_STATUS);
        }

        CurrentGpsFragment gpsFragment = mHomeList.get(0).getGpsFragment();
        if (gpsFragment != null)
            gpsFragment.updateGpsFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHazeView.setHazeViewVisible(View.GONE);

        int defaultHomeNumber = AuthorizeApp.shareInstance().getDefaultHomeNumber();
        UserConfig userConfig = new UserConfig(MainActivity.this);
        userConfig.saveDefaultHomeNumber(AuthorizeApp.shareInstance().getMobilePhone(),
                defaultHomeNumber);
    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityResume = false;
        AppConfig.shareInstance().setHomePageCover(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            MessageBox.createTwoButtonDialog(MainActivity.this, null, getString(R.string
                    .quit),getString(R.string.no), null,
                    getString(R.string.yes), quitEnroll);
        }

        return false;
    }

    private MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void updateViewPagerWhenCreate() {
        mHomeList.add(HomeHalfPageFragment.newInstance(this));
        mTopList.add(TopViewFragment.newInstance(this, 0));
        mTopPageAdapter = new TopViewPageAdapter(getSupportFragmentManager(), mTopList);
        mTopViewPager.setAdapter(mTopPageAdapter);

        mHomePageAdapter = new HomePageAdapter(getSupportFragmentManager(), mHomeList);
//        mHomePageAdapter.setMainActivity(this);
        mViewPager.setAdapter(mHomePageAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mCurrentHomeIndex = position;
                //when scroll the fragment,set the emotionpager can srcoll down or not
                if (mScrollLayout != null) {
                    setTwoScrollStatusWhenPagerScrollDown();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    if (mCurrentHomeIndex != preScrollHomeIndex && preScrollHomeIndex < mHomeList
                            .size()) {
                        //if the bubble is show ,we should hide the bubble and show the outdoor
                        // weather
                        BaseLocationFragment baseLocationFragment = mHomeList.get
                                (preScrollHomeIndex).getBaseLocationFragment();
                        if (baseLocationFragment != null && baseLocationFragment.getSkyView()
                                .getIsBubbleShowStatus()) {
                            baseLocationFragment.showOutDoorWeatherAnimation();
                        }

                        //init the emotionpage to the default status
                        emotionPagerMainView.setEmotionPagerToDefaultSatus();
                        isNeedToRequest = true;
                    }
                    preScrollHomeIndex = mCurrentHomeIndex;
                    int currentPmValue = 0;
                    Integer pmValueObject = localPmValueMap.get(mCurrentHomeIndex);
                    if (pmValueObject != null) {
                        currentPmValue = pmValueObject.intValue();
                    }
                    mHazeView.setHazeMove(currentPmValue);


                }
                if (state == 2) {
                    preScrollHomeIndex = mCurrentHomeIndex;
                }
            }
        });

        if (AuthorizeApp.shareInstance().isLoginSuccess() && AuthorizeApp.shareInstance()
                .isGetAllData()) {
            updateUserLogin();
            updateMenuItems();
            updateHomePage(LOGIN_STATUS);
        }

        mViewPager.setCurrentItem(mCurrentHomeIndex);
        mTopViewPager.setCurrentItem(mCurrentHomeIndex);
    }

    public void loadHomeList() {
        ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();

        if (userLocations == null || userLocations.size() == 0) {
            mCurrentHomeIndex = 0;
            clearHomeListWhenUserLocalIsNull();
        } else {
            updateHomeListAccountResult(userLocations);
        }

//        updateHomeDeviceInfo();
        mHomePageAdapter.notifyDataSetChanged();
        mTopPageAdapter.notifyDataSetChanged();
    }

    private void setTwoScrollStatusWhenPagerScrollDown() {
        if (mHomeList != null && mCurrentHomeIndex < mHomeList.size()) {
            HomeHalfPageFragment homeHalfPageFragment = mHomeList.get(mCurrentHomeIndex);
            boolean netWorkAvaible = NetWorkUtil.isNetworkAvailable(MainActivity
                    .this);
            if (mCurrentHomeIndex != 0 && homeHalfPageFragment != null) {
                HomeCellFragment homeCellFragment = homeHalfPageFragment
                        .getHomeCellFragment();
                if (homeCellFragment != null && homeCellFragment.getWorseDeviceIsShow()) {

                    //if the home has a device.
                    UserLocation mUserLocation = AuthorizeApp.shareInstance()
                            .getUserLocations().get(mCurrentHomeIndex - 1);

                    boolean isCanScroll = netWorkAvaible ? true : false;
                    homeHalfPageFragment.setIsScroll(!isCanScroll, !isCanScroll);
                    mScrollLayout.setIsScroll(isCanScroll);
                    setEmotionPagerLocalId(mUserLocation.getLocationID());
                } else {
                    //set homehalf cell can show tip when user scroll down
                    homeHalfPageFragment.setIsScroll(true, !netWorkAvaible);
                    mScrollLayout.setIsScroll(false);

                }
            } else if (homeHalfPageFragment != null) {
                //set homehalf cell can show tip when user scroll down
                homeHalfPageFragment.setIsScroll(false, !netWorkAvaible);
                mScrollLayout.setIsScroll(false);
            }
        }

    }

    private void clearHomeListWhenUserLocalIsNull() {
        //if userLocation list is null or size equal 0,delete all home list exception first one
        logoutUpdateHomePage();
        mCurrentHomeIndex = AuthorizeApp.shareInstance().getDefaultHomeNumber();
    }

    private void updateHomeListAccountResult(ArrayList<UserLocation> userLocations) {
        //if userLocation size is greater than home list size,generate new fragment and add in list
        int homeListSize = mHomeList.size() - 1;
        if (userLocations.size() < homeListSize){
            logoutUpdateHomePage();
            mCurrentHomeIndex = AuthorizeApp.shareInstance().getDefaultHomeNumber();
        }
        homeListSize = mHomeList.size() - 1;
        if (userLocations.size() > homeListSize) {

            int differentValue = userLocations.size() - homeListSize;
            for (int i = 0; i < differentValue; i++) {
                HomeHalfPageFragment homePageFragment = HomeHalfPageFragment.newInstance(this, homeListSize + 1 + i);
                mHomeList.add(homePageFragment);
//                homePageFragment.updateData(homeListSize + 1 + i, AuthorizeApp.shareInstance()
//                        .getUserLocations().get(homeListSize + i));

                TopViewFragment topViewFragment = TopViewFragment.newInstance(this, homeListSize + 1 + i);
                topViewFragment.setTopViewHillView(homeListSize + 1 + i);
                mTopList.add(topViewFragment);
            }

        }
    }

    private void updateHomeDeviceInfo(){
        for (int i = 1; i < mHomeList.size();i++){
            if ( mHomeList.get(i) != null && ((i -1) < AuthorizeApp.shareInstance().getUserLocations().size()) ){
                mHomeList.get(i).updateData(i, AuthorizeApp.shareInstance().getUserLocations()
                        .get(i - 1));
            }
        }
    }


    /**
     * init and update reside menu
     */
    private void setUpResideMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.color.setting_bg);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
//        resideMenu.setScaleValueX(0.5f);

        // create menu items;
        itemNick = new ResideMenuItem(this, R.drawable.menu_person, "Nick" + "  ");
        placesCare = new ResideMenuItem(this, R.drawable.menu_place, getString(R.string.places_care) + "  ");
        itemLine0 = new ResideMenuItem(this, "Line");
        itemLine1 = new ResideMenuItem(this, "Line");
        itemLine2 = new ResideMenuItem(this, "Line");
        itemLine3 = new ResideMenuItem(this, "Line");
        itemAddDevice = new ResideMenuItem(this, R.drawable.menu_add_device, getString(R.string.add_device) + "  ");
        itemUserGuide = new ResideMenuItem(this, R.drawable.menu_guide, getString(R.string.user_guide) + "  ");
        itemSettings = new ResideMenuItem(this, R.drawable.menu_password, getString(R.string.change_password) + "  ");
        itemLogin = new ResideMenuItem(this, R.drawable.menu_logout, getString(R.string.log_out) + "  ");
        if (AppConfig.isDebugMode) {
            itemTest = new ResideMenuItem(this, R.drawable.menu_guide, "");
            itemTest.setOnClickListener(this);
        }

        // set all menuItem OnClickListener
        itemNick.setOnClickListener(this);
        placesCare.setOnClickListener(this);
        itemAddDevice.setOnClickListener(this);
        itemUserGuide.setOnClickListener(this);
        itemLogin.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        updateMenuItems();

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

    }

    private void updateMenuItems() {

        List<ResideMenuItem> menuItems = new ArrayList<>();
        menuItems.add(itemNick);
        menuItems.add(placesCare);
        ArrayList<UserLocation> userLocations
                = AuthorizeApp.shareInstance().getUserLocations();

        if (AuthorizeApp.shareInstance().isLoginSuccess() && (userLocations != null)) {
            int defaultHomeNumber = AuthorizeApp.shareInstance().getDefaultHomeNumber();
            itemUserHomeList.clear();
            int locationSize = (userLocations.size() > 5 ? 5 : userLocations.size());
            for (int i = 0; i < locationSize; i++) {
                ResideMenuItem itemUserHome = new ResideMenuItem(MainActivity.this,
                        R.drawable.menu_default_home, userLocations.get(i).getName());
                itemUserHome.setOnClickListener(this);
                itemUserHomeList.add(itemUserHome);
                menuItems.add(itemUserHome);

                if (i == defaultHomeNumber) {
                    setAsCurrentHome(itemUserHome);
                    itemUserHome.setIcon(R.drawable.menu_default_home);
                    itemUserHome.getIv_icon().setVisibility(View.VISIBLE);
                }
            }

            // add edit location
            editPlaces = new ResideMenuItem(MainActivity.this,
                    R.drawable.menu_edit_place, getString(R.string.edit_places));
            editPlaces.getIv_icon().setVisibility(View.VISIBLE);
            editPlaces.setOnClickListener(this);
            itemUserHomeList.add(editPlaces);
            menuItems.add(editPlaces);
        }
        menuItems.add(itemLine0);
        menuItems.add(itemAddDevice);
        menuItems.add(itemLine1);
        menuItems.add(itemUserGuide);
        menuItems.add(itemLine2);
        menuItems.add(itemSettings);
        menuItems.add(itemLine3);
        menuItems.add(itemLogin);
        if (AppConfig.isDebugMode)
            menuItems.add(itemTest);

        resideMenu.setMenuItems(menuItems, ResideMenu.DIRECTION_LEFT);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    /**
     * Reside menu items handle
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == itemAddDevice) {
            if (AuthorizeApp.shareInstance().isLoginSuccess()) {
                UmengUtil.onEvent(MainActivity.this, UmengUtil.EventType.ENROLL_START.toString());

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EnrollWelcomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            } else {
                MessageBox.createTwoButtonDialog(MainActivity.this, null,
                        getString(R.string.not_login), getString(R.string.yes),
                        enrollLoginButton, getString(R.string.no), null);
            }
        } else if (view == itemNick) {
            if (!AuthorizeApp.shareInstance().isLoginSuccess()
                    && (!AuthorizeApp.shareInstance().isAutoLoginOngoing())) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UserLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        } else if (view == placesCare) {
            if (AuthorizeApp.shareInstance().isLoginSuccess()) {
                decideShowHomeList();
            }
        } else if (view == itemLogin) {
            if (AuthorizeApp.shareInstance().isLoginSuccess()) {
                logout();
            }
        } else if (view == itemTest) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, TestActivity.class);
            startActivity(intent);
            resideMenu.closeMenu();
        } else if (view == itemUserGuide) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ManualActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        } else if (view == itemSettings) {
            if (AuthorizeApp.shareInstance().isLoginSuccess()) {
                Intent intent = new Intent();
                intent.putExtra("isChangePassword", true);
                intent.setClass(MainActivity.this, MobileDoneActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        } else if (view == menuButton) {
            resideMenu.openMenu(resideMenu.getScaleDirection());
        } else if (view == editPlaces) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, UserEditHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        } else {
            changeHomePageClick(view);

//            CurrentGpsFragment gpsFragment = mHomeList.get(0).getGpsFragment();
//            if (gpsFragment != null)
//                gpsFragment.updateGpsFragment();
        }
    }

    private void decideShowHomeList() {
        if (itemUserHomeList != null) {
            if (isHideHomeList) {
                isHideHomeList = false;
                updateMenuItems();
            } else {
                isHideHomeList = true;
                hideHomeList();
            }
        }
    }

    private void changeHomePageClick(View view) {
        if (itemUserHomeList != null) {
            for (int i = 0; i < itemUserHomeList.size(); i++) {
//                itemUserHomeList.get(i).getIv_icon().setVisibility(View.INVISIBLE);
                if (view == itemUserHomeList.get(i)) {
                    changeHomePage(i);
                }
            }
        }
    }

    private void changeHomePage(int index) {
//        setAsCurrentHome(itemUserHomeList.get(index));
//        itemUserHomeList.get(index).setIcon(R.drawable.menu_default_home);
//        itemUserHomeList.get(index).getIv_icon().setVisibility(View.VISIBLE);
//        AuthorizeApp.shareInstance().setDefaultHomeNumber(index);
        mViewPager.setCurrentItem(index + 1);
        mTopViewPager.setCurrentItem(index + 1);
//        changeDefaultHomeListener.onChangeHomeListener();
    }

    private void hideHomeList() {
        if (itemUserHomeList != null) {
            for (int i = 0; i < itemUserHomeList.size(); i++) {
                itemUserHomeList.get(i).removeAllViews();
            }
        }
    }

    private MessageBox.MyOnClick enrollLoginButton = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            AuthorizeApp.shareInstance().setIsUserWantToEnroll(true);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, UserLoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    };

    /**
     * Reside menu listener
     */
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {

        }

        @Override
        public void closeMenu() {
            // Sometimes auto-login status doesn't change, need refresh to correct status by close and open menu
            if (AuthorizeApp.shareInstance().isAutoLoginOngoing()) {
                updateUserLogin();
                updateMenuItems();
            }
        }
    };

    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    private void updateUserLogin() {
        if (AuthorizeApp.shareInstance().isAutoLoginOngoing()) {

            itemNick.setTitle(getString(R.string.enroll_loading));
            itemNick.setMobile("");
            itemNick.setClickable(false);
            placesCare.setClickable(false);
            itemLogin.setVisibility(View.INVISIBLE);
//            itemLogin.setTitle(getString(R.string.log_in));
            itemLogin.setClickable(false);
            itemSettings.setVisibility(View.INVISIBLE);
            itemAddDevice.setClickable(false);
            itemSettings.setClickable(false);
        } else if (AuthorizeApp.shareInstance().isLoginSuccess()) {
            itemNick.setTitle(AuthorizeApp.shareInstance().getNickname());
            itemNick.setMobile(AuthorizeApp.shareInstance().getMobilePhone());
            itemNick.setClickable(false);
            placesCare.setClickable(true);
            itemLogin.setVisibility(View.VISIBLE);
            itemLogin.setTitle(getString(R.string.log_out));
            itemLogin.setClickable(true);
            itemAddDevice.setClickable(true);
            itemSettings.setVisibility(View.VISIBLE);
            itemSettings.setClickable(true);
            AuthorizeApp.shareInstance().setIsAutoLoginOngoing(false);
        } else if (!AuthorizeApp.shareInstance().isLoginSuccess()) {
            itemNick.setTitle(getString(R.string.log_in_reg));
            itemNick.setMobile("");
            itemNick.setClickable(true);
            placesCare.setClickable(false);
            itemLogin.setVisibility(View.INVISIBLE);
            itemSettings.setVisibility(View.INVISIBLE);
//            itemLogin.setTitle(getString(R.string.log_in));
//            itemLogin.setClickable(true);
//            itemSettings.setClickable(false);
            itemAddDevice.setClickable(true);
            AuthorizeApp.shareInstance().setIsAutoLoginOngoing(false);
            hideHomeList();
        }

    }

    private void logout() {

        mScrollLayout.setToScreen(1);
        mScrollLayout.setIsScroll(false);

        AuthorizeApp.shareInstance().setUserID("");
        AuthorizeApp.shareInstance().setSessionId("");
        AuthorizeApp.shareInstance().setIsLoginSuccess(false);
        AuthorizeApp.shareInstance().setIsAutoLoginOngoing(false);
        AuthorizeApp.shareInstance().setIsGetAllData(false);
        AuthorizeApp.shareInstance().getUserLocations().clear();
//        UserConfig userConfig = new UserConfig(MainActivity.this);
//        userConfig.saveDefaultHomeNumber(0);
//        userConfig.saveAutoLogin(false);
//        updateHomePage(LOGOUT_STATUS);
        updateUserLogin();
        logoutUpdateHomePage();
        hideHomeList();


        //init the emotionpage to the default status
        emotionPagerMainView.setEmotionPagerToDefaultSatus();
    }

    /**
     * Broadcast receiver
     * To get status of login/logout changed from App loading
     */
    private class UserLoginChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AirTouchConstants.HOME_CHANGED.equals(action) || AirTouchConstants.UPDATE_HOME_NAME.equals(action)) {
                isUpdateHomePage = false;
                //if homePage is covered.do not update homeList
                if (!AppConfig.shareInstance().isHomePageCover()) {
                    isUpdateHomePage = true;
                    updateMainActivityDisplay();
                }
                if (AirTouchConstants.UPDATE_HOME_NAME.equals(action)){
                    for (TopViewFragment topViewFragment : mTopList){
                        topViewFragment.updateHomeName();
                    }
                }

            }

            if (AirTouchConstants.ANIMATION_SHOW_CITY_LAYOUT_ACTION.equals(action)) {

                Bundle bundle = intent.getExtras();
                int locationId = bundle.getInt(HouseActivity.ARG_LOCATION);
                UserLocation userLocation = AuthorizeApp.shareInstance().getLocationWithId(locationId);
                if (mCurrentHomeIndex != 0  && mCurrentHomeIndex < mHomeList.size() && mHomeList.get(mCurrentHomeIndex) != null
                        && mHomeList.get(mCurrentHomeIndex).getHomeCellFragment() != null && userLocation != null) {

                    mHomeList.get(mCurrentHomeIndex).getHomeCellFragment().updateViewData(mCurrentHomeIndex, userLocation);
                    mHomeList.get(mCurrentHomeIndex).getHomeCellFragment().showHomePageViewNoCityAnimation();

                }
            }

            if (AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION.equals(action)) {
                setmViewPagerScroll(true);
                isAddHome = intent.getBooleanExtra(AirTouchConstants.IS_ADD_HOME, false);
                if (isAddHome && mHomeList != null) {
                    isUpdateHomePage = false;
                    return;

                } else if (mHomeList != null) {
                    //add device
                    isAddDevice = true;
                    localId = intent.getIntExtra(AirTouchConstants.LOCAL_LOCATION_ID, 0);


                }
            }
            if ("update_deviceinfo_intent".equals(action)){
                isUpdateHomePage = false;
                updateHomeDeviceInfo();
            }
//
//            if (AirTouchConstants.UPDATE_HOME_NAME.equals(action)){
//                for (TopViewFragment topViewFragment : mTopList){
//                    updateMenuItems();
//                    topViewFragment.updateHomeName();
//                }
//            }
        }
    }

    private void registerUserAliveChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("loginChanged");
        intentFilter.addAction(AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION);
        intentFilter.addAction(AirTouchConstants.DELETE_DEVICE_SUCCESS_ACTION);
        intentFilter.addAction(AirTouchConstants.ANIMATION_SHOW_CITY_LAYOUT_ACTION);
        intentFilter.addAction("update_deviceinfo_intent");
        intentFilter.addAction(AirTouchConstants.UPDATE_HOME_NAME);
        userLoginChangedReceiver = new UserLoginChangedReceiver();
        registerReceiver(userLoginChangedReceiver, intentFilter);
    }

    private void registerNetworkChangeBroadcast() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeBroadcastRec, mFilter);
    }

    private void unregisterNetworkBroadcast() {
        if (networkChangeBroadcastRec != null) {
            unregisterReceiver(networkChangeBroadcastRec);
        }
    }

    private void unRegisterLocationChangedReceiver() {
        if (userLoginChangedReceiver != null) {
            unregisterReceiver(userLoginChangedReceiver);
        }
    }


    /*
     * Find Home by AuthorizeApp.getUserLocations
     * Save it as CurrentHome
     */
    private void setAsCurrentHome(ResideMenuItem itemUserHome) {
        ArrayList<UserLocation> userLocations
                = AuthorizeApp.shareInstance().getUserLocations();
        if (userLocations != null) {
            for (int i = 0; i < userLocations.size(); i++) {
                if (userLocations.get(i).getName().equals(itemUserHome.getTv_title().getText())) {
                    AuthorizeApp.shareInstance().setCurrentHome(userLocations.get(i), i);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DeviceActivity.HOME_HOUSE_REQUEST_CODE:
            case DeviceActivity.HOME_DEVICE_REQUEST_CODE:
                AppConfig.shareInstance().setHomePageCover(false);
                operateActivityResult(data);
                setmViewPagerScroll(true);

            default:
                break;
        }
        ShareUtility eps = ShareUtility.getInstance(MainActivity.this);
        eps.addSinaCallback(requestCode, resultCode, data);
    }

    private void operateActivityResult(Intent data) {

        Bundle bundle = data.getExtras();
        int locationId = bundle.getInt(HouseActivity.ARG_LOCATION);
        UserLocation userLocation = AuthorizeApp.shareInstance().getLocationWithId(locationId);
        if (mCurrentHomeIndex != 0 && userLocation != null) {

            HomeCellFragment homeCellFragment = mHomeList.get(mCurrentHomeIndex).getHomeCellFragment();

            if (userLocation != null && mHomeList.get(mCurrentHomeIndex).getHomeCellFragment() != null) {

                homeCellFragment.updateViewData(mCurrentHomeIndex, userLocation);

                homeCellFragment.showHomePageViewNoAnimation();
                homeCellFragment.bindData2View();
            }


        } else {
            mViewPager.setCurrentItem(0);
            mTopViewPager.setCurrentItem(0);
        }
    }


    public class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TIME_CHANGE_ACTION) && AppConfig.shareInstance().isDaylight() !=
                    AppConfig.shareInstance().getCurrentDaylight()) {
                AppConfig.shareInstance().refreshDaylight();
                emotionPagerMainView.switchBgAccordingTime();
                for (int i = 0; i < mHomeList.size(); i++) {
                    mHomeList.get(i).switchTimeView();

                }
                if (mHazeView != null) {
                    mHazeView.setHazeViewVisible(View.VISIBLE);
                }

            }
        }
    }

    public void registerTimeChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TIME_CHANGE_ACTION);
        mTimeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(mTimeChangeReceiver, intentFilter);
    }

    public void unRegisterTimeChangeReceiver() {
        if (mTimeChangeReceiver != null) {
            unregisterReceiver(mTimeChangeReceiver);
        }
    }

    public void setMenuButtonAlpha(float alpha) {
        if (menuButton != null) {
            ViewHelper.setAlpha(menuButton, alpha);
        }
    }

    public interface ChangeDefaultHomeListener {
        void onChangeHomeListener();
    }

    public static void setChangeDefaultHomeListener(ChangeDefaultHomeListener listener) {
        changeDefaultHomeListener = listener;
    }

    public void setmViewPagerScroll(boolean isCanScroll) {
        mViewPager.setScanScroll(isCanScroll);
        mTopViewPager.setScanScroll(isCanScroll);
    }

    public void setCurrentHazeMoving(int homeIndex, int pmValue) {

        int currentPmValue = 0;
        localPmValueMap.put(homeIndex, pmValue);
        Integer pmValueObject = localPmValueMap.get(mCurrentHomeIndex);
        if (pmValueObject != null) {
            currentPmValue = pmValueObject.intValue();
        }
        mHazeView.setHazeMove(currentPmValue);
    }


    public void showNearHillNoAnimation(int homeIndex) {
        TopViewFragment topViewItem = mTopList.get(homeIndex);
        if (topViewItem != null) {
            topViewItem.showNearHillNoAnimation();
        }
    }

    public void showNearHillWithAimation(int homeIndex) {
        TopViewFragment topViewItem = mTopList.get(homeIndex);
        if (topViewItem != null) {
            topViewItem.showNearHillAnimation();
        }
    }

    public void hideNearHillAnimation(int homeIndex) {
        TopViewFragment topViewItem = mTopList.get(homeIndex);
        if (topViewItem != null) {
            topViewItem.hideNearHillAnimation();
        }
    }

    public void hideNearHillAnimationOfScroll(int homeIndex) {
        TopViewFragment topViewItem = mTopList.get(homeIndex);
        if (topViewItem != null) {
            topViewItem.hideNearHillNoAnimationOfScroll();
        }
    }

    public void setTopViewPagerOnTouchEvent() {
        mTopViewPager.setOnTouchListener(new ViewPager.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                return true;
            }
        });
    }

    public void setTopViewPagerOnTouchEvent2() {
        mTopViewPager.setOnTouchListener(new ViewPager.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                return false;
            }
        });
    }


    public void setScrollViewCanScroll(boolean isCanScroll) {
        isCanScroll = NetWorkUtil.isNetworkAvailable(this) ? isCanScroll : false;
        mScrollLayout.setIsScroll(isCanScroll);
    }

    public void setEmotionPagerLocalId(int localId) {
        emotionPagerMainView.setLocationId(localId);
    }

    /**
     * when there is no network,
     * 1.should set current show is home cell,not emotion pager
     * 2.set the tip scroll can see(the home cell which has device)
     */
    public void dealNoNetwork() {

        if (mHomeList != null && mCurrentHomeIndex < mHomeList.size() && mCurrentHomeIndex != 0)
        {
            HomeHalfPageFragment homeHalfPageFragment = mHomeList.get(mCurrentHomeIndex);
            if (homeHalfPageFragment != null) {
                HomeCellFragment homeCellFragment = homeHalfPageFragment.getHomeCellFragment();
                //if current home has device
                if (homeCellFragment != null && homeCellFragment.getWorseDeviceIsShow()) {
                    homeHalfPageFragment.setIsScroll(true, true);
                    if (mScrollLayout != null) {
                        mScrollLayout.setToScreen(1);
                        mScrollLayout.setIsScroll(false);
                    }
                    homeHalfPageFragment.showHasDeviceButNoNetwork();
                } else {
                    //no device
                    homeHalfPageFragment.setIsScroll(true, true);
                    mScrollLayout.setIsScroll(false);
                }
            }
        }
    }

    private void setScrollStatusWhenNetworkChange(boolean emotionCanScroll) {

        if (mHomeList != null && mCurrentHomeIndex < mHomeList.size() && mCurrentHomeIndex != 0)
        {
            HomeHalfPageFragment homeHalfPageFragment = mHomeList.get(mCurrentHomeIndex);
            if (mCurrentHomeIndex != 0 && homeHalfPageFragment != null) {
                HomeCellFragment homeCellFragment = homeHalfPageFragment.getHomeCellFragment();
                if (homeCellFragment != null && homeCellFragment.getWorseDeviceIsShow()) {
                    if (mScrollLayout != null) {
                        mScrollLayout.setIsScroll(emotionCanScroll);
                    }
                    homeHalfPageFragment.setIsScroll(!emotionCanScroll, false);
                } else {
                    //no device
                    homeHalfPageFragment.setIsScroll(true, false);
                    mScrollLayout.setIsScroll(false);
                }
            }
        }

    }

    /**
     * when emtion tip (no wifi or no device),should set menu button visible or invisible
     *
     * @param visible
     */
    public void setMenuBtnVisible(int visible) {
        if (menuButton != null) {
            menuButton.setVisibility(visible);
        }
    }

}
