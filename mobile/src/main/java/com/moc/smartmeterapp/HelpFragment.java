package com.moc.smartmeterapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.moc.smartmeterapp.database.MeterDataSource;

import com.moc.smartmeterapp.com.moc.smartmeterapp.communication.ComUtils;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Day;
import com.moc.smartmeterapp.model.EntryObject;
import com.moc.smartmeterapp.model.Hour;
import com.moc.smartmeterapp.model.MyPreferences;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.List;

/**
 * Created by michael on 24.11.15.
 */
public class HelpFragment extends Fragment{

    private MeterDbHelper meterDbHelper;
    private ComUtils.IRestTestService restService;

    private EditText text;
    private Day day;
    private Day day1;
    private Day day2;
    private Day day3;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        restService = ComUtils.createRetrofitService(ComUtils.IRestTestService.class);

        return inflater.inflate(R.layout.help_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text = (EditText) view.findViewById(R.id.editText);

        Button restButton = (Button) view.findViewById(R.id.button_rest);
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meterDbHelper.openDatabase();
                //Day newDay = meterDbHelper.loadDay(nDate);
                //meterDbHelper.loadMonth(nDate);
                //meterDbHelper.loadYear(day1.getDate());
                //meterDbHelper.deleteMonth(day1.getDate());
                //meterDbHelper.deleteYear(day.getDate());
                //meterDbHelper.deleteDay(day.getDate());
                //listView.setAdapter(listToArrayadapter(meterDbHelper.loadMonth(day1.getDate())));
                //listView.setAdapter(showAllDBEntries());
                MyPreferences pref = meterDbHelper.loadPreferences();
                text.setText("WeekLimit: "+pref.getWeekLimit()+"\n"+
                        "WeekLimitColor: "+pref.getWeekLimitColor()+"\n"+
                        "MonthLimit: "+ pref.getMonthLimit()+"\n"+
                        "MonthLimitColor: "+pref.getMonthLimitColor()+"\n"+
                        "YearLimit: "+ pref.getYearLimit()+"\n"+
                        "YearLimitColor: "+ pref.getYearLimitColor()+"\n"+
                        "IP Address: "+ pref.getIpAddress()+"\n"+
                        "Notification: "+ pref.getNotification()+"\n");
                meterDbHelper.closeDatabase();
            }
        });


        List<Hour> hours;
        hours = new ArrayList<Hour>();
        for(int i=0; i<24; i++) {
            hours.add(new Hour());
        }


        List<Day> dataList = new ArrayList<>();

//        Calendar calendar1 = new GregorianCalendar(2016,1,2);
//        Calendar calendar2 = new GregorianCalendar(2016,1,15);
//        Calendar calendar3 = new GregorianCalendar(2016,1,27);
//        Calendar calendar4 = new GregorianCalendar(2017,1,3);

        Date date = new Date();
        date.setDate(2);
        date.setMonth(2);
        date.setYear(2016);
        day = new Day();
        day.setDate(date);
        dataList.add(day);

        Date date1 = new Date();
        date1.setDate(15);
        date1.setMonth(1);
        date1.setYear(2016);
        day1 = new Day();
        day1.setDate(date1);
        dataList.add(day1);

        Date date2 = new Date();
        date2.setDate(27);
        date2.setMonth(1);
        date2.setYear(2016);
        day2 = new Day();
        day2.setDate(date2);
        dataList.add(day2);

        Date date3 = new Date();
        date3.setDate(3);
        date3.setMonth(1);
        date3.setYear(2017);
        day3 = new Day();
        day3.setDate(date3);
        dataList.add(day3);

        meterDbHelper = new MeterDbHelper(getActivity().getBaseContext());
        meterDbHelper.openDatabase();
//        meterDbHelper.deleteAll();
//        meterDbHelper.saveDay(day);
//        meterDbHelper.saveDay(day1);
//        meterDbHelper.saveDay(day2);
//        meterDbHelper.saveDay(day3);
//        listView.setAdapter(showAllDBEntries());
        MyPreferences pref = new MyPreferences(100,"#11",100,"#11",100,"#11","10.0.0.1",true);
        //meterDbHelper.savePreferences(pref);
        meterDbHelper.closeDatabase();
    }
}
