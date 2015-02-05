package edu.oakland.secs.testdrive;

import android.app.Activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import junit.framework.Test;


public class MainActivity extends ActionBarActivity {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new TestDrivePageAdapter(getSupportFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

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

    public static final int FRAGMENT_VEHICLE = 0;
    public static final int FRAGMENT_RECORD = 1;

    public class TestDrivePageAdapter extends FragmentPagerAdapter {

        public TestDrivePageAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            switch(position) {
                case FRAGMENT_VEHICLE:
                    return new VehicleFragment();
                case FRAGMENT_RECORD:
                    return new RecordFragment();
                default:
                    return null;
            }
        }

        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            switch(position) {
                case FRAGMENT_VEHICLE:
                    return getString(R.string.vehicle);
                case FRAGMENT_RECORD:
                    return getString(R.string.record);
                default:
                    return null;
            }
        }

    }

}
