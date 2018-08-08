package com.mabe.productions.hrv_madison.firebaseDatase;

import com.google.android.gms.maps.model.LatLng;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;

import java.util.Date;

public class FireWorkout {

    private String date; //Date object as string
    private float workout_duration;
    private float average_bpm;
    private String bpm_data; //int array as string
    private String pace_data; //in kilometers/minute; Float array converted to string
    private String route; //LatLng array as string
    private float calories_burned; //In KCal
    private int pulse_zone;
    private float distance;

    public FireWorkout(){

    }

    public FireWorkout(WorkoutMeasurements workoutMeasurements){
        this.date             = Utils.getStringFromDate(workoutMeasurements.getDate());
        this.workout_duration = workoutMeasurements.getWorkout_duration();
        this.average_bpm      = workoutMeasurements.getAverage_bpm();
        this.bpm_data         = FeedReaderDbHelper.intArrayToString(workoutMeasurements.getBpm_data());
        this.pace_data        = FeedReaderDbHelper.floatArrayToString(workoutMeasurements.getPace_data());
        this.route            = FeedReaderDbHelper.routeToString(workoutMeasurements.getRoute());
        this.calories_burned  = workoutMeasurements.getCalories_burned();
        this.pulse_zone       = workoutMeasurements.getPulse_zone();
        this.distance         = workoutMeasurements.getDistance();
    }

    public FireWorkout(String date, float workout_duration, float average_bpm, String bpm_data, String pace_data, String route, float calories_burned, int pulse_zone, float distance) {
        this.date             = date;
        this.workout_duration = workout_duration;
        this.average_bpm      = average_bpm;
        this.bpm_data         = bpm_data;
        this.pace_data        = pace_data;
        this.route            = route;
        this.calories_burned  = calories_burned;
        this.pulse_zone       = pulse_zone;
        this.distance         = distance;
    }

    public String getDate() {
        return date;
    }

    public float getWorkoutDuration() {
        return workout_duration;
    }

    public float getAverageBpm() {
        return average_bpm;
    }

    public String getBpmData() {
        return bpm_data;
    }

    public String getPaceData() {
        return pace_data;
    }

    public String getRoute() {
        return route;
    }

    public float getCaloriesBurned() {
        return calories_burned;
    }

    public int getPulseZone() {
        return pulse_zone;
    }

    public float getDistance() {
        return distance;
    }
}
