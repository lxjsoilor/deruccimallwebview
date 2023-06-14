package com.derucci.deruccimallwebview.utils;

import android.util.Log;

/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/6/14
 */
public class Logger {
    private static String TAG_LOG = "DERUCCI-TAG";

    public static void init(String tag) {
        TAG_LOG = tag;
    }

    public static void i(String msg) {
        Log.i(TAG_LOG, msg);
    }

    public static void i(int msg) {
        Log.i(TAG_LOG, Integer.toString(msg));
    }

    public static void i(boolean msg) {
        Log.i(TAG_LOG, msg ? "true" : "false");
    }
}
