package com.honeywell.hch.airtouchv2.framework.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.User;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nan.liu on 2/3/15.
 */
public class UserDBService extends DBService {

    //table info
    public static final String TABLE_NAME = "user";
    public static final String USER_ID = "userID";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PASSWORD = "password";
    public static final String NICKNAME = "nickName";
    public static final String IS_DEFAULT = "isDefault";

    public static String[] DBKey = {USER_ID, PHONE_NUMBER, PASSWORD, NICKNAME, IS_DEFAULT};

    public UserDBService(Context context) {
        super(context);
    }

    public void insertUsers(List<User> list) {
        List<HashMap<String, Object>> userList = new ArrayList<>();
        for (User userInfo : list) {
            userList.add(userInfo.getHashMap());
        }
        insertOrUpdate(TABLE_NAME, DBKey, userList);
    }

    public void insertUser(User user) {
        if (StringUtil.isEmpty(user.getUserID()))
            return;
        List<HashMap<String, Object>> userList = new ArrayList<>();
        userList.add(user.getHashMap());
        insertOrUpdate(TABLE_NAME, DBKey, userList);
    }

    public ArrayList<User> findAllUsers() {
        ArrayList<HashMap<String, String>> userDBList = findAll(TABLE_NAME, DBKey);
        ArrayList<User> userList = new ArrayList<>();
        for (HashMap<String, String> userMap : userDBList) {
            userList.add(new User(userMap));
        }
        return userList;
    }

    public User getDefaultUser() {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                + " FROM " + TABLE_NAME
                + " WHERE " + IS_DEFAULT + " = '1'", null);
        User user = new User();
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            HashMap<String, String> userMap = new HashMap<>();
            for (int i = 0; i < DBKey.length; i++) {
                userMap.put(DBKey[i], cursor.getString(i));
            }
            user = new User(userMap);
        }
        cursor.close();
        sqLiteDatabase.close();
        return user;
    }

    public ArrayList<User> getUsersByKey(String key) {
        ArrayList<User> userList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                + " FROM " + TABLE_NAME
                + " WHERE " + PHONE_NUMBER + " LIKE '" + key + "%'", null);
        while (cursor.moveToNext()) {
            HashMap<String, String> userMap = new HashMap<>();
            for (int i = 0; i < DBKey.length; i++) {
                userMap.put(DBKey[i], cursor.getString(i));
            }
            userList.add(new User(userMap));
        }
        cursor.close();
        sqLiteDatabase.close();
        return userList;
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                + " FROM " + TABLE_NAME
                + " WHERE " + PHONE_NUMBER + " = '" + phoneNumber + "'", null);
        User user = new User();
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            HashMap<String, String> userMap = new HashMap<>();
            for (int i = 0; i < DBKey.length; i++) {
                userMap.put(DBKey[i], cursor.getString(i));
            }
            user = new User(userMap);
        }
        cursor.close();
        sqLiteDatabase.close();
        return user;
    }

    public User getUserByID(String userID) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *"
                + " FROM " + TABLE_NAME
                + " WHERE " + USER_ID + " = '" + userID + "'", null);
        User user = new User();
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            HashMap<String, String> userMap = new HashMap<>();
            for (int i = 0; i < DBKey.length; i++) {
                userMap.put(DBKey[i], cursor.getString(i));
            }
            user = new User(userMap);
        }
        cursor.close();
        sqLiteDatabase.close();
        return user;
    }

    public void deleteUserByID(String userID) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("DELETE"
                + " FROM " + TABLE_NAME
                + " WHERE " + USER_ID + " = '" + userID + "';", new Object[]{});
        sqLiteDatabase.close();
    }
}
