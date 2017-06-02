package com.bmbstack.kit.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bmbstack.kit.log.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SystemUtils {

  private final static String TAG = SystemUtils.class.getSimpleName();

  public static boolean isLandScape(Context context) {
    return context.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_LANDSCAPE;
  }

  public static void hideSoftInputFromWindow(Context context, EditText editText) {
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
  }

  public static void showSoftInputFromWindow(Context context, EditText editText) {
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
  }

  public static String getEmid(Context context) {
    String ret = getIMEI(context);
    if (TextUtils.isEmpty(ret)) {
      ret = getLocalMacAddress(context);
    }
    return (ret == null) ? "" : ret;
  }

  public static String getLocalMacAddress(Context context) {
    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    if (wifi == null) {
      return "";
    }

    String mac = null;
    WifiInfo info = wifi.getConnectionInfo();
    if (info != null) {
      mac = info.getMacAddress();
    }
    return (mac == null) ? "" : mac;
  }

  public static String getMobileInfo() {
    StringBuffer sb = new StringBuffer();
    try {
      Field[] fields = Build.class.getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        String name = field.getName();
        String value = field.get(null).toString();
        sb.append(name + "=" + value);
        sb.append("\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public static String getCPUInfo() {
    try {
      byte[] bs = new byte[1024];
      RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
      reader.read(bs);
      String ret = new String(bs);
      int index = ret.indexOf(0);
      reader.close();
      if (index != -1) {
        return ret.substring(0, index);
      } else {
        return ret;
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return "";
  }

  public static String getCPUArch() {
    String fileName = "/proc/cpuinfo";// 系统内存信息文件
    String armArct = "";
    try {
      FileReader localFileReader = new FileReader(fileName);
      BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
      String line;
      String[] arrayOfString;

      while ((line = localBufferedReader.readLine()) != null) {
        arrayOfString = line.trim().split(":");
        if ((arrayOfString != null) && (arrayOfString.length == 2)) {
          if (arrayOfString[0].trim().equalsIgnoreCase("processor")) {
            armArct = arrayOfString[1].trim();
            break;
          }
        }
      }
      localBufferedReader.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return armArct;
  }

  public static String getCPUFreq() {
    String fileName = "/proc/cpuinfo";// 系统内存信息文件
    String mainFreq = "";
    try {
      FileReader localFileReader = new FileReader(fileName);
      BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
      String line;
      String[] arrayOfString;

      while ((line = localBufferedReader.readLine()) != null) {
        arrayOfString = line.trim().split(":");
        if ((arrayOfString != null) && (arrayOfString.length == 2)) {

          if (arrayOfString[0].trim().equalsIgnoreCase("bogomips")) {
            mainFreq = arrayOfString[1].trim();
            break;
          }
        }
      }
      localBufferedReader.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return mainFreq;
  }

  // 获取所有内存大小
  public static long getTotalMemSize(Context context) {
    String dir = "/proc/meminfo";
    long initial_memory = 0;
    try {
      FileReader fr = new FileReader(dir);
      BufferedReader br = new BufferedReader(fr, 2048);
      String memoryLine = br.readLine();
      String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
      br.close();
      initial_memory = Long.parseLong(subMemoryLine.replaceAll("\\D+", "")) * 1024;// 返回的是kb，转化为byte
    } catch (IOException e) {
    }
    // return Formatter.formatFileSize(context, initial_memory);//
    // Byte转换为KB或者MB，内存大小规格化
    return initial_memory;
  }

  public static int getUsedPercentValue(Context context) {
    String dir = "/proc/meminfo";
    try {
      FileReader fr = new FileReader(dir);
      BufferedReader br = new BufferedReader(fr, 2048);
      String memoryLine = br.readLine();
      String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
      br.close();
      long totalMemorySize = Long.parseLong(subMemoryLine.replaceAll("\\D+", ""));
      long availableSize = getAvailMemSize(context) / 1024;
      int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
      return percent;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return -1;
  }

  // 获得系统可用内存
  public static long getAvailMemSize(Context context) {
    // 获得MemoryInfo对象
    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
    // 获得系统可用内存，保存在MemoryInfo对象上
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    am.getMemoryInfo(memoryInfo);
    // return Formatter.formatFileSize(context, memoryInfo.availMem);
    return memoryInfo.availMem;
  }

  // 获取指定SD卡的总容量
  public static long getTotalSDCardSize(String sdPath) {
    File root = new File(sdPath);
    if (root.exists() && root.canRead()) {
      try {
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long blockCount = sf.getBlockCount();
        return blockCount * blockSize;
      } catch (Exception e) {
        return 0;
      }
    } else {
      return 0;
    }
  }

  // 获取指定SD卡的可用容量
  public static long getAvailSDCardSize(String sdPath) {
    File root = new File(sdPath);
    if (root.exists() && root.canRead()) {
      try {
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        return availCount * blockSize;
      } catch (Exception e) {
        return 0;
      }
    } else {
      return 0;
    }
  }

  // 获取SD卡总容量
  public static long getTotalSDCardSize() {
    File root = Environment.getExternalStorageDirectory();
    StatFs sf = new StatFs(root.getPath());
    long blockSize = sf.getBlockSize();
    long blockCount = sf.getBlockCount();
    return blockCount * blockSize;
  }

  // 获取SD卡可得到的容量
  public static long getAvailSDCardSize() {
    File root = Environment.getExternalStorageDirectory();
    StatFs sf = new StatFs(root.getPath());
    long blockSize = sf.getBlockSize();
    long availCount = sf.getAvailableBlocks();
    return availCount * blockSize;
  }

  // 获取机身ROM的总容量
  public static long getTotalRomSize() {
    File root = Environment.getRootDirectory();
    StatFs sf = new StatFs(root.getPath());
    long blockSize = sf.getBlockSize();
    long blockCount = sf.getBlockCount();
    return blockCount * blockSize;
  }

  // 获取机身ROM可使用的容量
  public static long getAvailRomSize() {
    File root = Environment.getRootDirectory();
    StatFs sf = new StatFs(root.getPath());
    long blockSize = sf.getBlockSize();
    long availCount = sf.getAvailableBlocks();
    return availCount * blockSize;
  }

  /**
   * 是否开启重力感应
   */
  public static boolean getSystemGravity(Context context) {
    boolean isGravity = false;
    try {
      int systemGravity = Settings.System.getInt(context.getContentResolver(),
          Settings.System.ACCELEROMETER_ROTATION);
      isGravity = 0 != systemGravity;
    } catch (Settings.SettingNotFoundException e1) {
      e1.printStackTrace();
    }
    return isGravity;
  }

  /**
   * 获取本机IMEI
   *
   * @param context 上下文
   * @return imei号
   */
  public static String getIMEI(final Context context) {
    TelephonyManager manager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return manager.getDeviceId();
  }

  public static int getStatusBerHeight(Activity activity) {
    Rect frame = new Rect();
    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
    return frame.top;
  }

  public static int getScreenWidth(Context context) {
    return getScreenWidth(context, false);
  }

  public static int getScreenHeight(Context context) {
    return getScreenHeight(context, false);
  }

  public static int getScreenHeight(Context context, boolean realHeight) {
    int orientation = context.getResources().getConfiguration().orientation;
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    if (realHeight && orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return metrics.widthPixels;
    } else {
      return metrics.heightPixels;
    }
  }

  public static int getScreenWidth(Context context, boolean realWidth) {
    int orientation = context.getResources().getConfiguration().orientation;
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    if (realWidth && orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return metrics.heightPixels;
    } else {
      return metrics.widthPixels;
    }
  }

  public static float getScreenDesity(Context context) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    return metrics.density;
  }

  public static int getScreenDesityDpi(Context context) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    return metrics.densityDpi;
  }

  public static int getSystemIconSize() {
    Drawable d = Resources.getSystem().getDrawable(android.R.drawable.sym_def_app_icon);
    return d.getIntrinsicWidth();
  }

  public static String getSystemVersion(Context context) {
    return "android" + Build.VERSION.RELEASE;
  }

  /**
   * 如果Model不为null，去掉空格
   */
  public static String getModel(Context context) {
    return Build.MODEL != null ? Build.MODEL.replace(" ", "") : "unknown";
  }

  public static int getScreenOrientation(Context context) {
    return context.getResources().getConfiguration().orientation;
  }

  public static float getDensity(Context context) {
    try {
      return context.getResources().getDisplayMetrics().density;
    } catch (Exception e) {
      Logger.e(TAG, "", e);
      return 1.5f;
    }
  }

  public static boolean supportNEON() {
    return supportNEON(getCPUInfo());
  }

  public static boolean supportNEON(String cpuInfo) {
    if (TextUtils.isEmpty(cpuInfo)) {
      return false;
    }
    return cpuInfo.toLowerCase(Locale.getDefault()).contains("neon");
  }

  public static boolean isARMV5(String cpuInfo) {
    return (cpuInfo != null) && (cpuInfo.toLowerCase(Locale.getDefault()).contains("armv5")
        || cpuInfo.toLowerCase().contains("5te"));
  }

  public static boolean isARMV5VFP(String cpuInfo) {
    return (cpuInfo != null) && (cpuInfo.toLowerCase(Locale.getDefault()).contains("armv5")
        || cpuInfo.toLowerCase().contains("5te")) && cpuInfo.toLowerCase(Locale.getDefault())
        .contains("vfp");
  }

  /**
   * 判断是否为低端机
   */
  public static boolean isLowEndDevice() {
    if (Build.MODEL.equals("Lenovo A520")) {
      return false;
    } else {
      return !getCPUInfo().toLowerCase(Locale.getDefault()).contains("neon");
    }
  }

  private static String devLevel = null;

  public static String getDeviceLevel() {
    if (null == devLevel) {
      devLevel = isLowEndDevice() ? "1" : "0";
    }
    return devLevel;
  }

  private static String devLevelConsideringScreen = null;

  public static String getDeviceLevelConsideringScreen(Context context) {
    if (null == devLevelConsideringScreen) {
      devLevelConsideringScreen = (isLowEndDevice() || (getDensity(context) < 1.5f)) ? "1" : "0";
    }
    return devLevelConsideringScreen;
  }

  public static boolean isCloudTransPreferred() {
    return (isLowEndDevice() || "GT-S7562".equalsIgnoreCase(Build.MODEL));
  }

  public static boolean isARMV6(String cpuInfo) {
    return (cpuInfo != null) && cpuInfo.toLowerCase().contains("armv6");
  }

  public static boolean isARMV6VFP(String cpuInfo) {
    return (cpuInfo != null) && cpuInfo.toLowerCase().contains("armv6") && cpuInfo.toLowerCase()
        .contains("vfp");
  }

  public static boolean isARMV7(String cpuInfo) {
    return (cpuInfo != null) && cpuInfo.toLowerCase().contains("armv7");
  }

  public static boolean isARMV7NEON(String cpuInfo) {
    return isARMV7(cpuInfo) && supportNEON(cpuInfo);
  }

  public static boolean isARMV7VFPV3(String cpuInfo) {
    return (cpuInfo != null) && isARMV7(cpuInfo) && cpuInfo.toLowerCase().contains("vfpv3");
  }

  public static boolean isARMV7VFP(String cpuInfo) {
    return (cpuInfo != null) && isARMV7(cpuInfo) && cpuInfo.toLowerCase().contains("vfp");
  }

  public static boolean isARMV7() {
    String abi = Build.CPU_ABI;
    if (TextUtils.isEmpty(abi)) {
      return false;
    }

    return abi.toLowerCase().contains("armeabi-v7");
  }

  public static boolean isX86(String cpuInfo) {
    String abi = Build.CPU_ABI;
    if (TextUtils.isEmpty(abi)) {
      return false;
    }

    return abi.toLowerCase().contains("x86");
  }

  public static String getCpuABI() {
    try {
      String abi = Build.CPU_ABI;
      if (TextUtils.isEmpty(abi)) {
        return "";
      }
      return abi.toLowerCase();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * 清理内存
   */
  public static void clearMemory(final Context context) {
    new Thread() {
      @Override public void run() {
        super.run();
        try {
          ActivityManager activityManager =
              (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
          List<ActivityManager.RunningAppProcessInfo> appProcessList =
              activityManager.getRunningAppProcesses();
          for (ActivityManager.RunningAppProcessInfo runningApp : appProcessList) {
            String packageNames[] = runningApp.pkgList;
            for (String packageName : packageNames) {
              activityManager.killBackgroundProcesses(packageName);
            }
          }
        } catch (Exception e) {
        }
      }
    }.start();
  }

  private static String getLocalizedLabel(final Context ctx, final PackageManager pm,
      final ApplicationInfo appInfo, Locale locale) {
    if ((ctx == null) || (pm == null) || (appInfo == null)) {
      return null;
    }
    try {
      String label;
      if (appInfo.labelRes != 0) {
        final Configuration config = new Configuration();
        config.locale = locale;
        final Resources resources = pm.getResourcesForApplication(appInfo);
        resources.updateConfiguration(config, ctx.getResources().getDisplayMetrics());
        label = resources.getString(appInfo.labelRes);
        if (TextUtils.isEmpty(label)) {
          CharSequence cs = pm.getApplicationLabel(appInfo);
          if (cs != null) {
            label = cs.toString();
          } else {
            label = "";
          }
        }
      } else {
        CharSequence cs = pm.getApplicationLabel(appInfo);
        if (cs != null) {
          label = cs.toString();
        } else {
          label = "";
        }
      }
      return label;
    } catch (PackageManager.NameNotFoundException e) {
      return null;
    } catch (Resources.NotFoundException e1) {
      CharSequence cs = pm.getApplicationLabel(appInfo);
      if (cs != null) {
        return cs.toString();
      } else {
        return "";
      }
    }
  }

  public static String getEnLocalizedAppLaben(Context ctx, PackageManager pm,
      ApplicationInfo appInfo) {
    String s = getLocalizedLabel(ctx, pm, appInfo, Locale.CHINA);
    if (TextUtils.isEmpty(s)) {
      s = getLocalizedLabel(ctx, pm, appInfo, Locale.ENGLISH);
    }
    return s;
  }

  public static String getApkLabel(Context ctx, String apkFile) {
    try {
      PackageInfo pkgInfo = ctx.getPackageManager().getPackageArchiveInfo(apkFile, 0);
      ApplicationInfo ai = pkgInfo.applicationInfo;

      if (Build.VERSION.SDK_INT >= 8) {
        ai.sourceDir = apkFile;
        ai.publicSourceDir = apkFile;
      }
      return getEnLocalizedAppLaben(ctx, ctx.getPackageManager(), ai);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * To determine whether it contains a gyroscope
   *
   * @return boolean
   */
  public static boolean isHaveGravity(Context context) {
    SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    return manager != null;
  }

  public static boolean isSamsungPhone() {
    try {
      return Build.MANUFACTURER.equalsIgnoreCase("samsung");
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static void setFullScreen(Activity activity, boolean isFull) {
    WindowManager.LayoutParams params = activity.getWindow().getAttributes();
    if (isFull) {
      params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
      activity.getWindow().setAttributes(params);
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    } else {
      params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
      activity.getWindow().setAttributes(params);
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
  }

  public static String getCurProcessName(Context ctx) {
    String currentProcName = "";
    try {
      int pid = android.os.Process.myPid();
      ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
      // isolatedProcess属性为true的process 调用getRunningAppProcesses会抛出异常
      for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
        if (processInfo.pid == pid) {
          currentProcName = processInfo.processName;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return currentProcName;
  }

  private static final String WHITE_LIST = "com\\.saxdashi";
  private static final Pattern WHITE_LIST_PATTERN = Pattern.compile(WHITE_LIST);

  public static int killProcess(Context context) {
    try {
      final ActivityManager manager =
          (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      List<ActivityManager.RunningAppProcessInfo> runningAppList = manager.getRunningAppProcesses();
      int processCount = 0;
      for (ActivityManager.RunningAppProcessInfo info : runningAppList) {
        if (info.uid >= 10000) {
          String[] pkgList = info.pkgList;
          if ((pkgList == null) || (pkgList.length == 0)) {
            continue;
          }
          processCount += 1;

          for (String pkgName : pkgList) {
            if (!WHITE_LIST_PATTERN.matcher(pkgName).find()) {
              manager.killBackgroundProcesses(pkgName);
              android.os.Process.killProcess(info.pid);
            }
          }
        }
      }
      return processCount;
    } catch (Exception e) {
      return 0;
    }
  }

  public static long killProcessWithMemFree(Context context) {
    final ActivityManager activityManager =
        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    final ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
    activityManager.getMemoryInfo(outInfo);
    final long startAvailMem = outInfo == null ? 0 : outInfo.availMem;

    killProcess(context);

    activityManager.getMemoryInfo(outInfo);
    final long endAvailMem = outInfo == null ? 0 : outInfo.availMem;

    return (endAvailMem - startAvailMem);
  }

  public static boolean isProcessRunning(Context ctx, String processName) {
    boolean ret = false;
    ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
    for (ActivityManager.RunningAppProcessInfo app : apps) {
      if (app.processName.equals(processName)) {
        ret = true;
        break;
      }
    }
    apps.clear();
    return ret;
  }

  /****
   * sdk>=2.2
   ******/
  public static boolean hasFroyo() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
  }

  /****
   * sdk>=2.3.3
   ******/
  public static boolean hasGingerbreadMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
  }

  /****
   * sdk>=3.0
   ******/
  public static boolean hasHoneycomb() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
  }

  /****
   * sdk>=3.1
   ******/
  public static boolean hasHoneycombMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
  }

  /****
   * sdk>=4.0
   ******/
  public static boolean hasICS() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
  }

  /****
   * sdk>=4.1
   ******/
  public static boolean hasJellyBean() {
    return Build.VERSION.SDK_INT >= 16/* (Build.VERSION_CODES.JELLY_BEAN) */;
  }

  /****
   * sdk>=4.2
   ******/
  public static boolean hasJellyBeanMR1() {
    return Build.VERSION.SDK_INT >= 17/* (Build.VERSION_CODES.JELLY_BEAN_MR1) */;
  }

  public static boolean hasJELLY_BEAN_MR2() {
    return Build.VERSION.SDK_INT >= 18;
  }

  public static boolean hasKITKAT() {
    return Build.VERSION.SDK_INT >= 19;
  }

  /**
   * 是否是5.0以上的系统(包含)
   */
  public static boolean hasLOLLIPOP() {
    return Build.VERSION.SDK_INT >= 21;
  }

  /**
   * 是否是5.1以上的系统(包含)
   */
  public static boolean hasLOLLIPOP_MR1() {
    return Build.VERSION.SDK_INT >= 22;
  }

  public static boolean isTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
  }

  public static boolean isHoneycombTablet(Context context) {
    return hasHoneycomb() && isTablet(context);
  }

  public static boolean isGingerbread() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
        && Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1;
  }
}
