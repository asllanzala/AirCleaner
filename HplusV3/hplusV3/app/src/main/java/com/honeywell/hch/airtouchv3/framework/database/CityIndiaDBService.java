package com.honeywell.hch.airtouchv3.framework.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Qian Jin on 11/25/15.
 */
public class CityIndiaDBService extends DBService {

    //table info
    public static final String TABLE_NAME = "cityIndia";
    public static final String NAME_ZH = "nameZh";
    public static final String NAME_EN = "nameEn";
    public static final String CODE = "code";
    public static final String IS_CURRENT = "isCurrent";

    public static String[] DBKey = {NAME_ZH, NAME_EN, CODE, IS_CURRENT};

    public CityIndiaDBService(Context context) {
        super(context);
    }

    public synchronized void insertAllCity(List<City> list) {
        List<HashMap<String, Object>> cityList = new ArrayList<>();
        for (City cityInfo : list) {
            cityList.add(cityInfo.getHashMap());
        }
        insertOrUpdate(TABLE_NAME, DBKey, cityList);
    }

    public synchronized ArrayList<City> findAllCities() {
        ArrayList<HashMap<String, String>> cityDBList = findAll(TABLE_NAME, DBKey);
        ArrayList<City> cityList = new ArrayList<>();
        for (HashMap<String, String> cityMap : cityDBList) {
            cityList.add(new City(cityMap));
        }
        return cityList;
    }

    public synchronized ArrayList<City> getCitiesByKey(String key) {
        ArrayList<City> cityList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                + " FROM " + TABLE_NAME
                + " WHERE " + NAME_ZH + " LIKE '" + "%" + key + "%'"
                + " OR " + NAME_EN + " LIKE '" + "%" + key + "%'"
                + " COLLATE NOCASE", null);
        while (cursor.moveToNext()) {
            HashMap<String, String> cityMap = new HashMap<>();
            for (int i = 0; i < DBKey.length; i++) {
                cityMap.put(DBKey[i], cursor.getString(i));
            }
            cityList.add(new City(cityMap));
        }
        cursor.close();
        sqLiteDatabase.close();
        return cityList;
    }

    public synchronized City getCityByKey(String key) {
        City city = new City();
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                + " FROM " + TABLE_NAME
                + " WHERE " + NAME_ZH + " LIKE '" + "%" + key + "%'"
                + " OR " + NAME_EN + " LIKE '" + "%" + key + "%'"
                + " COLLATE NOCASE", null);
        while (cursor.moveToNext()) {
            HashMap<String, String> cityMap = new HashMap<>();
            for (int i = 0; i < DBKey.length; i++) {
                cityMap.put(DBKey[i], cursor.getString(i));
            }
            city = new City(cityMap);
        }
        cursor.close();
        sqLiteDatabase.close();
        return city;
    }

    public synchronized City getCityByName(String name) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        City city = new City();
        try {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                    + " FROM " + TABLE_NAME
                    + " WHERE " + NAME_ZH + " = '" + name + "'"
                    + " OR " + NAME_EN + " = '" + name + "'"
                    + " COLLATE NOCASE", null);


            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                HashMap<String, String> cityMap = new HashMap<>();
                for (int i = 0; i < DBKey.length; i++) {
                    cityMap.put(DBKey[i], cursor.getString(i));
                }
                city = new City(cityMap);
            }
            cursor.close();
        } catch (Exception e) {

        }

        sqLiteDatabase.close();
        return city;
    }

    public synchronized City getCityByCode(String cityCode) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        sqLiteDatabase.beginTransaction();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                + " FROM " + TABLE_NAME
                + " WHERE " + CODE + " = '" + cityCode + "'", null);
        City city = new City();
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            HashMap<String, String> cityMap = new HashMap<>();
            for (int i = 0; i < DBKey.length; i++) {
                cityMap.put(DBKey[i], cursor.getString(i));
            }
            city = new City(cityMap);
        }

        cursor.close();
        /**
         * AIRQA-572
         [Testin] Java Runtime error: java.lang.NullPointerException - SQLite releated
          */
        sqLiteDatabase.endTransaction();

        //resolve java.lang.IllegalStateException: Cannot perform this operation because the connection pool has been closed.
        sqLiteDatabase.close();
        return city;
    }
}
