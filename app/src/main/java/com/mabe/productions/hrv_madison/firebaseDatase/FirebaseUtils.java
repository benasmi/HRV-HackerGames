package com.mabe.productions.hrv_madison.firebaseDatase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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

    public static void getAllMeasurements(final OnMeasurementFetchListener finishListener){

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

    public static abstract class OnMeasurementFetchListener {
        public abstract void onSuccess(List<FireMeasurement> measurements);
        public abstract void onFailure(DatabaseError error);
    }
    public static abstract class OnInitialDoneFetchListener {
        public abstract void onSuccess(boolean isInitialDone);
        public abstract void onFailure(DatabaseError error);
    }



    public static void addUser(){
        DatabaseReference usersTable = FirebaseDatabase.getInstance().getReference(USERS_TABLE);
        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = usersTable.push().getKey();
        String identifier = fireUser.getUid();
        String email = fireUser.getEmail();
        String password = "Slaptazodis";
        boolean doneInitial = false;
        FireUser database_user = new FireUser(id,identifier,email,password,doneInitial);
        usersTable.child(identifier).setValue(database_user);
       
    }
    
    public static void isInitialDone(final OnInitialDoneFetchListener finishListener){
        
        
        Log.i("auth", "Second time user providing info...");

        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference specificUser = FirebaseDatabase.getInstance().getReference("ipulsus/users/"+fireUser.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                FireUser fireUser = dataSnapshot.getValue(FireUser.class);
                Log.i("auth", "DoneInitial: " + String.valueOf(fireUser.isDoneInitial()));
                Log.i("auth", "email: " + String.valueOf(fireUser.getEmail()));
                Log.i("auth", "password: " + String.valueOf(fireUser.getPassword()));

                finishListener.onSuccess(fireUser.isDoneInitial());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("auth", "loadPost:onCancelled", databaseError.toException());
                finishListener.onFailure(databaseError);

            }
        };

        specificUser.addValueEventListener(postListener);
    }


}
