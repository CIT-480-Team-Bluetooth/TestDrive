package edu.oakland.secs.testdrive;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * This class handles actually logging the data. It will run in the background even when the
 * app is minimized.
 */
public class LoggerService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Callbacks mCallback;
    private long mThisDriveId;
    private final AtomicBoolean mRunning = new AtomicBoolean(false);
    private long mDriveStartTime;
    private GoogleApiClient mGoogleApiClient;
    private Database mDatabase;

    private static final int MESSAGE_START = 0;
    private static final int MESSAGE_STOP = 1;
    private static final int MESSAGE_LOG = 2;

    private static final String DATA_MODEL = "MODEL";
    private static final String DATA_VIN = "VIN";
    private static final String DATA_NOTES = "NOTES";
    private static final String DATA_WEATHER = Database.Contract.Entries.COLUMN_NAME_WEATHER_CONDITION;
    private static final String DATA_ROAD_TYPE = Database.Contract.Entries.COLUMN_NAME_ROAD_TYPE;
    private static final String DATA_ROAD_CONDITION = Database.Contract.Entries.COLUMN_NAME_ROAD_CONDITION;
    private static final String DATA_VISIBILITY = Database.Contract.Entries.COLUMN_NAME_VISIBILITY;
    private static final String DATA_TRAFFIC = Database.Contract.Entries.COLUMN_NAME_TRAFFIC_CONGESTION;
    private static final String DATA_CONFIRMATION = "CONFIRMATION";


    private final class ServiceHandler extends Handler implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_START:
                    start(msg.getData().getString(DATA_MODEL), msg.getData().getString(DATA_VIN),
                            msg.getData().getString(DATA_NOTES));
                    break;
                case MESSAGE_STOP:
                    stop();
                    break;
                case MESSAGE_LOG:
                    log(msg.getData().getInt(DATA_WEATHER), msg.getData().getInt(DATA_ROAD_TYPE),
                            msg.getData().getInt(DATA_ROAD_CONDITION), msg.getData().getInt(DATA_VISIBILITY),
                            msg.getData().getInt(DATA_TRAFFIC), msg.getData().getBoolean(DATA_CONFIRMATION, true));
                    break;
            }
        }

        private void log(int weatherCondition, int roadType, int roadCondition, int visibility,
                                int traffic, boolean showConfirmation) {

            ContentValues entry = new ContentValues();

            entry.put(Database.Contract.Entries.COLUMN_NAME_DRIVE, mThisDriveId);
            entry.put(Database.Contract.Entries.COLUMN_NAME_TIME, System.currentTimeMillis());
            entry.put(Database.Contract.Entries.COLUMN_NAME_WEATHER_CONDITION, weatherCondition);
            entry.put(Database.Contract.Entries.COLUMN_NAME_ROAD_TYPE, roadType);
            entry.put(Database.Contract.Entries.COLUMN_NAME_ROAD_CONDITION, roadCondition);
            entry.put(Database.Contract.Entries.COLUMN_NAME_VISIBILITY, visibility);
            entry.put(Database.Contract.Entries.COLUMN_NAME_TRAFFIC_CONGESTION, traffic);

            if(mGoogleApiClient != null) {
                if(mGoogleApiClient.isConnected()) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if(location != null) {
                        entry.put(Database.Contract.Entries.COLUMN_NAME_LATITUDE, location.getLatitude());
                        entry.put(Database.Contract.Entries.COLUMN_NAME_LONGITUDE, location.getLongitude());
                        if(location.hasBearing())
                            entry.put(Database.Contract.Entries.COLUMN_NAME_BEARING, location.getBearing());
                        if(location.hasSpeed())
                            entry.put(Database.Contract.Entries.COLUMN_NAME_SPEED, location.getSpeed());
                    }
                }
            }

            synchronized (mDatabase) {
                if (mDatabase.getWritableDatabase().insert(Database.Contract.Entries.TABLE_NAME, null,
                        entry) == -1) {
                    //TODO: Figure out what to do when there's an error saving
                } else if (showConfirmation) {
                    Toast.makeText(LoggerService.this, R.string.saved, Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void start(String model, String vin, String notes) {

            ContentValues drivesValues = new ContentValues();
            drivesValues.put(Database.Contract.Drives.COLUMN_NAME_MODEL, model);
            drivesValues.put(Database.Contract.Drives.COLUMN_NAME_VIN, vin);
            drivesValues.put(Database.Contract.Drives.COLUMN_NAME_NOTES, notes);
            drivesValues.put(Database.Contract.Drives.COLUMN_NAME_START_TIME,
                    mDriveStartTime = System.currentTimeMillis());

            synchronized(mDatabase) {
                mThisDriveId = mDatabase.getWritableDatabase().insert(Database.Contract.Drives.TABLE_NAME,
                        null, drivesValues);
            }

            if( GooglePlayServicesUtil.isGooglePlayServicesAvailable(LoggerService.this) == ConnectionResult.SUCCESS) {
                mGoogleApiClient = new GoogleApiClient.Builder(LoggerService.this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
            else {
                Toast.makeText(LoggerService.this, R.string.google_play_unavailable, Toast.LENGTH_LONG);
            }

            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(LoggerService.this, MainActivity.class);
            PendingIntent pending = PendingIntent.getActivity(LoggerService.this, 0, intent, 0);
            Notification n = new Notification.Builder(LoggerService.this)
                    .setContentTitle(getString(R.string.notification_title))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pending).build();
            n.flags |= Notification.FLAG_ONGOING_EVENT;
            nm.notify(0, n);

            mRunning.set(true);

            if(mCallback != null)
                mCallback.onStartStop(true);
        }

        private void stop() {
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(0);

            mRunning.set(false);

            if(mGoogleApiClient != null) {
                if(mGoogleApiClient.isConnected())
                    mGoogleApiClient.disconnect();
                mGoogleApiClient = null;
            }

            if(mCallback != null)
                mCallback.onStartStop(false);

        }

        public long getDriveStartTime() {
            return mDriveStartTime;
        }

        @Override
        public void onConnected(Bundle connectionHint) {

        }

        @Override
        public void onConnectionSuspended(int cause) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult result) {

        }

    }

    public void onCreate() {

        mDatabase = new Database(this);

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    public class LoggerBinder extends Binder {

        public boolean isRunning() {
            return mRunning.get();
        }

        public void start(String model, String vin, String notes) {
            Message msg = new Message();
            msg.getData().putString(DATA_MODEL, model);
            msg.getData().putString(DATA_VIN, vin);
            msg.getData().putString(DATA_NOTES, notes);
            msg.what = MESSAGE_START;
            mServiceHandler.sendMessage(msg);
        }

        public void stop() {
            mServiceHandler.sendEmptyMessage(MESSAGE_STOP);
        }

        public void setCallbacks(Callbacks cb) {
            mCallback = cb;
        }

        public long getDriveStartTime() {
            return mServiceHandler.getDriveStartTime();
        }

        public void log(int weatherCondition, int roadType, int roadCondition, int visibility,
                        int traffic) {
            Message msg = new Message();
            msg.getData().putInt(DATA_WEATHER, weatherCondition);
            msg.getData().putInt(DATA_ROAD_TYPE, roadType);
            msg.getData().putInt(DATA_ROAD_CONDITION, roadCondition);
            msg.getData().putInt(DATA_VISIBILITY, visibility);
            msg.getData().putInt(DATA_TRAFFIC, traffic);
            msg.what = MESSAGE_LOG;
            mServiceHandler.sendMessage(msg);
        }

        public Database getDatabase() {
            return mDatabase;
        }

    }

    public IBinder onBind(Intent intent) {
        return new LoggerBinder();
    }

    public static interface Callbacks {
        public void onStartStop(boolean started);
    }


}
