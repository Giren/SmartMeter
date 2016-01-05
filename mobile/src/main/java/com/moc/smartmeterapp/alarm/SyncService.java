package com.moc.smartmeterapp.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
import com.moc.smartmeterapp.utils.HomeHelper;

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

        final MyPreferences myPreferences = PreferenceHelper.getPreferences(getApplicationContext());
        //Check if limits allready set to server. If not get them
        if(myPreferences != null && myPreferences.getUnSynced()) {
            new RestCommunication(getApplicationContext()).saveLimit(myPreferences.getLimit1(), 0, new RestCommunication.IRestAnswer() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onComplete() {
                    new RestCommunication(getApplicationContext()).saveLimit(myPreferences.getLimit2(), 1, new RestCommunication.IRestAnswer() {
                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onComplete() {
                            new RestCommunication(getApplicationContext()).saveLimit(myPreferences.getLimit3(), 2, new RestCommunication.IRestAnswer() {
                                @Override
                                public void onError(String message) {

                                }

                                @Override
                                public void onComplete() {
                                    myPreferences.setUnSynced(false);
                                    PreferenceHelper.setPreferences(getApplicationContext(), myPreferences);
                                }
                            });
                        }
                    });
                }
            });
        } else {
            final Limit limit1 = new Limit();
            final Limit limit2 = new Limit();
            final Limit limit3 = new Limit();

            new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
                @Override
                public void onLimitsReceived(Limit limit, int slot) {
                    limit1.setMax(limit.getMax());
                    limit1.setColor(limit.getColor());
                    limit1.setMin(limit.getMin());
                }

                @Override
                public void onError(String message) {

                }

                @Override
                public void onComplete() {
                    new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
                        @Override
                        public void onLimitsReceived(Limit limit, int slot) {
                            limit2.setMax(limit.getMax());
                            limit2.setColor(limit.getColor());
                            limit2.setMin(limit.getMin());
                        }

                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onComplete() {
                            new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
                                @Override
                                public void onLimitsReceived(Limit limit, int slot) {
                                    limit3.setMax(limit.getMax());
                                    limit3.setColor(limit.getColor());
                                    limit3.setMin(limit.getMin());
                                }

                                @Override
                                public void onError(String message) {

                                }

                                @Override
                                public void onComplete() {
                                    MyPreferences tempPreferences = PreferenceHelper.getPreferences(getApplicationContext());
                                    boolean changes = false;

                                    if(!limit1.equals(tempPreferences.getLimit1())) {
                                        tempPreferences.setLimit1(limit1);
                                        changes = true;
                                    }

                                    if(!limit2.equals(tempPreferences.getLimit2())) {
                                        tempPreferences.setLimit2(limit2);
                                        changes = true;
                                    }

                                    if(!limit3.equals(tempPreferences.getLimit3())) {
                                        tempPreferences.setLimit3(limit3);
                                        changes = true;
                                    }

                                    if(changes){
                                        PreferenceHelper.setPreferences(getApplicationContext(), tempPreferences);
                                        PreferenceHelper.sendBroadcast(getApplicationContext(), tempPreferences);
                                    }
                                }
                            }, 2);
                        }
                    }, 1);
                }
            }, 0);
        }

        //Check limit reached
        HomeHelper helper = new HomeHelper(getApplicationContext());
        Integer consumption = helper.getConsumption(HomeHelper.MONTH);
        if(consumption != null && myPreferences != null && consumption > myPreferences.getLimit1().getMin()) {
            sendAlarmNotification("Das Monatslimit wurde erreicht. Aktueller Verbrauch um " + String.valueOf(consumption-myPreferences.getLimit1().getMin()) + " kWh überschritten.");
        }
    }


    private void sendAlarmNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.limit)
                        .setContentTitle("Smart Meter App")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setColor(Color.WHITE)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void sendSyncNotification(String msg) {
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
        sendSyncNotification("Es ist ein Fehler bei der Synchronisation aufgetreten. Es konnte keine Vebindung zum Server aufgebaut werden.");
    }

    @Override
    public void onComplete() {
        String s = "Es wurden " + String.valueOf(count) + " Datensätze vom Server übertragen.";
        if(total > 0)
            s += " Total: " + String.valueOf(total);
        sendSyncNotification(s);
    }
}
