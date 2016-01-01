package com.moc.smartmeterapp.communication;

import android.app.PendingIntent;
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

    MyPreferences myPreferences;

    TimeZone timeZone;
    private MeterDbHelper meterDbHelper;

    //private DateFormat dayFormat, monthFormat, yearFormat;

    private String limitWeek;
    private String limitMonth;
    private String limitYear;

    private String visibleFragmentOnWearable;

    private Limit limit1, limit2, limit3;


    @Override
    public void onCreate() {
        Log.d("DEBUG", "WEAR SERVICE ON CREATE");

        //dayFormat = new SimpleDateFormat( "dd");
        //monthFormat = new SimpleDateFormat( "MM");
        //yearFormat = new SimpleDateFormat( "yyyy");

        myPreferences = PreferenceHelper.getPreferences(getApplicationContext());
        if(meterDbHelper == null){
            meterDbHelper = new MeterDbHelper( getBaseContext());
        }

        liveCommunication = new LiveCommunication(getApplicationContext());

        timeZone = TimeZone.getDefault();
        //calendar = new GregorianCalendar( timeZone);

        limitWeek = String.valueOf(myPreferences.getLimit1().getMax() / 4);
        limitMonth = String.valueOf( myPreferences.getLimit1().getMax());
        limitYear = String.valueOf( myPreferences.getLimit1().getMax() * 12);
        limit1 = myPreferences.getLimit1();
        limit2 = myPreferences.getLimit2();
        limit3 = myPreferences.getLimit3();

        Log.d("DEBUG", "limitWeek: " + limitWeek);
        Log.d("DEBUG", "limitMonth: " + limitMonth);
        Log.d("DEBUG", "limitYear: " + limitYear);

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
        liveCommunication.registerDataEventHandler( this);
        liveCommunication.create();
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
        liveCommunication.unregisterDataEventHandler(this);
        liveCommunication.destroy();
        visibleFragmentOnWearable = null;
        if ( null != googleClient)
        {
            if ( googleClient.isConnected())
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
        Log.d("DEBUG", "reactOnMessage() + message: " + receivedMessage);
        switch ( receivedMessage) {
            case "keepAlive": {
                Log.d( "DEBUG", "case: keepAlive");
                // send "keepAlive" response to wearable
                keepAliveResponse( WEARABLE_DATA_PATH, "keepAlive");
                break;
            }
            case "liveData": {
                Log.d( "DEBUG", "liveData case");
                visibleFragmentOnWearable = "liveData";
                // liveDataKeepAlive(WEARABLE_DATA_PATH);
                break;
            }
            case "limitWeek": {
                Log.d( "DEBUG", "limitWeekCase");
                visibleFragmentOnWearable = "limitWeek";
                meterDbHelper.openDatabase();
                Log.d( "DEBUG", "reactOnMessage() - openDatabase()");
                dataMessageToWearableLimit( WEARABLE_DATA_PATH, "limitWeek", limitWeek);
                meterDbHelper.closeDatabase();
                Log.d( "DEBUG", "REACT ON MESSAGE - closeDatabase()");
                break;
            }
            case "limitMonth": {
                Log.d( "DEBUG", "limitMonth case");
                visibleFragmentOnWearable = "limitMonth";
                meterDbHelper.openDatabase();
                Log.d( "DEBUG", "reactOnMessage() - openDatabase()");
                dataMessageToWearableLimit( WEARABLE_DATA_PATH, "limitMonth", limitMonth);
                meterDbHelper.closeDatabase();
                Log.d( "DEBUG", "REACT ON MESSAGE - closeDatabase()");
                break;
            }
            case "limitYear": {
                Log.d( "DEBUG", "limitYear case");
                visibleFragmentOnWearable = "limitYear";
                meterDbHelper.openDatabase();
                Log.d( "DEBUG", "reactOnMessage() - openDatabase()");
                dataMessageToWearableLimit( WEARABLE_DATA_PATH, "limitYear", limitYear);
                meterDbHelper.closeDatabase();
                Log.d( "DEBUG", "REACT ON MESSAGE - closeDatabase()");
                break;
            }
            default:
                Log.d("DEBUG", "default case");
                break;
        }
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

            //Day dayCurrent = meterDbHelper.loadDay( dateCurrent);
            Day dayCurrent = meterDbHelper.loadLatestDay(); // load latest day from DB
            Day dayBefore = meterDbHelper.loadDay( dateBefore);

            if( dayCurrent == null || dayBefore == null) {
                return ERR_NO_DATA_IN_DB;
            } else {
                if( dayCurrent.getDate().after( dayBefore.getDate())) {
                    int currentValue = dayCurrent.getMmm().getTotalSum() - dayBefore.getMmm().getTotalSum();
                    return String.valueOf( currentValue);
                } else {
                    return ERR_NO_DATA_IN_DB;
                }
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

            //Day dayCurrent = meterDbHelper.loadDay( dateCurrent);
            Day dayCurrent = meterDbHelper.loadLatestDay(); // load latest day from DB
            Day dayBefore = meterDbHelper.loadDay( dateBefore);

            if( dayCurrent == null || dayBefore == null) {
                return ERR_NO_DATA_IN_DB;
            } else {
                if( dayCurrent.getDate().after( dayBefore.getDate())) {
                    int currentValue = dayCurrent.getMmm().getTotalSum() - dayBefore.getMmm().getTotalSum();
                    return String.valueOf( currentValue);
                } else {
                    return ERR_NO_DATA_IN_DB;
                }
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

            //Day dayCurrent = meterDbHelper.loadDay( dateCurrent);
            Day dayCurrent = meterDbHelper.loadLatestDay(); // load latest day from DB
            Day dayBefore = meterDbHelper.loadDay( dateBefore);

            if( dayCurrent == null || dayBefore == null) {
                return ERR_NO_DATA_IN_DB;
        } else {
            if( dayCurrent.getDate().after( dayBefore.getDate())) {
                int currentValue = dayCurrent.getMmm().getTotalSum() - dayBefore.getMmm().getTotalSum();
                return String.valueOf( currentValue);
            } else {
                return ERR_NO_DATA_IN_DB;
            }
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
                //return String.valueOf( ( int)( Math.random() * Integer.valueOf( limitMonth)));
            }
            case "limitYear": {
                Log.d("DEBUG", "getCurrentLimitValue - limitYear case");
                return getCurrentValueOfThisYear();
                //return String.valueOf( ( int)( Math.random() * Integer.valueOf( limitYear)));
            }
            default: {
                Log.d("DEBUG", "getCurrentLimitValue - default case");
                break;
            }
        }
        return ERR_NO_DATA_IN_DB;
    }

    public void liveDataKeepAlive( String path) {

        String dataMessage = "liveData;keepAlive;";
        dataMessage += "limit1;";
        dataMessage += limit1.getMin() + ";";
        dataMessage += limit1.getMax() + ";";
        dataMessage += limit1.getColor();
        if( limit2 != null) {
            dataMessage += ";limit2;";
            dataMessage += limit2.getMin() + ";";
            dataMessage += limit2.getMax() + ";";
            dataMessage += limit2.getColor();
            if( limit3 != null) {
                dataMessage += ";limit3;";
                dataMessage += limit3.getMin() + ";";
                dataMessage += limit3.getMax() + ";";
                dataMessage += limit3.getColor();
            }
        }
        // vor dem senden nochmal warten
        try {
            Thread.sleep( 500);
        } catch (Exception e) {
            System.out.println( e.getMessage());
        }
        // send Data on messagePath
        new SendToDataLayerThread( path, dataMessage).start();
    }

    public void keepAliveResponse( String messagePath, String keepAliveMessage) {
        /*
        TODO
        implement information which should be send with keepAlive
        for example limit informations
         */
        try {
            // wait before send
            Thread.sleep( 500);
        } catch (Exception e) {
            Log.d("DEBUG", "keepAliveResponse() - Exception" + e.getMessage());
        }
        // send keepAliveResponse on messagePath
        new SendToDataLayerThread( messagePath, keepAliveMessage).start();
    }

    @Override
    public void onPrefReceive(MyPreferences pref) {
        // TODO benötigte updaten
        limitWeek = String.valueOf( pref.getLimit1().getMax() / 4);
        limitMonth = String.valueOf( pref.getLimit1().getMax());
        limitYear = String.valueOf( pref.getLimit1().getMax() * 12);

        limit1 = pref.getLimit1();
        limit2 = pref.getLimit2();
        limit3 = pref.getLimit3();
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
