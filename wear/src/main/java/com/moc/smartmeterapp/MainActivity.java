package com.moc.smartmeterapp;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

/**
 * Created by David on 23.11.2015.
 */
public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String WEARABLE_DATA_PATH = "/SmartMeterToWearable";
    private static final String HANDHELD_DATA_PATH = "/SmartMeterToHandheld";

    ViewPager pager;
//    CustomFragment actualFragment;
    GoogleApiClient googleClient;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState);
        setContentView(R.layout.main_activity);

        pager = ( ViewPager) findViewById( R.id.viewPager);
        pager.setAdapter( new MyPagerAdapter( getSupportFragmentManager()));

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter( Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder( this)
                .addApi( Wearable.API)
                .addConnectionCallbacks( this)
                .addOnConnectionFailedListener( this)
                .build();

        // TODO send "hello" to handheld
    }

    /**
     *
     */
    @Override
    protected void onStop() {
        // TODO send "goodbye" to handheld and disconnect googleClient
        sendDataToHandheld("goodbye");

        if( null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
        System.out.println("onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestry called");
    }

    /**
     * Connect to the data layer when the Activity starts
     */
    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }



    /**
     * Send a message when the Data Layer connection is successful
     * @param connectionHint
     */
    @Override
    public void onConnected( Bundle connectionHint) {

        //String message = "Hello Wearable\n Via the data layer";
        // Requeres a new thread to avoid blocking the UI
        //new SendToDataLayerThread( WEARABLE_DATA_PATH, message).start();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended( int cause) { }
//
    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) { }


    public void sendDataToHandheld(String toHandheld) {
        new SendToDataLayerThread( HANDHELD_DATA_PATH, toHandheld).start();
    }


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive( Context context, final Intent intent) {

//            final Intent localIntent = intent;
//            runOnUiThread( new Runnable() {
//                @Override
//                public void run() {
//
//
//                }
//            });

            String message = intent.getStringExtra( "message");
            // Display message in UI
            System.out.println("onReceive MainActivity" + message);
            // System.out.println( actualFragment.getActivity().getSupportFragmentManager().);
            List<Fragment> allFragments = getSupportFragmentManager().getFragments();

            if (allFragments != null) {
                for (Fragment fragment : allFragments) {
                    CustomFragment myFragment = (CustomFragment) fragment;
                    if (myFragment != null && myFragment.getUserVisibleHint()) {
                        // Ausführung wenn Fragment dem User sichtbar ist
                        String[] msgSplit = message.split(";");
                        // Wenn empfangene Nachricht nicht dem angezeigten Fragment entspricht,
                        // das Fragment nicht mit falschen Daten versorgen
                        // Daten können noch von vor dem Fragment/View-Wechsel sein
                        if( msgSplit[0].equals( myFragment.getFragmentName())) {
                            System.out.println( "visibleFragmentName: " + myFragment.getFragmentName());
                            // wenn der Name des Fragments (Empfang) gleich dem angezeigten Fragment ist update die Daten
                            myFragment.UpdateFragmentContent(message);
                            // Fordere nach Empfang direkt neue Daten an für die sichtbare View
                            final CustomFragment finalFragment = myFragment;
                            Runnable getNewData = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep( 2000);
                                    } catch (Exception e) {
                                        System.out.println( e.getMessage());
                                    }
                                    new SendToDataLayerThread( HANDHELD_DATA_PATH, finalFragment.getFragmentName()).start();
                                }
                            };
                            new Thread(getNewData).start();
                        }
                        System.out.println("visible");


                    }
                }
            }


        } // onReceive end
    } // class MessageReceiver end

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter( FragmentManager fm) {
            super(fm);
        }

        @Override
        public CustomFragment getItem(int pos) {
            switch(pos) {
                case 0: {
                    System.out.println( "liveData");
                    return LiveFragment.newInstance("liveData");
                }
                case 1: {
                    System.out.println( "limitWeek");
                    return StatisticFragment.newInstance("limitWeek");
                }
                case 2: {
                    System.out.println( "limitMonth");
                    return StatisticFragment.newInstance("limitMonth");
                }
                case 3: {
                    System.out.println( "limitYear");
                    return StatisticFragment.newInstance("limitYear");
                }
                default: {
                    System.out.println( "Default");
                    return LiveFragment.newInstance("liveData");
                }
            }
        }

        @Override
        public void setPrimaryItem (ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            System.out.println( "setprimaryitem: " + position);
        }

        @Override
        public int getCount() {
            return 4;
        }
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
}
