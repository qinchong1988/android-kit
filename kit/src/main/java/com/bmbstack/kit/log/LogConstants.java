package com.bmbstack.kit.log;

import com.bmbstack.kit.app.AppEnv;

public class LogConstants {

    // Log显示Level, >= 这个Level的log才显示
    static final BaseLog.LogType LOG_LEVEL = BaseLog.LogType.V;
    // Log开关
    static final boolean IS_SHOW_LOG = AppEnv.DEBUG;

    /**
     * for JSON format
     */
    static final int JSON_INDENT = 4;
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
}
