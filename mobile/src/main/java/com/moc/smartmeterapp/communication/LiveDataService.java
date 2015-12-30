package com.moc.smartmeterapp.communication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.moc.smartmeterapp.MainActivity;
import com.moc.smartmeterapp.model.EntryObject;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;

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
public class LiveDataService extends Service implements PreferenceHelper.PrefReceive {

    private static final int TESTDELAY = 10;
    private final int PORT = 9999;

    private final IBinder mBinder = new LocalBinder();
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.moc.smartmeterapp.LiveDataService";

    private boolean receiverIsRunning = false;
    private boolean testIsRunning = false;

    private Socket clientSocket;
    private BufferedReader inFromServer;
    public String message;
    private Intent intent;

    private PreferenceHelper preferenceHelper;
    private MyPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DEBUG", "Service created...");

        intent = new Intent(BROADCAST_ACTION);

        prefs = new MyPreferences();

        MyPreferences tempPref = PreferenceHelper.getPreferences(getApplicationContext());
        if(tempPref != null) {
            prefs = tempPref;
        } else {
            prefs.setIpAddress("127.0.0.1");
        }

        preferenceHelper = new PreferenceHelper();
        preferenceHelper.register(this);
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

    @Override
    public void onPrefReceive(MyPreferences pref) {
        this.prefs = prefs;
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

                Log.d("DEBUG", "Connecting to socket at " + prefs.getIpAddress() + "...");
                try {
                    clientSocket = new Socket(prefs.getIpAddress(), PORT);

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
