package com.mabe.productions.hrv_madison.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    /*
     * HRV-Madison SQLite database architecture
     *
     * LF/HF/VHF columns are to be added later
     * ###########################
     * #        HRV DATA         #
     * ###########################
     * #  ID  #  DATE  #  RMSSD  #
     * ###########################
     *
     * ############################
     * #       JOGGING DATA       #
     * ############################
     * #  ID  #  TIME  # DISTANCE #
     * ############################
     *
     * Initial user data is stored in SharedPreference, called user_data
     *
     */

    public static final String SHARED_PREFS_USER_DATA = "user_data";

    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_KMI = "KMI";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_BIRTHDAY = "birthday";
    public static final String FIELD_ACTIVITY_INDEX = "activity_index";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_BASE_DURATION = "base_duration";
    public static final String FIELD_SELECTED_SPORT = "selected_sport";
    public static final String FIELD_FIRST_TIME = "first_time";
    public static final String FIELD_WEEK_DAYS = "first_time";



    private static final String SQL_CREATE_HRV_DATA_TABLE_ENTRIES =
            "CREATE TABLE " + FeedReaderDbHelper.HRV_DATA_TABLE_NAME + " (" +
                    FeedReaderDbHelper.COL_ID + " INTEGER PRIMARY KEY," +
                    FeedReaderDbHelper.COL_DATE + " STRING," +
                    FeedReaderDbHelper.COL_RMSSD + " INTEGER)";

    private static final String SQL_CREATE_JOGGING_TABLE_ENTRIES =
            "CREATE TABLE " + FeedReaderDbHelper.JOGGING_TABLE_NAME + " (" +
                    FeedReaderDbHelper.COL_ID + " INTEGER PRIMARY KEY," +
                    FeedReaderDbHelper.COL_JOG_TIME + " REAL," +
                    FeedReaderDbHelper.COL_JOG_DISTANCE + " REAL)";

;

    public static final String HRV_DATA_TABLE_NAME = "hrv_data";
    public static final String JOGGING_TABLE_NAME = "jogging_data";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HRV_Madison.db";

    public final static String COL_ID = "ID";

    public final static String COL_RMSSD = "RMSSD";
    public final static String COL_DATE = "Date";

    public final static String COL_JOG_TIME = "Jog_time";
    public final static String COL_JOG_DISTANCE = "Jog_distance";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.i("TEST", "QUERY: " + SQL_CREATE_HRV_DATA_TABLE_ENTRIES);
        db.execSQL(SQL_CREATE_HRV_DATA_TABLE_ENTRIES);
        db.execSQL(SQL_CREATE_JOGGING_TABLE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

       // db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }



}
