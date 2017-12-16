package com.honeywell.hch.airtouchv2.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {

    /**
     * 判断手机网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        try
        {
            ConnectivityManager mgr = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] info = mgr.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogUtil.log(LogUtil.LogLevel.ERROR,"NetworkUtil","Exception = " + e.toString());
        }
        return false;
    }
}
