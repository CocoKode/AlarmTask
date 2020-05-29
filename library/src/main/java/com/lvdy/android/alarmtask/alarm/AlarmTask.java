package com.lvdy.android.alarmtask.alarm;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 对外操作类，暴露给用户的方法
 */
public class AlarmTask {

    /**
     * 添加单个闹钟
     *
     * @param context
     * @param alarm 闹钟实例
     * @return 添加结果，返回null表示闹钟设置失败
     */
    public Alarm addAlarm(Context context, Alarm alarm) {
        return setupAlarmInstance(context, alarm, System.currentTimeMillis());
    }

    /**
     * 添加多个闹钟
     *
     * @param context
     * @param alarmList 闹钟实例列表
     * @return 添加成功的闹钟
     */
    public List<Alarm> addAlarmList(Context context, List<Alarm> alarmList) {
        final long currMillis = System.currentTimeMillis();
        List<Alarm> setedAlarms = new ArrayList<>(alarmList.size());
        for (Alarm instance : alarmList) {
            final Alarm alarm = setupAlarmInstance(context, instance, currMillis);
            if (alarm != null) {
                setedAlarms.add(alarm);
            }
        }

        return setedAlarms;
    }

    /**
     * 取消单个闹钟
     *
     * @param context
     * @param alarm 闹钟实例
     * @return 取消结果，返回true表示闹钟取消成功
     */
    public boolean cancelAlarm(Context context, Alarm alarm) {
        return AlarmStateManager.unregisterInstance(context, alarm);
    }

    /**
     * 取消多个闹钟
     *
     * @param context
     * @param alarmList 闹钟实例列表
     * @return 取消失败的闹钟
     */
    public List<Alarm> cancelAlarmList(Context context, List<Alarm> alarmList) {
        List<Alarm> missedAlarms = new ArrayList<>(alarmList.size());
        for (Alarm instance : alarmList) {
            final boolean success = cancelAlarm(context, instance);
            if (!success) {
                missedAlarms.add(instance);
            }
        }

        return missedAlarms;
    }

    /**
     * 设置闹钟
     *
     * @param context
     * @param alarm 闹钟实例
     * @param currentMillis 当前时间毫秒数
     * @return 返回null表示设置失败
     */
    private Alarm setupAlarmInstance(Context context, Alarm alarm, long currentMillis) {
        Alarm newInstance = alarm.createInstanceAfter(Calendar.getInstance(Locale.CHINA));
        if (!alarm.isPeriodic() && newInstance.getTime() <= currentMillis) {
            return null;
        }

        AlarmStateManager.registerInstance(context, newInstance);
        return newInstance;
    }

    public void cancelAllAlarms(Context context) {
        AlarmStateManager.unregisterAllInstance(context);
    }

    public static class Logger {
        public static final boolean DEBUG = true;
        private static String LOG_TAG = "AlarmTask";
        public static void setTag(String tag) {
            LOG_TAG = tag;
        }
        public static void d(String s) {
            if (DEBUG) {
                Log.d(LOG_TAG, s);
            }
        }
    }
}
