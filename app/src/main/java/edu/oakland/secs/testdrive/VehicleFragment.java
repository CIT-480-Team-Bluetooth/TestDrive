package edu.oakland.secs.testdrive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jeffq on 2/5/2015.
 */
public class VehicleFragment extends Fragment  {

    public TextView mModelText;
    public TextView mVINText;
    public TextView mNotesText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehicle, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mModelText = (TextView)view.findViewById(R.id.model_text);
        mVINText = (TextView)view.findViewById(R.id.vin_text);
        mNotesText = (TextView)view.findViewById(R.id.notes_text);
    }
}
