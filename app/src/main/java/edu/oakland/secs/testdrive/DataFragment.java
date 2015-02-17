package edu.oakland.secs.testdrive;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;

/**
 * Created by jeffq on 2/5/2015.
 */
public class DataFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private LinearLayout mLinearLayout;
    private ListView mHistory;

    /* Saved through view being destroyed and recreated */
    private CameraPosition mCameraPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mLinearLayout = (LinearLayout)view.findViewById(R.id.data_layout);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            mLinearLayout.setWeightSum(3);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 2;

            mHistory = new ListView(getActivity());
            mHistory.setLayoutParams(params);
            mLinearLayout.addView(mHistory);
        }

        GoogleMapOptions mapOptions = new GoogleMapOptions();
        if(mCameraPosition != null)
            mapOptions.camera(mCameraPosition);

        mMapFragment = SupportMapFragment.newInstance(mapOptions);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.data_layout, mMapFragment);
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {

            mLinearLayout.setWeightSum(2);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            mMapFragment.getView().setLayoutParams(params);

            mHistory = new ListView(getActivity());
            params.weight = 1;
            mHistory.setLayoutParams(params);
            mLinearLayout.addView(mHistory);

        }
        else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            mMapFragment.getView().setLayoutParams(params);
        }

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mMap != null) {
            mCameraPosition = mMap.getCameraPosition();
        }

        mMapFragment = null;
        mMap = null;
        mLinearLayout = null;
        mHistory = null;
    }
}
