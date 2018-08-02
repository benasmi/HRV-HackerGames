package com.mabe.productions.hrv_madison;

import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mabe.productions.hrv_madison.fragments.DataTodayFragment;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class AdvancedWorkoutHistoryActivity extends AppCompatActivity {

    //Map
    private GoogleMap route_display_googlemap;
    private SupportMapFragment map_fragment;

    //Toolbar
    private ImageView img_back_arrow;
    private TextView toolbar_txt;

    //Card
    private TextView advanced_history_txt_card_title;
    private TextView advanced_history_txt_card_duration;
    private TextView advanced_history_txt_card_date;
    private CardView advanced_history_card;

    //WorkoutData
    private LinearLayout advanced_history_data_layout;
    private TextView advanced_history_txt_calories;
    private TextView advanced_history_txt_calories_value;

    private TextView advanced_history_txt_bpm;
    private TextView advanced_history_txt_bpm_value;

    private TextView advanced_history_txt_pace;
    private TextView advanced_history_txt_pace_value;

    private TextView advanced_history_txt_distance;
    private TextView advanced_history_txt_distance_value;

    private TextView advanced_history_txt_intensity;
    private TextView advanced_history_txt_intensity_value;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_workout_history);


        //Receiving data from RecyclerView and creating workout object
        RecyclerViewDataHolder parcel = getIntent().getExtras().getParcelable("Workout");
        WorkoutMeasurements workout = new WorkoutMeasurements(parcel.getDate()
                ,parcel.getWorkout_duration()
                ,parcel.getAverage_bpm()
                ,parcel.getBpm_data()
                ,parcel.getPace_data()
                ,parcel.getRoute()
                ,parcel.getCalories_burned()
                ,parcel.getPulse_zone()
                ,parcel.getDistance());


        initialiseViews();
        setUpData(workout);
        settingWorkoutMap(workout);

    }





    private void initialiseViews(){
        Animation top_to_bottom = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);
        Animation bottom_to_top = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        Animation left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation left_to_right_strong = AnimationUtils.loadAnimation(this, R.anim.left_to_right_strong);
        Animation left_to_right_d = AnimationUtils.loadAnimation(this, R.anim.left_to_right_delay);

        toolbar_txt = (TextView) findViewById(R.id.toolbar_title_advanced);
        advanced_history_card = (CardView) findViewById(R.id.advanced_history_card);
        advanced_history_data_layout = (LinearLayout) findViewById(R.id.advanced_history_data_layout);

        map_fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.history_workout_route);


        img_back_arrow = (ImageView) findViewById(R.id.img_back_arrow);
        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvancedWorkoutHistoryActivity.this.finish();
            }
        });


        advanced_history_txt_card_title = (TextView) findViewById(R.id.advanced_history_txt_card_title);
        advanced_history_txt_card_duration = (TextView) findViewById(R.id.advanced_history_txt_card_duration);
        advanced_history_txt_card_date = (TextView) findViewById(R.id.advanced_history_txt_card_date);
        advanced_history_txt_calories = (TextView) findViewById(R.id.advanced_history_txt_calories);
        advanced_history_txt_calories_value = (TextView) findViewById(R.id.advanced_history_txt_calories_value);
        advanced_history_txt_bpm = (TextView) findViewById(R.id.advanced_history_txt_bpm);
        advanced_history_txt_bpm_value = (TextView) findViewById(R.id.advanced_history_txt_bpm_value);
        advanced_history_txt_pace = (TextView) findViewById(R.id.advanced_history_txt_pace);
        advanced_history_txt_pace_value = (TextView) findViewById(R.id.advanced_history_txt_pace_value);
        advanced_history_txt_distance = (TextView) findViewById(R.id.advanced_history_txt_distance);
        advanced_history_txt_distance_value = (TextView) findViewById(R.id.advanced_history_txt_distance_value);
        advanced_history_txt_intensity = (TextView) findViewById(R.id.advanced_history_txt_intensity);
        advanced_history_txt_intensity_value = (TextView) findViewById(R.id.advanced_history_txt_intensity_value);

        img_back_arrow.startAnimation(left_to_right);
        toolbar_txt.startAnimation(left_to_right_d);
        advanced_history_card.startAnimation(left_to_right);
        advanced_history_data_layout.startAnimation(bottom_to_top);
    }

    private void setUpData(WorkoutMeasurements workout){
        advanced_history_txt_card_duration.setText((int)(workout.getWorkout_duration()/1000/60) + " min running");
        advanced_history_txt_card_date.setText(formatDate(workout.getDate()));
        advanced_history_txt_calories_value.setText(workout.getCalories_burned()==0 ? "-" : String.valueOf(workout.getCalories_burned()) + " Kcal");
        advanced_history_txt_bpm_value.setText(String.valueOf(workout.getAverage_bpm()== 0 ? "-" : workout.getAverage_bpm()));
        advanced_history_txt_pace_value.setText(String.valueOf(workout.getAveragePace()) + " m/s");
        advanced_history_txt_distance_value.setText(String.valueOf(workout.getDistance()) + " km");
        advanced_history_txt_intensity_value.setText(String.valueOf(workout.getPulse_zone()));

    }

    private String formatDate(Date date){
        String time = DateFormat.getMediumDateFormat(this).format(date);
        String realTime = time.substring(0,5);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int years = cal.get(Calendar.YEAR);
        return String.valueOf(new StringBuilder().append(realTime).append(", ").append(years));
    }


    private void settingWorkoutMap(final WorkoutMeasurements workout) {



            map_fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    route_display_googlemap = googleMap;

                    try {

                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(AdvancedWorkoutHistoryActivity.this, R.raw.dark_google_map));

                    } catch (Resources.NotFoundException e) {
                        Log.e("GMAPS", "Can't find style. Error: ", e);
                    }

                    map_fragment.getView().setClickable(false);
                    displayMapRoute(workout.getRoute());

                }
            });

    }

    private void displayMapRoute(LatLng[] route) {
        //Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions lineOptions = new PolylineOptions()
                .width(9)
                .color(getResources().getColor(R.color.colorAccent))
                .geodesic(false);

        //This will zoom the map to our polyline
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng point : route) {
            lineOptions.add(point);
            builder.include(point);
        }
        route_display_googlemap.addPolyline(lineOptions);

        final CameraUpdate cu;
        if (route.length == 0) {
            //If no points are present for some reason
            cu = CameraUpdateFactory.newLatLngZoom(new LatLng(55.19f, 23.4f), 6f); //Geographical centre of lithuania
        } else {
            LatLngBounds bounds = builder.build();
            double lat = (bounds.northeast.latitude+bounds.southwest.latitude)/2;
            double longt = (bounds.northeast.longitude+bounds.southwest.longitude)/2;
            cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat,longt),16);
        }

        route_display_googlemap.animateCamera(cu);
    }
    @Override
    public void onResume() {
        super.onResume();

        if (route_display_googlemap != null) {
            map_fragment.onResume();
        }
    }



    @Override
    public void onPause() {
        if (map_fragment != null) {
            map_fragment.onPause();
        }
        super.onPause();
    }
}
