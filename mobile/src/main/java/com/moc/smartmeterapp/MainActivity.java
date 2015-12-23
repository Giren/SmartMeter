package com.moc.smartmeterapp;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.moc.smartmeterapp.communication.LiveDataService;
import com.moc.smartmeterapp.communication.RestCommunication;
import com.moc.smartmeterapp.database.IDatabase;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.DataObject;

public class MainActivity extends AppCompatActivity{

    private LiveFragment liveFragment;
    private SettingFragment settingFragment;
    private StatisticFragment statisticFragment;
    private TabFragment tabFragment;
    private HelpFragment helpFragment;

    private Toolbar toolbar;
    private ServiceConnection dataServiceConnection;
    private LiveDataService liveDataService;
    private boolean serviceBinded;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private RestCommunication restCommunication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        restCommunication = new RestCommunication();
        restCommunication.fetchYearData(0, new RestCommunication.IDataReceiver() {
            @Override
            public void onDataReceived(DataObject dataObject) {
                Toast toast = Toast.makeText(getApplicationContext(), "Synchronisierung läuft...", Toast.LENGTH_LONG);
                toast.show();
                if(dataObject != null) {
                    IDatabase dbHelper = new MeterDbHelper(getApplicationContext());
                    dbHelper.createIfNotCreated();
                    dbHelper.saveYear(dataObject.getDays());
                }
            }

            @Override
            public void onError(String message) {
                if(message != null)
                    Log.e("ERROR:", message);

                Toast toast = Toast.makeText(getApplicationContext(), "Synchronisierung fehlgeschlagen...", Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onComplete() {
                Toast toast = Toast.makeText(getApplicationContext(), "Synchronisierung erfolgreich...", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

}
