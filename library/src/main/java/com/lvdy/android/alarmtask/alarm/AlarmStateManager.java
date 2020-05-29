package com.lvdy.android.alarmtask.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.lvdy.android.alarmtask.database.DataManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

class AlarmStateManager {

    private static final String INTENT_NAME_ALARM_ID = "alarm_id";

    static void registerInstance(Context context, Alarm instance) {
        DataManager.getInstance(context.getApplicationContext()).addAlarm(instance);
        scheduleInstanceStateChange(context, instance);
    }

    static boolean unregisterInstance(Context context, Alarm instance) {
        if (DataManager.getInstance(context.getApplicationContext()).deleteAlarm(instance)) {
            return cancelScheduledInstanceStateChange(context, instance);
        }

        return false;
    }

    static void handleIntent(Context context, Intent intent) {
        long alarmId = intent.getLongExtra(INTENT_NAME_ALARM_ID, -1);
        Alarm instance = DataManager.getInstance(context.getApplicationContext()).queryAlarmById(alarmId);

        if (instance == null) {
            return;
        }

        deleteInstanceAndUpdateParent(context, instance);

        Intent notify = new Intent(instance.getAlarmAction());
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(notify);
    }

    private static void deleteInstanceAndUpdateParent(Context context, Alarm instance) {
        if (instance != null) {
            unregisterInstance(context, instance);
            if (instance.isPeriodic()) {
                updateParentAlarm(context, instance);
            }
        }
    }

    private static void updateParentAlarm(Context context, Alarm instance) {
        Alarm nextRepeatedInstance = instance.createInstanceAfter(Calendar.getInstance(Locale.CHINA));
        registerInstance(context, nextRepeatedInstance);
    }

    private static Intent createStateChangeIntent(Context context, Alarm instance) {
        Intent intent = Alarm.createIntent(context, AlarmReceiver.class);
        intent.putExtra(INTENT_NAME_ALARM_ID, instance.getId());
        return intent;
    }

    private static void scheduleInstanceStateChange(Context context, Alarm instance) {
        final long timeInMillis = instance.getTime();

        final Intent stateChangeIntent =
                createStateChangeIntent(context, instance);
        stateChangeIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, instance.getId().hashCode(),
                stateChangeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (isMOrLater()) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else  {
            am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }

        AlarmTask.Logger.d("设定闹钟:" + convertMills2UTC(timeInMillis) + "id=" + instance.getId());
    }

    private static boolean cancelScheduledInstanceStateChange(Context context, Alarm instance) {
        if (instance.getId() == null) {
            throw new NullPointerException("闹钟id为空");
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, instance.getId().hashCode(),
                createStateChangeIntent(context, instance), PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.cancel(pendingIntent);
            pendingIntent.cancel();

            AlarmTask.Logger.d("取消闹钟:" + convertMills2UTC(instance.getTime()) + "id=" + instance.getId());
            return true;
        }

        return false;
    }

    private static boolean isMOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private static SimpleDateFormat datefromat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒", Locale.CHINA);
    private static String convertMills2UTC(long millis) {
        return datefromat.format(new Date(millis));
    }

    public static void unregisterAllInstance(Context context) {
        List<Alarm> alarmList = DataManager.getInstance(context.getApplicationContext()).loadAll();
        for (Alarm alarm : alarmList) {
            final boolean result = unregisterInstance(context, alarm);
            if (!result) {
                AlarmTask.Logger.d("闹钟取消失败:" + alarm.getId());
            }
        }
    }
}
