package com.moc.smartmeterapp.com.moc.smartmeterapp.communication;

import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by philipp on 26.11.2015.
 */
public class WearService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Communication.ILiveDataEventHandler{

    private static final String WEARABLE_DATA_PATH = "/SmartMeterToWearable";
    private static final String HANDHELD_DATA_PATH = "/SmartMeterToHandheld";

    private Communication communication;
    private GoogleApiClient googleClient;

    @Override
    public void onCreate() {
        communication = new Communication(getApplicationContext(), Communication.LIVE_DATA);
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
        communication.registerDataEventHandler(this);
        communication.bindService();
    }

    @Override
    public void onConnectionSuspended(int i) {
        communication.unregisterDataEventHandler(this);
        communication.unbindService();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        communication.unregisterDataEventHandler(this);
        communication.unbindService();
    }

    @Override
    public boolean onLiveDataReceived(int value) {
        Log.d("DEBUG", "SEND DATA TO WEAR");
        new SendToDataLayerThread( WEARABLE_DATA_PATH, "liveData" + ";" + String.valueOf(value)).start();
        return true;
    }

    @Override
    public void onDestroy()
    {
        communication.unregisterDataEventHandler(this);
        communication.unbindService();

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
        switch ( receivedMessage) {
            case "liveData": {
                //dataMessageToWearable( WEARABLE_DATA_PATH, "liveData");
                break;
            }
            case "limitWeek": {
                System.out.println("limitWeek case");
                dataMessageToWearable(WEARABLE_DATA_PATH, "limitWeek");
                break;
            }
            case "limitMonth": {
                System.out.println( "limitMonth case");
                dataMessageToWearable(WEARABLE_DATA_PATH, "limitMonth");
                break;
            }
            case "limitYear": {
                System.out.println( "limitYear case");
                dataMessageToWearable(WEARABLE_DATA_PATH, "limitYear");
                break;
            }
            case "goodbye": {
                System.out.println( "goodbye case");
                break;
            }
            default:
                System.out.println("default case");
                break;
        }
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
            Thread.sleep( 2000);
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
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for( Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if( result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    Log.d("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
}
