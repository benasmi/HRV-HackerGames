package com.mabe.productions.hrv_madison.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
     *
     *
     * BPM data row is a json array converted to string, that contains bpm values as integers
     *
     * Initial user data is stored in SharedPreference, called user_data
     *
     */

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    //User data
    public static final String SHARED_PREFS_USER_DATA = "user_data";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_KMI = "KMI";
    public static final String FIELD_MAX_DURATION = "KMI";
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


    public final static String HRV_COL_RMSSD = "RMSSD";
    public final static String HRV_COL_LN_RMSSD = "LN_RMSSD";
    public final static String HRV_COL_LOWEST_RMSSD = "lowest_RMSSD";
    public final static String HRV_COL_HIGHEST_RMSSD = "highest_RMSSD";
    public final static String HRV_COL_LOWEST_BPM = "lowest_BPM";
    public final static String HRV_COL_HIGHEST_BPM = "highest_BPM";
    public final static String HRV_COL_AVERAGE_BPM = "average_BPM";
    public final static String HRV_COL_BPM_DATA = "BPM_data";
    public final static String HRV_COL_RMSSD_DATA = "RMSSD_data";
    public final static String HRV_COL_LF_BAND = "LF_BAND";
    public final static String HRV_COL_HF_BAND = "HF_BAND";
    public final static String HRV_COL_VLF_BAND = "VLF_BAND";
    public final static String HRV_COL_VHF_BAND = "VHF_BAND";
    public final static String HRV_COL_DATE = "Date";
    public final static String HRV_COL_MEASUREMENT_DURATION = "measurement_duration";
    public final static String HRV_COL_ID = "ID";
    public static final String HRV_COL_MOOD = "Mood";
    public static final String HRV_COL_HRV = "hrv";

    public static final String HRV_DATA_TABLE_NAME = "hrv_data";

    private static final String SQL_CREATE_HRV_DATA_TABLE_ENTRIES =
            "CREATE TABLE " + FeedReaderDbHelper.HRV_DATA_TABLE_NAME + " (" +
                    FeedReaderDbHelper.HRV_COL_ID + " INTEGER PRIMARY KEY," +
                    FeedReaderDbHelper.HRV_COL_DATE + " STRING," +
                    FeedReaderDbHelper.HRV_COL_LN_RMSSD + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_HRV + " INTEGER," +
                    FeedReaderDbHelper.HRV_COL_LOWEST_RMSSD + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_HIGHEST_RMSSD + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_LOWEST_BPM + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_HIGHEST_BPM + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_AVERAGE_BPM + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_LF_BAND + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_HF_BAND + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_VLF_BAND + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_VHF_BAND + " FLOAT," +
                    FeedReaderDbHelper.HRV_COL_BPM_DATA + " STRING," +
                    FeedReaderDbHelper.HRV_COL_RMSSD_DATA + " STRING," +
                    FeedReaderDbHelper.HRV_COL_MEASUREMENT_DURATION + " INTEGER," +
                    FeedReaderDbHelper.HRV_COL_MOOD + " INTEGER," +
                    FeedReaderDbHelper.HRV_COL_RMSSD + " INTEGER)";



    public static final String WORKOUT_COL_ID = "id";
    public static final String WORKOUT_COL_DATE = "date";
    public static final String WORKOUT_COL_DURATION = "duration";
    public static final String WORKOUT_COL_AVERAGE_PACE = "pace";
    public static final String WORKOUT_COL_AVERAGE_BPM = "average_bpm";
    public static final String WORKOUT_COL_CALORIES = "calories";
    public static final String WORKOUT_COL_PULSE_ZONE = "pulse_zone";
    public static final String WORKOUT_COL_ROUTE = "route";
    public static final String WORKOUT_COL_PACE_DATA = "pace_data";
    public static final String WORKOUT_COL_BPM_DATA = "bpm_data";
    public static final String WORKOUT_COL_DISTANCE = "distance";

    public static final String WORKOUT_DATA_TABLE_NAME = "workout_data";

    private static final String SQL_CREATE_WORKOUT_DATA_TABLE_ENTRIES =
            "CREATE TABLE " + FeedReaderDbHelper.WORKOUT_DATA_TABLE_NAME + " (" +
                    FeedReaderDbHelper.WORKOUT_COL_ID + " INTEGER PRIMARY KEY," +
                    FeedReaderDbHelper.WORKOUT_COL_DATE + " STRING," +
                    FeedReaderDbHelper.WORKOUT_COL_DURATION + " FLOAT," +
                    FeedReaderDbHelper.WORKOUT_COL_AVERAGE_PACE + " FLOAT," +
                    FeedReaderDbHelper.WORKOUT_COL_AVERAGE_BPM + " FLOAT," +
                    FeedReaderDbHelper.WORKOUT_COL_CALORIES + " FLOAT," +
                    FeedReaderDbHelper.WORKOUT_COL_DISTANCE + " FLOAT," +
                    FeedReaderDbHelper.WORKOUT_COL_PULSE_ZONE + " INTEGER," +
                    FeedReaderDbHelper.WORKOUT_COL_BPM_DATA + " STRING," +
                    FeedReaderDbHelper.WORKOUT_COL_PACE_DATA + " STRING," +
                    FeedReaderDbHelper.WORKOUT_COL_ROUTE + " FLOAT)";


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HRV_Madison.db";



    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HRV_DATA_TABLE_ENTRIES);
        db.execSQL(SQL_CREATE_WORKOUT_DATA_TABLE_ENTRIES);
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

    public static int[] getIntArrayFromString(String jsonArray){
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

    public static float[] getFloatArrayFromString(String jsonArray){
        try {
            JSONArray array = new JSONArray(jsonArray);

            float[] bpmValues = new float[array.length()];

            for(int i = 0; i < array.length(); i++){
                bpmValues[i] = (float) array.getDouble(i);
            }

            return bpmValues;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String intArrayToString(int[] bpmValues){

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
    //todo: change names
    public static String floatArrayToString(float[] bpmValues){

        JSONArray array = new JSONArray();

        try{
            for(int i = 0; i < bpmValues.length; i++){
                array.put(i, (double) bpmValues[i]);
            }
            return array.toString();
        }catch(JSONException e){
            e.printStackTrace();
        }

        return null;


    }


    public static LatLng[] getRouteFromString(String data){
        try {
            JSONArray array = new JSONArray(data);
            LatLng[] locationArray = new LatLng[array.length()];

            for(int i = 0; i < array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                double latitude = obj.getDouble("latitude");
                double longtitude = obj.getDouble("longtitude");
                LatLng location = new LatLng(latitude, longtitude);
                locationArray[i] = location;
            }

            return locationArray;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String locationToString(LatLng[] points){

        JSONArray array = new JSONArray();

        for(int i = 0; i < points.length; i++){
            JSONObject locationObj = new JSONObject();
            LatLng currentLocation = points[i];

            try {
                locationObj.put("latitude", currentLocation.latitude);
                locationObj.put("longtitude", currentLocation.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(locationObj);
        }

        return array.toString();
    }






}
