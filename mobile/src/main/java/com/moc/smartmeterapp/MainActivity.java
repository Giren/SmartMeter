package com.moc.smartmeterapp;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity{

    private LiveFragment liveFragment;
    private SettingFragment settingFragment;
    private StatisticFragment statisticFragment;
    private TabFragment tabFragment;
    private HelpFragment helpFragment;

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

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
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }
}
