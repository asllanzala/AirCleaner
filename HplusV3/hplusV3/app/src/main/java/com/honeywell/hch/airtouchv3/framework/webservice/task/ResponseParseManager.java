package com.honeywell.hch.airtouchv3.framework.webservice.task;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.CapabilityResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.control.CommTaskResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.EmotionBottleResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.GatewayAliveResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.RecordCreatedResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.user.response.UserLoginResponse;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.app.dashboard.model.CreateGroupResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupData;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupDataResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.GroupResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.MultiCommTaskListResponse;
import com.honeywell.hch.airtouchv3.app.dashboard.model.ScenarioGroupResponse;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.RunStatus;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Hour;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HourlyFuture;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HourlyHistory;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Weather;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.WeatherData;
import com.honeywell.hch.airtouchv3.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv3.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv3.lib.http.RequestID;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuyuan on 15/5/20.
 */
public class ResponseParseManager {
    private final static String TAG = "AirTouchResponseParse";

    /**
     * parse Response of Check Mac on Tcc
     *
     * @param response http response
     * @return
     */
    public static ResponseResult parseCheckMacResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", response.getRequestID());

        if (!StringUtil.isEmpty(response.getData())) {
            GatewayAliveResponse gatewayAliveResponse = new Gson().fromJson(response.getData(),
                    GatewayAliveResponse.class);
            LogUtil.log(LogUtil.LogLevel.INFO, TAG, "CHECK_MAC:" + gatewayAliveResponse.isAlive());
            if (gatewayAliveResponse.isAlive()) {
                result.setFlag(AirTouchConstants.CHECK_MAC_ALIVE);
            } else {
                result.setFlag(AirTouchConstants.CHECK_MAC_AGAIN);
            }
        } else {
            result.setFlag(AirTouchConstants.CHECK_MAC_AGAIN);
        }

        return result;
    }


    /**
     * parse User login Response
     *
     * @param response  http response
     * @param requestID requestID request id
     * @return
     */
    public static ResponseResult parseUserLoginResponse(HTTPRequestResponse response, RequestID requestID) {

        AuthorizeApp authorizeApp = AppManager.shareInstance().getAuthorizeApp();

        if (response != null && response.getStatusCode() == StatusCode.OK) {
            ResponseResult result = new ResponseResult(true, StatusCode.OK, "", response.getRequestID());
            if (!StringUtil.isEmpty(response.getData())) {
                UserLoginResponse userLoginResponse = new Gson().fromJson(response.getData(), UserLoginResponse.class);

                if (userLoginResponse.getUserInfo() != null) {
                    // Save user data to sharedPreference
                    String nickname = userLoginResponse.getUserInfo().getFirstName();
                    String countryCode = userLoginResponse.getUserInfo().getCountryPhoneNum();
                    authorizeApp.dealResultAfterRelogin(userLoginResponse.getUserInfo().getUserID(),
                            userLoginResponse.getSessionId(), nickname, true, countryCode);

                    return result;
                }
            } else {
                result.setResult(false);
                authorizeApp.dealResultAfterRelogin("", "", "", false, "");
                result.setResponseCode(StatusCode.NO_RESPONSE_DATA);
                return result;
            }
        }
        authorizeApp.dealResultAfterRelogin("", "","", false, "");
        return getErrorResponse(response, requestID);
    }

    /**
     * get bottle response
     *
     * @param response
     * @return
     */
    public static ResponseResult parseBottleResponse(HTTPRequestResponse response, RequestID requestID) {
        if (response != null && response.getStatusCode() == StatusCode.OK) {
            ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

            if (!StringUtil.isEmpty(response.getData())) {
                EmotionBottleResponse emotionBottleResponse = new Gson().fromJson(response.getData(), EmotionBottleResponse.class);
                Bundle bundle = new Bundle();
                bundle.putFloat("clean_dust", emotionBottleResponse.getCleanDust());
                bundle.putFloat("heavy_metal", emotionBottleResponse.getHeavyMetal());
                bundle.putFloat("PAHs", emotionBottleResponse.getPahs());
                result.setResponseData(bundle);
                return result;
            }
        }
        return getErrorResponse(response, requestID);
    }

    public static ResponseResult parseGetGroupByIdResponse(HTTPRequestResponse response, RequestID requestID) {
        if (response != null && response.getStatusCode() == StatusCode.OK) {
            ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);
            try {
                Bundle bundle = new Bundle();
                JSONArray responseArray = new JSONArray(response.getData());
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject responseJSON = responseArray.getJSONObject(i);
                    GroupDataResponse groupList = new Gson().fromJson(responseJSON.toString(),
                            GroupDataResponse.class);
                    bundle.putSerializable(GroupDataResponse.GROUP_LIST + i, groupList);
                }
                result.setResponseData(bundle);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
        return getErrorResponse(response, requestID);
    }

    public static ResponseResult parseGetGroupByLocationIdResponse(HTTPRequestResponse response, RequestID requestID) {
        if (response != null && response.getStatusCode() == StatusCode.OK) {
            ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

            if (!StringUtil.isEmpty(response.getData())) {
                GroupData groupData = new Gson().fromJson(response.getData(), GroupData.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GroupData.GROUP_DATA, groupData);
                result.setResponseData(bundle);
                return result;
            }
        }

        return getErrorResponse(response, requestID);
    }


    /**
     * get register response
     *
     * @param response
     * @return
     */
    public static ResponseResult getRegsterResponse(HTTPRequestResponse response) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", response.getRequestID());

        if (response != null && !StringUtil.isEmpty(response.getData()) &&
                response.getStatusCode() == StatusCode.CREATE_OK) {
            return result;
        }
        return getErrorResponse(response, response.getRequestID());
    }

    /**
     * get location response and set it to UserLocations
     *
     * @param response
     * @return
     */
    public static ResponseResult parseGetLocationResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", response.getRequestID());
        if (!StringUtil.isEmpty(response.getData())) {
            try {
                JSONArray responseArray = new JSONArray(response.getData());

                List<UserLocationData> tempList = new ArrayList<>();
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject responseJSON = responseArray.getJSONObject(i);
                    UserLocation userLocation = new Gson().fromJson(responseJSON.toString(), UserLocation.class);
                    AppManager.shareInstance().addLocationDataFromGetLocationAPI(userLocation, tempList);
                }

                AppManager.shareInstance().updateUsrdataList(tempList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        return getErrorResponse(response, requestID);
    }


    public static ResponseResult parseAddLocationResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.CREATE_OK) {
                RecordCreatedResponse recordCreatedResponse = new Gson().fromJson(response.getData(),
                        RecordCreatedResponse.class);
                Bundle bundle = new Bundle();
                bundle.putInt(AirTouchConstants.LOCATION_ID_BUNDLE_KEY, recordCreatedResponse.getId());
                result.setResponseData(bundle);

                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }

        return null;
    }

    public static ResponseResult parseAddDeviceResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.CREATE_OK) {
                RecordCreatedResponse recordCreatedResponse = new Gson().fromJson(response.getData(),
                        RecordCreatedResponse.class);
                Bundle bundle = new Bundle();
                bundle.putInt(AirTouchConstants.COMM_TASK_BUNDLE_KEY, recordCreatedResponse.getId());
                result.setResponseData(bundle);

                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }

        return null;
    }

    public static ResponseResult parseCreateGroupResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {

                CreateGroupResponse createGroupResponse = new Gson().fromJson(response.getData(),
                        CreateGroupResponse.class);
                Bundle bundle = new Bundle();
                bundle.putInt(CreateGroupResponse.GROUP_ID, createGroupResponse.getGroupId());
                bundle.putInt(CreateGroupResponse.CODE_ID, createGroupResponse.getCode());
                result.setResponseData(bundle);

                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }
        return null;
    }

    public static ResponseResult parseGroupResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {
                GroupResponse groupResponse = new Gson().fromJson(response.getData(),
                        GroupResponse.class);
                Bundle bundle = new Bundle();
                bundle.putInt(GroupResponse.CODE_ID, groupResponse.getCode());
                result.setResponseData(bundle);
                return result;
            } else if (response.getStatusCode() == 401) { // 401 - group does not exist
                Bundle bundle = new Bundle();
                bundle.putInt(GroupResponse.CODE_ID, response.getStatusCode());
                result.setResponseData(bundle);
                return result;
            }  else {
                return getErrorResponse(response, requestID);
            }
        }
        return null;
    }

    public static ResponseResult parseIsMasterResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {

                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }
        return null;
    }

    public static ResponseResult parseScenarioResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {

                ScenarioGroupResponse res = new Gson().fromJson(response.getData(),
                        ScenarioGroupResponse.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ScenarioGroupResponse.SCENARIO_DATA, res);
                result.setResponseData(bundle);

                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }
        return null;
    }

    public static ResponseResult parseMultiCommTaskResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", response.getRequestID());
        if (response.getStatusCode() == StatusCode.OK) {
            if (!StringUtil.isEmpty(response.getData())) {
                if (!StringUtil.isEmpty(response.getData())) {
                    MultiCommTaskListResponse commTaskData
                            = new Gson().fromJson(response.getData(), MultiCommTaskListResponse.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MultiCommTaskListResponse.MUTLICOMMTASK, commTaskData);
                    result.setResponseData(bundle);
                }
            }
            return result;
        } else {
            return getErrorResponse(response, requestID);
        }
    }

    public static ResponseResult parseCommTaskResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", response.getRequestID());
        if (response.getStatusCode() == StatusCode.OK) {
            if (!StringUtil.isEmpty(response.getData())) {
                CommTaskResponse commTaskResponse = new Gson().fromJson(response.getData(),
                        CommTaskResponse.class);
                if (commTaskResponse.getState().equals("Succeeded")) {
                    result.setFlag(AirTouchConstants.COMM_TASK_SUCCEED);
                } else if (commTaskResponse.getState().equals("Failed")) {
                    result.setFlag(AirTouchConstants.COMM_TASK_FAILED);
                } else {
                    result.setFlag(AirTouchConstants.COMM_TASK_RUNNING);
                }
            }
            return result;
        } else {
            return getErrorResponse(response, requestID);
        }
    }

    public static ResponseResult parseCommonResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() >= StatusCode.OK
                    && (response.getStatusCode() <= StatusCode.SMS_OK)) {
                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }
        return null;
    }


    private static ResponseResult getErrorResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(false, response.getStatusCode(), "", requestID);

        if (response == null) {
            return result;
        }

        result.setResponseCode(response.getStatusCode());

        if (response.getException() != null) {
            result.setExceptionMsg(response.getException().toString());
        }
        return result;
    }

    /**
     * get device capability and parse the response
     *
     * @param response
     * @return
     */
    public static ResponseResult parseGetDeviceCapabilityResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {
                CapabilityResponse capabilityResponse = new Gson().fromJson(response.getData(),
                        CapabilityResponse.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(AirTouchConstants.DEVICE_CAPABILITY_KEY, capabilityResponse);
                result.setResponseData(bundle);

                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }

        return null;
    }

    public static ResponseResult parseGetDeviceRunStatusResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {
                RunStatus runStatus = new Gson().fromJson(response.getData(),
                        RunStatus.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(AirTouchConstants.DEVICE_RUNSTATUS_KEY, runStatus);
                result.setResponseData(bundle);

                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }

        return null;
    }

    public static ResponseResult parseGetWeatherResponse(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {
                WeatherData weatherData = new Gson().fromJson(response.getData(),
                        WeatherData.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(AirTouchConstants.WEATHER_DATA_KEY, weatherData);
                result.setResponseData(bundle);

                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }

        return null;
    }


    public static ResponseResult parseCleanTime(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {
                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }
        return null;
    }

    public static ResponseResult parseAllWeatherResponse(HTTPRequestResponse baseWeatherData,
                                                         RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);
        HashMap<String, WeatherPageData> weatherPageDataMap = new HashMap<>();
        if (baseWeatherData != null) {
            if (baseWeatherData.getStatusCode() == StatusCode.OK) {
                WeatherData weatherData = new Gson().fromJson(baseWeatherData.getData(), WeatherData
                        .class);
                if (weatherData == null) {
                    result = getErrorResponse(baseWeatherData, requestID);
                } else if (weatherData.getWeather() != null) {
                    for (Weather weather : weatherData.getWeather()) {
                        WeatherPageData weatherPageData = new WeatherPageData();
                        weatherPageData.setWeather(weather);
                        weatherPageDataMap.put(weather.getCityID(), weatherPageData);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(AirTouchConstants.WEATHER_DATA_KEY, weatherPageDataMap);
                    result.setResponseData(bundle);
                }
            } else {
                result = getErrorResponse(baseWeatherData, requestID);
            }
        }
        return result;
    }

    public static Hour[] parseHourlyData(HourlyHistory hourlyHistory, HourlyFuture
            hourlyFuture) {
        Hour[] hours = new Hour[28];
        // TODO now data need to be added in the array
        if (hourlyHistory != null && hourlyHistory.getHours() != null && hourlyHistory
                .getHours().length > 2) {
            hours[0] = hourlyHistory.getHours()[1];
            hours[1] = hourlyHistory.getHours()[0];
        }
        if (hourlyFuture != null && hourlyFuture.getHours() != null && hourlyFuture
                .getHours().length > 2) {
            for (int i = 0; i < hourlyFuture.getHours().length; i++) {
                hours[i + 2] = hourlyFuture.getHours()[i];
            }
            hours[26] = hourlyFuture.getHours()[hourlyFuture.getHours().length - 1];
            hours[27] = hourlyFuture.getHours()[hourlyFuture.getHours().length - 1];
        }
        return hours;
    }

    public static HourlyFuture parseHourlyFutureResponse(HTTPRequestResponse hourResponse) {
        HourlyFuture hourlyFuture = null;
        if (hourResponse != null && hourResponse.getStatusCode() == StatusCode.OK) {
            hourlyFuture = new Gson().fromJson(hourResponse.getData(), HourlyFuture.class);
        }
        return hourlyFuture;
    }

    public static HourlyHistory parseHourlyHistoryResponse(HTTPRequestResponse hourResponse) {
        HourlyHistory hourlyHistory = null;
        if (hourResponse != null && hourResponse.getStatusCode() == StatusCode.OK) {
            hourlyHistory = new Gson().fromJson(hourResponse.getData(), HourlyHistory.class);
        }
        return hourlyHistory;
    }

    public static ResponseResult parseTurnOnDevie(HTTPRequestResponse response, RequestID requestID) {
        ResponseResult result = new ResponseResult(true, StatusCode.OK, "", requestID);

        if (response != null) {
            if (response.getStatusCode() == StatusCode.OK) {
                return result;
            } else {
                return getErrorResponse(response, requestID);
            }
        }
        return null;
    }

    public static ResponseResult parseGetEnrollTypeResponse(HTTPRequestResponse response, RequestID requestID) {
        Log.i("SmartLinkEnroll","response: "+response);
        if (response != null && response.getStatusCode() == StatusCode.OK) {
            if (!StringUtil.isEmpty(response.getData())) {
                ResponseResult result = new ResponseResult(true, StatusCode.OK, response.getData(), requestID);

                Log.i("SmartLinkEnroll", "response.getData()： " + response.getData());

                return result;
            }
        }
        Log.i("SmartLinkEnroll", "response == null");

        return getErrorResponse(response, requestID);
    }

}
