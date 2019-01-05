package com.mabe.productions.hrv_madison;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;

import java.util.Date;

/**
 * Created by Benas on 7/31/2018.
 */

public class HistoryRecyclerViewDataHolder implements Parcelable  {


    /*
     * average_bpm, bpm_data, durationand date is used for both measurements and workouts
     */


    //HRV MEASUREMENT DATA
    private Date date;
    private int duration;
    private int rmssd;
    private float ln_rmssd;
    private float lowest_rmssd;
    private float highest_rmssd;
    private float lowest_bpm;
    private float highest_bpm;
    private float average_bpm;
    private float LF_band;
    private float VLF_band;
    private float VHF_band;
    private float HF_band;
    private int[] bpm_data;
    private int[] rmssd_data;
    private int mood;
    private int hrv;


    //WORKOUT DATA
    private int unique_id;
    private float workout_duration;
    private float[] pace_data; //in kilometers/minute
    private LatLng[] route;
    private float calories_burned; //In KCal
    private float distance;
    private Exercise exercise;


    private int viewType = 0;



    public HistoryRecyclerViewDataHolder(Exercise exercise, int unique_id, Date date, float workout_duration, float average_bpm, int[] bpm_data, float[] pace_data, LatLng[] route, float calories_burned, float distance, int viewType) {
        this.unique_id = unique_id;
        this.date = date;
        this.workout_duration = workout_duration;
        this.average_bpm = average_bpm;
        this.bpm_data = bpm_data;
        this.pace_data = pace_data;
        this.route = route;
        this.calories_burned = calories_burned;
        this.distance = distance;
        this.viewType = viewType;
        this.exercise = exercise;
    }

    public HistoryRecyclerViewDataHolder(WorkoutMeasurements workout) {
        this.unique_id = workout.getUnique_id();
        this.date = workout.getDate();
        this.workout_duration = workout.getWorkout_duration();
        this.average_bpm = workout.getAverage_bpm();
        this.bpm_data = workout.getBpm_data();
        this.pace_data = workout.getPace_data();
        this.route = workout.getRoute();
        this.calories_burned = workout.getCalories_burned();
        this.distance = workout.getDistance();
        this.viewType = 1;
        this.exercise = workout.getExercise();
    }

    public HistoryRecyclerViewDataHolder(Date date, int duration, int rmssd, float ln_rmssd, float lowest_rmssd, float highest_rmssd, float lowest_bpm, float highest_bpm, float average_bpm, float LF_band, float VLF_band, float VHF_band, float HF_band, int[] bpm_data, int[] rmssd_data, int uniqueId, int mood, int hrv, int viewType) {
        this.date = date;
        this.duration = duration;
        this.rmssd = rmssd;
        this.ln_rmssd = ln_rmssd;
        this.lowest_rmssd = lowest_rmssd;
        this.highest_rmssd = highest_rmssd;
        this.lowest_bpm = lowest_bpm;
        this.highest_bpm = highest_bpm;
        this.average_bpm = average_bpm;
        this.LF_band = LF_band;
        this.VLF_band = VLF_band;
        this.VHF_band = VHF_band;
        this.HF_band = HF_band;
        this.bpm_data = bpm_data;
        this.rmssd_data = rmssd_data;
        this.unique_id = uniqueId;
        this.mood = mood;
        this.hrv = hrv;
        this.viewType = viewType;
    }




    protected HistoryRecyclerViewDataHolder(Parcel in) {
        duration = in.readInt();
        rmssd = in.readInt();
        ln_rmssd = in.readFloat();
        lowest_rmssd = in.readFloat();
        highest_rmssd = in.readFloat();
        lowest_bpm = in.readFloat();
        highest_bpm = in.readFloat();
        average_bpm = in.readFloat();
        LF_band = in.readFloat();
        VLF_band = in.readFloat();
        VHF_band = in.readFloat();
        HF_band = in.readFloat();
        bpm_data = in.createIntArray();
        rmssd_data = in.createIntArray();
        mood = in.readInt();
        hrv = in.readInt();
        unique_id = in.readInt();
        workout_duration = in.readFloat();
        pace_data = in.createFloatArray();
        route = in.createTypedArray(LatLng.CREATOR);
        calories_burned = in.readFloat();
        distance = in.readFloat();
        viewType = in.readInt();
        date = (Date) in.readSerializable();
        exercise = in.readParcelable(Exercise.class.getClassLoader());
    }

    public static final Creator<HistoryRecyclerViewDataHolder> CREATOR = new Creator<HistoryRecyclerViewDataHolder>() {
        @Override
        public HistoryRecyclerViewDataHolder createFromParcel(Parcel in) {
            return new HistoryRecyclerViewDataHolder(in);
        }

        @Override
        public HistoryRecyclerViewDataHolder[] newArray(int size) {
            return new HistoryRecyclerViewDataHolder[size];
        }
    };

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setRmssd(int rmssd) {
        this.rmssd = rmssd;
    }

    public void setLn_rmssd(float ln_rmssd) {
        this.ln_rmssd = ln_rmssd;
    }

    public void setLowest_rmssd(float lowest_rmssd) {
        this.lowest_rmssd = lowest_rmssd;
    }

    public void setHighest_rmssd(float highest_rmssd) {
        this.highest_rmssd = highest_rmssd;
    }

    public void setLowest_bpm(float lowest_bpm) {
        this.lowest_bpm = lowest_bpm;
    }

    public void setHighest_bpm(float highest_bpm) {
        this.highest_bpm = highest_bpm;
    }

    public void setAverage_bpm(float average_bpm) {
        this.average_bpm = average_bpm;
    }

    public void setLF_band(float LF_band) {
        this.LF_band = LF_band;
    }

    public void setVLF_band(float VLF_band) {
        this.VLF_band = VLF_band;
    }

    public void setVHF_band(float VHF_band) {
        this.VHF_band = VHF_band;
    }

    public void setHF_band(float HF_band) {
        this.HF_band = HF_band;
    }

    public void setBpm_data(int[] bpm_data) {
        this.bpm_data = bpm_data;
    }

    public void setRmssd_data(int[] rmssd_data) {
        this.rmssd_data = rmssd_data;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public void setHrv(int hrv) {
        this.hrv = hrv;
    }

    public void setUnique_id(int unique_id) {
        this.unique_id = unique_id;
    }

    public void setWorkout_duration(float workout_duration) {
        this.workout_duration = workout_duration;
    }

    public void setPace_data(float[] pace_data) {
        this.pace_data = pace_data;
    }

    public void setRoute(LatLng[] route) {
        this.route = route;
    }

    public void setCalories_burned(float calories_burned) {
        this.calories_burned = calories_burned;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public int getUnique_id() {
        return unique_id;
    }

    public float getWorkout_duration() {
        return workout_duration;
    }

    public float[] getPace_data() {
        return pace_data;
    }

    public LatLng[] getRoute() {
        return route;
    }

    public float getCalories_burned() {
        return calories_burned;
    }

    public float getDistance() {
        return distance;
    }

    public Date getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }

    public int getRmssd() {
        return rmssd;
    }

    public float getLn_rmssd() {
        return ln_rmssd;
    }

    public float getLowest_rmssd() {
        return lowest_rmssd;
    }

    public float getHighest_rmssd() {
        return highest_rmssd;
    }

    public float getLowest_bpm() {
        return lowest_bpm;
    }

    public float getHighest_bpm() {
        return highest_bpm;
    }

    public float getAverage_bpm() {
        return average_bpm;
    }

    public float getLF_band() {
        return LF_band;
    }

    public float getVLF_band() {
        return VLF_band;
    }

    public float getVHF_band() {
        return VHF_band;
    }

    public float getHF_band() {
        return HF_band;
    }

    public int[] getBpm_data() {
        return bpm_data;
    }

    public int[] getRmssd_data() {
        return rmssd_data;
    }

    public int getMood() {
        return mood;
    }

    public int getHrv() {
        return hrv;
    }

    public Exercise getExercise() {
        return exercise;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(duration);
        parcel.writeInt(rmssd);
        parcel.writeFloat(ln_rmssd);
        parcel.writeFloat(lowest_rmssd);
        parcel.writeFloat(highest_rmssd);
        parcel.writeFloat(lowest_bpm);
        parcel.writeFloat(highest_bpm);
        parcel.writeFloat(average_bpm);
        parcel.writeFloat(LF_band);
        parcel.writeFloat(VLF_band);
        parcel.writeFloat(VHF_band);
        parcel.writeFloat(HF_band);
        parcel.writeIntArray(bpm_data);
        parcel.writeIntArray(rmssd_data);
        parcel.writeInt(mood);
        parcel.writeInt(hrv);
        parcel.writeInt(unique_id);
        parcel.writeFloat(workout_duration);
        parcel.writeFloatArray(pace_data);
        parcel.writeTypedArray(route, i);
        parcel.writeFloat(calories_burned);
        parcel.writeFloat(distance);
        parcel.writeInt(viewType);
        parcel.writeSerializable(date);
        parcel.writeParcelable(exercise, i);
    }
}
