package com.moc.smartmeterapp.preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moc.smartmeterapp.SettingFragment;
import com.moc.smartmeterapp.database.IDatabase;
import com.moc.smartmeterapp.database.MeterDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 28.12.15.
 */
public class PreferenceHelper {

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
        return pref;
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
