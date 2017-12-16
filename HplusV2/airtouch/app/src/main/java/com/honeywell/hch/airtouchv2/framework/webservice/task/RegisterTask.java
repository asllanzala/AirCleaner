package com.honeywell.hch.airtouchv2.framework.webservice.task;

import android.util.Log;

import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;

/**
 * Created by wuyuan on 15/5/19.
 */
public class RegisterTask extends BaseRequestTask
{
    private IActivityReceive mIReceiveResponse;
    private IRequestParams requestParams;

    public RegisterTask(IActivityReceive iReceiveResponse,IRequestParams requestParams)
    {
        this.mIReceiveResponse = iReceiveResponse;
        this.requestParams = requestParams;
    }
    @Override
    protected ResponseResult doInBackground(Object... params)
    {

        Log.e("haha","==RegisterTask doInBackground==");
       ResponseResult registerResult = HttpClientRequest.sharedInstance().userRegister((UserRegisterRequest)requestParams,mIReceiveResponse);
        if (registerResult.isResult())
        {
            UserLoginRequest userLoginRequest = new UserLoginRequest(((UserRegisterRequest)requestParams).getTelephone(), ((UserRegisterRequest)requestParams).getPassword(),
                    AppConfig.APPLICATION_ID);
            return HttpClientRequest.sharedInstance().userLogin(userLoginRequest,mIReceiveResponse);
        }
        return registerResult;
    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null)
        {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }
}
