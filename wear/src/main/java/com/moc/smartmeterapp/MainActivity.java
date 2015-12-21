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
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

/**
 * Created by David on 23.11.2015.
 */
public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    private static final String WEARABLE_DATA_PATH = "/SmartMeterToWearable";
    private static final String HANDHELD_DATA_PATH = "/SmartMeterToHandheld";

    LiveFragment liveFragment;
    StatisticFragment limitWeek;
    StatisticFragment limitMonth;
    StatisticFragment limitYear;
    MessageApi.MessageListener messageListener;

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
        setContentView( R.layout.main_activity);

        pager = ( ViewPager) findViewById( R.id.viewPager);
        pager.setAdapter( new MyPagerAdapter( getSupportFragmentManager()));

        // Register the local broadcast receiver
        // IntentFilter messageFilter = new IntentFilter( Intent.ACTION_SEND);
        // MessageReceiver messageReceiver = new MessageReceiver();
        // LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder( this)
                .addApi( Wearable.API)
                .addConnectionCallbacks( this)
                .addOnConnectionFailedListener( this)
                .build();


        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // TODO onCreate
    }

    /**
     *
     */
    @Override
    protected void onStop() {
        // sendDataToHandheld("goodbye");
        // remove MessageListener and disconnect googleClient
        if( null != googleClient && googleClient.isConnected()) {
            Wearable.MessageApi.removeListener( googleClient, this);
            googleClient.disconnect();
        }
        super.onStop();
        Log.d("DEBUG", "MainActivity - onStop()");
        System.out.println("onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d("DEBUG", "MainActivity - onDestroy()");
        System.out.println("onDestroy called");
    }

    /**
     * Connect to the data layer when the Activity starts
     */
    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
        Log.d("DEBUG", "MainActivity - onStart()");
    }

    @Override
    public void onConnected( Bundle connectionHint) {
        // register MessageListener
        Wearable.MessageApi.addListener(googleClient, this);
        Log.d("DEBUG", "MainActivity - onConnected()");
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended( int cause) {
        Log.d("DEBUG", "MainActivity - onConnectionSuspend()");}

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {
        Log.d("DEBUG", "MainActivity - onConnectionFailed()");}

    public void sendDataToHandheld(String toHandheld) {
        new SendToDataLayerThread( HANDHELD_DATA_PATH, toHandheld).start();
        Log.d("DEBUG", "MainActivity - sendDataToHandheld()");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equals( WEARABLE_DATA_PATH)) {
            final String message = new String( messageEvent.getData());
            Log.d("DEBUG", "Message path received on watch is: " + messageEvent.getPath());
            Log.d("DEBUG", "Message received on watch is: " + message);

            // verarbeite die empfangene message
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handleReceivedMessage(message);
                }
            });

            // Broadcast message to wearable activity for display
//            Intent messageIntent = new Intent();
//            messageIntent.setAction( Intent.ACTION_SEND);
//            messageIntent.putExtra( "message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast( messageIntent);
        } // if end
    }


    public void handleReceivedMessage(String message) {
        // String message = intent.getStringExtra( "message");
        // Display message in UI
        Log.d("DEBUG", "MainActivity - handleReceivedMessage(): " + message);
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
                                    Thread.sleep( 1000);
                                } catch (Exception e) {
                                    System.out.println( e.getMessage());
                                }
                                new SendToDataLayerThread( HANDHELD_DATA_PATH, finalFragment.getFragmentName()).start();
                            }
                        };
                        new Thread(getNewData).start();
                    }
                    // System.out.println("visible");


                }
            }
        }
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter( FragmentManager fm) {
            super(fm);
        }

        @Override
        public CustomFragment getItem(int pos) {
            switch(pos) {
                case 0: {
                    System.out.println( "liveData");
                    if( liveFragment != null)
                        return liveFragment;
                    else
                        return LiveFragment.newInstance("liveData");
                }
                case 1: {
                    System.out.println( "limitWeek");
                    if( limitWeek != null)
                        return limitWeek;
                    else
                        return StatisticFragment.newInstance("limitWeek");
                }
                case 2: {
                    System.out.println( "limitMonth");
                    if( limitMonth != null)
                        return limitMonth;
                    else
                        return StatisticFragment.newInstance("limitMonth");
                }
                case 3: {
                    System.out.println( "limitYear");
                    if( limitYear != null)
                        return  limitYear;
                    else
                        return StatisticFragment.newInstance("limitYear");
                }
                default: {
                    System.out.println( "Default");
                    if( liveFragment != null)
                        return liveFragment;
                    else
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
                    Log.d("DEBUG", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    // Log on error
                    Log.d("DEBUG", "ERROR: failed to send Message");
                } // if end
            } // for end
        } // run end
    } // class SendToDataLayerThread end
}
