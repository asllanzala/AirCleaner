package com.honeywell.hch.airtouchv3.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;


/**
 * Created by Jin Qian on 3/10/2015.
 */
public class WifiUtil {
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public static WifiCipherType getWifiCipher(String capability){

        String cipher = getEncryptString(capability);
        if (cipher.contains("WEP")){
            return WifiCipherType.WIFICIPHER_WEP;
        } else if (cipher.contains("WPA") || cipher.contains("WPA2") || cipher.contains("WPS")){
            return WifiCipherType.WIFICIPHER_WPA;
        } else if (cipher.contains("unknow")){
            return WifiCipherType.WIFICIPHER_INVALID;
        } else {
            return WifiCipherType.WIFICIPHER_NOPASS;
        }
    }

    public static String getEncryptString(String capability){
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(capability))
            return "unknow";
        if (capability.contains("WEP")){
            sb.append("WEP");
            return sb.toString();
        }
        if (capability.contains("WPA")){
            sb.append("WPA");
        }
        if (capability.contains("WPA2")){
            sb.append("/");
            sb.append("WPA2");
        }
        if (capability.contains("WPS")){
            sb.append("/");
            sb.append("WPS");
        }
        if (TextUtils.isEmpty(sb))
            return "OPEN";

        return sb.toString();
    }

    public static List<WifiConfiguration> getConfigurations(Context mContext){
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> mList = wm.getConfiguredNetworks();
        return mList;
    }

    public static boolean removeWifi(Context mContext ,int networkId){
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return wm.removeNetwork(networkId);
    }

    public static boolean addNetWork(WifiConfiguration cfg ,Context mContext){
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo mInfo = wm.getConnectionInfo();
        if (mInfo != null) {
            wm.disableNetwork(mInfo.getNetworkId());
        }
        boolean flag = false;
        if (cfg.networkId > 0) {
            Log.d(WifiUtil.class.getSimpleName(), "cfg networkId = " + cfg.networkId);
            flag = wm.enableNetwork(cfg.networkId,true);
            wm.updateNetwork(cfg);
        } else {
            int netId = wm.addNetwork(cfg);
            Log.d(WifiUtil.class.getSimpleName(), "after adding netId = " + netId);
            if (netId > 0) {
                wm.saveConfiguration();
                flag = wm.enableNetwork(netId, true);
            }
        }
        return flag;
    }

    public static WifiConfiguration createWifiConfig(String SSID, String Password,
                                                     WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        if (!SSID.startsWith("\"")) {
            SSID = "\"" + SSID + "\"";
        }
        config.SSID = SSID ;
        Log.d(WifiUtil.class.getSimpleName(), config.SSID );

        // 无密码
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        // WEP加密
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        // WPA加密
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
//			 config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//			 config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//			 config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//			 config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    public static WifiInfo getConnectedWifiInfo(Context mContext) {
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo();
    }

    public static boolean isWifiOpen(Context mContext){
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return  wm.isWifiEnabled() ;
    }

    public static void openWifi(final Context mContext , final IWifiOpen mCallBack){
        new Thread(
                new Runnable(){

                    @Override
                    public void run() {
                        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                        wm.setWifiEnabled(true);
                        while (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {

                        }
                        Log.d(WifiUtil.class.getSimpleName(), "openWifi finish... " + wm.getWifiState());
                        if (mCallBack != null) {
                            mCallBack.onWifiOpen(wm.getWifiState());
                        }
                    }


                }).start();
    }

    public interface IWifiOpen{
        public void onWifiOpen(int state);
    }


    public static boolean setHighestPriority(WifiManager mWifi,String ssid) {
        int priority = 0;
        WifiConfiguration currentWifi = null;
        List<WifiConfiguration> netList = mWifi.getConfiguredNetworks();
        if (netList != null) {
            for (WifiConfiguration wifiConfig : netList) {
                // ignore disabled entries or entries with same ssid
                if (wifiConfig.status != WifiConfiguration.Status.DISABLED
                        && wifiConfig.priority >= priority && !ssid.equals(wifiConfig.SSID)) {
                    priority = wifiConfig.priority + 1;
                }
                if (wifiConfig.SSID.contains(ssid)){
                    currentWifi = wifiConfig;
                }
                else{
                    mWifi.disableNetwork(wifiConfig.networkId);
                }
            }

            if (currentWifi != null){
                currentWifi.priority = priority;
                int netId = mWifi.updateNetwork(currentWifi);
                mWifi.enableNetwork(currentWifi.networkId, true);

                mWifi.reconnect();
                return true;
            }

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
            if (!StringUtil.isEmpty(ssid) && Build.VERSION.SDK_INT > 13){
                ssid = ssid.substring(1,ssid.length() - 1);
            }
        }

        return ssid;
    }


    public static WifiConfiguration getWifiConfiguration(ScanResult scanResult) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = '\"' + scanResult.SSID + '\"';
        wifiConfiguration.hiddenSSID = false;
        wifiConfiguration.allowedKeyManagement
                .set(WifiConfiguration.KeyMgmt.NONE);
        return wifiConfiguration;
    }

    public static boolean setHighestPriority(ScanResult scanResult,WifiManager mWifi) {
        int priority = 0;
        WifiConfiguration currentWifi = null;
        List<WifiConfiguration> netList = mWifi.getConfiguredNetworks();
        if (netList != null) {
            for (WifiConfiguration wifiConfig : netList) {
                // ignore disabled entries or entries with same ssid
                if (wifiConfig.status != WifiConfiguration.Status.DISABLED
                        && wifiConfig.priority >= priority && !scanResult.SSID.equals(wifiConfig.SSID)) {
                    priority = wifiConfig.priority + 1;
                }
                if (wifiConfig.SSID.contains(scanResult.SSID)) {
                    currentWifi = wifiConfig;
                } else {
                    mWifi.disableNetwork(wifiConfig.networkId);
                }
            }

            if (currentWifi != null) {
                currentWifi.priority = 40;
                int netId = mWifi.updateNetwork(currentWifi);
                mWifi.enableNetwork(currentWifi.networkId, true);

                mWifi.reconnect();
                return true;
            }

        }
        return false;
    }


    public static boolean reConnectWifiInAndroidM(ScanResult scanResult,WifiManager mWifi){
        WifiConfiguration wifiConfiguration = getWifiConfiguration(scanResult);
        boolean disconnect= mWifi.disconnect();
        if (!disconnect){
            return false;
        }

        int res = mWifi.addNetwork(wifiConfiguration);
        if (res==-1){
            if (WifiUtil.setHighestPriority(scanResult,mWifi)){
               return true;
            }
            return false;
        }
        boolean enabled=mWifi.enableNetwork(res, true);

        if (!enabled){
            return false;
        }
        mWifi.reconnect();
        return true;
    }


}
