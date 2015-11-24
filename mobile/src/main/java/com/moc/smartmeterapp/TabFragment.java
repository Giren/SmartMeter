package com.moc.smartmeterapp;

/**
 * Created by michael on 23.11.15.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TabFragment extends Fragment {

    private HomeWeekFragment homeWeekFragment;
    private HomeMonthFragment homeMonthFragment;
    private HomeYearFragment homeYearFragment;

    private static final String[] homeTabs = { "Woche", "Monat", "Jahr"};
    private static final int WEEK = 0;
    private static final int MONTH = 1;
    private static final int YEAR = 2;

    private static final int MAX_HOME_FRAGMENTS = 3;

    public static TabLayout tabLayout;
    public static ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.tab_fragment_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return x;
    }

    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */
        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case WEEK :
                    if(homeWeekFragment == null){
                        homeWeekFragment = new HomeWeekFragment();
                    }
                    return homeWeekFragment;
                case MONTH :
                    if(homeMonthFragment == null){
                        homeMonthFragment = new HomeMonthFragment();
                    }
                    return homeMonthFragment;
                case YEAR :
                    if(homeYearFragment == null){
                        homeYearFragment = new HomeYearFragment();
                    }
                    return homeYearFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return MAX_HOME_FRAGMENTS;
        }

        /**
         * This method returns the title of the tab according to the position.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return homeTabs[position];
        }
    }

}