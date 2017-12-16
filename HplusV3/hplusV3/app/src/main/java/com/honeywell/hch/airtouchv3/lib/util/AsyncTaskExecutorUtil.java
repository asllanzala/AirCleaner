package com.honeywell.hch.airtouchv3.lib.util;

import android.os.AsyncTask;
import android.os.Build;

import com.honeywell.hch.airtouchv3.framework.config.AppConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MockThreadExecutor implements Executor {
    public void execute(Runnable r) {
        r.run();
    }
}


/**
 * Created by allanhwmac on 15/10/22.
 */
public class AsyncTaskExecutorUtil {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT * 2 + 2;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 3 + 2;
    private static final int KEEP_ALIVE = 5;

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };
    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);


    public static void executeAsyncTask(AsyncTask asyncTask) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (AppConfig.isTestMode) {
                    asyncTask.executeOnExecutor(new MockThreadExecutor(), 0);
                } else {
                    asyncTask.executeOnExecutor(THREAD_POOL_EXECUTOR, 0);
                }
            }
            else {
                asyncTask.execute();
            }
        } catch (RuntimeException e) {

        } catch (Exception e) {

        }
    }
}
