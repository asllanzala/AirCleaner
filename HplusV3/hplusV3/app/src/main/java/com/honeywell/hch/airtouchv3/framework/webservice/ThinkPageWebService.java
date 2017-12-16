package com.honeywell.hch.airtouchv3.framework.webservice;

import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HourlyFuture;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.HourlyHistory;

/**
 * Created by lynnliu on 10/15/15.
 */
public interface ThinkPageWebService {

    public HourlyHistory getHistoryWeather(String city, String lang);

    public HourlyFuture getFutureWeather(String city, String lang);
}
