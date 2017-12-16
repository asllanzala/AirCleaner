package com.honeywell.hch.airtouchv3.app.authorize.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.EnrollAccessManager;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.config.UserConfig;
import com.honeywell.hch.airtouchv3.framework.notification.baidupushnotification.BaiduPushConfig;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv3.framework.webservice.task.UserLoginTask;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.SharePreferenceUtil;

/**
 * Created by Vincent on 04/01/16.
 */
public class UserLoginBaseActivity extends BaseActivity {
    protected String TAG = "UserLoginBaseActivity";
    protected Context mContext = this;
    protected UserConfig mUserConfig;
    protected AuthorizeApp mAuthorizeApp;

    private SuccessCallback mSuccessCallback;
    private ErrorCallback mErrorCallback;


    public interface SuccessCallback {
        void onSuccess(ResponseResult responseResult);
    }

    public interface ErrorCallback {
        void onError(ResponseResult responseResult, String errorMsg);
    }

    public void setSuccessCallback(SuccessCallback successCallback) {
        mSuccessCallback = successCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserConfig = new UserConfig(mContext);
        mAuthorizeApp = AppManager.shareInstance().getAuthorizeApp();
    }

    public AuthorizeApp getAuthorizeApp() {
        if (mAuthorizeApp == null) {
            mAuthorizeApp = AppManager.shareInstance().getAuthorizeApp();
        }
        return mAuthorizeApp;
    }

    public UserConfig getUserConfig() {
        if (mUserConfig == null) {
            mUserConfig = new UserConfig(mContext);
        }
        return mUserConfig;
    }

    protected void afterLoginSuccess(Dialog mDialog, String mMobilePhone, String mUserPassword) {
        AppManager appManager = AppManager.shareInstance();

        if (mDialog != null) {
            mDialog.dismiss();
        }
        //save data in SharedPreferences
        String mUserId = mAuthorizeApp.getUserID();
        String mSessionId = mAuthorizeApp.getSessionId();
        String mUserNickname = mAuthorizeApp.getNickname();
        mAuthorizeApp.saveUserInfo(mUserNickname, mMobilePhone, mUserPassword,
                mUserId, mSessionId, mAuthorizeApp.getCountryCode());
        mUserConfig.loadDefaultHomeId();


        if (mAuthorizeApp.isUserWantToEnroll()) {
            finish();
            EnrollAccessManager.startIntent((Activity) mContext, "");
        } else {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

    }

    protected void beginUserLogin(String mMobilePhone, String mUserPassword, IActivityReceive loginReceiver) {
        if (!mAuthorizeApp.isLoginSuccess()) {
            mAuthorizeApp.setIsAutoLoginOngoing(true);
        }

        UserLoginRequest userLoginRequest
                = new UserLoginRequest(mMobilePhone, mUserPassword, AppConfig.APPLICATION_ID);
        UserLoginTask userLoginTask
                = new UserLoginTask(loginReceiver, userLoginRequest);
        AsyncTaskExecutorUtil.executeAsyncTask(userLoginTask);
    }


    protected IActivityReceive userLogin (final Dialog mDialog, final Activity activity, final String mMobilePhone, final String mUserPassword) {


        IActivityReceive loginReceiver = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (!responseResult.isResult()) {
                    if (responseResult.getResponseCode() == StatusCode.UNAUTHORIZED) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        MessageBox.createSimpleDialog(activity, null,
                                getString(R.string.login_password_invalid), null, null);
                    } else {
                        errorHandle(responseResult, getString(R.string.enroll_error));
                    }
                } else {
                    afterLoginSuccess(mDialog, mMobilePhone, mUserPassword);
                    // India version
                    if (!AppConfig.shareInstance().isIndiaAccount()) {
                        SharePreferenceUtil.clearMyPreference(mContext, SharePreferenceUtil.getSharedPreferencesInstance());
                        PushManager.startWork(getApplicationContext(),
                                PushConstants.LOGIN_TYPE_API_KEY,
                                BaiduPushConfig.getMetaValue(mContext, BaiduPushConfig.BAIDUPUSHAPIKEY));
                    }
                }

            }
        };
        return loginReceiver;

    }


}
