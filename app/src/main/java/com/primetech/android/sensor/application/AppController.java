package com.primetech.android.sensor.application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by TD-Android on 1/11/2018.
 */

public class AppController extends com.activeandroid.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
