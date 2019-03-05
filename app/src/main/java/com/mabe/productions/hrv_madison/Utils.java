package com.mabe.productions.hrv_madison;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.location.LocationManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


import com.google.android.gms.maps.model.LatLng;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.fragments.ViewPagerAdapter;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utils {


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;



    public static void speak(String text) {
        if (MainScreenActivity.isTTSAvailable && MainScreenActivity.textToSpeech != null) {
            MainScreenActivity.textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    /**
     * Returns the heart rate bounds of given pulse zone range
     *
     * @param required_pulse_zones Pulse zone range using the following format: int[]{min_pulse_zone, max_pulse_zone}
     * @param hr_max               The user's maximum heart rate.
     * @return Pulse zone bounds using the following format: int[]{min_pulse_zone, max_pulse_zone}
     */
    public static int[] getPulseZoneBounds(int[] required_pulse_zones, float hr_max) {

        int min_pulse = 0;
        int max_pulse = 0;

        int lowest_pulse_zone = Utils.minNum(required_pulse_zones);
        int highest_pulse_zone = Utils.maxNum(required_pulse_zones);

        //Setting hrMax
        if (highest_pulse_zone == 1) {
            max_pulse = (int) (hr_max * 0.6f);
        } else if (highest_pulse_zone == 2) {
            max_pulse = (int) (hr_max * 0.7f);
        } else if (highest_pulse_zone == 3) {
            max_pulse = (int) (hr_max * 0.8f);
        } else if (highest_pulse_zone == 4) {
            max_pulse = (int) (hr_max * 0.9f);
        } else if (highest_pulse_zone == 5) {
            max_pulse = (int) (hr_max * 1f);
        }
        //Setting hrMin
        if (lowest_pulse_zone == 1) {
            min_pulse = (int) (hr_max * 0.5f);
        } else if (lowest_pulse_zone == 2) {
            min_pulse = (int) (hr_max * 0.6f);
        } else if (lowest_pulse_zone == 3) {
            min_pulse = (int) (hr_max * 0.7f);
        } else if (lowest_pulse_zone == 4) {
            min_pulse = (int) (hr_max * 0.8f);
        } else if (lowest_pulse_zone == 5) {
            min_pulse = (int) (hr_max * 0.9f);
        }

        return new int[]{min_pulse, max_pulse};

    }

    /**
     * Returns the heart rate bounds of given pulse zone
     *
     * @param required_pulse_zone The target pulse zone
     * @param hr_max              The user's maximum heart rate.
     * @return Pulse zone bounds using the following format: int[]{min_pulse_zone, max_pulse_zone}
     */
    public static int[] getPulseZoneBounds(int required_pulse_zone, float hr_max) {
        return getPulseZoneBounds(new int[]{required_pulse_zone, required_pulse_zone}, hr_max);
    }


    /**
     * Gets the integer number suffix
     *
     * @param number Number to get the suffix of
     * @return suffix of the number
     */
    public static String getNumberSuffix(int number) {

        //Hardcoded because of how weird launguages are
        if (number == 11 || number == 12 || number == 13) {
            return "th";
        }

        String numberSuffix = "th";
        String numString = String.valueOf(number);
        int lastNum = Integer.valueOf(numString.substring(numString.length() - 1, numString.length()));
        if (lastNum == 1) {
            numberSuffix = "st";
        }
        if (lastNum == 2) {
            numberSuffix = "nd";
        }
        if (lastNum == 3) {
            numberSuffix = "rd";
        }
        return numberSuffix;
    }

    public static void changeNotifBarColor(String color, Window window) {

        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor(color));
        }

    }


    /**
     * Gets the day of week
     *
     * @return returns an index of the day of the week, where monday is 0 and sunday is 6
     */
    public static int getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.MONDAY) {
            return 0;
        } else if (dayOfWeek == Calendar.TUESDAY) {
            return 1;
        } else if (dayOfWeek == Calendar.WEDNESDAY) {
            return 2;
        } else if (dayOfWeek == Calendar.THURSDAY) {
            return 3;
        } else if (dayOfWeek == Calendar.FRIDAY) {
            return 4;
        } else if (dayOfWeek == Calendar.SATURDAY) {
            return 5;
        } else if (dayOfWeek == Calendar.SUNDAY) {
            return 6;
        }
        return -1;
    }

    public static void changeNotifBarColor(int color, Window window) {

        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }

    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    /**
     * Builds an alert dialog with a yes/no decision.
     * Builds an alert dialog with a yes/no decision.
     * If a given listener is null, a default listener, which dismisses the dialog on press, is used.
     */
    public static void buildAlertDialogPrompt(Context context, String title, String message, String positiveButtonText, String negativeButtonText, @Nullable DialogInterface.OnClickListener positiveButtonListener, @Nullable DialogInterface.OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.AppThemeDialog);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveButtonListener != null ? positiveButtonListener : new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(negativeButtonText, negativeButtonListener != null ? negativeButtonListener : new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public static boolean isBluetoothEnabled() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }


    public static final boolean isDeviceInPowerSavingMode(Context context) {
        PowerManager powerManager = (PowerManager)
                context.getSystemService(Context.POWER_SERVICE);

        return (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                && powerManager.isPowerSaveMode();
    }


    public static int getAgeFromDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);

        return Calendar.getInstance().get(Calendar.YEAR) - year;
    }

    public static Date getDateFromString(String date) {
        SimpleDateFormat format = new SimpleDateFormat(FeedReaderDbHelper.DATE_FORMAT);

        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }


    public static String getStringFromDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(FeedReaderDbHelper.DATE_FORMAT);
        return format.format(date);
    }

    public static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    public static int getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }


        return age;
    }


    public static boolean[] readFromSharedPrefs_boolarray(Context context, String holder, String sharedPref) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        int size = prefs.getInt(holder + "_size", 0);
        boolean array[] = new boolean[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getBoolean(holder + "_" + i, false);
        return array;
    }

    public static int[] readFromSharedPrefs_intarray(Context context, String holder, String sharedPref) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        int size = prefs.getInt(holder + "_size", 0);
        int array[] = new int[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getInt(holder + "_" + i, 0);
        return array;
    }

    public static long[] readFromSharedPrefs_longarray(Context context, String holder, String sharedPref) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        int size = prefs.getInt(holder + "_size", 0);
        long array[] = new long[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getLong(holder + "_" + i, 0);
        return array;
    }

    public static void saveToSharedPrefs(Context context, String holder, boolean[] array, String sharedPref) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(holder + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putBoolean(holder + "_" + i, array[i]);
        editor.commit();
    }

    public static void saveToSharedPrefs(Context context, String holder, int[] array, String sharedPref) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(holder + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putInt(holder + "_" + i, array[i]);
        editor.commit();
    }

    public static void saveToSharedPrefs(Context context, String holder, long[] array, String sharedPref) {
        SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(holder + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putLong(holder + "_" + i, array[i]);
        editor.commit();
    }

    /**
     * Returns a number of weeks between two given dates.
     *
     * @param date1 The first date
     * @param date2 The second date
     * @return A number of weeks between the two given dates
     */
    public static int weekDifference(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        long milliSeconds1 = calendar.getTimeInMillis();
        calendar.setTime(date2);
        long milliSeconds2 = calendar.getTimeInMillis();
        long periodSeconds = Math.abs((milliSeconds2 - milliSeconds1)) / 1000;
        long elapsedDays = periodSeconds / 60 / 60 / 24;

        return (int) (elapsedDays / 7);
    }

    public static int calendoricWeekDifference(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int month1 = calendar.get(Calendar.MONTH);
        int day1 = calendar.get(Calendar.DATE) - getDayOfWeek(calendar);
        int year1 = calendar.get(Calendar.YEAR);
        calendar.setTime(date2);
        int month2 = calendar.get(Calendar.MONTH);
        int day2 = calendar.get(Calendar.DATE) - getDayOfWeek(calendar);
        int year2 = calendar.get(Calendar.YEAR);

        calendar.set(year1, month1, day1);
        Date first = calendar.getTime();

        calendar.set(year2, month2, day2);
        Date second = calendar.getTime();

        return weekDifference(first, second);
    }

    /**
     * Returns a number of days between two dates
     *
     * @param date1 The first date
     * @param date2 The second date
     * @return A number of days between the given dates
     */
    public static int dayDifference(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        long msDiff = Math.abs(cal1.getTimeInMillis() - cal2.getTimeInMillis());
        int daysDiff = (int) TimeUnit.MILLISECONDS.toDays(msDiff);

        return daysDiff;
    }


    public static int calendoricDayDifference(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date1);
        int year1 = cal.get(Calendar.YEAR);
        int month1 = cal.get(Calendar.MONTH);
        int day1 = cal.get(Calendar.DATE);

        cal.setTime(date2);
        int year2 = cal.get(Calendar.YEAR);
        int month2 = cal.get(Calendar.MONTH);
        int day2 = cal.get(Calendar.DATE);

        cal.set(year1, month1, day1);
        Date firstDate = cal.getTime();

        cal.set(year2, month2, day2);
        Date secondDate = cal.getTime();

        return Utils.dayDifference(firstDate, secondDate);
    }

    public static void saveToSharedPrefs(Context context, String holder, String value, String sharedPref) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(holder, value).commit();
    }

    public static void saveToSharedPrefs(Context context, String holder, boolean value, String sharedPref) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(holder, value).commit();

    }

    public static void saveToSharedPrefs(Context context, String holder, int value, String sharedPref) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(holder, value).commit();

    }

    public static void saveToSharedPrefs(Context context, String holder, float value, String sharedPref) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(holder, value).commit();

    }

    public static String readFromSharedPrefs_string(Context context, String holder, String sharedPref) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        return sharedPreferences.getString(holder, "");
    }

    public static int readFromSharedPrefs_int(Context context, String holder, String sharedPref) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(holder, 0);
    }

    public static boolean readFromSharedPrefs_bool(Context context, String holder, String sharedPref) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(holder, false);
    }

    public static float readFromSharedPrefs_float(Context context, String holder, String sharedPref) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(holder, 0f);
    }

    public static void vibrate(Context context, long duration) {
        final Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.vibrate(duration);
    }

    public static int[] convertIntArrayListToArray(ArrayList<Integer> arrayList) {
        int[] newArray = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            newArray[i] = arrayList.get(i);
        }
        return newArray;
    }


    public static float[] convertFloatArrayListToArray(ArrayList<Float> arrayList) {
        float[] newArray = new float[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            newArray[i] = arrayList.get(i);
        }
        return newArray;
    }

    public static LatLng[] convertLatLngArrayListToArray(ArrayList<LatLng> arrayList) {
        LatLng[] newArray = new LatLng[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            newArray[i] = arrayList.get(i);
        }
        return newArray;
    }

    public static void setViewpagerTab(Activity activity, int itemIndex) {
        ViewPager parentViewPager = activity.findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = (ViewPagerAdapter) parentViewPager.getAdapter();
        parentViewPager.setCurrentItem(itemIndex);

    }


    public static boolean isGPSEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean intArrayContains(int[] array, int num) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == num) {
                return true;
            }
        }
        return false;
    }

    public static int maxNum(int[] array) {
        int maxX = array[0];
        for (int x : array) {
            if (x > maxX) {
                maxX = x;
            }
        }
        return maxX;
    }

    public static int minNum(int[] array) {
        int minX = array[0];
        for (int x : array) {
            if (x < minX) {
                minX = x;
            }
        }
        return minX;
    }

    public static boolean checkDayOffStatus(Context context) {
        int day = getDayOfWeek(Calendar.getInstance());
        boolean[] weekDays = User.getUser(context).getWeekDays();
        return weekDays[day];
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();

        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Minutes ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " min ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " h ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + "days ago";
        }
    }


}
