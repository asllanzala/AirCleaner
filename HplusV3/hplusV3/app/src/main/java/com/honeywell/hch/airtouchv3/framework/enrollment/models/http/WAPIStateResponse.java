package com.honeywell.hch.airtouchv3.framework.enrollment.models.http;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;

public class WAPIStateResponse implements IRequestResponse, Serializable {

    private static final long serialVersionUID = 7719420449834978084L;

    @SerializedName("MAC")
    private String mMac;

    @SerializedName("DIYState")
    private String mWAPIState;

    @SerializedName("ErrorNotification")
    private String mErrorNotification;

    public String getMac() {
        return mMac;
    }

    public WAPIState getWAPIState() {
        return WAPIState.fromServerValue(mWAPIState);
    }

    public WAPIErrorNotification getErrorNotification() {
        return WAPIErrorNotification.fromServerValue(mErrorNotification);
    }
    public void setErrorNotification(String errorNotification) {
        mErrorNotification = errorNotification;
    }

    public static enum WAPIState {

        DIY_STATE_T1A_DOWNLOAD_LYRIC_APP("DIY_STATE_T1A_DOWNLOAD_LYRIC_APP"),
        DIY_STATE_T2_BROADCASTING_STA("DIY_STATE_T2_BROADCASTING_STA"),
        DIY_STATE_T3_CONNECTED_TO_SMARTPHONE("DIY_STATE_T3_CONNECTED_TO_SMARTPHONE"),
        DIY_STATE_T3_ISU_CHANGE_SESSION("DIY_STATE_T3_ISU_CHANGE_SESSION"),
        DIY_STATE_T4_TSTAT_EQUIPMENT_SETUP_COMPLETE("DIY_STATE_T4_TSTAT_EQUIPMENT_SETUP_COMPLETE"),
        DIY_STATE_INVALID("DIY_STATE_INVALID");

        private String mServerValue;

        private WAPIState(String serverValue) {
            mServerValue = serverValue;
        }

        public String getServerValue() {
            return mServerValue;
        }

        public static WAPIState fromServerValue(String serverValue) {
            for(WAPIState wapiState : WAPIState.values()) {
                if (wapiState.getServerValue().equals(serverValue)) {
                    return wapiState;
                }
            }

            return null;
        }
    }

    public static enum WAPIErrorNotification {

        NO_ERROR("NO_ERROR"),
        RM_RESET("RM_RESET"),
        TSTAT_RESET("TSTAT_RESET"),
        LOW_BATTERY("LOW_BATTERY"),
        FACTORY_RESET("FACTORY_RESET"),
        UNKNOWN_ERROR("UNKNOWN_ERROR");

        private String mServerValue;

        private WAPIErrorNotification(String serverValue) {
            mServerValue = serverValue;
        }

        public String getServerValue() {
            return mServerValue;
        }

        public static WAPIErrorNotification fromServerValue(String serverValue) {
            for(WAPIErrorNotification wapiState : WAPIErrorNotification.values()) {
                if (wapiState.getServerValue().equals(serverValue)) {
                    return wapiState;
                }
            }

            return null;
        }
    }
}
