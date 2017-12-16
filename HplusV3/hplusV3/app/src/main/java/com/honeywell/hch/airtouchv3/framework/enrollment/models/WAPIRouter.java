package com.honeywell.hch.airtouchv3.framework.enrollment.models;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

import java.io.Serializable;
import java.util.ArrayList;

public class WAPIRouter implements IRequestParams, Serializable, Comparable<WAPIRouter> {

    private static final long serialVersionUID = -4265312612937472161L;

    public enum RouterSecurity {
        OPEN("OPEN", R.string.wapi_router_security_open),
        WEP_PSK("WEP_PSK", R.string.wapi_router_security_wep),
        WPA_AES_PSK("WPA_AES_PSK", R.string.wapi_router_security_wpa_aes),
        WPA_TKIP_PSK("WPA_TKIP_PSK", R.string.wapi_router_security_wpa_tkip),
        WPA2_AES_PSK("WPA2_AES_PSK", R.string.wapi_router_security_wpa2_aes),
        WPA2_TKIP_PSK("WPA2_TKIP_PSK", R.string.wapi_router_security_wpa2_tkip),
        WPA2_MIXED_PSK("WPA2_MIXED_PSK", R.string.wapi_router_security_wpa2_mixed);

        private String mServerValue;
        private int mStringResId;

        private RouterSecurity(String serverValue, int stringResId) {
            mServerValue = serverValue;
            mStringResId = stringResId;
        }

        public String getServerValue() {
            return mServerValue;
        }

        public int getStringResId() {
            return mStringResId;
        }

        public static RouterSecurity fromServerValue(String serverValue) {
            for (RouterSecurity routerSecurity : RouterSecurity.values()) {
                if (routerSecurity.getServerValue().equals(serverValue)) {
                    return routerSecurity;
                }
            }

            return OPEN;
        }

        public static String[] getStringArray(Context context) {
            ArrayList<String> strings = new ArrayList<String>();
            for (RouterSecurity routerSecurity : RouterSecurity.values()) {
                strings.add(context.getString(routerSecurity.getStringResId()));
            }

            String[] stringsArray = new String[strings.size()];
            return strings.toArray(stringsArray);
        }
    }

    public enum RouterSignalStrength {
        VERY_POOR("Very poor", 5),
        POOR("Poor", 4),
        GOOD("Good", 3),
        VERY_GOOD("Very good", 2),
        EXCELLENT("Excellent", 1);

        private String mServerValue;
        private int mSortOrder; // Lower numbers will put this enum value at the top

        private RouterSignalStrength(String serverValue, int sortOrder) {
            mServerValue = serverValue;
            mSortOrder = sortOrder;
        }

        public String getServerValue() {
            return mServerValue;
        }

        public int getSortOrder() {
            return mSortOrder;
        }

        public static RouterSignalStrength fromServerValue(String serverValue) {
            if (serverValue == null) {
                return null;
            }

            for (RouterSignalStrength routerSignalStrength : RouterSignalStrength.values()) {
                if (routerSignalStrength.getServerValue().equals(serverValue.trim())) {
                    return routerSignalStrength;
                }
            }

            return null;
        }
    }

    @SerializedName("SSID")
    private String mSSID;

    @SerializedName("Security")
    private String mSecurity;

    @SerializedName("Password")
    private String mPassword;

    @SerializedName("SignalStrength")
    private String mSignalStrength;

    public WAPIRouter() {
    }

    public WAPIRouter(WAPIRouter wapiRouter) {
        mSSID = wapiRouter.getSSID();
        mSecurity = wapiRouter.getSecurity().getServerValue();
        mPassword = wapiRouter.getPassword();
        mSignalStrength = wapiRouter.getSignalStrength().getServerValue();
    }

    public String getSSID() {
        return mSSID;
    }

    public void setSSID(String SSSID) {
        mSSID = SSSID;
    }

    public RouterSecurity getSecurity() {
        return RouterSecurity.fromServerValue(mSecurity);
    }

    public void setSecurity(RouterSecurity routerSecurity) {
        mSecurity = routerSecurity.getServerValue();
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public RouterSignalStrength getSignalStrength() {
        return RouterSignalStrength.fromServerValue(mSignalStrength);
    }

    public void setSignalStrength(RouterSignalStrength signalStrength) {
        mSignalStrength = signalStrength.getServerValue();
    }

    public boolean isLocked() {
        return getSecurity() != RouterSecurity.OPEN;
    }

    public boolean isWEP() {
        return getSecurity() == RouterSecurity.WEP_PSK;
    }

    @Override
    public String getRequest(Gson gson) {
        gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(this);
    }

    @Override
    public String getPrintableRequest(Gson gson) {
        String password = mPassword;
        mPassword = "*****";
        String log = getRequest(gson);
        mPassword = password;
        return log;
    }

    @Override
    public int compareTo(WAPIRouter another) {
        if (another == null) {
            return -1;
        }

        RouterSignalStrength anotherSignal = another.getSignalStrength();
        if (anotherSignal == null) {
            return -1;
        }

        if (getSignalStrength() == null) {
            return 1;
        }

        int otherStrength = anotherSignal.getSortOrder();
        int strength = getSignalStrength().getSortOrder();
        return Integer.valueOf(strength).compareTo(otherStrength);
    }
}
