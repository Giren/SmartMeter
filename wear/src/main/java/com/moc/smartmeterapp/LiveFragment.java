package com.moc.smartmeterapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by David on 23.11.2015.
 */
public class LiveFragment extends CustomFragment {

    String fragmentName;
    TextView tv;
    boolean userVisible;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.live_frag, container, false);

        tv = (TextView) v.findViewById(R.id.tvLiveFrag);
        tv.setText(getArguments().getString("msg"));


        System.out.println("live onCreate");

        return v;
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
        tv.setText(update);
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
