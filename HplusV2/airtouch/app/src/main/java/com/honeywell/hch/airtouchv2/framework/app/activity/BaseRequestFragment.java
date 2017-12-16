package com.honeywell.hch.airtouchv2.framework.app.activity;

import com.honeywell.hch.airtouchv2.framework.webservice.TccClient;

import java.util.ArrayList;

/**
 * Base request fragment, implement some common request function
 * Created by nan.liu on 4/8/15.
 */
public class BaseRequestFragment extends BaseFragment {
//    protected String TAG = "AirTouchBaseRequestFragment";

    private ArrayList<Integer> mRequestList = new ArrayList<>();

    protected TccClient getRequestClient() {
        return TccClient.sharedInstance();
    }

    protected void addRequestId(int requestId) {
        mRequestList.add(requestId);
    }

    protected void removeRequestId(int requestId) {
        for (int i = 0; i < mRequestList.size(); i++) {
            if (mRequestList.get(i) == requestId) {
                mRequestList.remove(i);
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i < mRequestList.size(); i++) {
            getRequestClient().cancelRequest(mRequestList.get(i));
        }
        mRequestList.clear();
    }
}
