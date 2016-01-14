package com.moc.smartmeterapp.communication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.moc.smartmeterapp.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by philipp on 28.11.2015.
 */
public class LiveCommunication {

    public static final String LIVE_DATA = "live_data";

    public interface ILiveDataEvent {
        void onLiveDataReceived(int value);
    }

    private LiveDataService liveDataService;
    private boolean serviceBinded;
    private boolean isRegistered;

    private Context context;
    private List<ILiveDataEvent> dataEventHandlers;

    private ServiceConnection dataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LiveDataService.LocalBinder binder = (LiveDataService.LocalBinder)iBinder;
            liveDataService = binder.getService();
            serviceBinded = true;

            liveDataService.startReceiver();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBinded = false;
        }
    };

    public LiveDataService getServiceHandle() {
        if(liveDataService != null && serviceBinded)
            return liveDataService;

        return null;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra(String.valueOf(LIVE_DATA));

            if(temp != null && !temp.isEmpty()) {
                for(ILiveDataEvent e : dataEventHandlers) {
                    if(e != null) {
                        try {
                            e.onLiveDataReceived(Integer.valueOf(temp));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            Log.e("ERROR IN LIVEDATA", "CALLBACK NULL");
                            dataEventHandlers.remove(e);
                        }
                    }
                }
            }
        }
    };

    public LiveCommunication(Context context) {
        dataEventHandlers = new ArrayList<ILiveDataEvent>();
        this.context = context;
        isRegistered = false;
    }

    public void registerDataEventHandler(ILiveDataEvent dataEventHandler) {
        if(dataEventHandler != null) {
            dataEventHandlers.add(dataEventHandler);
            Log.d("DEBUG", "registered: " + dataEventHandler.toString());

            if(!isRegistered) {
                registerReceiver();
            }
        }
    }

    public void unregisterDataEventHandler(ILiveDataEvent dataEventHandler) {
        if(dataEventHandler != null) {
            dataEventHandlers.remove(dataEventHandler);
            Log.d("DEBUG", "unregistered: " + dataEventHandler.toString());
        }

        if(dataEventHandlers.isEmpty()) {
            unregisterReceiver();
        }
    }

    public void create() {
        registerReceiver();
        bindService();
    }

    public void registerReceiver() {
        if(context != null && broadcastReceiver != null && !isRegistered) {
            context.registerReceiver(broadcastReceiver, new IntentFilter(LiveDataService.BROADCAST_ACTION));
            Log.d("DEBUG", "registered broadcast receiver");
            isRegistered = true;
        }
    }

    public void unregisterReceiver() {
        if(context != null && broadcastReceiver != null && isRegistered) {
            context.unregisterReceiver(broadcastReceiver);
            Log.d("DEBUG", "unregistered broadcast receiver");
            isRegistered = false;
        }
    }

    public void  bindService() {
        if(context != null && !serviceBinded) {
            context.bindService(new Intent(context, LiveDataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void unbindService() {
        if(context != null && dataServiceConnection != null) {
            if(serviceBinded) {
                context.unbindService(dataServiceConnection);
                Log.d("DEBUG", "Service unbinded sucessfully");
            } else {
                Log.d("DEBUG", "Service allready unbinded");
            }
        }
    }

    public void destroy() {
        unregisterReceiver();
        dataEventHandlers.clear();
        unbindService();
    }
}
