package com.honeywell.hch.airtouchv3.framework.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liunan on 1/15/15.
 */
public class BaseConfig {

    protected String mFileName = "air_touch_config";
    protected Context mContext;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private SharedPreferences mSharedPreferences;

    public BaseConfig(Context context) {
        super();
        this.mContext = context;
    }

    public SharedPreferences.Editor getSharedPreferencesEditor() {
        if (null == mSharedPreferencesEditor) {
            mSharedPreferencesEditor = getSharedPreferences().edit();
        }
        return mSharedPreferencesEditor;
    }

    public SharedPreferences getSharedPreferences() {
        if (null == mSharedPreferencesEditor) {
            mSharedPreferences = mContext.getSharedPreferences(mFileName,
                    Activity.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }
}
