package com.honeywell.hch.airtouchv3.framework.app.activity;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.lib.util.UmengUtil;

/**
 * Base fragment activity, implement some common function
 * Created by Jin Qian on 1/30/15.
 */
public class BaseFragmentActivity extends FragmentActivity{
    protected String TAG = "BaseFragmentActivity";



    public void onResume() {
        super.onResume();
        UmengUtil.onActivityResume(this, TAG);
        AppManager.shareInstance().registerBus(this);
    }
    public void onPause() {
        super.onPause();
        UmengUtil.onActivityPause(this, TAG);
        AppManager.shareInstance().unregisterBus(this);
    }

    protected void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT)
                .show();
    }

}
