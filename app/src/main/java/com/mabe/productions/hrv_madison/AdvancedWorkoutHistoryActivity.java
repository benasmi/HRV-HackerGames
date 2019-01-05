package com.mabe.productions.hrv_madison;

import android.content.res.Resources;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.ContentLoadingProgressBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.zxing.client.result.TextParsedResult;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdvancedWorkoutHistoryActivity extends AppCompatActivity {

    //Map
    private GoogleMap route_display_googlemap;
    private SupportMapFragment map_fragment;
    private RelativeLayout map_frame_layout;
    private ContentLoadingProgressBar loading_progress;
    private TextView txt_loading;


    //Toolbar
    private ImageView img_back_arrow;
    private TextView toolbar_txt;

    //Card
    private TextView advanced_history_txt_card_title;
    private TextView advanced_history_txt_card_duration;
    private TextView advanced_history_txt_card_date;
    private CardView advanced_history_card;

    //BPM card
    private TextView average_bpm_value_history;
    private TextView max_bpm_history_value;
    private LineChart chart_bpm_history;

    //Pulse distribution card
    private HorizontalBarChart horizontal_pulse_distribution_history;

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
        HistoryRecyclerViewDataHolder parcel = getIntent().getExtras().getParcelable("Workout");
        WorkoutMeasurements workout = new WorkoutMeasurements(
                parcel.getExercise()
                ,parcel.getDate()
                ,parcel.getWorkout_duration()
                ,parcel.getAverage_bpm()
                ,parcel.getBpm_data()
                ,parcel.getPace_data()
                ,parcel.getRoute()
                ,parcel.getCalories_burned()
                ,parcel.getDistance());


        initialiseViews();
        setUpData(workout);
        settingWorkoutMap(workout);
        setFonts();
        bpm_lineChart();
    }

    private void setFonts(){

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");
        txt_loading.setTypeface(verdana);



    }





    private void initialiseViews(){




        Animation bottom_to_top = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        Animation left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation left_to_right_d = AnimationUtils.loadAnimation(this, R.anim.left_to_right_delay);

        txt_loading = (TextView) findViewById(R.id.txt_loading);
        toolbar_txt = (TextView) findViewById(R.id.toolbar_title_advanced);
        advanced_history_card = (CardView) findViewById(R.id.advanced_history_card);
        advanced_history_data_layout = (LinearLayout) findViewById(R.id.advanced_history_data_layout);

        map_fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.history_workout_route);


        img_back_arrow = (ImageView) findViewById(R.id.img_back_arrow);
        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvancedWorkoutHistoryActivity.this.finish();
            }
        });


        //Bpm card
        average_bpm_value_history = (TextView) findViewById(R.id.average_bpm_value_history);
        max_bpm_history_value = (TextView) findViewById(R.id.max_bpm_history_value);
        chart_bpm_history = (LineChart) findViewById(R.id.chart_bpm_history);

        //Pulse distribution
        horizontal_pulse_distribution_history = (HorizontalBarChart) findViewById(R.id.horizontal_pulse_distribution_history);

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

        map_frame_layout = (RelativeLayout) findViewById(R.id.map_frame_layout);
        loading_progress = (ContentLoadingProgressBar) findViewById(R.id.loading_progress);
        loading_progress.show();

        img_back_arrow.startAnimation(left_to_right);
        toolbar_txt.startAnimation(left_to_right_d);
        advanced_history_card.startAnimation(left_to_right);

        advanced_history_data_layout.startAnimation(bottom_to_top);
    }

    private void setUpData(WorkoutMeasurements workout){

        //BPM card
        average_bpm_value_history.setText((int) workout.getAverage_bpm() + "");
        max_bpm_history_value.setText((int) getHighestBpm(workout));

        int bpmValues[] = workout.getBpm_data();
        for (int i = 0; i < bpmValues.length; i++) {
                addEntryBpm(bpmValues[i], getHighestBpm(workout));

        }

        //Other info
        advanced_history_txt_card_duration.setText((int)(workout.getWorkout_duration()/1000/60) + " min running");
        advanced_history_txt_card_date.setText(formatDate(workout.getDate()));
        advanced_history_txt_calories_value.setText(workout.getCalories_burned()==0 ? "-" : String.valueOf(workout.getCalories_burned()) + " Kcal");
        advanced_history_txt_bpm_value.setText(String.valueOf(workout.getAverage_bpm()== 0 ? "-" : workout.getAverage_bpm()));
        advanced_history_txt_pace_value.setText(String.valueOf(workout.getAveragePace()) + " m/s");
        advanced_history_txt_distance_value.setText(String.valueOf(workout.getDistance()) + " km");

        int minPulseZone = workout.getExercise().getMinimumPulseZone();
        int maxPulseZone = workout.getExercise().getMaximumPulseZone();
        if(minPulseZone == maxPulseZone){
            advanced_history_txt_intensity_value.setText(minPulseZone + Utils.getNumberSuffix(minPulseZone));
        }else{
            advanced_history_txt_intensity_value.setText(minPulseZone +  Utils.getNumberSuffix(minPulseZone) + "-" + maxPulseZone + Utils.getNumberSuffix(maxPulseZone));
        }

    }
    private void addEntryBpm(int hr, int max_points) {

        LineData data = chart_bpm_history.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            //Creating a line with single hr value
            ArrayList<Entry> singleValueList = new ArrayList<>();
            singleValueList.add(new Entry(0, hr));
            set = new LineDataSet(singleValueList, "HR");
            set.setLineWidth(getResources().getDimension(R.dimen.line_width));
            set.setDrawValues(false);
            set.setDrawCircleHole(false);
            set.setDrawCircles(false);
            set.setCircleRadius(getResources().getDimension(R.dimen.circle_radius));
            set.setCircleColor(Color.parseColor("#F62459"));
            set.setColor(Color.parseColor("#F62459"));
            set.setDrawFilled(false);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColors(new int[]{
                    Color.parseColor("#a6f62459"),
                    Color.TRANSPARENT
            });
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setSize(240, 160);
            set.setFillDrawable(drawable);
            data.addDataSet(set);
        } else {
            set.addEntry(new Entry(set.getEntryCount(), hr));
        }

        data.notifyDataChanged();
        chart_bpm_history.notifyDataSetChanged();
        chart_bpm_history.setVisibleXRangeMaximum(max_points);
        chart_bpm_history.setVisibleXRangeMinimum(0);



    }
    private void bpm_lineChart() {



        chart_bpm_history.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        chart_bpm_history.getLegend().setTextColor(Color.WHITE);
        chart_bpm_history.getXAxis().setDrawAxisLine(false);
        chart_bpm_history.getAxisRight().setDrawAxisLine(false);
        chart_bpm_history.getAxisLeft().setDrawAxisLine(true);
        chart_bpm_history.getAxisLeft().setDrawGridLines(true);
        chart_bpm_history.getXAxis().setDrawGridLines(false);
        chart_bpm_history.getAxisRight().setDrawGridLines(false);
        chart_bpm_history.setDescription(null);
        chart_bpm_history.getAxisLeft().setDrawLabels(true);
        chart_bpm_history.getAxisLeft().setAxisMinimum(0);
        chart_bpm_history.getAxisLeft().setAxisMaximum(200);
        chart_bpm_history.getAxisRight().setDrawLabels(false);
        chart_bpm_history.getXAxis().setDrawLabels(false);
        chart_bpm_history.setTouchEnabled(false);
        chart_bpm_history.setViewPortOffsets(0f, 0f, 0f, 0f);

        LineData data = new LineData();
        //Creating a line with single hr value

        //BPM DATA SET
        ArrayList<Entry> singleValueList = new ArrayList<>();
        singleValueList.add(new Entry(0, 16));
        singleValueList.add(new Entry(1, 24));
        singleValueList.add(new Entry(2, 64));
        singleValueList.add(new Entry(3, 35));
        LineDataSet set = new LineDataSet(singleValueList, "HR");
        set.setLineWidth(1);
        set.setDrawValues(false);
        set.setDrawCircleHole(false);
        set.setDrawCircles(false);
        set.setCircleRadius(getResources().getDimension(R.dimen.circle_radius));
        set.setCircleColor(Color.parseColor("#FFFFFF"));
        set.setColor(Color.parseColor("#F62459"));
        set.setDrawFilled(false);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColors(new int[]{
                Color.parseColor("#a6f62459"),
                Color.TRANSPARENT
        });

        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setSize(240, 160);
        set.setFillDrawable(drawable);
        data.addDataSet(set);
        chart_bpm_history.setData(data);




    }

    public int getHighestBpm(WorkoutMeasurements workout){
        int highest_bpm = workout.getBpm_data()[0];

        for(int bpm : workout.getBpm_data()){
            highest_bpm = bpm > highest_bpm ? bpm : highest_bpm;
        }

        return highest_bpm;
    }


    private String formatDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, hh:mm 'val'");
        return dateFormat.format(cal.getTime());

    }


    private void settingWorkoutMap(final WorkoutMeasurements workout) {



            map_fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {



                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            map_frame_layout.setVisibility(View.GONE);
                            loading_progress.hide();


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
//            LatLngBounds bounds = builder.build();
//            double lat = (bounds.northeast.latitude+bounds.southwest.latitude)/2;
//            double longt = (bounds.northeast.longitude+bounds.southwest.longitude)/2;
//            cu = CameraUpdateFactory.newLatLngZoom(new LatLng(lat,longt),16);

            LatLngBounds bounds = builder.build();
            cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);

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
