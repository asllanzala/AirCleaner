package com.honeywell.hch.airtouchv3.framework.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.honeywell.hch.airtouchv3.framework.webservice.task.LongTimerRefreshTask;
import com.honeywell.hch.airtouchv3.framework.webservice.task.ShortTimerRefreshTask;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;

/**
 * Created by Stephen(H127856) on 12/10/2015.
 */
public class TimerRefreshService extends Service {
    private static final String TAG = "AirTouchService";

    private static final int SHOR_FRESH = 10001;
    private static final int LONG_FRESH = 10002;

    private static final int POLLING_GAP = 20 * 1000;

    private int sleepCount = 0;

    private boolean isThreadRunning = true;

    private boolean isNeedRefresh = true;

    private final IBinder binder = new MyBinder();

    private Thread refreshThread;

    private ShortTimerRefreshTask mShortTimerRefreshTask;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOR_FRESH:
                    if (mShortTimerRefreshTask == null || !mShortTimerRefreshTask.isRunning()){
                        mShortTimerRefreshTask = new ShortTimerRefreshTask();
                        AsyncTaskExecutorUtil.executeAsyncTask(mShortTimerRefreshTask);
                    }
                    break;
                case LONG_FRESH:
                    LongTimerRefreshTask longTimerRefreshTask = new LongTimerRefreshTask();
                    AsyncTaskExecutorUtil.executeAsyncTask(longTimerRefreshTask);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class MyBinder extends Binder {
        public TimerRefreshService getService() {
            return TimerRefreshService.this;
        }
    }
    private String mSessionId;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSessionId = AppManager.shareInstance().getAuthorizeApp().getSessionId();
        
        refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadRunning) {
                    try {
                        if (isNeedRefresh){
                            //get location data and device data
                            Message message = Message.obtain();
                            message.what = SHOR_FRESH;
                            mHandler.sendMessage(message);

                            if (sleepCount == 60 || sleepCount == 0){
                                Message message2 = Message.obtain();
                                message2.what = LONG_FRESH;
                                mHandler.sendMessage(message2);
                                sleepCount = 0;
                            }

                            sleepCount++;
                        }
                        Thread.sleep(POLLING_GAP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        refreshThread.start();
    }

    /**
     * stop the refresh
     */
    public void stopRefreshThread(){
        isNeedRefresh = false;
        sleepCount = 0;
    }

    /**
     * start the refresh
     */
    public void startRefreshThread(){
        isNeedRefresh = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isThreadRunning = false;
    }


}
