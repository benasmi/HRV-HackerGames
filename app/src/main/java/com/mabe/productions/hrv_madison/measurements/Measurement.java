package com.mabe.productions.hrv_madison.measurements;

import android.content.ContentValues;
import android.support.annotation.Nullable;

import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebase.FireMeasurement;

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
    private int uniqueId;
    private int mood;
    private int hrv;
    private String remoteDbId;

    public Measurement(Date date, int rmssd, float ln_rmssd, float lowest_rmssd, float highest_rmssd, float lowest_bpm, float highest_bpm, float average_bpm, float LF_band, float VLF_band, float VHF_band, float HF_band, int[] bpm_data, int[] rmssd_data, int duration, @Nullable int uniqueId, int mood, int hrv, String remoteDbId) {
        this.date          = date;
        this.rmssd         = rmssd;
        this.ln_rmssd      = ln_rmssd;
        this.lowest_rmssd  = lowest_rmssd;
        this.highest_rmssd = highest_rmssd;
        this.lowest_bpm    = lowest_bpm;
        this.highest_bpm   = highest_bpm;
        this.average_bpm   = average_bpm;
        this.LF_band       = LF_band;
        this.VLF_band      = VLF_band;
        this.VHF_band      = VHF_band;
        this.HF_band       = HF_band;
        this.bpm_data      = bpm_data;
        this.rmssd_data    = rmssd_data;
        this.duration      = duration;
        this.uniqueId      = uniqueId;
        this.mood          = mood;
        this.hrv           = hrv;
        this.remoteDbId    = remoteDbId;
    }

    public Measurement(RMSSD rmssd, FrequencyMethod frequencies, BPM bpm, int duration, Date date){
        this.rmssd = rmssd.getRmssd();
        this.hrv = rmssd.getPURE_HRV();
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
        this.rmssd_data = rmssd.getRMSSDValues();
        this.duration = duration;
        this.date = date;
    }

    public Measurement(FireMeasurement fireMeasurement){
        this.rmssd = fireMeasurement.getRmssd();
        this.hrv = fireMeasurement.getHrv();
        this.ln_rmssd = fireMeasurement.getLn_rmssd();
        this.date = Utils.getDateFromString(fireMeasurement.getDate());
        this.lowest_rmssd = fireMeasurement.getLowest_rmssd();
        this.highest_rmssd = fireMeasurement.getHighest_rmssd();
        this.lowest_bpm = fireMeasurement.getLowest_bpm();
        this.highest_bpm = fireMeasurement.getHighest_bpm();
        this.average_bpm = fireMeasurement.getAverage_bpm();
        this.bpm_data = FeedReaderDbHelper.getIntArrayFromString(fireMeasurement.getBpm_data());
        this.LF_band = fireMeasurement.getLf_band();
        this.VLF_band = fireMeasurement.getVlf_band();
        this.VHF_band = fireMeasurement.getVhf_band();
        this.HF_band = fireMeasurement.gethf_band();
        this.rmssd_data = FeedReaderDbHelper.getIntArrayFromString(fireMeasurement.getRmssd_data());
        this.duration = fireMeasurement.getDuration();
        this.mood = fireMeasurement.getMood();
    }



    public int getHrv() {
        return hrv;
    }

    public void setHrv(int hrv) {
        this.hrv = hrv;
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

    public void setMood(final int mood){
        this.mood = mood;
    }
    public int getMood(){
        return mood;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public String getRemoteDbId() {
        return remoteDbId;
    }

    public void setRemoteDbId(String remoteDbId) {
        this.remoteDbId = remoteDbId;
    }

    /*
    * Just a useful method, that fills in all ContentValues from a given Measurement instance
    */
    public ContentValues getContentValues(){

        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.HRV_COL_RMSSD, getRmssd());
        values.put(FeedReaderDbHelper.HRV_COL_LN_RMSSD, getLn_rmssd());
        values.put(FeedReaderDbHelper.HRV_COL_LOWEST_RMSSD, getLowest_rmssd());
        values.put(FeedReaderDbHelper.HRV_COL_HIGHEST_RMSSD, getHighest_rmssd());
        values.put(FeedReaderDbHelper.HRV_COL_RMSSD_DATA, FeedReaderDbHelper.intArrayToString(getRmssd_data()));
        values.put(FeedReaderDbHelper.HRV_COL_LOWEST_BPM, getLowest_bpm());
        values.put(FeedReaderDbHelper.HRV_COL_HIGHEST_BPM, getHighest_bpm());
        values.put(FeedReaderDbHelper.HRV_COL_AVERAGE_BPM, getAverage_bpm());
        values.put(FeedReaderDbHelper.HRV_COL_BPM_DATA, FeedReaderDbHelper.intArrayToString(getBpm_data()));
        values.put(FeedReaderDbHelper.HRV_COL_MEASUREMENT_DURATION, getDuration());
        values.put(FeedReaderDbHelper.HRV_COL_LF_BAND, getLF_band());
        values.put(FeedReaderDbHelper.HRV_COL_HF_BAND, getHF_band());
        values.put(FeedReaderDbHelper.HRV_COL_VLF_BAND, getVLF_band());
        values.put(FeedReaderDbHelper.HRV_COL_VHF_BAND, getVHF_band());
        values.put(FeedReaderDbHelper.HRV_COL_MOOD, getMood());
        values.put(FeedReaderDbHelper.HRV_COL_HRV, getHrv());
        values.put(FeedReaderDbHelper.HRV_COL_DATE, Utils.getStringFromDate(getDate()));
        values.put(FeedReaderDbHelper.HRV_COL_REMOTE_DB_KEY, getRemoteDbId());

        return values;
    }
}
