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

import com.moc.smartmeterapp.com.moc.smartmeterapp.communication.Communication;
import com.moc.smartmeterapp.model.Limit;

public class LiveFragment extends Fragment implements Communication.ILiveDataEventHandler {

    private MeterView meterView;
    private Limiter limiter;
    private Limit limitRed;
    private Limit limitYellow;
    private SeekBar seekBar;
    private Communication communication;

    private int meterViewMax = 1000;
    private int meterViewAVG = 300;

    @Override
    public boolean onLiveDataReceived(int value) {
        if (meterView != null) {
            if(value > meterViewMax){
                meterViewMax = value;
                meterView.setMax(meterViewMax);
            }

            meterViewAVG = (meterViewAVG + value) / 2;

            meterView.setAverage(meterViewAVG);
            meterView.setValue(value);
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        communication = new Communication(getActivity(), Communication.LIVE_DATA, Communication.TEST);
        communication.registerDataEventHandler(this);
        communication.bindService();
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
        communication.unregisterDataEventHandler(this);
        communication.unbindService();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        limitRed = new Limit(2000, 2500, Color.RED);
        limitYellow = new Limit(1500, 2000, Color.YELLOW);

        limiter = new Limiter();
        limiter.addLimit(limitYellow);
        limiter.addLimit(limitRed);

        meterView = (MeterView) view.findViewById(R.id.meterview);
        meterView.setOffsetAngle(45);
        meterView.setLimiter(limiter);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setMax(2500);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                meterView.setMax(seekBar.getProgress() + 2500);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
