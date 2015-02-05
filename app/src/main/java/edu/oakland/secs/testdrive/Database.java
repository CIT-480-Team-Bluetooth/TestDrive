package edu.oakland.secs.testdrive;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by jeffq on 2/5/2015.
 */
public class Database extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TestDrive.db";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

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
        }

        public static abstract class RoadTypes implements BaseColumns {
            public static final String TABLE_NAME = "road_types";
            public static final String COLUMN_NAME_TYPES = "type";
        }

        public static abstract class RoadConditions implements BaseColumns {
            public static final String TABLE_NAME = "road_conditions";
            public static final String COLUMN_NAME_CONDITION = "condition";
        }

        public static abstract class Visbilities implements BaseColumns {
            public static final String TABLE_NAME = "visibilities";
            public static final String COLUMN_NAME_VISIBILITY = "visibility";
        }

        public static abstract class TrafficCongestions implements BaseColumns {
            public static final String TABLE_NAME = "traffic_congestions";
            public static final String COLUMN_NAME_CONGESTION = "congestion";
        }

        public static abstract class Entries implements BaseColumns {
            public static final String TABLE_NAME = "entries";
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
        }
    }

}
