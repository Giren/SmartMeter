package com.moc.smartmeterapp;

/**
 * Created by michael on 23.11.15.
 */
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.moc.smartmeterapp.communication.LiveCommunication;
import com.moc.smartmeterapp.communication.RestCommunication;
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;
import com.moc.smartmeterapp.ui.Limiter;
import com.moc.smartmeterapp.ui.MeterView;

public class LiveFragment extends Fragment implements LiveCommunication.ILiveDataEvent, PreferenceHelper.PrefReceive {

    private MyPreferences prefs;
    private PreferenceHelper preferenceHelper;

    private final int FACTOR = ( (3600*1000) / (31*24*900) );

    private MeterView meterView;
    private LiveCommunication liveCommunication;

    private int meterViewMax = 1000;

    @Override
    public void onLiveDataReceived(int value) {
        if (meterView != null) {
            if(value > meterViewMax){
                meterViewMax = value;
                meterView.setMax(meterViewMax);
            }
            meterView.setValue(value);
        }

        Log.i("Info: LiveDataService", "Got LiveData");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        preferenceHelper = new PreferenceHelper(getActivity());
        preferenceHelper.register(this);

        liveCommunication = new LiveCommunication(getActivity());
        liveCommunication.create();
        liveCommunication.registerDataEventHandler(this);

        prefs = PreferenceHelper.getPreferences(getActivity());

        return inflater.inflate(R.layout.live_fragment_layout, null);
    }

    @Override
    public void onResume() {
        Log.d("DEBUG", "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("DEBUG", "onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "onDestroy");
        liveCommunication.destroy();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        meterView = (MeterView) view.findViewById(R.id.meterview);
        meterView.setOffsetAngle(45);
        meterView.setLimiter(getLimiter());
    }

    private Limiter getLimiter() {
        Limiter limiter = new Limiter();

        Limit limit3 = prefs.getLimit3();
        limit3.setMax(limit3.getMax() * FACTOR);
        limit3.setMin(limit3.getMin() * FACTOR);

        Limit limit2 = prefs.getLimit2();
        limit2.setMax(limit2.getMax() * FACTOR);
        limit2.setMin(limit2.getMin() * FACTOR);

        Limit limit1 = prefs.getLimit1();
        limit1.setMax(limit1.getMax() * FACTOR);
        limit1.setMin(limit1.getMin() * FACTOR);

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
