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
import com.moc.smartmeterapp.model.EntryObject;

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

    private MeterDataSource meterDataSource;
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

        //new HttpRequestTask().execute();

        Button restButton = (Button) view.findViewById(R.id.button_rest);
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new HttpRequestTask().execute();
                Observable.interval(1, 3, TimeUnit.SECONDS)
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Long aLong) {
                                restService.getEntryObjectObservable()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<EntryObject>() {

                                            @Override
                                            public void onCompleted() {
                                                Log.d("DEBUG", "onComplete");
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.d("DEBUG", "onError: " + e.getMessage());
                                            }

                                            @Override
                                            public void onNext(EntryObject entryObject) {
                                                Log.d("DEBUG", "onNext: EntryObject " + entryObject.getCurrentEnergy());
                                            }
                                        });
                            }
                        });
            }
        });

//        ListView listView = (ListView) view.findViewById(R.id.listView);
//        RestData test1 = new RestData(4711,1,1,1,1);
//        RestData test2 = new RestData(4711,2,1,1,1);
//        RestData test3 = new RestData(4711,3,1,1,1);
//        RestData test4 = new RestData(4711,4,1,1,1);
//        RestData test5 = new RestData(4711,5,1,1,1);
//        meterDataSource = new MeterDataSource(getActivity().getBaseContext());
//        meterDataSource.openDataBase();
//        RestData dbData = meterDataSource.createRestData(test1);
//        dbData = meterDataSource.createRestData(test2);
//        dbData = meterDataSource.createRestData(test3);
//        dbData = meterDataSource.createRestData(test4);
//        dbData = meterDataSource.createRestData(test5);
//        listView.setAdapter(showAllDBEntries());
//        meterDataSource.closeDataBase();
    }

    private ArrayAdapter showAllDBEntries(){
        List<RestData> dataList = meterDataSource.getAllRestData();

        ArrayAdapter<RestData> restDataArrayAdapter = new ArrayAdapter<RestData>(
                getActivity().getBaseContext(),
                android.R.layout.simple_list_item_multiple_choice,
                dataList);
        return restDataArrayAdapter;
    }
}
