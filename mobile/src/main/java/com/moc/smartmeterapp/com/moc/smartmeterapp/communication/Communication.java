package com.moc.smartmeterapp.com.moc.smartmeterapp.communication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.moc.smartmeterapp.model.DataObject;
import com.moc.smartmeterapp.model.Global;
import com.moc.smartmeterapp.model.Limit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by philipp on 28.11.2015.
 */
public class Communication{

    public interface ILiveDataEventHandler {
        boolean onLiveDataReceived(int value);
    }

    public interface IGlobalDataEventHandler {
        boolean onGlobalDataReceived(Global value);
    }

    public interface ILimitsEventHandler {
        boolean onLimitsReceived(List<Limit> value);
    }

    public interface IMeterDataEventHandler {
        boolean onMeterDataReceived(DataObject value);
    }

    private DataService dataService;
    private boolean serviceBinded;
    private boolean isRegistered;

    public final static int LIVE_DATA = 0;
    public final static int GLOBAL_DATA = 1;
    public final static int LIMITS = 2;
    public final static int METER_DATA = 3;
    public final static int TEST = 4;

    private Context context;
    private List<ILiveDataEventHandler> dataEventHandlers;
    private Integer[] flags;

    private ServiceConnection dataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DataService.LocalBinder binder = (DataService.LocalBinder)iBinder;
            dataService = binder.getService();
            serviceBinded = true;

            Log.d("DEBUG", "Service binded sucessfully");
            Log.d("DEBUG", "Flags: ");

            for(int f : flags) {
                switch(f) {
                    case LIVE_DATA:
                        Log.d("DEBUG", "LIVE_DATA");
                        dataService.startReceiverIfNotRunning();
                        break;
                    case GLOBAL_DATA:
                        Log.d("DEBUG", "GLOBAL_DATA");
                        break;
                    case LIMITS:
                        Log.d("DEBUG", "LIMITS");
                        break;
                    case METER_DATA:
                        Log.d("DEBUG", "METER_DATA");
                        break;
                    case TEST:
                        dataService.startRestTest();
                        Log.d("DEBUG", "TEST");
                        break;
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBinded = false;
        }
    };

    public DataService getServiceHandle() {
        if(dataService != null && serviceBinded)
            return dataService;

        return null;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            for(ILiveDataEventHandler e : dataEventHandlers) {
                if(e!=null && !e.onLiveDataReceived(Integer.valueOf(intent.getStringExtra("value")))) {
                    unregisterDataEventHandler(e);
                }
            }

            if(dataEventHandlers.size() <= 0){
                unregisterReceiver();
            }
        }
    };

    public Communication(Context context, Integer... flags) {
        dataEventHandlers = new ArrayList<ILiveDataEventHandler>();
        this.flags = flags;
        this.context = context;
        isRegistered = false;
    }

    public void registerDataEventHandler(ILiveDataEventHandler dataEventHandler) {
        if(dataEventHandler != null) {
            dataEventHandlers.add(dataEventHandler);
            Log.d("DEBUG", "registered: " + dataEventHandler.toString());

            if(!isRegistered) {
                registerReceiver();
            }
        }
    }

    public void unregisterDataEventHandler(ILiveDataEventHandler dataEventHandler) {
        if(dataEventHandler != null) {
            dataEventHandlers.remove(dataEventHandler);
            Log.d("DEBUG", "unregistered: " + dataEventHandler.toString());
        }

        if(dataEventHandlers.isEmpty()) {
            unregisterReceiver();
        }
    }

    private void registerReceiver() {
        if(context != null && broadcastReceiver != null && !isRegistered) {
            context.registerReceiver(broadcastReceiver, new IntentFilter(DataService.BROADCAST_ACTION));
            Log.d("DEBUG", "registered broadcast receiver");
            isRegistered = true;
        }
    }

    public void unregisterReceiver() {
        if(context != null && broadcastReceiver != null && isRegistered) {
            context.registerReceiver(broadcastReceiver, new IntentFilter(DataService.BROADCAST_ACTION));
            Log.d("DEBUG", "unregistered broadcast receiver");
            isRegistered = false;
        }
    }

    public void  bindService() {
        if(context != null && broadcastReceiver != null && !serviceBinded) {
            context.bindService(new Intent(context, DataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);
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
