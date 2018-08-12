package com.mabe.productions.hrv_madison.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;


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

        Log.i("debugging", "addMeasurement");

        measurementsTable.child(user.getUid()).child(key).setValue(measurement);
    }


    public static void updateIntervalProgram(float duration, long[] workout_intervals, int[] running_pulse_zones, int[] walking_pulse_zones, int activity_streak){
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final DatabaseReference fireDatabase = FirebaseDatabase.getInstance().getReference("ipulsus/users/"+user.getUid());

        Log.i("debugging", "updateIntervalProgram");

        fireDatabase.child("workout_intervals").setValue(FeedReaderDbHelper.longArrayToString(workout_intervals));
        fireDatabase.child("running_pulse_zones").setValue(FeedReaderDbHelper.intArrayToString(running_pulse_zones));
        fireDatabase.child("walking_pulse_zones").setValue(FeedReaderDbHelper.intArrayToString(walking_pulse_zones));
        fireDatabase.child("workout_duration").setValue(duration);
        fireDatabase.child("activity_streak").setValue(activity_streak);
    }
    
    public static void addWorkout(FireWorkout workout){
        DatabaseReference workoutsTable = FirebaseDatabase.getInstance().getReference().child(WORKOUTS_TABLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String key = workoutsTable.push().getKey();

        Log.i("debugging", "addWorkout");

        workoutsTable.child(user.getUid()).child(key).setValue(workout);
    }

    public static void startListeningForMeasurements(final OnMeasurementFetchListener finishListener){

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                ArrayList<FireMeasurement> measurements = new ArrayList<>();

                for(DataSnapshot measurement : dataSnapshot.getChildren()){
                    measurements.add(measurement.getValue(FireMeasurement.class));
                }
                Log.i("debugging", "startListeningForMeasurements onDataChange");
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

        Log.i("debugging", "startListeningForMeasurements");

    }
    public static void startListeningForWorkouts(final OnWorkoutFetchListener finishListener){

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<FireWorkout> workouts = new ArrayList<>();

                for(DataSnapshot workout : dataSnapshot.getChildren()){
                    workouts.add(workout.getValue(FireWorkout.class));
                }
                Log.i("debugging", "startListeningForWorkoutso onDataChange");
                finishListener.onSuccess(workouts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finishListener.onFailure(databaseError);
            }
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference workoutsTable = FirebaseDatabase.getInstance().getReference().child(WORKOUTS_TABLE).child(user.getUid());
        workoutsTable.addValueEventListener(listener);
        Log.i("debugging", "startListeningForWorkouts");

    }


    public static void getUserFromFirebase(final OnUserDoneFetchListener finishListener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference specificUser = FirebaseDatabase.getInstance().getReference("ipulsus/users/"+user.getUid());
        Log.i("debugging", "getUserFromFirebase");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("debugging", "getUserFromFirebase onDataChange");
                FireUser fireUser = dataSnapshot.getValue(FireUser.class);
                finishListener.onSuccess(fireUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finishListener.onFailure(databaseError);
            }
        };


        specificUser.addListenerForSingleValueEvent(listener);
    }

    public static void saveFirstWeeklyProgramDate(String firstWeeklyDate) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("debugging", "saveFirstWeeklyProgramDate");
        final DatabaseReference userTable = FirebaseDatabase.getInstance().getReference("ipulsus/users/"+user.getUid());

        userTable.child("first_weekly_date").setValue(firstWeeklyDate);
    }

    public static abstract class OnMeasurementFetchListener {
        public abstract void onSuccess(List<FireMeasurement> measurements);
        public abstract void onFailure(DatabaseError error);
    }
    public static abstract class OnWorkoutFetchListener {
        public abstract void onSuccess(List<FireWorkout> workouts);
        public abstract void onFailure(DatabaseError error);
    }
    public static abstract class OnInitialDoneFetchListener {
        public abstract void onSuccess(boolean isInitialDone);
        public abstract void onFailure(DatabaseError error);
    }

    public static abstract class OnUserDoneFetchListener {
        public abstract void onSuccess(FireUser fireUser);
        public abstract void onFailure(DatabaseError error);
    }


    /**
     * Adds new user to the remote database.
     */
    public static void addUser(){
        DatabaseReference usersTable = FirebaseDatabase.getInstance().getReference(USERS_TABLE);
        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = usersTable.push().getKey();
        String identifier = fireUser.getUid();
        String email = fireUser.getEmail();
        boolean doneInitial = false;
        FireUser database_user = new FireUser(id,identifier,email,doneInitial);
        usersTable.child(identifier).setValue(database_user);

        Log.i("debugging", "addUser");
       
    }
    
    public static void isInitialDone(final OnInitialDoneFetchListener finishListener){
        Log.i("debugging", "isInitialDone");


        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference specificUser = FirebaseDatabase.getInstance().getReference("ipulsus/users/"+fireUser.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                FireUser fireUser = dataSnapshot.getValue(FireUser.class);
                finishListener.onSuccess(fireUser.isDoneInitial());
                Log.i("debugging", "isInitialDone onDataChange");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("auth", "loadPost:onCancelled", databaseError.toException());
                finishListener.onFailure(databaseError);

            }
        };

        specificUser.addListenerForSingleValueEvent(postListener);
    }


}
