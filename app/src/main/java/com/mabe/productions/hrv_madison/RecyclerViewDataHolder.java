package com.mabe.productions.hrv_madison;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by Benas on 7/31/2018.
 */

public class RecyclerViewDataHolder {


    /*
    average_bpm, bpm_data and date is used for both measurements and workouts
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
    private int pulse_zone;
    private float distance;


    private int viewType = 0;



    public RecyclerViewDataHolder(int unique_id, Date date, float workout_duration, float average_bpm, int[] bpm_data, float[] pace_data, LatLng[] route, float calories_burned, int pulse_zone, float distance, int viewType) {
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
        this.viewType = viewType;
    }

    public RecyclerViewDataHolder(Date date, int duration, int rmssd, float ln_rmssd, float lowest_rmssd, float highest_rmssd, float lowest_bpm, float highest_bpm, float average_bpm, float LF_band, float VLF_band, float VHF_band, float HF_band, int[] bpm_data, int[] rmssd_data, int uniqueId, int mood, int hrv, int viewType) {
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

    public int getPulse_zone() {
        return pulse_zone;
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








}
