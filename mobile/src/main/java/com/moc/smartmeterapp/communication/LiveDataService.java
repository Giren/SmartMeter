package com.moc.smartmeterapp.communication;

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
import rx.schedulers.Schedulers;

/**
 * Created by philipp on 26.11.2015.
 */
public class LiveDataService extends Service {

    private static final int TESTDELAY = 10;

    private final IBinder mBinder = new LocalBinder();
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.mov.smartmeterapp.LiveDataService";

    private boolean receiverIsRunning = false;
    private boolean testIsRunning = false;

    private Socket clientSocket;
    private BufferedReader inFromServer;
    public String message;
    private Intent intent;

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
        Log.d("DEBUG", "Service destroyed...");
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public LiveDataService getService() {

            return LiveDataService.this;
        }
    }

    private Thread socketThread = new Thread() {

        public void run() {
            receiverIsRunning = true;
            Log.d("DEBUG", "Thread startet...");
            while(receiverIsRunning){

                Log.d("DEBUG", "Connectiong to socket...");
                try {
                    clientSocket = new Socket("10.0.0.20", 9999);

                    while (clientSocket.isConnected()) {
                        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        message = inFromServer.readLine();
                        if(message != null) {
                            intent.putExtra(LiveCommunication.LIVE_DATA, message);
                            sendBroadcast(intent);
                        } else {
                            break;
                        }
                    }

                    clientSocket.close();

                } catch(UnknownHostException e1) {
                    Log.d("DEBUG", "UnknownHostException");
                } catch(IOException e1) {
                    Log.d("DEBUG", "IOException");
                } finally {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.e("EXEPTION:", e.getMessage());
                    }
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
