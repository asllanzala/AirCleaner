package com.honeywell.hch.airtouchv3.app.dashboard.controller;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.android.pushservice.PushManager;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.TestActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.device.DeviceActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.EnrollAccessManager;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.SmartEnrollScanEntity;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.manual.ManualActivity;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.app.authorize.controller.MobileDoneActivity;
import com.honeywell.hch.airtouchv3.app.authorize.controller.UserEditHomeActivity;
import com.honeywell.hch.airtouchv3.app.authorize.controller.UserLoginActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.AllDeviceActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.BaseLocationFragment;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.CurrentGpsFragment;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.CurrentLocationLogic;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.DepthPageTransformer;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.HomePageAdapter;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.OnTravelMinderActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.TurnOnAllDeviceActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.UpdateVersionMinderActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.ViewPager;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.WeatherEffectViewLogic;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.TimerRefreshService;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseFragmentActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.config.UserConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Now;
import com.honeywell.hch.airtouchv3.framework.permission.HPlusPermission;
import com.honeywell.hch.airtouchv3.framework.permission.Permission;
import com.honeywell.hch.airtouchv3.framework.permission.PermissionListener;
import com.honeywell.hch.airtouchv3.framework.view.CustomViewPager;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.view.residemenu.ResideMenu;
import com.honeywell.hch.airtouchv3.framework.view.residemenu.ResideMenuItem;
import com.honeywell.hch.airtouchv3.lib.log.CrashLogHandler;
import com.honeywell.hch.airtouchv3.lib.log.SystemLogHandler;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.honeywell.hch.airtouchv3.lib.util.UmengUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by Jin Qian on 1/22/2015.
 */
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener, PermissionListener{

    private static final String TAG = "AirTouchMain";
    public static final String TIME_CHANGE_ACTION = "TimeChange";
    public static final int TRAVEL_REQUEST_CODE = 14;

    private ResideMenu resideMenu;
    private ResideMenuItem itemLine0, itemLine1, itemLine2, itemLine3, itemLine4;
    private ResideMenuItem itemNick;
    private ResideMenuItem placesCare;
    private ResideMenuItem editPlaces;
    private ResideMenuItem mOntravelMenu;

    private ArrayList<ResideMenuItem> itemUserHomeList = new ArrayList<>();
    private ResideMenuItem itemAddDevice;
    private ResideMenuItem itemUserGuide;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemServiceCall;

    private ResideMenuItem itemLogin;
    private ResideMenuItem itemTest;
    private CustomViewPager mViewPager = null;

    private ImageButton menuButton = null; // menu button on left upper side of home page
    protected Button mSwitchTimeButton = null;

    private int mCurrentHomeIndex = 0;
    private boolean isHideHomeList = false;
    private List<BaseLocationFragment> mHomeList = new CopyOnWriteArrayList<>();

    private HomePageAdapter mHomePageAdapter = null;

    private BroadcastReceiver userLoginChangedReceiver;
    private TimeChangeReceiver mTimeChangeReceiver;

    //set false status when receive  loginchange broadcast if homePage is covered
    //otherwise set true status
    private boolean isUpdateHomePage = true;

    private boolean isAddHome = false;

    private boolean isAddDevice = false;

    public static final int LOG_OUT_STATUS = 0;
    private static final int ADD_HOME_STATUS = 1;
    public static final int LOGIN_STATUS = 2;
    public static final int REFRESH_STATUS = 3;

    private int localId;

    //the home index before the scroll
    private int preScrollHomeIndex = 0;

    private boolean isNeedToRequest = true;

    private boolean isActivityResume = false;


    private boolean mIsTurnToOnTravel = false;

    private AuthorizeApp mAuthorizeApp;

    private TimerRefreshService mServiceBinder;

    private boolean isBind = false ;

    private CurrentLocationLogic mCurrentLocationLogic;

    private RelativeLayout mParentView;

    private WeatherEffectViewLogic mWeatherEffectViewLogic;

    private ImageView mBackground;

    private Bitmap mUniversityBitmap;

    private boolean  isNeedRefresh = false;
    private String  mFreshActionStr = AirTouchConstants.AFTER_USER_LOGIN;

    private AlertDialog mAlertDialog = null;

    private HPlusPermission mHPlusPermission;

    private Button mDebugBtn;

    private static final String CONTACT_PHONE_NUMBER = "4007204321";

    private static final String INDIA_CONTACT_PHONE_NUMBER = "1800-103-4761";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mHPlusPermission = new HPlusPermission(this);

        super.TAG = TAG;
        mViewPager = (CustomViewPager) findViewById(R.id.main_page);
        mParentView = (RelativeLayout)findViewById(R.id.parent_view_id);

        mBackground = (ImageView)findViewById(R.id.universe_id);

        mUniversityBitmap = BitmapUtil.createBitmapEffectly(this,R.raw.universe_bg);
        mBackground.setImageBitmap(mUniversityBitmap);

        mWeatherEffectViewLogic = new WeatherEffectViewLogic(this,mParentView);

        menuButton = (ImageButton) findViewById(R.id.side_menu);
        menuButton.setOnClickListener(this);

        mAuthorizeApp = AppManager.shareInstance().getAuthorizeApp();
        mCurrentLocationLogic = new CurrentLocationLogic(this);

        setUpResideMenu();
        registerUserAliveChangedReceiver();
        registerTimeChangeReceiver();


        initUpdateVersion();

        // for test day or night
        mSwitchTimeButton = (Button) findViewById(R.id.switch_time);
        mDebugBtn = (Button) findViewById(R.id.debug_btn_id);
        mSwitchTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.isDebugMode) {
                    testMethod();
                }
            }
        });
        initDebugBtn();


        updateUserLogin();
        updateViewPagerWhenCreate();
        updateMenuItems();

        mHPlusPermission.checkAndRequestPermission(Permission.PermissionCodes.STORAGE_AND_LOCATION_CODE, this);

    }

    private void initDebugBtn(){
        if (AppConfig.isDebugMode){

            CrashLogHandler crashHandler = CrashLogHandler.getInstance();
            crashHandler.init(this);
            mDebugBtn.setVisibility(View.VISIBLE);
            mDebugBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppConfig.isChangeEnv = true;
                    AppConfig.isDebugMode = !AppConfig.isDebugMode;
                    logoutAndToLoginActivity();

                }
            });
            mDebugBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    SystemLogHandler systemLogHandler = new SystemLogHandler(MainActivity.this);
                    systemLogHandler.collectAndSendSystemLog();
                    return false;
                }
            });
        }
        else{
            mDebugBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unRegisterLocationChangedReceiver();
        unRegisterTimeChangeReceiver();

        if (!StringUtil.isEmpty(mAuthorizeApp.getMobilePhone())) {
            mAuthorizeApp.setIsGetAllData(false);
        }
        // for a special case
        if (!mAuthorizeApp.isRemember()) {
            mAuthorizeApp.setIsLoginSuccess(false);
        }

        isActivityResume = false;

        for (BaseLocationFragment baseLocationFragment : mHomeList) {
            if (baseLocationFragment != null) {
                baseLocationFragment.recycleBackground();
            }
        }

        if (isBind){
            unbindService(serviceConnection);
            isBind = false;
        }
        if (mUniversityBitmap != null){
            mUniversityBitmap.recycle();
            mUniversityBitmap = null;
        }

        if (mAlertDialog != null && mAlertDialog.isShowing()){
            mAlertDialog.cancel();
            mAlertDialog = null;
        }

//        // release application's RAM
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        System.exit(0);
    }

    public int getCurrentHomeIndex() {
        if (mCurrentHomeIndex < 0){
            mCurrentHomeIndex = 0;
        }
        return mCurrentHomeIndex;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    /*
     * receive notification when app has a new version
     */
    private void initUpdateVersion() {
        final AppConfig appConfig = AppConfig.shareInstance();
        final int currentTimeHour = (int)((System.currentTimeMillis()) / (3600*1000*24));

        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                LogUtil.log(LogUtil.LogLevel.INFO, TAG, "updateStatus: " + updateStatus);
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        if (currentTimeHour - AppConfig.mInitTime <= 6) {
                            return;
                        }
                        Intent intent = new Intent(MainActivity.this, UpdateVersionMinderActivity.class);
                        intent.putExtra("version_action", updateInfo);
                        startActivity(intent);
                        break;
                    case UpdateStatus.No: // has no update
                    case UpdateStatus.NoneWifi: // none wifi
                    case UpdateStatus.Timeout: // time out
                        break;
                }
            }
        });
        UmengUpdateAgent.update(this);
    }
    /**
     * init and update home page fragment
     */
    private void updateHomePage(int status) {
        LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "update home fragment");

        if (mAuthorizeApp.isLoginSuccess()) {
            mCurrentLocationLogic.loadHomeList(status);
            initService();

        } else {
            logout();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        AppConfig.shareInstance().setHomePageCover(false);

        if (isNeedRefresh){
            isNeedRefresh = false;
            int status  = (AirTouchConstants.AFTER_USER_LOGIN.equals(mFreshActionStr) ||  AirTouchConstants.DELETE_HOME.equals(mFreshActionStr))
                    ? LOGIN_STATUS : REFRESH_STATUS;
            updateMainActivityDisplay(status);
        }
        if (isAddHome) {
            isAddHome = false;
            mCurrentHomeIndex = mHomeList.size() - 1;
            setViewPagerGotoCurrentItem(mCurrentHomeIndex);
        }
        if (isAddDevice){
            isAddDevice = false;
            addDeviceToHome();
        }

        if (mServiceBinder != null && isBind){
            mServiceBinder.startRefreshThread();
        }

        isActivityResume = true;


        startAllAnimation(mCurrentHomeIndex);


        if (SmartEnrollScanEntity.getEntityInstance().isRegisteredByThisUser()){
            showDeviceRegisteredDialog();
            SmartEnrollScanEntity.getEntityInstance().setIsRegisteredByThisUser(false);
        }

    }




    private void callPhone() {
        Intent intent = null;
        if(AppConfig.shareInstance().isIndiaAccount()) {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + INDIA_CONTACT_PHONE_NUMBER));
        } else {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CONTACT_PHONE_NUMBER));
        }
        startActivity(intent);
    }

    private void showDeviceRegisteredDialog(){
        if (mAlertDialog == null){
            mAlertDialog = MessageBox.createSimpleDialog(this, "", getResources().getString(R.string.device_register_id),
                    getResources().getString(R.string.ok), new MessageBox.MyOnClick() {
                        @Override
                        public void onClick(View v) {
                            if (mAlertDialog != null){
                                mAlertDialog.cancel();
                                mAlertDialog = null;
                            }
                        }
                    });
            try{
                mAlertDialog.show();
            } catch (Exception e){

            }

        }
    }

    private void addDeviceToHome() {
        mCurrentLocationLogic.updateHomeWithLocationId(localId);
    }

    public void updateMainActivityDisplay(int status) {

        updateHomePage(status);

        updateMenuItems();


        updateUserLogin();


        goToOnTravelActivity();

    }

    public void goToOnTravelActivity(){
        if (mAuthorizeApp.isLoginSuccess() && mCurrentLocationLogic.isOnTravelling() && !mIsTurnToOnTravel
                && !AppConfig.shareInstance().isIndiaAccount()) {
            mIsTurnToOnTravel = true;
            mViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, OnTravelMinderActivity.class);
                    startActivityForResult(intent, TRAVEL_REQUEST_CODE);
                    overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
                    AppConfig.shareInstance().setIsDifferent(false);
                }
            }, 400);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityResume = false;
        stopAllAnimation();
        AppConfig.shareInstance().setHomePageCover(true);
//        if (mServiceBinder != null && isBind){
//            mServiceBinder.stopRefreshThread();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            MessageBox.createTwoButtonDialog(MainActivity.this, null, getString(R.string
                            .quit), getString(R.string.no), null,
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

        mHomePageAdapter = new HomePageAdapter(getSupportFragmentManager(), mHomeList,mCurrentLocationLogic);
        mViewPager.setAdapter(mHomePageAdapter);
        mViewPager.setPageTransformer(false, new DepthPageTransformer(this));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset != 0 && positionOffset != -1) {
                    stopAllAnimation();
                }
            }


            @Override
            public void onPageSelected(int position) {
                mCurrentHomeIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0 || state == 2) {
                    preScrollHomeIndex = mCurrentHomeIndex;

                }
                if (state == 0) {
                    //update current data
                    mCurrentLocationLogic.updateCurrentHomeData(preScrollHomeIndex);
                    startAllAnimation(preScrollHomeIndex);
                }
            }
        });
        mCurrentLocationLogic.addCurrentLocation(true);

        if (mAuthorizeApp.isLoginSuccess()) {
            updateHomePage(LOGIN_STATUS);
        }
        setViewPagerGotoCurrentItem(mCurrentHomeIndex);
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
        itemLine4 = new ResideMenuItem(this, "Line");
        itemAddDevice = new ResideMenuItem(this, R.drawable.menu_add_device, getString(R.string.add_device) + "  ");
        itemUserGuide = new ResideMenuItem(this, R.drawable.menu_guide, getString(R.string.user_guide) + "  ");
        itemSettings = new ResideMenuItem(this, R.drawable.menu_password, getString(R.string.change_password) + "  ");
        itemServiceCall = new ResideMenuItem(this, R.drawable.menu_callservice, getString(R.string.sideitem_customer_care) + "  ");
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
        itemServiceCall.setOnClickListener(this);

//        updateMenuItems();

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

    }

    public void updateMenuItems() {

        List<ResideMenuItem> menuItems = new ArrayList<>();
        menuItems.add(itemNick);
        menuItems.add(placesCare);
        List<UserLocationData> userLocations
                = AppManager.shareInstance().getUserLocationDataList();

        itemUserHomeList.clear();
        if (mCurrentLocationLogic.iSHasCurrentLocation()) {

            if (AppConfig.shareInstance().isIndiaAccount()) {
                // do nothing
            } else if (AppConfig.shareInstance().isLocatedInIndia()
                    && (!mAuthorizeApp.isLoginSuccess())) {
                //do nothing
            } else {
                addOntravelSideMenu(menuItems, getOntravelMenuStr());
            }
        }

        if (mAuthorizeApp.isLoginSuccess() && (userLocations != null)) {
            boolean isSetDefault = false;
            int locationSize = (userLocations.size() > AppManager.maxHomeCount ? AppManager.maxHomeCount : userLocations.size());
            for (int i = 0; i < locationSize; i++) {
                ResideMenuItem itemUserHome = new ResideMenuItem(MainActivity.this,
                        R.drawable.menu_default_home, userLocations.get(i).getName());
                itemUserHome.setOnClickListener(this);
                itemUserHomeList.add(itemUserHome);
                menuItems.add(itemUserHome);

                if (userLocations.get(i).getLocationID() == mAuthorizeApp.getDefaultHomeLocalId()) {
                    isSetDefault = true;
                    mAuthorizeApp.setDefaultHomeNumber(i);
                    setDefaultHomeIcon(itemUserHome);
                }
            }
            if (!isSetDefault && locationSize > 0){
                int index = mCurrentLocationLogic.iSHasCurrentLocation() ? 1 : 0;
                mAuthorizeApp.setDefaultHomeNumber(0);
                mAuthorizeApp.setDefaultHomeLocalId(userLocations.get(0).getLocationID());
                setDefaultHomeIcon(itemUserHomeList.get(index));
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
        menuItems.add(itemServiceCall);
        menuItems.add(itemLine3);
        menuItems.add(itemSettings);
        menuItems.add(itemLine4);
        menuItems.add(itemLogin);
        if (AppConfig.isDebugMode)
            menuItems.add(itemTest);

        resideMenu.setMenuItems(menuItems, ResideMenu.DIRECTION_LEFT);
    }

    private void setDefaultHomeIcon( ResideMenuItem itemUserHome){
        itemUserHome.setIcon(R.drawable.menu_default_home);
        itemUserHome.getIv_icon().setVisibility(View.VISIBLE);
    }

    private void addOntravelSideMenu(List<ResideMenuItem> menuItems,String gpsName){
        if (gpsName != null){
            // add edit location
            mOntravelMenu = new ResideMenuItem(MainActivity.this,
                    R.drawable.ontravel_icon, gpsName);
            mOntravelMenu.getIv_icon().setVisibility(View.VISIBLE);
            mOntravelMenu.setOnClickListener(this);

            int sideCount = itemUserHomeList.size() == 0 ? 0 : itemUserHomeList.size() - 1;

            itemUserHomeList.add(sideCount,mOntravelMenu);

            menuItems.add(mOntravelMenu);
        }
    }

    /**
     * update the current location reside menu
     *
     */
    public void updateTravelSideMenu(){
        if (itemUserHomeList != null && itemUserHomeList.size() > 0){
            ResideMenuItem resideMenuItem = itemUserHomeList.get(0);
            resideMenuItem.setTitle(getOntravelMenuStr());
        }
    }

    private String getOntravelMenuStr(){
        //add current menu
        UserLocationData gpsLocation = AppManager.shareInstance().getGpsUserLocation();

        if (!mHPlusPermission.isLocationPermissionGranted(this)){
            return getResources().getString(R.string.no_located_permission_title);
        }
        else{
            if (AppConfig.LOCATION_FAIL.equals(gpsLocation.getName())){
                return getResources().getString(R.string.current_gps_fail);
            }
            else if (StringUtil.isEmpty(gpsLocation.getName())){
                return getResources().getString(R.string.enroll_gps_on);
            }
            else{
                return gpsLocation.getName();
            }
        }

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
            if (mAuthorizeApp.isLoginSuccess()) {
                UmengUtil.onEvent(MainActivity.this, UmengUtil.EventType.ENROLL_START.toString());
                EnrollAccessManager.startIntent(MainActivity.this, "");
//                resideMenu.closeMenu();
            } else {
                MessageBox.createTwoButtonDialog(MainActivity.this, null,
                        getString(R.string.not_login), getString(R.string.yes),
                        enrollLoginButton, getString(R.string.no), null);
            }
        } else if (view == itemNick.getTv_title()) {
            if (!mAuthorizeApp.isLoginSuccess()
                    && (!mAuthorizeApp.isAutoLoginOngoing())) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UserLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        } else if (view == placesCare) {
            if (mAuthorizeApp.isLoginSuccess()) {
                decideShowHomeList();
            }
        } else if (view == itemLogin) {
            if (mAuthorizeApp.isLoginSuccess()) {
                logoutAndToLoginActivity();
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
        } else if (view == itemServiceCall) {
            mHPlusPermission.checkAndRequestPermission(Permission.PermissionCodes.CALL_PHONE_REQUEST_CODE,this);

        } else if (view == itemSettings) {
            if (mAuthorizeApp.isLoginSuccess()) {
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
        }
        else if (view == mOntravelMenu){
            mCurrentLocationLogic.gotoTheSpecifyHome(0);
            resideMenu.closeMenu();
        }
        else {
            changeHomePageClick(view);
        }
    }

    private void logoutAndToLoginActivity(){
        logout();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, UserLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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
//        index = mCurrentLocationLogic.iSHasCurrentLocation() ? index - 1 : index;
        setViewPagerGotoCurrentItem(index);
        resideMenu.closeMenu();
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
            mAuthorizeApp.setIsUserWantToEnroll(true);
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
            if (mAuthorizeApp.isAutoLoginOngoing()) {
                updateUserLogin();
                updateMenuItems();
            }
        }
    };

    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    private void updateUserLogin() {
        UserConfig userConfig = new UserConfig(MainActivity.this);

        if (mAuthorizeApp.isAutoLoginOngoing()) {
            userConfig.saveAutoLogin(true);
            itemNick.setTitle(getString(R.string.enroll_loading));
            itemNick.setMobile("");
            itemNick.setClickable(false);
            placesCare.setClickable(false);
            itemLogin.setVisibility(View.INVISIBLE);
//            itemLogin.setTitle(getString(R.string.log_in));
            itemLogin.setClickable(false);
            itemSettings.setVisibility(View.INVISIBLE);
            itemServiceCall.setVisibility(View.INVISIBLE);
            itemServiceCall.setClickable(false);
            itemAddDevice.setClickable(false);
            itemSettings.setClickable(false);

        } else if (mAuthorizeApp.isLoginSuccess()) {
            userConfig.saveAutoLogin(true);
            itemNick.setTitle(mAuthorizeApp.getNickname());
            itemNick.setMobile(mAuthorizeApp.getMobilePhone());
            itemNick.setClickable(false);
            placesCare.setClickable(true);
            itemLogin.setVisibility(View.VISIBLE);
            itemLogin.setTitle(getString(R.string.log_out));
            itemLogin.setClickable(true);
            itemAddDevice.setClickable(true);
            itemSettings.setVisibility(View.VISIBLE);
            itemServiceCall.setVisibility(View.VISIBLE);
            itemServiceCall.setClickable(true);
            itemSettings.setClickable(true);
            mAuthorizeApp.setIsAutoLoginOngoing(false);
        } else if (!mAuthorizeApp.isLoginSuccess()) {
            userConfig.saveAutoLogin(false);
            updateMenuItems();
            itemNick.setTitle(getString(R.string.log_in_reg));
            itemNick.setMobile("");
            itemNick.setClickable(false);
            itemNick.getTv_title().setClickable(true);
            itemNick.getTv_title().setOnClickListener(this);
            placesCare.setClickable(false);
            itemLogin.setVisibility(View.INVISIBLE);
            itemSettings.setVisibility(View.INVISIBLE);
            itemServiceCall.setVisibility(View.INVISIBLE);
            itemAddDevice.setClickable(true);
            mAuthorizeApp.setIsAutoLoginOngoing(false);
//            hideHomeList();
        }

    }

    private void logout() {

        stopAllAnimation();
        mAuthorizeApp.setUserID("");
        mAuthorizeApp.setSessionId("");
        mAuthorizeApp.setIsLoginSuccess(false);
        mAuthorizeApp.setIsAutoLoginOngoing(false);
        mAuthorizeApp.setIsGetAllData(false);
        AppManager.shareInstance().getUserLocationDataList().clear();

        mCurrentLocationLogic.loadHomeList(LOG_OUT_STATUS);
        updateUserLogin();

        if (isBind){
            unbindService(serviceConnection);
            isBind = false;
        }

        PushManager.stopWork(MainActivity.this);
    }

    /**
     * Broadcast receiver
     * To get status of login/logout changed from App loading
     */
    private class UserLoginChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AirTouchConstants.AFTER_USER_LOGIN.equals(action) || AirTouchConstants.HOME_CHANGED.equals(action)
                    || AirTouchConstants.DELETE_HOME.equals(action)) {
                     if (AppConfig.shareInstance().isHomePageCover()){
                         isNeedRefresh = true;
                         mFreshActionStr = action;
                     }
                     else{
                         int status  = (AirTouchConstants.AFTER_USER_LOGIN.equals(action) ||  AirTouchConstants.DELETE_HOME.equals(action)) ? LOGIN_STATUS : REFRESH_STATUS;
                         updateMainActivityDisplay(status);
                     }
                if (AirTouchConstants.AFTER_USER_LOGIN.equals(action) ||  AirTouchConstants.DELETE_HOME.equals(action)){
                    resideMenu.closeMenu();
                }

            }

            if (AirTouchConstants.RENAME_HOME.equals(action)){
                afterRenameHome(intent);
                resideMenu.closeMenu();
            }
            if (AirTouchConstants.SET_DEFALUT_HOME.equals(action)){
                afterSetDefaultHome();
                resideMenu.closeMenu();
            }
            if (AirTouchConstants.ADD_DEVICE.equals(action)){
                resideMenu.closeMenu();
            }

            if (AirTouchConstants.SHORTTIME_REFRESH_END_ACTION.equals(action)){

                if (mAuthorizeApp.isLoginSuccess()
                        && AppConfig.shareInstance().isFirstLogin()){
                    AppConfig.shareInstance().setIsFirstLogin(false);
                    decideShowTurnOnAllDevice();
                }
            }
            if (AirTouchConstants.LONG_REFRESH_END_ACTION.equals(action)){
                if (mAuthorizeApp.isLoginSuccess()
                        && AppConfig.shareInstance().isFirstLogin()){
                    AppConfig.shareInstance().setIsFirstLogin(false);

                    //update weather
                    for (BaseLocationFragment baseLocationFragment : mHomeList){
                        baseLocationFragment.updateWeatherData();
                    }
                    decideShowTurnOnAllDevice();
                }
                else if (!mAuthorizeApp.isLoginSuccess()){
                    mHomeList.get(0).updateWeatherData();
                }

            }

            if (AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION.equals(action)) {
                isAddHome = intent.getBooleanExtra(AirTouchConstants.IS_ADD_HOME, false);
                resideMenu.closeMenu();
                if (isAddHome && mHomeList != null) {
                    return;

                } else if (mHomeList != null) {
                    //add device
                    isAddDevice = true;
                    localId = intent.getIntExtra(AirTouchConstants.LOCAL_LOCATION_ID, 0);
                }

            }

            if (AirTouchConstants.GPS_RESULT.equals(action)){
                updateCurrentLocationFragmentShow();
            }

        }
    }

    public void updateCurrentLocationFragmentShow(){
        if (mHomeList.size() != 0 && mHomeList.get(0) instanceof CurrentGpsFragment){
            CurrentGpsFragment currentGpsFragment = (CurrentGpsFragment)mHomeList.get(0);
            if (currentGpsFragment != null){
                currentGpsFragment.updateGpsCurrentLocation();
            }
        }
    }

    private void startGpsCurrentLocation(){
        if (mHomeList.size() != 0 && mHomeList.get(0) instanceof CurrentGpsFragment){
            CurrentGpsFragment currentGpsFragment = (CurrentGpsFragment)mHomeList.get(0);
            if (currentGpsFragment != null){
                currentGpsFragment.startGps();
            }
        }
    }

    private void decideShowTurnOnAllDevice(){
       ArrayList<Integer> deviceIdList = AppManager.shareInstance().getNeedOpenDevice();
       if (deviceIdList.size() > 0){
           Intent intent = new Intent();
           intent.setClass(MainActivity.this, TurnOnAllDeviceActivity.class);
           intent.putIntegerArrayListExtra(AirTouchConstants.DEVICE_ID_LIST, deviceIdList);
           startActivity(intent);
           overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
       }
    }


    private void afterSetDefaultHome(){
        mCurrentLocationLogic.goToTheDefaultHome();
        updateMenuItems();

        int defaultHomeNumber = mAuthorizeApp.getDefaultHomeNumber();
        int defaultHomeId = mAuthorizeApp.getDefaultHomeLocalId();
        UserConfig userConfig = new UserConfig(MainActivity.this);
        userConfig.saveDefaultHomeNumber(mAuthorizeApp.getMobilePhone(), defaultHomeNumber);
        userConfig.saveDefaultHomeId(mAuthorizeApp.getMobilePhone(), defaultHomeId);
    }

    private void afterRenameHome(Intent intent){
        String newName = intent.getStringExtra("location_name");
        int locationId = intent.getIntExtra("location_id", 0);
        for (BaseLocationFragment baseLocationFragment : mHomeList){
            if (baseLocationFragment.getUserLocationData() != null &&
                    baseLocationFragment.getUserLocationData().getLocationID() == locationId){

                baseLocationFragment.updateHomeName(newName);
            }
        }

        //update slide item
        updateMenuItems();
    }

    private void registerUserAliveChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("loginChanged");
        intentFilter.addAction(AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION);
        intentFilter.addAction(AirTouchConstants.DELETE_DEVICE_SUCCESS_ACTION);
        intentFilter.addAction(AirTouchConstants.UPDATE_HOME_NAME);
        intentFilter.addAction(AirTouchConstants.AFTER_USER_LOGIN);
        intentFilter.addAction(AirTouchConstants.RENAME_HOME);
        intentFilter.addAction(AirTouchConstants.LONG_REFRESH_END_ACTION);
        intentFilter.addAction(AirTouchConstants.SET_DEFALUT_HOME);
        intentFilter.addAction(AirTouchConstants.SHORTTIME_REFRESH_END_ACTION);
        intentFilter.addAction(AirTouchConstants.DELETE_HOME);
        intentFilter.addAction(AirTouchConstants.GPS_RESULT);
        intentFilter.addAction(AirTouchConstants.ADD_DEVICE);

        userLoginChangedReceiver = new UserLoginChangedReceiver();
        registerReceiver(userLoginChangedReceiver, intentFilter);
    }


    private void unRegisterLocationChangedReceiver() {
        if (userLoginChangedReceiver != null) {
            unregisterReceiver(userLoginChangedReceiver);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DeviceActivity.HOME_HOUSE_REQUEST_CODE:
            case DeviceActivity.HOME_DEVICE_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    AppConfig.shareInstance().setHomePageCover(false);
                    operateActivityResult(data);
                }
                break;
            case TRAVEL_REQUEST_CODE:
                mIsTurnToOnTravel = false;
                if (resultCode == RESULT_OK){
                   boolean isToCurrent = data.getBooleanExtra(OnTravelMinderActivity.IS_NEED_TO_CURRENT,false);
                    if (isToCurrent){
                        mCurrentHomeIndex = 0;
                        setViewPagerGotoCurrentItem(mCurrentHomeIndex);
                    }
                    else{
                        //go to the default home
                        mCurrentLocationLogic.goToTheDefaultHome();
                    }
                }
                break;
            default:
                break;
        }
//        ShareUtility eps = ShareUtility.getInstance(MainActivity.this);
//        eps.addSinaCallback(requestCode, resultCode, data);
    }

    private void operateActivityResult(Intent data) {
        if (data == null){
            return;
        }
        Bundle bundle = data.getExtras();
        int locationId = bundle.getInt(AllDeviceActivity.ARG_LOCATION_ID);
        mCurrentLocationLogic.updateHomeWithLocationId(locationId);

    }

    /**
     * add this condition
     * @param baseLocationFragment
     * @return
     */
    private boolean isContainDefaultDevice(BaseLocationFragment baseLocationFragment){
        List<HomeDevice> deviceList = baseLocationFragment.getUserLocationData().getHomeDevicesList();
        if (deviceList != null && deviceList.size() > 0){
            for (HomeDevice device : deviceList){
                if (device.getDeviceInfo().getDeviceID() == AppManager.shareInstance().getCurrentDeviceId()){
                    return true;
                }
            }
        }
        return false;
    }


    public class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TIME_CHANGE_ACTION) && AppConfig.shareInstance().isDaylight() !=
                    AppConfig.shareInstance().getCurrentDaylight()) {
                AppConfig.shareInstance().refreshDaylight();
                //swtich background
                switchBackground();

            }
        }
    }

    private void switchBackground(){
        UserLocationData gpsLocationData = AppManager.shareInstance().getGpsUserLocation();
        if (gpsLocationData != null){
            updateLocationItemBackground(gpsLocationData);
        }
        List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
        if (userLocationDataList != null){
            for (int i = 0; i < userLocationDataList.size();i++){
                UserLocationData userLocationData = userLocationDataList.get(i);
                updateLocationItemBackground(userLocationData);
            }
        }
        if (!AppConfig.shareInstance().isDaylight()){
            mWeatherEffectViewLogic.stopHalo();
        }
    }

    private void updateLocationItemBackground(UserLocationData userLocationData){
        if (!AppConfig.shareInstance().isDaylight() && userLocationData != null){
            userLocationData.getCityBackgroundDta().initmCityBackgroundObjectListList(-1,true);
        }
        else if (userLocationData != null && userLocationData.getCityWeatherData() != null){
            Now thisNowWeather = userLocationData.getCityWeatherData().getWeather().getNow();
            if (thisNowWeather != null ){
                int mWeatherCode = thisNowWeather.getCode();
                if (mWeatherCode == 99){
                    mWeatherCode = 0;
                }
                userLocationData.getCityBackgroundDta().initmCityBackgroundObjectListList(mWeatherCode,true);
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


    /**
     * Service to GetRunStatus 30s each
     */
    private void initService() {
        if (!isBind){
            try {
                Intent intent = new Intent(MainActivity.this, Class.forName(TimerRefreshService.class
                        .getName()));
                isBind =  bindService(intent, serviceConnection,
                        TimerRefreshService.BIND_AUTO_CREATE);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "in onServiceDisconnected");
            mServiceBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "in onServiceConnected");
            mServiceBinder = ((TimerRefreshService.MyBinder) service).getService();
        }
    };

    public List<BaseLocationFragment> getHomeList(){
        return mHomeList;
    }

    public HomePageAdapter getHomePageAdapter(){
        return mHomePageAdapter;
    }


    public void notifyViewPagerChange(){
        setViewPagerScroll(true);
        mHomePageAdapter.notifyDataSetChanged();
        setViewPagerGotoCurrentItem(mCurrentHomeIndex);
    }

    public void setCurrentHomeIndex(int index){
        mCurrentHomeIndex = index;
    }

    public void testMethod(){
        Intent intent = new Intent();
        intent.setClass(this, OnTravelMinderActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
//        AppConfig.shareInstance().setDaylight(false);
    }

    public AuthorizeApp getAuthorizeApp(){
        return mAuthorizeApp;
    }

    public void stopAllAnimation(){
        for (BaseLocationFragment baseLocationFragment : mHomeList){
            if (baseLocationFragment != null){
                baseLocationFragment.stopSwitchBackground(true);
                baseLocationFragment.stopChartWeatherAnimation();
            }
        }
         mWeatherEffectViewLogic.stopAllWeatherEffect();
    }

    public void startAllAnimation(int homeIndex){
        if (!AppConfig.shareInstance().isIndiaAccount()){
            if (homeIndex < mHomeList.size() && homeIndex >= 0){
                BaseLocationFragment baseLocationFragment = mHomeList.get(homeIndex);
                if (baseLocationFragment != null){
                    baseLocationFragment.startSwitchBackground();
                    baseLocationFragment.startCharWeatherAnimation();
                }
            }
            mWeatherEffectViewLogic.startShowHazeView();

            mWeatherEffectViewLogic.dicideShowEffect();

        }

    }


    public void setCurrentHazeMoving(){
        mWeatherEffectViewLogic.setCurrentHazeMoving();
    }

    public void startOtherWeatherEffect(){
        mWeatherEffectViewLogic.startOtherWeatherEffect();
    }

    public boolean isHasCurrentLocation(){
        return mCurrentLocationLogic.iSHasCurrentLocation();
    }

    public void setViewPagerScroll(boolean isCanScroll){
        if (mViewPager != null){
            mViewPager.setScanScroll(isCanScroll);
        }
    }

    public void removeWeatherTutorial(){
        for (int i = 0; i < mHomeList.size(); i++){
            BaseLocationFragment baseLocationFragment = mHomeList.get(i);
            if (baseLocationFragment != null){
                baseLocationFragment.removeWeatherTutorial();
            }
        }
    }




    /**
     * when call the setCurrentItem,should ensure that the view pager can be scroll
     * @param index
     */
    public void setViewPagerGotoCurrentItem(int index){
        setViewPagerScroll(true);
        mViewPager.setCurrentItem(index, true);
    }

    @Override
    public void onPermissionGranted(int permissionCode) {
        if (permissionCode == Permission.PermissionCodes.STORAGE_REQUEST_CODE
                || permissionCode == Permission.PermissionCodes.STORAGE_AND_LOCATION_CODE){
            AppManager.shareInstance().startDownBackgroundTask(false);
        }
        if (permissionCode == Permission.PermissionCodes.LOCATION_SERVICE_REQUEST_CODE
                || permissionCode == Permission.PermissionCodes.STORAGE_AND_LOCATION_CODE){
            updateCurrentLocationFragmentShow();
            startGpsCurrentLocation();
        }
        if (permissionCode == Permission.PermissionCodes.CALL_PHONE_REQUEST_CODE){
            callPhonePermissionResult(true);
        }

    }

    @Override
    public void onPermissionNotGranted(String[] permission, int permissionCode) {
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M){
            this.requestPermissions(permission, permissionCode);
        }
    }

    @Override
    public void onPermissionDenied(int permissionCode) {
//        if (permissionCode == Permission.PermissionCodes.CALL_PHONE_REQUEST_CODE){
//            callPhonePermissionResult(false);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case Permission.PermissionCodes.STORAGE_REQUEST_CODE:
                storagePermissionResult(mHPlusPermission.verifyPermissions(grantResults));

                break;
            case Permission.PermissionCodes.LOCATION_SERVICE_REQUEST_CODE:
                locationPermissionResult(mHPlusPermission.verifyPermissions(grantResults));
                break;
            case Permission.PermissionCodes.CALL_PHONE_REQUEST_CODE:
                callPhonePermissionResult(mHPlusPermission.verifyPermissions(grantResults));
                break;
            case  Permission.PermissionCodes.STORAGE_AND_LOCATION_CODE:

                locationPermissionResult(mHPlusPermission.verifyTwoPermissionFromMulti(permissions, grantResults, HPlusPermission.LOCATION_SERVICE_CORSE, HPlusPermission.LOCATION_SERVICE_FINE));
                storagePermissionResult(mHPlusPermission.verifyTwoPermissionFromMulti(permissions, grantResults, HPlusPermission.WRITE_STORAGE, HPlusPermission.READ_STORAGE));
        }
    }

    private void callPhonePermissionResult(boolean verifyResult){
        if (verifyResult) {
            callPhone();
        } else {
            mAlertDialog = MessageBox.createSimpleDialog(this, "", getResources().getString(R.string.phone_call_permission_deny),
                    getResources().getString(R.string.ok), null);
        }
    }

    private void storagePermissionResult(boolean verifyResult){
        if (verifyResult){
            try {
                AppManager.shareInstance().startDownBackgroundTask(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, getString(R.string.permission_deny), Toast.LENGTH_LONG).show();
        }
    }

    private void locationPermissionResult(boolean verifyResult){
        if (verifyResult){
            startGpsCurrentLocation();
        }
        else{
            updateCurrentLocationFragmentShow();
        }
    }

    public int getCurrentHomeLocationId(){
        if (mCurrentHomeIndex < mHomeList.size() && mHomeList.get(mCurrentHomeIndex).getUserLocationData() != null){
            return mHomeList.get(mCurrentHomeIndex).getUserLocationData().getLocationID();
        }
        return CurrentLocationLogic.NO_FOUND_LOCATION_ID;
    }

}
