package com.honeywell.hch.airtouchv2.lib.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The util for date/time.
 * Created by liunan on 1/14/15.
 */
public class DateTimeUtil {

    public static final String LOG_TIME_FORMAT = "yyyy-mm-dd HH:mm:ss";

    /**
     * return current time string use the specified format
     *
     * @param format date/time format
     * @return date/time string
     */
    public static String getNowDateTimeString(String format) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }
}
