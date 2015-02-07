package edu.oakland.secs.testdrive;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
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
    private boolean mGooglePlayAvailable;
    private long mThisDriveId;
    private final AtomicBoolean mRunning = new AtomicBoolean(false);
    private long mDriveStartTime;

    private static final int MESSAGE_START = 0;
    private static final int MESSAGE_STOP = 1;

    private static final String DATA_MODEL = "MODEL";
    private static final String DATA_VIN = "VIN";
    private static final String DATA_NOTES = "NOTES";



    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        private Database db;

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_START:
                    start(msg.getData().getString(DATA_MODEL), msg.getData().getString(DATA_VIN),
                            msg.getData().getString(DATA_NOTES));
                    break;
                case MESSAGE_STOP:
                    stop();
                    break;
            }
        }

        private void start(String model, String vin, String notes) {

            db = new Database(LoggerService.this);

            ContentValues drivesValues = new ContentValues();
            drivesValues.put(Database.Contract.Drives.COLUMN_NAME_MODEL, model);
            drivesValues.put(Database.Contract.Drives.COLUMN_NAME_VIN, vin);
            drivesValues.put(Database.Contract.Drives.COLUMN_NAME_NOTES, notes);
            drivesValues.put(Database.Contract.Drives.COLUMN_NAME_START_TIME,
                    mDriveStartTime = System.currentTimeMillis());

            mThisDriveId = db.getWritableDatabase().insert(Database.Contract.Drives.TABLE_NAME,
                    null, drivesValues);

            mGooglePlayAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(LoggerService.this) == ConnectionResult.SUCCESS;
            if(mGooglePlayAvailable) {

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

            if(mCallback != null)
                mCallback.onStartStop(false);

        }

        public long getDriveStartTime() {
            return mDriveStartTime;
        }

    }

    public void onCreate() {
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


    }

    public IBinder onBind(Intent intent) {
        return new LoggerBinder();
    }

    public static interface Callbacks {
        public void onStartStop(boolean started);
    }


}
