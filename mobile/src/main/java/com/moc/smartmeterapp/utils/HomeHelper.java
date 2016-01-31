package com.moc.smartmeterapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.moc.smartmeterapp.database.IDatabase;
import com.moc.smartmeterapp.database.MeterDataSource;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Day;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by michael on 04.01.16.
 */
public class HomeHelper {

    private Context context;

    public static final int WEEK = 0;
    public static final int MONTH = 1;
    public static final int YEAR = 2;

    public HomeHelper(Context context){
        this.context = context;
    }

    public Integer getConsumption(int periodConst) {
        List<Day> days = new ArrayList<Day>();
        int consumption;
        Calendar ca = new GregorianCalendar(2015,10,20); //für Präsi
        //Calendar ca = new GregorianCalendar();

        IDatabase databaseHelper = new MeterDbHelper(context);
        databaseHelper.openDatabase();

        switch (periodConst) {
            case WEEK:
                ca.add(Calendar.DAY_OF_MONTH, 1 - (ca.get(Calendar.DAY_OF_WEEK)));
                for(int i=0; i<7; i++){
                    Day day = databaseHelper.loadDay(ca.getTime());
                    if(day != null){
                        days.add(day);
                    }
                    ca.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case MONTH:
                days = databaseHelper.loadMonth(ca.getTime());
                break;
            case YEAR:
                days = databaseHelper.loadYear(ca.getTime());
                break;
        }

        if (days != null && days.size() > 0) {
            consumption = days.get(days.size() - 1).getMmm().getTotalSum() - days.get(0).getMmm().getTotalSum();
            consumption /= 10000;
            databaseHelper.closeDatabase();
            return consumption;
        }

        databaseHelper.closeDatabase();
        Toast.makeText(context, "Keine Daten für aktuellen Zeitraum", Toast.LENGTH_SHORT).show();
        return null;
    }

}
