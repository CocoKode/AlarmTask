package com.lvdy.android.alarmtask.database;

import android.content.Context;
import android.text.TextUtils;

import com.lvdy.android.alarmtask.alarm.Alarm;

import java.util.List;

public class DataManager{
    private static DataManager INSTANCE;
    private static IAlarmDataSource dataSource;
    private static final Object INIT_LOCK = new Object();

    public static DataManager getInstance(Context context){
        return getInstance(context, null);
    }

    public static DataManager getInstance(Context context, IAlarmDataSource dataSource) {
        if (INSTANCE == null) {
            synchronized (INIT_LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new DataManager(context, dataSource);
                }
            }
        }
        return INSTANCE;
    }

    private DataManager(Context context, IAlarmDataSource dataSource) {
        if (dataSource == null) {
            DataManager.dataSource = new SQLAlarmDataSource(context);
        } else {
            DataManager.dataSource = dataSource;
        }
    }

    public synchronized void addAlarm(Alarm instance) {
        dataSource.insert(instance);
    }

    public synchronized void updateAlarm(Alarm alarm) {
        dataSource.update(alarm);
    }

    public synchronized boolean deleteAlarm(Alarm instance) {
        if (instance == null || TextUtils.isEmpty(instance.getAlarmAction())) {
            return false;
        }

        dataSource.delete(instance);
        return true;
    }

    public synchronized Alarm queryAlarmById(long alarmId) {
        return dataSource.queryAlarmById(alarmId);
    }

    public synchronized List<Alarm> queryAlarmByAction(String action) {
        return dataSource.queryAlarmByAction(action);
    }

    public synchronized List<Alarm> loadAll() {
        return dataSource.loadAll();
    }

    public synchronized void clear() {
        dataSource.clear();
    }
}
