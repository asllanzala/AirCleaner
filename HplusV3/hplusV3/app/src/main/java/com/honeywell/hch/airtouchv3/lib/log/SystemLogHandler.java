package com.honeywell.hch.airtouchv3.lib.log;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wuyuan on 2/22/16.
 */
public class SystemLogHandler {



    private static final int HANDLER_MESSAGE_LOG_END = 0x0001;

    private  String mLogFilePath;

    private  Context mContext;

    public SystemLogHandler(Context context){
        mContext = context;
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg == null)
                return;
            switch (msg.what) {
                case HANDLER_MESSAGE_LOG_END:
                    //send email
                    LogHandlerUtil.sendEmailToDeveloper(mLogFilePath, mContext,"airtouch android system log");
                    break;

                default:
                    break;
            }
        }
    };

    public void collectAndSendSystemLog(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("logcat -d");
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    StringBuilder log=new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        log.append(line);
                    }

                    mLogFilePath = LogHandlerUtil.saveLogInfo2File(log.toString(), mContext);

                    handler.sendEmptyMessage(HANDLER_MESSAGE_LOG_END);

                } catch (IOException e) {
                }
            }
        }).start();

    }

}
