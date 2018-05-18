package com.mabe.productions.hrv_madison;

public class Exercise {

    /*
     * The intervals of workout's walk/run ratio.
     * Every second element of the array represents running duration.
     * {walking_duration, running_duration, walking_duration, running_duration...}
     * Specified in seconds
     */
    private long[] workout_intervals =  new long[0];
    private int[] running_pulse_zones  =  {3, 4};
    private int[] walking_pulse_zones  =  {1, 2};

    public Exercise() {
    }

    public Exercise(long[] workout_intervals, int[] running_pulse_zones, int[] walking_pulse_zones) {
        this.workout_intervals = workout_intervals;
        this.running_pulse_zones = running_pulse_zones;
        this.walking_pulse_zones = walking_pulse_zones;
    }

    public long[] getWorkoutIntervals() {
        return workout_intervals;
    }

    public void setWorkoutIntervals(long[] workout_intervals) {
        this.workout_intervals = workout_intervals;
    }

    public int[] getRunningPulseZones() {
        return running_pulse_zones;
    }

    public void setRunningPulseZones(int[] running_pulse_zones) {
        this.running_pulse_zones = running_pulse_zones;
    }

    public int[] getWalkingPulseZones() {
        return walking_pulse_zones;
    }

    public void setWalkingPulseZones(int[] walking_pulse_zones) {
        this.walking_pulse_zones = walking_pulse_zones;
    }
}
