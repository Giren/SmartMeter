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
        meterDbHelper = new MeterDbHelper(context);
    }

    private void insertListDataToDB(List<Day> days){
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
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

        System.out.println(day.getDate().getYear());
        return day;
    }

//    public RestData createRestData(RestData restData) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MeterDbHelper.COLUMN_ENERGY, restData.getEnergy());
//        contentValues.put(MeterDbHelper.COLUMN_T1, restData.getT1());
//        contentValues.put(MeterDbHelper.COLUMN_T2, restData.getT2());
//        contentValues.put(MeterDbHelper.COLUMN_CURRENT_ENERGY, restData.getCurrentEnergy());
//
//        long insertID = database.insert(MeterDbHelper.TABLE_METER_LIST, null, contentValues);
//
//        //check RestData-Object
//        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST, columns,
//                MeterDbHelper.COLUMN_ID + "=" + insertID, null, null, null, null);
//        cursor.moveToFirst();
//        RestData data = cursorToMeterData(cursor);
//        cursor.close();
//        return data;
//    }

    public List<Day> getAllDBData(){
        List<Day> dataList = new ArrayList<>();

        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST, columns,
                null,null,null,null,null);
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

    public void openDataBase(){
        database = meterDbHelper.getWritableDatabase();
    }

    public void closeDataBase(){
        meterDbHelper.close();
    }
}
