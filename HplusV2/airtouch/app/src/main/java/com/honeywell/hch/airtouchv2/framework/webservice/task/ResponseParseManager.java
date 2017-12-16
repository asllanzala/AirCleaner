package com.honeywell.hch.airtouchv2.framework.webservice.task;

import android.os.Bundle;

import com.google.gson.Gson;

import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.control.CommTaskResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.GatewayAliveResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.RecordCreatedResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.UserLoginRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.EmotionBottleResponse;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLoginResponse;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
     * @param response http response
     * @param request  userLogin request params
     * @return
     */
    public static ResponseResult parseUserLoginResponse(HTTPRequestResponse response, UserLoginRequest request) {
        if (response != null && response.getStatusCode() == StatusCode.OK) {
            ResponseResult result = new ResponseResult(true, StatusCode.OK, "", response.getRequestID());
            if (!StringUtil.isEmpty(response.getData())) {
                UserLoginResponse userLoginResponse = new Gson().fromJson(response.getData(), UserLoginResponse.class);

                if (userLoginResponse.getUserInfo() != null) {
                    // Save user data to sharedPreference

//                    String mUserNickname = request.getUsername();
//
//                    UserConfig userConfig = new UserConfig(ATApplication.getInstance().getApplicationContext());
//                    userConfig.saveUserInfo(mUserNickname, userLoginResponse.getUserInfo()
//                            .getTelephone(), request.getPassword(), userLoginResponse.getUserInfo
//                            ().getUserID(), userLoginResponse.getSessionId(), true);
                    AuthorizeApp.shareInstance().dealResultAfterRelogin(userLoginResponse.getUserInfo().getUserID(),
                            userLoginResponse.getSessionId(), true);

                    return result;
                }
            } else {
                result.setResult(false);
                AuthorizeApp.shareInstance().dealResultAfterRelogin("", "", false);
                result.setResponseCode(StatusCode.NO_RESPONSE_DATA);
                return result;
            }
        }
        AuthorizeApp.shareInstance().dealResultAfterRelogin("", "", false);
        return getErrorResponse(response, response.getRequestID());
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
        ArrayList<UserLocation> userLocations = new ArrayList<>();

        if (!StringUtil.isEmpty(response.getData())) {
            try {
                JSONArray responseArray = new JSONArray(response.getData());

                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject responseJSON = responseArray.getJSONObject(i);
                    UserLocation userLocation = new Gson().fromJson(responseJSON.toString(), UserLocation.class);
                    userLocations.add(userLocation);
                }
                // Cation: update user locations which are not combine with device RunStatus.
                AuthorizeApp.shareInstance().setUserLocations(userLocations);

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
        ResponseResult result = new ResponseResult(false, StatusCode.RETURN_RESPONSE_NULL, "", requestID);

        if (response == null) {
            return result;
        }

        result.setResponseCode(response.getStatusCode());

        if (response.getException() != null) {
            result.setExceptionMsg(response.getException().toString());
            result.setResponseCode(StatusCode.EXCEPTION);
        }
        return result;
    }

}
