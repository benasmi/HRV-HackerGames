package com.mabe.productions.hrv_madison.measurements;

import android.content.ContentValues;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mabe.productions.hrv_madison.Exercise;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebaseDatase.FireWorkout;

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
    private float distance;
    private Exercise exercise;

    public WorkoutMeasurements(Exercise exercise, int unique_id, Date date, float workout_duration, float average_bpm, int[] bpm_data, float[] pace_data, LatLng[] route, float calories_burned, float distance) {
        this.unique_id = unique_id;
        this.date = date;
        this.workout_duration = workout_duration;
        this.average_bpm = average_bpm;
        this.bpm_data = bpm_data;
        this.pace_data = pace_data;
        this.route = route;
        this.calories_burned = calories_burned;
        this.distance = distance;
        this.exercise = exercise;
    }
    public WorkoutMeasurements(Exercise exercise, Date date, float workout_duration,  float average_bpm, int[] bpm_data, float[] pace_data, LatLng[] route, float calories_burned, float distance) {
        this.date             = date;
        this.workout_duration = workout_duration;
        this.average_bpm      = average_bpm;
        this.bpm_data         = bpm_data;
        this.pace_data        = pace_data;
        this.route            = route;
        this.calories_burned  = calories_burned;
        this.distance         = distance;
        this.exercise         = exercise;
    }

    public WorkoutMeasurements(FireWorkout workout){

        this.date             = Utils.getDateFromString(workout.getDate());
        this.workout_duration = workout.getWorkout_duration();
        this.average_bpm      = workout.getAverage_bpm();
        this.bpm_data         = FeedReaderDbHelper.getIntArrayFromString(workout.getBpm_data());
        this.pace_data        = FeedReaderDbHelper.getFloatArrayFromString(workout.getPace_data());
        this.route            = FeedReaderDbHelper.getRouteFromString(workout.getRoute());
        this.calories_burned  = workout.getCalories_burned();
        this.distance         = workout.getDistance();

        this.exercise = new Exercise();
        this.exercise.setRunningPulseZones(FeedReaderDbHelper.getIntArrayFromString(workout.getRunning_pulse_zones()));
        this.exercise.setWalkingPulseZones(FeedReaderDbHelper.getIntArrayFromString(workout.getWalking_pulse_zones()));
        this.exercise.setWorkoutIntervals(FeedReaderDbHelper.getLongArrayFromString(workout.getWorkout_intervals()));

    }



    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.WORKOUT_COL_AVERAGE_BPM, getAverage_bpm());
        values.put(FeedReaderDbHelper.WORKOUT_COL_DATE, Utils.getStringFromDate(getDate()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_DURATION, getWorkout_duration());
        values.put(FeedReaderDbHelper.WORKOUT_COL_AVERAGE_BPM, getAverage_bpm());
        values.put(FeedReaderDbHelper.WORKOUT_COL_CALORIES, getCalories_burned());
        values.put(FeedReaderDbHelper.WORKOUT_COL_ROUTE, FeedReaderDbHelper.routeToString(getRoute()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_PACE_DATA, FeedReaderDbHelper.floatArrayToString(getPace_data()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_BPM_DATA, FeedReaderDbHelper.intArrayToString(getBpm_data()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_DISTANCE, getDistance());
        values.put(FeedReaderDbHelper.WORKOUT_COL_RUNNING_PULSE_ZONES, FeedReaderDbHelper.intArrayToString(exercise.getRunningPulseZones()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_WALKING_PULSE_ZONES, FeedReaderDbHelper.intArrayToString(exercise.getWalkingPulseZones()));
        values.put(FeedReaderDbHelper.WORKOUT_COL_INTERVALS, FeedReaderDbHelper.longArrayToString(exercise.getWorkoutIntervals()));
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

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }
}
