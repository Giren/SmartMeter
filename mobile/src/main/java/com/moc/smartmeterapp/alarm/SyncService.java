package com.moc.smartmeterapp.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.moc.smartmeterapp.MainActivity;
import com.moc.smartmeterapp.R;
import com.moc.smartmeterapp.communication.RestCommunication;
import com.moc.smartmeterapp.database.IDatabase;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.DataObject;
import com.moc.smartmeterapp.model.Day;
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;

import java.util.Date;
import java.util.List;

/**
 * Created by philipp on 23.12.2015.
 */
public class SyncService extends IntentService implements RestCommunication.IDataReceiver {

    private NotificationManager mNotificationManager;
    private int count = 0;
    private int total = 0;

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ALARM", "SYNC SERVICE");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        IDatabase database = new MeterDbHelper(getApplicationContext());
        database.openDatabase();
        Day lastDay = database.loadLatestDay();
        database.closeDatabase();

        if(lastDay != null) {
            new RestCommunication(getApplicationContext()).fetchSinceData(lastDay.getDate(), this);
        }

        new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
            @Override
            public void onLimitsReceived(Limit limit, int slot) {
                Log.i("GOT LIMIT", "slot=" + String.valueOf(slot) + " max=" + String.valueOf(limit.getMax()));
                MyPreferences preferences = PreferenceHelper.getPreferences(getApplicationContext());
                if (preferences != null) {
                    preferences.setLimit1(limit);
                    PreferenceHelper.setPreferences(getApplicationContext(), preferences);
                    PreferenceHelper.sendBroadcast(getApplicationContext(), preferences);
                }
            }

            @Override
            public void onError(String message) {
                Log.e("ERROR LIMIT", message);
            }

            @Override
            public void onComplete() {

            }
        }, 0);

        new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
            @Override
            public void onLimitsReceived(Limit limit, int slot) {
                Log.i("GOT LIMIT", "slot=" + String.valueOf(slot) + " max=" + String.valueOf(limit.getMax()));
                MyPreferences preferences = PreferenceHelper.getPreferences(getApplicationContext());
                if (preferences != null) {
                    preferences.setLimit2(limit);
                    PreferenceHelper.setPreferences(getApplicationContext(), preferences);
                    PreferenceHelper.sendBroadcast(getApplicationContext(), preferences);
                }
            }

            @Override
            public void onError(String message) {
                Log.e("ERROR LIMIT", message);
            }

            @Override
            public void onComplete() {

            }
        }, 1);

        new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
            @Override
            public void onLimitsReceived(Limit limit, int slot) {
                Log.i("GOT LIMIT", "slot=" + String.valueOf(slot) + " max=" + String.valueOf(limit.getMax()));
                MyPreferences preferences = PreferenceHelper.getPreferences(getApplicationContext());
                if (preferences != null) {
                    preferences.setLimit3(limit);
                    PreferenceHelper.setPreferences(getApplicationContext(), preferences);
                    PreferenceHelper.sendBroadcast(getApplicationContext(), preferences);
                }
            }

            @Override
            public void onError(String message) {
                Log.e("ERROR LIMIT", message);
            }

            @Override
            public void onComplete() {

            }
        }, 2);

        //PreferenceHelper.sendBroadcast(getApplicationContext());
    }

    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cloud_sync_icon)
                        .setContentTitle("Smart Meter Synchronisation")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setColor(Color.WHITE)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public void onDataReceived(DataObject dataObject) {
        if(dataObject != null) {
            count = dataObject.getDays().size();

            IDatabase db = new MeterDbHelper(getApplicationContext());
            db.openDatabase();
            db.saveYear(dataObject.getDays());

            db.closeDatabase();
        }
    }

    @Override
    public void onError(String message) {
        sendNotification("Es ist ein Fehler bei der Synchronisation aufgetreten.");
    }

    @Override
    public void onComplete() {
        String s = "Es wurden " + String.valueOf(count) + " Datensätze vom Server übertragen.";
        if(total > 0)
            s += " Total: " + String.valueOf(total);
        sendNotification(s);
    }
}
