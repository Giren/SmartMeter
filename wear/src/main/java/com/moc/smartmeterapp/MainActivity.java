package com.moc.smartmeterapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
    LimitFragment limitWeek;
    LimitFragment limitMonth;
    LimitFragment limitYear;

    FragmentData fragmentData;

    ViewPager pager;
    GoogleApiClient googleClient;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        fragmentData = new FragmentData();

        pager = ( ViewPager) findViewById( R.id.viewPager);
        pager.setAdapter( new MyPagerAdapter( getSupportFragmentManager()));

        // Build a new GoogleApiClient for the Wearable API
        googleClient = new GoogleApiClient.Builder( this)
                .addApi( Wearable.API)
                .addConnectionCallbacks( this)
                .addOnConnectionFailedListener( this)
                .build();

        // Set flag keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d("DEBUG", "MainActivity - onDestroy()");
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

    // Placeholder for required connection callbacks
    @Override
    public void onConnectionSuspended( int cause) {
        Log.d("DEBUG", "MainActivity - onConnectionSuspend()");}

    // Placeholder for required connection callbacks
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
            Log.d("DEBUG", "MainActivity - onMessageReceived() - Message path received on watch is: " + messageEvent.getPath());
            Log.d("DEBUG", "MainActivity - onMessageReceived() - Message received on watch is: " + message);

            // handle received message on watch
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    handleReceivedMessage( message);
                }
            });
        } // if end
    }

    public void handleReceivedMessage(String message) {
        Log.d("DEBUG", "MainActivity - handleReceivedMessage(): " + message);
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();

        fragmentData.addData( message);

        if (allFragments != null) {
            for (Fragment fragment : allFragments) {
                CustomFragment myFragment = (CustomFragment) fragment;
                if (myFragment != null && myFragment.getUserVisibleHint()) {
                    // Processed when fragment not null and visible for the user
                    String[] msgSplit = message.split(";");
                    // check that message and fragment name equals
                    if( msgSplit[0].equals(myFragment.getFragmentName())) {
                        Log.d("DEBUG", "MainActivity - handleReceivedMessage() - visibleFragmentName: " + myFragment.getFragmentName());
                        // Content update in visible fragment
                        myFragment.UpdateFragmentContent( message);
                        // send message to get new data
                        final CustomFragment finalFragment = myFragment;

                        final int threadTimeout;

                        if( message.contains( "liveData") && !message.contains( "keepAlive")) {
                            // TODO update limits and redraw tacho
                            break;
                        } else {
                            if( message.contains( "liveData")) {
                                threadTimeout = 500;
                            } else {
                                threadTimeout = 10000;
                            }
                            Runnable getNewData = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep( threadTimeout);
                                    } catch (Exception e) {
                                        System.out.println( e.getMessage());
                                    }
                                    new SendToDataLayerThread( HANDHELD_DATA_PATH, finalFragment.getFragmentName()).start();
                                }
                            };
                            new Thread( getNewData).start();
                        }
                    } // if close
                } // if close
            } // iteration for close
        } // if close
    } // handleReceivedMessage close

    public FragmentData getFragmentData() {
        return this.fragmentData;
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
                        return LimitFragment.newInstance("limitWeek");
                }
                case 2: {
                    System.out.println( "limitMonth");
                    if( limitMonth != null)
                        return limitMonth;
                    else
                        return LimitFragment.newInstance("limitMonth");
                }
                case 3: {
                    System.out.println( "limitYear");
                    if( limitYear != null)
                        return  limitYear;
                    else
                        return LimitFragment.newInstance("limitYear");
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
            Log.d("DEBUG", "MyPagerAdapter - setPrimaryItem(): " + position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    // use to send messages to handheld
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
