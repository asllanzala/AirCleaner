package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.request.UserRegisterRequest;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;

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

       ResponseResult registerResult = HttpProxy.getInstance().getWebService().userRegister((UserRegisterRequest)requestParams,mIReceiveResponse);
        if (registerResult.isResult())
        {
            UserLoginRequest userLoginRequest = new UserLoginRequest(((UserRegisterRequest)requestParams).getTelephone(), ((UserRegisterRequest)requestParams).getPassword(),
                    AppConfig.APPLICATION_ID);
            return HttpProxy.getInstance().getWebService().userLogin(userLoginRequest,mIReceiveResponse);
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
