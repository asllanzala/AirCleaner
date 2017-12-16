package com.honeywell.hch.airtouchv3.framework.webservice;


/**
 * Created by nan.liu on 1/21/15.
 */
public class StatusCode {

    public static final int OK = 200;
    public static final int CREATE_OK = 201;
    public static final int SMS_OK = 204;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int EXCEPTION = 601;
    public static final int NETWORK_TIMEOUT = 999;
    public static final int NETWORK_ERROR = 1000;

    /**
     * error code when response code is 200 but no response data
     */
    public static final int NO_RESPONSE_DATA = 1001;


    /**
     * error code when return response is null
     */
    public static final int RETURN_RESPONSE_NULL = 1002;

    /**
     * error code when register error
     */
    public static final int NO_RIGSTER_ERROR = 1003;
}

