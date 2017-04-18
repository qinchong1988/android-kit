package com.bmbstack.kit.log;

import android.util.Log;

public class BaseLog {

    /**
     * 有两个作用： 1、作为Log类型的标识 2、intValue来做输出日志级别控制
     */
    public enum LogType {
        V(0x1), D(0x2), I(0x3), W(0x4), E(0x5), A(0x6), JSON(0x7), XML(0x8), PROTOBUF(0x9);

        private int mType = 0x2; // default is debug.

        LogType(int type) {
            this.mType = type;
        }

        public int intValue() {
            return this.mType;
        }


        @Override
        public String toString() {
            switch (this.mType) {
                case 0x1:
                    return "V";
                case 0x2:
                    return "D";
                case 0x3:
                    return "I";
                case 0x4:
                    return "W";
                case 0x5:
                    return "E";
                case 0x6:
                    return "A";
                case 0x7:
                    return "JSON";
                case 0x8:
                    return "XML";
                case 0x9:
                    return "PROTOBUF";
            }
            return super.toString();
        }
    }

    public static void printDefault(LogType type, String tag, String msg) {

        int index = 0;
        int maxLength = 4000;
        int countOfSub = msg.length() / maxLength;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + maxLength);
                printSub(type, tag, sub);
                index += maxLength;
            }
            printSub(type, tag, msg.substring(index, msg.length()));
        } else {
            printSub(type, tag, msg);
        }
    }

    private static void printSub(LogType type, String tag, String sub) {
        switch (type) {
            case V:
                Log.v(tag, sub);
                break;
            case D:
                Log.d(tag, sub);
                break;
            case I:
                Log.i(tag, sub);
                break;
            case W:
                Log.w(tag, sub);
                break;
            case E:
                Log.e(tag, sub);
                break;
            case A:
                Log.wtf(tag, sub);
                break;
        }
    }

}