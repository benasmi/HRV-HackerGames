package com.mabe.productions.hrv_madison.measurements;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Benas on 2/15/2018.
 */

public class RMSSD {

    ArrayList<Integer> rrList = new ArrayList<>();
    ArrayList<Integer> rmssdList = new ArrayList<>();

    public int calculateRMSSD() {

        int rmssd=0;
        int rrSum = 0;
        int inverse = 0;
        int i = 0;
        int tempSum = 0;
        if (rrList.size() >= 2) {


            for ( i = 2 ; i < rrList.size(); i++) {

                rrSum = (rrList.get(i-1) - rrList.get(i));
                rrSum = rrSum * rrSum;
                tempSum = tempSum + rrSum;
            }

            inverse = tempSum / (rrList.size() - 1);
            rmssd = (int) Math.sqrt(inverse);
            Math.log(rmssd);
        }

        rmssdList.add(rmssd);
        return rmssd;
    }

    public int getRmssd(){
        return rmssdList.get(rmssdList.size()-1);
    }



    public void clear(){
        rmssdList.clear();
        rrList.clear();
    }


    public void addIntervals(int [] intervals){

            for(int i = 0; i<intervals.length; i++){
                rrList.add(intervals[i]);
            }
    }
    public void addInterval(int interval){
            rrList.add(interval);
    }

    public int getLowestRmssd(){
        int lowest_rmssd = rmssdList.get(0);

        for(int rmssd : rmssdList){
            lowest_rmssd = rmssd < lowest_rmssd ? rmssd : lowest_rmssd;
        }

        return lowest_rmssd;
    }

    public float getLnRmssd(){

        return (float) Math.log(rmssdList.get(rmssdList.size()-1));
    }

    public int getPURE_HRV(){

        int HRV = (int) (getLnRmssd()*15.5f);
        if(HRV<=0){
            return 0;
        }
        return HRV;
    }

    public int getHighestRmssd(){
        int highest_rmssd = rmssdList.get(0);

        for(int rmssd : rmssdList){
            highest_rmssd = rmssd > highest_rmssd ? rmssd : highest_rmssd;
        }

        return highest_rmssd;
    }

    public int[] getRMSSDValues(){
        int[] values = new int[rmssdList.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = rmssdList.get(i);
        }
        return values;
    }

}
