package com.honeywell.hch.airtouchv2.app.authorize.model;

/**
 * Created by Qian Jin on 8/14/15.
 */
public class HomeAndCity {
    private String mHomeName;
    private int mLocationId;
    private String mHomeCity;

    public HomeAndCity(String homeName, int locationId, String homeCity) {
        mHomeName = homeName;
        mLocationId = locationId;
        mHomeCity = homeCity;
    }

    public String getHomeName() {
        return mHomeName;
    }

    public int getLocationId() {
        return mLocationId;
    }

    public String getHomeCity() {
        return mHomeCity;
    }
}
