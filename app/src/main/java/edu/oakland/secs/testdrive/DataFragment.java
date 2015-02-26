package edu.oakland.secs.testdrive;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
    public GoogleMap mMap;
    private LinearLayout mLinearLayout;
    private RecyclerView mHistory;
    private DataAdapter mDataAdapter;
    private LinearLayoutManager mLayoutManager;

    /* Saved through view being destroyed and recreated */
    private CameraPosition mCameraPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    private void makeHistoryView(LinearLayout.LayoutParams params) {
        mHistory = new RecyclerView(getActivity());
        mHistory.setLayoutParams(params);
        mHistory.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mHistory.setLayoutManager(mLayoutManager);
        mLinearLayout.addView(mHistory);
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

            makeHistoryView(params);
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

            params.weight = 1;
            makeHistoryView(params);
        }
        else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            mMapFragment.getView().setLayoutParams(params);
        }

        mMap.setMyLocationEnabled(true);

        setAdapter();

    }

    private void setAdapter() {
        new AsyncTask<Void, Void, Cursor>() {

            @Override
            protected Cursor doInBackground(Void... params) {
                MainActivity activity = (MainActivity)getActivity();
                LoggerService.LoggerBinder binder = activity.getLoggerInterface();
                if(binder == null)
                    return null;

                Database db = binder.getDatabase();
                synchronized(db) {
                    return db.getReadableDatabase().rawQuery(Database.GET_DRIVES_AND_ENTRIES_SQL, null);
                }

            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                if(mHistory != null && cursor != null) {
                    mHistory.setAdapter(mDataAdapter = new DataAdapter(DataFragment.this, cursor));
                }
            }

        }.execute();
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
