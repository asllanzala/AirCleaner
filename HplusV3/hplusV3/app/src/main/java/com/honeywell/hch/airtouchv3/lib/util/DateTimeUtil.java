package com.honeywell.hch.airtouchv3.lib.util;

import com.honeywell.hch.airtouchv3.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The util for date/time.
 * Created by liunan on 1/14/15.
 */
public class DateTimeUtil {
    private static final String TAG = "HPlusDateTimeUtil";

    public static final String LOG_TIME_FORMAT = "yyyy-mm-dd HH:mm:ss";

    public static final String THINKPAGE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static final String THINKPAGE_DATE_FORMAT = "yyyy-MM-dd";

    public static final String WEATHER_CHART_TIME_FORMAT = "HH:mm";

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

    /**
     * return calendar instance with specified format and specified time string
     *
     * @param format date/time format
     * @param timeString data/time string
     * @return date
     */
    public static Date getDateTimeFromString(String format, String timeString) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            date = dateFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "xinzhi date time transfer error");
        }
        return date;
    }

    /**
     * return time string with the specified format
     *
     * @param date date need to be formatted
     * @param format date/time format
     * @return date/time string
     */
    public static String getDateTimeString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static int isTodayOrTomorrow(Date date) {

        Calendar calendar = Calendar.getInstance();
        Calendar calendarTomorrow = Calendar.getInstance();

        calendar.setTime(date);
        calendarTomorrow.set(Calendar.HOUR_OF_DAY, 23);
        calendarTomorrow.set(Calendar.MINUTE, 59);
        calendarTomorrow.set(Calendar.SECOND, 59);

        int timeStringID = R.string.weather_today;
        if (calendar.after(calendarTomorrow)) {
            timeStringID = R.string.weather_tomorrow;
        }
        return timeStringID;
    }
}
