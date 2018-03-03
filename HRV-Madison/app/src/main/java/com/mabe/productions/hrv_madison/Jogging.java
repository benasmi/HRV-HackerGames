package com.mabe.productions.hrv_madison;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

/**
 * Created by Martynas on 2018-02-19.
 */

public class Jogging {

    public final static float NORMAL_JOGGING_SPEED = 5f;

    public float baseTime;
    public float distance;

    public void generateInitialProgram(float baseTime, Context context){
        this.baseTime = baseTime;
        distance = NORMAL_JOGGING_SPEED*baseTime;

        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.COL_JOG_DISTANCE, distance);
        values.put(FeedReaderDbHelper.COL_JOG_TIME, baseTime);

        db.insert(FeedReaderDbHelper.JOGGING_TABLE_NAME, null, values);
    }

    public void upgradeExistingProgram(Context context){
        baseTime*=1.1;
        distance = baseTime * NORMAL_JOGGING_SPEED;
        SQLiteDatabase db = new FeedReaderDbHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.COL_JOG_DISTANCE, distance);
        values.put(FeedReaderDbHelper.COL_JOG_TIME, baseTime);

        db.insert(FeedReaderDbHelper.JOGGING_TABLE_NAME, null, values);
    }
}
