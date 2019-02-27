package com.mabe.productions.hrv_madison;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.mabe.productions.hrv_madison.measurements.Measurement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdvancedMeasurementHistoryActivity extends AppCompatActivity {

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

    private TextView advanced_history_txt_lf;
    private TextView advanced_history_txt_lf_value;
    private TextView advanced_history_txt_hf;
    private TextView advanced_history_txt_hf_value;


    //bpm cardview
    private LineChart bpm_line_chart;
    private CardView bpm_card;
    private TextView bpm_card_txt_bpm;
    private TextView bpm_card_txt_date;
    private TextView bpm_card_txt_average;
    private TextView bpm_card_value_average;
    private TextView bpm_card_txt_hrv_average_value;
    private TextView bpm_card_hrv_average_value;

    //How do you feel? cardview
    private CardView feeling_cardview;
    private TextView txt_how_do_you_feel;
    private TextView txt_emotion_explaining;
    private ImageView img_negatively_excited;
    private ImageView img_negatively_mellow;
    private ImageView img_neutral;
    private ImageView img_positively_mellow;
    private ImageView img_positively_excited;

    private TextView advanced_history_txt_rmssd;
    private TextView advanced_history_txt_rmssd_value;

    private TextView advanced_history_txt_lnrmssd;
    private TextView advanced_history_txt_lnrmssd_value;

    private TextView advanced_history_txt_hrv;
    private TextView advanced_history_txt_hrv_value;

    private TextView advanced_history_txt_bpm;
    private TextView advanced_history_txt_bpm_value;

    private TextView advanced_history_txt_card_date;
    private TextView advanced_history_txt_card_duration;
    private TextView advanced_history_txt_card_title;


    private ImageView img_back_arrow;
    private TextView toolbar_title_advanced;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_measurement_history);

        //Receiving data from RecyclerView and creating workout object
        HistoryRecyclerViewDataHolder parcel = getIntent().getExtras().getParcelable("Measurement");
        Measurement measurement = new Measurement(
                parcel.getDate()
                , parcel.getRmssd()
                , parcel.getLn_rmssd()
                , parcel.getLowest_rmssd()
                , parcel.getHighest_rmssd()
                , parcel.getLowest_bpm()
                , parcel.getHighest_bpm()
                , parcel.getAverage_bpm()
                , parcel.getLF_band()
                , parcel.getVLF_band()
                , parcel.getVHF_band()
                , parcel.getHF_band()
                , parcel.getBpm_data()
                , parcel.getRmssd_data()
                , parcel.getDuration()
                , parcel.getUnique_id()
                , parcel.getMood()
                , parcel.getHrv(),
                null);


        initialiseViews();
        setFonts();
        bpm_lineChart();
        addDataToBPMChart(measurement);
        setFrequencyChartData(measurement.getHF_band(), measurement.getLF_band(), measurement.getVLF_band(), measurement.getVHF_band());
        setUpData(measurement);
        setLF_HF_RatioZone(measurement.getLF_band() / measurement.getHF_band());
    }


    private void setUpData(Measurement measurement) {
        freq_card_txt_hf_after_measurament.setText(String.valueOf((int) measurement.getHF_band() + "%"));
        freq_card_txt_lf_after_measurement.setText(String.valueOf((int) measurement.getLF_band() + "%"));

        advanced_history_txt_lf_value.setText(String.valueOf((int) measurement.getLF_band() + "%"));
        advanced_history_txt_hf_value.setText(String.valueOf((int) measurement.getHF_band() + "%"));


        freq_card_txt_freq_band_date.setText(formatDate(measurement.getDate()));
        bpm_card_txt_date.setText(formatDate(measurement.getDate()));


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

        bpm_card_value_average.setText(String.valueOf((int) measurement.getAverage_bpm()));
        bpm_card_hrv_average_value.setText(String.valueOf(measurement.getRmssd()));


        advanced_history_txt_rmssd_value.setText(String.valueOf(measurement.getRmssd()));
        advanced_history_txt_lnrmssd_value.setText(String.valueOf(measurement.getLn_rmssd()));
        advanced_history_txt_hrv_value.setText(String.valueOf(measurement.getHrv()));
        advanced_history_txt_bpm_value.setText(String.valueOf((int) measurement.getAverage_bpm()));

        advanced_history_txt_card_date.setText(formatDate(measurement.getDate()));
        advanced_history_txt_card_duration.setText(measurement.getDuration() + " min measurement");

    }

    private String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, hh:mm 'val'");
        return dateFormat.format(cal.getTime());

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

    private void setFonts() {
        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");
        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/Verdana.ttf");


        //FrequencyCardView
        freq_card_txt_freq_band.setTypeface(verdana);
        freq_card_txt_freq_band_date.setTypeface(verdana);
        freq_card_txt_after_this_measure.setTypeface(verdana);
        freq_card_txt_hf_after_measurament.setTypeface(verdana);
        freq_card_txt_lf_after_measurement.setTypeface(verdana);


        txt_how_do_you_feel.setTypeface(verdana);
        txt_emotion_explaining.setTypeface(verdana);

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
        bpm_line_chart.getAxisLeft().setLabelCount(10, true);
        bpm_line_chart.getAxisLeft().setDrawLabels(true);
        bpm_line_chart.setAutoScaleMinMaxEnabled(true);
        bpm_line_chart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        bpm_line_chart.getAxisLeft().setTextColor(Color.WHITE);
        bpm_line_chart.setViewPortOffsets(0f, 0f, 0f, 0f);


    }

    private void initialiseViews() {

        Animation left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation left_to_right_d = AnimationUtils.loadAnimation(this, R.anim.left_to_right_delay);

        img_back_arrow = (ImageView) findViewById(R.id.img_back_arrow);
        toolbar_title_advanced = (TextView) findViewById(R.id.toolbar_title_advanced);
        img_back_arrow.startAnimation(left_to_right);
        toolbar_title_advanced.startAnimation(left_to_right_d);

        advanced_history_txt_card_title = (TextView) findViewById(R.id.advanced_history_txt_card_title);
        advanced_history_txt_card_duration = (TextView) findViewById(R.id.advanced_history_txt_card_duration);
        advanced_history_txt_card_date = (TextView) findViewById(R.id.advanced_history_txt_card_date);
        advanced_history_txt_rmssd = (TextView) findViewById(R.id.advanced_history_txt_rmssd);
        advanced_history_txt_rmssd_value = (TextView) findViewById(R.id.advanced_history_txt_rmssd_value);
        advanced_history_txt_lnrmssd = (TextView) findViewById(R.id.advanced_history_txt_lnrmssd);
        advanced_history_txt_lnrmssd_value = (TextView) findViewById(R.id.advanced_history_txt_lnrmssd_value);
        advanced_history_txt_hrv = (TextView) findViewById(R.id.advanced_history_txt_hrv);
        advanced_history_txt_hrv_value = (TextView) findViewById(R.id.advanced_history_txt_hrv_value);
        advanced_history_txt_bpm = (TextView) findViewById(R.id.advanced_history_txt_bpm);
        advanced_history_txt_bpm_value = (TextView) findViewById(R.id.advanced_history_txt_bpm_value);

        advanced_history_txt_lf = (TextView) findViewById(R.id.advanced_history_txt_lf);
        advanced_history_txt_lf_value = (TextView) findViewById(R.id.advanced_history_txt_lf_value);
        advanced_history_txt_hf = (TextView) findViewById(R.id.advanced_history_txt_hf);
        advanced_history_txt_hf_value = (TextView) findViewById(R.id.advanced_history_txt_hf_value);


        freq_card_txt_hf_after_measurament = (TextView) findViewById(R.id.freq_card_txt_hf_after_measurement);
        freq_card_txt_lf_after_measurement = (TextView) findViewById(R.id.freq_card_txt_lf_after_measurement);
        freq_card_ratio_meaning = (TextView) findViewById(R.id.freq_card_ratio_meaning);
        freq_card_ratio_meaning_advice = (TextView) findViewById(R.id.freq_card_ratio_meaning_advice);
        freq_card_ratio_scale = (FrequencyZoneView) findViewById(R.id.freq_card_ratio_scale);

        //Frequency PieChart
        freq_card = (CardView) findViewById(R.id.frequency_card);

        freq_card_txt_freq_band = (TextView) findViewById(R.id.frequency_bands_text_view);
        freq_card_txt_freq_band_date = (TextView) findViewById(R.id.frequency_bands_measurement_date);
        freq_card_txt_after_this_measure = (TextView) findViewById(R.id.freq_card_txt_after_this_measure);
        frequency_chart = (PieChart) findViewById(R.id.chart_frequencies);

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


        //BPM PieChart
        bpm_card = (CardView) findViewById(R.id.bpm_card);
        bpm_line_chart = (LineChart) findViewById(R.id.chart_bpm);
        bpm_card_txt_bpm = (TextView) findViewById(R.id.bpm_index_text_view);
        bpm_card_txt_date = (TextView) findViewById(R.id.bpm_index_measurement_date);
        bpm_card_txt_average = (TextView) findViewById(R.id.bpm_txt_average);
        bpm_card_value_average = (TextView) findViewById(R.id.bpm_value);
        bpm_card_txt_hrv_average_value = (TextView) findViewById(R.id.bpm_card_txt_hrv_average_value);
        bpm_card_hrv_average_value = (TextView) findViewById(R.id.bpm_card_hrv_average_value);

        //Feeling cardview
        feeling_cardview = (CardView) findViewById(R.id.feeling_card);
        txt_how_do_you_feel = (TextView) findViewById(R.id.txt_how_do_you_feel);
        txt_emotion_explaining = (TextView) findViewById(R.id.txt_emotion_explaining);
        img_negatively_excited = (ImageView) findViewById(R.id.img_negatively_excited);
        img_negatively_mellow = (ImageView) findViewById(R.id.img_negatively_mellow);
        img_neutral = (ImageView) findViewById(R.id.img_neutral);
        img_positively_mellow = (ImageView) findViewById(R.id.img_positively_mellow);
        img_positively_excited = (ImageView) findViewById(R.id.img_positively_excited);

        feeling_cardview.setTranslationY(Utils.getScreenHeight(this));
        feeling_cardview.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();

        bpm_card.setTranslationY(Utils.getScreenHeight(this));
        bpm_card.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();

        freq_card.setTranslationY(Utils.getScreenHeight(this));
        freq_card.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(1500)
                .start();
        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdvancedMeasurementHistoryActivity.this.finish();
            }
        });


    }

    private void setFrequencyChartData(float hf, float lf, float vlf, float vhf) {
        //Modify Y-axis value

        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(hf, "HF"));
        values.add(new PieEntry(lf, "LF"));
        values.add(new PieEntry(vlf, "VLF"));
        values.add(new PieEntry(vhf, "VHF"));

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
            set.setLineWidth(getResources().getDimension(R.dimen.line_width));
            set.setDrawValues(false);
            set.setDrawCircleHole(false);
            set.setDrawCircles(false);
            set.setCircleRadius(getResources().getDimension(R.dimen.circle_radius));
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

    private void addDataToBPMChart(Measurement measurement) {
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
        bpm_line_chart.animateY(2000, Easing.EasingOption.EaseInOutSine);
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
        rmssdSet.setCircleRadius(getResources().getDimension(R.dimen.circle_radius));
        rmssdSet.setCircleColor(Color.parseColor("#FFFFFF"));
        rmssdSet.setColor(Color.parseColor("#2ecc71"));
        rmssdSet.setDrawFilled(false);


        data.addDataSet(rmssdSet);

        bpm_line_chart.setData(data);


    }
}