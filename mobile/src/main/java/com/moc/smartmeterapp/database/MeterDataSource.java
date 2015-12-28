package com.moc.smartmeterapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moc.smartmeterapp.model.Day;
import com.moc.smartmeterapp.model.MyPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by michael on 13.12.15.
 */
public class MeterDataSource {

    private SQLiteDatabase database;
    private MeterDbHelper meterDbHelper;

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
    public static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

    private String[] columns = {
                MeterDbHelper.COLUMN_ID,
                MeterDbHelper.COLUMN_DATE,
                MeterDbHelper.COLUMN_DAY_O
    };

    private String[] prefColumns = {
            MeterDbHelper.COLUMN_PREF_ID,
            MeterDbHelper.COLUMN_PREF_WEEK_LIMIT,
            MeterDbHelper.COLUMN_PREF_WEEK_LIMIT_COLOR,
            MeterDbHelper.COLUMN_PREF_MONTH_LIMIT,
            MeterDbHelper.COLUMN_PREF_MONTH_LIMIT_COLOR,
            MeterDbHelper.COLUMN_PREF_YEAR_LIMIT,
            MeterDbHelper.COLUMN_PREF_YEAR_LIMIT_COLOR,
            MeterDbHelper.COLUMN_PREF_IP,
            MeterDbHelper.COLUMN_PREF_SYNC
    };

    public MeterDataSource(Context context){
        //meterDbHelper = new MeterDbHelper(context);
    }

    public void insertListDataToDB(List<Day> days){
        System.out.println("Gonna put " + days.size() + " objects into Database");
        for(Day d : days) {
            insertDataToDB(d);
        }
    }

    public void insertDataToDB(Day day){
        Gson gson = new Gson();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MeterDbHelper.COLUMN_DATE, dateToString(day.getDate()));
        contentValues.put(MeterDbHelper.COLUMN_DAY_O, gson.toJson(day).getBytes());

        long insertID = database.insert(MeterDbHelper.TABLE_METER_LIST, null, contentValues);
    }

    private String dateToString(Date date){
        return DATE_FORMAT.format(date);
    }

    private String monthToString(Date date){
        return MONTH_FORMAT.format(date);
    }

    private String yearToString(Date date){
        return YEAR_FORMAT.format(date);
    }

    private Day cursorToMeterData(Cursor cursor){
        int dbIndex = cursor.getColumnIndex(MeterDbHelper.COLUMN_ID);
        int dbDate = cursor.getColumnIndex(MeterDbHelper.COLUMN_DATE);
        Long id = cursor.getLong(dbIndex);
        String da = cursor.getString(dbDate);

        byte[] blob = cursor.getBlob(cursor.getColumnIndex(MeterDbHelper.COLUMN_DAY_O));
        String json = new String(blob);
        Gson gson = new Gson();
        Day day = gson.fromJson(json,new TypeToken<Day>(){}.getType());

        return day;
    }

    private List<Day> cursorToMeterList(Cursor cursor){
        if(cursor.moveToFirst()){
            System.out.println("Found something: " + cursor.getCount());
            List<Day> dataList = new ArrayList<>();
            Day day;

            while(!cursor.isAfterLast()){
                day = cursorToMeterData(cursor);
                dataList.add(day);
                cursor.moveToNext();
            }
            cursor.close();
            return dataList;
        }
        System.out.println("Nothing found in DataBase");
        cursor.close();
        return null;
    }

    public Day getDayFromDataBase(Date date){
        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST,
                        columns,
                        MeterDbHelper.COLUMN_DATE + "=?",
                        new String[] { dateToString(date) },
                        null,null,null,null);

        if(cursor.moveToFirst()){
            Day day = cursorToMeterData(cursor);
            System.out.println("found Date: " + dateToString(day.getDate()));
            cursor.close();
            return day;
        }
        System.out.println("No Date found in DataBase");
        cursor.close();
        return null;
    }

    public List<Day> getMonthFromDataBase(Date date){
        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST,
                columns,
                MeterDbHelper.COLUMN_DATE + " like ?",
                new String[] { monthToString(date) +"%" },
                null, null, null, null);

        return cursorToMeterList(cursor);
    }

    public List<Day> getYearFromDataBase(Date date){
        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST,
                columns,
                MeterDbHelper.COLUMN_DATE + " like ?",
                new String[]{ yearToString(date) + "%"},
                null, null, null, null);

        return cursorToMeterList(cursor);
    }

    public List<Day> getAllDBData(){
        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST, columns,
                null, null, null, null, null);

        return cursorToMeterList(cursor);
    }

    public void deleteDayFromDataBase(Date date){
        database.delete(MeterDbHelper.TABLE_METER_LIST,
                MeterDbHelper.COLUMN_DATE + "=?",
                new String[] { dateToString(date) });
    }

    public void deleteMonthFromDataBase(Date date){
        database.delete(MeterDbHelper.TABLE_METER_LIST,
                MeterDbHelper.COLUMN_DATE + " like ?",
                new String[]{ monthToString(date)+"%"} );
    }

    public void deleteYearFromDataBase(Date date){
        database.delete(MeterDbHelper.TABLE_METER_LIST,
                MeterDbHelper.COLUMN_DATE + " like ?",
                new String[]{ yearToString(date)+"%"} );
    }

    public Day getLatestDayFromDB() {
        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST,
                null,
                null,
                null,
                null,
                null,
                MeterDbHelper.COLUMN_DATE+" DESC LIMIT 1");

        if(cursor.moveToFirst()){
            System.out.println("hole letztes Datum aus der DB");
            Day day = cursorToMeterData(cursor);
            cursor.close();
            return day;
        }
        System.out.println("Nichts fefunden");
        return null;
    }




    private MyPreferences cursorToPreferences(Cursor cursor){
        int dbIndex = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_ID);
        int dbWeekLimit = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_WEEK_LIMIT);
        int dbWeekLimitColor = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_WEEK_LIMIT_COLOR);
        int dbMonthLimit = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_MONTH_LIMIT);
        int dbMonthLimitColor = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_MONTH_LIMIT_COLOR);
        int dbYearLimit = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_YEAR_LIMIT);
        int dbYearLimitColor = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_YEAR_LIMIT_COLOR);

        int dbIp = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_IP);
        int dbSync = cursor.getColumnIndex(MeterDbHelper.COLUMN_PREF_SYNC);

        Long id = cursor.getLong(dbIndex);
        int weekLimit = cursor.getInt(dbWeekLimit);
        String weekLimitColor = cursor.getString(dbWeekLimitColor);
        int monthLimit = cursor.getInt(dbMonthLimit);
        String monthLimitColor = cursor.getString(dbMonthLimitColor);
        int yearLimit = cursor.getInt(dbYearLimit);
        String yearLimitColor = cursor.getString(dbYearLimitColor);

        String ip = cursor.getString(dbIp);
        Boolean sync = Boolean.parseBoolean(cursor.getString(dbSync));

        MyPreferences preferences = new MyPreferences(weekLimit,
                weekLimitColor,
                monthLimit,
                monthLimitColor,
                yearLimit,
                yearLimitColor,
                ip,
                sync);

        return preferences;
    }

    public void savePreferences(MyPreferences myPreferences){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MeterDbHelper.COLUMN_PREF_WEEK_LIMIT, myPreferences.getWeekLimit());
        contentValues.put(MeterDbHelper.COLUMN_PREF_WEEK_LIMIT_COLOR, myPreferences.getWeekLimitColor());
        contentValues.put(MeterDbHelper.COLUMN_PREF_MONTH_LIMIT, myPreferences.getMonthLimit());
        contentValues.put(MeterDbHelper.COLUMN_PREF_MONTH_LIMIT_COLOR, myPreferences.getMonthLimitColor());
        contentValues.put(MeterDbHelper.COLUMN_PREF_YEAR_LIMIT, myPreferences.getYearLimit());
        contentValues.put(MeterDbHelper.COLUMN_PREF_YEAR_LIMIT_COLOR, myPreferences.getYearLimitColor());

        contentValues.put(MeterDbHelper.COLUMN_PREF_IP, myPreferences.getIpAddress());
        contentValues.put(MeterDbHelper.COLUMN_PREF_SYNC, String.valueOf(myPreferences.getSync()));

        deleteMeterPref();
        long insertID = database.insert(MeterDbHelper.TABLE_PREFS, null, contentValues);
    }

    public MyPreferences loadPreferences(){
        Cursor cursor = database.query(MeterDbHelper.TABLE_PREFS,
                prefColumns,
                null, null, null, null, null);

        if(cursor.moveToFirst()){
            System.out.println("found Preferences: " + cursor.getCount());
            MyPreferences preferences = cursorToPreferences(cursor);
            cursor.close();
            return preferences;
        }
        System.out.println("No Preferences found in DataBase");
        cursor.close();
        return null;
    }




    public void openDataBase(){
        database = meterDbHelper.getWritableDatabase();
    }

    public void closeDataBase(){
        database.close();
    }

    public void deleteDataBase(){
        database.delete(MeterDbHelper.TABLE_METER_LIST, null, null);
        database.delete(MeterDbHelper.TABLE_PREFS, null, null);
    }

    public void deleteMeterList(){
        database.delete(MeterDbHelper.TABLE_METER_LIST, null, null);
    }

    public void deleteMeterPref(){
        database.delete(MeterDbHelper.TABLE_PREFS, null, null);
    }

    public void setMeterDbHelper(MeterDbHelper meterDbHelper) {
        this.meterDbHelper = meterDbHelper;
    }
}
