package com.moc.smartmeterapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by David on 23.11.2015.
 */
public class LiveFragment extends CustomFragment {

    String fragmentName;
    boolean userVisible;

    private MeterView_Wear meterView;
    private Limiter limiter;
    private Limit limit1;
    private Limit limit2;
    private Limit limit3;

    private int meterViewMax = 1000;
    private final int FACTOR = ( ( 3600 * 1000) / ( 31 * 24 * 900) );

    private Vibrator vibrator;
    private long[] vibrationPattern = {0, 500, 50, 300};
    private final int indexInPatternToRepeat = -1;

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState);
        vibrator = ( Vibrator) view.getContext().getSystemService( view.getContext().VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_frag, container, false);

        limit1 = new Limit(900, 1250, 0);
        limit2 = new Limit(250, 900, 0);
        limit3 = new Limit(0,250, 0);

//        limit1 = ( ( MainActivity)getActivity()).fragmentData.limit1;
//        limit2 = ( ( MainActivity)getActivity()).fragmentData.limit2;
//        limit3 = ( ( MainActivity)getActivity()).fragmentData.limit3;

        limit1.setEventHandler(new Limit.ILimitEventHandler() {
            @Override
            public void onLimitReached( Limit limit, float value) {
                vibrator.vibrate( vibrationPattern, indexInPatternToRepeat);
            }
            @Override
            public void onLimitLeave( Limit limit, float value) {
            }
        });

        limit2.setEventHandler( new Limit.ILimitEventHandler() {
            @Override
            public void onLimitReached( Limit limit, float value) {
                vibrator.vibrate( vibrationPattern, indexInPatternToRepeat);
            }

            @Override
            public void onLimitLeave( Limit limit, float value) {
            }
        });

        limiter = new Limiter();
        limiter.addLimit( limit1);
        limiter.addLimit( limit2);
        limiter.addLimit( limit3);

        meterView = ( MeterView_Wear)view.findViewById( R.id.meterview);
        meterView.setMax( meterViewMax);
        meterView.setOffsetAngle( 45);
        meterView.setTicks( 45, 10);
        meterView.setLimiter( limiter);
        meterView.enableValueText( false);
        meterView.setText( "0 W");
        meterView.setValue( 0);
        meterView.setTicks( 45);

        return view;
    }

    @Override
    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName( String fname) {
        fragmentName = fname;
    }

    @Override
    public void UpdateFragmentContent( String update) {
        Log.d("DEBUG", "LiveFragment - UpdateFragmentContent(): " + update);
        String[] splitted = update.split( ";");

        if( splitted.length == 2) {
            int meterValue = Integer.valueOf( splitted[1]);
            if( meterValue > meterViewMax) {
                meterViewMax = meterValue;
                meterView.setMax(meterViewMax);
            }
            meterView.enableValueText(false);
            meterView.setText( meterValue + " W");
            meterView.setValue( meterValue);
        } else {
            // update limits
            limit1.setMin( Integer.valueOf( splitted[2]) * FACTOR);
            limit1.setMax(Integer.valueOf(splitted[3]) * FACTOR);
            limit1.setColor(Integer.valueOf(splitted[4]));

            limit2.setMin(Integer.valueOf(splitted[6]) * FACTOR);
            limit2.setMax(Integer.valueOf(splitted[7]) * FACTOR);
            limit2.setColor(Integer.valueOf(splitted[8]));

            limit3.setMin(Integer.valueOf(splitted[10]) * FACTOR);
            limit3.setMax(Integer.valueOf(splitted[11]) * FACTOR);
            limit3.setColor(Integer.valueOf(splitted[12]));

            limiter.addLimit( limit1);
            limiter.addLimit( limit2);
            limiter.addLimit( limit3);

            meterView.setLimiter( limiter);
            meterView.invalidate();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.setUserVisible(isVisibleToUser);

        if ( this.getUserVisible()) {
            Log.d( "DEBUG", fragmentName + " is fragment now visible");
            ( ( MainActivity)getActivity()).sendDataToHandheld( fragmentName);
        } else if( !this.getUserVisible()) {
            fragmentName = getArguments().getString("msg");
            Log.d("DEBUG", fragmentName + " is fragment now invisible");
        }
    }

    public boolean getUserVisible() {
        return this.userVisible;
    }

    public void setUserVisible( boolean status) {
        this.userVisible = status;
    }

    public static LiveFragment newInstance( String text) {

        LiveFragment liveFragment = new LiveFragment();
        Bundle bundle = new Bundle();
        bundle.putString( "msg", text);

        liveFragment.setArguments( bundle);

        return liveFragment;
    }
}
