package com.honeywell.hch.airtouchv2.framework.app.activity;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.honeywell.hch.airtouchv2.lib.util.UmengUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Base fragment activity, implement some common function
 * Created by Jin Qian on 1/30/15.
 */
public class BaseFragmentActivity extends FragmentActivity {
    protected String TAG = "BaseFragmentActivity";

    public void onResume() {
        super.onResume();
        UmengUtil.onActivityResume(this, TAG);
    }
    public void onPause() {
        super.onPause();
        UmengUtil.onActivityPause(this, TAG);
    }

    protected void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT)
                .show();
    }
}
