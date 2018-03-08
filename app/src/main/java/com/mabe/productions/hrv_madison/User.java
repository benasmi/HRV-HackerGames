package com.mabe.productions.hrv_madison;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.measurements.BPM;
import com.mabe.productions.hrv_madison.measurements.FrequencyMethod;
import com.mabe.productions.hrv_madison.measurements.Measurement;
import com.mabe.productions.hrv_madison.measurements.RMSSD;


import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

public class User {

    public final static int GENDER_MALE = 0;
    public final static int GENDER_FEMALE = 1;

    public final static int SPORT_JOGGING = 0;

    private static final float POOR_ACTIVITY_INDEX = 30;
    private static final float LOW_INITIAL_WORKOUT_DURATION = 15;
    private static final float HIGH_INITIAL_WORKOUT_DURATION = 20;

    private int program_update_state;

    public static final int PROGRAM_STATE_UPGRADED = 0;
    public static final int PROGRAM_STATE_DOWNGRADED = 1;
    public static final int PROGRAM_STATE_UNCHANGED = 2;

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

    private int pulse_zone;
    private float workout_duration;
    public float second_last_week_hrv;


    private String user_id;
    private String user_password;

    private boolean[] week_days = new boolean[7];
    private float max_duration; //In minutes
    private ArrayList<Measurement> measurements;


    /*
     * Saves a new measurement to the database.
     * @param overrideByDate If true, existing table row with today's date is overriden.
     */
    //todo: save measurement duration
    public static void addMeasurementData(Context context, Measurement measurement, boolean overrideByDate){
        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        //Checking if a measurement with today's date exists
        String todayDate = Utils.getStringFromDate(Calendar.getInstance().getTime());

        String[] projection = {
                FeedReaderDbHelper.COL_DATE,
                FeedReaderDbHelper.COL_ID,

        };

        String sortOrder =
                FeedReaderDbHelper.COL_ID+ " DESC";

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
        if(cursor.moveToNext() && overrideByDate){
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_ID));
            String dateString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_DATE));

            //Removing existing row if measurement was made today
            if(dateString.equals(todayDate)){
                cursor.close();
                db.delete(FeedReaderDbHelper.HRV_DATA_TABLE_NAME, FeedReaderDbHelper.COL_ID + "=" + id, null);
            }

        }

        //Inserting values
        ContentValues values = new ContentValues();
            values.put(FeedReaderDbHelper.COL_RMSSD, measurement.getRmssd());
            values.put(FeedReaderDbHelper.COL_LN_RMSSD, measurement.getLn_rmssd());
            values.put(FeedReaderDbHelper.COL_LOWEST_RMSSD, measurement.getLowest_rmssd());
            values.put(FeedReaderDbHelper.COL_HIGHEST_RMSSD, measurement.getHighest_rmssd());
            values.put(FeedReaderDbHelper.COL_RMSSD_DATA, FeedReaderDbHelper.getStringFromBpmValues(measurement.getRmssd_data()));
            values.put(FeedReaderDbHelper.COL_LOWEST_BPM, measurement.getLowest_bpm());
            values.put(FeedReaderDbHelper.COL_HIGHEST_BPM, measurement.getHighest_bpm());
            values.put(FeedReaderDbHelper.COL_AVERAGE_BPM, measurement.getAverage_bpm());
            values.put(FeedReaderDbHelper.COL_BPM_DATA, FeedReaderDbHelper.getStringFromBpmValues(measurement.getBpm_data()));

            values.put(FeedReaderDbHelper.COL_LF_BAND, measurement.getLF_band());
            values.put(FeedReaderDbHelper.COL_HF_BAND, measurement.getHF_band());
            values.put(FeedReaderDbHelper.COL_VLF_BAND, measurement.getVLF_band());
            values.put(FeedReaderDbHelper.COL_VHF_BAND, measurement.getVHF_band());

        values.put(FeedReaderDbHelper.COL_DATE, Utils.getStringFromDate(Calendar.getInstance().getTime()));
        db.insert(FeedReaderDbHelper.HRV_DATA_TABLE_NAME, null, values);

    }

    /*
     * Returns last saved measurement
     * todo: not sure if this returns the last, or the first measurement
     */
    public Measurement getLastMeasurement(){
        return measurements.get(0);
    }

    private void getAllMeasurementsFromDb(Context context){

        ArrayList<Measurement> measurementList = new ArrayList<>();

        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        String[] projection = {
                FeedReaderDbHelper.COL_DATE,
                FeedReaderDbHelper.COL_ID,
                FeedReaderDbHelper.COL_RMSSD,
                FeedReaderDbHelper.COL_LN_RMSSD,
                FeedReaderDbHelper.COL_LOWEST_RMSSD,
                FeedReaderDbHelper.COL_HIGHEST_RMSSD,
                FeedReaderDbHelper.COL_LOWEST_BPM,
                FeedReaderDbHelper.COL_HIGHEST_BPM,
                FeedReaderDbHelper.COL_AVERAGE_BPM,
                FeedReaderDbHelper.COL_LF_BAND,
                FeedReaderDbHelper.COL_VLF_BAND,
                FeedReaderDbHelper.COL_VHF_BAND,
                FeedReaderDbHelper.COL_HF_BAND,
                FeedReaderDbHelper.COL_BPM_DATA,
                FeedReaderDbHelper.COL_RMSSD_DATA

        };

        String sortOrder =
                FeedReaderDbHelper.COL_ID+ " DESC";

        Cursor cursor = db.query(
                FeedReaderDbHelper.HRV_DATA_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );



        while(cursor.moveToNext()){
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_ID));
            Date date = Utils.getDateFromString(cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_DATE)));
            int rmssd = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_RMSSD));
            float ln_rmssd = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_LN_RMSSD));
            float lowest_rmssd = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_LOWEST_RMSSD));
            float highest_rmssd = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_HIGHEST_RMSSD));
            float lowest_bpm = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_LOWEST_BPM));
            float highest_bpm = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_HIGHEST_BPM));
            float average_bpm = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_AVERAGE_BPM));
            float LF_band = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_LF_BAND));
            float VLF_band = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_VLF_BAND));
            float VHF_band = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_VHF_BAND));
            float HF_band = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_HF_BAND));
            String bpmDataString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_BPM_DATA));
            Log.i("bpmdata", "bpm data string: " + bpmDataString);
            int[] bpmData = FeedReaderDbHelper.getBpmValuesFromString(bpmDataString);


            String rmssdDataString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_RMSSD_DATA));
            int[] rmssdData = FeedReaderDbHelper.getBpmValuesFromString(rmssdDataString);

            Measurement measurement = new Measurement(date, rmssd, ln_rmssd, lowest_rmssd, highest_rmssd, lowest_bpm, highest_bpm, average_bpm, LF_band, VLF_band, VHF_band, HF_band, bpmData,rmssdData);

            measurementList.add(measurement);

        }



        measurements = measurementList;
    }

    public static User getUser(Context context){
        User user = new User();

        //Getting initial user data from SharedPreferences
        user.setKMI(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_KMI,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setBirthday(Utils.getDateFromString(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_BIRTHDAY,FeedReaderDbHelper.SHARED_PREFS_USER_DATA)));
        user.setGender(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_GENDER,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setHeight(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_HEIGHT,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setActivityIndex(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_ACTIVITY_INDEX,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setSelectedSport(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_SELECTED_SPORT,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setWeight(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_WEIGHT,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setUserId(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_USER_ID,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setUserPassword(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_PASSWORD,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setMaxDuration(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_BASE_DURATION,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        user.setWorkoutDuration(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_DURATION, FeedReaderDbHelper.SHARED_PREFS_SPORT));
        user.setPulseZone(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_PULSE_ZONE, FeedReaderDbHelper.SHARED_PREFS_SPORT));
        user.setWeekDays(Utils.readFromSharedPrefs_boolarray(context, FeedReaderDbHelper.FIELD_WEEK_DAYS, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));


        FeedReaderDbHelper databaseHelper = new FeedReaderDbHelper(context);
        String[] projection = {
                FeedReaderDbHelper.COL_ID,
                FeedReaderDbHelper.COL_RMSSD,
                FeedReaderDbHelper.COL_DATE

        };

        String sortOrder =
                FeedReaderDbHelper.COL_ID+ " DESC";

        Cursor cursor = databaseHelper.getReadableDatabase().query(
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
        while (cursor.moveToNext()){
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_ID));
            int rmssd = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_RMSSD));
            String dateString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_DATE));


            Date date = Utils.getDateFromString(dateString);

            //Summing up rmssd if date if from last week
            int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            calendar.setTime(date);

            if(currentWeek-1 ==  (calendar.get(Calendar.WEEK_OF_YEAR))) {
                rmssdCountLastWeek++;
                rmssdSumLastWeek+=rmssd;
            }


            if(currentWeek-2 == (calendar.get(Calendar.WEEK_OF_YEAR))) {
                rmssdCountSecondLastWeek++;
                rmssdSumSecondLastWeek+=rmssd;
            }

        }

        if(rmssdCountLastWeek!=0){
            //Calculating average last week's hrv
            user.last_week_hrv = ((float) rmssdSumLastWeek)/ ((float)rmssdCountLastWeek);

            if(rmssdCountSecondLastWeek!=0){
                user.second_last_week_hrv = ((float) rmssdSumSecondLastWeek)/ ((float)rmssdCountSecondLastWeek);
            }
        }






        //Dummy data
        //user.setCurrentHrv(40);
        user.setLastWeekHrv(30);
        user.setSecondLastWeekHrv(20);
        user.setSelectedSport(SPORT_JOGGING);
        user.setMaxDuration(20);

        user.getAllMeasurementsFromDb(context);
        ArrayList<Measurement> measurements = user.getAllMeasurements();

        calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        for(int i = 0; i < measurements.size(); i++){
            calendar.setTime(measurements.get(i).getDate());
            int measurementDay = calendar.get(Calendar.DAY_OF_YEAR);

            if(measurementDay == today){
                //Today's measurement
                user.setCurrentHrv(measurements.get(i).getRmssd());
            }else if(measurementDay == today-1){
                //yesterday's measurement
                user.setYesterdayHrv(measurements.get(i).getRmssd());
            }
        }

        user.generateWeeklyProgram(context);
        user.generateDailyReccomendation(context);


        return user;
    }


    private void saveProgram(Context context){
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_DURATION, workout_duration, FeedReaderDbHelper.SHARED_PREFS_SPORT);
        Utils.saveToSharedPrefs(context, FeedReaderDbHelper.FIELD_PULSE_ZONE, pulse_zone, FeedReaderDbHelper.SHARED_PREFS_SPORT);
    }

    public ArrayList<Measurement> getAllMeasurements(){
        return measurements;
    }

    public String generateWeeklyProgram(Context context){



        //First time
        if(last_week_hrv == 0){

            //Weak duration
            if(activity_index < 30){
                workout_duration = 15;
            }else{
                //Fit duration
                workout_duration = 20;
            }
            saveProgram(context);
            return "Jums sugeneruota pirmoji programa";
        }

        //Checking if there is at least two weeks worth of data
        if(second_last_week_hrv == 0){
            return "Nepakanka duomenų programos tobulinimui. Programa nekeičiama";
        }

        //Not upgrading duo to poor hrv
        if(0.5*last_week_hrv < current_hrv && current_hrv < 0.85*last_week_hrv){
            return "Programa nepasikeitė dėl prasto HRV";
        }


        //Upgrading program due to very good hrv score
        if(current_hrv >= last_week_hrv*0.85){


            //if user has reached his max available workout duration, increasing pulse_zone(intensity) and decreasing workout duration(time).
            if(workout_duration == max_duration){

                //Decreasing to minimum workout duration based on activity index
                if(activity_index < POOR_ACTIVITY_INDEX) {
                    //User is not active enough, hence initial workout duration is lower
                    workout_duration = LOW_INITIAL_WORKOUT_DURATION;
                }else{
                    //User is active, hence initial workout duration is higher
                    workout_duration = HIGH_INITIAL_WORKOUT_DURATION;
                }
                pulse_zone++;

                //There are only 5 pulse zones
                if(pulse_zone > 5){
                    pulse_zone = 5;
                }
                saveProgram(context);

                return "Jūs pasekėte savo max trukmę, todėl padidinsime jūsų intensyvumą";
            }

            //Increasing workout duration and checking if it does not exceed maximum available duration

            workout_duration*=1.1;
            if(workout_duration<= max_duration){
                saveProgram(context);
                return "Juma puikiai sekasi. Jūsų sportavimo trukmė pakilo 10%";
            }else{
                workout_duration = max_duration;
                saveProgram(context);
                return "Pasiekėtė max trukmę, todėl ši savaitė bus užtvirtinamoji.";
            }


        }

        return "not supported";
    }

    public String generateDailyReccomendation(Context context){
            Calendar c = Calendar.getInstance();

            //Returns the current week day, where 1 is Monday
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            if(week_days[dayOfWeek-1]){

                final float HIGH_INCREASE = 0.3f;
                final float MEDIOCRE_INCREASE = 0.15f;
                final float MINIMAL_INCREASE = 0.05f;
                final float MINIMAL_DECREASE = 0.05f;
                final float MEDIOCRE_DECREASE = 0.15f;
                final float HIGH_DECREASE = 0.3f;

                int baselineHrv = -1;
                int minBaselineHrv = -1;
                int maxBaselineHrv = -1;
                int hrvBias = -1;

                Resources resources = context.getResources();

            int age = Utils.getAgeFromDate(birthday);

            if(age >= 18 && age <= 24){
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_18_24_M) : resources.getInteger(R.integer.baseline_18_24_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_18_24_M) : resources.getInteger(R.integer.baseline_bias_18_24_F);
            }else if(age >=25 && age <= 34){
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_25_34_M) : resources.getInteger(R.integer.baseline_25_34_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_25_34_M) : resources.getInteger(R.integer.baseline_bias_25_34_F);
            }else if(age >=35 && age <= 44){
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_35_44_M) : resources.getInteger(R.integer.baseline_35_44_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_35_44_M) : resources.getInteger(R.integer.baseline_bias_35_44_F);
            }else if(age >=45 && age <= 54){
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_45_54_M) : resources.getInteger(R.integer.baseline_45_54_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_45_54_M) : resources.getInteger(R.integer.baseline_bias_45_54_F);
            }else if(age >=55 && age <= 64){
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_55_64_M) : resources.getInteger(R.integer.baseline_55_64_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_55_64_M) : resources.getInteger(R.integer.baseline_bias_55_64_F);
            }else if(age >=65 && age <= 74){
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_65_74_M) : resources.getInteger(R.integer.baseline_65_74_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_65_74_M) : resources.getInteger(R.integer.baseline_bias_65_74_F);
            }else if(age >= 75){
                baselineHrv = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_75_M) : resources.getInteger(R.integer.baseline_75_F);
                hrvBias = gender == GENDER_MALE ? resources.getInteger(R.integer.baseline_bias_75_M) : resources.getInteger(R.integer.baseline_bias_75_F);
            }

            minBaselineHrv = baselineHrv - hrvBias;
            maxBaselineHrv = baselineHrv + hrvBias;

            //If no yesterday data is present
            if(yesterday_hrv == 0){
                yesterday_hrv = baselineHrv;
            }




            if(minBaselineHrv <= current_hrv /*&& current_hrv <= maxBaselineHrv*/){

                float percentageChange = current_hrv/yesterday_hrv;

                //Hrv has increased
                if(percentageChange >= 1 ){
                    program_update_state = PROGRAM_STATE_UPGRADED;
                    float percentageIncrease = percentageChange-1;

                    if(percentageChange >= HIGH_INCREASE){
                        //hrv has greatly increased
                        Log.i("TEST", "hrv has greatly increased");
                        return "hrv has greatly increased";
                    }
                    if(percentageChange >= MEDIOCRE_INCREASE){
                        Log.i("TEST", "mediocre hrv increase");
                        return "detected mediocre hrv increase";
                    }
                    if(percentageChange <= MINIMAL_INCREASE){
                        program_update_state = PROGRAM_STATE_UNCHANGED;
                        Log.i("TEST", "detected minimal hrv increase");
                        return "detected minimal hrv increase";
                    }

                }else{
                    //Hrv has decreased
                    program_update_state = PROGRAM_STATE_DOWNGRADED;
                    float percentageDecrease = 1-percentageChange;

                    if(percentageChange >= HIGH_DECREASE){
                        //hrv has greatly increased
                        Log.i("TEST", "detected great hrv decrease");
                        return "hrv has greatly decreased";
                    }
                    if(percentageChange >= MEDIOCRE_DECREASE){
                        Log.i("TEST", "detected mediocre hrv decrease");
                        return "detected mediocre hrv decrease";
                    }
                    if(percentageChange <= MINIMAL_DECREASE){
                        program_update_state = PROGRAM_STATE_UNCHANGED;
                        Log.i("TEST", "detected minimal hrv decrease");
                        return "detected minimal hrv decrese";
                    }
                }



            }else{
                Log.i("TEST", "Jusu hrv neatitinka normu");
                return "Jūsų hrv neatitinka normų";
            }



        }else{
            Log.i("TEST", "Šiandien jums poilsio diena");
            return "Šiandien jums poilsio diena";
        }

        return "not supported";

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
}
