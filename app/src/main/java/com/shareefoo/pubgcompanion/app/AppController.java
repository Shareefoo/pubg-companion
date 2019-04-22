package com.shareefoo.pubgcompanion.app;


import android.app.Application;
import android.util.Log;

import com.shareefoo.pubgcompanion.BuildConfig;

import timber.log.Timber;

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    // TODO: use crashlytics to send crash reports

    /**
     * A tree which logs important information for crash reporting.
     */
    private static final class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {

            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                // Do something about this
                return;
            }

        }
    }
}
