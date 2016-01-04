package com.moc.smartmeterapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Day;
import com.moc.smartmeterapp.model.Hour;
import com.moc.smartmeterapp.preferences.MyPreferences;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

/**
 * Created by michael on 24.11.15.
 */
public class HelpFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.help_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
