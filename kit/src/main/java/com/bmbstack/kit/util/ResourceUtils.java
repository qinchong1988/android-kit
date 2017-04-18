package com.bmbstack.kit.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.widget.TextView;

import com.bmbstack.kit.app.BaseApplication;
import com.bmbstack.kit.log.Logger;

public class ResourceUtils {

  /**
   * get MetaData VALUE
   *
   * @return metaData in Manifest, or null
   */
  public static String getMetaDataValue(Context context, String name) {
    try {
      PackageManager localPackageManager = context.getPackageManager();
      ApplicationInfo localApplicationInfo =
          localPackageManager.getApplicationInfo(context.getPackageName(),
              PackageManager.GET_META_DATA);
      if (localApplicationInfo != null) {
        // use metaData.get(Object) Compatible with pass the pure digital
        return String.valueOf(localApplicationInfo.metaData.get(name));
      }
      Logger.e("getMetaDataValue", "Could not read "
          + name
          + " meta-data from AndroidManifest.xml. localApplicationInfo=null");
    } catch (Exception e) {
      Logger.e("getMetaDataValue",
          "Could not read " + name + " meta-data from AndroidManifest.xml.", e);
      e.printStackTrace();
    }
    return null;
  }

  public static String getString(@StringRes int resId) {
    return BaseApplication.instance().getString(resId);
  }

  public static String getString(@StringRes int resId, Object... formatArgs) {
    return BaseApplication.instance().getString(resId, formatArgs);
  }

  public static void setTextColor(Context context, TextView tv, @ColorRes int id) {
    tv.setTextColor(context.getResources().getColor(id));
  }
}
