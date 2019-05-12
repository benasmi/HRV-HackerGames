package com.mabe.productions.hrv_madison;

import android.content.res.Resources;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.StringRes;
import android.support.v4.widget.ContentLoadingProgressBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;
import com.tooltip.Tooltip;

import java.text.DecimalFormat;
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
    private ScrollView scrollView;

    //Toolbar
    private ImageView img_back_arrow;
    private TextView toolbar_txt;

    //Card
    private TextView advanced_history_txt_card_title;
    private TextView advanced_history_txt_card_duration;
    private TextView advanced_history_txt_card_date;
    private CardView advanced_history_card;

    private CardView bpm_card;
    private CardView pulse_distribution_card;

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

    private AppCompatImageButton imgButton_distribution;

    public Tooltip tooltip;

    private View.OnClickListener createTooltipListener(@StringRes final int message) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (tooltip != null)
                    tooltip.dismiss();

                tooltip = new Tooltip.Builder(view)
                        .setText(getResources().getString(message))
                        .setDismissOnClick(true)
                        .setBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setCornerRadius(7f)
                        .setGravity(Gravity.TOP)
                        .setCancelable(true)
                        .show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_workout_history);


        //Receiving data from RecyclerView and creating workout object
        HistoryRecyclerViewDataHolder parcel = getIntent().getExtras().getParcelable("Workout");
        WorkoutMeasurements workout = new WorkoutMeasurements(
                parcel.getExercise()
                , parcel.getDate()
                , parcel.getWorkout_duration()
                , parcel.getAverage_bpm()
                , parcel.getBpm_data()
                , parcel.getPace_data()
                , parcel.getRoute()
                , parcel.getCalories_burned()
                , parcel.getDistance());


        initialiseViews();
        setUpData(workout);
        settingWorkoutMap(workout);
        setFonts();


    }

    private void setFonts() {

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");
        txt_loading.setTypeface(verdana);


    }

    private void initialiseViews() {


        Animation bottom_to_top = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        Animation left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation left_to_right_d = AnimationUtils.loadAnimation(this, R.anim.left_to_right_delay);
        scrollView = (ScrollView) findViewById(R.id.activity_advanced_workout_history_scroll);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (tooltip != null) {
                    if (tooltip.isShowing()) {
                        tooltip.dismiss();
                    }
                }
            }
        });


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

        bpm_card = (CardView) findViewById(R.id.bpm_card);
        pulse_distribution_card = (CardView) findViewById(R.id.bpm_distribution_history);

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

    private void setUpData(WorkoutMeasurements workout) {

        if (workout.getBpm_data().length != 0) {
            //BPM card
            bpm_lineChart();

            average_bpm_value_history.setText((int) workout.getAverage_bpm() + "");
            max_bpm_history_value.setText(String.valueOf(getHighestBpm(workout)));

            int bpmValues[] = workout.getBpm_data();
            for (int i = 0; i < bpmValues.length; i++) {
                addEntryBpm(bpmValues[i], getHighestBpm(workout));

            }

        } else {
            pulse_distribution_card.setVisibility(View.GONE);
            bpm_card.setVisibility(View.GONE);
        }

        //Pulse zone distribution card
        int HRMax = (int) ((MainScreenActivity.user.getGender() == 0 ? 202 : 216) - (MainScreenActivity.user.getGender() == 0 ? 0.55f : 1.09f) * Utils.getAgeFromDate(MainScreenActivity.user.getBirthday()));

        int[] percentages = getPulseZonePercentages(HRMax, workout.getBpm_data());

        pulse_zone_lineChart(percentages);

        //Other info
        advanced_history_txt_card_duration.setText((int) (workout.getWorkout_duration() / 1000 / 60) + " min running");
        advanced_history_txt_card_date.setText(formatDate(workout.getDate()));
        advanced_history_txt_calories_value.setText(workout.getCalories_burned() == 0 ? "-" : String.valueOf(workout.getCalories_burned()) + " Kcal");
        advanced_history_txt_bpm_value.setText(String.valueOf(workout.getAverage_bpm() == 0 ? "-" : workout.getAverage_bpm()));
        advanced_history_txt_pace_value.setText(String.valueOf(workout.getAveragePace()) + " m/s");
        advanced_history_txt_distance_value.setText(String.valueOf(workout.getDistance()) + " km");

        int minPulseZone = workout.getExercise().getMinimumPulseZone();
        int maxPulseZone = workout.getExercise().getMaximumPulseZone();
        if (minPulseZone == maxPulseZone) {
            advanced_history_txt_intensity_value.setText(minPulseZone + Utils.getNumberSuffix(minPulseZone));
        } else {
            advanced_history_txt_intensity_value.setText(minPulseZone + Utils.getNumberSuffix(minPulseZone) + "-" + maxPulseZone + Utils.getNumberSuffix(maxPulseZone));
        }


        imgButton_distribution = (AppCompatImageButton) findViewById(R.id.imgButton_distribution);
        imgButton_distribution.setOnClickListener(createTooltipListener(R.string.pulse_distribution));

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
            set.setDrawValues(true);
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
        chart_bpm_history.getAxisLeft().setDrawAxisLine(false);
        chart_bpm_history.getAxisLeft().setDrawGridLines(true);
        chart_bpm_history.getXAxis().setDrawGridLines(false);
        chart_bpm_history.getXAxis().setDrawAxisLine(false);
        chart_bpm_history.getAxisRight().setDrawGridLines(false);
        chart_bpm_history.setDescription(null);
        chart_bpm_history.setTouchEnabled(false);
        chart_bpm_history.getAxisLeft().setLabelCount(5, true);
        chart_bpm_history.getAxisLeft().setDrawLabels(true);
        chart_bpm_history.setAutoScaleMinMaxEnabled(true);
        chart_bpm_history.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        chart_bpm_history.getAxisLeft().setTextColor(Color.WHITE);
        chart_bpm_history.setViewPortOffsets(0f, 0f, 0f, 0f);

        LineData data = new LineData();
        //Creating a line with single hr value

        //BPM DATA SET
        ArrayList<Entry> singleValueList = new ArrayList<>();

        LineDataSet set = new LineDataSet(singleValueList, "HR");
        set.setLineWidth(1);
        set.setDrawValues(true);
        set.setDrawCircleHole(false);
        set.setDrawCircles(false);
        set.setCircleRadius(getResources().getDimension(R.dimen.circle_radius));
        set.setCircleColor(Color.parseColor("#FFFFFF"));
        set.setColor(Color.parseColor("#F62459"));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
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

        chart_bpm_history.animateY(3000, Easing.EasingOption.EaseInBack);

    }

    private void pulse_zone_lineChart(int[] pulse_zone_distribution) {

        ArrayList<String> labels = new ArrayList<>();
        labels.add("1st");
        labels.add("2nd");
        labels.add("3rd");
        labels.add("4th");
        labels.add("5th");

        horizontal_pulse_distribution_history.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        horizontal_pulse_distribution_history.getLegend().setEnabled(false);
        horizontal_pulse_distribution_history.getXAxis().setDrawAxisLine(false);
        horizontal_pulse_distribution_history.getAxisRight().setDrawAxisLine(false);
        horizontal_pulse_distribution_history.getAxisLeft().setDrawAxisLine(true);
        horizontal_pulse_distribution_history.getAxisLeft().setTextColor(Color.WHITE);
        horizontal_pulse_distribution_history.getAxisRight().setTextColor(Color.WHITE);
        horizontal_pulse_distribution_history.getAxisLeft().setDrawGridLines(true);
        horizontal_pulse_distribution_history.getXAxis().setDrawGridLines(false);
        horizontal_pulse_distribution_history.getAxisRight().setDrawGridLines(false);
        horizontal_pulse_distribution_history.setDescription(null);
        horizontal_pulse_distribution_history.getAxisLeft().setDrawLabels(true);
        horizontal_pulse_distribution_history.getAxisLeft().setAxisMinimum(0);
        horizontal_pulse_distribution_history.getAxisLeft().setAxisMaximum(100);
        horizontal_pulse_distribution_history.getAxisRight().setDrawLabels(false);
        horizontal_pulse_distribution_history.getXAxis().setDrawLabels(true);
        horizontal_pulse_distribution_history.setTouchEnabled(false);
        horizontal_pulse_distribution_history.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        horizontal_pulse_distribution_history.getXAxis().setTextColor(Color.WHITE);
        horizontal_pulse_distribution_history.getAxisLeft().setValueFormatter(new PercentFormatter(new DecimalFormat("#0")));
        horizontal_pulse_distribution_history.setViewPortOffsets(0f, 0f, 0f, 0f);

        BarData data = new BarData();
        //Creating a line with single hr value

        //BPM DATA SET
        ArrayList<BarEntry> singleValueList = new ArrayList<>();
        singleValueList.add(new BarEntry(0, pulse_zone_distribution[0]));
        singleValueList.add(new BarEntry(1, pulse_zone_distribution[1]));
        singleValueList.add(new BarEntry(2, pulse_zone_distribution[2]));
        singleValueList.add(new BarEntry(3, pulse_zone_distribution[3]));
        singleValueList.add(new BarEntry(4, pulse_zone_distribution[4]));
        BarDataSet set = new BarDataSet(singleValueList, "HR");


        //Set label count to 5 as we are displaying 5 star rating
        horizontal_pulse_distribution_history.getXAxis().setLabelCount(5);
        horizontal_pulse_distribution_history.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        set.setDrawValues(false);
        set.setColor(Color.parseColor("#F62459"));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColors(new int[]{
                Color.parseColor("#a6f62459"),
                Color.TRANSPARENT
        });

        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setSize(240, 160);
        data.addDataSet(set);
        horizontal_pulse_distribution_history.setData(data);

    }

    public int getHighestBpm(WorkoutMeasurements workout) {
        int highest_bpm = workout.getBpm_data()[0];

        for (int bpm : workout.getBpm_data()) {
            highest_bpm = bpm > highest_bpm ? bpm : highest_bpm;
        }

        return highest_bpm;
    }


    private String formatDate(Date date) {
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

    /**
     * Returns the percentage of time the user has been in each pulse zone
     *
     * @param hr_max   The maximum heart rate of a user
     * @param bpm_data The heart rate data
     * @return The percentage of time the user has been in each pulse zone.
     */
    private static int[] getPulseZonePercentages(int hr_max, int[] bpm_data) {

        int zone_1_data_count = 0;
        int zone_2_data_count = 0;
        int zone_3_data_count = 0;
        int zone_4_data_count = 0;
        int zone_5_data_count = 0;

        for (int i = 0; i < bpm_data.length; i++) {

            if (bpm_data[i] > Utils.getPulseZoneBounds(1, hr_max)[0] && bpm_data[i] < Utils.getPulseZoneBounds(1, hr_max)[1]) {
                zone_1_data_count++;
            }
            if (bpm_data[i] > Utils.getPulseZoneBounds(2, hr_max)[0] && bpm_data[i] < Utils.getPulseZoneBounds(2, hr_max)[1]) {
                zone_2_data_count++;
            }
            if (bpm_data[i] > Utils.getPulseZoneBounds(3, hr_max)[0] && bpm_data[i] < Utils.getPulseZoneBounds(3, hr_max)[1]) {
                zone_3_data_count++;
            }
            if (bpm_data[i] > Utils.getPulseZoneBounds(4, hr_max)[0] && bpm_data[i] < Utils.getPulseZoneBounds(4, hr_max)[1]) {
                zone_4_data_count++;
            }
            if (bpm_data[i] > Utils.getPulseZoneBounds(5, hr_max)[0] && bpm_data[i] < Utils.getPulseZoneBounds(5, hr_max)[1]) {
                zone_5_data_count++;
            }

        }

        int zone1Percentage = (int) ((zone_1_data_count * 100f) / (float) (zone_1_data_count + zone_2_data_count + zone_3_data_count + zone_4_data_count + zone_5_data_count));
        int zone2Percentage = (int) ((zone_2_data_count * 100f) / (float) (zone_1_data_count + zone_2_data_count + zone_3_data_count + zone_4_data_count + zone_5_data_count));
        int zone3Percentage = (int) ((zone_3_data_count * 100f) / (float) (zone_1_data_count + zone_2_data_count + zone_3_data_count + zone_4_data_count + zone_5_data_count));
        int zone4Percentage = (int) ((zone_4_data_count * 100f) / (float) (zone_1_data_count + zone_2_data_count + zone_3_data_count + zone_4_data_count + zone_5_data_count));
        int zone5Percentage = (int) ((zone_5_data_count * 100f) / (float) (zone_1_data_count + zone_2_data_count + zone_3_data_count + zone_4_data_count + zone_5_data_count));

        return new int[]{zone1Percentage, zone2Percentage, zone3Percentage, zone4Percentage, zone5Percentage};
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
