package com.moc.smartmeterapp;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment extends Fragment {

    public final static String EXTRA_MESSAGE = "com.moc.MyFragment.MESSAGE";

    public static final MyFragment newInstance(String message){
        MyFragment myFragment = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        myFragment.setArguments(bdl);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        String message = getArguments().getString(EXTRA_MESSAGE);
        View view = inflater.inflate(R.layout.my_fragment, container, false);
        TextView messageTextView = (TextView) view.findViewById(R.id.textView);
        messageTextView.setText(message);
        return view;
    }
}
