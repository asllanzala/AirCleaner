package com.honeywell.hch.airtouchv2.framework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nan.liu on 2/2/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "airTouch.db";

    public MySQLiteHelper(Context context, CursorFactory factory, int version) {
        super(context, DB_NAME, factory, version);
    }

    public MySQLiteHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCityTable = "CREATE table if not exists "
                + CityDBService.TABLE_NAME + "("
                + CityDBService.NAME_ZH + " text,"
                + CityDBService.NAME_EN + " text,"
                + CityDBService.CODE + " text primary key,"
                + CityDBService.IS_CURRENT + " integer)";
        String createUserTable = "CREATE table if not exists "
                + UserDBService.TABLE_NAME + "("
                + UserDBService.USER_ID + " text primary key,"
                + UserDBService.PHONE_NUMBER + " text,"
                + UserDBService.PASSWORD + " text,"
                + UserDBService.NICKNAME + " text,"
                + UserDBService.IS_DEFAULT + " integer)";
        db.execSQL(createCityTable);
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
