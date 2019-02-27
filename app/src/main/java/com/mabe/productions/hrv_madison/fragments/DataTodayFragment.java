package com.mabe.productions.hrv_madison.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.mabe.productions.hrv_madison.AdvancedWorkoutHistoryActivity;
import com.mabe.productions.hrv_madison.FrequencyZoneView;
import com.mabe.productions.hrv_madison.HistoryActivity;
import com.mabe.productions.hrv_madison.HistoryRecyclerViewDataHolder;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.firebase.FireWorkout;
import com.mabe.productions.hrv_madison.firebase.FirebaseUtils;
import com.mabe.productions.hrv_madison.measurements.Measurement;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataTodayFragment extends Fragment {

    private PullRefreshLayout pull_refresh_layout;
    private ScrollView scrollview;

    //FrequencyCardView
    private CardView freq_card;
    private TextView freq_card_txt_freq_band;
    private TextView freq_card_txt_freq_band_date;
    private TextView freq_card_txt_after_this_measure;
    private TextView freq_card_txt_hf_after_measurament;
    private TextView freq_card_txt_lf_after_measurement;
    private TextView freq_card_ratio_meaning;
    private TextView freq_card_ratio_meaning_advice;
    private FrequencyZoneView freq_card_ratio_scale;
    private PieChart frequency_chart;
    private AppCompatImageButton imgButton_frequencies_info;

    //HrvCardView
    private CardView hrv_card;
    private TextView hrv_card_txt_hrv;
    private TextView hrv_card_txt_hrv_band_date;
    private TextView hrv_card_txt_bpm;
    private TextView hrv_card_txt_bpm_after_measurament;
    private TextView hrv_card_txt_bpm_norm_after_measurement;
    private TextView hrv_card_txt_hrv_after;
    private TextView hrv_card_txt_average_hrv;
    private TextView hrv_card_txt_average_norm_hrv;
    private PieChart health_index_chart;


    //bpm cardview
    private LineChart bpm_line_chart;
    private CardView bpm_card;
    private TextView bpm_card_txt_bpm;
    private TextView bpm_card_txt_date;
    private TextView bpm_card_txt_average;
    private TextView bpm_card_value_average;
    private TextView bpm_card_txt_hrv_average_value;
    private TextView bpm_card_hrv_average_value;
    private AppCompatImageButton imgButton_rmssd_info;


    //Daily reccomendation cardview
    private CardView recommendation_card;
    private TextView reccomendation_txt_todays_program;
    private TextView reccomendation_txt_hrv_increase;
    private TextView reccomendation_txt_duration;
    private TextView reccomendation_txt_pulse_zone;
    private TextView reccomendation_txt_verbal_recommendation;
    private ImageView reccomendation_img_arrow;
    private TextView reccomendation_todays_program_explanation;
    private AppCompatImageButton imgButton_hrv_info;
    private AppCompatImageButton imgButton_duration_info;
    private AppCompatImageButton imgButton_intensity_info;

    //How do you feel? cardview
    private CardView feeling_cardview;
    private TextView txt_how_do_you_feel;
    private TextView txt_emotion_explaining;
    private ImageView img_negatively_excited;
    private ImageView img_negatively_mellow;
    private ImageView img_neutral;
    private ImageView img_positively_mellow;
    private ImageView img_positively_excited;
    private int STATE_FEELING = 2;
    private AppCompatButton test_button;
    private EditText test_edittext;

    //Workout route cardview
    private CardView workout_done_cardview;
    private SupportMapFragment map_fragment;
    private TextView txt_workout_data;
    private TextView txt_workout_time_ago;
    private TextView workout_card_pace;
    private TextView workout_card_distance;
    private TextView workout_card_calories;
    private AppCompatButton workout_see_more_info_btn;
    private GoogleMap route_display_googlemap;
    private AppCompatImageButton imgButton_workout_card_info;

    //First time layout
    private LinearLayout no_measured_today_layout;
    private AppCompatButton no_measured_today_button;
    private TextView first_time_greeting;

    private boolean hasMeasuredToday;
    private boolean hasWorkedOutToday;

    private AppCompatButton reccomendation_btn_start_workout;
    private AppCompatButton button_history;
    private ImageView img_back_arrow;

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
                        .setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent))
                        .setTextColor(getActivity().getResources().getColor(R.color.white))
                        .setCornerRadius(7f)
                        .setGravity(Gravity.TOP)
                        .show();
            }
        };
    }

    public DataTodayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.data_today_fragment, container, false);

        initializeViews(view);
        setFonts();
        health_index_pieChart();
        bpm_lineChart();
        imageViewClickers();
        updateData(getContext());
        setUpFirebaseListeners();
        return view;

    }


    private void setCardViewsVisibilityAndData(User user) {

        //Today measurements and workouts
        Measurement todayMeasurement = user.getTodaysMeasurement();
        WorkoutMeasurements todaysWorkoutMeasurement = user.getTodaysWorkout();

        hasMeasuredToday = todayMeasurement == null ? false : true;
        hasWorkedOutToday = todaysWorkoutMeasurement == null ? false : true;

        if (hasMeasuredToday) {

            //Show: ---> mood ; BPM ; LFHF; Training Plan
            feeling_cardview.setVisibility(View.VISIBLE);
            bpm_card.setVisibility(View.VISIBLE);
            freq_card.setVisibility(View.VISIBLE);
            recommendation_card.setVisibility(View.VISIBLE);
            //Hide: ---> No measurement layout
            no_measured_today_layout.setVisibility(View.GONE);

            /*
               If view has visibility ---> SET DATA
               BPM CARDVIEW
             */
            Measurement measurement = user.getLastMeasurement();

            //Setting bpm/hrv data
            if (measurement != null) {
                int bpmValues[] = measurement.getBpm_data();
                int rmssdValues[] = measurement.getRmssd_data();

                int maxGraphValue = bpmValues.length > rmssdValues.length ? bpmValues.length : rmssdValues.length;

                for (int i = 0; i < bpmValues.length; i++) {
                    if (i <= maxGraphValue) {
                        addEntryBpm(bpmValues[i], maxGraphValue);

                    }
                }

                for (int i = 0; i < rmssdValues.length; i++) {
                    if (i <= maxGraphValue) {
                        addEntryRmssd(rmssdValues[i], maxGraphValue);

                    }
                }
            }
            bpm_card_txt_date.setText(Utils.getTimeAgo(user.getLastMeasurement().getDate().getTime()));
            bpm_line_chart.animateY(2000, Easing.EasingOption.EaseInOutSine);

            //FREQUENCY CARDVIEW
            freq_card_txt_freq_band_date.setText(Utils.getTimeAgo(user.getLastMeasurement().getDate().getTime()));
            freq_card_txt_hf_after_measurament.setText(String.valueOf("HF: " + (int) measurement.getHF_band() + "%"));
            freq_card_txt_lf_after_measurement.setText(String.valueOf("LF: " + (int) measurement.getLF_band() + "%"));
            float lf = measurement.getLF_band();
            float hf = measurement.getHF_band();
            float ratio = lf / hf;
            setLF_HF_RatioZone(ratio);
            setFrequencyChartData(measurement.getHF_band(), measurement.getLF_band());

            bpm_card_hrv_average_value.setText(String.valueOf(measurement.getRmssd()));
            bpm_card_value_average.setText(String.valueOf((int) measurement.getAverage_bpm()));

            //INITIAL MOOD CARDVIEW
            switch (measurement.getMood()) {
                case User.MOOD_NEGATIVELY_EXCITED:
                    img_negatively_excited.callOnClick();
                    break;
                case User.MOOD_NEGATIVELY_MELLOW:
                    img_negatively_mellow.callOnClick();
                    break;
                case User.MOOD_NEUTRAL:
                    img_neutral.callOnClick();
                    break;
                case User.MOOD_POSITIVELY_MELLOW:
                    img_positively_mellow.callOnClick();
                    break;
                case User.MOOD_POSITIVELY_EXCITED:
                    img_positively_excited.callOnClick();
                    break;
            }


        } else {
            //Show: ---> no measurement layout
            no_measured_today_layout.setVisibility(View.VISIBLE);
            //Hide: ---> mood; BPM; LFHF; Training plan; workout results
            feeling_cardview.setVisibility(View.GONE);
            bpm_card.setVisibility(View.GONE);
            freq_card.setVisibility(View.GONE);
            //recommendation_card.setVisibility(View.GONE);
            workout_done_cardview.setVisibility(View.GONE);
        }


        if (hasMeasuredToday && user.getAllMeasurements().size() < 2) {
            //We do not have enough data to display percentage, so current hrv is displayed
            reccomendation_txt_hrv_increase.setText(String.valueOf((int) user.getCurrentHrv()));
            reccomendation_img_arrow.setImageResource(R.drawable.ic_appicon_rectangle);
            reccomendation_txt_hrv_increase.setTextColor(Color.parseColor("#ffffff"));
        } else if (!hasMeasuredToday) {
            //FireUser has not measured today, asking him to do so.
            reccomendation_txt_hrv_increase.setText("Please measure\nyour HRV!");
            reccomendation_img_arrow.setImageResource(R.drawable.ic_question);
            reccomendation_txt_hrv_increase.setTextColor(Color.parseColor("#ffffff"));
        } else {
            //FireUser has measured today and there is plenty of data to display percentage.
            setReccomendationCardPercentage(user.getLatestHrvRatio());
        }


        if (!user.getWeekDays()[Utils.getDayOfWeek(Calendar.getInstance())]) {
            reccomendation_txt_verbal_recommendation.setText("We reccomend you to take a day off!");
        }

        int minPulseZone = user.getExercise().getMinimumPulseZone();
        int maxPulseZone = user.getExercise().getMaximumPulseZone();
        if (minPulseZone == maxPulseZone) {
            reccomendation_txt_pulse_zone.setText(minPulseZone + Utils.getNumberSuffix(minPulseZone) + " pulse zone");
        } else {
            reccomendation_txt_pulse_zone.setText(minPulseZone + Utils.getNumberSuffix(minPulseZone) + "-" + maxPulseZone + Utils.getNumberSuffix(maxPulseZone) + "\npulse zone");
        }
        reccomendation_txt_todays_program.setText(user.getWorkoutSessionType());
        reccomendation_todays_program_explanation.setText(user.getVerbalSessionExplanation());
        reccomendation_txt_duration.setText(String.valueOf((int) user.getWorkoutDuration()) + " " + getString(
                R.string.min));


        if (hasWorkedOutToday) {
            //Show: ---> workoutResults
            workout_done_cardview.setVisibility(View.VISIBLE);

            //WORKOUT DATA CARDVIEW
            final WorkoutMeasurements workout = user.getLastWorkout();
            if (workout != null) {
                txt_workout_time_ago.setText(Utils.getTimeAgo(user.getLastWorkout().getDate().getTime()));
                workout_card_pace.setText(String.valueOf(workout.getAveragePace()));
                workout_card_distance.setText(String.valueOf(workout.getDistance()));
                workout_see_more_info_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getContext(), AdvancedWorkoutHistoryActivity.class).putExtra("Workout", new HistoryRecyclerViewDataHolder((workout))).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }
                });

                workout_card_calories.setText(String.valueOf(workout.getCalories_burned()));
                settingWorkoutMap(workout);
            }
        } else {
            workout_done_cardview.setVisibility(View.GONE);
        }

    }


    private void setUpFirebaseListeners() {
        pull_refresh_layout.setRefreshing(true);
        FirebaseUtils.fetchMeasurements(measurementFetchListener, false);
        FirebaseUtils.fetchWorkouts(workoutFetchListener, false);
    }

    private void fetchDataOnce() {
        pull_refresh_layout.setRefreshing(true);
        FirebaseUtils.fetchMeasurements(measurementFetchListener, true);
        FirebaseUtils.fetchWorkouts(workoutFetchListener, true);
    }

    private FirebaseUtils.OnMeasurementFetchListener measurementFetchListener = new FirebaseUtils.OnMeasurementFetchListener() {

        @Override
        public void onSuccess(List<Measurement> measurements) {

            User.removeAllMeasurements(getContext());

            for (Measurement measurement : measurements) {
                User.addMeasurementData(getContext(), measurement, false);
            }

            updateData(getContext());

            pull_refresh_layout.setRefreshing(false);
        }

        @Override
        public void onFailure(DatabaseError error) {
            Log.i("TEST", "Failure: " + error.getMessage());
            pull_refresh_layout.setRefreshing(false);
        }
    };

    private FirebaseUtils.OnWorkoutFetchListener workoutFetchListener = new FirebaseUtils.OnWorkoutFetchListener() {

        @Override
        public void onSuccess(List<FireWorkout> workouts) {
            User.removeAllWorkouts(getContext());
            for (FireWorkout fireWorkout : workouts) {
                WorkoutMeasurements workout = new WorkoutMeasurements(fireWorkout);
                User.addWorkoutData(getContext(), workout, false);
            }

            updateData(getContext());


            pull_refresh_layout.setRefreshing(false);
        }

        @Override
        public void onFailure(DatabaseError error) {
            pull_refresh_layout.setRefreshing(false);
        }
    };


    public void updateData(Context context) {

        MainScreenActivity.user = User.getUser(context);
        setCardViewsVisibilityAndData(MainScreenActivity.user);

    }


    private void setLF_HF_RatioZone(float ratio) {
        freq_card_ratio_scale.setElementPosition(ratio);
        if (ratio >= 1.4f && ratio <= 1.6f) {
            freq_card_ratio_meaning.setText("Ideal balance!");
            freq_card_ratio_meaning_advice.setText("Your body is feeling great, keep it up!");
        }
        if (ratio >= 1.61f && ratio <= 2f) {
            freq_card_ratio_meaning.setText("Your body starts to feel stressed!");
            freq_card_ratio_meaning_advice.setText("Consider some relaxation exercises and not pushing yourself too hard today!");
        }
        if (ratio >= 0.5f && ratio <= 1.39f) {
            freq_card_ratio_meaning.setText("Your body is recovering!");
            freq_card_ratio_meaning_advice.setText("Consider doing some non-stresful activities with higher intensity during your daily schedule ");
        }
        if (ratio > 2f) {
            freq_card_ratio_meaning.setText("Your body is under pressure!");
            freq_card_ratio_meaning_advice.setText("Ratio indicates anxiety, stress, pressure. You should reduce your work load and physical activities!");
        }
        if (ratio < 0.5f) {
            freq_card_ratio_meaning.setText("Your body is exhausted!");
            freq_card_ratio_meaning_advice.setText("Ratio indicates low energy and exhaustion. You should consider taking a day off and relaxing!");
        }


    }

    private void settingWorkoutMap(final WorkoutMeasurements workout) {
        if (route_display_googlemap == null) {

            map_fragment.onCreate(getArguments());
            map_fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    DataTodayFragment.this.route_display_googlemap = googleMap;

                    try {
                        // Customise the styling of the base map using a JSON object defined
                        // in a raw resource file.
                        boolean success = googleMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.dark_google_map));

                        if (!success) {
                            Log.e("GMAPS", "Style parsing failed.");
                        } else {
                            Log.e("GMAPS", "Style SUCCESS");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e("GMAPS", "Can't find style. Error: ", e);
                    }

                    map_fragment.getView().setClickable(false);
                    displayMapRoute(workout.getRoute());

                }
            });
        } else {
            displayMapRoute(workout.getRoute());
        }
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
            cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
        }

        route_display_googlemap.animateCamera(cu);
    }


    private void setReccomendationCardPercentage(float percentageChange) {
        reccomendation_img_arrow.setRotation(0f);

        if (percentageChange >= 1) {
            int percentageIncrease = Math.round((percentageChange - 1) * 100);
            reccomendation_txt_hrv_increase.setText("+ " + percentageIncrease + "%\nincrease");
            reccomendation_txt_hrv_increase.setTextColor(Color.parseColor("#2ecc71"));
            reccomendation_img_arrow.setImageResource(R.drawable.ic_arrow_green);

        } else if (percentageChange <= 1) {
            int percentageDecrease = Math.round((1 - percentageChange) * 100);
            reccomendation_txt_hrv_increase.setText("- " + percentageDecrease + "%\ndecrease");
            reccomendation_txt_hrv_increase.setTextColor(Color.parseColor("#e74c3c"));
            reccomendation_img_arrow.setImageResource(R.drawable.ic_arrow_red);
            reccomendation_img_arrow.setRotation(180f);
        }

    }


    private void initializeViews(View view) {

        //Frequency PieChart
        freq_card = view.findViewById(R.id.frequency_card);
        freq_card_txt_freq_band = view.findViewById(R.id.frequency_bands_text_view);
        freq_card_txt_freq_band_date = view.findViewById(R.id.frequency_bands_measurement_date);
        freq_card_txt_after_this_measure = view.findViewById(R.id.freq_card_txt_after_this_measure);
        freq_card_txt_hf_after_measurament = view.findViewById(R.id.freq_card_txt_hf_after_measurement);
        freq_card_txt_lf_after_measurement = view.findViewById(R.id.freq_card_txt_lf_after_measurement);
        freq_card_ratio_meaning = view.findViewById(R.id.freq_card_ratio_meaning);
        freq_card_ratio_meaning_advice = view.findViewById(R.id.freq_card_ratio_meaning_advice);
        freq_card_ratio_scale = view.findViewById(R.id.freq_card_ratio_scale);
        imgButton_frequencies_info = view.findViewById(R.id.imgButton_frequency_info);

        frequency_chart = view.findViewById(R.id.chart_frequencies);


        //Casual modifications
        frequency_chart.setUsePercentValues(true);
        frequency_chart.setDrawSliceText(false);
        frequency_chart.getDescription().setEnabled(false);

        //Space inside chart and color
        frequency_chart.setTransparentCircleRadius(50f);
        frequency_chart.setHoleColor(Color.TRANSPARENT);
        frequency_chart.setHoleRadius(80f);
        frequency_chart.setCenterText("Frequencies");
        frequency_chart.setCenterTextSize(20f);
        frequency_chart.setCenterTextColor(Color.WHITE);
        //Remove X-axis values
        frequency_chart.setDrawEntryLabels(false);
        //Animate pieChart
        frequency_chart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        health_index_chart = view.findViewById(R.id.chart_health_index);

        //HRV PieChart
        hrv_card_txt_hrv = (TextView) view.findViewById(R.id.health_index_text_view);
        hrv_card_txt_hrv_band_date = (TextView) view.findViewById(R.id.health_index_measurement_date);
        hrv_card_txt_bpm = (TextView) view.findViewById(R.id.hrc_card_txt_bpm);
        hrv_card_txt_bpm_after_measurament = (TextView) view.findViewById(R.id.hrc_card_txt_average_bpm);
        hrv_card_txt_bpm_norm_after_measurement = (TextView) view.findViewById(R.id.hrc_card_txt_norm_bpm);
        hrv_card_txt_hrv_after = (TextView) view.findViewById(R.id.hrc_card_txt_hrv);
        hrv_card_txt_average_hrv = (TextView) view.findViewById(R.id.hrc_card_txt_average_hrv);
        hrv_card_txt_average_norm_hrv = (TextView) view.findViewById(R.id.hrc_card_txt_norm_hrv);

        //BPM PieChart
        bpm_card = (CardView) view.findViewById(R.id.bpm_card);
        bpm_line_chart = (LineChart) view.findViewById(R.id.chart_bpm);
        bpm_card_txt_bpm = (TextView) view.findViewById(R.id.bpm_index_text_view);
        bpm_card_txt_date = (TextView) view.findViewById(R.id.bpm_index_measurement_date);
        bpm_card_txt_average = (TextView) view.findViewById(R.id.bpm_txt_average);
        bpm_card_value_average = (TextView) view.findViewById(R.id.bpm_value);
        bpm_card_txt_hrv_average_value = (TextView) view.findViewById(R.id.bpm_card_txt_hrv_average_value);
        bpm_card_hrv_average_value = (TextView) view.findViewById(R.id.bpm_card_hrv_average_value);
        imgButton_rmssd_info = view.findViewById(R.id.imgButton_rmssd_info);


        //Reccomendation cardview
        recommendation_card = view.findViewById(R.id.recommendation_card);
        reccomendation_txt_todays_program = view.findViewById(R.id.txt_todays_program);
        reccomendation_txt_hrv_increase = view.findViewById(R.id.txt_hrv_increase);
        reccomendation_txt_duration = view.findViewById(R.id.txt_duration_value);
        reccomendation_txt_pulse_zone = view.findViewById(R.id.txt_pulse_zone_value);
        reccomendation_txt_verbal_recommendation = view.findViewById(R.id.txt_verbal_recomendation);
        reccomendation_img_arrow = view.findViewById(R.id.reccomendation_arrow_img);
        reccomendation_todays_program_explanation = view.findViewById(R.id.txt_todays_program_explanation);
        reccomendation_btn_start_workout = view.findViewById(R.id.startWorkout);
        imgButton_hrv_info = view.findViewById(R.id.imgButton_hrv_info);
        imgButton_duration_info = view.findViewById(R.id.imgButton_duration_info);
        imgButton_intensity_info = view.findViewById(R.id.imgButton_intensity_info);
        reccomendation_btn_start_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.setViewpagerTab(getActivity(), 2);
            }
        });

        //Feeling cardview
        feeling_cardview = view.findViewById(R.id.feeling_card);
        txt_how_do_you_feel = view.findViewById(R.id.txt_how_do_you_feel);
        txt_emotion_explaining = view.findViewById(R.id.txt_emotion_explaining);
        img_negatively_excited = view.findViewById(R.id.img_negatively_excited);
        img_negatively_mellow = view.findViewById(R.id.img_negatively_mellow);
        img_neutral = view.findViewById(R.id.img_neutral);
        img_positively_mellow = view.findViewById(R.id.img_positively_mellow);
        img_positively_excited = view.findViewById(R.id.img_positively_excited);


        //Maps cardview
        //todo: override onPause() onResume() and onDestroy() methods and call map_fragment.onPause(), map_fragment.onResume(), map_fragment.onDestroy() accordingly.
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(54.6, 25.27)) //Just a random starting point. Will be changed in reloadData()
                .zoom(1f)
                .build();
        map_fragment = SupportMapFragment.newInstance(new GoogleMapOptions().camera(cp));
        fm.beginTransaction().replace(R.id.workout_route, map_fragment).commit();


        //Workout cardview
        workout_done_cardview = view.findViewById(R.id.cardview_route);
        workout_card_calories = view.findViewById(R.id.workout_card_calories_burned);
        workout_card_pace = view.findViewById(R.id.workout_card_running_pace);
        workout_card_distance = view.findViewById(R.id.workout_card_distance_run);
        workout_see_more_info_btn = view.findViewById(R.id.more_info_workout_btn);
        txt_workout_data = view.findViewById(R.id.workout_index_text_view);
        txt_workout_time_ago = view.findViewById(R.id.workout_index_measurement_date);
        imgButton_workout_card_info = view.findViewById(R.id.imgButton_workout_card_info);


        feeling_cardview.setTranslationY(Utils.getScreenHeight(getContext()));
        feeling_cardview.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();

        recommendation_card.setTranslationY(Utils.getScreenHeight(getContext()));
        recommendation_card.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();

        bpm_card.setTranslationY(Utils.getScreenHeight(getContext()));
        bpm_card.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();

        workout_done_cardview.setTranslationY(Utils.getScreenHeight(getContext()));
        workout_done_cardview.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();

        freq_card.setTranslationY(Utils.getScreenHeight(getContext()));
        freq_card.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();


        //First time cardview
        no_measured_today_button = view.findViewById(R.id.first_time_measure_button);
        no_measured_today_layout = view.findViewById(R.id.no_measurement_today_layout);
        first_time_greeting = view.findViewById(R.id.first_time_greeting);
        no_measured_today_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setViewpagerTab(getActivity(), 0);
            }
        });

        Animation anim_btn = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom_delay);
        Animation anim_txt = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        no_measured_today_button.startAnimation(anim_btn);
        first_time_greeting.startAnimation(anim_txt);

        test_button = view.findViewById(R.id.test_button);
        test_edittext = view.findViewById(R.id.text_editText);

        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.addMeasurementData(getContext(), new Measurement(Calendar.getInstance().getTime(), 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, new int[]{5, 4, 3}, new int[]{5, 4, 3}, 2, 0, 4, Integer.valueOf(test_edittext.getText().toString()), null), true);
            }
        });

        button_history = view.findViewById(R.id.button_history);
        button_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DataTodayFragment.this.getActivity(), HistoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        pull_refresh_layout = view.findViewById(R.id.rootLayout);

        pull_refresh_layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDataOnce();
            }
        });

        imgButton_duration_info.setOnClickListener(createTooltipListener(R.string.duration_info));
        imgButton_hrv_info.setOnClickListener(createTooltipListener(R.string.hrv_info));
        imgButton_intensity_info.setOnClickListener(createTooltipListener(R.string.intensity_info));
        imgButton_rmssd_info.setOnClickListener(createTooltipListener(R.string.rmssd_info));
        imgButton_workout_card_info.setOnClickListener(createTooltipListener(R.string.workout_card_info));
        imgButton_frequencies_info.setOnClickListener(createTooltipListener(R.string.frequencies_info));

        scrollview = view.findViewById(R.id.data_today_fragment_scrollview);
        scrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (tooltip != null) {
                    if (tooltip.isShowing()) {
                        tooltip.dismiss();
                    }
                }
            }
        });
    }


    private void setFonts() {
        Typeface futura = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/futura_light.ttf");
        Typeface verdana = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Verdana.ttf");


        frequency_chart.setCenterTextTypeface(futura);
        health_index_chart.setCenterTextTypeface(futura);

        //Weekly reccomendation

        //FrequencyCardView
        freq_card_txt_freq_band.setTypeface(verdana);
        freq_card_txt_freq_band_date.setTypeface(verdana);
        freq_card_txt_after_this_measure.setTypeface(verdana);
        freq_card_txt_hf_after_measurament.setTypeface(verdana);
        freq_card_txt_lf_after_measurement.setTypeface(verdana);
        freq_card_ratio_meaning.setTypeface(verdana);
        freq_card_ratio_meaning_advice.setTypeface(verdana);

        //HrvCardView
        hrv_card_txt_hrv.setTypeface(verdana);
        hrv_card_txt_hrv_band_date.setTypeface(verdana);
        hrv_card_txt_bpm.setTypeface(verdana);
        hrv_card_txt_bpm_after_measurament.setTypeface(verdana);
        hrv_card_txt_bpm_norm_after_measurement.setTypeface(verdana);
        hrv_card_txt_hrv_after.setTypeface(verdana);
        hrv_card_txt_average_hrv.setTypeface(verdana);
        hrv_card_txt_average_norm_hrv.setTypeface(verdana);

        bpm_card_txt_bpm.setTypeface(verdana);
        bpm_card_txt_date.setTypeface(verdana);
        bpm_card_txt_average.setTypeface(verdana);
        bpm_card_value_average.setTypeface(verdana);
        bpm_card_txt_hrv_average_value.setTypeface(verdana);
        bpm_card_hrv_average_value.setTypeface(verdana);


        bpm_line_chart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        bpm_line_chart.getLegend().setTextColor(Color.WHITE);
        bpm_line_chart.getXAxis().setDrawAxisLine(false);
        bpm_line_chart.getAxisRight().setDrawAxisLine(false);
        bpm_line_chart.getAxisLeft().setDrawAxisLine(true);
        bpm_line_chart.getAxisLeft().setDrawGridLines(true);
        bpm_line_chart.getXAxis().setDrawGridLines(false);
        bpm_line_chart.getAxisRight().setDrawGridLines(false);
        bpm_line_chart.setDescription(null);
        bpm_line_chart.getAxisLeft().setDrawLabels(true);
        bpm_line_chart.getAxisLeft().setAxisMinimum(0);
        bpm_line_chart.getAxisLeft().setAxisMaximum(200);
        bpm_line_chart.getAxisRight().setDrawLabels(false);
        bpm_line_chart.getXAxis().setDrawLabels(false);
        bpm_line_chart.setTouchEnabled(false);
        bpm_line_chart.setViewPortOffsets(0f, 0f, 0f, 0f);
        bpm_line_chart.getAxisLeft().setLabelCount(10, true);
        bpm_line_chart.getAxisLeft().setDrawLabels(true);
        bpm_line_chart.setAutoScaleMinMaxEnabled(true);
        bpm_line_chart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        bpm_line_chart.getAxisLeft().setTextColor(Color.WHITE);
        bpm_line_chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        //Reccomendation cardview
        reccomendation_txt_todays_program.setTypeface(futura);
        reccomendation_txt_hrv_increase.setTypeface(futura);
        reccomendation_txt_duration.setTypeface(futura);
        reccomendation_txt_pulse_zone.setTypeface(futura);
        reccomendation_txt_verbal_recommendation.setTypeface(futura);

        //Workout cardview
        workout_card_calories.setTypeface(futura);
        workout_card_pace.setTypeface(futura);
        workout_card_distance.setTypeface(futura);
        txt_workout_data.setTypeface(futura);
        txt_workout_time_ago.setTypeface(futura);


        //First time cardview
        first_time_greeting.setTypeface(futura);

    }


    private void setFrequencyChartData(float hf, float lf) {
        //Modify Y-axis value

        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(hf, "HF"));
        values.add(new PieEntry(lf, "LF"));

        final PieDataSet dataSet = new PieDataSet(values, "Frequencies");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(3f);
        dataSet.setColors(new int[]{Color.parseColor("#e74c3c"), Color.parseColor("#2980b9"), Color.parseColor("#9b59b6"), Color.parseColor("#f1c40f")});
        dataSet.setDrawValues(false);

        //Modify Data looks
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        //Setting data
        frequency_chart.setData(data);

        data.notifyDataChanged();
        frequency_chart.notifyDataSetChanged();
        //Modify data
        Legend legend = frequency_chart.getLegend();
        legend.setEnabled(false);
    }

    private void addEntryBpm(int hr, int max_points) {

        LineData data = bpm_line_chart.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            //Creating a line with single hr value
            ArrayList<Entry> singleValueList = new ArrayList<>();
            singleValueList.add(new Entry(0, hr));
            set = new LineDataSet(singleValueList, "HR");
            set.setLineWidth(getContext().getResources().getDimension(R.dimen.line_width));
            set.setDrawValues(false);
            set.setDrawCircleHole(false);
            set.setDrawCircles(false);
            set.setCircleRadius(getContext().getResources().getDimension(R.dimen.circle_radius));
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
        bpm_line_chart.notifyDataSetChanged();

        //chart_hr.setData(data);
        //chart_hr.animate();
        //chart_hr.moveViewToX(set.getEntryCount());
        bpm_line_chart.setVisibleXRangeMaximum(max_points);
        bpm_line_chart.setVisibleXRangeMinimum(0);
        //chart_hr.setAutoScaleMinMaxEnabled(true);


    }

    private void addEntryRmssd(int rmssd, int max_points) {

        LineData data = bpm_line_chart.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(1);
        if (set == null) {
            //Creating a line with single hr value
            ArrayList<Entry> singleValueList = new ArrayList<>();
            singleValueList.add(new Entry(0, rmssd));
            set = new LineDataSet(singleValueList, "RMSSD");
            set.setLineWidth(getContext().getResources().getDimension(R.dimen.line_width));
            set.setDrawValues(false);
            set.setDrawCircleHole(false);
            set.setDrawCircles(false);
            set.setCircleRadius(getContext().getResources().getDimension(R.dimen.circle_radius));
            set.setCircleColor(Color.parseColor("#F62459"));
            set.setColor(Color.parseColor("#2ecc71"));
            set.setDrawFilled(true);

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
            set.addEntry(new Entry(set.getEntryCount(), rmssd));
        }

        data.notifyDataChanged();
        bpm_line_chart.notifyDataSetChanged();

        //chart_hr.setData(data);
        //chart_hr.animate();
        //chart_hr.moveViewToX(set.getEntryCount());
        bpm_line_chart.setVisibleXRangeMaximum(max_points);
        bpm_line_chart.setVisibleXRangeMinimum(0);
        //chart_hr.setAutoScaleMinMaxEnabled(true);


    }

    private void bpm_lineChart() {
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
        set.setCircleRadius(getContext().getResources().getDimension(R.dimen.circle_radius));
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

        //RMSSD DATA SET
        ArrayList<Entry> secondValueList = new ArrayList<>();
        secondValueList.add(new Entry(0, 35));
        secondValueList.add(new Entry(1, 65));
        secondValueList.add(new Entry(2, 14));
        secondValueList.add(new Entry(3, 34));
        LineDataSet rmssdSet = new LineDataSet(secondValueList, "RMSSD");
        rmssdSet.setLineWidth(1);
        rmssdSet.setDrawValues(false);
        rmssdSet.setDrawCircleHole(false);
        rmssdSet.setDrawCircles(false);
        rmssdSet.setCircleRadius(getContext().getResources().getDimension(R.dimen.circle_radius));
        rmssdSet.setCircleColor(Color.parseColor("#FFFFFF"));
        rmssdSet.setColor(Color.parseColor("#2ecc71"));
        rmssdSet.setDrawFilled(false);


        data.addDataSet(rmssdSet);

        bpm_line_chart.setData(data);


    }

    private void health_index_pieChart() {

        //Casual modifications
        health_index_chart.setUsePercentValues(true);
        health_index_chart.setDrawSliceText(false);
        health_index_chart.getDescription().setEnabled(false);

        //Space inside chart and color
        health_index_chart.setTransparentCircleRadius(50f);
        health_index_chart.setHoleColor(Color.TRANSPARENT);
        health_index_chart.setHoleRadius(80f);
        health_index_chart.setCenterText("Index\n20%");
        health_index_chart.setCenterTextSize(20f);
        health_index_chart.setCenterTextColor(Color.WHITE);

        //Remove X-axis values
        health_index_chart.setDrawEntryLabels(false);

        //Animate pieChart
        health_index_chart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(30f, "BPM"));
        values.add(new PieEntry(70f, "asfBPM"));

        //Modify Y-axis value
        final PieDataSet dataSet = new PieDataSet(values, "Frequencies");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(3f);
        dataSet.setColors(new int[]{Color.parseColor("#20bf6b"), Color.parseColor("#26de81")});
        dataSet.setDrawValues(false);

        //Modify Data looks
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        //Setting data
        health_index_chart.setData(data);

        //Modify data
        Legend legend = health_index_chart.getLegend();
        legend.setEnabled(false);

    }

    private void imageViewClickers() {
        img_negatively_excited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                STATE_FEELING = 0;
                txt_emotion_explaining.setText(R.string.negatively_excited);
                img_negatively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_excited_selected));
                img_negatively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_mellow));
                img_neutral.setImageDrawable(getResources().getDrawable(R.drawable.ic_neutral));
                img_positively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_mellow));
                img_positively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_excited));
                updateMood(User.MOOD_NEGATIVELY_EXCITED);
            }
        });
        img_negatively_mellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                STATE_FEELING = 1;
                txt_emotion_explaining.setText(R.string.negatively_mellow);
                img_negatively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_excited));
                img_negatively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_mellow_selected));
                img_neutral.setImageDrawable(getResources().getDrawable(R.drawable.ic_neutral));
                img_positively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_mellow));
                img_positively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_excited));
                updateMood(User.MOOD_NEGATIVELY_MELLOW);
            }
        });
        img_neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                STATE_FEELING = 2;
                txt_emotion_explaining.setText(R.string.neutral);
                img_negatively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_excited));
                img_negatively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_mellow));
                img_neutral.setImageDrawable(getResources().getDrawable(R.drawable.ic_neutral_selected));
                img_positively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_mellow));
                img_positively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_excited));
                updateMood(User.MOOD_NEUTRAL);
            }
        });
        img_positively_mellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                STATE_FEELING = 3;
                txt_emotion_explaining.setText(R.string.positively_mellow);
                img_negatively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_excited));
                img_negatively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_mellow));
                img_neutral.setImageDrawable(getResources().getDrawable(R.drawable.ic_neutral));
                img_positively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_mellow_selected));
                img_positively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_excited));
                updateMood(User.MOOD_POSITIVELY_MELLOW);
            }
        });
        img_positively_excited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                STATE_FEELING = 4;
                txt_emotion_explaining.setText(R.string.positively_excited);
                img_negatively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_excited));
                img_negatively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_negatively_mellow));
                img_neutral.setImageDrawable(getResources().getDrawable(R.drawable.ic_neutral));
                img_positively_mellow.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_mellow));
                img_positively_excited.setImageDrawable(getResources().getDrawable(R.drawable.ic_positively_excited_selected));
                updateMood(User.MOOD_POSITIVELY_EXCITED);
            }
        });
    }

    private void updateMood(final int status) {
        Measurement lastMeasurement = MainScreenActivity.user.getLastMeasurement();
        if (lastMeasurement == null) {
            return;
        }

        lastMeasurement.setMood(status);
        User.updateMeasurement(getContext(), lastMeasurement, User.UPDATE_TYPE_BY_ID);
        FirebaseUtils.updateMeasurement(lastMeasurement);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (map_fragment != null && route_display_googlemap != null) {
            map_fragment.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (map_fragment != null && route_display_googlemap != null) {
            map_fragment.onDestroy();
        }
        Runtime.getRuntime().gc();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map_fragment != null && route_display_googlemap != null) {
            map_fragment.onPause();

        }

    }

}
