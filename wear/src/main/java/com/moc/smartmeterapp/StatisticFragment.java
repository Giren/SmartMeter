package com.moc.smartmeterapp;


import android.graphics.Color;
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
    String currentValue;
    String limitValue;
    String limitPercent;
    TextView tvCurrentValue;
    TextView tvLimitValue;
    TextView tvLimitPercent;
    TextView tv;
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

        tv = ( TextView) v.findViewById( R.id.tvLimitView);
        String localLabel;
        switch ( getArguments().getString( "msg")) {
            case "limitWeek": {
                localLabel = "Wochenlimit";
                break;
            }
            case "limitMonth": {
                localLabel = "Monatslimit";
                break;
            }
            case "limitYear": {
                localLabel = "Jahreslimit";
                break;
            }
            default: {
                localLabel = "Default";
                break;
            }
        }
        tv.setText( localLabel);

        tvCurrentValue = ( TextView) v.findViewById( R.id.tvCurrentValue);
        tvLimitValue = ( TextView) v.findViewById( R.id.tvLimitValue);
        tvLimitPercent = ( TextView) v.findViewById( R.id.tvLimitPercent);

        tvLimitValue.setText( "100");

        pbLimit = ( ProgressBar) v.findViewById( R.id.pbLimit);
        pbLimit.setMax(100);
        pbLimit.setProgress(0);
        pbLimit.setScaleY(3f);
        pbLimit.setDrawingCacheBackgroundColor(Color.GREEN);

        tvCurrentValue.setText( "0");
        tvLimitValue.setText( "0");
        tvLimitPercent.setText( "0 %");


//        getActivity().runOnUiThread( new Runnable() {
//            @Override
//            public void run() {
//                int progressStatus = 0;
//                while( progressStatus < 100) {
//                    progressBar.setProgress( progressStatus);
//                    tvActualValue.setText( String.valueOf( progressStatus));
//                    tvPercent.setText( String.valueOf( progressStatus));
//                    progressStatus++;
//
//                    try {
//                        Thread.sleep( 300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        fragmentName = getArguments().getString("msg");

        Log.d("DEBUG", "StatisticFragment - onCreateView() - " + fragmentName);
        return v;
    }

    /**
     * @param update
     */
    @Override
    public void UpdateFragmentContent( String update) {
        Log.d("DEBUG", "StatisticFragment - UpdateFragmentContent() - " + update);
        /*
            Format des empfangenen Strings:
            <fragmentKürzel>;<LimitValue>;<CurrentValue>
         */
        String[] msgSplit = update.split(";");

        tvLimitValue.setText( msgSplit[1]);
        tvCurrentValue.setText( msgSplit[2]);

        int percenthelp = ( Integer.valueOf( msgSplit[2]) * 100) / Integer.valueOf(msgSplit[1]);
        tvLimitPercent.setText( String.valueOf( percenthelp) + " %");
        pbLimit.setProgress( percenthelp);

        //tv.setText(update);
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
