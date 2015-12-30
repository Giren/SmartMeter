package com.moc.smartmeterapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.moc.smartmeterapp.alarm.AlarmReceiver;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;
import com.moc.smartmeterapp.utils.HSVColorPickerDialog;

import java.util.regex.Pattern;


/**
 * Created by michael on 24.11.15.
 */
public class SettingFragment extends Fragment {
    private MeterDbHelper meterDbHelper;

    private EditText primeLimitStart;
    private EditText primeLimitStop;
    private Button primeLimitColorBtn;

    private EditText opt1LimitStart;
    private EditText opt1LimitStop;
    private Button opt1LimitColorBtn;

    private EditText opt2LimitStart;
    private EditText opt2LimitStop;
    private Button opt2LimitColorBtn;

    private EditText editIP;

    private CheckBox syncCheck;
    private Button manualSync;

    private Button saveButton;

    private MyPreferences prefs;
    private PreferenceHelper preferenceHelper;

    private Intent intent;

    private static final Pattern PARTIAl_IP_ADDRESS =
            Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}" +
                    "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(meterDbHelper == null){
            meterDbHelper = new MeterDbHelper(getActivity().getBaseContext());
        }

        intent = new Intent(PreferenceHelper.INTENT_IDENTIFIER);

        prefs = PreferenceHelper.getPreferences(getActivity());
        preferenceHelper = new PreferenceHelper();
        preferenceHelper.register(new PreferenceHelper.PrefReceive() {
            @Override
            public void onPrefReceive(MyPreferences pref) {
                setPreferenceView(prefs);
            }
        });

        return inflater.inflate(R.layout.setting_fragment_layout,null,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        primeLimitStart = (EditText)view.findViewById(R.id.prime_limit_start);
        primeLimitStop = (EditText)view.findViewById(R.id.prime_limit_stop);
        primeLimitColorBtn = (Button)view.findViewById(R.id.prime_limit_color);
        primeLimitColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSVColorPickerDialog dialog = new HSVColorPickerDialog(getActivity(), primeLimitColorBtn.getSolidColor(), new HSVColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void colorSelected(Integer color) {
                        primeLimitColorBtn.setBackgroundColor(color);
                    }
                });

                dialog.setTitle("Wählen Sie eine Farbe");
                dialog.show();
            }
        });

        opt1LimitStart = (EditText)view.findViewById(R.id.opt1_limit_start);
        opt1LimitStop = (EditText)view.findViewById(R.id.opt1_limit_stop);
        opt1LimitColorBtn = (Button)view.findViewById(R.id.opt1_limit_color_button);
        opt1LimitColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSVColorPickerDialog dialog = new HSVColorPickerDialog(getActivity(), opt1LimitColorBtn.getSolidColor(), new HSVColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void colorSelected(Integer color) {
                        opt1LimitColorBtn.setBackgroundColor(color);
                    }
                });

                dialog.setTitle("Wählen Sie eine Farbe");
                dialog.show();
            }
        });

        opt2LimitStart = (EditText)view.findViewById(R.id.opt2_limit_start);
        opt2LimitStop = (EditText)view.findViewById(R.id.opt2_limit_stop);
        opt2LimitColorBtn = (Button)view.findViewById(R.id.opt2_limit_color_button);
        opt2LimitColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSVColorPickerDialog dialog = new HSVColorPickerDialog(getActivity(), opt2LimitColorBtn.getSolidColor(), new HSVColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void colorSelected(Integer color) {
                        opt2LimitColorBtn.setBackgroundColor(color);
                    }
                });

                dialog.setTitle("Wählen Sie eine Farbe");
                dialog.show();
            }
        });

        editIP = (EditText)view.findViewById(R.id.network_ip);
        editIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            private String mPreviousText = "";

            @Override
            public void afterTextChanged(Editable s) {
                if (PARTIAl_IP_ADDRESS.matcher(s).matches()) {
                    mPreviousText = s.toString();
                } else {
                    s.replace(0, s.length(), mPreviousText);
                }
            }
        });
        syncCheck = (CheckBox)view.findViewById(R.id.sync_check);
        syncCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AlarmReceiver alarmReceiver = new AlarmReceiver();
                if(b) {
                    alarmReceiver.setAlarm(getActivity());
                } else {
                    alarmReceiver.cancelAlarm(getActivity());
                }
            }
        });

        saveButton = (Button)view.findViewById(R.id.save_prefs);
        manualSync = (Button)view.findViewById(R.id.manual_sync);
        manualSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmReceiver alarmReceiver = new AlarmReceiver();
                alarmReceiver.doAlarm(getActivity());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Limit limit1 = new Limit(
                        Integer.valueOf(primeLimitStart.getText().toString()),
                        Integer.valueOf(primeLimitStop.getText().toString()),
                        ((ColorDrawable) primeLimitColorBtn.getBackground()).getColor());
                Limit limit2 = new Limit(
                        Integer.valueOf(opt1LimitStart.getText().toString()),
                        Integer.valueOf(opt1LimitStop.getText().toString()),
                        ((ColorDrawable) opt1LimitColorBtn.getBackground()).getColor());
                Limit limit3 = new Limit(
                        Integer.valueOf(opt2LimitStart.getText().toString()),
                        Integer.valueOf(opt2LimitStop.getText().toString()),
                        ((ColorDrawable) opt2LimitColorBtn.getBackground()).getColor());

                MyPreferences preferences = new MyPreferences(
                        limit1,
                        limit2,
                        limit3,
                        editIP.getText().toString(),
                        syncCheck.isChecked()
                );

                PreferenceHelper.setPreferences(getActivity(), preferences);
                PreferenceHelper.limitsToServer(getActivity());
                PreferenceHelper.sendBroadcast(getActivity());

                Toast.makeText(getActivity(), "Gespeichert", Toast.LENGTH_SHORT).show();
            }
        });

        if(prefs != null)
            setPreferenceView(prefs);
    }

    private void setPreferenceView(MyPreferences pref){
        primeLimitStart.setText(String.valueOf(pref.getLimit1().getMin()));
        primeLimitStop.setText(String.valueOf(pref.getLimit1().getMax()));
        primeLimitColorBtn.setBackgroundColor(pref.getLimit1().getColor());

        opt1LimitStart.setText(String.valueOf(pref.getLimit2().getMin()));
        opt1LimitStop.setText(String.valueOf(pref.getLimit2().getMax()));
        opt1LimitColorBtn.setBackgroundColor(pref.getLimit2().getColor());

        opt2LimitStart.setText(String.valueOf(pref.getLimit3().getMin()));
        opt2LimitStop.setText(String.valueOf(pref.getLimit3().getMax()));
        opt2LimitColorBtn.setBackgroundColor(pref.getLimit3().getColor());

        editIP.setText(pref.getIpAddress());

        syncCheck.setChecked(pref.getSync());
    }

}
