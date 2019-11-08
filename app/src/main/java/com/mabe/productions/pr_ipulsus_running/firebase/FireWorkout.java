package com.mabe.productions.pr_ipulsus_running.firebase;

import com.mabe.productions.pr_ipulsus_running.Utils;
import com.mabe.productions.pr_ipulsus_running.database.FeedReaderDbHelper;
import com.mabe.productions.pr_ipulsus_running.measurements.WorkoutMeasurements;

public class FireWorkout {

    private String date; //Date object as string
    private float workout_duration;
    private float average_bpm;
    private String bpm_data; //int array as string
    private String pace_data; //in kilometers/minute; Float array converted to string
    private String route; //LatLng array as string
    private float calories_burned; //In KCal
    private float distance;

    private String workout_intervals;
    private String running_pulse_zones;
    private String walking_pulse_zones;

    public FireWorkout() {

    }

    public FireWorkout(WorkoutMeasurements workoutMeasurements) {
        this.date = Utils.getStringFromDate(workoutMeasurements.getDate());
        this.workout_duration = workoutMeasurements.getWorkout_duration();
        this.average_bpm = workoutMeasurements.getAverage_bpm();
        this.bpm_data = FeedReaderDbHelper.intArrayToString(workoutMeasurements.getBpm_data());
        this.pace_data = FeedReaderDbHelper.floatArrayToString(workoutMeasurements.getPace_data());
        this.route = FeedReaderDbHelper.routeToString(workoutMeasurements.getRoute());
        this.calories_burned = workoutMeasurements.getCalories_burned();
        this.distance = workoutMeasurements.getDistance();
        this.workout_intervals = FeedReaderDbHelper.longArrayToString(workoutMeasurements.getExercise().getWorkoutIntervals());
        this.running_pulse_zones = FeedReaderDbHelper.intArrayToString(workoutMeasurements.getExercise().getRunningPulseZones());
        this.walking_pulse_zones = FeedReaderDbHelper.intArrayToString(workoutMeasurements.getExercise().getWalkingPulseZones());
    }

    public FireWorkout(String date, float workout_duration, float average_bpm, String bpm_data, String pace_data, String route, float calories_burned, float distance, String workout_intervals, String running_pulse_zones, String walking_pulse_zones) {
        this.date = date;
        this.workout_duration = workout_duration;
        this.average_bpm = average_bpm;
        this.bpm_data = bpm_data;
        this.pace_data = pace_data;
        this.route = route;
        this.calories_burned = calories_burned;
        this.distance = distance;
        this.workout_intervals = workout_intervals;
        this.running_pulse_zones = running_pulse_zones;
        this.walking_pulse_zones = walking_pulse_zones;
    }

    public String getDate() {
        return date;
    }

    public float getWorkout_duration() {
        return workout_duration;
    }

    public float getAverage_bpm() {
        return average_bpm;
    }

    public String getBpm_data() {
        return bpm_data;
    }

    public String getPace_data() {
        return pace_data;
    }

    public String getRoute() {
        return route;
    }

    public float getCalories_burned() {
        return calories_burned;
    }

    public float getDistance() {
        return distance;
    }

    public String getWorkout_intervals() {
        return workout_intervals;
    }

    public String getRunning_pulse_zones() {
        return running_pulse_zones;
    }

    public String getWalking_pulse_zones() {
        return walking_pulse_zones;
    }
}
