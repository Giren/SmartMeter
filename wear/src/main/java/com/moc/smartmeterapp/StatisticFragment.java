package com.moc.smartmeterapp;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by David on 23.11.2015.
 */
public class StatisticFragment extends CustomFragment {

    String fragmentName;
    TextView tvCurrentValue;
    TextView tvLimitValue;
    TextView tvLimitPercent;
    TextView tvLimitView;
    ProgressBar pbLimit;
    boolean userVisible;

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.statistic_frag, container, false);

        // get elements
        tvLimitView = ( TextView) v.findViewById( R.id.tvLimitView);
        tvCurrentValue = ( TextView) v.findViewById( R.id.tvCurrentValue);
        tvLimitValue = ( TextView) v.findViewById( R.id.tvLimitValue);
        tvLimitPercent = ( TextView) v.findViewById( R.id.tvLimitPercent);
        pbLimit = ( ProgressBar) v.findViewById( R.id.pbLimit);

        // set fragment name an name in view
        fragmentName = getArguments().getString( "msg");
        tvLimitView.setText( getLimitViewName( fragmentName));

        // configure progressbar
        pbLimit.setMax( 100);
        pbLimit.setProgress( 0);
        pbLimit.setScaleY( 3f);
        //pbLimit.setDrawingCacheBackgroundColor( Color.GREEN);

        // confige current value, limit value, percent value in view
        tvCurrentValue.setText( "0");
        tvLimitValue.setText( "0");
        tvLimitPercent.setText( "0 %");

        Log.d("DEBUG", "StatisticFragment - onCreateView() - " + fragmentName);
        return v;
    }

    public String getLimitViewName( String fragmentName) {
        Log.d("DEBUG", "StatisticFragment - getLimitViewName() for:" + fragmentName);
        switch ( fragmentName) {
            case "limitWeek": {
                Log.d("DEBUG", "StatisticFragment - getLimitViewName() - return: Wochenlimit");
                return "Wochenlimit";
            }
            case "limitMonth": {
                Log.d("DEBUG", "StatisticFragment - getLimitViewName() - return: Monatslimit");
                return "Monatslimit";
            }
            case "limitYear": {
                Log.d("DEBUG", "StatisticFragment - getLimitViewName() - return: Jahreslimit");
                return "Jahreslimit";
            }
            default: {
                Log.d("DEBUG", "StatisticFragment - getLimitViewName() - return: Default");
                return "Default";
            }
        }
    }

    /**
     * @param update
     */
    @Override
    public void UpdateFragmentContent( String update) {
        Log.d("DEBUG", "StatisticFragment - UpdateFragmentContent() - " + update);
        /*
            Format of received string:
            <fragmentName>;<LimitValue>;<CurrentValue>
         */
        String[] msgSplit = update.split(";");

        tvLimitValue.setText( msgSplit[1]);
        tvCurrentValue.setText( msgSplit[2]);

        int percentValue = ( Integer.valueOf( msgSplit[2]) * 100) / Integer.valueOf(msgSplit[1]);
        String limitPercentHelper = String.valueOf( percentValue) + " %";
        tvLimitPercent.setText( limitPercentHelper);
        pbLimit.setProgress( percentValue);
    }

    @Override
    public String getFragmentName() {
        return fragmentName;
    }

    /**
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // set this fragment visible or invisible
        this.setUserVisible( isVisibleToUser);

        if ( this.getUserVisible()) {
            Log.d("DEBUG", "StatisticFragment - setUserVisibleHint() - visible");
            ((MainActivity)getActivity()).sendDataToHandheld( fragmentName);
        } else if( !this.getUserVisible()) {
            Log.d("DEBUG", "StatisticFragment - setUserVisibleHint() - invisible");
            fragmentName = getArguments().getString("msg");
        }
        Log.d("DEBUG", "StatisticFragment - setUserVisibleHint()");
    }

    public boolean getUserVisible() {
        return this.userVisible;
    }

    public void setUserVisible( boolean status) {
        this.userVisible = status;
    }

    public static StatisticFragment newInstance(String text) {

        StatisticFragment f = new StatisticFragment();
        Bundle b = new Bundle();
        b.putString( "msg", text);

        f.setArguments(b);

        return f;
    }
}
