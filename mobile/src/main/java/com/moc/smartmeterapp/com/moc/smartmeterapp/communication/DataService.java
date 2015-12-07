package com.moc.smartmeterapp.com.moc.smartmeterapp.communication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 26.11.2015.
 */
public class DataService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private boolean isRunning;
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.mov.smartmeterapp.DataService";
    private Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DEBUG", "Service created...");

        intent = new Intent(BROADCAST_ACTION);
        startReceiver();
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

    public void startReceiver() {
        socketThread.start();
    }

    public void stopReceiver() {
        isRunning = false;
    }

    private Thread socketThread = new Thread() {

        public void run() {
            isRunning = true;
            Log.d("DEBUG", "Thread startet...");
            while(isRunning){
                try {
                    if(intent != null) {
                        Log.d("DEBUG", "Thread tick...");
                        intent.putExtra("value", String.valueOf(0 + (int)(Math.random()*2000)));
                        sendBroadcast(intent);
                    }
                    Thread.sleep(2000);
                } catch(Exception e) {

                }
            }
            Log.d("DEBUG", "Thread stopped...");
        }
    };
}
