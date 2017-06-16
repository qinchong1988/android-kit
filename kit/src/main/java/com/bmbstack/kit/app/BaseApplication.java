package com.bmbstack.kit.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.Utils;

public class BaseApplication extends Application {

    private static BaseApplication sInstance = null;

    public static BaseApplication instance() {
        if (sInstance == null) {
            throw new RuntimeException(
                    "BaseApplication ==null ?? you should extends BaseApplication in you app");
        }
        return sInstance;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public BaseApplication() {
        super();
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Utils.init(this);
        CrashUtils.init();
    }
}