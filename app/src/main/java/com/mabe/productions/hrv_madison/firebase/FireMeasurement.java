package com.mabe.productions.hrv_madison.firebase;

import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.measurements.Measurement;

public class FireMeasurement {

    private String date;
    private int duration;
    private int rmssd;
    private float ln_rmssd;
    private float lowest_rmssd;
    private float highest_rmssd;
    private float lowest_bpm;
    private float highest_bpm;
    private float average_bpm;
    private float lf_band;
    private float vlf_band;
    private float vhf_band;
    private float hf_band;
    private String bpm_data;
    private String rmssd_data;
    private int mood;
    private int hrv;

    public FireMeasurement() {

    }

    public FireMeasurement(Measurement measurement) {
        this.date = Utils.getStringFromDate(measurement.getDate());
        this.duration = measurement.getDuration();
        this.rmssd = measurement.getRmssd();
        this.ln_rmssd = measurement.getLn_rmssd();
        this.lowest_rmssd = measurement.getLowest_rmssd();
        this.highest_rmssd = measurement.getHighest_rmssd();
        this.lowest_bpm = measurement.getLowest_bpm();
        this.highest_bpm = measurement.getHighest_bpm();
        this.average_bpm = measurement.getAverage_bpm();
        this.lf_band = measurement.getLF_band();
        this.vlf_band = measurement.getVLF_band();
        this.vhf_band = measurement.getVHF_band();
        this.hf_band = measurement.getHF_band();
        this.bpm_data = FeedReaderDbHelper.intArrayToString(measurement.getBpm_data());
        this.rmssd_data = FeedReaderDbHelper.intArrayToString(measurement.getRmssd_data());
        this.mood = measurement.getMood();
        this.hrv = measurement.getHrv();
    }

    public FireMeasurement(String date, int duration, int rmssd, float ln_rmssd, float lowest_rmssd, float highest_rmssd, float lowest_bpm, float highest_bpm, float average_bpm, float LF_band, float VLF_band, float VHF_band, float hf_band, String bpm_data, String rmssd_data, int uniqueId, int mood, int hrv) {
        this.date = date;
        this.duration = duration;
        this.rmssd = rmssd;
        this.ln_rmssd = ln_rmssd;
        this.lowest_rmssd = lowest_rmssd;
        this.highest_rmssd = highest_rmssd;
        this.lowest_bpm = lowest_bpm;
        this.highest_bpm = highest_bpm;
        this.average_bpm = average_bpm;
        this.lf_band = LF_band;
        this.vlf_band = VLF_band;
        this.vhf_band = VHF_band;
        this.hf_band = hf_band;
        this.bpm_data = bpm_data;
        this.rmssd_data = rmssd_data;
        this.mood = mood;
        this.hrv = hrv;
    }

    public String getDate() {
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

    public float getLf_band() {
        return lf_band;
    }

    public float getVlf_band() {
        return vlf_band;
    }

    public float getVhf_band() {
        return vhf_band;
    }

    public float gethf_band() {
        return hf_band;
    }

    public String getBpm_data() {
        return bpm_data;
    }

    public String getRmssd_data() {
        return rmssd_data;
    }

    public int getMood() {
        return mood;
    }

    public int getHrv() {
        return hrv;
    }
}
