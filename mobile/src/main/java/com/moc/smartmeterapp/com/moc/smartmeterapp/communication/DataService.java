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
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by philipp on 26.11.2015.
 */
public class DataService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private boolean isRunning = false;
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.mov.smartmeterapp.DataService";

    private Socket clientSocket;
    private BufferedReader inFromServer;
    public String message;
    private Intent intent;

    private ComUtils.IRestTestService restService = ComUtils.createRetrofitService(ComUtils.IRestTestService.class);
    private Observable test = Observable.interval(1, 3, TimeUnit.SECONDS);


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
        super.onDestroy();
        isRunning = false;
        Log.d("DEBUG", "Service destroy...");
    }

    public class LocalBinder extends Binder {
        public DataService getService() {

            return DataService.this;
        }
    }

    public void startRestTest() {
        test.subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                restService.getEntryObjectObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<EntryObject>() {

                            @Override
                            public void onCompleted() {
                                Log.d("DEBUG", "onComplete");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("DEBUG", "onError: " + e.getMessage());
                            }

                            @Override
                            public void onNext(EntryObject entryObject) {
                                Log.d("DEBUG", "onNext: EntryObject " + entryObject.getCurrentEnergy());
                            }
                        });
            }
        });
    }

    private void stopRestTest() {
       //DO UNSUBSCRIBE
    }

    public void startReceiver() {
        socketThread.start();
    }

    public boolean startReceiverIfNotRunning() {
        if(!isRunning) {
            startReceiver();
            return true;
        }

        return false;
    }

    public void stopReceiver() {
        isRunning = false;
    }

    private Thread socketThread = new Thread() {

        public void run() {
            isRunning = true;
            Log.d("DEBUG", "Thread startet...");
            while(isRunning){

                Log.d("DEBUG", "Connectiong to socket...");
                try {
                    clientSocket = new Socket("10.0.0.20", 9999);

                    while (clientSocket.isConnected()) {
                        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        message = inFromServer.readLine();
                        if(message != null) {
                            Log.d("DEBUG", "Live Data: " + message);
                            intent.putExtra("value", message);
                            sendBroadcast(intent);
                        } else {
                            break;
                        }
                    }

                    clientSocket.close();
                    Log.d("DEBUG", "Connectiong closed...");

                } catch(UnknownHostException e1) {
                    Log.d("DEBUG", "UnknownHostException");
                } catch(IOException e1) {
                    Log.d("DEBUG", "IOException");
                }

                try {
                    Thread.sleep(3000);
                }catch(InterruptedException e) {

                }
            }
            Log.d("DEBUG", "Thread stopped...");
        }
    };
}
