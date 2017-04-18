package com.bmbstack.kit.storage;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bmbstack.kit.app.BaseApplication;
import com.bmbstack.kit.app.Client;

import net.grandcentrix.tray.TrayPreferences;

public class LibTraySp extends TrayPreferences {

  private static final String NAME = "lib";
  public static final int VERSION = 1;

  public static final String LAST_VERSION_NAME = "last_version_name";
  public static final String LAST_VERSION_CODE = "last_version_code";

  private static class SingleInstance {
    private static LibTraySp INSTANCE = new LibTraySp(BaseApplication.instance());
  }

  public static LibTraySp sp() {
    return SingleInstance.INSTANCE;
  }

  public LibTraySp(@NonNull Context context) {
    super(context, NAME, VERSION);
  }

  public static String getLastVersionName() {
    return LibTraySp.sp().getString(LibTraySp.LAST_VERSION_NAME, "1.0.0");
  }

  public static void updateLastVersionName() {
    LibTraySp.sp().put(LibTraySp.LAST_VERSION_NAME, Client.APP_VERSION_NAME);
  }

  public static int getLastVerionCode() {
    return LibTraySp.sp().getInt(LibTraySp.LAST_VERSION_CODE, -1);
  }

  public static void updateLastVersionCode() {
    LibTraySp.sp().put(LibTraySp.LAST_VERSION_CODE, Client.APP_VERSION_CODE);
  }
}
