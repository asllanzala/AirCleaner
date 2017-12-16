package com.honeywell.hch.airtouchv3.lib.http;

/**
 * network request identifier
 * Created by liunan on 1/13/15.
 */
public enum RequestID {
    // User Location
    GET_LOCATION,
    ADD_LOCATION,
    SWAP_LOCATION,
    DELETE_LOCATION,
    ADD_DEVICE,
    GET_HOME_PM25,

    // Device Control
    CHECK_MAC,
    CONTROL_DEVICE,
    GET_DEVICE_STATUS,
    GET_DEVICE_CAPABILITY,
    COMM_TASK,
    DELETE_DEVICE,

    //Password
    UPDATE_PASSWORD,
    CHANGE_PASSWORD,

    //Clean time
    CLEAN_TIME,

    // Emotional
    EMOTION_BOTTLE,

    // Group
    CREATE_GROUP,
    DELETE_GROUP,
    ADD_DEVICE_TO_GROUP,
    DELETE_DEVICE_FROM_GROUP,
    UPDATE_GROUP_NAME,
    GET_GROUP_BY_GROUP_ID,
    GET_GROUP_BY_LOCATION_ID,
    SEND_SCENARIO_TO_GROUP,
    MULTI_COMM_TASK,
    IS_DEVICE_MASTER,

    // last RequestID need to login first before timeout
    USER_LOGOUT,

    // User Account
    USER_LOGIN,
    USER_REGISTER,
    GET_SMS_CODE,
    VERIFY_SMS_VALID,
    UPDATE_SESSION,

    // ThinkPage
    ALL_DATA,
    NOW_DATA,
    AIR_DATA,
    GET_WEATHER,
    HOURLY_FUTURE,
    HOURLY_HISTORY,

    // Enrollment
    SEND_PHONE_NAME,
    GET_KEY,
    GET_MAC_CRC,
    GET_ERROR,
    GET_ROUTER,
    CONNECT_ROUTER,
    GET_ENROLL_TYPE
}
