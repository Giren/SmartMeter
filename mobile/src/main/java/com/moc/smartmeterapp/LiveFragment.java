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
    private Limit limit;
    private SeekBar seekBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.live_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        limit = new Limit(2000, 2500, Color.RED);
        limit.setEventHandler(new Limit.ILimitEventHandler() {
            @Override
            public void onLimitReached(Limit limit, float value) {

            }

            @Override
            public void onLimitLeave(Limit limit, float value) {

            }
        });

        limiter = new Limiter();
        limiter.addLimit(limit);

        meterView = (MeterView)view.findViewById(R.id.meterview);
        meterView.setMax(2500);
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
