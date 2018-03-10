package com.mabe.productions.hrv_madison.measurements;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Martynas on 2018-02-28.
 */

public class Measurement {


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

    public Measurement(Date date, int rmssd, float ln_rmssd, float lowest_rmssd, float highest_rmssd, float lowest_bpm, float highest_bpm, float average_bpm, float LF_band, float VLF_band, float VHF_band, float HF_band, int[] bpm_data, int[] rmssd_data, int duration) {
        this.date = date;
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
        this.duration = duration;
    }

    public Measurement(RMSSD rmssd, FrequencyMethod frequencies, BPM bpm, int duration){
        this.rmssd = rmssd.getRmssd();
        this.ln_rmssd = rmssd.getLnRmssd();
        this.date = Calendar.getInstance().getTime();
        this.lowest_rmssd = rmssd.getLowestRmssd();
        this.highest_rmssd = rmssd.getHighestRmssd();
        this.lowest_bpm = bpm.getLowestBpm();
        this.highest_bpm = bpm.getHighestBpm();
        this.average_bpm = bpm.getAverageBpm();
        this.bpm_data = bpm.getBpmValues();
        this.LF_band = (float) frequencies.getLF_value();
        this.VLF_band = (float) frequencies.getVLF_value();
        this.VHF_band = (float) frequencies.getVHF_value();
        this.HF_band = (float) frequencies.getHF_value();
        this.bpm_data = bpm.getBpmValues();
        this.rmssd_data = rmssd.getRMSSDValues();
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public Date getDate() {
        return date;
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
}
