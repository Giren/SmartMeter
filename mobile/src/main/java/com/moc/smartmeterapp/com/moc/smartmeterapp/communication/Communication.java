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
import com.moc.smartmeterapp.model.EntryObject;
import com.moc.smartmeterapp.model.Global;
import com.moc.smartmeterapp.model.Limit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by philipp on 28.11.2015.
 */
public class Communication{

    public interface IDataEvent {
        boolean onLiveDataReceived(int value);
        boolean onGlobalDataReceived(Global global);
        boolean onLimitsReceived(List<Limit> limits);
        boolean onMeterDataReceived(DataObject dataObject);
        boolean onTestReceived(EntryObject entryObject);
    }

    private DataService dataService;
    private boolean serviceBinded;
    private boolean isRegistered;

    private Context context;
    private List<IDataEvent> dataEventHandlers;
    private List<Integer> flags;

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
                    case ComUtils.LIVE_DATA:
                        Log.d("DEBUG", "LIVE_DATA");
                        dataService.startReceiver();
                        break;
                    case ComUtils.GLOBAL_DATA:
                        Log.d("DEBUG", "GLOBAL_DATA");
                        break;
                    case ComUtils.LIMITS:
                        Log.d("DEBUG", "LIMITS");
                        break;
                    case ComUtils.METER_DATA:
                        Log.d("DEBUG", "METER_DATA");
                        break;
                    case ComUtils.TEST:
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

            for(IDataEvent e : dataEventHandlers) {
                if(e!=null) {

                    //Handle Live Data
                    if(flags.contains(ComUtils.LIVE_DATA)) {
                        String temp = intent.getStringExtra(String.valueOf(ComUtils.RECIVED_LIVE_DATA));

                        if(temp != null && !temp.isEmpty()) {
                            if(!e.onLiveDataReceived(Integer.valueOf(temp))) {
                                unregisterDataEventHandler(e);
                            }
                        }
                    }

                    //Handle Global
                    if(flags.contains(ComUtils.GLOBAL_DATA)) {
                        Global global = (Global) intent.getSerializableExtra(String.valueOf(ComUtils.RECIVED_GLOBAL_DATA));

                        if(global != null) {
                            if(!e.onGlobalDataReceived(global)) {
                                unregisterDataEventHandler(e);
                            }
                        }
                    }

                    //Handle Limits
                    if(flags.contains(ComUtils.LIMITS)) {
                        List<Limit> limits = (ArrayList<Limit>) intent.getSerializableExtra(String.valueOf(ComUtils.RECIVED_LIMITS));

                        if (limits != null) {
                            if(!e.onLimitsReceived(limits)) {
                                unregisterDataEventHandler(e);
                            }
                        }
                    }

                    //Handle Meter Data
                    if(flags.contains(ComUtils.METER_DATA)) {
                        DataObject dataObject = (DataObject) intent.getSerializableExtra(String.valueOf(ComUtils.RECIVED_METER_DATA));

                        if (dataObject != null) {
                            if(!e.onMeterDataReceived(dataObject))  {
                                unregisterDataEventHandler(e);
                            }
                        }
                    }

                    //Handle Test
                    if(flags.contains(ComUtils.TEST)) {
                        EntryObject entryObject = (EntryObject) intent.getSerializableExtra(String.valueOf(ComUtils.RECIVED_TEST));

                        if (entryObject != null) {
                            if(!e.onTestReceived(entryObject)) {
                                unregisterDataEventHandler(e);
                            }
                        }
                    }
                }
            }

            if(dataEventHandlers.size() <= 0){
                unregisterReceiver();
            }
        }
    };

    public Communication(Context context, Integer... flags) {
        dataEventHandlers = new ArrayList<IDataEvent>();
        this.flags = Arrays.asList(flags);
        this.context = context;
        isRegistered = false;
    }

    public void registerDataEventHandler(IDataEvent dataEventHandler) {
        if(dataEventHandler != null) {
            dataEventHandlers.add(dataEventHandler);
            Log.d("DEBUG", "registered: " + dataEventHandler.toString());

            if(!isRegistered) {
                registerReceiver();
            }
        }
    }

    public void unregisterDataEventHandler(IDataEvent dataEventHandler) {
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
            context.unregisterReceiver(broadcastReceiver);
            Log.d("DEBUG", "unregistered broadcast receiver");
            isRegistered = false;
        }
    }

    public void  bindService() {
        if(context != null && !serviceBinded) {
            context.bindService(new Intent(context, DataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);
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
}
