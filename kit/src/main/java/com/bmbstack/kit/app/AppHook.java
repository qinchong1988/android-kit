package com.bmbstack.kit.app;

import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.storage.LibTraySp;

public class AppHook {

  private static final String TAG = AppHook.class.getSimpleName();

  public interface AppUpgradeHook {

    void onFirstInstallOrFromUninstall(int lastVersionCode, int appVersionCode);

    void onUpgrade(int lastVersionCode, int appVersionCode);

    void onSameVersion(int versionCode);

    void onDowngrading(int lastVersionCode, int appVersionCode);
  }

  public static void checkUpgrade() {
    checkUpgrade(null);
  }

  public static void checkUpgrade(AppUpgradeHook hook) {
    String lastVName = LibTraySp.getLastVersionName();
    int lastVCode = LibTraySp.getLastVerionCode();
    Logger.v(TAG, "lastVName = ", lastVName, " , lastVCode=", lastVCode);
    Logger.v(TAG, "curVName = ", Client.APP_VERSION_NAME, " , curVCode=", Client.APP_VERSION_CODE);
    if (lastVCode == -1) {
      Logger.v(TAG, " 当前是首次安装或卸载安装");
      if (hook != null) {
        hook.onFirstInstallOrFromUninstall(lastVCode, Client.APP_VERSION_CODE);
      }
    } else if (Client.APP_VERSION_CODE > lastVCode) {
      Logger.v(TAG, " 当前是升级安装");
      if (hook != null) {
        hook.onUpgrade(lastVCode, Client.APP_VERSION_CODE);
      }
    } else if (Client.APP_VERSION_CODE == lastVCode) {
      Logger.v(TAG, " 当前是平级安装");
      if (hook != null) {
        hook.onSameVersion(Client.APP_VERSION_CODE);
      }
    } else {
      Logger.v(TAG, " 当前是降级安装");
      if (hook != null) {
        hook.onDowngrading(lastVCode, Client.APP_VERSION_CODE);
      }
    }
    LibTraySp.updateLastVersionName();
    LibTraySp.updateLastVersionCode();
  }
}
