package com.honeywell.hch.airtouchv2.lib.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.honeywell.hch.airtouchv2.ATApplication;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The util for log. Set whether print log, save log to file and the file log level.
 * Created by liunan on 1/14/15.
 */
public class LogUtil {

    public static enum LogLevel {

        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR

    }

    private static boolean isLogEnabled = true;
    private static boolean isLogSaved = false;
    private static String mLogFileName = "";
    private static LogLevel mLogFileLevel = LogLevel.ERROR;

    public static boolean isLogEnabled() {
        return isLogEnabled;
    }

    public static void setIsLogEnabled(boolean isLogEnabled) {
        LogUtil.isLogEnabled = isLogEnabled;
    }

    public static boolean isLogSaved() {
        return isLogSaved;
    }

    public static void setIsLogSaved(boolean isLogSaved) {
        LogUtil.isLogSaved = isLogSaved;
    }

    public static String getLogFileName() {
        return mLogFileName;
    }

    public static void setLogFileName(String logFileName) {
        LogUtil.mLogFileName = logFileName;
    }

    public static LogLevel getLogFileLevel() {
        return mLogFileLevel;
    }

    public static void setLogFileLevel(LogLevel logFileLevel) {
        LogUtil.mLogFileLevel = logFileLevel;
    }

    public static void log(LogLevel level, String tag, String message) {
        if (isLogEnabled) {
            switch (level) {
                case VERBOSE:
                    Log.v(tag, message);
                    break;
                case DEBUG:
                    Log.d(tag, message);
                    break;
                case INFO:
                    Log.i(tag, message);
                    break;
                case WARN:
                    Log.w(tag, message);
                    break;
                case ERROR:
                    Log.e(tag, message);
                    break;
                default:
                    Log.d(tag, message);
                    break;
            }
        }
        if (!isLogSaved)
            return;
        saveLogToFile(level, tag, message);
    }

    private static void saveLogToFile(LogLevel level, String tag, String message) {
        if (TextUtils.isEmpty(mLogFileName)) {
            mLogFileName = DateTimeUtil.getNowDateTimeString(DateTimeUtil.LOG_TIME_FORMAT) + ".log";
        }
        try {
            FileOutputStream fos = ATApplication.getInstance().openFileOutput(mLogFileName, Context.MODE_APPEND);
            fos.write((DateTimeUtil.getNowDateTimeString(DateTimeUtil.LOG_TIME_FORMAT) + "_" +
                    level.name() + "_" + tag + "_" + message).getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
