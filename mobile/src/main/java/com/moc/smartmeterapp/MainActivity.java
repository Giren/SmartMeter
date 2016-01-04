package com.moc.smartmeterapp;

import android.app.Activity;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.moc.smartmeterapp.communication.RestCommunication;
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;

public class MainActivity extends AppCompatActivity{

    private LiveFragment liveFragment;
    private SettingFragment settingFragment;
    private StatisticFragment statisticFragment;
    private TabFragment tabFragment;
    private HelpFragment helpFragment;

    private Toolbar toolbar;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        runnable = new Runnable() {
            @Override
            public void run() {
                final Limit limit1 = new Limit();
                final Limit limit2 = new Limit();
                final Limit limit3 = new Limit();

                new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
                    @Override
                    public void onLimitsReceived(Limit limit, int slot) {
                        limit1.setMax(limit.getMax());
                        limit1.setColor(limit.getColor());
                        limit1.setMin(limit.getMin());
                    }

                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onComplete() {
                        new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
                            @Override
                            public void onLimitsReceived(Limit limit, int slot) {
                                limit2.setMax(limit.getMax());
                                limit2.setColor(limit.getColor());
                                limit2.setMin(limit.getMin());
                            }

                            @Override
                            public void onError(String message) {

                            }

                            @Override
                            public void onComplete() {
                                new RestCommunication(getApplicationContext()).fetchLimit(new RestCommunication.ILimitsReceiver() {
                                    @Override
                                    public void onLimitsReceived(Limit limit, int slot) {
                                        limit3.setMax(limit.getMax());
                                        limit3.setColor(limit.getColor());
                                        limit3.setMin(limit.getMin());
                                    }

                                    @Override
                                    public void onError(String message) {

                                    }

                                    @Override
                                    public void onComplete() {
                                        MyPreferences tempPreferences = PreferenceHelper.getPreferences(getApplicationContext());
                                        boolean changes = false;

                                        if(!limit1.equals(tempPreferences.getLimit1())) {
                                            tempPreferences.setLimit1(limit1);
                                            changes = true;
                                        }

                                        if(!limit2.equals(tempPreferences.getLimit2())) {
                                            tempPreferences.setLimit2(limit2);
                                            changes = true;
                                        }

                                        if(!limit3.equals(tempPreferences.getLimit3())) {
                                            tempPreferences.setLimit3(limit3);
                                            changes = true;
                                        }

                                        if(changes){
                                            tempPreferences.setUnSynced(false);
                                            PreferenceHelper.setPreferences(getApplicationContext(), tempPreferences);
                                            PreferenceHelper.sendBroadcast(getApplicationContext(), tempPreferences);
                                        }

                                        handler.postDelayed(runnable, 5000);
                                    }
                                }, 2);
                            }
                        }, 1);
                    }
                }, 0);
            }
        };

        handler = new Handler();
        handler.postDelayed(runnable, 1000);

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
                    if (liveFragment == null) {
                        liveFragment = new LiveFragment();
                    }
                    FragmentTransaction liveFragmentTransaction = mFragmentManager.beginTransaction();
                    liveFragmentTransaction.replace(R.id.containerView, liveFragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_home) {
                    if (tabFragment == null) {
                        tabFragment = new TabFragment();
                    }
                    FragmentTransaction homeFragmentTransaction = mFragmentManager.beginTransaction();
                    homeFragmentTransaction.replace(R.id.containerView, tabFragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_statistic) {
                    if (statisticFragment == null) {
                        statisticFragment = new StatisticFragment();
                    }
                    FragmentTransaction statisticFragmentTransaction = mFragmentManager.beginTransaction();
                    statisticFragmentTransaction.replace(R.id.containerView, statisticFragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_setting) {
                    if (settingFragment == null) {
                        settingFragment = new SettingFragment();
                    }
                    FragmentTransaction settingFragmentTransaction = mFragmentManager.beginTransaction();
                    settingFragmentTransaction.replace(R.id.containerView, settingFragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_help) {
                    if (helpFragment == null) {
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
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);

        super.onDestroy();
    }
}
