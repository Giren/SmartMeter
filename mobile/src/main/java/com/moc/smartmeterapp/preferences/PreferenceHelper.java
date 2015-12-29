package com.moc.smartmeterapp.preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.moc.smartmeterapp.SettingFragment;
import com.moc.smartmeterapp.database.IDatabase;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Limit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 28.12.15.
 */
public class PreferenceHelper {

    private static final String STD_IP = "127.0.0.1";

    private List<PrefReceive> prefList;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyPreferences pref = (MyPreferences)intent.getSerializableExtra(SettingFragment.MESSAGE_IDENTIFIER);

            if(pref != null){
                for(PrefReceive p: prefList){
                    if(p != null){
                        p.onPrefReceive(pref);
                    }
                }
            }
        }
    };

    private static MyPreferences createStdPrefs() {
        MyPreferences myPreferences = new MyPreferences();
        myPreferences.setIpAddress(STD_IP);
        myPreferences.setLimit1(new Limit(2500, 3000, Color.RED));
        myPreferences.setLimit2(new Limit(2000, 2500, Color.YELLOW));
        myPreferences.setLimit3(new Limit(0, 2000, Color.GREEN));
        myPreferences.setSync(true);

        return myPreferences;
    }

    public PreferenceHelper(){
        prefList = new ArrayList<PrefReceive>();
    }

    public void register(PrefReceive prefReceive){
        prefList.add(prefReceive);
    }

    public boolean unregister(PrefReceive prefReceive){
        return prefList.remove(prefReceive);
    }

    public static MyPreferences getPreferences(Context context){
        IDatabase helper = new MeterDbHelper(context);
        helper.openDatabase();
        MyPreferences pref = helper.loadPreferences();
        helper.closeDatabase();

        if(pref != null)
            return pref;

        return createStdPrefs();
    }

    public static void setPreferences(Context context, MyPreferences prefs){
        if( prefs!= null) {
            IDatabase helper = new MeterDbHelper(context);
            helper.openDatabase();

            helper.savePreferences(prefs);

            helper.closeDatabase();
        }
    }

    public static interface PrefReceive{
        void onPrefReceive(MyPreferences pref);
    }
}
