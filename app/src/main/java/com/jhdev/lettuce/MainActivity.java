package com.jhdev.lettuce;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.app.ActionBar.Tab;

public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = {"one", "2", "trois"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    // Top Rated fragment activity
                    return new BlankFragment();
                case 1:
                    // Games fragment activity
                    return new BlankFragment();
                case 2:
                    // Movies fragment activity
                    return new BlankFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 3;
        }

    }

    /**
     * on swiping the viewpager make respective tab selected
     * */


}