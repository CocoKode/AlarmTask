package com.lvdy.android.alarmtask.alarm;

import android.content.Context;

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
     * @return 添加结果，返回true表示闹钟设置成功
     */
    public boolean addAlarm(Context context, Alarm alarm) {
        setupAlarmInstance(context, alarm, System.currentTimeMillis());
        return true;
    }

    /**
     * 添加多个闹钟
     *
     * @param context
     * @param alarmList 闹钟实例列表
     * @return 添加失败的闹钟
     */
    public List<Alarm> addAlarmList(Context context, List<Alarm> alarmList) {
        final long currMillis = System.currentTimeMillis();
        List<Alarm> missedAlarms = new ArrayList<>(alarmList.size());
        for (Alarm instance : alarmList) {
            final boolean success = setupAlarmInstance(context, instance, currMillis);
            if (!success) {
                missedAlarms.add(instance);
            }
        }

        return missedAlarms;
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
     * @return 返回true表示设置成功
     */
    private boolean setupAlarmInstance(Context context, Alarm alarm, long currentMillis) {
        Alarm newInstance = alarm.createInstanceAfter(Calendar.getInstance(Locale.CHINA));
        if (!alarm.isPeriodic() && newInstance.getTime() <= currentMillis) {
            return false;
        }

        AlarmStateManager.registerInstance(context, newInstance);
        return true;
    }
}
