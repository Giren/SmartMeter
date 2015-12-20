package com.moc.smartmeterapp;


import android.graphics.Color;
import android.os.Bundle;
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
    String actualValue;
    String limitValue;
    String limitPercent;
    TextView tvActualValue;
    TextView tvLimitValue;
    TextView tvPercent;
    TextView tv;
    ProgressBar progressBar;
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

        tv = ( TextView) v.findViewById( R.id.tvStatisticFrag);
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

        tvActualValue = ( TextView) v.findViewById( R.id.tvActualValue);
        tvLimitValue = ( TextView) v.findViewById( R.id.tvLimitValue);
        tvPercent = ( TextView) v.findViewById( R.id.tvLimitPercent);

        tvLimitValue.setText( "100");

        progressBar = ( ProgressBar) v.findViewById( R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setScaleY(3f);
        progressBar.setDrawingCacheBackgroundColor(Color.GREEN);


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
        System.out.println("statistic onCreate");

        return v;
    }

    /**
     * @param update
     */
    @Override
    public void UpdateFragmentContent( String update) {
        System.out.println(" Statistic UpdateFragmentContent" + update);
        String[] msgSplit = update.split(";");

        tvLimitValue.setText( msgSplit[1]);
        tvActualValue.setText(  msgSplit[2]);

        int percenthelp = ( Integer.valueOf( msgSplit[2]) * 100) / Integer.valueOf(msgSplit[1]);
        tvPercent.setText( String.valueOf( percenthelp));
        progressBar.setProgress( percenthelp);

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

    public static StatisticFragment newInstance(String text) {

        StatisticFragment f = new StatisticFragment();
        Bundle b = new Bundle();
        b.putString( "msg", text);

        f.setArguments(b);

        return f;
    }
}
