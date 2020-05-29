package com.lvdy.android.alarmtask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.lvdy.android.alarmtask.alarm.Alarm;
import com.lvdy.android.alarmtask.alarm.AlarmTask;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_TEST = "action_test";
    AlarmTask alarmTask;
    Alarm alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TEST);
        manager.registerReceiver(mReceiver, filter);

        Alarm a = new Alarm(System.currentTimeMillis() + 10000, ACTION_TEST);
        a.setPeriod(TimeUnit.MINUTES, 1);
        alarmTask = new AlarmTask();
        alarm = alarmTask.addAlarm(this, a);
        Toast.makeText(MainActivity.this, "设置:" + alarm.getId(), Toast.LENGTH_LONG).show();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, intent.getAction() + "收到了, 取消:" + alarm.getId(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarmTask.cancelAllAlarms(this);
    }
}
