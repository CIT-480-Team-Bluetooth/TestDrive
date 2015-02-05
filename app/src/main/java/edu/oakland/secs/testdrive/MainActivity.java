package edu.oakland.secs.testdrive;

import android.app.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
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

/**
 * Main activity that holds the entire app
 *
 */
public class MainActivity extends ActionBarActivity {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private LoggerService.LoggerBinder mLoggerBinder;
    private MenuItem mStartStopMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new TestDrivePageAdapter(getSupportFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        Intent loggerIntent = new Intent(this, LoggerService.class);
        startService(loggerIntent);
        bindService(loggerIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLoggerBinder = (LoggerService.LoggerBinder)service;
            mLoggerBinder.setCallbacks(new LoggerService.Callbacks() {
                @Override
                public void onStartStop(boolean started) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            syncStartStop();
                        }
                    });
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLoggerBinder = null;
        }
    };

    public LoggerService.LoggerBinder getLoggerInterface() {
        return mLoggerBinder;
    }

    private void syncStartStop() {
        if(mLoggerBinder == null || mStartStopMenuItem == null)
            return;
        mStartStopMenuItem.setIcon(mLoggerBinder.isRunning() ?
                R.drawable.ic_stop : R.drawable.ic_play);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mStartStopMenuItem = menu.findItem(R.id.action_start_stop);
        syncStartStop();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_start_stop) {
            if(mLoggerBinder != null) {
                if(mLoggerBinder.isRunning())
                    mLoggerBinder.stop();
                else
                    mLoggerBinder.start();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static final int FRAGMENT_VEHICLE = 0;
    public static final int FRAGMENT_RECORD = 1;
    public static final int FRAGMENT_LIVE = 2;
    public static final int FRAGMENT_DATA = 3;
    public static final int FRAGMENT_EXPORT = 4;

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
                case FRAGMENT_DATA:
                    return new DataFragment();
                case FRAGMENT_LIVE:
                    return new LiveFragment();
                case FRAGMENT_EXPORT:
                    return new ExportFragment();
                default:
                    return null;
            }
        }

        public int getCount() {
            return 5;
        }

        public CharSequence getPageTitle(int position) {
            switch(position) {
                case FRAGMENT_VEHICLE:
                    return getString(R.string.vehicle);
                case FRAGMENT_RECORD:
                    return getString(R.string.record);
                case FRAGMENT_DATA:
                    return getString(R.string.data);
                case FRAGMENT_LIVE:
                    return getString(R.string.live);
                case FRAGMENT_EXPORT:
                    return getString(R.string.export);
                default:
                    return null;
            }
        }

    }

}
