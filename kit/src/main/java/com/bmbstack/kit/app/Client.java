package com.bmbstack.kit.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.bmbstack.kit.util.SystemUtils;

import java.net.URLEncoder;

public class Client {
    public static int APP_VERSION_CODE;
    public static String APP_VERSION_NAME;
    public static String APP_PKG_NAME;
    public static String APP_CHANNEL;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static void init(Application application) {
        requestPhone(application);
        requestAppInfo(application);
    }

    public static final void requestChannel(String channel) {
        APP_CHANNEL = channel;
    }

    public static void requestPhone(Application application) {
        SCREEN_WIDTH = SystemUtils.getScreenWidth(application, true);
        SCREEN_HEIGHT = SystemUtils.getScreenHeight(application, true);
    }

    public static void requestAppInfo(Application application) {
        try {
            Context ctx = application.getBaseContext();
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            APP_VERSION_CODE = info.versionCode;
            APP_VERSION_NAME = info.versionName;
            APP_PKG_NAME = URLEncoder.encode(application.getPackageName(), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
