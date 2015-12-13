package com.moc.smartmeterapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by michael on 13.12.15.
 */
public class MeterDbHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "MeterDB";
    private static final int DB_VERSION = 1;

    public static final String TABLE_METER_LIST = "meter_list";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ENERGY = "energy";
    public static final String COLUMN_T1 = "t1";
    public static final String COLUMN_T2 = "t2";
    public static final String COLUMN_CURRENT_ENERGY = "currentEnergy";

    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_METER_LIST + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ENERGY + " INTEGER NOT NULL, " +
            COLUMN_T1 + " INTEGER NOT NULL, " +
            COLUMN_T2 + " INTEGER NOT NULL, " +
            COLUMN_CURRENT_ENERGY + " INTEGER NOT NULL);";

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
}
