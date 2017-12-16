package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.view.TypeTextView;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateResponse;

import java.util.Locale;

/**
 * Created by Vincent on 28/10/15.
 */
public class UpdateVersionMinderActivity extends BaseHasBackgroundActivity {
    public final static String IS_NEED_TO_CURRENT = "is_need_to_current";

    private TypeTextView mTitleText;
    private TypeTextView mContentText;
    private RelativeLayout mUpdateLayout;
//    private RelativeLayout mRemindLayout;
    private UpdateResponse mUpdateInfo;
    private int mCurrentTimeHour;
    private AppConfig mAppConfig;
    private String mLocalLanguage;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mUpdateInfo = (UpdateResponse) getIntent().getSerializableExtra("version_action");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.verion_reminder);
        initDynamicBackground();

        init();
        initTextValue();
    }

    private void init() {
        mTitleText = (TypeTextView) findViewById(R.id.version_title);
        mContentText = (TypeTextView) findViewById(R.id.version_content);
        mUpdateLayout = (RelativeLayout) findViewById(R.id.version_layout);
        mAppConfig = AppConfig.shareInstance();
        mCurrentTimeHour = (int) ((System.currentTimeMillis()) / (3600 * 1000 * 24));
        mLocalLanguage = Locale.getDefault().getLanguage();
    }

    private void initTextValue() {
        String updateVersion = mUpdateInfo.version.replace("V", "").replace("v", "").replace("Version", "").replace("version", "").replace("ersion", "").trim();
        mTitleText.setText(getString(R.string.version_title, updateVersion));
        if ("zh".equals(mLocalLanguage)) {
            mContentText.setText(mUpdateInfo.updateLog.split("<=>")[0].split("=")[1]);
        } else {
            mContentText.setText(mUpdateInfo.updateLog.split("<=>")[1].split("=")[1]);
        }
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.version_update_now:
                mAppConfig.setUpdateVersionTime(0);
                UmengUpdateAgent.startDownload(UpdateVersionMinderActivity.this, mUpdateInfo);
                break;
            case R.id.version_update_later:
                mAppConfig.setUpdateVersionTime(mCurrentTimeHour);
                finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
