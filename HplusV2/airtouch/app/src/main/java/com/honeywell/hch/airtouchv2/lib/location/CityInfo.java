package com.honeywell.hch.airtouchv2.lib.location;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CityInfo implements Serializable {

    private static final long serialVersionUID = -7530666141652669565L;

    @SerializedName("country")
    private String mCountry;

    @SerializedName("city")
    private String mCity;// 城市

    @SerializedName("district")
    private String mDistrict;// 区

    @SerializedName("province")
    private String mProvince;// 省

    @SerializedName("street_number")
    private String mStreetNumber;// 街道号码

    @SerializedName("street")
    private String mStreet;// 街道

    @SerializedName("lat")
    private double mLatitude = -1;

    @SerializedName("lng")
    private double mLongitude = -1;

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getDistrict() {
        return mDistrict;
    }

    public void setDistrict(String district) {
        mDistrict = district;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public String getStreetNumber() {
        return mStreetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        mStreetNumber = streetNumber;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(mCountry);
        strBuffer.append(",");
        strBuffer.append(mProvince);
        strBuffer.append(",");
        strBuffer.append(mCity);
        strBuffer.append(",");
        strBuffer.append(mDistrict);
        strBuffer.append(",");
        strBuffer.append(mStreetNumber);
        strBuffer.append(",");
        strBuffer.append(mStreet);
        strBuffer.append(",");
        strBuffer.append(mLatitude);
        strBuffer.append(",");
        strBuffer.append(mLongitude);
        strBuffer.append(",");
        return strBuffer.toString();
    }
}
