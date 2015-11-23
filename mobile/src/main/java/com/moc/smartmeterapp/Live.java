package com.moc.smartmeterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Live extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_live,frameLayout);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_live);
//        Intent intent = getIntent();
//        String msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//
//        TextView textView = new TextView(this);
//        textView.setTextSize(40);
//        textView.setText(msg);
//
//        setContentView(textView);
//    }

}
