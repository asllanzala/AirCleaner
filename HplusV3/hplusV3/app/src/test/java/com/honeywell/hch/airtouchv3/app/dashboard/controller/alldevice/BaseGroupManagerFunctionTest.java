package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice;

import com.honeywell.hch.airtouchv3.framework.app.AppManager;

/**
 * Created by Vincent on 11/1/16.
 */
public class BaseGroupManagerFunctionTest {

    protected void setReloginSuccess(){
        AppManager.shareInstance().getAuthorizeApp().setIsLoginSuccess(true);
        AppManager.shareInstance().getAuthorizeApp().setSessionLastUpdated(System.currentTimeMillis());

    }

    protected void setReloginFail(){
        AppManager.shareInstance().getAuthorizeApp().setIsLoginSuccess(false);
        AppManager.shareInstance().getAuthorizeApp().setSessionLastUpdated(System.currentTimeMillis());

    }
}
