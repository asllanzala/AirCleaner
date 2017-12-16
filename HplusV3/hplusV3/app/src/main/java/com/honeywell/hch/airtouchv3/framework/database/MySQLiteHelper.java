package com.honeywell.hch.airtouchv3.framework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nan.liu on 2/2/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int VERSION = 4;
    private static final String DB_NAME = "airTouch.db";

    public MySQLiteHelper(Context context, CursorFactory factory, int version) {
        super(context, DB_NAME, factory, version);
    }

    public MySQLiteHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createCityChinaTable(db);
        createCityIndiaTable(db);
        createUserTable(db);
        createDefaultDeviceTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                upgradeToVersionTwo(db);
            case 2:
                upgradeToVersionThree(db);
            case 3:
                upgradeToVersionFour(db);
            default:
                break;
        }
    }

    private void upgradeToVersionTwo(SQLiteDatabase db) {
        String upgradeIsEncryptedQuery = "ALTER TABLE " + UserDBService.TABLE_NAME
                + " ADD COLUMN " + UserDBService.IS_ENCRYPTED + " INTEGER DEFAULT 0";
        db.execSQL(upgradeIsEncryptedQuery);
    }

    private void upgradeToVersionThree(SQLiteDatabase db) {
        createDefaultDeviceTable(db);
    }

    private void upgradeToVersionFour(SQLiteDatabase db) {
        String upgradeCountryCodeQuery = "ALTER TABLE " + UserDBService.TABLE_NAME
                + " ADD COLUMN " + UserDBService.COUNTRY_CODE + " TEXT DEFAULT 86";
        db.execSQL(upgradeCountryCodeQuery);

        createCityIndiaTable(db);
    }

    private void createCityChinaTable(SQLiteDatabase db) {
        String createCityTable = "CREATE TABLE IF NOT EXISTS "
                + CityChinaDBService.TABLE_NAME + "("
                + CityChinaDBService.NAME_ZH + " TEXT,"
                + CityChinaDBService.NAME_EN + " TEXT,"
                + CityChinaDBService.CODE + " TEXT PRIMARY KEY,"
                + CityChinaDBService.IS_CURRENT + " INTEGER)";
        db.execSQL(createCityTable);
    }

    private void createCityIndiaTable(SQLiteDatabase db) {
        String createCityTable = "CREATE TABLE IF NOT EXISTS "
                + CityIndiaDBService.TABLE_NAME + "("
                + CityIndiaDBService.NAME_ZH + " TEXT,"
                + CityIndiaDBService.NAME_EN + " TEXT,"
                + CityIndiaDBService.CODE + " TEXT PRIMARY KEY,"
                + CityIndiaDBService.IS_CURRENT + " INTEGER)";
        db.execSQL(createCityTable);
    }

    private void createUserTable(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE IF NOT EXISTS "
                + UserDBService.TABLE_NAME + "("
                + UserDBService.USER_ID + " TEXT PRIMARY KEY,"
                + UserDBService.PHONE_NUMBER + " TEXT,"
                + UserDBService.PASSWORD + " TEXT,"
                + UserDBService.NICKNAME + " TEXT,"
                + UserDBService.IS_DEFAULT + " INTEGER,"
                + UserDBService.IS_ENCRYPTED + " INTEGER DEFAULT 0,"
                + UserDBService.COUNTRY_CODE + " TEXT DEFAULT 86)";
        db.execSQL(createUserTable);
    }

    private void createDefaultDeviceTable(SQLiteDatabase db) {
        String createDefaultDeviceTable = "CREATE TABLE IF NOT EXISTS "
                + DefaultDeviceDBService.TABLE_NAME + "("
                + DefaultDeviceDBService.LOCATION_ID + " INTEGER PRIMARY KEY,"
                + DefaultDeviceDBService.DEVICE_ID + " INTEGER )";
        db.execSQL(createDefaultDeviceTable);
    }

}
