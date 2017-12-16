package com.honeywell.hch.airtouchv3.framework.webservice.task;

import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.framework.webservice.ThinkPageClient;
import com.honeywell.hch.airtouchv3.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv3.lib.http.IRequestParams;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;

import java.util.List;

/**
 * Created by Jin Qian on 15/8/24.
 * Note that after addLocation successfully, getLocation and save the updates.
 */
public class AddLocationTask extends BaseRequestTask {
    private String mUserId;
    private String mSessionId;
    private IActivityReceive mIReceiveResponse;
    private IRequestParams mRequestParams;

    public AddLocationTask(IRequestParams requestParams, IActivityReceive iReceiveResponse) {
        this.mRequestParams = requestParams;
        this.mIReceiveResponse = iReceiveResponse;

        mUserId = AppManager.shareInstance().getAuthorizeApp().getUserID();
        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
    }

    @Override
    protected ResponseResult doInBackground(Object... params) {

        ResponseResult reLoginResult = reloginSuccessOrNot();
        if (reLoginResult.isResult()) {
            ResponseResult addResult = HttpProxy.getInstance().getWebService()
                    .addLocation(AppManager.shareInstance().getAuthorizeApp().getUserID(),
                            AppManager.shareInstance().getAuthorizeApp().getSessionId(),
                            mRequestParams, mIReceiveResponse);


            if (addResult!= null && addResult.isResult()) {
                HttpProxy.getInstance().getWebService().getLocation(mUserId, mSessionId, null, mIReceiveResponse);
                reloadDeviceInfo();

//                int localId = addResult.getResponseData().getInt(AirTouchConstants.LOCATION_ID_BUNDLE_KEY);
//                getWeatherOftheLocation(localId);

            }

            return addResult;
        }
        return new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", RequestID.DELETE_DEVICE);

    }

    @Override
    protected void onPostExecute(ResponseResult responseResult) {

        if (mIReceiveResponse != null) {
            mIReceiveResponse.onReceive(responseResult);
        }
        super.onPostExecute(responseResult);
    }

    private void getWeatherOftheLocation(int  localId) {
        List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
        if (userLocationDataList != null && userLocationDataList.size() > 0){
            for (UserLocationData userLocationData : userLocationDataList){
                if (userLocationData.getLocationID() == localId){
//                    CityChinaDBService mCityDBService = new CityChinaDBService(ATApplication.getInstance().getApplicationContext());
//                    City city = mCityDBService.getCityByCode(userLocationData.getCity());

                    ResponseResult responseResult = ThinkPageClient.sharedInstance()
                            .getWeatherDataNew(userLocationData.getCity(), AppConfig
                                    .getLanguageXinzhi(), 'c', RequestID.ALL_DATA);
                    if (responseResult != null && responseResult.isResult()){
                        WeatherPageData weatherData = (WeatherPageData)responseResult
                                .getResponseData().getSerializable(AirTouchConstants.WEATHER_DATA_KEY);
                        userLocationData.setCityWeatherData(weatherData);
                    }
                }
            }
        }


    }
}
