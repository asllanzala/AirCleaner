package com.honeywell.hch.airtouchv3.framework.app.activity;

import android.support.v4.app.Fragment;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.umeng.analytics.MobclickAgent;

/**
 * Base fragment, implement some common function
 * Created by nan.liu on 1/19/15.
 */
public class BaseFragment extends Fragment {
    protected String TAG = "BaseFragment";

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //statistic pager
        AppManager.shareInstance().registerBus(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        AppManager.shareInstance().unregisterBus(this);
    }
}
