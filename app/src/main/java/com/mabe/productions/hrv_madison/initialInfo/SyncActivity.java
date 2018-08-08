package com.mabe.productions.hrv_madison.initialInfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebaseDatase.FireMeasurement;
import com.mabe.productions.hrv_madison.firebaseDatase.FireUser;
import com.mabe.productions.hrv_madison.firebaseDatase.FirebaseUtils;
import com.mabe.productions.hrv_madison.measurements.Measurement;

import java.util.List;

public class SyncActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);


        FirebaseUtils.getUserFromFirebase(new FirebaseUtils.OnUserDoneFetchListener() {
            @Override
            public void onSuccess(FireUser fireUser) {
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_ACTIVITY_STREAK, fireUser.getActivity_streak(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_INITIAL_DURATION, fireUser.getBase_duration(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_KMI, fireUser.getKmi(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_BIRTHDAY, fireUser.getBirthday(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_GENDER, fireUser.getGender(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_HEIGHT, fireUser.getHeight(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_ACTIVITY_INDEX, fireUser.getActivity_index(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_WEIGHT, fireUser.getWeight(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_PASSWORD, fireUser.getPassword(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_BASE_DURATION, fireUser.getMaxDuration(),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Utils.saveToSharedPrefs(SyncActivity.this, FeedReaderDbHelper.FIELD_WEEK_DAYS, FeedReaderDbHelper.getWeeksFromString(fireUser.getWorkout_days()),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);

                FirebaseUtils.getAllMeasurements(new FirebaseUtils.OnMeasurementFetchListener(){

                    @Override
                    public void onSuccess(List<FireMeasurement> measurements) {

                        User.removeAllMeasurements(getApplicationContext());

                        for(FireMeasurement fireMeasurement : measurements){
                            Measurement measurement = new Measurement(fireMeasurement);
                            User.addMeasurementData(getApplicationContext(), measurement, false);
                        }

                        startActivity(new Intent(SyncActivity.this, MainScreenActivity.class));
                    }

                    @Override
                    public void onFailure(DatabaseError error) {
                        Log.i("TEST", "Failure: " + error.getMessage());
                    }
                });

            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });




    }
}
