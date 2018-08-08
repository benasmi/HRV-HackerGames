package com.mabe.productions.hrv_madison.firebaseDatase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mabe.productions.hrv_madison.LoginActivity;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialPage;
import com.mabe.productions.hrv_madison.measurements.Measurement;

public class FirebaseUtils {

    public static final String MEASUREMENTS_TABLE = "ipulsus/measurements";
    public static final String WORKOUTS_TABLE = "ipulsus/workouts";
    public static final String USERS_TABLE = "ipulsus/users";
    private static boolean doneInitial = false;



    public static void addMeasurement(FireMeasurement measurement){

        DatabaseReference measurementsTable = FirebaseDatabase.getInstance().getReference().child(MEASUREMENTS_TABLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        measurementsTable.child(user.getUid()).setValue(measurement);
    }
    
    public static void addWorkout(FireWorkout workout){
        DatabaseReference workoutsTable = FirebaseDatabase.getInstance().getReference().child(WORKOUTS_TABLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        workoutsTable.child(user.getUid()).setValue(workout);
    }

    public static void changeBool(boolean initial){
        doneInitial = initial;
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
    
    public static boolean isInitialDone(){
        
        
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
                changeBool(fireUser.isDoneInitial());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("auth", "loadPost:onCancelled", databaseError.toException());

            }
        };

        specificUser.addValueEventListener(postListener);

        Log.i("auth", "IsINITIAL DONE FIREBASE UTILS: " + String.valueOf(doneInitial));
        return doneInitial;
    }


}
