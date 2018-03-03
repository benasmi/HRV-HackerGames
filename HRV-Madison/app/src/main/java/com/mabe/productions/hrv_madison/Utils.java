package com.mabe.productions.hrv_madison;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Benas on 1/30/2018.
 */

public class Utils {

    public static void changeNotifBarColor(String color, Window window){

        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor(color));
        }

    }

    public static void changeNotifBarColor(int color, Window window){

        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }

    }




    public static boolean isBluetoothEnabled(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            return true;
        }
    }

    //TODO: fix api level errors
    //By the way, not sure if this works
    public static int getAgeFromDate(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);

        return  Calendar.getInstance().get(Calendar.YEAR) - year;
    }

    //TODO: fix api level errors
    public static Date getDateFromString(String date){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return format.parse(date);
            //calculate the difference in days here
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO: fix api level errors
    public static String getStringFromDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(date);
    }

    public static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }


    public static boolean[] readFromSharedPrefs_boolarray(Context context, String holder){
        SharedPreferences prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        int size = prefs.getInt(holder + "_size", 0);
        boolean array[] = new boolean[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getBoolean(holder + "_" + i, false);
        return array;
    }

    //TODO: save shared preference name user_data to a final static field
    public static void saveToSharedPrefs(Context context, String holder, boolean[] array) {
        SharedPreferences prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(holder +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putBoolean(holder + "_" + i, array[i]);
        editor.commit();
    }

    public static void saveToSharedPrefs(Context context, String holder, String value){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("user_data",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(holder, value).commit();
    }

    public static void saveToSharedPrefs(Context context, String holder, boolean value){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("user_data",Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(holder, value).commit();

    }

    public static void saveToSharedPrefs(Context context, String holder, int value){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("user_data",Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(holder, value).commit();

    }

    public static void saveToSharedPrefs(Context context, String holder, float value){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("user_data",Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(holder, value).commit();

    }

    public static String readFromSharedPrefs_string(Context context, String holder){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        return sharedPreferences.getString(holder, "");
    }

    public static int readFromSharedPrefs_int(Context context, String holder){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        return sharedPreferences.getInt(holder, 0);
    }
    public static boolean readFromSharedPrefs_bool(Context context, String holder){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(holder, true);
    }
    public static float readFromSharedPrefs_float(Context context, String holder){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data",Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(holder, 0f);
    }

    public static void Vibrate(Context context, long duration){
        final Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.vibrate(duration);
    }



}
