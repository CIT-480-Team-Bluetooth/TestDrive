package edu.oakland.secs.testdrive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by jeffq on 2/5/2015.
 */
public class RecordFragment extends Fragment {

    public Spinner mWeatherSpinner;
    public Spinner mRoadTypeSpinner;
    public Spinner mRoadConditionSpinner;
    public Spinner mVisibilitySpinner;
    public Spinner mTrafficSpinner;
    public TextView mTimestampText;
    public Button mSaveButton;

    private ArrayAdapter<CharSequence> mWeatherAdapter;
    private ArrayAdapter<CharSequence> mRoadTypeAdapter;
    private ArrayAdapter<CharSequence> mRoadConditionAdapter;
    private ArrayAdapter<CharSequence> mVisibilityAdapter;
    private ArrayAdapter<CharSequence> mTrafficAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mWeatherSpinner = (Spinner)view.findViewById(R.id.weather_spinner);
        mWeatherAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.weather_items, android.R.layout.simple_spinner_item);
        mWeatherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWeatherSpinner.setAdapter(mWeatherAdapter);

        mRoadTypeSpinner = (Spinner)view.findViewById(R.id.road_type_spinner);
        mRoadTypeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.road_type_items, android.R.layout.simple_spinner_item);
        mRoadTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoadTypeSpinner.setAdapter(mRoadTypeAdapter);

        mRoadConditionSpinner = (Spinner)view.findViewById(R.id.road_conditions_spinner);
        mRoadConditionAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.road_condition_items, android.R.layout.simple_spinner_item);
        mRoadTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoadConditionSpinner.setAdapter(mRoadConditionAdapter);

        mVisibilitySpinner = (Spinner)view.findViewById(R.id.visibility_spinner);
        mVisibilityAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.visibility_items, android.R.layout.simple_spinner_item);
        mVisibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mVisibilitySpinner.setAdapter(mVisibilityAdapter);

        mTrafficSpinner = (Spinner)view.findViewById(R.id.traffic_spinner);
        mTrafficAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.traffic_items, android.R.layout.simple_spinner_item);
        mTrafficAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTrafficSpinner.setAdapter(mTrafficAdapter);

        mTimestampText = (TextView)view.findViewById(R.id.timestamp_text);

        mSaveButton = (Button)view.findViewById(R.id.saveButton);

    }

}
