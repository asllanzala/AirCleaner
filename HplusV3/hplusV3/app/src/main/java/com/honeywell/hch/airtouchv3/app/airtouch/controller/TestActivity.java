package com.honeywell.hch.airtouchv3.app.airtouch.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.EnrollChoiceActivity;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.SwapLocationRequest;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice.GroupManager;
import com.honeywell.hch.airtouchv3.app.dashboard.model.DeviceListRequest;
import com.honeywell.hch.airtouchv3.app.dashboard.model.ScenarioGroupRequest;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.WeatherData;
import com.honeywell.hch.airtouchv3.framework.share.ShareUtility;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.ThinkPageClient;
import com.honeywell.hch.airtouchv3.framework.webservice.task.DeleteLocationTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.framework.webservice.task.SwapLocationNameTask;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by Jin Qian on 1/22/2015.
 */
public class TestActivity extends BaseActivity {
    private Button loginButton;
    private Button enrollButton;
    private Button addDeviceButton;
    private Button thinkpageButton;
    private Button updateButton;
    private Button shareButton;
    private Button keyButton;
    private Button locationButton;
    private TextView homeTextView;
    private GroupManager mGroupManager;

    // emotional share
    private ShareUtility mEps;
    private LinearLayout emotionShareLayout;
    private LinearLayout emotionShareDummyLayout;
    private Animation translateInAnimation;
    private Animation translateOutAnimation;
    private ImageView weChatShareImageView;
    private ImageView weBoShareImageView;
    private ImageView captureImageView;
    private ImageView captureImageView2;
    private LinearLayout shareCancelLayout;

    private static String TAG = "AirTouchTest";
    private String city = "Shanghai";
    private String lang = "zh-chs";
    private char temperatureUnit = 'c';


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mGroupManager = new GroupManager();
        keyButton = (Button) findViewById(R.id.key);
        keyButton.setOnClickListener(keyOnClick);
//        homeTextView = (TextView) findViewById(R.id.home);
        enrollButton = (Button) findViewById(R.id.btnEnroll);
        enrollButton.setOnClickListener(enrollOnClick);
        loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(loginOnClick);
        addDeviceButton = (Button) findViewById(R.id.btnDevice);
        addDeviceButton.setOnClickListener(deviceOnClick);
        thinkpageButton = (Button) findViewById(R.id.thinkPageBtn);
        thinkpageButton.setOnClickListener(thickPageOnClick);
        updateButton = (Button) findViewById(R.id.btnUpdate);
        updateButton.setOnClickListener(updateOnClick);

        // delete device
        locationButton = (Button) findViewById(R.id.btnLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] deviceId = {28431};
                int groupId = 4;
                DeviceListRequest deviceListRequest = new DeviceListRequest(deviceId);
                mGroupManager.deleteDeviceFromGroup(groupId, deviceListRequest);
                mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
                    @Override
                    public void onSuccess(ResponseResult responseResult) {
                        int a = 0;
                    }
                });

            }
        });


        // umeng share
        shareButton = (Button) findViewById(R.id.btnShare);
        shareButton.setOnClickListener(shareOnClick);
        weChatShareImageView = (ImageView) findViewById(R.id.wechat_share_btn_dummy);
        weChatShareImageView.setOnClickListener(weChatShareOnClick);
        weBoShareImageView = (ImageView) findViewById(R.id.webo_share_btn_dummy);
        weBoShareImageView.setOnClickListener(weboShareOnClick);
        shareCancelLayout = (LinearLayout) findViewById(R.id.share_cancel_layout_dummy);
        shareCancelLayout.setOnClickListener(shareCancelOnClick);
        emotionShareLayout = (LinearLayout) findViewById(R.id.emotion_share_layout);
        emotionShareLayout.setVisibility(View.INVISIBLE);
        translateInAnimation = AnimationUtils.loadAnimation(TestActivity.this, R.anim.share_translate_in);
        translateOutAnimation = AnimationUtils.loadAnimation(TestActivity.this, R.anim.share_translate_out);
        translateInAnimation.setAnimationListener(new translateInAnimationListener());
        emotionShareDummyLayout = (LinearLayout) findViewById(R.id.emotion_share_layout_dummy);
        emotionShareDummyLayout.setVisibility(View.INVISIBLE);

        // Umeng share
        mEps = ShareUtility.getInstance(TestActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isHasNullPoint()) {
            initDevice();
        }

    }

    private void errorHandle(ResponseResult responseResult) {
        if (responseResult.getExeptionMsg() != null || !responseResult.getExeptionMsg().equals("")) {
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exception：" + responseResult.getExeptionMsg());
        }
    }


    View.OnClickListener enrollOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("isEnrollDemo", true);
            intent.setClass(TestActivity.this, EnrollChoiceActivity.class);
            startActivity(intent);
        }
    };

    /**
     * Create Group
     */
    public View.OnClickListener loginOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int[] deviceId = {28431};
            String groupName = "Group1";
            int masterDeviceId = 28474;
            int locationId = 9238;
            DeviceListRequest request = new DeviceListRequest(deviceId);
            mGroupManager.createGroup(groupName, masterDeviceId, locationId, request);
            mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
                @Override
                public void onSuccess(ResponseResult responseResult) {
                    int a = 0;
                }
            });

        }
    };

    View.OnClickListener shareOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            emotionShareLayout.startAnimation(translateInAnimation);
//            mEps.initShareMsgAndPic();
        }
    };

    View.OnClickListener weboShareOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mEps.umengShare(SHARE_MEDIA.SINA);

        }
    };

    View.OnClickListener weChatShareOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mEps.umengShare(SHARE_MEDIA.WEIXIN_CIRCLE);


        }
    };

    View.OnClickListener shareCancelOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            emotionShareDummyLayout.setVisibility(View.INVISIBLE);
            emotionShareLayout.startAnimation(translateOutAnimation);
        }
    };


    /**
     * Group Scenario
     */
    View.OnClickListener updateOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = getIntent().getExtras();
            int groupId = bundle.getInt("group_id");
            int scenarioMode = ScenarioGroupRequest.SCENARIO_SLEEP;
            ScenarioGroupRequest request = new ScenarioGroupRequest(scenarioMode);
            mGroupManager.sendScenarioToGroup(groupId, request);
            mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
                @Override
                public void onSuccess(ResponseResult responseResult) {
                    int a = 0;
                }
            });


        }

    };

    View.OnClickListener deviceOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent();
//            intent.setClass(TestActivity.this, LocationAndDeviceRegisterActivity.class);
//            startActivity(intent);

            //            VersionCollector versionCollector = HPlusApplication.getVersionCollector();
//            int downloadStatus = DownloadUtils.getInstance()
//                    .getDownloadStatusByDownloadId(versionCollector.getDownloadId());
            // If not downloading right now, or download failed, do not reminder
//            if (versionCollector.getDownloadId() == DownloadUtils.FILE_DOWNLOAD_ID_INVALID || downloadStatus == -1
//                    || (downloadStatus != DownloadManager.STATUS_RUNNING && downloadStatus != DownloadManager.STATUS_PAUSED)) {
//                UpgradeCheckThread upgradeCheckThread = new UpgradeCheckThread();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    upgradeCheckThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                } else {
//                    upgradeCheckThread.execute("");
//                }
//            }
        }
    };

    /**
     * Delete Group
     */
    View.OnClickListener keyOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int groupId = 55;
            mGroupManager.deleteGroup(groupId);
            mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
                @Override
                public void onSuccess(ResponseResult responseResult) {
                    int a = 0;
                }
            });


//            VersionCollector versionCollector = HPlusApplication.getVersionCollector();
//            int downloadStatus = DownloadUtils.getInstance().getDownloadStatusByDownloadId(versionCollector.getDownloadId());
//            // 没有正在下载，或者下载失败的情况下载再次检测更新,这个逻辑是为了保证正在下载的时候，不要重复提醒
//            if (versionCollector.getDownloadId() == DownloadUtils.FILE_DOWNLOAD_ID_INVALID || downloadStatus == -1
//                    || (downloadStatus != DownloadManager.STATUS_RUNNING && downloadStatus != DownloadManager.STATUS_PAUSED)) {
//                UpgradeCheckThread upgradeCheckThread = new UpgradeCheckThread();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    upgradeCheckThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                } else {
//                    upgradeCheckThread.execute("");
//                }
//            }

//            try {
//                TripleDES tripleDES = new TripleDES("ECB");
//                byte[] a = tripleDES.encrypt("HON123well");
//                LogUtil.log(LogUtil.LogLevel.INFO, TAG, new String(a));
//                String b = new String(tripleDES.decrypt(a));
//                LogUtil.log(LogUtil.LogLevel.INFO, TAG, b);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }
    };


    /**
     * Change group name
     */
    View.OnClickListener thickPageOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String groupName="testName";
            int groupId = 4;
            mGroupManager.updateGroupName(groupName, groupId);
            mGroupManager.setSuccessCallback(new GroupManager.SuccessCallback() {
                public void onSuccess(ResponseResult responseResult) {

                }
            });

        }

    };

    final IReceiveResponse thickPageResponse = new IReceiveResponse() {

        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {

            if (httpRequestResponse.getStatusCode() == StatusCode.OK) {

                switch (httpRequestResponse.getRequestID()) {
                    case ALL_DATA:
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            WeatherData weatherData = new Gson().fromJson(httpRequestResponse.getData(),
                                    WeatherData.class);

                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "all temp:"
                                    + weatherData.getWeather().get(0).getNow().getTemperature());
                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "all pm2.5:"
                                    + weatherData.getWeather().get(0).getNow().getAirQuality().getAirQualityIndex().getPm25());

                            RequestID requestID = RequestID.NOW_DATA;
                            ThinkPageClient.sharedInstance().getWeatherData(city, lang, temperatureUnit, requestID, thickPageResponse);
                        }
                        break;

                    case NOW_DATA:
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            WeatherData weatherData = new Gson().fromJson(httpRequestResponse.getData(),
                                    WeatherData.class);

                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "now temp:"
                                    + weatherData.getWeather().get(0).getNow().getTemperature());

                            RequestID requestID = RequestID.AIR_DATA;
                            ThinkPageClient.sharedInstance().getWeatherData(city, lang, temperatureUnit, requestID, thickPageResponse);
                        }
                        break;

                    case AIR_DATA:
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            WeatherData weatherData = new Gson().fromJson(httpRequestResponse.getData(),
                                    WeatherData.class);

                            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "air pm2.5:"
                                    + weatherData.getWeather().get(0).getNow().getAirQuality()
                                    .getAirQualityIndex().getPm25());
                        }
                        break;

                    default:
                        break;
                }

            } else {
                if (httpRequestResponse.getException() != null) {
                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "Exeption："
                            + httpRequestResponse.getException().toString());
                }
            }
        }
    };

    private Boolean isHasNullPoint() {
//        if (AppManager.shareInstance().getCurrentHome() == null)
//            return true;

//        if (AuthorizeApp.shareInstance().getCurrentHome().getCurrentDevice().getHomeDevicePm25() == null)
//            return true;
//
//        if (AuthorizeApp.shareInstance().getCurrentHome().getCurrentDevice().getDeviceInfo() == null)
//            return true;

        return false;
    }

    private void initDevice() {
//        UserLocation currentHome = AuthorizeApp.shareInstance().getCurrentHome();
//        int deviceNumber = currentHome.getDeviceInfo().size();
//        if (deviceNumber > 0) {
//            ArrayList<HomeDevice> homeDevices = new ArrayList<>();
//            ArrayList<HomeDevicePM25> homeDevicesPM25 = currentHome.getHomeDevicesPM25();
//            ArrayList<DeviceInfo> deviceInfos = currentHome.getDeviceInfo();
//            if (homeDevicesPM25.size() > 0) {
//                for (int i = 0; i < deviceNumber; i++) {
//                    HomeDevice homeDevice = new HomeDevice();
//                    homeDevice.setHomeDevicePm25(homeDevicesPM25.get(i));
//                    homeDevice.setDeviceInfo(deviceInfos.get(i));
//                    homeDevices.add(homeDevice);
//                }
//                currentHome.setHomeDevices(homeDevices);
//            }
//        }
    }

    private void showDevice() {
//        String home = AuthorizeApp.shareInstance().getCurrentHome().getName();
//        ArrayList<HomeDevicePM25> homeDevicesPM25
//                = AuthorizeApp.shareInstance().getCurrentHome().getHomeDevicesPM25();
//        for (int i = 0; i < homeDevicesPM25.size(); i++) {
//            String mode = homeDevicesPM25.get(i).getAirCleanerFanModeSwitch();
//            String speed = homeDevicesPM25.get(i).getFanSpeedStatus();
//            int id = homeDevicesPM25.get(i).getDeviceID();
//            int pm25 = homeDevicesPM25.get(i).getPM25Value();
//
//            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "home:" + home + "  deviceId:" + id
//                    + "  pm2.5:" + pm25 + "  mode:" + mode + " speed:" + speed);
//            homeTextView.setText("home:" + home + "  deviceId:" + id
//                    + "  pm2.5:" + pm25 + "  mode:" + mode + " speed:" + speed);
//        }
    }


    /**
     * Animation helper
     */
    private class translateInAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            emotionShareDummyLayout.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        mEps.addSinaCallback(requestCode, resultCode, data);

    }


    private void deleteLocation(int locationId) {
        IActivityReceive swapLocationResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (responseResult.isResult()) {
                    switch (responseResult.getRequestId()) {
                        case DELETE_LOCATION:
                            if (responseResult.getResponseCode() == StatusCode.OK)
                                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "swap location succeed!");
                            else if (responseResult.getResponseCode() == StatusCode.BAD_REQUEST)
                                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "swap location bad request!");
                            else if (responseResult.getResponseCode() == StatusCode.NOT_FOUND)
                                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "swap location not found!");
                            else
                                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "swap location fail!");
                            break;
                    }
                } else {
                    errorHandle(responseResult);
                }
            }
        };

        DeleteLocationTask requestTask
                = new DeleteLocationTask(locationId, swapLocationResponse);
        requestTask.execute();


    }

    private void swapLocation(int locationId) {
        IActivityReceive swapLocationResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (responseResult.isResult()) {
                    switch (responseResult.getRequestId()) {
                        case SWAP_LOCATION:
                            if (responseResult.getResponseCode() == StatusCode.OK)
                                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "swap location succeed!");
                            else if (responseResult.getResponseCode() == StatusCode.BAD_REQUEST)
                                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "swap location bad request!");
                            else if (responseResult.getResponseCode() == StatusCode.NOT_FOUND)
                                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "swap location not found!");
                            else
                                LogUtil.log(LogUtil.LogLevel.DEBUG, TAG, "swap location fail!");
                            break;
                    }
                } else {
                    errorHandle(responseResult);
                }
            }
        };

        SwapLocationRequest swapLocationRequest = new SwapLocationRequest();
        swapLocationRequest.setName("jin");
        SwapLocationNameTask requestTask
                = new SwapLocationNameTask(locationId, swapLocationRequest, swapLocationResponse);
        requestTask.execute();

    }

}
