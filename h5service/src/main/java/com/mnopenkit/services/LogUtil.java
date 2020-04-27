
package com.mnopenkit.services;

import android.text.TextUtils;
import android.util.Log;

/**
 * 带日志文件输入的，又可控开关的日志调试
 */
public class LogUtil {
    private static Boolean MYLOG_SWITCH = BuildConfig.LOG_DEBUG; // 日志文件总开关
    private static char MYLOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息

    public static void w(String tag, Object msg) { // 警告信息
        log(tag, msg.toString(), 'w');
    }

    public static void e(String tag, Object msg) { // 错误信息
        log(tag, msg.toString(), 'e');
    }

    public static void d(String tag, Object msg) {// 调试信息
        log(tag, msg.toString(), 'd');
    }

    public static void i(String tag, Object msg) {//
        log(tag, msg.toString(), 'i');
    }

    public static void v(String tag, Object msg) {
        log(tag, msg.toString(), 'v');
    }

    public static void w(String tag, String text) {
        log(tag, text, 'w');
    }

    public static void e(String tag, String text) {
        log(tag, text, 'e');
    }

    public static void d(String tag, String text) {
        log(tag, text, 'd');
    }

    public static void i(String tag, String text) {
        if (MYLOG_SWITCH) {
            if (TextUtils.isEmpty(text)) {
                log(tag, "null", 'i');
            } else if (text.length() > 2000) {
                segmentedPrint(tag, text, 1000);
            } else {
                log(tag, text, 'i');
            }
        }
    }

    public static void v(String tag, String text) {
        log(tag, text, 'v');
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     * @return void
     * @since v 1.0
     */
    private static void log(String tag, String msg, char level) {
        if (msg == null) {
            msg = "null";
        }
        try {
            if (MYLOG_SWITCH) {
                if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息
                    Log.e(tag, msg);
                } else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                    Log.w(tag, msg);
                } else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                    Log.d(tag, msg);
                } else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                    Log.i(tag, msg);
                } else {
                    Log.v(tag, msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void segmentedPrint(String tag, String allStr, int len) {
        int length = allStr.length();
        int count = length / len;
        int lastYu = length % len;
        Log.i(tag, "========================\n\n");
        for (int i = 0; i < count; i++) {
            String duanStr = allStr.substring(i * len, (i + 1) * len);
            Log.i("", duanStr);
        }
        if (lastYu > 0) {
            String duanStr = allStr.substring(count * len, length);
            Log.i("", duanStr);
        }
        Log.i(tag, "========================\n\n");
    }

}
