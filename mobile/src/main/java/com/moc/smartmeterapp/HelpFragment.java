package com.moc.smartmeterapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * Created by michael on 24.11.15.
 */
public class HelpFragment extends Fragment{

    private MeterDbHelper meterDbHelper;
    private ComUtils.IRestTestService restService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        restService = ComUtils.createRetrofitService(ComUtils.IRestTestService.class);

        return inflater.inflate(R.layout.help_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button restButton = (Button) view.findViewById(R.id.button_rest);
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date nDate = new Date();
                nDate.setDate(20);
                nDate.setMonth(5);
                nDate.setYear(2016);
                meterDbHelper.openDatabase();
                //Day newDay = meterDbHelper.loadDay(nDate);
                //System.out.println(newDay.getDate().getYear());
                //meterDbHelper.loadMonth(nDate);
                meterDbHelper.loadYear(nDate);
                meterDbHelper.closeDatabase();

//                //new HttpRequestTask().execute();
//                Observable.interval(1, 3, TimeUnit.SECONDS)
//                        .subscribe(new Observer<Long>() {
//                            @Override
//                            public void onCompleted() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onNext(Long aLong) {
//                                restService.getEntryObjectObservable()
//                                        .subscribeOn(Schedulers.io())
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(new Subscriber<EntryObject>() {
//
//                                            @Override
//                                            public void onCompleted() {
//                                                Log.d("DEBUG", "onComplete");
//                                            }
//
//                                            @Override
//                                            public void onError(Throwable e) {
//                                                Log.d("DEBUG", "onError: " + e.getMessage());
//                                            }
//
//                                            @Override
//                                            public void onNext(EntryObject entryObject) {
//                                                Log.d("DEBUG", "onNext: EntryObject " + entryObject.getCurrentEnergy());
//                                            }
//                                        });
//                            }
//                        });
            }
        });


        List<Hour> hours;
        hours = new ArrayList<Hour>();
        for(int i=0; i<24; i++) {
            hours.add(new Hour());
        }


        List<Day> dataList = new ArrayList<>();

        Date date = new Date();
        date.setDate(1);
        date.setMonth(1);
        date.setYear(2016);
        Day day = new Day();
        day.setDate(date);
        dataList.add(day);

        Date date1 = new Date();
        date1.setDate(2);
        date1.setMonth(1);
        date1.setYear(2016);
        Day day1 = new Day();
        day1.setDate(date1);
        dataList.add(day1);

        Date date2 = new Date();
        date2.setDate(3);
        date2.setMonth(1);
        date2.setYear(2016);
        Day day2 = new Day();
        day2.setDate(date2);
        dataList.add(day2);

        ListView listView = (ListView) view.findViewById(R.id.listView);

        meterDbHelper = new MeterDbHelper(getActivity().getBaseContext());
        meterDbHelper.openDatabase();
        meterDbHelper.deleteAll();
        meterDbHelper.saveMonth(dataList);
        listView.setAdapter(showAllDBEntries());
        meterDbHelper.closeDatabase();
    }

    private ArrayAdapter showAllDBEntries(){
        List<Day> dataList = meterDbHelper.getAllEntries();

        ArrayAdapter<Day> restDataArrayAdapter = new ArrayAdapter<Day>(
                getActivity().getBaseContext(),
                android.R.layout.simple_list_item_multiple_choice,
                dataList);
        return restDataArrayAdapter;
    }
}
