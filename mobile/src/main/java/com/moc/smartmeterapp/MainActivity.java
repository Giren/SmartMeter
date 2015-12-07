package com.moc.smartmeterapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.moc.smartmeterapp.com.moc.smartmeterapp.communication.Communication;
import com.moc.smartmeterapp.com.moc.smartmeterapp.communication.DataService;
import com.moc.smartmeterapp.com.moc.smartmeterapp.communication.IDataEventHandler;

public class MainActivity extends AppCompatActivity{

    private LiveFragment liveFragment;
    private SettingFragment settingFragment;
    private StatisticFragment statisticFragment;
    private TabFragment tabFragment;
    private HelpFragment helpFragment;

    private ServiceConnection dataServiceConnection;
    private DataService dataService;
    private boolean serviceBinded;

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( Communication.getInstance().setContext(this) == false) {
            Log.d("DEBUG:", "MAIN ACTIVITY: CONTEXT ALLREADY SET");
        }

        dataServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                DataService.LocalBinder binder = (DataService.LocalBinder)iBinder;
                dataService = binder.getService();
                serviceBinded = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        //bindService();

        setContentView(R.layout.activity_main);

        /**
         *Setup the DrawerLayout and NavigationView
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.drawer_menu) ;

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        if(tabFragment == null) {
            tabFragment = new TabFragment();
        }

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,tabFragment).commit();

        /**
         * Setup click events on the Navigation View Items.
         */
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_item_live) {
                    if(liveFragment == null){
                        liveFragment = new LiveFragment();
                    }
                    FragmentTransaction liveFragmentTransaction = mFragmentManager.beginTransaction();
                    liveFragmentTransaction.replace(R.id.containerView, liveFragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_home) {
                    if(tabFragment == null) {
                        tabFragment = new TabFragment();
                    }
                    FragmentTransaction homeFragmentTransaction = mFragmentManager.beginTransaction();
                    homeFragmentTransaction.replace(R.id.containerView, tabFragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_statistic) {
                    if(statisticFragment == null){
                        statisticFragment = new StatisticFragment();
                    }
                    FragmentTransaction statisticFragmentTransaction = mFragmentManager.beginTransaction();
                    statisticFragmentTransaction.replace(R.id.containerView, statisticFragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_setting) {
                    if(settingFragment == null){
                        settingFragment = new SettingFragment();
                    }
                    FragmentTransaction settingFragmentTransaction = mFragmentManager.beginTransaction();
                    settingFragmentTransaction.replace(R.id.containerView, settingFragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_help) {
                    if(helpFragment == null){
                        helpFragment = new HelpFragment();
                    }
                    FragmentTransaction helpFragmentTransaction = mFragmentManager.beginTransaction();
                    helpFragmentTransaction.replace(R.id.containerView, helpFragment).commit();
                }

                return false;
            }
        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    public void startService() {
        startService(new Intent(this, DataService.class));
    }

    public void stopService() {
        stopService(new Intent(this, DataService.class));
    }

    public void bindService() {
        bindService(new Intent(this, DataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);
        serviceBinded = true;
        Toast.makeText(getBaseContext(), "Service binded sucessfully", Toast.LENGTH_LONG).show();
    }

    public void unbindService() {
        if(serviceBinded) {
            unbindService(dataServiceConnection);
            serviceBinded = false;
            Toast.makeText(getBaseContext(), "Service unbinded sucessfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Service allready unbinded", Toast.LENGTH_LONG).show();
        }
    }
}
