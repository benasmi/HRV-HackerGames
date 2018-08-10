package com.mabe.productions.hrv_madison;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable{

    /*
     * The intervals of workout's walk/run ratio.
     * Every second element of the array represents running duration.
     * {walking_duration, running_duration, walking_duration, running_duration...}
     * Specified in seconds
     */
    private long[] workout_intervals =  new long[0];
    private int[] running_pulse_zones  =  {3, 4};
    private int[] walking_pulse_zones  =  {1, 2};


    //{0,0} - Running always;
    //{0} - Walking always;

    public Exercise() {
    }

    public Exercise(long[] workout_intervals, int[] running_pulse_zones, int[] walking_pulse_zones) {
        this.workout_intervals = workout_intervals;
        this.running_pulse_zones = running_pulse_zones;
        this.walking_pulse_zones = walking_pulse_zones;
    }

    protected Exercise(Parcel in) {
        workout_intervals = in.createLongArray();
        running_pulse_zones = in.createIntArray();
        walking_pulse_zones = in.createIntArray();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

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

    public int getMaximumPulseZone() {
        int[] runningPulseZones = getRunningPulseZones();
        int[] walkingPulseZones = getWalkingPulseZones();

        if (walkingPulseZones.length != 0 && runningPulseZones.length != 0) {
            return Math.max(Utils.maxNum(runningPulseZones), Utils.maxNum(walkingPulseZones));
        } else if (walkingPulseZones.length == 0) {
            return Utils.maxNum(runningPulseZones);
        } else if (runningPulseZones.length == 0) {
            return Utils.maxNum(walkingPulseZones);
        }
        return 0;
    }

    public int getMinimumPulseZone() {
        int[] runningPulseZones = getRunningPulseZones();
        int[] walkingPulseZones = getWalkingPulseZones();

        if (walkingPulseZones.length != 0 && runningPulseZones.length != 0) {
            return Math.min(Utils.minNum(runningPulseZones), Utils.minNum(walkingPulseZones));
        } else if (walkingPulseZones.length == 0) {
            return Utils.minNum(runningPulseZones);
        } else if (runningPulseZones.length == 0) {
            return Utils.minNum(walkingPulseZones);
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLongArray(workout_intervals);
        parcel.writeIntArray(running_pulse_zones);
        parcel.writeIntArray(walking_pulse_zones);
    }
}
