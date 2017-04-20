package com.bmbstack.kit.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;

import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
    }

    public BaseApplication() {
        super();
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // Uncaught Exception Handler
        LauncherUncaughtExceptionHandler launcherUncaughtExceptionHandler =
                new LauncherUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(launcherUncaughtExceptionHandler);

        Utils.init(this);
    }

    private static class LauncherUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private final Thread.UncaughtExceptionHandler _defaultHandler;

        private final Map<String, String> _infos = new LinkedHashMap<String, String>();
        private final DateFormat _formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

        public LauncherUncaughtExceptionHandler() {
            _defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Logger.e("uncaught exception @ thread " + thread.getId() + ", err: " + ex);
            try {
                collectDeviceInfo(BaseApplication.instance());
                saveCrashInfo2File(ex);
                saveHeapDump2File(ex);
            } catch (Exception e) {
                Logger.e("error writing crash log", "", e);
            } finally {
                _defaultHandler.uncaughtException(thread, ex);
            }
        }

        public void collectDeviceInfo(Context context) {
            _infos.put("PackageName", context.getPackageName());
            _infos.put("VersionName", Client.APP_VERSION_NAME);
            _infos.put("VersionCode", String.valueOf(Client.APP_VERSION_CODE));
            _infos.put("ChannelCode", Client.APP_CHANNEL);
            _infos.put("ProcessName", getCurProcessName(context));
            _infos.put("ThreadName", Thread.currentThread().getName());
            _infos.put("=", "==============================");

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    _infos.put(field.getName(), field.get("").toString());
                } catch (Exception e) {
                }
            }
            _infos.put("==", "=============================");
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

        private String saveCrashInfo2File(Throwable ex) {
            String fileName = null;

            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entry : _infos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            sb.append(writer.toString());
            try {
                fileName = String.format("crash-%s.txt", _formatter.format(new Date()));
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    final String path =
                            Environment.getExternalStorageDirectory() + "/" + BaseApplication.instance()
                                    .getPackageName() + "/log";
                    File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);
                    fos.write(sb.toString().getBytes());
                    fos.close();
                }
            } catch (Exception e) {
                Logger.e("an error occured while writing file..." + e.getMessage());
            }

            return fileName;
        }

        private void saveHeapDump2File(Throwable throwable) {
            if (isOutOfMemoryError(throwable)) {
                try {
                    String fileName = String.format("crash-%s.hprof", _formatter.format(new Date()));
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        final String path =
                                Environment.getExternalStorageDirectory() + "/" + BaseApplication.instance()
                                        .getPackageName() + "/log";
                        File dir = new File(path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        Debug.dumpHprofData(path + File.separator + fileName);
                    }
                } catch (Exception ex) {
                    Logger.e("couldn't dump hprof:" + ex.getMessage());
                }
            }
        }
    }

    private static boolean isOutOfMemoryError(Throwable ex) {
        if (OutOfMemoryError.class.equals(ex.getClass())) {
            return true;
        } else {
            Throwable cause = ex.getCause();
            while (null != cause) {
                if (OutOfMemoryError.class.equals(cause.getClass())) {
                    return true;
                }
                cause = cause.getCause();
            }
        }
        return false;
    }
}