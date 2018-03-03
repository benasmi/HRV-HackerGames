package com.mabe.productions.hrv_madison;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

/**
 * Created by Martynas on 2018-02-19.
 */

public class Jogging {


    public double baseTime;
    public float pulse_zone;

 /*   public void generateInitialProgram(double baseTime, int pulseZone, Context context){
        this.baseTime = baseTime;
        pulse_zone = pulseZone;

        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.COL_JOG_DISTANCE, pulse_zone);
        values.put(FeedReaderDbHelper.COL_JOG_TIME, baseTime);

        db.insert(FeedReaderDbHelper.JOGGING_TABLE_NAME, null, values);
    }
*/
   /* public void upgradeExistingProgram(Context context){
        baseTime*=1.1;
        pulse_zone = baseTime * NORMAL_JOGGING_SPEED;
        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.COL_JOG_DISTANCE, pulse_zone);
        values.put(FeedReaderDbHelper.COL_JOG_TIME, baseTime);

        db.insert(FeedReaderDbHelper.JOGGING_TABLE_NAME, null, values);
    }*/
}
