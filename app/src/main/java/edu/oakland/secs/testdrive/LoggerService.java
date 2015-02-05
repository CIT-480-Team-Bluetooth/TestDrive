package edu.oakland.secs.testdrive;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class handles actually logging the data. It will run in the background even when the
 * app is minimized.
 */
public class LoggerService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Callbacks mCallback;
    private final AtomicBoolean mRunning = new AtomicBoolean(false);

    private static final int MESSAGE_START = 0;
    private static final int MESSAGE_STOP = 1;

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_START:
                    start();
                    break;
                case MESSAGE_STOP:
                    stop();
                    break;
            }
        }

        private void start() {

            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(LoggerService.this, MainActivity.class);
            PendingIntent pending = PendingIntent.getActivity(LoggerService.this, 0, intent, 0);
            Notification n = new Notification.Builder(LoggerService.this)
                    .setContentTitle(getString(R.string.notification_titile))
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

        public void start() {
            mServiceHandler.sendEmptyMessage(MESSAGE_START);
        }

        public void stop() {
            mServiceHandler.sendEmptyMessage(MESSAGE_STOP);
        }

        public void setCallbacks(Callbacks cb) {
            mCallback = cb;
        }

    }

    public IBinder onBind(Intent intent) {
        return new LoggerBinder();
    }

    public static interface Callbacks {
        public void onStartStop(boolean started);
    }


}
