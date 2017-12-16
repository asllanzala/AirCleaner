package com.honeywell.hch.airtouchv3.framework.model;

import android.content.Context;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.model.modelinterface.IControllable;
import com.honeywell.hch.airtouchv3.framework.model.modelinterface.IFilterable;
import com.honeywell.hch.airtouchv3.framework.model.modelinterface.IRefreshEnd;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.TccClient;
import com.honeywell.hch.airtouchv3.framework.webservice.task.GetDeviceFilterInfoTask;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyuan on 7/30/15.
 * the  class for the series of Air Touch, like AirTouch S,
 * or AirPrim.not include AirTouch 450
 * by Stephen(H127856)
 * data model reconstruction
 */
public class AirTouchSeriesDevice extends HomeDevice implements IFilterable, IControllable {
    private static final String TAG = "AirTouchSeriesDevice";

    public final static String SPEED_1 = "Speed_1";
    public final static String SPEED_2 = "Speed_2";
    public final static String SPEED_3 = "Speed_3";
    public final static String SPEED_4 = "Speed_4";
    public final static String SPEED_5 = "Speed_5";
    public final static String SPEED_6 = "Speed_6";
    public final static String SPEED_7 = "Speed_7";
    public final static String SPEED_8 = "Speed_8";
    public final static String SPEED_9 = "Speed_9";
    public final static String MODE_AUTO = "Auto";
    public final static String MODE_SLEEP = "Sleep";
    public final static String MODE_QUICK = "QuickClean";
    public final static String MODE_SILENT = "Silent";
    public final static String MODE_OFF = "Off";

    private static final int AIRTOUCH_S_CONTROL_POINT_TOTAL = 7;

    private static final int AIRTOUCH_P_CONTROL_POINT_TOTAL = 9;

    private static final int AIRTOUCH_CONTROL_LEVEL_POINT = 2;

    private static final int AIRTOUCH_S_FILTER_NUMBER = 3;

    private static final int AIRTOUCH_P_FILTER_NUMBER = 1;

    private static final int TOTAL_PROGRESS = 100;

    private RunStatus mDeviceRunStatus;

    private List<Filter> filterList = new ArrayList<Filter>();

    IReceiveResponse mReceiveResponse = new IReceiveResponse() {
        @Override
        public void onReceive(HTTPRequestResponse httpRequestResponse) {
            switch (httpRequestResponse.getRequestID()) {
                case GET_DEVICE_STATUS:
                    if (httpRequestResponse.getStatusCode() == StatusCode.OK) {
                        if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                            mDeviceRunStatus = new Gson().fromJson(httpRequestResponse.getData(),
                                    RunStatus.class);
                        }
                    } else {
                        LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "GET_DEVICE_STATUS ERROR device id = " + mDeviceInfo
                                .getDeviceID() +
                                ", response status code = " + httpRequestResponse.getStatusCode() + " , " +
                                "error msg = " + httpRequestResponse.getException());
                    }
                    notifyRefreshEnd.notifyDataRefreshEnd(RequestID.GET_DEVICE_STATUS);
                    break;
            }
        }
    };



    /**
     * get Device runstatus
     *
     * @return
     */
    public RunStatus getDeviceRunStatus() {
        return mDeviceRunStatus;
    }

    /**
     * set device runstatus
     *
     * @param mDeviceRunStatus
     */
    public void setDeviceRunStatus(RunStatus mDeviceRunStatus) {
        this.mDeviceRunStatus = mDeviceRunStatus;
    }


    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    /**
     * refresh data,get run status of device from server
     *
     * @param iRefreshEnd
     */
    @Override
    public void loadData(IRefreshEnd iRefreshEnd) {
        notifyRefreshEnd = iRefreshEnd;
        String sessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        TccClient.sharedInstance().getDeviceStatus(mDeviceInfo.getDeviceID(), sessionId,
                mReceiveResponse);
    }

    /**
     * get the device control info.used for compatible airtouch s and airtouch p. used in UI
     *
     * @return
     */
    @Override
    public ControlPoint getDeviceControlInfo() {
        ControlPoint controlPoint = new ControlPoint();
        controlPoint.setPointNumberOfEveryLevel(AIRTOUCH_CONTROL_LEVEL_POINT);

        if (AppManager.shareInstance().isAirtouch450(getDeviceInfo().getmDeviceType()) ||
                AppManager.shareInstance().isAirtouchs(getDeviceInfo().getmDeviceType())) {
            controlPoint.setTotalPointNumber(AIRTOUCH_S_CONTROL_POINT_TOTAL);
        } else if (AppManager.shareInstance().isAirtouchP(getDeviceInfo().getmDeviceType())) {
            controlPoint.setTotalPointNumber(AIRTOUCH_P_CONTROL_POINT_TOTAL);
        }
        return controlPoint;
    }


    /**
     * get Filter list ,used for compatible airtouch s and airtouch p. used in UI
     *
     * @return
     */
    @Override
    public void getFilterInfo(IActivityReceive getDeviceCapabilityReceive) {
        String sessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        GetDeviceFilterInfoTask getDeviceFilterInfoTask = new GetDeviceFilterInfoTask(sessionId, mDeviceInfo.getDeviceID(), mDeviceRunStatus, getDeviceCapabilityReceive);
        AsyncTaskExecutorUtil.executeAsyncTask(getDeviceFilterInfoTask);

    }

    private void addFilterToList(int nameResourceId, int desResoucceId, int progress) {
        Filter filterone = new Filter();
        filterone.setNameResourceId(nameResourceId);
        filterone.setDesResourceId(desResoucceId);
        filterone.setUsagePrecent(progress);
        filterList.add(filterone);
    }

    public String getDeviceModeOrSpeed(Context context) {
        if (getDeviceRunStatus() == null)
            return context.getString(R.string.offline);

        String mode = getDeviceRunStatus().getScenarioMode();
        String modeOrSpeed;

        if (!getDeviceInfo().getIsAlive()) {
            modeOrSpeed = context.getString(R.string.offline);
        } else if (mode.equals(MODE_AUTO)) {
            modeOrSpeed = context.getString(R.string.control_auto);
        } else if (mode.equals(MODE_SLEEP)) {
            modeOrSpeed = context.getString(R.string.control_sleep);
        } else if (mode.equals(MODE_QUICK)) {
            modeOrSpeed = context.getString(R.string.control_quick);
        } else if (mode.equals(MODE_SILENT)) {
            modeOrSpeed = context.getString(R.string.control_silent);
        } else if (mode.equals(MODE_OFF)) {
            modeOrSpeed = context.getString(R.string.off);
        } else {
            modeOrSpeed = getDeviceRunStatus().getFanSpeedStatus();
            // manual mode
            switch (modeOrSpeed) {
                case SPEED_1:
                case SPEED_2:
                    // set AirTouchS low speed
                    if (!AppManager.shareInstance().isAirtouchP(getDeviceType())) {
                        modeOrSpeed = context.getString(R.string.speed_low);
                        break;
                    }
                case SPEED_3:
                    // set AirTouchP low speed
                    if (AppManager.shareInstance().isAirtouchP(getDeviceType())) {
                        modeOrSpeed = context.getString(R.string.speed_low);
                        break;
                    }
                case SPEED_4:
                case SPEED_5:
                    // set AirTouchS medium speed
                    if (!AppManager.shareInstance().isAirtouchP(getDeviceType())) {
                        modeOrSpeed = context.getString(R.string.speed_medium);
                        break;
                    }
                case SPEED_6:
                    // set AirTouchP medium speed
                    if (AppManager.shareInstance().isAirtouchP(getDeviceType())) {
                        modeOrSpeed = context.getString(R.string.speed_medium);
                        break;
                    }
                case SPEED_7:
                    // set AirTouchS high speed
                    if (!AppManager.shareInstance().isAirtouchP(getDeviceType())) {
                        modeOrSpeed = context.getString(R.string.speed_high);
                        break;
                    }
                case SPEED_8:
                case SPEED_9:
                    if (AppManager.shareInstance().isAirtouchP(getDeviceType())) {
                        modeOrSpeed = context.getString(R.string.speed_high);
                        break;
                    }
                    break;
                default:
                    modeOrSpeed = context.getString(R.string.offline);
                    break;
            }
        }

        return modeOrSpeed;
    }

}
