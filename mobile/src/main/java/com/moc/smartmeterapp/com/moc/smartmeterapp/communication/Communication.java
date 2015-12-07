package com.moc.smartmeterapp.com.moc.smartmeterapp.communication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 28.11.2015.
 */
public class Communication{

    private static Communication instance;

    private DataService dataService;
    private boolean serviceBinded;
    private boolean isRunning;
    private boolean contextSet;

    private Context context;
    private List<IDataEventHandler> dataEventHandlers;

    private ServiceConnection dataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DataService.LocalBinder binder = (DataService.LocalBinder)iBinder;
            dataService = binder.getService();
            serviceBinded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBinded = false;
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            for(IDataEventHandler e : dataEventHandlers) {
                if(e!=null && !e.onLiveDataReceived(Integer.valueOf(intent.getStringExtra("value")))) {
                    unregisterDataEventHandler(e);
                }
            }

            if(dataEventHandlers.size() <= 0){
                unregisterReceiver();
            }
        }
    };

    public Communication() {
        isRunning = false;
        contextSet = false;
        dataEventHandlers = new ArrayList<IDataEventHandler>();;
    }

    public boolean setContext(Context context) {
        if(this.context != null) {
            this.context = context;
            return true;
        }

        return false;
    }

    public static Communication getInstance() {
        if(instance == null) {
            instance = new Communication();
        }

        return instance;
    }

    public void registerDataEventHandler(IDataEventHandler dataEventHandler) {
        if(dataEventHandler != null) {
            if(!isRunning) {
                registerReceiver();
            }

            if(!serviceBinded) {
                bindService();
            }

            dataEventHandlers.add(dataEventHandler);
            Log.d("DEBUG", "registered: " + dataEventHandler.toString());
        }
    }

    public void unregisterDataEventHandler(IDataEventHandler dataEventHandler) {
        if(dataEventHandler != null) {
            dataEventHandlers.remove(dataEventHandler);
            Log.d("DEBUG", "unregistered: " + dataEventHandler.toString());
        }
    }

    private void registerReceiver() {
        if(context != null && broadcastReceiver != null && !isRunning) {
            context.registerReceiver(broadcastReceiver, new IntentFilter(DataService.BROADCAST_ACTION));
            Log.d("DEBUG", "registered broadcast receiver");
            isRunning = true;
        }
    }

    private void unregisterReceiver() {
        if(context != null && broadcastReceiver != null && isRunning) {
            context.registerReceiver(broadcastReceiver, new IntentFilter(DataService.BROADCAST_ACTION));
            Log.d("DEBUG", "unregistered broadcast receiver");
            isRunning = false;
        }
    }

    public void bindService() {
        if(context != null && broadcastReceiver != null && !serviceBinded) {
            context.bindService(new Intent(context, DataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);
            Log.d("DEBUG", "Service binded sucessfully");
        }
    }

    public void unbindService() {
        if(context != null && broadcastReceiver != null && dataServiceConnection != null) {
            if(serviceBinded) {
                context.unbindService(dataServiceConnection);
                Log.d("DEBUG", "Service unbinded sucessfully");
            } else {
                Log.d("DEBUG", "Service allready unbinded");
            }
        }
    }
}
