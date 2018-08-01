package com.mabe.productions.hrv_madison;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mabe.productions.hrv_madison.measurements.Measurement;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {

    private View img_back_arrow;
    private TextView txt_history;

    private ArrayList<Measurement> measurements = new ArrayList<>();
    private ArrayList<WorkoutMeasurements> workouts = new ArrayList<>();
    private ArrayList<RecyclerViewDataHolder> adapterDataSet = new ArrayList<>();

    private AdapterRecyclerView recyclerView_adapter;
    private RecyclerView recyclerview_history;
    private RecyclerView.LayoutManager layout_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        User user = User.getUser(this);
        workouts = user.getAllWorkouts();
        measurements = user.getAllMeasurements();

        combineMeasurementsAndWorkouts(measurements,workouts);
        sortByDate(adapterDataSet);

        initialiseViews();

    }

    private void initialiseViews(){


        Animation left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation left_to_right_d = AnimationUtils.loadAnimation(this, R.anim.left_to_right_delay);



        recyclerview_history = (RecyclerView) findViewById(R.id.history_recyler);
        recyclerview_history.setHasFixedSize(true);

        // use a linear layout manager
        layout_manager = new LinearLayoutManager(this);
        recyclerview_history.setLayoutManager(layout_manager);

        Log.i("TEST1", "AdapterDataSet: " + String.valueOf(adapterDataSet.size()));
        Log.i("TEST1", "Workouts: " + String.valueOf(workouts.size()));
        Log.i("TEST1", "Measurements: " + String.valueOf(measurements.size()));


        recyclerView_adapter = new AdapterRecyclerView(adapterDataSet, this);
        recyclerview_history.setAdapter(recyclerView_adapter);

        txt_history = (TextView) findViewById(R.id.toolbar_title_registration);
        img_back_arrow = (ImageView) findViewById(R.id.img_back_arrow);
        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryActivity.this.finish();
            }
        });



        img_back_arrow.startAnimation(left_to_right);
        txt_history.startAnimation(left_to_right_d);
    }




    private void combineMeasurementsAndWorkouts(ArrayList<Measurement> measurements, ArrayList<WorkoutMeasurements> workouts){

        for(WorkoutMeasurements workout: workouts){
            adapterDataSet.add(0,new RecyclerViewDataHolder(
                    workout.getUnique_id()
                    ,workout.getDate()
                    ,workout.getWorkout_duration()
                    ,workout.getAverage_bpm()
                    ,workout.getBpm_data()
                    ,workout.getPace_data()
                    ,workout.getRoute()
                    ,workout.getCalories_burned()
                    ,workout.getPulse_zone()
                    ,workout.getDistance()
                    ,0));
        }

        for(Measurement measurement: measurements){
            adapterDataSet.add(0,new RecyclerViewDataHolder(
                    measurement.getDate()
                    ,measurement.getDuration()
                    ,measurement.getRmssd()
                    ,measurement.getLn_rmssd()
                    ,measurement.getLowest_rmssd()
                    ,measurement.getHighest_rmssd()
                    ,measurement.getLowest_bpm()
                    ,measurement.getHighest_bpm()
                    ,measurement.getAverage_bpm()
                    ,measurement.getLF_band()
                    ,measurement.getVLF_band()
                    ,measurement.getVHF_band()
                    ,measurement.getHF_band()
                    ,measurement.getBpm_data()
                    ,measurement.getRmssd_data()
                    ,measurement.getUniqueId()
                    ,measurement.getMood()
                    ,measurement.getHrv(),1));
        }
    }

    private void sortByDate(ArrayList<RecyclerViewDataHolder> data){
            for(int i = 0; i<data.size(); i++){
                for(int z = 0; z<data.size()-1; z++){
                    int day = Integer.parseInt(DateFormat.format("dd", data.get(z).getDate()).toString());
                    int day1 = Integer.parseInt(DateFormat.format("dd", data.get(z+1).getDate()).toString());
                    if(day>day1){
                        RecyclerViewDataHolder temporaryItem = data.get(z+1);
                        data.set(z+1, data.get(z));
                        data.set(z, temporaryItem);
                    }
                }
            }

    }

}
