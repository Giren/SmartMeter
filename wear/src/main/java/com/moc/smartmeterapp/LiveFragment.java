package com.moc.smartmeterapp;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
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
    private Limit limitRed;
    private Limit limitYellow;

    private Vibrator vibrator;
    private long[] vibrationPattern = {0, 500, 50, 300};
    private final int indexInPatternToRepeat = -1;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vibrator = (Vibrator) view.getContext().getSystemService(view.getContext().VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_frag, container, false);

        limitRed = new Limit(2000, 2500, Color.RED);
        limitRed.setEventHandler(new Limit.ILimitEventHandler() {
            @Override
            public void onLimitReached(Limit limit, float value) {
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            }

            @Override
            public void onLimitLeave(Limit limit, float value) {
            }
        });
        limitYellow = new Limit(1500, 2000, Color.YELLOW);
        limitYellow.setEventHandler(new Limit.ILimitEventHandler() {
            @Override
            public void onLimitReached(Limit limit, float value) {
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            }

            @Override
            public void onLimitLeave(Limit limit, float value) {

            }
        });

        limiter = new Limiter();
        limiter.addLimit(limitYellow);
        limiter.addLimit(limitRed);

        meterView = (MeterView_Wear)view.findViewById(R.id.meterview);
        meterView.setMax(2500);
        meterView.setOffsetAngle(45);
        meterView.setTicks(45, 10);
        meterView.setAverage(450);
        meterView.setLimiter(limiter);
        meterView.enableValueText(false);
        meterView.setTicks(45);

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
        System.out.println("Live UpdateFragmentContent" + update);
        String[] splitted = update.split(";");
        meterView.setValue( Float.valueOf( splitted[1]));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.setUserVisible( isVisibleToUser);

        if ( this.getUserVisible()) {
            System.out.println( "this fragment is now visible");
            ((MainActivity)getActivity()).sendDataToHandheld( fragmentName);
        } else if( !this.getUserVisible()) {
            System.out.println( "this fragment is now invisible");
            fragmentName = getArguments().getString("msg");
        }

        System.out.println("setUserVisibleHint called");
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
