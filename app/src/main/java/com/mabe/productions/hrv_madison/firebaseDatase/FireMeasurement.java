package com.mabe.productions.hrv_madison.firebaseDatase;

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
    private float LF_band;
    private float VLF_band;
    private float VHF_band;
    private float HF_band;
    private String bpm_data;
    private String rmssd_data;
    private int mood;
    private int hrv;

    public FireMeasurement(){

    }

    public FireMeasurement(Measurement measurement){
        this.date = Utils.getStringFromDate(measurement.getDate());
        this.duration = measurement.getDuration();
        this.rmssd = measurement.getRmssd();
        this.ln_rmssd = measurement.getLn_rmssd();
        this.lowest_rmssd = measurement.getLowest_rmssd();
        this.highest_rmssd = measurement.getHighest_rmssd();
        this.lowest_bpm = measurement.getLowest_bpm();
        this.highest_bpm = measurement.getHighest_bpm();
        this.average_bpm = measurement.getAverage_bpm();
        this.LF_band = measurement.getLF_band();
        this.VLF_band = measurement.getVLF_band();
        this.VHF_band = measurement.getVHF_band();
        this.HF_band = measurement.getHF_band();
        this.bpm_data = FeedReaderDbHelper.intArrayToString(measurement.getBpm_data());
        this.rmssd_data = FeedReaderDbHelper.intArrayToString(measurement.getRmssd_data());
        this.mood = measurement.getMood();
        this.hrv = measurement.getHrv();
    }

    public FireMeasurement(String date, int duration, int rmssd, float ln_rmssd, float lowest_rmssd, float highest_rmssd, float lowest_bpm, float highest_bpm, float average_bpm, float LF_band, float VLF_band, float VHF_band, float HF_band, int[] bpm_data, int[] rmssd_data, int uniqueId, int mood, int hrv) {
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
        this.bpm_data = FeedReaderDbHelper.intArrayToString(bpm_data);
        this.rmssd_data = FeedReaderDbHelper.intArrayToString(rmssd_data);
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

    public float getLnRmssd() {
        return ln_rmssd;
    }

    public float getLowestRmssd() {
        return lowest_rmssd;
    }

    public float getHighestRmssd() {
        return highest_rmssd;
    }

    public float getLowestBpm() {
        return lowest_bpm;
    }

    public float getHighestBpm() {
        return highest_bpm;
    }

    public float getAverageBpm() {
        return average_bpm;
    }

    public float getLFBand() {
        return LF_band;
    }

    public float getVLFBand() {
        return VLF_band;
    }

    public float getVHFBand() {
        return VHF_band;
    }

    public float getHFBand() {
        return HF_band;
    }

    public String getBpmData() {
        return bpm_data;
    }

    public String getRmssdData() {
        return rmssd_data;
    }

    public int getMood() {
        return mood;
    }

    public int getHrv() {
        return hrv;
    }
}
