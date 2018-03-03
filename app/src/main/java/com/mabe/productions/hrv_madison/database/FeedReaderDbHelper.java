package com.mabe.productions.hrv_madison.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
     * gitlab.org
     * #############################################################################################################################################################################
     * #                                                                    HRV DATA                                                                                  # JSON array #
     * #############################################################################################################################################################################
     * #  ID  #  RMSSD  #  LN_RMSSD  #  LOWEST_RMSSD  #  HIGHEST_RMSSD  #  LOWEST_BPM  #  HIGHEST_BPM  #  AVERAGE_BPM  #  LF_BAND  #  VLF_BAND  #  VHF_BAND  #  DATE  #  BPM data  #
     * #############################################################################################################################################################################
     *
     * BPM data row is a json array converted to string, that contains bpm values as integers
     *
     * Initial user data is stored in SharedPreference, called user_data
     *
     */

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public final static String COL_RMSSD = "RMSSD";
    public final static String COL_LN_RMSSD = "LN_RMSSD";
    public final static String COL_LOWEST_RMSSD = "lowest_RMSSD";
    public final static String COL_HIGHEST_RMSSD = "highest_RMSSD";
    public final static String COL_LOWEST_BPM = "lowest_BPM";
    public final static String COL_HIGHEST_BPM = "highest_BPM";
    public final static String COL_AVERAGE_BPM = "average_BPM";
    public final static String COL_BPM_DATA = "BPM_data";
    public final static String COL_LF_BAND = "LF_BAND";
    public final static String COL_HF_BAND = "HF_BAND";
    public final static String COL_VLF_BAND = "VLF_BAND";
    public final static String COL_VHF_BAND = "VHF_BAND";
    public final static String COL_DATE = "Date";


    //User data
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
    public static final String FIELD_DONE_INITIAL = "first_time";
    public static final String FIELD_WEEK_DAYS = "week_days";
    public static final String FIELD_LAST_MEASUREMENT_DATE = "week_days";


    //User BT prefs.
    public static final String SHARED_PREFS_DEVICES = "SavedDevice";
    public static final String BT_FIELD_MAC_ADRESS = "MAC_adress";
    public static final String BT_FIELD_DEVICE_NAME = "device_name";

    //User sport prefs
    public static final String SHARED_PREFS_SPORT = "Sport";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_PULSE_ZONE = "pulse_zone";






    private static final String SQL_CREATE_HRV_DATA_TABLE_ENTRIES =
            "CREATE TABLE " + FeedReaderDbHelper.HRV_DATA_TABLE_NAME + " (" +
                    FeedReaderDbHelper.COL_ID + " INTEGER PRIMARY KEY," +
                    FeedReaderDbHelper.COL_DATE + " STRING," +
                    FeedReaderDbHelper.COL_LN_RMSSD + " FLOAT," +
                    FeedReaderDbHelper.COL_LOWEST_RMSSD + " FLOAT," +
                    FeedReaderDbHelper.COL_HIGHEST_RMSSD + " FLOAT," +
                    FeedReaderDbHelper.COL_LOWEST_BPM + " FLOAT," +
                    FeedReaderDbHelper.COL_HIGHEST_BPM + " FLOAT," +
                    FeedReaderDbHelper.COL_AVERAGE_BPM + " FLOAT," +
                    FeedReaderDbHelper.COL_LF_BAND + " FLOAT," +
                    FeedReaderDbHelper.COL_HF_BAND + " FLOAT," +
                    FeedReaderDbHelper.COL_VLF_BAND + " FLOAT," +
                    FeedReaderDbHelper.COL_VHF_BAND + " FLOAT," +
                    FeedReaderDbHelper.COL_BPM_DATA + " STRING," +
                    FeedReaderDbHelper.COL_RMSSD + " INTEGER)";





    public static final String HRV_DATA_TABLE_NAME = "hrv_data";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HRV_Madison.db";

    public final static String COL_ID = "ID";


    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HRV_DATA_TABLE_ENTRIES);
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

    public static int[] getBpmValuesFromString(String jsonArray){
        try {
            JSONArray array = new JSONArray(jsonArray);

            int[] bpmValues = new int[array.length()];

            for(int i = 0; i < array.length(); i++){
                bpmValues[i] = array.getInt(i);
            }

            return bpmValues;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringFromBpmValues(int[] bpmValues){

        JSONArray array = new JSONArray();

        try{
            for(int i = 0; i < bpmValues.length; i++){
                array.put(i, bpmValues[i]);
            }
            return array.toString();
        }catch(JSONException e){
            e.printStackTrace();
        }

        return null;


    }





}
