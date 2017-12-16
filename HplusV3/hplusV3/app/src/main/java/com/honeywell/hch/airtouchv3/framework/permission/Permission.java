package com.honeywell.hch.airtouchv3.framework.permission;

import android.Manifest;
import android.app.Activity;


public interface Permission {

    public static final String BLUETOOTH_ADMIN = Manifest.permission.BLUETOOTH_ADMIN;
    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String LOCATION_SERVICE_FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String LOCATION_SERVICE_CORSE = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String BLUETOOTH = Manifest.permission.BLUETOOTH;
    public static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;

    interface PermissionCodes {

        public static final int BLUETOOTH_ADMIN_REQUEST_CODE = 1;
        public static final int CAMERA_REQUEST_CODE = 2;
        public static final int LOCATION_SERVICE_REQUEST_CODE = 3;
        public static final int BLUETOOTH_REQUEST_CODE = 4;
        public final static int STORAGE_REQUEST_CODE = 5;
        public final static int CALL_PHONE_REQUEST_CODE = 6;
        public final static int STORAGE_AND_LOCATION_CODE = 7;

    }


    public void requestBluetoothPermission(Activity permissionActivity);

    public void requestLocationPermission(Activity permissionActivity);

    public void requestCallPhonePermission(Activity permissionActivity);

}
