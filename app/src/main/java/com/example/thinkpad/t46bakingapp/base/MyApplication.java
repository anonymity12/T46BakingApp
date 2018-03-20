package com.example.thinkpad.t46bakingapp.base;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by thinkpad on 2018/1/30.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
