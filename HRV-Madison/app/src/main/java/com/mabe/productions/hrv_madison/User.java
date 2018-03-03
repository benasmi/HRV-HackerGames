package com.mabe.productions.hrv_madison;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class User {

    public final static int GENDER_MALE = 0;
    public final static int GENDER_FEMALE = 1;

    public final static int SPORT_JOGGING = 0;

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
    private Jogging jogging;

    private String user_id;
    private String user_password;

    private boolean[] week_days = new boolean[7];
    private float base_duration; //In minutes


    public static User getUser(Context context){
        User user = new User();

        //Getting initial user data from SharedPreferences
        user.setKMI(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_KMI));
        user.setBirthday(Utils.getDateFromString(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_BIRTHDAY)));
        user.setGender(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_GENDER));
        user.setHeight(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_HEIGHT));
        user.setActivityIndex(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_ACTIVITY_INDEX));
        user.setSelectedSport(Utils.readFromSharedPrefs_int(context, FeedReaderDbHelper.FIELD_SELECTED_SPORT));
        user.setWeight(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_WEIGHT));
        user.setUserId(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_USER_ID));
        user.setUserPassword(Utils.readFromSharedPrefs_string(context, FeedReaderDbHelper.FIELD_PASSWORD));
        user.setWeekDays(Utils.readFromSharedPrefs_boolarray(context, FeedReaderDbHelper.FIELD_WEEK_DAYS));
        //TODO: Ar is sharedprefu imti base duration?
        user.setBaseDuration(Utils.readFromSharedPrefs_float(context, FeedReaderDbHelper.FIELD_BASE_DURATION));


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

        //Getting user hrv data from SQLite
        while (cursor.moveToNext()){
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_ID));
            int rmssd = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_RMSSD));
            String dateString = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COL_DATE));

            //Setting user's current hrv to the last measured hrv (since ordering is DESC, the first rmssd is the most recent one)
            //TODO: palikti ar pakeisti?
/*            if(user.current_hrv == 0){
                user.current_hrv = rmssd;
            }else if(user.yesterday_hrv == 0){ //Second last measurement is used as yesterday's measurement
                user.yesterday_hrv = rmssd;

            }
*/
            Date date = Utils.getDateFromString(dateString);

            //Summing up rmssd if date if from last week
            int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            calendar.setTime(date);
            if(currentWeek == calendar.get(Calendar.WEEK_OF_YEAR)+1) {
                rmssdCountLastWeek++;
                rmssdSumLastWeek+=rmssd;
            }

            Log.i("TEST", itemId + " " + rmssd + " " + date.toString());
        }

        if(rmssdCountLastWeek==0){
            //If there are no measurements from last week, using last measurement as last week's measurement
            user.last_week_hrv = user.current_hrv;
        }else{
            //Calculating average last week's hrv
            user.last_week_hrv = ((float) rmssdSumLastWeek)/ ((float)rmssdCountLastWeek);

        }





        //Dummy data
//        user.setKMI(20);
        user.setCurrentHrv(40);
        user.setLastWeekHrv(30);
//        user.setBirthday(new Date(1990, 1, 25));
//        user.setGender(GENDER_MALE);
//        user.setHeight(180);
        user.setActivityIndex(18);
        user.setSelectedSport(SPORT_JOGGING);
        user.setWeekDays(new boolean[]{false, true, true, true, false, true, false});
        user.setBaseDuration(20);
        return user;
    }

    public String generateWeeklyProgram(){


        return "not supported";
    }

           public String generateDailyReccomendation(Context context){
            Calendar c = Calendar.getInstance();
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            Log.i("TEST", "day of week: "+ dayOfWeek );

            if(week_days[dayOfWeek]){

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




            if(minBaselineHrv <= current_hrv && current_hrv <= maxBaselineHrv){

                float percentageChange = current_hrv/yesterday_hrv;

                //Hrv has increased
                if(percentageChange >= 1 ){
                    float percentageIncrease = percentageChange-1;

                    if(percentageChange >= HIGH_INCREASE){
                        //hrv has greatly increased
                        return "hrv has greatly increased";
                    }
                    if(percentageChange >= MEDIOCRE_INCREASE){
                        return "detected mediocre hrv increase";
                    }
                    if(percentageChange <= MINIMAL_INCREASE){
                        return "detected minimal hrv increase";
                    }

                }else{
                    //Hrv has decreased
                    float percentageDecrease = 1-percentageChange;

                    if(percentageChange >= HIGH_DECREASE){
                        //hrv has greatly increased
                        return "hrv has greatly decreased";
                    }
                    if(percentageChange >= MEDIOCRE_DECREASE){

                        return "detected mediocre hrv decrease";
                    }
                    if(percentageChange <= MINIMAL_DECREASE){

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

    public float getCurrent_hrv() {
        return current_hrv;
    }

    public void setCurrentHrv(float current_hrv) {
        this.current_hrv = current_hrv;
    }

    public float getLastWeekHrv() {
        return last_week_hrv;
    }

    public void setLastWeekHrv(float last_week_hrv) {
        this.last_week_hrv = last_week_hrv;
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

    public float getBaseDuration() {
        return base_duration;
    }

    public void setBaseDuration(float base_duration) {
        this.base_duration = base_duration;
    }








}
