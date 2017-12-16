package com.honeywell.hch.airtouchv3.framework.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Vincent on 20/10/15.
 */
public class DefaultDeviceDBService extends DBService {

    //table info
    public static final String TABLE_NAME = "defaultDevice";
    public static final String LOCATION_ID = "locationID";
    public static final String DEVICE_ID = "deviceId";
    public static String[] DBKey = {LOCATION_ID, DEVICE_ID};


    public DefaultDeviceDBService(Context context) {
        super(context);
    }


    public void insertDefaultDevice(int locationId,int deviceId) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getWritableDatabase();
        Integer[] value = new Integer[]{locationId,deviceId};
        insertOrUpdate(TABLE_NAME, DBKey, value);
    }

    public int findDefaultByLocationID(int locationID) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        sqLiteDatabase.beginTransaction();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                + " FROM " + TABLE_NAME
                + " WHERE " + LOCATION_ID + " = '" + locationID + "'", null);
        int deviceId = 0;
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            deviceId = cursor.getInt(cursor.getColumnIndex(DEVICE_ID));
        }
        cursor.close();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
        return deviceId;
    }
    public void deleteDefaultDevice(int deviceId) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE"
                + " FROM " + TABLE_NAME
                + " WHERE " + DEVICE_ID + " = '" + deviceId + "';", new Object[]{});
        sqLiteDatabase.close();
    }
}
