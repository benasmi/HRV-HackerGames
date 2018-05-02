package com.mabe.productions.hrv_madison;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by Benas on 5/1/2018.
 */

public class GoogleMapService extends Service {

    public static boolean isLocationListeningEnabled = false;
    public static final String ACTION_SEND_GPS_DATA = "SEND_GPS_DATA";
    public static final String GPS_DATA = "LOCATION_RESULT";
    public static final String GPS_WAKELOCK_TAG = "HRV_MADISON_GPS";
    private FusedLocationProviderClient mFusedLocationClient;
    private KeyguardManager myKM;
    private ArrayList<LocationResult> locationArrayList = new ArrayList<>();
    private PowerManager.WakeLock wakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("TEST", "Creating service...");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //noinspection MissingPermission
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);

        //noinspection MissingPermission
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
//                                                    locationPendingIntent);



        myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);


    }

    private void aquireWakelock(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                                                                  GPS_WAKELOCK_TAG);
        wakeLock.acquire();
    }

    private void releaseWakelock(){
        if(wakeLock.isHeld()){
            wakeLock.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        aquireWakelock();
        return START_NOT_STICKY;
    }

    //The location callback that stores paceData and distance.
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.i("TEST", "Getting results...");

            //If accuraccy is shit do not add to locationResults Array
            if(locationResult.getLastLocation().getAccuracy() > 50){
                Log.i("TEST", "Location not accurate enough: " + locationResult.getLastLocation().getAccuracy() + "m");
                return;
            }
            Log.i("TEST", "Location accuracy is reasonable: " + locationResult.getLastLocation().getAccuracy() + "m");
            //If accuraccy is good add to locationResults Array
            locationArrayList.add(locationResult);

            //If screen is locked, keep on adding and don't send any data
            if( myKM.inKeyguardRestrictedInputMode()) {
                //it is locked
                return;
            }

            //When screen is not locked anymore - send data chunk to workoutFragment.class
            LocalBroadcastManager.getInstance(GoogleMapService.this).sendBroadcast(new Intent(ACTION_SEND_GPS_DATA).putExtra(GPS_DATA, locationArrayList.toArray(new LocationResult[0])));

            //Clear arrayList after sending, to start adding new data
            locationArrayList.clear();

            }



    };

    @Override
    public void onDestroy() {
        releaseWakelock();
        Log.i("TEST", "Stopping service...");
        super.onDestroy();
    }


}
