package com.mobileoid2.celltone.Util;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;


public class CelltoneApplication extends MultiDexApplication {

    public static final String TAG=CelltoneApplication.class.getSimpleName();

    /*Database Name*/
    private static final String DATABASE_NAME = "MyDatabase";

    public static Context appContext = null;

    public static CelltoneApplication instance = null;


    public static CelltoneApplication getInstance() {

        if (instance == null) {
            synchronized (CelltoneApplication.class) {
                instance = new CelltoneApplication();
                            }
        }
        return instance;

    }

   /* private CelltoneDatabase database;

    public CelltoneDatabase getDatabase() {
        return database;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(this);
        /*database = Room.databaseBuilder(this, CelltoneDatabase.class, DATABASE_NAME).build();*/

        MultiDex.install(this);
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/OPTIMA.TTF");

    }

    public static Context getAppContext() {
        return appContext;
    }

    /**/
    private static RequestQueue mRequestQueue;


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
             mRequestQueue = Volley.newRequestQueue(instance);
            //mRequestQueue = Volley.newRequestQueue(instance, new HurlStack());
        }

        return mRequestQueue;
    }



    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
