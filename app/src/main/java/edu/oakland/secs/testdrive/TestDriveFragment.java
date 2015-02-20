package edu.oakland.secs.testdrive;

import android.support.v4.app.Fragment;

/**
 * Created by jeffq on 2/20/2015.
 */
public abstract class TestDriveFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();

        MainActivity activity = (MainActivity)getActivity();
        LoggerService.LoggerBinder binder = activity.getLoggerInterface();
        if(binder != null)
            syncStartStop(binder.isRunning());
    }

    public abstract void syncStartStop(boolean started);
}
