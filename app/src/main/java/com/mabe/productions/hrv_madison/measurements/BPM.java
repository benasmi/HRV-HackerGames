package com.mabe.productions.hrv_madison.measurements;

import java.util.ArrayList;

/**
 * Created by Martynas on 2018-02-27.
 */



public class BPM {

    private ArrayList<Integer> bpmList = new ArrayList<>();

    public void clear(){
        bpmList.clear();
    }

    public void addBPM(int bpm){
        bpmList.add(bpm);
    }

    public float getAverageBpm(){
        int sum = 0;
        for(int bpm : bpmList){
            sum+=bpm;
        }
        return (float) sum / (float)bpmList.size();
    }

    public int getLowestBpm(){
        int lowest_bpm = bpmList.get(0);

        for(int bpm : bpmList){
            lowest_bpm = bpm < lowest_bpm ? bpm : lowest_bpm;
        }

        return lowest_bpm;
    }

    public int getHighestBpm(){
        int highest_bpm = bpmList.get(0);

        for(int bpm : bpmList){
            highest_bpm = bpm > highest_bpm ? bpm : highest_bpm;
        }

        return highest_bpm;
    }

    public int[] getBpmValues(){
        int[] values = new int[bpmList.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = bpmList.get(i);
        }
        return values;
    }

}
