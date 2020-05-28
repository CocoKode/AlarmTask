package com.lvdy.android.alarmtask;

import android.util.Log;

public class Logger {

    private static String LOG_TAG = "AlarmTask";

    public static void setTag(String tag) {
        LOG_TAG = tag;
    }

    public static void d(String s) {
        Log.d(LOG_TAG, s);
    }
}
