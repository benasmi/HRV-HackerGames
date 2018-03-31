package com.mabe.productions.hrv_madison;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.measurements.Measurement;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;


import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;

public class User {

    public final static int GENDER_MALE = 0;
    public final static int GENDER_FEMALE = 1;

    public final static int SPORT_JOGGING = 0;

    private static final float POOR_ACTIVITY_INDEX = 30;
    private static final float LOW_INITIAL_WORKOUT_DURATION = 15;
    private static final float HIGH_INITIAL_WORKOUT_DURATION = 20;

    private int program_update_state;

    public static final int PROGRAM_STATE_CHANGED = 0;
    public static final int PROGRAM_STATE_DAY_OFF = 1;
    public static final int PROGRAM_STATE_INVALID = 2;
    public static final int PROGRAM_STATE_NOT_ENOUGH_DATA = 3;


    public static final int MOOD_NEUTRAL = 0;
    public static final int MOOD_NEGATIVELY_EXCITED = 1;
    public static final int MOOD_NEGATIVELY_MELLOW = 2;
    public static final int MOOD_POSITIVELY_MELLOW = 4;
    public static final int MOOD_POSITIVELY_EXCITED = 5;

    public static final float SIGNIFICANT_HRV_INCREASE = 1.3f;
    public static final float MEDIOCRE_HRV_INCREASE = 1.15f;
    public static final float MINIMAL_HRV_INCREASE = 1.05f;
    public static final float MINIMAL_HRV_DECREASE = 0.95f;
    public static final float MEDIOCRE_HRV_DECREASE = 0.85f;
    public static final float SIGNIFICANT_HRV_DECREASE = 0.7f;


    private float KMI;
    private float current_hrv;
    private float yesterday_hrv;
    private float last_week_hrv;
    private Date birthday;
    private int gender;
    private float height;
    private float activity_index;
    private int selected_sport;
    private float weight;
    private String verbal_reccomendation;

    private int pulse_zone;
    private float workout_duration; //in minutes
    public float second_last_week_hrv;


    private String user_id;
    private String user_password;

    private boolean[] week_days = new boolean[7];
    private float max_duration; //In minutes

    private ArrayList<Measurement> measurements;
    private ArrayList<WorkoutMeasurements> workouts;


    /*
     * Saves a new measurement to the database.
     * @param overrideByDate If true, existing table row with today's date is overriden.
     */

    public static void addMeasurementData(Context context, Measurement measurement, boolean overrideByDate) {
        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        //Checking if a measurement with today's date exists
        String todayDate = Utils.getStringFromDate(Calendar.getInstance().getTime());

        String[] projection = {
                FeedReaderDbHelper.HRV_COL_DATE,
                FeedReaderDbHelper.HRV_COL_ID,

        };

        String sortOrder =
                FeedReaderDbHelper.HRV_COL_ID + " DESC";

        Cursor cursor = db.query(
                FeedReaderDbHelper.HRV_DATA_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        //Executing SELECT query if overrideByDate is true
        if (cursor.moveToNext() && overrideByDate) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_ID));
            String dateString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_DATE));

            //Removing existing row if measurement was made today
            if (dateString.equals(todayDate)) {
                db.delete(FeedReaderDbHelper.HRV_DATA_TABLE_NAME, FeedReaderDbHelper.HRV_COL_ID + "=" + id, null);
            }

        }

        //Inserting values
        ContentValues values = measurement.getContentValues();

        db.insertOrThrow(FeedReaderDbHelper.HRV_DATA_TABLE_NAME, null, values);

        db.close();

    }

    /*
     * Saves a new workout to the database.
     * @param overrideByDate If true, existing table row with today's date is overriden.
     */

    public static void addWorkoutData(Context context, WorkoutMeasurements workout, boolean overrideByDate) {
        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        //Checking if a measurement with today's date exists
        String todayDate = Utils.getStringFromDate(Calendar.getInstance().getTime());

        String[] projection = {
                FeedReaderDbHelper.WORKOUT_COL_DATE,
                FeedReaderDbHelper.WORKOUT_COL_ID,

        };

        String sortOrder =
                FeedReaderDbHelper.WORKOUT_COL_ID + " DESC";

        Cursor cursor = db.query(
                FeedReaderDbHelper.WORKOUT_DATA_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        //Executing SELECT query if overrideByDate is true
        if (cursor.moveToNext() && overrideByDate) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_ID));
            String dateString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_DATE));

            //Removing existing row if measurement was made today
            if (dateString.equals(todayDate)) {
                db.delete(FeedReaderDbHelper.WORKOUT_DATA_TABLE_NAME, FeedReaderDbHelper.WORKOUT_COL_ID + "=" + id, null);
            }

        }

        //Inserting values
        ContentValues values = workout.getContentValues();

        db.insertOrThrow(FeedReaderDbHelper.WORKOUT_DATA_TABLE_NAME, null, values);

        db.close();

    }

    /*
     * Returns last saved measurement
     */
    public Measurement getLastMeasurement() {
        if (measurements.size() > 0) {
            return measurements.get(0);
        } else {
            return null;
        }
    }

    public Measurement getTodaysMeasurement(){

        Calendar todaysDate = Calendar.getInstance(TimeZone.getDefault());
        int today = todaysDate.get(Calendar.DAY_OF_YEAR);
        for(int i = 0; i <measurements.size(); i++){
        todaysDate.setTime(measurements.get(i).getDate());
            int measurementDate = todaysDate.get(Calendar.DAY_OF_YEAR);
            if(today==measurementDate){
                Log.i("MEASUREMENTS", "MEASUREAMENT FOUND");
                return measurements.get(i);
            }
        }
        Log.i("MEASUREMENTS", "MEASUREMENT NOT FOUND");
        return null;

    }

    private void getAllMeasurementsFromDb(Context context) {

        ArrayList<Measurement> measurementList = new ArrayList<>();

        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        String[] projection = {
                FeedReaderDbHelper.HRV_COL_DATE,
                FeedReaderDbHelper.HRV_COL_ID,
                FeedReaderDbHelper.HRV_COL_RMSSD,
                FeedReaderDbHelper.HRV_COL_LN_RMSSD,
                FeedReaderDbHelper.HRV_COL_LOWEST_RMSSD,
                FeedReaderDbHelper.HRV_COL_HIGHEST_RMSSD,
                FeedReaderDbHelper.HRV_COL_LOWEST_BPM,
                FeedReaderDbHelper.HRV_COL_HIGHEST_BPM,
                FeedReaderDbHelper.HRV_COL_AVERAGE_BPM,
                FeedReaderDbHelper.HRV_COL_LF_BAND,
                FeedReaderDbHelper.HRV_COL_VLF_BAND,
                FeedReaderDbHelper.HRV_COL_VHF_BAND,
                FeedReaderDbHelper.HRV_COL_HF_BAND,
                FeedReaderDbHelper.HRV_COL_BPM_DATA,
                FeedReaderDbHelper.HRV_COL_RMSSD_DATA,
                FeedReaderDbHelper.HRV_COL_MEASUREMENT_DURATION,
                FeedReaderDbHelper.HRV_COL_MOOD

        };

        String sortOrder =
                FeedReaderDbHelper.HRV_COL_ID + " DESC";

        Cursor cursor = db.query(
                FeedReaderDbHelper.HRV_DATA_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_ID));
            Date date = Utils.getDateFromString(cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_DATE)));
            int rmssd = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_RMSSD));
            float ln_rmssd = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_LN_RMSSD));
            float lowest_rmssd = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_LOWEST_RMSSD));
            float highest_rmssd = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_HIGHEST_RMSSD));
            float lowest_bpm = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_LOWEST_BPM));
            float highest_bpm = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_HIGHEST_BPM));
            float average_bpm = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_AVERAGE_BPM));
            float LF_band = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_LF_BAND));
            float VLF_band = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_VLF_BAND));
            float VHF_band = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_VHF_BAND));
            float HF_band = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_HF_BAND));
            String bpmDataString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_BPM_DATA));
            int[] bpmData = FeedReaderDbHelper.getIntArrayFromString(bpmDataString);

            String rmssdDataString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_RMSSD_DATA));
            int[] rmssdData = FeedReaderDbHelper.getIntArrayFromString(rmssdDataString);

            int measurement_duration = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_MEASUREMENT_DURATION));
            //TODO: jei tau sita vieta crashina, perinstaliuok appsa
            int mood = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_MOOD));

            Measurement measurement = new Measurement(date,
                    rmssd,
                    ln_rmssd,
                    lowest_rmssd,
                    highest_rmssd,
                    lowest_bpm,
                    highest_bpm,
                    average_bpm,
                    LF_band,
                    VLF_band,
                    VHF_band,
                    HF_band,
                    bpmData,
                    rmssdData,
                    measurement_duration,
                    id,
                    mood);

            measurementList.add(measurement);
            Log.i("TEST", "rmssd: " + rmssd);

        }

        db.close();


        measurements = measurementList;


    }
    private void getAllWorkoutsFromDb(Context context) {

        ArrayList<WorkoutMeasurements> workoutList = new ArrayList<>();

        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        String[] projection = {"*"};

        String sortOrder =
                FeedReaderDbHelper.WORKOUT_COL_ID + " DESC";

        Cursor cursor = db.query(
                FeedReaderDbHelper.WORKOUT_DATA_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_ID));
            Date date = Utils.getDateFromString(cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_DATE)));
            float duration = cursor.getFloat(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_DURATION));
            float average_bpm = cursor.getFloat(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_AVERAGE_BPM));
            float average_pace = cursor.getFloat(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_AVERAGE_PACE));
            int pulse_zone = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_PULSE_ZONE));
            float calories = cursor.getFloat(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_CALORIES));
            int[] bpm_data = FeedReaderDbHelper.getIntArrayFromString(cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_BPM_DATA)));
            float[] pace_data = FeedReaderDbHelper.getFloatArrayFromString(cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_PACE_DATA)));
            LatLng[] route = FeedReaderDbHelper.getRouteFromString(cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_ROUTE)));
            float distance = cursor.getFloat(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.WORKOUT_COL_DISTANCE));
            WorkoutMeasurements workout = new WorkoutMeasurements(
                    id,
                    date,
                    duration,
                    average_pace,
                    average_bpm,
                    bpm_data,
                    pace_data,
                    route,
                    calories,
                    pulse_zone,
                    distance
            );



            workoutList.add(workout);
        }

        db.close();

        workouts = workoutList;

    }

    public WorkoutMeasurements getLastWorkout(){
        if (workouts.size() > 0) {
            return workouts.get(0);
        } else {
            return null;
        }
    }

    public WorkoutMeasurements getTodaysWorkout(){
        Calendar todaysDate = Calendar.getInstance();
        int today = todaysDate.get(Calendar.DAY_OF_YEAR);
        for(int i = 0; i <workouts.size(); i++){
            todaysDate.setTime(workouts.get(i).getDate());
            int measurementDate = todaysDate.get(Calendar.DAY_OF_YEAR);
            if(today==measurementDate){
                Log.i("MEASUREMENTS", "FOUND WORKOUT");
                return workouts.get(i);
            }
        }
        Log.i("MEASUREMENTS", "WORKOUT NOT FOUND");
        return null;
    }

    public static User getUser(Context context) {
        User user = new User();

        //Getting initial user data from SharedPreferences
        user.setKMI(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_KMI, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setBirthday(Utils.getDateFromString(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_BIRTHDAY, FeedReaderDbHelper.SHARED_PREFS_USER_DATA)));
        user.setGender(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_GENDER, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setHeight(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_HEIGHT, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setActivityIndex(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_ACTIVITY_INDEX, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setSelectedSport(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_SELECTED_SPORT, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setWeight(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_WEIGHT, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setUserId(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_USER_ID, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setUserPassword(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_PASSWORD, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setMaxDuration(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_BASE_DURATION, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setWorkoutDuration(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_DURATION, FeedReaderDbHelper.SHARED_PREFS_SPORT));
        user.setPulseZone(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_PULSE_ZONE, FeedReaderDbHelper.SHARED_PREFS_SPORT));
        user.setWeekDays(Utils.readFromSharedPrefs_boolarray(context, FeedReaderDbHelper.FIELD_WEEK_DAYS, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));


        FeedReaderDbHelper databaseHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderDbHelper.HRV_COL_ID,
                FeedReaderDbHelper.HRV_COL_RMSSD,
                FeedReaderDbHelper.HRV_COL_DATE

        };

        String sortOrder =
                FeedReaderDbHelper.HRV_COL_ID + " DESC";

        Cursor cursor = database.query(
                FeedReaderDbHelper.HRV_DATA_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        Calendar calendar = Calendar.getInstance();

        int rmssdSumLastWeek = 0;
        int rmssdCountLastWeek = 0;

        int rmssdSumSecondLastWeek = 0;
        int rmssdCountSecondLastWeek = 0;

        //Getting user hrv data from SQLite
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_ID));
            int rmssd = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_RMSSD));
            String dateString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_DATE));


            Date date = Utils.getDateFromString(dateString);

            //Summing up rmssd if date if from last week
            int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            calendar.setTime(date);

            if (currentWeek - 1 == (calendar.get(Calendar.WEEK_OF_YEAR))) {
                rmssdCountLastWeek++;
                rmssdSumLastWeek += rmssd;
            }

            if (currentWeek - 2 == (calendar.get(Calendar.WEEK_OF_YEAR))) {
                rmssdCountSecondLastWeek++;
                rmssdSumSecondLastWeek += rmssd;
            }

        }

        database.close();


        if (rmssdCountLastWeek != 0) {
            //Calculating average last week's hrv
            user.last_week_hrv = ((float) rmssdSumLastWeek) / ((float) rmssdCountLastWeek);

            if (rmssdCountSecondLastWeek != 0) {
                user.second_last_week_hrv = ((float) rmssdSumSecondLastWeek) / ((float) rmssdCountSecondLastWeek);
            }
        }


        //Dummy data
        //user.setCurrentHrv(40);
        //user.setLastWeekHrv(30);
        //user.setSecondLastWeekHrv(20);
        user.setSelectedSport(SPORT_JOGGING);
        user.setMaxDuration(20);

        user.getAllMeasurementsFromDb(context);
        user.getAllWorkoutsFromDb(context);
        ArrayList<Measurement> measurements = user.getAllMeasurements();

        calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        for (int i = 0; i < measurements.size(); i++) {
            calendar.setTime(measurements.get(i).getDate());
            int measurementDay = calendar.get(Calendar.DAY_OF_YEAR);

            if (measurementDay == today) {
                //Today's measurement
                user.setCurrentHrv(measurements.get(i).getRmssd());
            } else if (measurementDay == today - 1) {
                //yesterday's measurement
                user.setYesterdayHrv(measurements.get(i).getRmssd());
            }
        }

        user.loadProgram(context);
        user.generateDailyReccomendation(context);

        return user;
    }

    private void loadProgram(Context context){
        workout_duration = Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_DURATION, FeedReaderDbHelper.SHARED_PREFS_SPORT);
        pulse_zone = Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_PULSE_ZONE, FeedReaderDbHelper.SHARED_PREFS_SPORT);
    }


    private void saveProgram(Context context) {
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_DURATION, workout_duration, FeedReaderDbHelper.SHARED_PREFS_SPORT);
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_PULSE_ZONE, pulse_zone, FeedReaderDbHelper.SHARED_PREFS_SPORT);
    }

    public ArrayList<Measurement> getAllMeasurements() {
        return measurements;
    }

    public String generateWeeklyProgram(Context context) {

        Log.i("TEST", "last week hrv: " + last_week_hrv);

        //First time
        if (last_week_hrv == 0) {

            //Weak duration
            if (activity_index < 30) {
                workout_duration = 15;
                pulse_zone = 2; //todo: think of a reasonable pulse zone
            } else {
                //Fit duration
                workout_duration = 20;
                pulse_zone = 1; //todo: think of a reasonable pulse zone
            }
            saveProgram(context);
            return "Jums sugeneruota pirmoji programa";
        }

        //Checking if there is at least two weeks worth of data
        if (second_last_week_hrv == 0) {
            return "Nepakanka duomenų programos tobulinimui. Programa nekeičiama";
        }

        //Not upgrading duo to poor hrv
        if (0.5 * last_week_hrv < current_hrv && current_hrv < 0.85 * last_week_hrv) {
            return "Programa nepasikeitė dėl prasto HRV";
        }


        //Upgrading program due to very good hrv score
        if (current_hrv >= last_week_hrv * 0.85) {


            //if user has reached his max available workout duration, increasing pulse_zone(intensity) and decreasing workout duration(time).
            if (workout_duration == max_duration) {

                //Decreasing to minimum workout duration based on activity index
                if (activity_index < POOR_ACTIVITY_INDEX) {
                    //User is not active enough, hence initial workout duration is lower
                    workout_duration = LOW_INITIAL_WORKOUT_DURATION;
                } else {
                    //User is active, hence initial workout duration is higher
                    workout_duration = HIGH_INITIAL_WORKOUT_DURATION;
                }
                pulse_zone++;

                //There are only 5 pulse zones
                if (pulse_zone > 5) {
                    pulse_zone = 5;
                }
                saveProgram(context);

                return "Jūs pasekėte savo max trukmę, todėl padidinsime jūsų intensyvumą";
            }

            //Increasing workout duration and checking if it does not exceed maximum available duration

            workout_duration *= 1.1;
            if (workout_duration <= max_duration) {
                saveProgram(context);
                return "Juma puikiai sekasi. Jūsų sportavimo trukmė pakilo 10%";
            } else {
                workout_duration = max_duration;
                saveProgram(context);
                return "Pasiekėtė max trukmę, todėl ši savaitė bus užtvirtinamoji.";
            }


        }

        return "not supported";
    }

    public void generateDailyReccomendation(Context context) {
        Calendar c = Calendar.getInstance();

        //Returns the current week day, where 1 is Monday
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (week_days[dayOfWeek - 1]) {


            int baselineHrv = -1;
            int minBaselineHrv = -1;
            int maxBaselineHrv = -1;
            int hrvBias = -1;

            Resources resources = context.getResources();

            int age = Utils.getAgeFromDate(birthday);

            if (age >= 18 && age <= 24) {
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_18_24_M) : resources.getInteger(R.integer.baseline_18_24_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_18_24_M) : resources.getInteger(R.integer.baseline_bias_18_24_F);
            } else if (age >= 25 && age <= 34) {
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_25_34_M) : resources.getInteger(R.integer.baseline_25_34_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_25_34_M) : resources.getInteger(R.integer.baseline_bias_25_34_F);
            } else if (age >= 35 && age <= 44) {
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_35_44_M) : resources.getInteger(R.integer.baseline_35_44_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_35_44_M) : resources.getInteger(R.integer.baseline_bias_35_44_F);
            } else if (age >= 45 && age <= 54) {
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_45_54_M) : resources.getInteger(R.integer.baseline_45_54_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_45_54_M) : resources.getInteger(R.integer.baseline_bias_45_54_F);
            } else if (age >= 55 && age <= 64) {
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_55_64_M) : resources.getInteger(R.integer.baseline_55_64_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_55_64_M) : resources.getInteger(R.integer.baseline_bias_55_64_F);
            } else if (age >= 65 && age <= 74) {
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_65_74_M) : resources.getInteger(R.integer.baseline_65_74_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_65_74_M) : resources.getInteger(R.integer.baseline_bias_65_74_F);
            } else if (age >= 75) {
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_75_M) : resources.getInteger(R.integer.baseline_75_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_75_M) : resources.getInteger(R.integer.baseline_bias_75_F);
            }

            minBaselineHrv = baselineHrv - hrvBias;
            maxBaselineHrv = baselineHrv + hrvBias;

            //If no yesterday data is present
            //TODO: do some deciding on this one
            if (yesterday_hrv == 0) {
//                yesterday_hrv = baselineHrv;
                yesterday_hrv = current_hrv;
                program_update_state = PROGRAM_STATE_NOT_ENOUGH_DATA; //we may want to change this to STATE_FIRST_TIME (which doesn't exist yet)
                verbal_reccomendation = "Not enough data to generate reasonable workout plan";
                pulse_zone = 1;
                workout_duration = max_duration*0.5f;
                return;
            }


            if (minBaselineHrv <= current_hrv /*&& current_hrv <= maxBaselineHrv*/) {

                float percentageChange = current_hrv / yesterday_hrv;

                workout_duration*=percentageChange;
                //Hrv has increased
                if (percentageChange >= 1) {
                    program_update_state = PROGRAM_STATE_CHANGED;


                    if (percentageChange <= MINIMAL_HRV_INCREASE) {
                        program_update_state = PROGRAM_STATE_CHANGED;
                        Log.i("TEST", "detected minimal hrv increase");
                        verbal_reccomendation =  "No significant change in your HRV, so your training plan is not altered";
                        return;
                    }
                    if (percentageChange <= MEDIOCRE_HRV_INCREASE) {

                        Log.i("TEST", "mediocre hrv increase");
                        verbal_reccomendation =  "Your HRV has increased,in comparison to last time: Upgrading your workout!";
                        return;
                    }

                    //hrv has greatly increased
                    Log.i("TEST", "hrv has greatly increased");
                    verbal_reccomendation =  "Incredible! Seems like you've been feeling lately: Increasing training difficulty!";
                    return;




                } else {

                    //Hrv has decreased
                    program_update_state = PROGRAM_STATE_CHANGED;

                    if (percentageChange >= MINIMAL_HRV_DECREASE) {
                        program_update_state = PROGRAM_STATE_CHANGED;
                        Log.i("TEST", "detected minimal hrv decrease");
                        verbal_reccomendation =  "No significant change in your HRV, so your training plan is not altered";
                        return;
                    }

                    if (percentageChange >= MEDIOCRE_HRV_DECREASE) {
                        Log.i("TEST", "detected mediocre hrv decrease");
                        verbal_reccomendation =  "Your HRV has decreased,in comparison to last time: Downgrading your workout!";
                        return;
                    }


                    Log.i("TEST", "detected great hrv decrease");
                    verbal_reccomendation =  "Your HRV has drastically decreased. You should take a day off.";
                    return;
                }


            } else {
                program_update_state = PROGRAM_STATE_INVALID;
                Log.i("TEST", "Jusu hrv neatitinka normu");
                verbal_reccomendation =  "Jūsų hrv neatitinka normų";
                return;
            }


        } else {
            program_update_state = PROGRAM_STATE_DAY_OFF;
            Log.i("TEST", "Šiandien jums poilsio diena");
            verbal_reccomendation =  "Šiandien jums poilsio diena";
            return;
        }


    }

    //Kompromisas ;)
    public static final int UPDATE_TYPE_BY_DATE = 0;
    public static final int UPDATE_TYPE_BY_ID = 1;

    public static void updateMeasurement(Context context, Measurement measurement, final int updateType) {

        SQLiteDatabase database = new FeedReaderDbHelper(context).getWritableDatabase();

        //Inserting values
        ContentValues values = measurement.getContentValues();

        switch (updateType) {
            case UPDATE_TYPE_BY_DATE:
                database.update(FeedReaderDbHelper.HRV_DATA_TABLE_NAME, values, FeedReaderDbHelper.HRV_COL_DATE + " = " + Utils.getStringFromDate(measurement.getDate()), null);
                break;

            case UPDATE_TYPE_BY_ID:
                database.update(FeedReaderDbHelper.HRV_DATA_TABLE_NAME, values, FeedReaderDbHelper.HRV_COL_ID + " = " + measurement.getUniqueId(), null);
                break;
        }

        database.close();


    }


    public float getKMI() {
        return KMI;
    }

    public void setKMI(float KMI) {
        this.KMI = KMI;
    }

    public float getCurrentHrv() {
        return current_hrv;
    }

    public void setCurrentHrv(float current_hrv) {
        this.current_hrv = current_hrv;
    }


    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getActivityIndex() {
        return activity_index;
    }

    public void setActivityIndex(float activity_index) {
        this.activity_index = activity_index;
    }

    public int getSelectedSport() {
        return selected_sport;
    }

    public void setSelectedSport(int selected_sport) {
        this.selected_sport = selected_sport;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getUserPassword() {
        return user_password;
    }

    public void setUserPassword(String user_password) {
        this.user_password = user_password;
    }

    public boolean[] getWeekDays() {
        return week_days;
    }

    public void setWeekDays(boolean[] week_days) {
        this.week_days = week_days;
    }

    public float getMaxDuration() {
        return max_duration;
    }

    public void setMaxDuration(float base_duration) {
        this.max_duration = base_duration;
    }


    public float getYesterdayHrv() {
        return yesterday_hrv;
    }

    public void setYesterdayHrv(float yesterday_hrv) {
        this.yesterday_hrv = yesterday_hrv;
    }

    public float getLastWeekHrv() {
        return last_week_hrv;
    }

    public void setLastWeekHrv(float last_week_hrv) {
        this.last_week_hrv = last_week_hrv;
    }

    public int getPulseZone() {
        return pulse_zone;
    }

    public void setPulseZone(int pulse_zone) {
        this.pulse_zone = pulse_zone;
    }

    public double getWorkoutDuration() {
        return workout_duration;
    }

    public void setWorkoutDuration(float workout_duration) {
        this.workout_duration = workout_duration;
    }

    public float getSecondLastWeekHrv() {
        return second_last_week_hrv;
    }

    public void setSecondLastWeekHrv(float second_last_week_hrv) {
        this.second_last_week_hrv = second_last_week_hrv;
    }

    public int getProgramUpdateState() {
        return program_update_state;
    }

    public float getHrvChangePercentage() {
        return  current_hrv / yesterday_hrv;
    }

    public String getVerbalReccomendation() {
        return verbal_reccomendation;
    }
}
