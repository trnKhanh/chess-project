package com.chessproject;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {
    ExecutorService mExecutorService = Executors.newFixedThreadPool(10);
    Handler mMainHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    public ExecutorService getExecutorService() {
        return mExecutorService;
    }
    public Handler getMainHandler() {
        return mMainHandler;
    }
}
