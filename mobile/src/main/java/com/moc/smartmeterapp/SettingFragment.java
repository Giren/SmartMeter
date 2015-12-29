package com.moc.smartmeterapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;


/**
 * Created by michael on 24.11.15.
 */
public class SettingFragment extends Fragment {

    private static final String INTENT_IDENTIFIER = "PREFERENCES_BROADCAST";
    public static final String MESSAGE_IDENTIFIER = "PREFS";

    private MeterDbHelper meterDbHelper;
    private Intent intent;

    private EditText editLimit1;
    private EditText editLimit1Color;
    private EditText editLimit2;
    private EditText editLimit2Color;
    private EditText editLimit3;
    private EditText editLimit3Color;
    private EditText editIP;

    private Switch syncSwitch;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(meterDbHelper == null){
            meterDbHelper = new MeterDbHelper(getActivity().getBaseContext());
        }

        intent = new Intent(INTENT_IDENTIFIER);

        return inflater.inflate(R.layout.setting_fragment_layout,null,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editLimit1 = (EditText)view.findViewById(R.id.edit_limit_1);
        editLimit1Color = (EditText)view.findViewById(R.id.edit_limit_1_color);
        editLimit2 = (EditText)view.findViewById(R.id.edit_limit_2);
        editLimit2Color = (EditText)view.findViewById(R.id.edit_limit_2_color);
        editLimit3 =  (EditText)view.findViewById(R.id.edit_limit_3);
        editLimit3Color = (EditText)view.findViewById(R.id.edit_limit_3_color);
        editIP = (EditText)view.findViewById(R.id.edit_ip);
        syncSwitch = (Switch)view.findViewById(R.id.sync_switch);

        meterDbHelper.openDatabase();
        MyPreferences pref = meterDbHelper.loadPreferences();
        meterDbHelper.closeDatabase();

        if(pref != null)
            setPreferenceView(pref);

        Button saveButton = (Button) view.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Limit limit1 = new Limit(100,200, Color.BLUE);
                Limit limit2 = new Limit(250,350, Color.RED);
                Limit limit3 = new Limit(1000,2000, Color.GREEN);

                MyPreferences preferences = new MyPreferences(
                        limit1,
                        limit2,
                        limit3,
                        editIP.getText().toString(),
                        syncSwitch.isChecked()
                );
                meterDbHelper.openDatabase();
                meterDbHelper.savePreferences(preferences);
                meterDbHelper.closeDatabase();

                sendBroadcast(preferences);
            }
        });
    }

    private void sendBroadcast(MyPreferences pref){
        intent.putExtra(MESSAGE_IDENTIFIER, pref);
    }

    private void setPreferenceView(MyPreferences pref){
        editLimit1.setText(String.valueOf(pref.getLimit1().getMin())+"-"+String.valueOf(pref.getLimit1().getMax()));
        editLimit1Color.setText(String.valueOf(pref.getLimit1().getColor()));
        editLimit2.setText(String.valueOf(pref.getLimit2().getMin())+"-"+String.valueOf(pref.getLimit2().getMax()));
        editLimit2Color.setText(String.valueOf(pref.getLimit2().getColor()));
        editLimit3.setText(String.valueOf(pref.getLimit3().getMin())+"-"+String.valueOf(pref.getLimit3().getMax()));
        editLimit3Color.setText(String.valueOf(pref.getLimit3().getColor()));
        editIP.setText(pref.getIpAddress());
        syncSwitch.setChecked(pref.getSync());
    }

}
