package com.mabe.productions.hrv_madison.firebaseDatase;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mabe.productions.hrv_madison.measurements.Measurement;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtils {

    public static final String MEASUREMENTS_TABLE = "ipulsus/measurements";
    public static final String WORKOUTS_TABLE = "ipulsus/workouts";
    public static final String USERS_TABLE = "ipulsus/users";

    public static void addMeasurement(FireMeasurement measurement){
        DatabaseReference measurementsTable = FirebaseDatabase.getInstance().getReference().child(MEASUREMENTS_TABLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String key = measurementsTable.push().getKey();

        measurementsTable.child(user.getUid()).child(key).setValue(measurement);

    }

    public static void addWorkout(FireWorkout workout){
        DatabaseReference workoutsTable = FirebaseDatabase.getInstance().getReference().child(WORKOUTS_TABLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String key = workoutsTable.push().getKey();

        workoutsTable.child(user.getUid()).child(key).setValue(workout);
    }

    public static void getAllMeasurements(final OnFinishListener finishListener){

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<FireMeasurement> measurements = new ArrayList<>();

                for(DataSnapshot measurement : dataSnapshot.getChildren()){
                    measurements.add(measurement.getValue(FireMeasurement.class));
                }

                finishListener.onSuccess(measurements);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finishListener.onFailure(databaseError);
            }
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference measurementsTable = FirebaseDatabase.getInstance().getReference().child(MEASUREMENTS_TABLE).child(user.getUid());
        measurementsTable.addValueEventListener(listener);


    }

    public static abstract class OnFinishListener{
        public abstract void onSuccess(List<FireMeasurement> measurements);
        public abstract void onFailure(DatabaseError error);
    }


}
