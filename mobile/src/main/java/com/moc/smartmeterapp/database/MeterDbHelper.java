package com.moc.smartmeterapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.moc.smartmeterapp.model.Day;
import com.moc.smartmeterapp.preferences.MyPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by michael on 13.12.15.
 */

public class MeterDbHelper extends SQLiteOpenHelper implements IDatabase{

    private MeterDataSource meterDataSource;

    private final static int STD_YEAR = 2015;

    private static final String DB_NAME = "MeterDB";
    private static final int DB_VERSION = 1;

    public static final String TABLE_METER_LIST = "meter_list";
    public static final String TABLE_PREFS = "meter_pref";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DAY_O = "day_o";

    public static final String COLUMN_PREF_ID = "id";
    public static final String COLUMN_PREF_LIMIT_1 = "limit1";
    public static final String COLUMN_PREF_LIMIT_2 = "limit2";
    public static final String COLUMN_PREF_LIMIT_3 = "limit3";
    public static final String COLUMN_PREF_IP = "ip_address";
    public static final String COLUMN_PREF_AUTO_SYNC = "auto_sync";
    public static final String COLUMN_PREF_UNSYNCED = "unsynced";

    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_METER_LIST + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT NOT NULL, " +
            COLUMN_DAY_O + " BLOB);";

    private static final String SQL_CREATE_PREF = "CREATE TABLE " + TABLE_PREFS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PREF_LIMIT_1 + " BLOB, " +
            COLUMN_PREF_LIMIT_2 + " BLOB, " +
            COLUMN_PREF_LIMIT_3 + " BLOB, " +
            COLUMN_PREF_IP + " TEXT NOT NULL, " +
            COLUMN_PREF_AUTO_SYNC + " TEXT NOT NULL, " +
            COLUMN_PREF_UNSYNCED + " TEXT NOT NULL);";

    public MeterDbHelper(Context context){
        super(context, DB_NAME ,null, DB_VERSION);
        meterDataSource = new MeterDataSource(context);
        meterDataSource.setMeterDbHelper(this);
    }

    @Override
    public void openDatabase(){
        meterDataSource.openDataBase();
    }

    @Override
    public void closeDatabase(){
        meterDataSource.closeDataBase();
    }

    @Override
    public List<Day> getAllEntries(){
        return meterDataSource.getAllDBData();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("MeterDbHelper", "Database created");
            db.execSQL(SQL_CREATE);
            db.execSQL(SQL_CREATE_PREF);
        } catch (Exception e){
            Log.e("MeterDbHelper",e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void createIfNotCreated() {

    }

    @Override
    public Day loadDay(Date date) {
        return meterDataSource.getDayFromDataBase(date);
    }

    @Override
    public Day loadLatestDay() {
        Day day = meterDataSource.getLatestDayFromDB();

        if(day != null)
            return day;

        Calendar calendar = new GregorianCalendar(STD_YEAR, 0, 1);
        day = new Day(calendar.getTime());

        return day;
    }

    @Override
    public List<Day> loadMonth(Date date) {
        return meterDataSource.getMonthFromDataBase(date);
    }

    @Override
    public List<Day> loadYear(Date date) {
        return meterDataSource.getYearFromDataBase(date);
    }

    @Override
    public void saveDay(Day day) {
        meterDataSource.deleteDayFromDataBase(day.getDate()); //to avoid duplicates
        meterDataSource.insertDataToDB(day);
    }

    @Override
    public void saveMonth(List<Day> days) {
        meterDataSource.deleteMonthFromDataBase(days.get(0).getDate());//to avoid duplicates
        meterDataSource.insertListDataToDB(days);
    }

    @Override
    public void saveYear(List<Day> days) {
        meterDataSource.deleteYearFromDataBase(days.get(0).getDate());//to avoid duplicates
        meterDataSource.insertListDataToDB(days);
    }

    @Override
    public void deleteDay(Date date) {
        meterDataSource.deleteDayFromDataBase(date);
    }

    @Override
    public void deleteMonth(Date date) {
        meterDataSource.deleteMonthFromDataBase(date);
    }

    @Override
    public void deleteYear(Date date) {
        meterDataSource.deleteYearFromDataBase(date);
    }

    @Override
    public void deleteAll() {
        System.out.println("deleting.............");
        meterDataSource.deleteDataBase();
    }

    @Override
    public void deleteMeterList() {
        meterDataSource.deleteMeterList();
    }

    @Override
    public void deleteMeterPref() {
        meterDataSource.deleteMeterPref();
    }

    @Override
    public void savePreferences(MyPreferences pref) {
        meterDataSource.savePreferences(pref);
    }

    @Override
    public MyPreferences loadPreferences() {
        return meterDataSource.loadPreferences();
    }
}
