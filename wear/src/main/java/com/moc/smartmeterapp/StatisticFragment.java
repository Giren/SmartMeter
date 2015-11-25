package com.moc.smartmeterapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by David on 23.11.2015.
 */
public class StatisticFragment extends CustomFragment {

    String fragmentName;
    TextView tv;
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
        tv.setText( getArguments().getString( "msg"));

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
        tv.setText(update);
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
