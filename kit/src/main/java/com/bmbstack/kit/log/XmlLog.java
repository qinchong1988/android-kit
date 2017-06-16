package com.bmbstack.kit.log;

import android.util.Log;

public class XmlLog {

    public static void printXml(String tag, String xml, String headString) {

        if (xml != null) {
            xml = headString + "\n" + xml;
        } else {
            xml = headString + Logger.NULL_TIPS;
        }

        LogUtils.printLine(tag, true);
        String[] lines = xml.split(LogConstants.LINE_SEPARATOR);
        for (String line : lines) {
            if (!LogUtils.isEmpty(line)) {
                Log.d(tag, "║ " + line);
            }
        }
        LogUtils.printLine(tag, false);
    }
}
