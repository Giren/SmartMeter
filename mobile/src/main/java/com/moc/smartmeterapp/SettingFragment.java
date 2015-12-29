package com.moc.smartmeterapp;

import android.content.Intent;
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
import com.moc.smartmeterapp.model.MyPreferences;
import com.moc.smartmeterapp.utils.HSVColorPickerDialog;


/**
 * Created by michael on 24.11.15.
 */
public class SettingFragment extends Fragment {

    private MeterDbHelper meterDbHelper;

    private EditText editWeek;
    private EditText editWeekColor;
    private EditText editMonth;
    private EditText editMonthColor;
    private EditText editYear;
    private EditText editYearColor;
    private EditText editIP;

    private Switch syncSwitch;

    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(meterDbHelper == null){
            meterDbHelper = new MeterDbHelper(getActivity().getBaseContext());
        }

        return inflater.inflate(R.layout.setting_fragment_layout,null,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = (Button)view.findViewById(R.id.main_limit_color_button);
        /*editWeekColor = (EditText)view.findViewById(R.id.edit_week_color);
        editMonth = (EditText)view.findViewById(R.id.edit_month);
        editMonthColor = (EditText)view.findViewById(R.id.edit_month_color);
        editYear =  (EditText)view.findViewById(R.id.edit_year);
        editYearColor = (EditText)view.findViewById(R.id.edit_year_color);
        editIP = (EditText)view.findViewById(R.id.edit_ip);
        syncSwitch = (Switch)view.findViewById(R.id.sync_switch);
        */

       // meterDbHelper.openDatabase();
        //MyPreferences pref = meterDbHelper.loadPreferences();
        //meterDbHelper.closeDatabase();

        //if(pref != null)
            //setPreferenceView(pref);

        /*Button saveButton = (Button) view.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPreferences preferences = new MyPreferences(
                        Integer.parseInt(editWeek.getText().toString()),
                        editWeekColor.getText().toString(),
                        Integer.parseInt(editMonth.getText().toString()),
                        editMonthColor.getText().toString(),
                        Integer.parseInt(editYear.getText().toString()),
                        editYearColor.getText().toString(),
                        editIP.getText().toString(),
                        syncSwitch.isChecked()
                );
                meterDbHelper.openDatabase();
                meterDbHelper.savePreferences(preferences);
                meterDbHelper.closeDatabase();
            }
        });*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*HSVColorPickerDialog cpd = new HSVColorPickerDialog( getActivity(), 0xFF4488CC, new HSVColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void colorSelected(Integer color) {
                        // Do something with the selected color
                    }
                });
                cpd.setTitle( "Pick a color" );
                cpd.show();*/
            }
        });
    }

    private void setPreferenceView(MyPreferences pref) {
        editWeek.setText(String.valueOf(pref.getWeekLimit()));
        editWeekColor.setText(pref.getWeekLimitColor());
        editMonth.setText(String.valueOf(pref.getMonthLimit()));
        editMonthColor.setText(pref.getMonthLimitColor());
        editYear.setText(String.valueOf(pref.getYearLimit()));
        editYearColor.setText(pref.getYearLimitColor());
        editIP.setText(pref.getIpAddress());
        syncSwitch.setChecked(pref.getSync());
    }



}
