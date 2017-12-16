package com.honeywell.hch.airtouchv3.framework.notification.baidupushnotification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.view.TypeTextView;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import java.util.HashMap;

/**
 * Created by Vincent on 28/10/15.
 */
public class BaiduPushRemindActivity extends BaseHasBackgroundActivity {
    private TypeTextView mPushTitle;
    private TypeTextView mContentText;
    private TypeTextView mSupportText;
    private RelativeLayout mRemindLayout;
    private String mNotification = "";
    private final String TAG = "BaiduPushRemindActivity";
    private HashMap mTouchuanMap = null;

    private final int TITLEDELAYTIME = 100;
    private final int CONTENTDELAYTIME = 50;
    private final String TELEPHONE = "400-7204321";

    private static final String NOTIFICATIONMSG = "tongzhi_msg";
    private static final String TOUCHUANMSG = "touchuan_msg";
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotification = getIntent().getStringExtra(NOTIFICATIONMSG);
        mTouchuanMap = (HashMap) getIntent().getSerializableExtra(TOUCHUANMSG);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_baidu_push_remind);
        initDynamicBackground();

        init();
        initTextValueNoType();
    }

    private void init() {
        mPushTitle = (TypeTextView) findViewById(R.id.push_sensor_title);
        mContentText = (TypeTextView) findViewById(R.id.push_sensor_content);
        mSupportText = (TypeTextView) findViewById(R.id.push_sensor_support);
        mRemindLayout = (RelativeLayout) findViewById(R.id.push_sensor_layout);
    }

    private void initTextValueNoType() {
        mPushTitle.setText(getResources().getString(R.string.gps_fail_content3));

        if (!"".equals(mNotification) && mNotification != null) {
            mContentText.setText(mNotification);
        } else {
            if (mTouchuanMap != null) {
//                        mContentText.start((String) mTouchuanMap.get("deviceType") + (String) mTouchuanMap.get("messageType"));
                mContentText.setText(getResources().getString(R.string.sensor_error));
            } else {
                mContentText.setText(getResources().getString(R.string.sensor_error));
            }
        }

        if (!StringUtil.isEmpty(mNotification) && (int) mNotification.charAt(0) != 35774) {
            mSupportText.setVisibility(View.VISIBLE);
            mSupportText.setText(getResources().getString(R.string.sensor_support));
        }
        mRemindLayout.setVisibility(View.VISIBLE);


    }
//    private void initTextValue() {
//        mPushTitle.start(getResources().getString(R.string.gps_fail_content3), TITLEDELAYTIME);
//        mPushTitle.setOnTypeViewListener(new TypeTextView.OnTypeViewListener() {
//            @Override
//            public void onTypeStart() {
//
//            }
//
//            @Override
//            public void onTypeOver() {
//                if (!"".equals(mNotification) && mNotification != null) {
//                    mContentText.start(mNotification, CONTENTDELAYTIME);
//                } else {
//                    if (mTouchuanMap != null) {
////                        mContentText.start((String) mTouchuanMap.get("deviceType") + (String) mTouchuanMap.get("messageType"));
//                        mContentText.start(getResources().getString(R.string.sensor_error), CONTENTDELAYTIME);
//                    } else {
//                        mContentText.start(getResources().getString(R.string.sensor_error), CONTENTDELAYTIME);
//                    }
//                }
//            }
//        });
//        mContentText.setOnTypeViewListener(new TypeTextView.OnTypeViewListener() {
//            @Override
//            public void onTypeStart() {
//
//            }
//
//            @Override
//            public void onTypeOver() {
//                if (!StringUtil.isEmpty(mNotification) && (int) mNotification.charAt(0) != 35774) {
//                    mSupportText.setVisibility(View.VISIBLE);
//                    mSupportText.start(getResources().getString(R.string.sensor_support), CONTENTDELAYTIME);
//                } else {
//                    mRemindLayout.setVisibility(View.VISIBLE);
//                    AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
//                    alphaAnimation.setDuration(1000);
//                    mRemindLayout.startAnimation(alphaAnimation);
//                }
//            }
//
//        });
//        mSupportText.setOnTypeViewListener(new TypeTextView.OnTypeViewListener() {
//            @Override
//            public void onTypeStart() {
//
//            }
//
//            @Override
//            public void onTypeOver() {
//                mRemindLayout.setVisibility(View.VISIBLE);
//                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
//                alphaAnimation.setDuration(1000);
//                mRemindLayout.startAnimation(alphaAnimation);
//            }
//
//        });
//
//    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.push_sensor_support:
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                Uri data = Uri.parse("tel:" + TELEPHONE);
//                intent.setData(data);
//                startActivity(intent);
                if (checkPermission())
                    callPhone();
                break;
            case R.id.version_update_now:
                finish();
                break;

        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                return false;
            }
            return true;
        }
        return true;
    }

    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + TELEPHONE);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone();
                } else {
                    Toast.makeText(this, getString(R.string.phone_call_permission_deny), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
