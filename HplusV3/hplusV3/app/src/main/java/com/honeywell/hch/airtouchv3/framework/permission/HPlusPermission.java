package com.honeywell.hch.airtouchv3.framework.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class HPlusPermission implements  Permission  {

    private PermissionListener mPermissionListener;

    public HPlusPermission(PermissionListener permissionListener){
        this.mPermissionListener=permissionListener;
    }

    public HPlusPermission(){
    }

    private int checkPermission(Activity thisActivity ,String permission){
        int permissionCheck = ContextCompat.checkSelfPermission(thisActivity,
                permission);
        return permissionCheck;
    }

    // use it where activity context is not available i.e. location service provider
    public boolean  checkAppPermission(Context context, String permission){
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                permission);
        return (permissionCheck==PackageManager.PERMISSION_GRANTED);
    }


    public void requestLocationPermission(Activity permissionActivity){
        int finePermission =checkPermission(permissionActivity, LOCATION_SERVICE_FINE);
        int corsePermission =checkPermission(permissionActivity, LOCATION_SERVICE_CORSE);
        boolean isPermissionDialogAlreadyShown=ActivityCompat.shouldShowRequestPermissionRationale(permissionActivity, LOCATION_SERVICE_FINE);
        boolean isPermissionDialogAlreadyShown2=ActivityCompat.shouldShowRequestPermissionRationale(permissionActivity,LOCATION_SERVICE_CORSE);

        if(finePermission==PackageManager.PERMISSION_GRANTED && corsePermission == PackageManager.PERMISSION_GRANTED){

                mPermissionListener.onPermissionGranted(PermissionCodes.LOCATION_SERVICE_REQUEST_CODE);
        }
        else {
               mPermissionListener.onPermissionNotGranted(new String[]{LOCATION_SERVICE_FINE,LOCATION_SERVICE_CORSE}, PermissionCodes.LOCATION_SERVICE_REQUEST_CODE);

        }

         if((finePermission == PackageManager.PERMISSION_DENIED && !isPermissionDialogAlreadyShown) ||
                 (corsePermission == PackageManager.PERMISSION_DENIED && !isPermissionDialogAlreadyShown2)){
            mPermissionListener.onPermissionDenied(PermissionCodes.LOCATION_SERVICE_REQUEST_CODE);
        }

    }
    public void requestBluetoothPermission(Activity permissionActivity) {
        int permission = checkPermission(permissionActivity, BLUETOOTH);
        boolean isPermissionDialogAlreadyShown=ActivityCompat.shouldShowRequestPermissionRationale(permissionActivity, BLUETOOTH);
        if (PackageManager.PERMISSION_GRANTED == permission) {
            mPermissionListener.onPermissionGranted(PermissionCodes.BLUETOOTH_REQUEST_CODE);

        }
        else{
            mPermissionListener.onPermissionNotGranted(new String[]{BLUETOOTH}, PermissionCodes.BLUETOOTH_REQUEST_CODE);
        }

        if(permission==PackageManager.PERMISSION_DENIED && !isPermissionDialogAlreadyShown){
            mPermissionListener.onPermissionDenied(PermissionCodes.BLUETOOTH_REQUEST_CODE);
        }
    }

    public void requestCameraPermission(Activity permissionActivity){
        int permission =checkPermission(permissionActivity, CAMERA);
        // we are checking it here because second condition is asynchronous call
        boolean isPermissionDialogAlreadyShown=ActivityCompat.shouldShowRequestPermissionRationale(permissionActivity, CAMERA);

        if(PackageManager.PERMISSION_GRANTED==permission){
            mPermissionListener.onPermissionGranted(PermissionCodes.CAMERA_REQUEST_CODE);
        }
        else  {
            // asynchronous call
            mPermissionListener.onPermissionNotGranted(new String[]{CAMERA}, PermissionCodes.CAMERA_REQUEST_CODE);
                // create snack bar to show interactive toast UI
            }
        if(permission==PackageManager.PERMISSION_DENIED && !isPermissionDialogAlreadyShown){
            mPermissionListener.onPermissionDenied(PermissionCodes.CAMERA_REQUEST_CODE);
        }
        }

    public void requestCallPhonePermission(Activity permissionActivity) {
        int callPhonePermission = checkPermission(permissionActivity, CALL_PHONE);
        boolean isPermissionDialogAlreadyShown=ActivityCompat.shouldShowRequestPermissionRationale(permissionActivity, CALL_PHONE);

        if(callPhonePermission == PackageManager.PERMISSION_GRANTED) {
            mPermissionListener.onPermissionGranted(PermissionCodes.CALL_PHONE_REQUEST_CODE);
        }
        else {
            mPermissionListener.onPermissionNotGranted(new String[]{CALL_PHONE}, PermissionCodes.CALL_PHONE_REQUEST_CODE);
        }
        if((callPhonePermission == PackageManager.PERMISSION_DENIED && !isPermissionDialogAlreadyShown)) {
            mPermissionListener.onPermissionDenied(PermissionCodes.CALL_PHONE_REQUEST_CODE);
        }
    }

    public void requestStoragePermission(Activity permissionActivity){
        int writeStoragePermission = checkPermission(permissionActivity, WRITE_STORAGE);
        int readStoragePermission = checkPermission(permissionActivity, READ_STORAGE);

        if(writeStoragePermission == PackageManager.PERMISSION_GRANTED && readStoragePermission == PackageManager.PERMISSION_GRANTED){

            mPermissionListener.onPermissionGranted(PermissionCodes.STORAGE_REQUEST_CODE);
        } else {
            mPermissionListener.onPermissionNotGranted(new String[]{WRITE_STORAGE, READ_STORAGE}, PermissionCodes.STORAGE_REQUEST_CODE);

        }
    }

    /**
     * if it has not  location and storage permission,should request request two permission  at the same time
     * @param permissionActivity
     */
    public void requestLocationAndStoragePermission(Activity permissionActivity){
        boolean storagePermission = isHasPermissionGranted(permissionActivity, new String[]{WRITE_STORAGE, READ_STORAGE});
        boolean locationPermission = isHasPermissionGranted(permissionActivity, new String[]{LOCATION_SERVICE_FINE, LOCATION_SERVICE_CORSE});

       if (!storagePermission && !locationPermission) {
           mPermissionListener.onPermissionNotGranted(new String[]{WRITE_STORAGE, READ_STORAGE, LOCATION_SERVICE_FINE, LOCATION_SERVICE_CORSE}, PermissionCodes.STORAGE_AND_LOCATION_CODE);
       }
       else  if (storagePermission && !locationPermission) {
           mPermissionListener.onPermissionNotGranted(new String[]{LOCATION_SERVICE_FINE,LOCATION_SERVICE_CORSE}, PermissionCodes.LOCATION_SERVICE_REQUEST_CODE);
           mPermissionListener.onPermissionGranted(PermissionCodes.STORAGE_REQUEST_CODE);
       }
       else if (!storagePermission && locationPermission){
           mPermissionListener.onPermissionNotGranted(new String[]{WRITE_STORAGE,READ_STORAGE}, PermissionCodes.STORAGE_REQUEST_CODE);
           mPermissionListener.onPermissionGranted(PermissionCodes.STORAGE_REQUEST_CODE);
       }
       else {
            mPermissionListener.onPermissionGranted(PermissionCodes.STORAGE_AND_LOCATION_CODE);
        }

    }

    // use this when you want specific events in your fragment
    // rather than generic fragment
    public void setHPlusPermissionListener(PermissionListener permissionListener){
        this.mPermissionListener=permissionListener;

    }

    public  boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean verifyTwoPermissionFromMulti(String[] permissions,int[] grantResults,String permissionOne,String permissionTwo){
        if (permissions == null || permissions.length == 0){
            return false;
        }
        int locationFine = PackageManager.PERMISSION_DENIED;
        int locationCorse = PackageManager.PERMISSION_DENIED;
        for (int i = 0; i < permissions.length; i++){
            if (permissionOne.equals(permissions[i])){
                locationFine = grantResults[i];
            }
            if (permissionTwo.equals(permissions[i])){
                locationCorse = grantResults[i];
            }
        }

        return verifyPermissions(new int[]{locationFine,locationCorse});
    }


    /**
     * if the permission is not granted,it regarded as permission deny
     * @param permissionActivity
     * @return
     */
    public boolean isLocationPermissionGranted(Activity permissionActivity){
        int finePermission =checkPermission(permissionActivity, LOCATION_SERVICE_FINE);
        int corsePermission =checkPermission(permissionActivity, LOCATION_SERVICE_CORSE);
        if(finePermission == PackageManager.PERMISSION_GRANTED &&
                corsePermission == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }


    public boolean isHasPermissionGranted(Activity permissionActivity,String[] permissionList){

        boolean result = true;
        if (permissionList == null || permissionList.length == 0){
            return result;
        }

        for (String permission : permissionList){
            result = result && (checkPermission(permissionActivity, permission) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }



    public boolean isCallPhonePermissionGranted(Activity permissionActivity) {
        int callPhonePermission = checkPermission(permissionActivity, CALL_PHONE);
        if (callPhonePermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    public void checkAndRequestPermission(int permissionCodes,Activity permissionActivity) {
        switch (permissionCodes) {
            case Permission.PermissionCodes.LOCATION_SERVICE_REQUEST_CODE:
                 requestLocationPermission(permissionActivity);
                break;

            case Permission.PermissionCodes.BLUETOOTH_REQUEST_CODE:
                 requestBluetoothPermission(permissionActivity);
                break;

            case Permission.PermissionCodes.CAMERA_REQUEST_CODE:
                requestCameraPermission(permissionActivity);
                 break;

            case PermissionCodes.CALL_PHONE_REQUEST_CODE:
                requestCallPhonePermission(permissionActivity);
                break;

            case PermissionCodes.STORAGE_AND_LOCATION_CODE:
                requestLocationAndStoragePermission(permissionActivity);
                break;

        }

    }

    public boolean checkPhoneCallPermission(Activity permissionActivity) {
        boolean currentPhoneCallPermission = isCallPhonePermissionGranted(permissionActivity);
        if (currentPhoneCallPermission) {
            return true;
        }
        return false;
    }

    public  boolean isGPSOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps) {
            return true;
        }

        return false;
    }

    public void fourceOpenGPS(Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
}
