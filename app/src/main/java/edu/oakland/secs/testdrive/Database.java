package edu.oakland.secs.testdrive;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by jeffq on 2/5/2015.
 */
public class Database extends SQLiteOpenHelper {

    private Context mContext;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TestDrive.db";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.Drives.CREATE_SQL);
        db.execSQL(Contract.WeatherConditions.CREATE_SQL);
        db.execSQL(Contract.RoadTypes.CREATE_SQL);
        db.execSQL(Contract.RoadConditions.CREATE_SQL);
        db.execSQL(Contract.Visbilities.CREATE_SQL);
        db.execSQL(Contract.TrafficCongestions.CREATE_SQL);
        db.execSQL(Contract.Entries.CREATE_SQL);

        for(String weather_item : mContext.getResources().getStringArray(R.array.weather_items)) {
            ContentValues values = new ContentValues();
            values.put(Contract.WeatherConditions.COLUMN_NAME_CONDITION, weather_item);
            db.insert(Contract.WeatherConditions.TABLE_NAME, null, values);
        }

        for(String road_type : mContext.getResources().getStringArray(R.array.road_type_items)) {
            ContentValues values = new ContentValues();
            values.put(Contract.RoadTypes.COLUMN_NAME_TYPE, road_type);
            db.insert(Contract.RoadTypes.TABLE_NAME, null, values);
        }

        for(String road_condition : mContext.getResources().getStringArray(R.array.road_condition_items)) {
            ContentValues values = new ContentValues();
            values.put(Contract.RoadConditions.COLUMN_NAME_CONDITION, road_condition);
            db.insert(Contract.RoadConditions.TABLE_NAME, null, values);
        }

        for(String visibility_item : mContext.getResources().getStringArray(R.array.visibility_items)) {
            ContentValues values = new ContentValues();
            values.put(Contract.Visbilities.COLUMN_NAME_VISIBILITY, visibility_item);
            db.insert(Contract.Visbilities.TABLE_NAME, null, values);
        }

        for(String congestion_item : mContext.getResources().getStringArray(R.array.traffic_items)) {
            ContentValues values = new ContentValues();
            values.put(Contract.TrafficCongestions.COLUMN_NAME_CONGESTION, congestion_item);
            db.insert(Contract.TrafficCongestions.TABLE_NAME, null, values);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static final class Contract {
        public Contract() {}

        public static abstract class Drives implements BaseColumns {
            public static final String TABLE_NAME = "drives";
            public static final String COLUMN_NAME_MODEL = "model";
            public static final String COLUMN_NAME_VIN = "vin";
            public static final String COLUMN_NAME_NOTES = "notes";
            public static final String COLUMN_NAME_START_TIME = "start_time";
            public static final String COLUMN_NAME_END_TIME = "end_time";

            public static final String CREATE_SQL =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_NAME_MODEL + " TEXT, " +
                            COLUMN_NAME_VIN + " TEXT, " +
                            COLUMN_NAME_NOTES + " TEXT, " +
                            COLUMN_NAME_START_TIME + " INTEGER, " +
                            COLUMN_NAME_END_TIME + " INTEGER)";

        }

        public static abstract class WeatherConditions implements BaseColumns {
            public static final String TABLE_NAME = "weather_conditions";
            public static final String COLUMN_NAME_CONDITION = "condition";

            public static final String CREATE_SQL =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_NAME_CONDITION + " TEXT)";
        }

        public static abstract class RoadTypes implements BaseColumns {
            public static final String TABLE_NAME = "road_types";
            public static final String COLUMN_NAME_TYPE = "type";

            public static final String CREATE_SQL =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_NAME_TYPE + " TEXT)";
        }

        public static abstract class RoadConditions implements BaseColumns {
            public static final String TABLE_NAME = "road_conditions";
            public static final String COLUMN_NAME_CONDITION = "condition";

            public static final String CREATE_SQL =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_NAME_CONDITION + " TEXT)";
        }

        public static abstract class Visbilities implements BaseColumns {
            public static final String TABLE_NAME = "visibilities";
            public static final String COLUMN_NAME_VISIBILITY = "visibility";

            public static final String CREATE_SQL =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_NAME_VISIBILITY + " TEXT)";
        }

        public static abstract class TrafficCongestions implements BaseColumns {
            public static final String TABLE_NAME = "traffic_congestions";
            public static final String COLUMN_NAME_CONGESTION = "congestion";

            public static final String CREATE_SQL =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_NAME_CONGESTION + " TEXT)";
        }

        public static abstract class Entries implements BaseColumns {
            public static final String TABLE_NAME = "entries";
            public static final String COLUMN_NAME_DRIVE = "drive";
            public static final String COLUMN_NAME_TIME = "time";
            public static final String COLUMN_NAME_WEATHER_CONDITION = "weather_condition";
            public static final String COLUMN_NAME_ROAD_TYPE = "road_type";
            public static final String COLUMN_NAME_ROAD_CONDITION = "road_condition";
            public static final String COLUMN_NAME_VISIBILITY = "visibility";
            public static final String COLUMN_NAME_TRAFFIC_CONGESTION = "traffic_congestion";
            public static final String COLUMN_NAME_LATITUDE = "latitude";
            public static final String COLUMN_NAME_LONGITUDE = "longitude";
            public static final String COLUMN_NAME_SPEED = "speed";
            public static final String COLUMN_NAME_BEARING = "bearing";

            public static final String CREATE_SQL =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_NAME_DRIVE + " INTEGER, " +
                            COLUMN_NAME_TIME + " INTEGER, " +
                            COLUMN_NAME_WEATHER_CONDITION + " INTEGER, " +
                            COLUMN_NAME_ROAD_TYPE + " INTEGER, " +
                            COLUMN_NAME_ROAD_CONDITION + " INTEGER, " +
                            COLUMN_NAME_VISIBILITY + " INTEGER, " +
                            COLUMN_NAME_TRAFFIC_CONGESTION + " INTEGER, " +
                            COLUMN_NAME_LATITUDE + " REAL, " +
                            COLUMN_NAME_LONGITUDE + " REAL, " +
                            COLUMN_NAME_SPEED + " REAL, " +
                            COLUMN_NAME_BEARING + " REAL)";
        }
    }

    public static final String GET_DRIVES_AND_ENTRIES_SQL = "SELECT " +
            Contract.Drives.TABLE_NAME + "." + Contract.Drives._ID + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries._ID + " AS " +
            "'" + Contract.Entries.TABLE_NAME + Contract.Entries._ID + "', " +
            Contract.Drives.TABLE_NAME + "." + Contract.Drives.COLUMN_NAME_START_TIME + ", " +
            Contract.Drives.TABLE_NAME + "." + Contract.Drives.COLUMN_NAME_END_TIME + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_TIME + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_WEATHER_CONDITION + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_ROAD_TYPE + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_ROAD_CONDITION + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_VISIBILITY + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_TRAFFIC_CONGESTION + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_LATITUDE + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_LONGITUDE + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_SPEED + ", " +
            Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_BEARING  +
            " FROM " + Contract.Drives.TABLE_NAME + " LEFT JOIN " + Contract.Entries.TABLE_NAME +
            " ON " + Contract.Drives.TABLE_NAME + "." + Contract.Drives._ID +
            " = " + Contract.Entries.TABLE_NAME + "." + Contract.Entries.COLUMN_NAME_DRIVE;


}
