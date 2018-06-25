package com.mobileoid2.celltone.Util;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.splunk.mint.Mint;

public class AppLevelConstraints extends MultiDexApplication {

    public static Context appContext = null;

    public static AppLevelConstraints instance = null;


    public static AppLevelConstraints getInstance() {

        if (instance == null) {
            synchronized (AppLevelConstraints.class) {
                instance = new AppLevelConstraints();
            }
        }
        return instance;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        MultiDex.install(this);
        Mint.initAndStartSession(this, "13e7af05");
    }

    public static Context getAppContext() {
        return appContext;
    }
}
