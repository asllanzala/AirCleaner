package com.honeywell.hch.airtouchv2.app.airtouch.controller.device;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseFragmentActivity;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.framework.view.ScrollLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * The Vertical Fragment for Control and Filter
 * Created by nan.liu on 2/24/15.
 */
public class DeviceActivity extends BaseFragmentActivity {
    public static final String ARG_DEVICE = "device";
    private static final String TAG = "AirTouchDevice";

    private ScrollLayout deviceScrollLayout;
    private DeviceControlFragment mControlFragment;
    private FilterFragment mFilterFragment;
    private DeviceRunStatusService serviceBinder;
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<>();
    private UserLocation mUserLocation = null; // this location may not be current location

    public static final int HOME_DEVICE_REQUEST_CODE = 11;
    public static final int HOUSE_DEVICE_REQUEST_CODE = 12;
    public static final int HOME_HOUSE_REQUEST_CODE = 13;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.TAG = TAG;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_device_control);
        deviceScrollLayout = (ScrollLayout) findViewById(R.id.control_filter_scroll);
        deviceScrollLayout.setOnViewChangeListener(scrollListener);
        deviceScrollLayout.setOnFirstPageDownListener(controlCloseListener);
        deviceScrollLayout.setIsScroll(true);

        loadFragment();
        initService();

        Bundle bundle = getIntent().getExtras();
        int locationId = bundle.getInt(HouseActivity.ARG_LOCATION);
        mUserLocation = AuthorizeApp.shareInstance().getLocationWithId(locationId);

    }

    public UserLocation getmUserLocation() {
        return mUserLocation;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(ARG_DEVICE, AuthorizeApp.shareInstance().getCurrentDeviceId());
        intent.putExtra(HouseActivity.ARG_LOCATION, mUserLocation.getLocationID());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService(serviceConnection);
    }

    public void loadFragment() {
        mControlFragment = DeviceControlFragment.newInstance(this);
        mFilterFragment = FilterFragment.newInstance(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.control_panel, mControlFragment);
        fragmentTransaction.replace(R.id.filter_panel, mFilterFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mControlFragment.setOnControlClickListener(new DeviceControlFragment.OnControlClickListener() {
            @Override
            public void onControl(boolean isOnControl) {
                if (isOnControl) {
                    deviceScrollLayout.setIsScroll(false);
                } else {
                    deviceScrollLayout.setIsScroll(true);
                }
            }
        });
        mControlFragment.setOnFilterClickListener(new DeviceControlFragment.OnFilterClickListener() {
            @Override
            public void onClick() {
                // Umeng statistic
                MobclickAgent.onEvent(DeviceActivity.this, "filter_status_click_event");

                if (deviceScrollLayout.getCurScreen() == 0) {
                    deviceScrollLayout.snapToScreen(1);
                } else {
                    deviceScrollLayout.snapToScreen(0);
                }
            }
        });
        mFilterFragment.setOnControlTutorialRemovedListener(new FilterFragment.OnControlTutorialRemovedListener() {
            @Override
            public void remove() {
                mControlFragment.removeControlTutorial();
            }
        });

        mFilterFragment.setOnFilterAnimationListener(new FilterFragment.OnFilterAnimationListener() {
            @Override
            public void onAnimation(int filter) {
                mControlFragment.setFilterAnimation(filter);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface MyTouchListener {
        public void onTouchEvent(MotionEvent event);
    }

    /**
     * register touch listener for fragment
     *
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * unregister touch listener for fragment
     *
     * @param listener
     */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }


    /**
     * Service to GetRunStatus 30s each
     */
    private void initService() {
        try {
            Intent intent = new Intent(DeviceActivity.this, Class.forName(DeviceRunStatusService.class
                    .getName()));
            bindService(intent, serviceConnection,
                    DeviceRunStatusService.BIND_AUTO_CREATE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "in onServiceDisconnected");
            serviceBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.log(LogUtil.LogLevel.VERBOSE, TAG, "in onServiceConnected");
            serviceBinder = ((DeviceRunStatusService.MyBinder) service).getService();
        }
    };

    /**
     * ScrollListener - to catch control page or filter page
     */
    ScrollLayout.OnViewChangeListener scrollListener = new ScrollLayout.OnViewChangeListener() {
        @Override
        public void OnViewChange(int view) {
            if (view == 0) {
                AppConfig.isFilterScrollPage = false;
            } else if (view == 1) {
                AppConfig.isFilterScrollPage = true;
                mFilterFragment.showFilterTutorial();
                mControlFragment.showControlTutorialForFilter();
            }
        }
    };

    ScrollLayout.OnFirstPageDownListener controlCloseListener = new ScrollLayout.OnFirstPageDownListener() {
        @Override
        public void onPageDwon() {
            finish();
            overridePendingTransition(0, 0);
        }
    };

}
