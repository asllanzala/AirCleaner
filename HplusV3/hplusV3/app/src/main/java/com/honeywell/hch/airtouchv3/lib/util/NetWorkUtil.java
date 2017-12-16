package com.honeywell.hch.airtouchv3.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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


    public static boolean isWifiAvailable(Context context) {

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!networkInfo.isConnected()) {
            return false;
        }
        return true;
    }

    public static boolean isNetworkCanAccessInternet(){
        try {
            String ip = "www.baidu.com";
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {

                return true;
            }
        } catch (IOException e) {

        } catch (InterruptedException e) {

        } finally {

        }
        return false;
    }


    public static  String updateWifiInfo(Context context) {
        String ssid = "";
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!networkInfo.isConnected()) {
            return ssid;
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        if (info != null){
            ssid = info.getSSID();
            //在4.0以上获取到的ssid带有 “”,需要去除
            if (!StringUtil.isEmpty(ssid) && Build.VERSION.SDK_INT > 16){
                ssid = ssid.substring(1,ssid.length() - 1);
            }
        }

        return ssid;
    }


    public static  int getNetworkIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        if (info != null){
            return info.getIpAddress();
        }
        return 0;
    }
}
