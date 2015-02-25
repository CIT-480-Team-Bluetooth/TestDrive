package edu.oakland.secs.testdrive;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jeffq on 2/25/2015.
 */
public class DataAdapter extends RecyclerView.Adapter {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mDataEntryText;
        public ImageView mDataEntryImage;

        public ViewHolder(View v) {
            super(v);
            mDataEntryText = (TextView)v.findViewById(R.id.data_entry_text);
            mDataEntryImage = (ImageView)v.findViewById(R.id.data_entry_image);
        }
    }

    private Cursor mCursor;
    private Context mContext;

    private final int START_TIME_INDEX;
    private final int TIME_INDEX;
    private final int WEATHER_CONDITION_INDEX;
    private final int ROAD_TYPE_INDEX;
    private final int ROAD_CONDITION_INDEX;
    private final int VISIBILITY_INDEX;
    private final int TRAFFIC_CONGESTION_INDEX;
    private final int LATITUDE_INDEX;
    private final int LONGITUDE_INDEX;
    private final int SPEED_INDEX;
    private final int BEARING_INDEX;
    private final int DRIVES_ID_INDEX;
    private final int ENTRIES_ID_INDEX;
    private final int[] DIFF_INDICIES;

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public DataAdapter(Context context, Cursor c) {
        mCursor = c;
        mContext = context;
        START_TIME_INDEX = c.getColumnIndex(Database.Contract.Drives.COLUMN_NAME_START_TIME);
        TIME_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_TIME);
        WEATHER_CONDITION_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_WEATHER_CONDITION);
        ROAD_TYPE_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_ROAD_TYPE);
        ROAD_CONDITION_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_ROAD_CONDITION);
        VISIBILITY_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_TRAFFIC_CONGESTION);
        TRAFFIC_CONGESTION_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_TRAFFIC_CONGESTION);
        LATITUDE_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_LATITUDE);
        LONGITUDE_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_LONGITUDE);
        SPEED_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_SPEED);
        BEARING_INDEX = c.getColumnIndex(Database.Contract.Entries.COLUMN_NAME_BEARING);
        DRIVES_ID_INDEX = c.getColumnIndex(Database.Contract.Drives._ID);
        ENTRIES_ID_INDEX = c.getColumnIndex(Database.Contract.Entries.TABLE_NAME  + Database.Contract.Entries._ID);

        DIFF_INDICIES = new int[] {
                WEATHER_CONDITION_INDEX,
                ROAD_TYPE_INDEX,
                ROAD_CONDITION_INDEX,
                VISIBILITY_INDEX,
                TRAFFIC_CONGESTION_INDEX,
                LATITUDE_INDEX,
                LONGITUDE_INDEX,
                SPEED_INDEX,
                BEARING_INDEX
        };

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_view,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder)viewHolder;

        int previousDriveId = -2, currentDriveId = -1;

        if( i > 0 ) {
            mCursor.moveToPosition(i - 1);
            previousDriveId = mCursor.getInt(DRIVES_ID_INDEX);
        }

        mCursor.moveToPosition(i);
        currentDriveId = mCursor.getInt(DRIVES_ID_INDEX);

        if(previousDriveId != currentDriveId) {
            /* start of drive */
            holder.mDataEntryText.setText(mContext.getString(R.string.drive_started_entry, new SimpleDateFormat(TIMESTAMP_FORMAT)
                    .format(new Date(mCursor.getLong(START_TIME_INDEX)))));
            holder.mDataEntryImage.setImageResource(R.drawable.car95);
        }
        else {

            HashMap<Integer, Pair<String, String>> diffMap = new HashMap<Integer, Pair<String, String>>();
            for(int index : DIFF_INDICIES) {
                mCursor.moveToPosition(i - 1);
                String previousVal = mCursor.getString(index);
                mCursor.moveToPosition(i);
                String currentVal = mCursor.getString(index);

                if(((previousVal == null) || (currentVal == null) || !previousVal.equals(currentVal))) {
                    if(previousVal != null || currentVal != null)
                        diffMap.put(index, new Pair<String, String>(previousVal, currentVal));
                }

            }

            StringBuilder builder = new StringBuilder();
            Iterator it = diffMap.entrySet().iterator();
            boolean first = true;
            while(it.hasNext()) {
                Map.Entry<Integer, Pair<String, String>> pair =
                        (Map.Entry<Integer, Pair<String, String>>)it.next();
            }

            if(diffMap.size() == 1) {
                int singleDiffIndex = diffMap.keySet().iterator().next().intValue();
                if(singleDiffIndex == WEATHER_CONDITION_INDEX)
                    holder.mDataEntryImage.setImageResource(R.drawable.cloudstorage);
            }
            else
                holder.mDataEntryImage.setImageResource(android.R.color.transparent);

            holder.mDataEntryText.setText(String.valueOf(mCursor.getInt(START_TIME_INDEX)));

        }

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


}