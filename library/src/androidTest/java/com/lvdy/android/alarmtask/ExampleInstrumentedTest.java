package com.lvdy.android.alarmtask;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.lvdy.android.alarmtask.alarm.Alarm;
import com.lvdy.android.alarmtask.database.DataManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.lvdy.android.alarmtask.test", appContext.getPackageName());
    }

    private Alarm addedAlarm;

    @Before
    public void testAddAlarmToDatabase() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Alarm alarm = new Alarm(System.currentTimeMillis() + 10 * 60 * 1000, "test_alarm");
        DataManager.getInstance(appContext).addAlarm(alarm);
        assertNotNull(alarm.getId());

        addedAlarm = alarm;
    }

    @After
    public void testQueryAddedAlarmById() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final long id = addedAlarm.getId();
        Alarm alarm = DataManager.getInstance(appContext).queryAlarmById(id);
        assertNotNull(alarm);
    }

    @After
    public void testQueryAddedAlarmsByAction() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final String action = addedAlarm.getAlarmAction();
        List<Alarm> alarms = DataManager.getInstance(appContext).queryAlarmByAction(action);
        assertFalse(alarms.isEmpty());
    }


    @Test
    public void testDeleteAlarmFromDatabase() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Alarm alarm = new Alarm(System.currentTimeMillis() + 10 * 60 * 1000, "test_alarm");
        DataManager.getInstance(appContext).addAlarm(alarm);
        List<Alarm> alarmList = DataManager.getInstance(appContext).loadAll();
        int num1 = alarmList.size();

        alarm.setId(null);
        DataManager.getInstance(appContext).deleteAlarm(alarm);
        alarmList = DataManager.getInstance(appContext).loadAll();
        int num2 = alarmList.size();

        assertEquals(num1 - 1,  num2);
    }

    @After
    public void testQueryDeletedAlarmById() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final long id = addedAlarm.getId();
        Alarm alarm = DataManager.getInstance(appContext).queryAlarmById(id);
        assertNotNull(alarm);
    }

    @After
    public void testQueryDeletedAlarmsByAction() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final String action = addedAlarm.getAlarmAction();
        List<Alarm> alarms = DataManager.getInstance(appContext).queryAlarmByAction(action);
        boolean result = alarms.isEmpty();
        assertTrue(result);
    }

    @Test
    public void testAllAlarmNumberEqualsAddedAlarmNumber() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final int totalCount = 3;
        for (int i = 0; i < totalCount; i++) {
            Alarm alarm = new Alarm(System.currentTimeMillis() + 10 * 60 * 1000, "test_alarm");
            DataManager.getInstance(appContext).addAlarm(alarm);
        }

        List<Alarm> alarmList = DataManager.getInstance(appContext).loadAll();
        assertEquals(alarmList.size(), totalCount);
    }

    @Test
    public void testAllAlarmIsEmptyAfterClear() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        DataManager.getInstance(appContext).clear();

        List<Alarm> alarmList = DataManager.getInstance(appContext).loadAll();
        System.out.println(alarmList.size());
        assertTrue(alarmList.isEmpty());
    }
}
