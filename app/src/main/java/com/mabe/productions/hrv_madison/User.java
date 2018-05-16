package com.mabe.productions.hrv_madison;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private static final float LOW_INITIAL_WORKOUT_DURATION = 20;
    private static final float HIGH_INITIAL_WORKOUT_DURATION = 25;

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

    public static final int UPDATE_TYPE_BY_DATE = 0;
    public static final int UPDATE_TYPE_BY_ID = 1;

    private float KMI;
    private float current_hrv;
    private float yesterday_hrv = 0f;
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
    private Date firstWeeklyDate;
    private Date lastWeeklyDate;

    /*
     * The intervals of workout's walk/run ratio.
     * Every second element of the array represents running duration.
     * {walking_duration, running_duration, walking_duration, running_duration...}
     * Specified in seconds
     */
    private long[] walkRunIntervals = {};
    private int[] pulseZoneIntervals = {3};

    public static final long[][] WEEKLY_INTERVAL_PROGRAM = {
            {0L}, /* Walking (No intervals) */
            {0L},
            {0L},
            {120L, 60L},
            {120L, 60L},
            {120L, 60L},
            {60L, 60L},
            {}, /* Jogging (No intervals) */
    };
    //Kolkas sukuriu tik array, nes daug reiktu keisti.
    //Paskui galesim padaryti su klase
    public static final int[][] WEEKLY_PULSE_ZONE_PROGRAM = {
            {2},
            {2},
            {2},
            {2, 3},
            {2, 3},
            {2, 3},
            {2, 3},
            {2},
    };


    private Date last_generated_weekly_date;
    public float second_last_week_hrv;


    private String user_id;
    private String user_password;

    private boolean[] week_days = new boolean[7];
    private float max_duration; //In minutes

    private ArrayList<Measurement> measurements;
    private ArrayList<WorkoutMeasurements> workouts;


    /**
     * Saves a new measurement to the database.
     * @param overrideByDate If true, existing table row with today's date is overridden.
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

    /**
     * Saves a new workout to the database.
     * @param overrideByDate If true, existing table row with today's date is overridden.
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

    /**
     * @return Last saved measurement
     */
    public Measurement getLastMeasurement() {
        if (measurements.size() > 0) {
            return measurements.get(0);
        } else {
            return null;
        }
    }


    /**
     * If no measurements were made today, returns null
     * @return Today's measurement.
     */
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


    /**
     * Fetches all measurements from the database and populates measurements list
     */
    private void getAllMeasurementsFromDb(Context context) {

        ArrayList<Measurement> measurementList = new ArrayList<>();

        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        String[] projection = {"*"};

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
            int hrv = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.HRV_COL_HRV));
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
                    mood,
                    hrv);

            measurementList.add(measurement);

        }

        db.close();


        measurements = measurementList;


    }

    /**
    * Fetches all workouts from the database and populates workouts list
    */
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

    /**
    * @return Last saved workout
     */
    public WorkoutMeasurements getLastWorkout(){
        if (workouts.size() > 0) {
            return workouts.get(0);
        } else {
            return null;
        }
    }

    /**
    * If no workouts were completed today, returns null
    * @return Today's workout.
    */
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

    /**
     * Sets last_week_hrv, second_last_week_hrv, current_hrv, yesterday_hrv values from measurement arrayList
     */
    private void setHrvMeasurementsByDate(){

        int hrvSumLastWeek = 0;
        int hrvCountLastWeek = 0;

        int hrvSumSecondLastWeek = 0;
        int hrvCountSecondLastWeek = 0;

        for(Measurement measurement : measurements){

            Date today = Calendar.getInstance().getTime();

            //Summing up rmssd if date if from last week
            if (Utils.calendoricWeekDifference(today, measurement.getDate()) == 1) {
                hrvCountLastWeek++;
                hrvSumLastWeek += measurement.getHrv();
            }

            if (Utils.calendoricWeekDifference(today, measurement.getDate()) == 2) {
                hrvCountSecondLastWeek++;
                hrvSumSecondLastWeek += measurement.getHrv();
            }


            if (Utils.calendoricDayDifference(today, measurement.getDate()) == 0) {
                //Today's measurement
                setCurrentHrv(measurement.getHrv());
            } else if (Utils.calendoricDayDifference(today, measurement.getDate()) == 1) {
                //yesterday's measurement
                setYesterdayHrv(measurement.getHrv());
            }

        }

        //Calculating and setting last_week and second_last_week hrv values
        if (hrvCountLastWeek != 0) {
            setLastWeekHrv(((float) hrvSumLastWeek) / ((float) hrvCountLastWeek));
            if (hrvCountSecondLastWeek != 0) {
                setSecondLastWeekHrv(((float) hrvSumSecondLastWeek) / ((float) hrvCountSecondLastWeek));
            }
        }
    }


    /**
     * Fetches data from the database and returns an instance of a user
     * @return User instance
     */
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
        user.setLastGeneratedWeeklyDate(Utils.getDateFromString(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_LAST_TIME_GENERATED_WEEKLY, FeedReaderDbHelper.SHARED_PREFS_SPORT)));
        user.setWalkRunIntervals(Utils.readFromSharedPrefs_longarray(context, FeedReaderDbHelper.FIELD_WORKOUT_INTERVALS, FeedReaderDbHelper.SHARED_PREFS_SPORT));
        user.setPulseZoneIntervals(Utils.readFromSharedPrefs_intarray(context, FeedReaderDbHelper.FIELD_PULSE_ZONE_INTERVALS, FeedReaderDbHelper.SHARED_PREFS_SPORT));

        //Dummy data
        user.setSelectedSport(SPORT_JOGGING);
        user.getAllMeasurementsFromDb(context);
        user.getAllWorkoutsFromDb(context);
        user.setHrvMeasurementsByDate();


        if(user.checkWeeklyProgramDate(context)){
            user.generateWeeklyProgram(context);
            User.saveProgram(context, user.getWorkoutDuration(), user.getPulseZone(), user.getWalkRunIntervals(), user.getPulseZoneIntervals());
        }

        user.generateDailyReccomendation(context);





        return user;
    }



    /**
     * Saves workout duration and pulse_zone to the database
     * @param walkRunIntervals Walk/Run intervals of the workout. Can be null.
     */
    public static void saveProgram(Context context, float workout_duration, int pulse_zone, long[] walkRunIntervals, int[] pulseZoneIntervals) {
        Log.i("TEST", "saving program...");
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_WORKOUT_INTERVALS, walkRunIntervals == null ? new long[0] : walkRunIntervals, FeedReaderDbHelper.SHARED_PREFS_SPORT);
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_PULSE_ZONE_INTERVALS, pulseZoneIntervals == null ? new int[0] : pulseZoneIntervals, FeedReaderDbHelper.SHARED_PREFS_SPORT);
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_DURATION, workout_duration, FeedReaderDbHelper.SHARED_PREFS_SPORT);
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_PULSE_ZONE, pulse_zone, FeedReaderDbHelper.SHARED_PREFS_SPORT);
    }



    /**
     * Gets all measurements that the user has made.
     * @return A list of measurements
     */
    public ArrayList<Measurement> getAllMeasurements() {
        return measurements;
    }


    /**
     * Determines whether a weekly program should be generated or not and sets {@link #firstWeeklyDate} and {@link #lastWeeklyDate}.
     *
     * True will be returned and {@link #lastWeeklyDate} will be set to today if either of these conditions are met:
     *    There is no saved lastWeeklyDate.
     *    lastWeeklyDate is at least one week behind current date.
     *
     * The variable {@link #firstWeeklyDate} will be set to today if any of the following conditions are met:
     *     There is no saved firstWeeklyDate
     *     firstWeeklyDate is at least two weeks behind
     *
     * @return Returns true, if weekly program should be generated.
     */
    private boolean checkWeeklyProgramDate(Context context) {

        //todo: calculate calendoric difference in weeks
        Calendar calendar = Calendar.getInstance();
        firstWeeklyDate = Utils.getDateFromString(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_FIRST_TIME_GENERATED_WEEKLY, FeedReaderDbHelper.SHARED_PREFS_SPORT));
        lastWeeklyDate = Utils.getDateFromString(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_LAST_TIME_GENERATED_WEEKLY, FeedReaderDbHelper.SHARED_PREFS_SPORT));
        boolean generateWeekly = false;

        if(lastWeeklyDate == null){
            generateWeekly = true;
            lastWeeklyDate = calendar.getTime();
        }

        if(firstWeeklyDate == null){
            firstWeeklyDate = calendar.getTime();
        }
        int weekDiff = Utils.calendoricWeekDifference(lastWeeklyDate, calendar.getTime());
        if(weekDiff > 2){
            firstWeeklyDate = calendar.getTime();
        }
        int dif = Utils.calendoricWeekDifference(lastWeeklyDate, calendar.getTime());
        if((int)(Utils.calendoricWeekDifference(lastWeeklyDate, calendar.getTime())) > 0){
            generateWeekly = true;
        }

        if(generateWeekly){
            lastWeeklyDate = calendar.getTime();
        }

        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_LAST_TIME_GENERATED_WEEKLY, Utils.getStringFromDate(lastWeeklyDate), FeedReaderDbHelper.SHARED_PREFS_SPORT);
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_FIRST_TIME_GENERATED_WEEKLY, Utils.getStringFromDate(firstWeeklyDate), FeedReaderDbHelper.SHARED_PREFS_SPORT);


        return generateWeekly;


    }


    /**
     * Sets pulse_zone and workout_duration based on weekly program algorithm.
     * The program is saved to the database.
     * @return Weekly program verbal information
     */

    public String generateWeeklyProgram(Context context) {

        Log.i("WORKOUT", "last week hrv: " + last_week_hrv + " | " + "second last week : " + second_last_week_hrv + " | " + "pulse_zone: " + pulse_zone + " | " + "workout_duration: " + workout_duration);

        //Setting walk/run intervals based on weekly program progress
        int programWeekProgress = Utils.calendoricWeekDifference(firstWeeklyDate, Calendar.getInstance().getTime());
        Log.i("TEST", "week diff: " + programWeekProgress);
        if(programWeekProgress < WEEKLY_INTERVAL_PROGRAM.length){

            setWalkRunIntervals(WEEKLY_INTERVAL_PROGRAM[programWeekProgress]);
            setPulseZoneIntervals(WEEKLY_PULSE_ZONE_PROGRAM[programWeekProgress]);
        }else{
            setWalkRunIntervals(new long[1]);
            setPulseZoneIntervals(new int[]{2});
        }

        //First time
        if (last_week_hrv == 0f) {

            //Weak duration
            if (activity_index < 30) {
                workout_duration = LOW_INITIAL_WORKOUT_DURATION;
                pulse_zone = 1;
            } else {
                //Fit duration
                workout_duration = HIGH_INITIAL_WORKOUT_DURATION;
                pulse_zone = 2 ;
            }
            return "Jums sugeneruota pirmoji programa";
        }

        //Checking if there is at least two weeks worth of data
        if (second_last_week_hrv == 0) {
            return "Nepakanka duomenų programos tobulinimui. Programa nekeičiama";
        }




        //Not upgrading duo to poor hrv
        if (0.5 * second_last_week_hrv < last_week_hrv && last_week_hrv < 0.85 * second_last_week_hrv) {
            return "Programa nepasikeitė dėl prasto HRV";
        }



        //Upgrading program due to very good hrv score
        if (last_week_hrv >= second_last_week_hrv * 0.85) {


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

                return "Jūs pasekėte savo max trukmę, todėl padidinsime jūsų intensyvumą";
            }

            //Increasing workout duration and checking if it does not exceed maximum available duration

            workout_duration *= 1.1;

            if (workout_duration <= max_duration) {
                return "Juma puikiai sekasi. Jūsų sportavimo trukmė pakilo 10%";
            } else {
                workout_duration = max_duration;
                return "Pasiekėtė max trukmę, todėl ši savaitė bus užtvirtinamoji.";
            }


        }

        return "not supported";
    }

    /**
     * Alters the weekly program based on current_hrv and yesterday_hrv.
     * workout_duration, verbal_reccomendation and pulse_zone values are changed
     */
    public void generateDailyReccomendation(Context context) {
        Calendar c = Calendar.getInstance();

        int dayOfWeek = Utils.getDayOfWeek(c);


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
            if (yesterday_hrv == 0 || current_hrv == 0) {
//                yesterday_hrv = baselineHrv;
                //yesterday_hrv = current_hrv;
                program_update_state = PROGRAM_STATE_NOT_ENOUGH_DATA;
                verbal_reccomendation = "Not enough data to generate reasonable workout plan";
                return;
            }

          float percentageChange = current_hrv / yesterday_hrv;

            workout_duration*=percentageChange;

            for(int i = 0; i < week_days.length; i++){
                Log.i("TEST", "week day " + i + week_days[i]);
            }


            if (!week_days[dayOfWeek]) {
                Log.i("TEST", "Šiandien jums poilsio diena");
                verbal_reccomendation =  "Šiandien rekomenduojame pailsėti";
                return;
            }

            if (minBaselineHrv <= current_hrv /*&& current_hrv <= maxBaselineHrv*/) {




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
                        verbal_reccomendation =  "Your HRV has increased,in comparison to last time. Upgrading your workout!";
                        return;
                    }

                    //hrv has greatly increased
                    Log.i("TEST", "hrv has greatly increased");
                    verbal_reccomendation =  "Incredible! Seems like you've been feeling great lately. Increasing training difficulty!";
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
                program_update_state = PROGRAM_STATE_NOT_ENOUGH_DATA; //todo: do some deciding (hrv out of bounds)
                Log.i("TEST", "Jusu hrv neatitinka normu");
                verbal_reccomendation =  "Jūsų hrv neatitinka normų";
                return;
            }









    }



    /**
     * Updates the measurement in the database
     * @param context     Current context
     * @param measurement The measurement to update
     * @param updateType  Type, which determines which column should be used to identify the measurement
     */
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

    private void setKMI(float KMI) {
        this.KMI = KMI;
    }

    public float getCurrentHrv() {
        return current_hrv;
    }

    private void setCurrentHrv(float current_hrv) {
        this.current_hrv = current_hrv;
    }


    public Date getBirthday() {
        return birthday;
    }

    private void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    private void setGender(int gender) {
        this.gender = gender;
    }

    public float getHeight() {
        return height;
    }

    private void setHeight(float height) {
        this.height = height;
    }

    public float getActivityIndex() {
        return activity_index;
    }

    private void setActivityIndex(float activity_index) {
        this.activity_index = activity_index;
    }

    public int getSelectedSport() {
        return selected_sport;
    }

    private void setSelectedSport(int selected_sport) {
        this.selected_sport = selected_sport;
    }

    public float getWeight() {
        return weight;
    }

    private void setWeight(float weight) {
        this.weight = weight;
    }

    public String getUserId() {
        return user_id;
    }

    private void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getUserPassword() {
        return user_password;
    }

    private void setUserPassword(String user_password) {
        this.user_password = user_password;
    }

    public boolean[] getWeekDays() {
        return week_days;
    }

    private void setWeekDays(boolean[] week_days) {
        this.week_days = week_days;
    }

    public float getMaxDuration() {
        return max_duration;
    }

    private void setMaxDuration(float base_duration) {
        this.max_duration = base_duration;
    }


    public float getYesterdayHrv() {
        return yesterday_hrv;
    }

    private void setYesterdayHrv(float yesterday_hrv) {
        this.yesterday_hrv = yesterday_hrv;
    }

    public float getLastWeekHrv() {
        return last_week_hrv;
    }

    private void setLastWeekHrv(float last_week_hrv) {
        this.last_week_hrv = last_week_hrv;
    }

    public int getPulseZone() {
        return pulse_zone;
    }

    private void setPulseZone(int pulse_zone) {
        this.pulse_zone = pulse_zone;
    }

    public float getWorkoutDuration() {
        return Math.round(workout_duration);
    }

    private void setWorkoutDuration(float workout_duration) {
        this.workout_duration = workout_duration;
    }

    public float getSecondLastWeekHrv() {
        return second_last_week_hrv;
    }

    private void setSecondLastWeekHrv(float second_last_week_hrv) {
        this.second_last_week_hrv = second_last_week_hrv;
    }

    public int getProgramUpdateState() {
        return program_update_state;
    }

    public float getHrvYesterdayTodayRatio() {


        if(yesterday_hrv == 0f){
            return 0;
        }

        return  current_hrv/ yesterday_hrv;

    }
    public String getVerbalReccomendation() {
        return verbal_reccomendation;
    }

    public Date getLastGeneratedWeeklyDate() {
        return last_generated_weekly_date;
    }

    private void setLastGeneratedWeeklyDate(Date last_generated_weekly_date) {
        this.last_generated_weekly_date = last_generated_weekly_date;
    }

    private void setWalkRunIntervals(long[] walkRunIntervals) {
        this.walkRunIntervals = walkRunIntervals;
    }

    private void setPulseZoneIntervals(int[] pulseZoneIntervals){
        this.pulseZoneIntervals = pulseZoneIntervals;
    }
    public int[] getPulseZoneIntervals(){
        return pulseZoneIntervals;
    }
    public long[] getWalkRunIntervals() {
        return walkRunIntervals;
    }
}
