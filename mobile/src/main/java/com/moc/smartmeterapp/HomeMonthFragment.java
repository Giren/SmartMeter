package com.moc.smartmeterapp;

/**
 * Created by michael on 23.11.15.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moc.smartmeterapp.database.IDatabase;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Day;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;
import com.moc.smartmeterapp.ui.Limiter;
import com.moc.smartmeterapp.ui.MeterView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Observer;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeMonthFragment extends Fragment implements PreferenceHelper.PrefReceive{

    private final float OFFSET = 0.1f;

    private MyPreferences prefs;
    private PreferenceHelper preferenceHelper;

    private MeterView meterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferenceHelper = new PreferenceHelper(getActivity());
        preferenceHelper.register(this);

        prefs = PreferenceHelper.getPreferences(getActivity());

        return inflater.inflate(R.layout.home_month_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        meterView = (MeterView) view.findViewById(R.id.month_meterview);
        meterView.setOffsetAngle(45);
        meterView.setLimiter(getLimiter());

        final IDatabase databaseHelper = new MeterDbHelper(getActivity());
        databaseHelper.openDatabase();
        final Day day = databaseHelper.loadLatestDay();

        if(day != null) {
            Observable.just(databaseHelper.loadMonth(day.getDate()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<List<Day>>() {
                    @Override
                    public void onCompleted() {
                        databaseHelper.closeDatabase();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Day> days) {
                        if(days != null) {
                            int diff = days.get(days.size()-1).getMmm().getTotalSum()-days.get(0).getMmm().getTotalSum();
                            diff /= 10000;
                            meterView.setMax((int) (diff + diff*OFFSET));
                            meterView.setValue(diff);
                            meterView.invalidate();
                        }
                    }
                });
        }
    }

    private Limiter getLimiter() {
        Limiter limiter = new Limiter();
        limiter.addLimit(prefs.getLimit3());
        limiter.addLimit(prefs.getLimit2());
        limiter.addLimit(prefs.getLimit1());
        return limiter;
    }

    @Override
    public void onPrefReceive(MyPreferences pref) {
        this.prefs = pref;
        meterView.setLimiter(getLimiter());
    }
}
