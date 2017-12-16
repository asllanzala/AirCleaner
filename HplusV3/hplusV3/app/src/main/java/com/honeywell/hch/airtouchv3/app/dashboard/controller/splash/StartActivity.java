package com.honeywell.hch.airtouchv3.app.dashboard.controller.splash;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.MainActivity;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.config.UserConfig;

/**
 * Created by Jin Qian on 2/26/2015.
 */
public class StartActivity extends BaseActivity {
    ImageView welcomeBackground;
    private final int MY_PERMISSIONS_REQUEST_WRITE_SETTINGS = 1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



        super.TAG = "AirTouchStart";
//        Log.e("", getDeviceInfo(StartActivity.this));
        
        /*
         * press home button to quit, after that, show main page if return back to app.
         */
        if(!isTaskRoot())
        {
            finish();
            return;
        }
//        checkPermission();
        /*
         * show welcome/welcome back background page
         */
        welcomeBackground = (ImageView) findViewById(R.id.welcome_background);
        if (AppConfig.isLauchendFirstTime) {
            AppConfig.isLauchendFirstTime = false;
            welcomeBackground.setBackgroundResource(R.drawable.start_bg);
        } else {
            welcomeBackground.setBackgroundResource(R.drawable.start_back_bg);
        }

        /*
         * Login before main page
         */
//        if (!StringUtil.isEmpty(AppManager.shareInstance().getAuthorizeApp().getMobilePhone())) {
//            AppManager.shareInstance().getAuthorizeApp().currentUserLogin();
//        }

        UserConfig userConfig = new UserConfig(this);
        if (userConfig.loadAutoLogin())
            AppManager.shareInstance().getAuthorizeApp().currentUserLogin();

        /*
         * stay welcome page for a while
         */
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);

                    Intent intent = new Intent();
                    intent.setClass(StartActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();


    }



    public static String getDeviceInfo(Context context) {
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if( TextUtils.isEmpty(device_id) ){
                device_id = mac;
            }

            if( TextUtils.isEmpty(device_id) ){
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
