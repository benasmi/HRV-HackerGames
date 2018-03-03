package com.mabe.productions.hrv_madison.measurements;

import java.util.ArrayList;

/**
 * Created by Benas on 2/15/2018.
 */

public class RMSSD {

    ArrayList<Integer> rrList = new ArrayList<>();

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

        return rmssd;
    }


    public void addIntervals(int [] intervals){

            for(int i = 0; i<intervals.length; i++){
                rrList.add(intervals[i]);
            }
    }


}
