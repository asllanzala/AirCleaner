package com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel;

import com.honeywell.hch.airtouchv3.framework.database.IDBModel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by nan.liu on 2/5/15.
 */
public class City implements Serializable, IDBModel {
    private String mCode;
    private String mNameZh;
    private String mNameEn;
    private int isCurrent = 0;

    public City(){

    }

    public City(HashMap<String, String> cityMap) {
        mCode = cityMap.get("code");
        mNameZh = cityMap.get("nameZh");
        mNameEn = cityMap.get("nameEn");
        isCurrent = Integer.parseInt(cityMap.get("isCurrent"));
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        this.mCode = code;
    }

    public String getNameZh() {
        return mNameZh;
    }

    public void setNameZh(String nameZh) {
        this.mNameZh = nameZh;
    }

    public String getNameEn() {
        return mNameEn;
    }

    public void setNameEn(String nameEn) {
        this.mNameEn = nameEn;
    }

    public int isCurrent() {
        return isCurrent;
    }

    public void setCurrent(int isCurrent) {
        this.isCurrent = isCurrent;
    }

    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> cityMap = new HashMap<>();
        cityMap.put("code", mCode);
        cityMap.put("nameZh", mNameZh);
        cityMap.put("nameEn", mNameEn);
        cityMap.put("isCurrent", isCurrent);
        return cityMap;
    }
}
