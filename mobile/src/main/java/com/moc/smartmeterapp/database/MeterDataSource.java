package com.moc.smartmeterapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moc.smartmeterapp.model.Day;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by michael on 13.12.15.
 */
public class MeterDataSource {

    private SQLiteDatabase database;
    private MeterDbHelper meterDbHelper;

    private String[] columns = {
                MeterDbHelper.COLUMN_ID,
                MeterDbHelper.COLUMN_DATE,
                MeterDbHelper.COLUMN_DAY_O
    };

    public MeterDataSource(Context context){
        //meterDbHelper = new MeterDbHelper(context);
    }

    public void insertListDataToDB(List<Day> days){
        System.out.println("Gonna put " + days.size() + " objects into Database");
        for(int i=0; i<days.size(); i++){
            insertDataToDB(days.get(i));
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
        return ( date.getYear()+"-"+date.getMonth()+"-"+date.getDay());
    }

    private Day cursorToMeterData(Cursor cursor){
        int dbIndex = cursor.getColumnIndex(MeterDbHelper.COLUMN_ID);
        int dbDate = cursor.getColumnIndex(MeterDbHelper.COLUMN_DATE);
        Long id = cursor.getLong(dbIndex);
        String da = cursor.getString(dbDate);

        System.out.println(">>>>>>>>>> "+da+" <<<<<<<<<");

        byte[] blob = cursor.getBlob(cursor.getColumnIndex(MeterDbHelper.COLUMN_DAY_O));
        String json = new String(blob);
        Gson gson = new Gson();
        Day day = gson.fromJson(json,new TypeToken<Day>(){}.getType());

        return day;
    }

    private List<Day> cursorToMeterList(Cursor cursor){
        System.out.println("Found something: "+cursor.getCount());
        cursor.moveToFirst();
        List<Day> dataList = new ArrayList<>();
        Day day;

        while(!cursor.isAfterLast()){
            day = cursorToMeterData(cursor);
            dataList.add(day);
            cursor.moveToNext();
        }
        return dataList;
    }

    public List<Day> getAllDBData(){
        List<Day> dataList = new ArrayList<>();

        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST, columns,
                null, null, null, null, null);
        cursor.moveToFirst();
        Day day;

        while(!cursor.isAfterLast()){
            day = cursorToMeterData(cursor);
            dataList.add(day);
            cursor.moveToNext();
        }
        cursor.close();

        return dataList;
    }

    public Day getDayFromDataBase(Date date){
        Cursor cursor =
                database.query(MeterDbHelper.TABLE_METER_LIST,
                        columns,
                        MeterDbHelper.COLUMN_DATE + "=?",
                        new String[] { dateToString(date) },
                        null,null,null,null);
        cursor.moveToFirst();

        if(cursor.getCount() > 0){
            Day day = cursorToMeterData(cursor);
            System.out.println("found Date: " + day.getDate().getYear());
            return day;
        }
        System.out.println("No Date found in DataBase");
        return null;
    }

    public List<Day> getMonthFromDataBase(Date date){
        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST,
                columns,
                MeterDbHelper.COLUMN_DATE + " like ?",
                new String[] { String.valueOf(date.getYear())+"-"+String.valueOf(date.getMonth())+"%" },
                null, null, null, null);

        if(cursor.getCount() > 0){
            return cursorToMeterList(cursor);
        }
        System.out.println("Found nothing ........");
        return null;
    }

    public List<Day> getYearFromDataBase(Date date){
        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST,
                columns,
                MeterDbHelper.COLUMN_DATE + " like ?",
                new String[] { String.valueOf(date.getYear())+"%" },
                null, null, null, null);

        if(cursor.getCount() > 0){
            return cursorToMeterList(cursor);
        }
        System.out.println("Found nothing ........");
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
    }

    public void setMeterDbHelper(MeterDbHelper meterDbHelper) {
        this.meterDbHelper = meterDbHelper;
    }
}
