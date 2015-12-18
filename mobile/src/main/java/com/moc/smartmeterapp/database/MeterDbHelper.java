package com.moc.smartmeterapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.moc.smartmeterapp.model.Day;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 13.12.15.
 */
public class MeterDbHelper extends SQLiteOpenHelper implements IDatabase{

    private static final String DB_NAME = "MeterDB";
    private static final int DB_VERSION = 1;

    public static final String TABLE_METER_LIST = "meter_list";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DAY_O = "day_o";

    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_METER_LIST + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT NOT NULL, " +
            COLUMN_DAY_O + " BLOB);";


    public MeterDbHelper(Context context){
        super(context, DB_NAME ,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("MeterDbHelper", "Database created");
            db.execSQL(SQL_CREATE);
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
        return null;
    }

    @Override
    public List<Day> loadMonth(Date date) {
        return null;
    }

    @Override
    public List<Day> loadYear(Date date) {
        return null;
    }

    @Override
    public void saveDay(Day day) {

    }

    @Override
    public void saveMonth(List<Day> days) {

    }

    @Override
    public void saveYear(List<Day> days) {

    }

    @Override
    public void deleteDay(Date date) {

    }

    @Override
    public void deleteMonth(Date date) {

    }

    @Override
    public void deleteYear(Date date) {

    }

    @Override
    public void deleteAll() {

    }
}
