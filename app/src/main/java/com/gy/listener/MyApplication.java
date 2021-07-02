package com.gy.listener;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

public class MyApplication extends Application {

    // region Members

    private static Context _appContext;
    private static final Handler _mainHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    // endregion

    // region Properties

    public static Context getAppContext() {
        return _appContext;
    }

    public static Handler getMainHandler() {
        return _mainHandler;
    }


    // endregion

    @Override
    public void onCreate() {
        super.onCreate();

        _appContext = getApplicationContext();
    }
}
