package com.honeywell.hch.airtouchv3.framework.app.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.database.CityChinaDBService;
import com.honeywell.hch.airtouchv3.framework.database.CityIndiaDBService;
import com.honeywell.hch.airtouchv3.framework.permission.HPlusPermission;
import com.honeywell.hch.airtouchv3.framework.permission.PermissionListener;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.lib.util.NetWorkUtil;
import com.honeywell.hch.airtouchv3.lib.util.UmengUtil;

/**
 * Base activity, implement some common function
 * Created by nan.liu on 1/19/15.
 */
public class BaseActivity extends Activity implements PermissionListener{
    protected String TAG = "AirTouchBaseActivity";
    private CityChinaDBService mCityChinaDBService;
    private CityIndiaDBService mCityIndiaDBService;

    protected HPlusPermission mHPlusPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UmengUtil.onActivityCreate(this);

        mHPlusPermission = new HPlusPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UmengUtil.onActivityResume(this, TAG);
        AppManager.shareInstance().registerBus(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        UmengUtil.onActivityPause(this, TAG);
        AppManager.shareInstance().unregisterBus(this);
    }

    protected void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT)
                .show();
    }

    public CityChinaDBService getCityChinaDBService() {
        if (mCityChinaDBService == null) {
            mCityChinaDBService = new CityChinaDBService(this);
        }
        return mCityChinaDBService;
    }

    public CityIndiaDBService getCityIndiaDBService() {
        if (mCityIndiaDBService == null) {
            mCityIndiaDBService = new CityIndiaDBService(this);
        }
        return mCityIndiaDBService;
    }

    public void errorHandle(ResponseResult responseResult, String errorMsg) {
        if (!NetWorkUtil.isNetworkAvailable(this)) {
            MessageBox.createSimpleDialog(this, null, getString(R.string.no_network), null, null);
            return;
        }

        if (responseResult.getResponseCode() == StatusCode.NETWORK_ERROR) {
            MessageBox.createSimpleDialog(this, null,
                    getString(R.string.no_network), null, null);
            return;
        }

        if (responseResult.getExeptionMsg() != null
                && responseResult.getResponseCode() == StatusCode.NETWORK_TIMEOUT) {
            MessageBox.createSimpleDialog(this, null,
                    getString(R.string.control_timeout), null, null);
            return;
        }

        if (responseResult.getResponseCode() == StatusCode.BAD_REQUEST) {
            MessageBox.createSimpleDialog(this, null, errorMsg, null, null);
            return;
        }

        if (errorMsg != null && !errorMsg.equals("")) {
            MessageBox.createSimpleDialog(this, null, errorMsg, null, null);
            return;
        }

        MessageBox.createSimpleDialog(this, null,
                getString(R.string.enroll_error), null, null);
    }


    @Override
    public void onPermissionGranted(int permissionCode) {
    }

    @Override
    public void onPermissionNotGranted(String[] permission, int permissionCode) {
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M) {
            this.requestPermissions(permission, permissionCode);
        }
    }

    @Override
    public void onPermissionDenied(int permissionCode) {
    }

    public boolean isPermissionEnabled(String permission){
        return mHPlusPermission.checkAppPermission(this,permission);
    }


}
