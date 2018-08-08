package com.mabe.productions.hrv_madison.firebaseDatase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mabe.productions.hrv_madison.measurements.Measurement;

public class FirebaseUtils {

    public static final String MEASUREMENTS_TABLE = "ipulsus/measurements";
    public static final String WORKOUTS_TABLE = "ipulsus/workouts";

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


}
