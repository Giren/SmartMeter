package com.moc.smartmeterapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 13.12.15.
 */
public class MeterDataSource {

    private SQLiteDatabase database;
    private MeterDbHelper meterDbHelper;

    private String[] columns = {
                MeterDbHelper.COLUMN_ID,
                MeterDbHelper.COLUMN_ENERGY,
                MeterDbHelper.COLUMN_T1,
                MeterDbHelper.COLUMN_T2,
                MeterDbHelper.COLUMN_CURRENT_ENERGY
    };

    public MeterDataSource(Context context){
        meterDbHelper = new MeterDbHelper(context);
    }

//    private RestData cursorToMeterData(Cursor cursor){
//        int dbIndex = cursor.getColumnIndex(MeterDbHelper.COLUMN_ID);
//        int dbEnergy = cursor.getColumnIndex(MeterDbHelper.COLUMN_ENERGY);
//        int dbT1 = cursor.getColumnIndex(MeterDbHelper.COLUMN_T1);
//        int dbT2 = cursor.getColumnIndex(MeterDbHelper.COLUMN_T2);
//        int dbCurrentEnergy = cursor.getColumnIndex(MeterDbHelper.COLUMN_CURRENT_ENERGY);
//
//        Long id = cursor.getLong(dbIndex);
//        int energy = cursor.getInt(dbEnergy);
//        int t1 = cursor.getInt(dbT1);
//        int t2 = cursor.getInt(dbT2);
//        int currentEnergy = cursor.getInt(dbCurrentEnergy);
//
//        return new RestData(id,energy,t1,t2,currentEnergy);
//
//    }
//
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
//
//    public List<RestData> getAllRestData(){
//        List<RestData> dataList = new ArrayList<>();
//
//        Cursor cursor = database.query(MeterDbHelper.TABLE_METER_LIST, columns,
//                null,null,null,null,null);
//        cursor.moveToFirst();
//        RestData restData;
//
//        while(!cursor.isAfterLast()){
//            restData = cursorToMeterData(cursor);
//            dataList.add(restData);
//            cursor.moveToNext();
//        }
//        cursor.close();
//
//        return dataList;
//    }

    public void openDataBase(){
        database = meterDbHelper.getWritableDatabase();
    }

    public void closeDataBase(){
        meterDbHelper.close();
    }
}
