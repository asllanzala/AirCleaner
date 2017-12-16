package com.honeywell.hch.airtouchv2.framework.webservice.task;

import android.os.Bundle;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;

/**
 * Created by Jin Qian on 15/7/2.
 */
public class EmotionalBottleTask extends BaseRequestTask {
    private int mLocationId;
    private int mPeriodType;
    private String mSessionId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public EmotionalBottleTask(int locationId, int periodType, String sessionId, IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mLocationId = locationId;
        this.mPeriodType = periodType;
        this.mSessionId = sessionId;
        this.mIReceiveResponse = iReceiveResponse;
        this.mRequestParams = requestParams;
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reloginResult = reloginSuccessOrNot();
        if (reloginResult.isResult())
        {
            ResponseResult result = HttpClientRequest.sharedInstance()
                    .emotionBottle(mLocationId, mPeriodType, AuthorizeApp.shareInstance().getSessionId(), null, mIReceiveResponse);
            Bundle response = result.getResponseData();
            if (response != null)
            {
                float pmvalue = response.getFloat("clean_dust");
                float pahsValue = response.getFloat("PAHs");
                float leadValue = pmvalue * 0.015f * 0.35f;
                //cigeratte value
                double cigerate = Math.round(pmvalue * 1000 * 0.0004/60);

                float carfumeScond = Math.round((leadValue /7.78f) * 1000);

                Bundle bundle2 =  new Bundle();
                bundle2.putFloat("pm25_value",pmvalue);
                bundle2.putFloat("lead_value",leadValue);
                bundle2.putDouble("cigerate_value", cigerate);
                bundle2.putFloat("PAHs", pahsValue);
                bundle2.putFloat("fume_second",carfumeScond);
                result.setResponseData(bundle2);
            }
            return result;
        }
        return  new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.EMOTION_BOTTLE);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
