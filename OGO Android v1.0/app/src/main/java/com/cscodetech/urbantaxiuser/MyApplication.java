package com.customerogo.app;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;

public class MyApplication extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
            FirebaseApp.initializeApp(this);
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

            // OneSignal Initialization
            OneSignal.initWithContext(this);
            OneSignal.setAppId("d03cea38-f3db-44a3-a273-9f1a6b2f014e");
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}