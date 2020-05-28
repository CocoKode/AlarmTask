package com.lvdy.android.alarmtask.alarm;

import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Alarm {

    public Alarm(long time, String action) {
        this(null, time, 0, action);
    }

    protected Alarm createInstanceAfter(Calendar time) {
        Calendar nextInstanceTime = getNextAlarmTime(time);
        Alarm nextAlarm = new Alarm(this.id, nextInstanceTime.getTimeInMillis(), this.period, this.alarmAction);
        return nextAlarm;
    }

    private Long id;
    private long time;
    private long period;
    private String alarmAction;


    public Alarm(Long id, long time, long period, String action) {
        this.id = id;
        this.time = time;
        this.period = period;
        this.alarmAction = action;
    }

    public Alarm() { }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAlarmAction() {
        return alarmAction;
    }

    public void setAlarmAction(String alarmAction) {
        this.alarmAction = alarmAction;
    }

    public Calendar getNextAlarmTime(Calendar currentTime) {
        final Calendar nextInstanceTime = Calendar.getInstance(currentTime.getTimeZone());

        final Calendar targetInstanceTime = Calendar.getInstance(Locale.CHINA);
        targetInstanceTime.setTimeInMillis(time);

        nextInstanceTime.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
        nextInstanceTime.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
        nextInstanceTime.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
        nextInstanceTime.set(Calendar.HOUR_OF_DAY, targetInstanceTime.get(Calendar.HOUR_OF_DAY));
        nextInstanceTime.set(Calendar.MINUTE, targetInstanceTime.get(Calendar.MINUTE));
        nextInstanceTime.set(Calendar.SECOND, targetInstanceTime.get(Calendar.SECOND));
        nextInstanceTime.set(Calendar.MILLISECOND, 0);


        if (this.period > 0 && nextInstanceTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
            long nextMills = nextInstanceTime.getTimeInMillis() + this.period;
            nextInstanceTime.setTimeInMillis(nextMills);
        }

//        nextInstanceTime.set(Calendar.HOUR_OF_DAY, targetInstanceTime.get(Calendar.HOUR_OF_DAY));
//        nextInstanceTime.set(Calendar.MINUTE, targetInstanceTime.get(Calendar.MINUTE));
//        nextInstanceTime.set(Calendar.SECOND, targetInstanceTime.get(Calendar.SECOND));
        return nextInstanceTime;
    }

    public static Intent createIntent(Context context, Class<?> cls) {
        return new Intent(context, cls);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPeriod() {
        return this.period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public boolean isPeriodic() {
        return this.period > 0;
    }

    public void setPeriod(TimeUnit timeUnit, int time) {
        setPeriod(timeUnit.toMillis(time));
    }
}
