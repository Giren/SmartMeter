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
import com.moc.smartmeterapp.model.DataObject;
import com.moc.smartmeterapp.model.EntryObject;
import com.moc.smartmeterapp.model.Global;
import com.moc.smartmeterapp.model.Limit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by philipp on 26.11.2015.
 */
public class WearService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LiveCommunication.ILiveDataEvent {

    private static final String WEARABLE_DATA_PATH = "/SmartMeterToWearable";
    private static final String HANDHELD_DATA_PATH = "/SmartMeterToHandheld";

    private LiveCommunication liveCommunication;
    private GoogleApiClient googleClient;

    @Override
    public void onCreate() {
        liveCommunication = new LiveCommunication( getApplicationContext());

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

        liveCommunication.create();
        liveCommunication.registerDataEventHandler(this);
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
        Log.d("DEBUG", "ON MESSAGE RECEIVED + message: " + new String( messageEvent.getData()));
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
        switch ( receivedMessage) {
            case "liveData": {
                Log.d("DEBUG", "liveData case");
                //dataMessageToWearable( WEARABLE_DATA_PATH, "liveData");
                break;
            }
            case "limitWeek": {
                System.out.println("limitWeek case");
                Log.d("DEBUG", "limitWeekCase");
                dataMessageToWearableLimit(WEARABLE_DATA_PATH, "limitWeek", "2500");
                //dataMessageToWearable(WEARABLE_DATA_PATH, "limitWeek");
                break;
            }
            case "limitMonth": {
                Log.d("DEBUG", "limitMonth case");
                System.out.println("limitMonth case");
                dataMessageToWearableLimit(WEARABLE_DATA_PATH, "limitMonth", "10000");
                //dataMessageToWearable(WEARABLE_DATA_PATH, "limitMonth");
                break;
            }
            case "limitYear": {
                Log.d("DEBUG", "limitYear case");
                System.out.println("limitYear case");
                dataMessageToWearableLimit(WEARABLE_DATA_PATH, "limitYear", "120000");
                //dataMessageToWearable(WEARABLE_DATA_PATH, "limitYear");
                break;
            }
            case "goodbye": {
                Log.d("DEBUG", "goodbye case");
                System.out.println( "goodbye case");
                break;
            }
            default:
                Log.d("DEBUG", "default case");
                System.out.println("default case");
                break;
        }
    }

    public void dataMessageToWearableLimit(String path, String text, String limit) {

        String seperator = ";";
        String dataMessage = limit + seperator;

        SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss");
        String myTime = sdf.format( new Date());

        // Datenvorbereitung
        // dataMessage += "\n";
        dataMessage += String.valueOf( 0 + ( (int)( Math.random() * Integer.valueOf( limit))));


        // TODO Datenbeschaffung innerhalb des Threads und anschließend senden

        // vor dem senden nochmal warten
        try {
            Thread.sleep( 500);
        } catch (Exception e) {
            System.out.println( e.getMessage());
        }
        // send Data on messagePath
        new SendToDataLayerThread( path, text + seperator + dataMessage).start();
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
