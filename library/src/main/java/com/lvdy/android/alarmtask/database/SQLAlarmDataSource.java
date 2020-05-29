package com.lvdy.android.alarmtask.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lvdy.android.alarmtask.Logger;
import com.lvdy.android.alarmtask.alarm.Alarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SQLAlarmDataSource extends SQLiteOpenHelper implements IAlarmDataSource {

    private static final String DB_NAME = "alarm.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "t_alarms";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_PERIOD = "period";
    private static final String COLUMN_ALARM_ACTION = "alarm_action";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    "( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TIME + " INTEGER NOT NULL," +
                    COLUMN_PERIOD + " INTEGER NOT NULL," +
                    COLUMN_ALARM_ACTION + " TEXT NOT NULL" +
                    " )";

    SQLAlarmDataSource(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Logger.d("Database updated from " + oldVersion + " to " + newVersion);
        final String deleteSql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(deleteSql);
        onCreate(sqLiteDatabase);
    }


    @Override
    public void insert(Alarm alarmInstance) {
        final SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TIME, alarmInstance.getTime());
        contentValues.put(COLUMN_PERIOD, alarmInstance.getPeriod());
        contentValues.put(COLUMN_ALARM_ACTION, alarmInstance.getAlarmAction());
        final long id = database.insert(TABLE_NAME, null, contentValues);
        alarmInstance.setId(id);
    }

    @Override
    public void delete(Alarm alarmInstance) {
        final SQLiteDatabase database = getWritableDatabase();
        final Long id = alarmInstance.getId();
        final String whereArgs;
        if (id != null) {
            whereArgs = "id = " + id;
        } else {
            whereArgs = String.format(Locale.CHINA,
                    "time = %d AND period = %d AND alarm_action = \"%s\"",
                    alarmInstance.getTime(),
                    alarmInstance.getPeriod(),
                    alarmInstance.getAlarmAction());
        }
        final String deleteSql = "DELETE FROM " + TABLE_NAME + " WHERE " + whereArgs + ";";
        database.execSQL(deleteSql);
    }

    @Override
    public Alarm queryAlarmById(long id) {
        final SQLiteDatabase database = getReadableDatabase();
        final String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?;";
        List<Alarm> alarmList = getAlarms(database.rawQuery(querySql, new String[] {String.valueOf(id)}));
        return alarmList.isEmpty() ? null : alarmList.get(0);
    }

    @Override
    public List<Alarm> queryAlarmByAction(String action) {
        final SQLiteDatabase database = getReadableDatabase();
        final String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE alarm_action = ?;";
        return getAlarms(database.rawQuery(querySql, new String[] {action}));
    }

    @Override
    public void update(Alarm alarmInstance) {
        final SQLiteDatabase database = getWritableDatabase();
        final Long id = alarmInstance.getId();
        if (id != null) {
            final String setArg = String.format(Locale.CHINA,
                    " SET time = %d, period = %d, alarm_action = \"%s\"",
                    alarmInstance.getTime(),
                    alarmInstance.getPeriod(),
                    alarmInstance.getAlarmAction());
            final String updateSql = "UPDATE " + TABLE_NAME + setArg + " WHERE id = " + id + ";";
            database.execSQL(updateSql);
        }
    }

    @Override
    public void clear() {
        final SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME);
    }

    @Override
    public List<Alarm> loadAll() {
        final SQLiteDatabase database = getReadableDatabase();
        final String querySql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(querySql, null);
        return getAlarms(cursor);
    }

    private List<Alarm> getAlarms(Cursor c) {
        if (c == null) {
            return Collections.emptyList();
        }

        List<Alarm> alarmList = null;
        try (final Cursor cursor = c) {
            final int alarmCount = cursor.getCount();
            if (alarmCount > 0) {
                alarmList = new ArrayList<>(alarmCount);
                final int columnCount = cursor.getColumnCount();

                boolean hasNext = cursor.moveToFirst();
                while (hasNext) {
                    Alarm alarm = new Alarm();

                    for (int j = 0; j < columnCount; j++) {
                        final String name = cursor.getColumnName(j);
                        switch (name) {
                            case COLUMN_ID:
                                alarm.setId(cursor.getLong(j));
                                continue;
                            case COLUMN_TIME:
                                alarm.setTime(cursor.getLong(j));
                                continue;
                            case COLUMN_PERIOD:
                                alarm.setPeriod(cursor.getLong(j));
                                continue;
                            case COLUMN_ALARM_ACTION:
                                alarm.setAlarmAction(cursor.getString(j));
                                continue;
                        }
                    }

                    alarmList.add(alarm);
                    hasNext = cursor.moveToNext();
                }

            } else {
                alarmList = Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return alarmList;
    }
}
