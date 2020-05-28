package com.lvdy.android.alarmtask.database;

import com.lvdy.android.alarmtask.alarm.Alarm;

import java.util.List;

public interface IAlarmDataSource {

    public void insert(Alarm alarmInstance);

    public void delete(Alarm alarmInstance);

    public Alarm queryAlarmById(long id);

    public List<Alarm> queryAlarmByAction(String action);

    public void update(Alarm alarmInstance);

    public void clear();

    public List<Alarm> loadAll();
}
