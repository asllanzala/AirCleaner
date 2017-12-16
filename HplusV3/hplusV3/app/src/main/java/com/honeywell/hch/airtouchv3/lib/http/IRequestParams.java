package com.honeywell.hch.airtouchv3.lib.http;

import com.google.gson.Gson;

/**
 * Created by nan.liu on 1/20/15.
 */
public interface IRequestParams {

    /** Return a json string for this request */
    public String getRequest(Gson gson);

    /** Return a json string that can be logged out
     * (No passwords or some other sensitive information!) */
    public String getPrintableRequest(Gson gson);
}
