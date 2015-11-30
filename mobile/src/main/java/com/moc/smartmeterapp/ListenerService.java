package com.moc.smartmeterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David on 23.11.2015.
 */
public class ListenerService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String WEARABLE_DATA_PATH = "/SmartMeterToWearable";
    private static final String HANDHELD_DATA_PATH = "/SmartMeterToHandheld";

    private GoogleApiClient googleClient;

    @Override
    public void onCreate()
    {
        System.out.println("listenerservice: oncreate");
        super.onCreate();
        if ( googleClient == null)
        {
            // Build a new GoogleApiClient for the Wearable API
            googleClient = new GoogleApiClient.Builder( this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if ( !googleClient.isConnected())
        {
            googleClient.connect();
            System.out.println("listenerservice: connect googleclient");
        }
    }

    // Send a message when the Data Layer connection is successful
    @Override
    public void onConnected( Bundle connectionHint) {
        ;
    }

    @Override
    public void onDestroy()
    {
        System.out.println("listenerservice: onDestroy");
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
    public void onDataChanged( DataEventBuffer dataEvents)
    {
        System.out.println("listenerservice: onDataChanged called");
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0)
    {
        System.out.println("listenerservice: onConnectionFailed called");
    }


    @Override
    public void onConnectionSuspended(int arg0)
    {
        System.out.println("listenerservice: onConnectionSuspended called");
    }

    @Override
    public void onMessageReceived( MessageEvent messageEvent) {

        if( messageEvent.getPath().equals( HANDHELD_DATA_PATH)) {
            final String message = new String( messageEvent.getData());
            Log.v( "myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.v( "myTag", "Message received on watch is: " + message);

            // reagiere auf empfangene Message
            Runnable reactOnMessageRunnable = new Runnable() {
                @Override
                public void run() {
                    reactOnMessage( message);
                }
            };
            new Thread(reactOnMessageRunnable).start();

        } else {
            super.onMessageReceived( messageEvent);
        } // if end
    } // onMessageReceive end

    // reagiere auf empfangene message
    public void reactOnMessage( String receivedMessage) {
        switch ( receivedMessage) {
            case "liveData": {
                System.out.println("liveData case");
//                if( runningDataThread != null) {
//                    runningDataThread.endThread();
//                    runningDataThread = null;
//                }
//                runningDataThread = new DataForWearable( WEARABLE_DATA_PATH, "liveData");
//                runningDataThread.start();
                dataMessageToWearable( WEARABLE_DATA_PATH, "liveData");
                // new DataForWearable( WEARABLE_DATA_PATH, "helloworld").start();
                // new SendToDataLayerThread( WEARABLE_DATA_PATH, "helloworld").start();
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
                    // Log on error
                    Log.v("myTag", "ERROR: failed to send Message");
                } // if end
            } // for end
        } // run end
    } // class SendToDataLayerThread end

} // class ListenerService end