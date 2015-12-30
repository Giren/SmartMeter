package com.moc.smartmeterapp.preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.moc.smartmeterapp.SettingFragment;
import com.moc.smartmeterapp.communication.RestCommunication;
import com.moc.smartmeterapp.database.IDatabase;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Limit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 28.12.15.
 */
public class PreferenceHelper extends BroadcastReceiver{

    private static final String STD_IP = "127.0.0.1";
    public static final String MESSAGE_IDENTIFIER = "PREFS";
    public static final String INTENT_IDENTIFIER = "com.moc.smartmeterapp.preferences.PreferenceHelper";

    private List<PrefReceive> prefList;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        MyPreferences pref = (MyPreferences)intent.getSerializableExtra(MESSAGE_IDENTIFIER);
        if(pref != null){
            for(PrefReceive p: prefList){
                if(p != null){
                    p.onPrefReceive(pref);
                }
            }
        }
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
        MyPreferences prefs = helper.loadPreferences();
        helper.closeDatabase();
        if(prefs != null) {
            MyPreferences stdPrefs = createStdPrefs();

            if(prefs.getLimit1() == null) {
                prefs.setLimit1(stdPrefs.getLimit1());
            }

            if(prefs.getLimit2() == null) {
                prefs.setLimit2(stdPrefs.getLimit2());
            }

            if(prefs.getLimit3() == null) {
                prefs.setLimit3(stdPrefs.getLimit3());
            }

            return prefs;
        }

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

    public static void sendBroadcast(Context context) {
        context.sendBroadcast(new Intent(INTENT_IDENTIFIER).putExtra(MESSAGE_IDENTIFIER, getPreferences(context)));
    }

    public static void limitsToServer(Context context) {
        MyPreferences myPreferences = getPreferences(context);
        if(myPreferences != null) {
            new RestCommunication(context).saveLimit(myPreferences.getLimit1(), 2);
            new RestCommunication(context).saveLimit(myPreferences.getLimit2(), 1);
            new RestCommunication(context).saveLimit(myPreferences.getLimit3(), 0);
        }
    }

    public static interface PrefReceive{
        void onPrefReceive(MyPreferences pref);
    }
}
