package com.honeywell.hch.airtouchv3.framework.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.hch.airtouchv3.HPlusApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nan.liu on 2/2/15.
 */
public class DBService {
	protected MySQLiteHelper mySQLiteHelper;

	public DBService(Context context) {
        if (context == null)
            context = HPlusApplication.getInstance().getApplicationContext();
		mySQLiteHelper = new MySQLiteHelper(context);
	}

    public void insertOrUpdate(String tableName, String[] volumn,
                               List<HashMap<String, Object>> value) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getWritableDatabase();
        StringBuffer volumnBuffer = new StringBuffer();
        volumnBuffer.append("replace into " + tableName + " (");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append(volumn[i] + ",");
        }

        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(") values(");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append("?,");
        }

        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(");");
        sqLiteDatabase.beginTransaction();
        for (int i = 0; i < value.size(); i++) {
            String[] valueString = new String[volumn.length];
            for (int j = 0; j < volumn.length; j++) {
                valueString[j] = value.get(i).get(volumn[j]) == null ? ""
                        : value.get(i).get(volumn[j]).toString();
            }
            sqLiteDatabase.execSQL(volumnBuffer.toString(), valueString);
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }
    public void insertOrUpdate(String tableName, String[] volumn,
                               Object[] value) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getWritableDatabase();
        StringBuffer volumnBuffer = new StringBuffer();
        volumnBuffer.append("replace into " + tableName + " (");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append(volumn[i] + ",");
        }

        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(") values(");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append("?,");
        }

        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(");");
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(volumnBuffer.toString(), value);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

    public void insert(String tableName, String[] volumn, String[] value) {
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getWritableDatabase();
        StringBuffer volumnBuffer = new StringBuffer();
        volumnBuffer.append("insert into " + tableName + " (");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append(volumn[i] + ",");
        }
        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(") values(");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append("?,");
        }
        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(");");
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(volumnBuffer.toString(), value);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

	public void delete(String tableName) {
		SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getWritableDatabase();
		StringBuffer deleteSQL = new StringBuffer();
		deleteSQL.append("delete from " + tableName + ";");
		sqLiteDatabase.execSQL(deleteSQL.toString(), new String[] {});
		sqLiteDatabase.close();
	}

    public ArrayList<HashMap<String, String>> findAll(String tableName,
                                                      String[] volumn) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase sqLiteDatabase = mySQLiteHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + tableName,
                null);
        while (cursor.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            for (int i = 0; i < volumn.length; i++) {
                hashMap.put(volumn[i], cursor.getString(i));
            }
            list.add(hashMap);
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;
    }

}
