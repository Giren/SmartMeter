package com.moc.smartmeterapp.com.moc.smartmeterapp.communication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.moc.smartmeterapp.model.EntryObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by philipp on 26.11.2015.
 */
public class DataService extends Service {

    private static final int DELAY = 7;

    private final IBinder mBinder = new LocalBinder();
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.mov.smartmeterapp.DataService";

    private boolean receiverIsRunning = false;
    private boolean testIsRunning = false;

    private Socket clientSocket;
    private BufferedReader inFromServer;
    public String message;
    private Intent intent;

    private ComUtils.IRestService restService = ComUtils.createRetrofitService(ComUtils.IRestService.class);
    private Observable networkObservable = Observable.interval(7, 7, TimeUnit.SECONDS);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DEBUG", "Service created...");

        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("DEBUG", "SOMEONE BINDED...");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        stopReceiver();
        stopRestTest();
        Log.d("DEBUG", "Service destroyed...");
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public DataService getService() {

            return DataService.this;
        }
    }

    //TEST DATA RECEIVER

   private Subscriber testSubscriber = new Subscriber() {
        @Override
        public void onCompleted() {
            testIsRunning = false;
        }

        @Override
        public void onError(Throwable e) {
            Log.e("ERROR: ", e.getMessage());
            testIsRunning = false;
        }

        @Override
        public void onNext(Object o) {
            testIsRunning = true;
            Map<String, String> map = new HashMap<String, String>();
            map.put("accessToken", "123456"); // "stats?accessToken=123456"

            restService.getEntryObjectObservable("stats", map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<EntryObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("ERROR: ", e.getMessage());
                        }

                        @Override
                        public void onNext(EntryObject entryObject) {
                            Log.i("Info: DataService", "Got TestData: " + entryObject.getCurrentEnergy());
                            intent.putExtra(String.valueOf(ComUtils.RECIVED_TEST), entryObject);
                            sendBroadcast(intent);
                        }
                    });
        }
    };

    public void startRestTest() {
        if(!testIsRunning) {
            networkObservable.subscribe(testSubscriber);
            Log.d("DEBUG: ", "sucessfully subscribed rest test");
        }
    }

    private void stopRestTest() {
        if(testIsRunning) {
            testSubscriber.unsubscribe();
            Log.d("DEBUG: ", "sucessfully unsubscribed rest test");
        }
    }

    //LIVE DATA RECEIVER

    private Thread socketThread = new Thread() {

        public void run() {
            receiverIsRunning = true;
            Log.d("DEBUG", "Thread startet...");
            while(receiverIsRunning){

                Log.d("DEBUG", "Connectiong to socket...");
                try {
                    //clientSocket = new Socket("192.168.1.65", 9999);

                   // while (!clientSocket.isConnected()) {
                    while( true) {
                        //inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        //message = inFromServer.readLine();
                        message = String.valueOf( 0 + ( (int)( Math.random() * 2500)));
                        if(message != null) {
                            intent.putExtra(String.valueOf(ComUtils.RECIVED_LIVE_DATA), message);
                            sendBroadcast(intent);
                            Log.i("Info: DataService", "Got LiveData: " + message);
                            Thread.sleep( 750);
                        } else {
                            break;
                        }
                    }

                    //clientSocket.close();
                    Log.d("DEBUG", "Connectiong closed...");

//                } catch(UnknownHostException e1) {
//                    Log.d("DEBUG", "UnknownHostException");
//                } catch(IOException e1) {
//                    Log.d("DEBUG", "IOException");
                } catch ( Exception e) {
                    Log.d( "DEBUG", "Exception: " + e.getMessage());
                }

                try {
                    Thread.sleep(3000);
                }catch(InterruptedException e) {

                }
            }
            Log.d("DEBUG", "Thread stopped...");
        }
    };

    public boolean startReceiver() {
        if(!receiverIsRunning) {
            socketThread.start();
            return true;
        }

        return false;
    }

    public void stopReceiver() {
        receiverIsRunning = false;
    }
}
