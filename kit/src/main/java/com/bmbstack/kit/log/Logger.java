package com.bmbstack.kit.log;

import android.text.TextUtils;

import com.bmbstack.kit.app.Client;

import java.io.File;

public class Logger extends BaseLog {

    public static final String DEFAULT_MESSAGE = "execute";
    public static final String NULL_TIPS = "Log with null object";
    public static final String PARAM = "Param";
    public static final String NULL = "null";

    public static void v() {
        printLog(LogType.V, null, DEFAULT_MESSAGE);
    }

    public static void v(Object msg) {
        printLog(LogType.V, null, msg);
    }

    public static void v(String tag, Object... objects) {
        printLog(LogType.V, tag, objects);
    }

    public static void vv(String tag, String msg) {
        printLog(LogType.V, tag, attachEnvironments(msg));
    }

    public static void d() {
        printLog(LogType.D, null, DEFAULT_MESSAGE);
    }

    public static void d(Object msg) {
        printLog(LogType.D, null, msg);
    }

    public static void d(String tag, Object... objects) {
        printLog(LogType.D, tag, objects);
    }

    public static void dd(String tag, String msg) {
        printLog(LogType.D, tag, attachEnvironments(msg));
    }

    public static void i() {
        printLog(LogType.I, null, DEFAULT_MESSAGE);
    }

    public static void i(Object msg) {
        printLog(LogType.I, null, msg);
    }

    public static void i(String tag, Object... objects) {
        printLog(LogType.I, tag, objects);
    }

    public static void ii(String tag, String msg) {
        printLog(LogType.I, tag, attachEnvironments(msg));
    }

    public static void w() {
        printLog(LogType.W, null, DEFAULT_MESSAGE);
    }

    public static void w(Object msg) {
        printLog(LogType.W, null, msg);
    }

    public static void w(String tag, Object... objects) {
        printLog(LogType.W, tag, objects);
    }

    public static void ww(String tag, String msg) {
        printLog(LogType.W, tag, attachEnvironments(msg));
    }

    public static void e() {
        printLog(LogType.E, null, DEFAULT_MESSAGE);
    }

    public static void e(Object msg) {
        printLog(LogType.E, null, msg);
    }

    public static void e(String tag, Object... objects) {
        printLog(LogType.E, tag, objects);
    }

    public static void ee(String tag, String msg) {
        printLog(LogType.E, tag, attachEnvironments(msg));
    }

    public static void a() {
        printLog(LogType.A, null, DEFAULT_MESSAGE);
    }

    public static void a(Object msg) {
        printLog(LogType.A, null, msg);
    }

    public static void a(String tag, Object... objects) {
        printLog(LogType.A, tag, objects);
    }

    public static void aa(String tag, String msg) {
        printLog(LogType.A, tag, attachEnvironments(msg));
    }

    public static void json(String jsonFormat) {
        printLog(LogType.JSON, null, jsonFormat);
    }

    public static void json(String tag, String jsonFormat) {
        printLog(LogType.JSON, tag, jsonFormat);
    }

    public static void xml(String xml) {
        printLog(LogType.XML, null, xml);
    }

    public static void xml(String tag, String xml) {
        printLog(LogType.XML, tag, xml);
    }

    public static void file(File targetDirectory, Object msg) {
        printFile(null, targetDirectory, null, msg);
    }

    public static void file(LogType tag, File targetDirectory, Object msg) {
        printFile(tag, targetDirectory, null, msg);
    }

    public static void file(LogType tag, File targetDirectory, String fileName, Object msg) {
        printFile(tag, targetDirectory, fileName, msg);
    }

    /**
     * @param type    -
     * @param tagStr
     * @param objects
     */
    private static void printLog(LogType type, String tagStr, Object... objects) {

        if (!LogConstants.IS_SHOW_LOG) {
            return;
        }
        if (TextUtils.isEmpty(tagStr)) {
            tagStr = Client.APP_PKG_NAME;
        }

        // 由mLoglevel控制输出级别
        if (type.intValue() < LogConstants.LOG_LEVEL.intValue()) {
            return;
        }

        String[] contents = wrapperContent(tagStr.toString(), objects);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];

        switch (type) {
            case V:
            case D:
            case I:
            case W:
            case E:
            case A:
                printDefault(type, tag, headString + msg);
                break;
            case JSON:
                JsonLog.printJson(tag, msg, headString);
                break;
            case XML:
                XmlLog.printXml(tag, msg, headString);
                break;
        }
    }

    private static void printFile(LogType type, File targetDirectory, String fileName, Object objectMsg) {

        if (!LogConstants.IS_SHOW_LOG) {
            return;
        }

        if (type.intValue() < LogConstants.LOG_LEVEL.intValue()) {
            return;
        }

        String[] contents = wrapperContent(type.toString(), objectMsg);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];

        FileLog.printFile(tag, targetDirectory, fileName, headString, msg);
    }

    private static String[] wrapperContent(String tagStr, Object... objects) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 5;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        String methodNameShort = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#")
                .append(methodNameShort).append(" ] ");

        String tag = (tagStr == null) ? className : tagStr;
        String msg = (objects == null) ? NULL_TIPS : getObjectsString(objects);
        String headString = stringBuilder.toString();

        return new String[]{tag, msg, headString};
    }

    private static String getObjectsString(Object... objects) {

        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if (object == null) {
                    stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ").append(NULL)
                            .append("\n");
                } else {
                    stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ")
                            .append(object.toString()).append("\n");
                }
            }
            return stringBuilder.toString();
        } else {
            Object object = objects[0];
            return object == null ? NULL : object.toString();
        }
    }

    private static String attachEnvironments(String msg) {
        StackTraceElement stackTraceElement = new Throwable().fillInStackTrace().getStackTrace()[2];
        return new StringBuilder()
                .append("{")
                .append("Thread:" + Thread.currentThread().getName() + ",")
                .append(stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName() + ":"
                        + stackTraceElement.getLineNumber()).append("} - ").append(msg).toString();
    }

}
