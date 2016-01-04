package com.moc.smartmeterapp;

/**
 * Created by Philipp Kamps on 23.11.15.
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
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;
import com.moc.smartmeterapp.ui.Limiter;
import com.moc.smartmeterapp.ui.MeterView;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeWeekFragment extends Fragment implements PreferenceHelper.PrefReceive {

    private final float OFFSET = 0.1f;
    private final int FACTOR = 4;

    private MyPreferences prefs;
    private PreferenceHelper preferenceHelper;

    private MeterView meterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferenceHelper = new PreferenceHelper(getActivity());
        preferenceHelper.register(this);

        prefs = PreferenceHelper.getPreferences(getActivity());

        return inflater.inflate(R.layout.home_week_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        meterView = (MeterView) view.findViewById(R.id.week_meterview);
        meterView.setOffsetAngle(45);
        meterView.setLimiter(getLimiter());

        final IDatabase databaseHelper = new MeterDbHelper(getActivity());
        databaseHelper.openDatabase();
        Day day = databaseHelper.loadLatestDay();

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
                            if (days != null) {
                                int diff = days.get(days.size() - 1).getMmm().getTotalSum() - days.get(0).getMmm().getTotalSum();
                                diff /= FACTOR;
                                diff /= 10000;
                                meterView.setMax((int)(diff + diff*OFFSET));
                                meterView.setValue(diff);
                                meterView.invalidate();
                            }
                        }
                    });
        }
    }

    private Limiter getLimiter() {
        Limiter limiter = new Limiter();

        Limit limit3 = prefs.getLimit3();
        limit3.setMax(limit3.getMax()/FACTOR);
        limit3.setMin(limit3.getMin() / FACTOR);

        Limit limit2 = prefs.getLimit2();
        limit2.setMax(limit2.getMax()/FACTOR);
        limit2.setMin(limit2.getMin() / FACTOR);

        Limit limit1 = prefs.getLimit1();
        limit1.setMax(limit1.getMax()/FACTOR);
        limit1.setMin(limit1.getMin()/FACTOR);

        limiter.addLimit(limit3);
        limiter.addLimit(limit2);
        limiter.addLimit(limit1);
        return limiter;
    }

    @Override
    public void onPrefReceive(MyPreferences pref) {
        this.prefs = pref;
        meterView.setLimiter(getLimiter());
    }
}