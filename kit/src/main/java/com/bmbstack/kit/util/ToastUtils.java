package com.bmbstack.kit.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.bmbstack.kit.app.BaseApplication;

import es.dmoral.toasty.Toasty;

public class ToastUtils {

  public static void error(@StringRes int resId) {
    error(BaseApplication.instance().getString(resId));
  }

  public static void error(String text) {
    Toasty.error(BaseApplication.instance(), text, Toast.LENGTH_SHORT, true).show();
  }

  public static void success(@StringRes int resId) {
    success(BaseApplication.instance().getString(resId));
  }

  public static void success(String text) {
    Toasty.success(BaseApplication.instance(), text, Toast.LENGTH_SHORT, true).show();
  }

  public static void info(@StringRes int resId) {
    info(BaseApplication.instance().getString(resId));
  }

  public static void info(String text) {
    Toasty.info(BaseApplication.instance(), text, Toast.LENGTH_SHORT, true).show();
  }

  public static void warning(@StringRes int resId) {
    warning(BaseApplication.instance().getString(resId));
  }

  public static void warning(String text) {
    Toasty.warning(BaseApplication.instance(), text, Toast.LENGTH_SHORT, true).show();
  }

  public static void normal(@StringRes int resId) {
    normal(BaseApplication.instance().getString(resId));
  }

  public static void normal(String text) {
    Toasty.normal(BaseApplication.instance(), text).show();
  }

  public static void normal(String text, Drawable icon) {
    Toasty.normal(BaseApplication.instance(), text, icon).show();
  }

}
