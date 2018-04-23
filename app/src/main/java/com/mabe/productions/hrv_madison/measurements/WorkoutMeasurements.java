package com.mabe.productions.hrv_madison.measurements;

import android.content.ContentValues;

import com.google.android.gms.maps.model.LatLng;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

import java.util.Date;

/**
 * Created by Martynas on 2018-03-17.
 */



public class WorkoutMeasurements {

    private int unique_id;
    private Date date;
    private float workout_duration;
    private float average_bpm;
    private int[] bpm_data;
    private float[] pace_data; //in kilometers/minute
    private LatLng[] route;
    private float calories_burned; //In KCal
    private int pulse_zone;
    private float distance;

    public WorkoutMeasurements(int unique_id, Date date, float workout_duration, float average_bpm, int[] bpm_data, float[] pace_data, LatLng[] route, float calories_burned, int pulse_zone, float distance) {
        this.unique_id = unique_id;
        this.date = date;
        this.workout_duration = workout_duration;
        this.average_bpm = average_bpm;
        this.bpm_data = bpm_data;
        this.pace_data = pace_data;
        this.route = route;
        this.calories_burned = calories_burned;
        this.pulse_zone = pulse_zone;
        this.distance = distance;
    }
    public WorkoutMeasurements(Date date, float workout_duration,  float average_bpm, int[] bpm_data, float[] pace_data, LatLng[] route, float calories_burned, int pulse_zone, float distance) {
        this.date = date;
        this.workout_duration = workout_duration;
        this.average_bpm = average_bpm;
        this.bpm_data = bpm_data;
        this.pace_data = pace_data;
        this.route = route;
        this.calories_burned = calories_burned;
        this.pulse_zone = pulse_zone;
        this.distance = distance;
    }



    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.WORKOUT_COL_AVERAGE_BPM, getAverage_bpm());
        values.put(FeedReaderDbHelper.WORKOUT_COL_DATE, Utils.getStringFromDate(getDate()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_DURATION, getWorkout_duration());
        values.put(FeedReaderDbHelper.WORKOUT_COL_AVERAGE_BPM, getAverage_bpm());
        values.put(FeedReaderDbHelper.WORKOUT_COL_CALORIES, getCalories_burned());
        values.put(FeedReaderDbHelper.WORKOUT_COL_PULSE_ZONE, getPulse_zone());
        values.put(FeedReaderDbHelper.WORKOUT_COL_ROUTE, FeedReaderDbHelper.routeToString(getRoute()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_PACE_DATA, FeedReaderDbHelper.floatArrayToString(getPace_data()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_BPM_DATA, FeedReaderDbHelper.intArrayToString(getBpm_data()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_DISTANCE, getDistance());
        return values;
    }


    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(int unique_id) {
        this.unique_id = unique_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getWorkout_duration() {
        return workout_duration;
    }

    public void setWorkout_duration(float workout_duration) {
        this.workout_duration = workout_duration;
    }

    public float getAveragePace() {

        if(pace_data.length == 0){
            return 0;
        }

        float totalPace = 0;
        for(float pace : pace_data){
            totalPace+=pace;
        }

        return Math.round(totalPace*100f/(float) pace_data.length)/100f;
    }


    public float getAverage_bpm() {
        return average_bpm;
    }

    public void setAverage_bpm(float average_bpm) {
        this.average_bpm = average_bpm;
    }

    public int[] getBpm_data() {
        return bpm_data;
    }

    public void setBpm_data(int[] bpm_data) {
        this.bpm_data = bpm_data;
    }

    public float[] getPace_data() {
        return pace_data;
    }

    public void setPace_data(float[] pace_data) {
        this.pace_data = pace_data;
    }

    public LatLng[] getRoute() {
        return route;
    }

    public void setRoute(LatLng[] route) {
        this.route = route;
    }

    public float getCalories_burned() {
        return calories_burned;
    }

    public void setCalories_burned(float calories_burned) {
        this.calories_burned = calories_burned;
    }

    public int getPulse_zone() {
        return pulse_zone;
    }

    public void setPulse_zone(int pulse_zone) {
        this.pulse_zone = pulse_zone;
    }
}
