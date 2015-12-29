package com.moc.smartmeterapp.communication;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.DataObject;
import com.moc.smartmeterapp.model.Day;
import com.moc.smartmeterapp.model.EntryObject;
import com.moc.smartmeterapp.model.Global;
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by philipp on 26.11.2015.
 */
public class WearService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LiveCommunication.ILiveDataEvent,
        PreferenceHelper.PrefReceive {

    final String ERR_NO_DATA_IN_DB = "ERRO01";

    private static final String WEARABLE_DATA_PATH = "/SmartMeterToWearable";
    private static final String HANDHELD_DATA_PATH = "/SmartMeterToHandheld";

    private LiveCommunication liveCommunication;
    private GoogleApiClient googleClient;

    private MyPreferences myPreferences;

    private Calendar calendar;
    private TimeZone timeZone;
    private MeterDbHelper meterDbHelper;

    private DateFormat dayFormat, monthFormat, yearFormat;

    private String limitWeek;
    private String limitMonth;
    private String limitYear;

    @Override
    public void onCreate() {
        Log.d("DEBUG", "WEAR SERVICE ON CREATE");
        liveCommunication = new LiveCommunication(getApplicationContext());
        liveCommunication.create();
        liveCommunication.registerDataEventHandler(this);

        dayFormat = new SimpleDateFormat( "dd");
        monthFormat = new SimpleDateFormat( "MM");
        yearFormat = new SimpleDateFormat( "yyyy");

        myPreferences = PreferenceHelper.getPreferences( getApplicationContext());
        if(meterDbHelper == null){
            meterDbHelper = new MeterDbHelper( getBaseContext());
        }

        timeZone = TimeZone.getDefault();
        calendar = new GregorianCalendar( timeZone);

        limitWeek = String.valueOf(myPreferences.getLimit1().getMax() / 4);
        limitMonth = String.valueOf( myPreferences.getLimit1().getMax());
        limitYear = String.valueOf( myPreferences.getLimit1().getMax() * 12);

        Log.d("DEBUG", "limitWeek: " + limitWeek);
        Log.d("DEBUG", "limitMonth: " + limitMonth);
        Log.d("DEBUG", "limitYear: " + limitYear);

        Log.d("DEBUG", "WEAR SERVICE ON CREATE");

        if ( googleClient == null) {
            // Build a new GoogleApiClient for the Wearable API
            googleClient = new GoogleApiClient.Builder( this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        if ( !googleClient.isConnected()) {
            googleClient.connect();
        }

        super.onCreate();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("DEBUG", "WEAR SERVICE ON CONNECTED");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("DEBUG", "WEAR SERVICE ON CONNECTION SUSPENDED");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("DEBUG", "WEAR SERVICE ON CONNECTION FAILED");
    }

    @Override
    public void onLiveDataReceived(int value) {
        Log.d("DEBUG", "SEND DATA TO WEAR - ON LIVE DATA RECEIVED");
        new SendToDataLayerThread( WEARABLE_DATA_PATH, "liveData" + ";" + String.valueOf(value)).start();
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "ON DESTROY");

        liveCommunication.destroy();

        if (null != googleClient)
        {
            if (googleClient.isConnected())
            {
                googleClient.disconnect();
            }
        }
        super.onDestroy();
    }

    @Override
     public void onMessageReceived( MessageEvent messageEvent) {
        Log.d("DEBUG", "ON MESSAGE RECEIVED + message: " + new String(messageEvent.getData()));
        if( messageEvent.getPath().equals( HANDHELD_DATA_PATH)) {
            final String message = new String( messageEvent.getData());

            Runnable reactOnMessageRunnable = new Runnable() {
                @Override
                public void run() {
                    reactOnMessage( message);
                }
            };
            new Thread(reactOnMessageRunnable).start();

        } else {
            super.onMessageReceived( messageEvent);
        }
    }

    public void reactOnMessage( String receivedMessage) {
        Log.d("DEBUG", "REACT ON MESSAGE + message: " + receivedMessage);
        meterDbHelper.openDatabase();
        Log.d("DEBUG", "REACT ON MESSAGE - openDatabase()");
        switch ( receivedMessage) {
            case "liveData": {
                Log.d("DEBUG", "liveData case");
                //dataMessageToWearable( WEARABLE_DATA_PATH, "liveData");
                break;
            }
            case "limitWeek": {
                Log.d("DEBUG", "limitWeekCase");
                dataMessageToWearableLimit(WEARABLE_DATA_PATH, "limitWeek", limitWeek);
                //dataMessageToWearable(WEARABLE_DATA_PATH, "limitWeek");
                break;
            }
            case "limitMonth": {
                Log.d("DEBUG", "limitMonth case");
                dataMessageToWearableLimit(WEARABLE_DATA_PATH, "limitMonth", limitMonth);
                //dataMessageToWearable(WEARABLE_DATA_PATH, "limitMonth");
                break;
            }
            case "limitYear": {
                Log.d("DEBUG", "limitYear case");
                dataMessageToWearableLimit(WEARABLE_DATA_PATH, "limitYear", limitYear);
                //dataMessageToWearable(WEARABLE_DATA_PATH, "limitYear");
                break;
            }
            case "goodbye": {
                Log.d("DEBUG", "goodbye case");
                break;
            }
            default:
                Log.d("DEBUG", "default case");
                break;
        }
        meterDbHelper.closeDatabase();
        Log.d("DEBUG", "REACT ON MESSAGE - closeDatabase()");
    }

    public void dataMessageToWearableLimit(String path, String fragmentName, String limitValue) {
        String seperator = ";";
        String dataMessage;

        // Datenvorbereitung
        String currentValue = getCurrentLimitValue( fragmentName);
        // TODO Datenbeschaffung der einzelnen limits, anschließend senden

        // vor dem senden nochmal warten
        try {
            Thread.sleep( 500);
        } catch (Exception e) {
            System.out.println( e.getMessage());
        }

        // send Data to wearable on messagePath
        // <fragmentName>;<currentValue>;<limitValue>
        dataMessage = fragmentName + seperator + currentValue + seperator + limitValue;
        new SendToDataLayerThread( path, dataMessage).start();
    }


    public String getCurrentValueOfThisWeek() {

        Date dateCurrent, dateBefore;
        Calendar weekCalendar = new GregorianCalendar();

        if( weekCalendar.get( Calendar.DAY_OF_WEEK) == 1)
        {
            Day dayCurrent = meterDbHelper.loadDay( weekCalendar.getTime());
            if( dayCurrent == null) {
                return ERR_NO_DATA_IN_DB;
            } else {
                return String.valueOf( dayCurrent.getMmm().getTotalSum());
            }
            //return String.valueOf((int) (Math.random() * Integer.valueOf(limitWeek)));

        } else {
            dateCurrent = weekCalendar.getTime();
            weekCalendar.add( Calendar.DAY_OF_MONTH, 1 - (weekCalendar.get( Calendar.DAY_OF_WEEK)));
            dateBefore = weekCalendar.getTime();

            DateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh-mm-ss");
            Log.d("DEBUG", "getCurrentValueOfThisWeek - DateFormat: " + sdf.format( dateCurrent));
            Log.d("DEBUG", "getCurrentValueOfThisWeek - DateFormat: " + sdf.format( dateBefore));

            Day dayCurrent = meterDbHelper.loadDay( dateCurrent);
            Day dayBefore = meterDbHelper.loadDay( dateBefore);

            if( dayCurrent == null || dayBefore == null) {
                return ERR_NO_DATA_IN_DB;
            } else {
                int currentValue = dayCurrent.getMmm().getTotalSum() - dayBefore.getMmm().getTotalSum();
                return String.valueOf( currentValue);
            }
            //return String.valueOf((int) (Math.random() * Integer.valueOf(limitWeek)));
        }
    }

    public String getCurrentValueOfThisMonth() {

        Date dateCurrent, dateBefore;
        Calendar monthCalendar = new GregorianCalendar();

        if( monthCalendar.get( Calendar.DAY_OF_MONTH) == 1)
        {
            Day dayCurrent = meterDbHelper.loadDay( monthCalendar.getTime());
            if( dayCurrent == null) {
                return ERR_NO_DATA_IN_DB;
            } else {
                return String.valueOf( dayCurrent.getMmm().getTotalSum());
            }
            //return String.valueOf((int) (Math.random() * Integer.valueOf(limitMonth)));

        } else {
            dateCurrent = monthCalendar.getTime();
            monthCalendar.add( Calendar.DAY_OF_MONTH, 1 - (monthCalendar.get( Calendar.DAY_OF_MONTH)));
            dateBefore = monthCalendar.getTime();

            DateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh-mm-ss");
            Log.d("DEBUG", "getCurrentValueOfThisWeek - DateFormat: " + sdf.format( dateCurrent));
            Log.d("DEBUG", "getCurrentValueOfThisWeek - DateFormat: " + sdf.format( dateBefore));

            Day dayCurrent = meterDbHelper.loadDay( dateCurrent);
            Day dayBefore = meterDbHelper.loadDay( dateBefore);

            if( dayCurrent == null || dayBefore == null) {
                return ERR_NO_DATA_IN_DB;
            } else {
                int currentValue = dayCurrent.getMmm().getTotalSum() - dayBefore.getMmm().getTotalSum();
                return String.valueOf( currentValue);
            }
            //return String.valueOf((int) (Math.random() * Integer.valueOf(limitMonth)));
        }
    }

    public String getCurrentValueOfThisYear() {

        Date dateCurrent, dateBefore;
        Calendar yearCalendar = new GregorianCalendar();

        if( yearCalendar.get( Calendar.DAY_OF_MONTH) == 1 && yearCalendar.get( Calendar.MONTH) == 0)
        {
            Day dayCurrent = meterDbHelper.loadDay( yearCalendar.getTime());

            if( dayCurrent == null) {
                return ERR_NO_DATA_IN_DB;
            } else {
                return String.valueOf( dayCurrent.getMmm().getTotalSum());
            }
           // return String.valueOf((int) (Math.random() * Integer.valueOf(limitYear)));

        } else {
            dateCurrent = yearCalendar.getTime();
            yearCalendar.add( Calendar.DAY_OF_YEAR, 1 - (yearCalendar.get( Calendar.DAY_OF_YEAR)));
            dateBefore = yearCalendar.getTime();

            DateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh-mm-ss");
            Log.d("DEBUG", "getCurrentValueOfThisWeek - DateFormat: " + sdf.format( dateCurrent));
            Log.d("DEBUG", "getCurrentValueOfThisWeek - DateFormat: " + sdf.format( dateBefore));

            Day dayCurrent = meterDbHelper.loadDay( dateCurrent);
            Day dayBefore = meterDbHelper.loadDay( dateBefore);

            if( dayCurrent == null || dayBefore == null) {
                return ERR_NO_DATA_IN_DB;
            } else {
                int currentValue = dayCurrent.getMmm().getTotalSum() - dayBefore.getMmm().getTotalSum();
                return String.valueOf( currentValue);
            }
            //return String.valueOf((int) (Math.random() * Integer.valueOf(limitYear)));
        }
    }



    public String getCurrentLimitValue( String fragmentName) {

        switch ( fragmentName) {
            case "liveData": {
                Log.d("DEBUG", "getCurrentLimitValue - liveData case");
                break;
            }
            case "limitWeek": {
                Log.d("DEBUG", "getCurrentLimitValue - limitWeek case");
                return getCurrentValueOfThisWeek();
                //return String.valueOf((int) (Math.random() * Integer.valueOf(limitWeek)));
            }
            case "limitMonth": {
                Log.d("DEBUG", "getCurrentLimitValue - limitMonth case");
                return getCurrentValueOfThisMonth();
                // TODO get Data from DB
                //return String.valueOf( ( int)( Math.random() * Integer.valueOf( limitMonth)));
            }
            case "limitYear": {
                Log.d("DEBUG", "getCurrentLimitValue - limitYear case");
                return getCurrentValueOfThisYear();
                // TODO get Data from DB
                //return String.valueOf( ( int)( Math.random() * Integer.valueOf( limitYear)));
            }
            default: {
                Log.d("DEBUG", "getCurrentLimitValue - default case");
                break;
            }
        }
        return ERR_NO_DATA_IN_DB;
    }

    public void dataMessageToWearable(String path, String text) {

        String seperator = ";";
        String dataMessage = "";
        SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss");
        String myTime = sdf.format( new Date());

        // Datenvorbereitung
        // dataMessage += "\n";
        dataMessage = String.valueOf( 0 + ( (int)( Math.random() * 2500)));

        // TODO Datenbeschaffung innerhalb des Threads und anschließend senden

        // vor dem senden nochmal warten
        try {
            Thread.sleep( 1000);
        } catch (Exception e) {
            System.out.println( e.getMessage());
        }
        // send Data on messagePath
        new SendToDataLayerThread( path, text + seperator + dataMessage).start();
    }

    @Override
    public void onPrefReceive(MyPreferences pref) {
        // TODO benötigte updaten
        limitWeek = String.valueOf( pref.getLimit1().getMax() / 4);
        limitMonth = String.valueOf( pref.getLimit1().getMax());
        limitYear = String.valueOf( pref.getLimit1().getMax() * 12);
    }

    public class SendToDataLayerThread extends Thread {

        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread( String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            Log.d("DEBUG", "SendToDataLayerThread run()");
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for( Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if( result.getStatus().isSuccess()) {
                    Log.d("DEBUG", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    Log.d("DEBUG", "ERROR: failed to send Message");
                }
            }
        }
    }
}
