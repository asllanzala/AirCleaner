package com.honeywell.hch.airtouchv2.lib.util;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;


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
}
