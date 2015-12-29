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

import java.util.Date;
import java.util.List;

public class HomeMonthFragment extends Fragment implements PreferenceHelper.PrefReceive{

    private MyPreferences prefs;
    private PreferenceHelper preferenceHelper;

    private MeterView meterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferenceHelper = new PreferenceHelper();

        prefs = PreferenceHelper.getPreferences(getActivity());

        return inflater.inflate(R.layout.home_month_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        meterView = (MeterView) view.findViewById(R.id.month_meterview);
        meterView.setOffsetAngle(45);
        meterView.setLimiter(getLimiter());

        IDatabase databaseHelpter = new MeterDbHelper(getActivity());
        databaseHelpter.openDatabase();
        Day day = databaseHelpter.loadLatestDay();
        databaseHelpter.closeDatabase();

        if(day != null) {
            meterView.setAverage(day.getMmm().getMean());
            meterView.setMax(day.getMmm().getMax());
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
