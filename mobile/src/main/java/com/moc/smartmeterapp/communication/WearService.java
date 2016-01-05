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
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;
import com.moc.smartmeterapp.utils.HomeHelper;


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
    private HomeHelper homeHelper;

    MyPreferences myPreferences;
    PreferenceHelper preferenceHelper;

    private String limitWeek;
    private String limitMonth;
    private String limitYear;

    private String visibleFragmentOnWearable;

    private Limit limit1, limit2, limit3;

    @Override
    public void onCreate() {
        Log.d("DEBUG", "WEAR SERVICE ON CREATE");

        homeHelper = new HomeHelper( getApplicationContext());
        preferenceHelper = new PreferenceHelper(getApplicationContext());
        myPreferences = PreferenceHelper.getPreferences(getApplicationContext());
        liveCommunication = new LiveCommunication( getApplicationContext());

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
        preferenceHelper.register(this);
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
        if( visibleFragmentOnWearable.equals( "liveData")) {
            new SendToDataLayerThread( WEARABLE_DATA_PATH, "liveData" + ";" + String.valueOf(value)).start();
        }

    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "ON DESTROY");
        liveCommunication.unregisterDataEventHandler(this);
        liveCommunication.destroy();
        visibleFragmentOnWearable = null;
        preferenceHelper.unregister(this);
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
        if( messageEvent.getPath().equals(HANDHELD_DATA_PATH)) {
            final String message = new String( messageEvent.getData());
            Runnable reactOnMessageRunnable = new Runnable() {
                @Override
                public void run() {
                    reactOnMessage( message);
                }
            };
            new Thread( reactOnMessageRunnable).start();
        } else {
            super.onMessageReceived(messageEvent);
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
                liveDataLimits(WEARABLE_DATA_PATH);
                break;
            }
            case "limitWeek": {
                Log.d( "DEBUG", "limitWeekCase");
                visibleFragmentOnWearable = "limitWeek";
                dataMessageToWearableLimit( WEARABLE_DATA_PATH, "limitWeek", limitWeek, HomeHelper.WEEK);
                break;
            }
            case "limitMonth": {
                Log.d( "DEBUG", "limitMonth case");
                visibleFragmentOnWearable = "limitMonth";
                dataMessageToWearableLimit( WEARABLE_DATA_PATH, "limitMonth", limitMonth, HomeHelper.MONTH);
                break;
            }
            case "limitYear": {
                Log.d( "DEBUG", "limitYear case");
                visibleFragmentOnWearable = "limitYear";
                dataMessageToWearableLimit( WEARABLE_DATA_PATH, "limitYear", limitYear, HomeHelper.YEAR);
                break;
            }
            default:
                Log.d("DEBUG", "default case");
                break;
        }
    }

    public void dataMessageToWearableLimit(String path, String fragmentName, String limitValue, int homeHelperInt) {
        String seperator = ";";
        String dataMessage;
        String currentValue = String.valueOf( homeHelper.getConsumption( homeHelperInt));
        if( currentValue == null) {
            currentValue = ERR_NO_DATA_IN_DB;
        }
        // vor dem senden nochmal warten
        try {
            Thread.sleep( 100);
        } catch (Exception e) {
            System.out.println( e.getMessage());
        }
        // send Data to wearable on messagePath
        // <fragmentName>;<currentValue>;<limitValue>
        dataMessage = fragmentName + seperator + currentValue + seperator + limitValue;
        new SendToDataLayerThread( path, dataMessage).start();
    }

    public void liveDataLimits( String path) {

        String dataMessage = "liveData;";
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
        } else {
            dataMessage += ";limit2;0;0;0;limit3;0;0;0";
        }
        // vor dem senden nochmal warten
        try {
            Thread.sleep( 100);
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
        Log.d( "DEBUG", "WearService Preference Update");
        // update local data
        limitWeek = String.valueOf( pref.getLimit1().getMax() / 4);
        limitMonth = String.valueOf( pref.getLimit1().getMax());
        limitYear = String.valueOf(pref.getLimit1().getMax() * 12);
        limit1 = pref.getLimit1();
        limit2 = pref.getLimit2();
        limit3 = pref.getLimit3();
        // send updated limit objects to wearable
        liveDataLimits(WEARABLE_DATA_PATH);
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
