package com.moc.smartmeterapp;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by David on 23.11.2015.
 */
public class ListenerService extends WearableListenerService {

    private static final String WEARABLE_DATA_PATH = "/SmartMeterToWearable";
    private static final String HANDHELD_DATA_PATH = "/SmartMeterToHandheld";

    @Override
    public void onMessageReceived( MessageEvent messageEvent) {

        if( messageEvent.getPath().equals( WEARABLE_DATA_PATH)) {
            final String message = new String( messageEvent.getData());
            Log.v("myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.v( "myTag", "Message received on watch is: " + message);

            // Broadcast message to wearable activity for display
            Intent messageIntent = new Intent();
            messageIntent.setAction( Intent.ACTION_SEND);
            messageIntent.putExtra( "message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast( messageIntent);
        } else {
            super.onMessageReceived( messageEvent);
        } // if end
    } // onMessageReceive end

} // class ListenerService end