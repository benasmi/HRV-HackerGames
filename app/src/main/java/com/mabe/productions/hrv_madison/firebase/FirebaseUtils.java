package com.mabe.productions.hrv_madison.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.measurements.Measurement;


import java.util.ArrayList;
import java.util.List;

public class FirebaseUtils {

    public static final String MEASUREMENTS_TABLE = "measurements";
    public static final String WORKOUTS_TABLE = "ipulsusRunning/workouts";

    public static final String USERS_TABLE_RUNNING = "ipulsusRunning/users";
    public static final String USERS_TABLE_GLOBAL = "users";


    /**
     * Adds (or queues to add) the measurement to remote db. This also
     * updates the locally saved measurement's remote db id.
     * @param measurement Measurement to save remotely
     */
    public static void addMeasurement(FireMeasurement measurement, Context context){
        DatabaseReference measurementsTable = FirebaseDatabase.getInstance().getReference().child(MEASUREMENTS_TABLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String key = measurementsTable.push().getKey();

        Crashlytics.log("Measurement's remote database key: " + key);

        String uid;
        try{
            uid = user.getUid();
        }catch(NullPointerException e){
            uid = "null";
        }
        Crashlytics.log("User's UID: " + uid);

        //We need to save the remote database key locally, so we could access the measurement later.
        Measurement localDbMeasurement = new Measurement(measurement);
        localDbMeasurement.setRemoteDbId(key);
        User.updateMeasurement(context, localDbMeasurement, User.UPDATE_TYPE_BY_ID);

        measurementsTable.child(user.getUid()).child(key).setValue(measurement);
    }

    /**
     * Updates the measurement remotely
     * @param measurement The new measurement
     */
    public static void updateMeasurement(Measurement measurement){
        String key = measurement.getRemoteDbId();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference measurementsTable = FirebaseDatabase.getInstance().getReference().child(MEASUREMENTS_TABLE);
        measurementsTable.child(user.getUid()).child(key).setValue(new FireMeasurement(measurement));
    }


    public static void updateIntervalProgram(float duration, long[] workout_intervals, int[] running_pulse_zones, int[] walking_pulse_zones, int activity_streak){
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final DatabaseReference fireDatabase = FirebaseDatabase.getInstance().getReference(USERS_TABLE_RUNNING + "/" + user.getUid());

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

        workoutsTable.child(user.getUid()).child(key).setValue(workout);
    }

    public static void fetchMeasurements(final OnMeasurementFetchListener finishListener, boolean onlyFetchOnce){

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                ArrayList<Measurement> measurements = new ArrayList<>();

                for(DataSnapshot measurement : dataSnapshot.getChildren()){
                    Measurement localMeasurement = new Measurement(measurement.getValue(FireMeasurement.class));
                    localMeasurement.setRemoteDbId(measurement.getKey());

                    measurements.add(localMeasurement);
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

        if(onlyFetchOnce){
            measurementsTable.addListenerForSingleValueEvent(listener);
        }else{
            measurementsTable.addValueEventListener(listener);
        }

    }
    public static void fetchWorkouts(final OnWorkoutFetchListener finishListener, boolean onlyFetchOnce){

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<FireWorkout> workouts = new ArrayList<>();

                for(DataSnapshot workout : dataSnapshot.getChildren()){
                    workouts.add(workout.getValue(FireWorkout.class));
                }
                finishListener.onSuccess(workouts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finishListener.onFailure(databaseError);
            }
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference workoutsTable = FirebaseDatabase.getInstance().getReference().child(WORKOUTS_TABLE).child(user.getUid());

        if(onlyFetchOnce){
            workoutsTable.addListenerForSingleValueEvent(listener);
        }else{
            workoutsTable.addValueEventListener(listener);
        }

    }

    public static void getGlobalUserInstance(final OnGlobalUserDoneFetchListener finishListener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference specificUser = FirebaseDatabase.getInstance().getReference(FirebaseUtils.USERS_TABLE_GLOBAL + "/" + user.getUid());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FireGlobalUser user = dataSnapshot.getValue(FireGlobalUser.class);
                finishListener.onSuccess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finishListener.onFailure(databaseError);
            }
        };
        specificUser.addListenerForSingleValueEvent(listener);

    }

    public static void getUserFromFirebase(final OnUserDoneFetchListener finishListener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference specificUser = FirebaseDatabase.getInstance().getReference( USERS_TABLE_RUNNING + "/"+user.getUid());
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        final DatabaseReference userTable = FirebaseDatabase.getInstance().getReference(USERS_TABLE_RUNNING + "/" + user.getUid());

        userTable.child("first_weekly_date").setValue(firstWeeklyDate);
    }

    public static abstract class OnMeasurementFetchListener {
        public abstract void onSuccess(List<Measurement> measurements);
        public abstract void onFailure(DatabaseError error);
    }
    public static abstract class OnGlobalUserDoneFetchListener {
        public abstract void onSuccess(FireGlobalUser globalUser);
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
    public static void addUser(String name){
        DatabaseReference usersTable = FirebaseDatabase.getInstance().getReference(USERS_TABLE_GLOBAL);

        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();

        boolean doneInitial = false;
        DatabaseReference aerobicsTable = FirebaseDatabase.getInstance().getReference(USERS_TABLE_RUNNING);
        aerobicsTable.child(fireUser.getUid()).child("doneInitial").setValue(doneInitial);

        String identifier = fireUser.getUid();
        String email = fireUser.getEmail();

        String displayname = name;
        if(displayname == null){
            displayname = fireUser.getDisplayName();
        }
        usersTable.child(identifier).child("email").setValue(email);
        usersTable.child(identifier).child("displayname").setValue(displayname);
    }

    public static void isInitialDone(final OnInitialDoneFetchListener finishListener){

        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference specificUser = FirebaseDatabase.getInstance().getReference(USERS_TABLE_RUNNING + "/"+fireUser.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                boolean doneInitial;

                try{
                    doneInitial = dataSnapshot.child("doneInitial").getValue(Boolean.class);
                }catch (NullPointerException e){
                    doneInitial = false;
                }

                finishListener.onSuccess(doneInitial);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                finishListener.onFailure(databaseError);

            }
        };

        specificUser.addListenerForSingleValueEvent(postListener);
    }
}
