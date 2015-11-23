package com.moc.smartmeterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {
    public final static String EXTRA_MESSAGE = "com.moc.SmartMeterApp.MESSAGE";

    private static final String[] osArray = {"Home", "Live", "Statistik"};
    private static final int HOME_VIEW = 0;
    private static final int LIVE_VIEW = 1;
    private static final int STATISTIC_VIEW = 2;
    private ArrayAdapter<String> mAdapter;

    protected ListView mDrawerList;
    protected FrameLayout frameLayout;
    protected int position;

    MyPageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        frameLayout = (FrameLayout) findViewById(R.id.contentPanel);

        mDrawerList = (ListView) findViewById(R.id.naviList);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case HOME_VIEW:
                        Toast.makeText(MainActivity.this, "Home-Ansicht", Toast.LENGTH_SHORT).show();
                        viewHome(view);
                        break;
                    case LIVE_VIEW:
                        Toast.makeText(MainActivity.this, "Live-Ansicht", Toast.LENGTH_SHORT).show();
                        viewLive(view);
                        break;
                    case STATISTIC_VIEW:
                        Toast.makeText(MainActivity.this, "Statistik-Ansicht", Toast.LENGTH_SHORT).show();
                        viewStatistic(view);
                        break;
                }
            }
        });
        addDrawerItems();
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();

        fragmentList.add(MyFragment.newInstance("week"));
        fragmentList.add(MyFragment.newInstance("month"));
        fragmentList.add(MyFragment.newInstance("year"));

        return fragmentList;
    }

    private void addDrawerItems(){
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,osArray);
        mDrawerList.setAdapter(mAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void viewLive(View view){
        Intent intent = new Intent(this, Live.class);
//        String msg = "this is the LiveView";
//        intent.putExtra(EXTRA_MESSAGE,msg);
        startActivity(intent);
    }

    public void viewStatistic(View view){
        Intent intent = new Intent(this, Statistic.class);
//        String msg = "this is the statisticView";
//        intent.putExtra(EXTRA_MESSAGE,msg);
        startActivity(intent);
    }
}
