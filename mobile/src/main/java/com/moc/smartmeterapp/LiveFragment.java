package com.moc.smartmeterapp;

/**
 * Created by michael on 23.11.15.
 */
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class LiveFragment extends Fragment {

    private MeterView meterView;
    private Limiter limiter;
    private Limit limitRed;
    private Limit limitYellow;
    private SeekBar seekBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.live_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        limitRed = new Limit(2000, 2500, Color.RED);
        limitYellow = new Limit(1500, 2000, Color.YELLOW);

        limiter = new Limiter();
        limiter.addLimit(limitYellow);
        limiter.addLimit(limitRed);

        meterView = (MeterView)view.findViewById(R.id.meterview);
        meterView.setMax(2500);
        meterView.setOffsetAngle(45);
        meterView.setAverage(750);
        meterView.setLimiter(limiter);

        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
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
                meterView.setValue(seekBar.getProgress());
            }
        });
    }
}
