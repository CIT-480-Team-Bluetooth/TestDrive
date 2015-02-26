package edu.oakland.secs.testdrive;

import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by jeffq on 2/5/2015.
 */
public class ExportFragment extends TestDriveFragment {

    public Button mCopyDBButton;
    public Button mClearDBButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_export, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mClearDBButton = (Button)view.findViewById(R.id.clear_db_button);
        mClearDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)getActivity();
                LoggerService.LoggerBinder binder = mainActivity.getLoggerInterface();
                if(binder != null)
                    binder.clearDatabase();
            }
        });

        mCopyDBButton = (Button)view.findViewById(R.id.copy_db_button);
        mCopyDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {

                        try {
                            File src = getActivity().getDatabasePath(Database.DATABASE_NAME);
                            String destPath = getActivity().getExternalFilesDir(null).getPath();
                            destPath = destPath.substring(0, destPath.lastIndexOf('/')).concat("/").concat(Database.DATABASE_NAME);
                            File dest = new File(destPath);
                            copyFile(src, dest);
                            MediaScannerConnection.scanFile(getActivity(), new String[] { dest.getPath() },
                                    null, null); //make it visible on external storage
                            return destPath;
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if(result != null) {
                            Toast.makeText(getActivity(), getString(R.string.copied_db, result), Toast.LENGTH_LONG).show();
                        }
                    }

                }.execute();
            }
        });
    }

    public static Boolean copyFile(File sourceFile, File destFile)
            throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null)
                    source.close();
                if (destination != null)
                    destination.close();
            }
            return true;
        }
        return false;
    }

    @Override
    public void syncStartStop(boolean started) {
        if(mClearDBButton != null)
            mClearDBButton.setEnabled(!started);
    }
}
