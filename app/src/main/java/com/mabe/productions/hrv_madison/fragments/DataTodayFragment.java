package com.mabe.productions.hrv_madison.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.measurements.Measurement;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;

import java.util.ArrayList;

//todo: card view animations and title snaping like 'prisiuk antraste'
public class DataTodayFragment extends Fragment {

    //FrequencyCardView
    private CardView freq_card;
    private TextView freq_card_txt_freq_band;
    private TextView freq_card_txt_freq_band_date;
    private TextView freq_card_txt_after_this_measure;
    private TextView freq_card_txt_hf_after_measurament;
    private TextView freq_card_txt_lf_after_measurement;
    private TextView freq_card_txt_vlf_after_measurement;
    private TextView freq_card_txt_norm;
    private TextView freq_card_txt_norm_hf;
    private TextView freq_card_txt_norm_vhf;
    private TextView freq_card_txt_norm_lf;
    private PieChart frequency_chart;


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



    //Daily reccomendation cardview
    private CardView recommendation_card;
    private TextView reccomendation_txt_todays_program;
    private TextView reccomendation_txt_hrv_increase;
    private TextView reccomendation_txt_duration;
    private TextView reccomendation_txt_pulse_zone;
    private TextView reccomendation_txt_verbal_recommendation;

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

    //Workout route cardview
    private CardView cardview_route;
    private SupportMapFragment map_fragment;
    private GoogleMap googlemap_route; //We may need this one in the future

    //First time layout
    private LinearLayout first_time_layout;
    private AppCompatButton first_time_btn;
    private TextView first_time_greeting;

    private boolean isFirstTime;

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
        frequency_pieChart();
        health_index_pieChart();
        bpm_lineChart();
        imageViewClickers();
        updateData();
        return view;

    }

    public void updateData(){

        //Checking if it's the first time
        isFirstTime = Utils.readFromSharedPrefs_string(getContext(), FeedReaderDbHelper.FIELD_LAST_MEASUREMENT_DATE, FeedReaderDbHelper.SHARED_PREFS_USER_DATA).equals("") ? true : false;

        if(isFirstTime){
            setMeasurementCardViewsVisibility(false);
            setWorkoutCardViewsVisibility(false);
        }else{
            first_time_layout.setVisibility(View.GONE);
        }

        User user = User.getUser(getContext());

        Measurement measurement = user.getLastMeasurement();
        if(measurement!=null){
            setMeasurementCardViewsVisibility(true);

            int bpmValues[] = measurement.getBpm_data();
            int rmssdValues[] = measurement.getRmssd_data();

            int maxGraphValue = bpmValues.length > rmssdValues.length ? bpmValues.length : rmssdValues.length;

            for(int i = 0; i<bpmValues.length; i++){
                if(i<=maxGraphValue){
                    addEntryBpm(bpmValues[i],maxGraphValue);

                }

            }

            for(int i = 0; i<rmssdValues.length; i++){
                if(i<=maxGraphValue){
                    addEntryRmssd(rmssdValues[i], maxGraphValue);

                }
            }

            freq_card_txt_hf_after_measurament.setText(String.valueOf(measurement.getHF_band()));
            freq_card_txt_lf_after_measurement.setText(String.valueOf(measurement.getHF_band()));
            freq_card_txt_vlf_after_measurement.setText(String.valueOf(measurement.getVHF_band()));
            bpm_card_hrv_average_value.setText(String.valueOf(measurement.getRmssd()));
            bpm_card_value_average.setText(String.valueOf((int)measurement.getAverage_bpm()));

            Log.i("TEST", "mood: " + measurement.getMood());

            //Setting initial mood
            switch(measurement.getMood()){
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

            reccomendation_txt_duration.setText(String.valueOf((int) user.getWorkoutDuration()) + " " + getString(
                                R.string.min));


            switch(user.getProgramUpdateState()){

                case User.PROGRAM_STATE_UPGRADED:

                    int percentageIncrease = Math.round((user.getHrvChangePercentage() - 1)*100);
                    reccomendation_txt_hrv_increase.setText("+ " + percentageIncrease + "%\nincrease" );
                    reccomendation_txt_hrv_increase.setTextColor(Color.parseColor("#2ecc71"));
                    reccomendation_txt_pulse_zone.setText(user.getPulseZone() + " pulse zone");

                    break;

                case User.PROGRAM_STATE_DOWNGRADED:

                    int percentageDecrease = Math.round((1 - user.getHrvChangePercentage())*100);
                    reccomendation_txt_hrv_increase.setText("+ " + percentageDecrease + "%\ndecrease" );
                    reccomendation_txt_hrv_increase.setTextColor(Color.parseColor("#e74c3c"));
                    reccomendation_txt_pulse_zone.setText(user.getPulseZone() + " pulse zone");

                    break;

                case User.PROGRAM_STATE_UNCHANGED:



                    break;



            }





        }

        final WorkoutMeasurements workout = user.getLastWorkout();

        if(workout != null){
            //setWorkoutCardViewsVisibility(false);
            //todo: populate views with data
//            if(getArguments() == null){
//                Log.i("TEST", "arguments null");
//            }
//
//            if(map_fragment == null){
//                Log.i("TEST", "map fragment null");
//            }
            /*
            if(googlemap_route == null){
                map_fragment.onCreate(getArguments());
                map_fragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        DataTodayFragment.this.googlemap_route = googleMap;
                        map_fragment.getView().setClickable(false);
                        //Instantiates a new Polyline object and adds points to define a rectangle
                        PolylineOptions lineOptions = new PolylineOptions()
                                .width(5)
                                .color(getResources().getColor(R.color.colorAccent))
                                .geodesic(false);

                        //This will zoom the map to our polyline
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        for(LatLng point : workout.getRoute()){
                            lineOptions.add(point);
                            builder.include(point);
                        }
                        googlemap_route.addPolyline(lineOptions);

                        LatLngBounds bounds = builder.build();
                        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                        googlemap_route.animateCamera(cu);

                    }
                });
            }else{
                //Instantiates a new Polyline object and adds points to define a rectangle
                PolylineOptions lineOptions = new PolylineOptions()
                        .width(5)
                        .color(getResources().getColor(R.color.colorAccent))
                        .geodesic(false);

                //This will zoom the map to our polyline
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for(LatLng point : workout.getRoute()){
                    lineOptions.add(point);
                    builder.include(point);
                }
                googlemap_route.addPolyline(lineOptions);

                LatLngBounds bounds = builder.build();
                final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                googlemap_route.animateCamera(cu);
            }

        */
        }

        bpm_line_chart.animateY(2000, Easing.EasingOption.EaseInOutSine);

    }

    private void setMeasurementCardViewsVisibility(boolean visibility){
        recommendation_card.setVisibility(visibility ? View.VISIBLE : View.GONE);
        freq_card.setVisibility(visibility ? View.VISIBLE : View.GONE);
        bpm_card.setVisibility(visibility ? View.VISIBLE : View.GONE);
        feeling_cardview.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
    private void setWorkoutCardViewsVisibility(boolean visibility){

        cardview_route.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }


    private void initializeViews(View view){

        //Frequency PieChart
        freq_card = view.findViewById(R.id.frequency_card);
        freq_card_txt_freq_band = view.findViewById(R.id.frequency_bands_text_view);
        freq_card_txt_freq_band_date  = view.findViewById(R.id.frequency_bands_measurement_date);
        freq_card_txt_after_this_measure = view.findViewById(R.id.freq_card_txt_after_this_measure);
        freq_card_txt_hf_after_measurament = view.findViewById(R.id.freq_card_txt_hf_after_measurement);
        freq_card_txt_lf_after_measurement = view.findViewById(R.id.freq_card_txt_lf_after_measurement);
        freq_card_txt_vlf_after_measurement = view.findViewById(R.id.freq_card_txt_vlf_after_measurement);
        freq_card_txt_norm = view.findViewById(R.id.freq_card_txt_norm);
        freq_card_txt_norm_hf = view.findViewById(R.id.freq_card_norm_hf);
        freq_card_txt_norm_vhf = view.findViewById(R.id.freq_card_norm_lf);
        freq_card_txt_norm_lf = view.findViewById(R.id.freq_card_norm_vlf);
        frequency_chart = view.findViewById(R.id.chart_frequencies);
        health_index_chart = view.findViewById(R.id.chart_health_index);

        //HRV PieChart
        hrv_card_txt_hrv = (TextView) view.findViewById(R.id.health_index_text_view);
        hrv_card_txt_hrv_band_date = (TextView) view.findViewById(R.id.health_index_measurement_date);
        hrv_card_txt_bpm= (TextView)view. findViewById(R.id.hrc_card_txt_bpm);
        hrv_card_txt_bpm_after_measurament= (TextView) view.findViewById(R.id.hrc_card_txt_average_bpm);
        hrv_card_txt_bpm_norm_after_measurement= (TextView) view.findViewById(R.id.hrc_card_txt_norm_bpm);
        hrv_card_txt_hrv_after= (TextView) view.findViewById(R.id.hrc_card_txt_hrv);
        hrv_card_txt_average_hrv= (TextView)view.findViewById(R.id.hrc_card_txt_average_hrv);
        hrv_card_txt_average_norm_hrv= (TextView) view.findViewById(R.id.hrc_card_txt_norm_hrv);

        //BPM PieChart
        bpm_card = (CardView) view.findViewById(R.id.bpm_card);
        bpm_line_chart = (LineChart) view.findViewById(R.id.chart_bpm);
        bpm_card_txt_bpm = (TextView) view.findViewById(R.id.bpm_index_text_view);
        bpm_card_txt_date = (TextView) view.findViewById(R.id.bpm_index_measurement_date);
        bpm_card_txt_average = (TextView) view.findViewById(R.id.bpm_txt_average);
        bpm_card_value_average = (TextView) view.findViewById(R.id.bpm_value);
        bpm_card_txt_hrv_average_value = (TextView) view.findViewById(R.id.bpm_card_txt_hrv_average_value);
        bpm_card_hrv_average_value = (TextView) view.findViewById(R.id.bpm_card_hrv_average_value);


        //Reccomendation cardview
        recommendation_card = view.findViewById(R.id.recommendation_card);
        reccomendation_txt_todays_program = view.findViewById(R.id.txt_todays_program);
        reccomendation_txt_hrv_increase = view.findViewById(R.id.txt_hrv_increase);
        reccomendation_txt_duration = view.findViewById(R.id.txt_duration_value);
        reccomendation_txt_pulse_zone = view.findViewById(R.id.txt_pulse_zone_value);
        reccomendation_txt_verbal_recommendation = view.findViewById(R.id.txt_verbal_recomendation);

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
/*        FragmentManager fm = getActivity().getSupportFragmentManager();
        CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(54.6,25.27)) //Just a random starting point. Will be changed in reloadData()
                .zoom(1f)
                .build();
*/        //map_fragment = SupportMapFragment.newInstance(new GoogleMapOptions().camera(cp));
        //fm.beginTransaction().replace(R.id.workout_route, map_fragment).commit();

        cardview_route = view.findViewById(R.id.cardview_route);

        feeling_cardview.setTranslationY( Utils.getScreenHeight(getContext()));
        feeling_cardview.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();

        recommendation_card.setTranslationY( Utils.getScreenHeight(getContext()));
        recommendation_card.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();

        //First time cardview
        first_time_btn = view.findViewById(R.id.first_time_measure_button);
        first_time_layout = view.findViewById(R.id.first_time_layout);
        first_time_greeting = view.findViewById(R.id.first_time_greeting);
        first_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setViewpagerTab(getActivity(), 0);
            }
        });
        Animation anim_btn = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom_delay);
        Animation anim_txt = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        first_time_btn.startAnimation(anim_btn);
        first_time_greeting.startAnimation(anim_txt);




    }



    private void setFonts(){
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
        freq_card_txt_vlf_after_measurement.setTypeface(verdana);
        freq_card_txt_norm.setTypeface(verdana);
        freq_card_txt_norm_hf.setTypeface(verdana);
        freq_card_txt_norm_vhf.setTypeface(verdana);
        freq_card_txt_norm_lf.setTypeface(verdana);


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

        //Reccomendation cardview
        reccomendation_txt_todays_program.setTypeface(verdana);
        reccomendation_txt_hrv_increase.setTypeface(verdana);
        reccomendation_txt_duration.setTypeface(verdana);
        reccomendation_txt_pulse_zone.setTypeface(verdana);
        reccomendation_txt_verbal_recommendation.setTypeface(verdana);

        //First time cardview
        first_time_greeting.setTypeface(verdana);

    }

    private void frequency_pieChart(){

        //Casual modifications
        frequency_chart.setUsePercentValues(true);
        frequency_chart.setDrawSliceText(false);
        frequency_chart.getDescription().setEnabled(false);

        //Space inside chart and color
        frequency_chart.setTransparentCircleRadius(50f);
        frequency_chart.setHoleColor(Color.TRANSPARENT);
        frequency_chart.setHoleRadius(80f);
        frequency_chart.setCenterText("VLF/LF/HF");
        frequency_chart.setCenterTextSize(20f);
        frequency_chart.setCenterTextColor(Color.WHITE);


        //Remove X-axis values
        frequency_chart.setDrawEntryLabels(false);

        //Animate pieChart
        frequency_chart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(65f,"BPM"));
        values.add(new PieEntry(50f,"BPM"));
        values.add(new PieEntry(20f,"BPM"));

        //Modify Y-axis value
        final PieDataSet dataSet = new PieDataSet(values,"Frequencies");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(3f);
        dataSet.setColors(new int[]{Color.parseColor("#e74c3c"), Color.parseColor("#2980b9"), Color.parseColor("#8e44ad")});
        dataSet.setDrawValues(false);

        //Modify Data looks
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        //Setting data
        frequency_chart.setData(data);

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

    private void bpm_lineChart(){
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

    private void health_index_pieChart(){

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
        values.add(new PieEntry(30f,"BPM"));
        values.add(new PieEntry(70f,"asfBPM"));

        //Modify Y-axis value
        final PieDataSet dataSet = new PieDataSet(values,"Frequencies");
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

    private void imageViewClickers(){
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

    private void updateMood(final int status){
        Measurement lastMeasurement = User.getUser(getContext()).getLastMeasurement();
        if(lastMeasurement == null){
            return;
        }

        lastMeasurement.setMood(status);
        User.updateMeasurement(getContext(), lastMeasurement, User.UPDATE_TYPE_BY_ID);
    }

}
