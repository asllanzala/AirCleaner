package com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather;

import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Hour;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Weather;

/**
 * Weather data for single city.
 * Created by lynnliu on 10/18/15.
 */
public class WeatherPageData {

    private Weather mWeather;

    private Hour[] mHourlyData = new Hour[28];

    public Weather getWeather() {
        return mWeather;
    }

    public void setWeather(Weather weather) {
        mWeather = weather;
    }

    public Hour[] getHourlyData() {
        return mHourlyData;
    }

    public void setHourlyData(Hour[] hourlyData) {
        mHourlyData = hourlyData;
    }
}
