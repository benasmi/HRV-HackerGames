package com.mabe.productions.hrv_madison;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;


import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

import java.text.ParseException;
import java.util.Date;
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


    /*
     * Builds an alert dialog with a yes/no decision.
     * If a given listener is null, a default listener, which dismisses the dialog on press, is used.
     */
    public static void buildAlertDialogPrompt(Context context, String title, String message, @Nullable DialogInterface.OnClickListener positiveButtonListener, @Nullable DialogInterface.OnClickListener negativeButtonListener){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.AppThemeDialog);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.measure, positiveButtonListener != null ? positiveButtonListener : new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, negativeButtonListener != null ? negativeButtonListener : new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public static boolean isBluetoothEnabled(){
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }



    public static int getAgeFromDate(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);

        return  Calendar.getInstance().get(Calendar.YEAR) - year;
    }

    public static Date getDateFromString(String date){
        SimpleDateFormat format = new SimpleDateFormat(FeedReaderDbHelper.DATE_FORMAT);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getStringFromDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat(FeedReaderDbHelper.DATE_FORMAT);
        return format.format(date);
    }

    public static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    public static int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }


        return age;
    }


    public static boolean[] readFromSharedPrefs_boolarray(Context context, String holder, String sharedPref){
        SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        int size = prefs.getInt(holder + "_size", 0);
        boolean array[] = new boolean[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getBoolean(holder + "_" + i, false);
        return array;
    }

    public static void saveToSharedPrefs(Context context, String holder, boolean[] array, String sharedPref) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(holder +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putBoolean(holder + "_" + i, array[i]);
        editor.commit();
    }

    public static void saveToSharedPrefs(Context context, String holder, String value, String sharedPref){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(holder, value).commit();
    }

    public static void saveToSharedPrefs(Context context, String holder, boolean value,String sharedPref){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(holder, value).commit();

    }

    public static void saveToSharedPrefs(Context context, String holder, int value,String sharedPref){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(holder, value).commit();

    }

    public static void saveToSharedPrefs(Context context, String holder, float value, String sharedPref){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(holder, value).commit();

    }

    public static String readFromSharedPrefs_string(Context context, String holder, String sharedPref){
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        return sharedPreferences.getString(holder, "");
    }

    public static int readFromSharedPrefs_int(Context context, String holder, String sharedPref){
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(holder, 0);
    }
    public static boolean readFromSharedPrefs_bool(Context context, String holder, String sharedPref){
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(holder, false);
    }
    public static float readFromSharedPrefs_float(Context context, String holder, String sharedPref){
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(holder, 0f);
    }

    public static void Vibrate(Context context, long duration){
        final Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.vibrate(duration);
    }



}
