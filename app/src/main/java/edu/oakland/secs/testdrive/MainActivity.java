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
import android.widget.TextView;

import junit.framework.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main activity that holds the entire app
 *
 */
public class MainActivity extends ActionBarActivity {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private LoggerService.LoggerBinder mLoggerBinder;
    private MenuItem mStartStopMenuItem;
    private Timer mTimer;
    private TextView mChronometer;
    private VehicleFragment mVehicleFragment;
    private RecordFragment mRecordFragment;

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

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
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
                    if(started)
                        startTimer();
                    else
                        stopTimer();
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
        mChronometer.setText("");
    }

    private static final int TIMESTAMP_UPDATE_RATE = 1000 / 20;
    private static final int CHRONOMETER_UPDATE_RATE = 1000 / 2;
    private static final int TIMESTAMP_CHRONOMETER_RATIO = CHRONOMETER_UPDATE_RATE / TIMESTAMP_UPDATE_RATE;
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    private int mChronometerCounter = 0;

    private void startTimer() {
        stopTimer();
        if(mLoggerBinder != null) {
            if(mLoggerBinder.isRunning()) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if((mRecordFragment != null) && mRecordFragment.isVisible()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mRecordFragment.mTimestampText.setText(
                                            new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date()));
                                    if(++mChronometerCounter == TIMESTAMP_CHRONOMETER_RATIO) {
                                        long elapsedTime = System.currentTimeMillis() - mLoggerBinder.getDriveStartTime();
                                        long seconds = elapsedTime / 1000;
                                        long hours = seconds / (60 * 60);
                                        long minutesInHour = (seconds % (60 * 60)) / 60;
                                        long secondsInMinute = seconds % 60;
                                        mChronometer.setText(String.format("%d:%02d:%02d",
                                                hours, minutesInHour, secondsInMinute));
                                        mChronometerCounter = 0;
                                    }
                                }
                            });
                        }
                    }
                }, TIMESTAMP_UPDATE_RATE, TIMESTAMP_UPDATE_RATE);
            }
        }
    }

    private void stopTimer() {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private static int CHRONOMETER_ID = 9191;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mStartStopMenuItem = menu.findItem(R.id.action_start_stop);
        mChronometer = new TextView(this);
        mChronometer.setTextAppearance(this, R.style.Base_TextAppearance_AppCompat_Inverse);
        mChronometer.setPadding(5, 0, 5, 0);
        menu.add(0, CHRONOMETER_ID, 2, "Chronometer")
                .setActionView(mChronometer).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
                else if(mVehicleFragment != null)
                    mLoggerBinder.start(mVehicleFragment.mModelText.getText().toString(),
                            mVehicleFragment.mVINText.getText().toString(),
                            mVehicleFragment.mNotesText.getText().toString());
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
                    return mVehicleFragment = new VehicleFragment();
                case FRAGMENT_RECORD:
                    return mRecordFragment = new RecordFragment();
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
