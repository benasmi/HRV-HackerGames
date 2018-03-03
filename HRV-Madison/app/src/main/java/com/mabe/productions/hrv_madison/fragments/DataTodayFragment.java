package com.mabe.productions.hrv_madison.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mabe.productions.hrv_madison.R;

import java.util.ArrayList;


public class DataTodayFragment extends Fragment {

    //FrequencyCardView
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


    //HrvCardView
    private TextView hrv_card_txt_hrv;
    private TextView hrv_card_txt_hrv_band_date;
    private TextView hrv_card_txt_bpm;
    private TextView hrv_card_txt_bpm_after_measurament;
    private TextView hrv_card_txt_bpm_norm_after_measurement;
    private TextView hrv_card_txt_hrv_after;
    private TextView hrv_card_txt_average_hrv;
    private TextView hrv_card_txt_average_norm_hrv;

    private PieChart frequency_chart;
    private PieChart health_index_chart;
    private PieChart hrv_chart;


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

        return view;

    }



    private void initializeViews(View view){

        //Frequency PieChart
        freq_card_txt_freq_band = (TextView) view.findViewById(R.id.frequency_bands_text_view);
        freq_card_txt_freq_band_date  = (TextView) view.findViewById(R.id.frequency_bands_measurement_date);
        freq_card_txt_after_this_measure = (TextView) view.findViewById(R.id.freq_card_txt_after_this_measure);
        freq_card_txt_hf_after_measurament = (TextView) view.findViewById(R.id.freq_card_txt_hf_after_measurement);
        freq_card_txt_lf_after_measurement = (TextView) view.findViewById(R.id.freq_card_txt_lf_after_measurement);
        freq_card_txt_vlf_after_measurement = (TextView) view.findViewById(R.id.freq_card_txt_vlf_after_measurement);
        freq_card_txt_norm = (TextView) view.findViewById(R.id.freq_card_txt_norm);
        freq_card_txt_norm_hf = (TextView) view.findViewById(R.id.freq_card_norm_hf);
        freq_card_txt_norm_vhf = (TextView) view.findViewById(R.id.freq_card_norm_lf);
        freq_card_txt_norm_lf = (TextView) view.findViewById(R.id.freq_card_norm_vlf);
        frequency_chart = (PieChart) view.findViewById(R.id.chart_frequencies);
        health_index_chart = (PieChart) view.findViewById(R.id.chart_health_index);

        //HRV PieChart
        hrv_card_txt_hrv = (TextView) view.findViewById(R.id.health_index_text_view);
        hrv_card_txt_hrv_band_date = (TextView) view.findViewById(R.id.health_index_measurement_date);
        hrv_card_txt_bpm= (TextView)view. findViewById(R.id.hrc_card_txt_bpm);
        hrv_card_txt_bpm_after_measurament= (TextView) view.findViewById(R.id.hrc_card_txt_average_bpm);
        hrv_card_txt_bpm_norm_after_measurement= (TextView) view.findViewById(R.id.hrc_card_txt_norm_bpm);
        hrv_card_txt_hrv_after= (TextView) view.findViewById(R.id.hrc_card_txt_hrv);
        hrv_card_txt_average_hrv= (TextView)view.findViewById(R.id.hrc_card_txt_average_hrv);
        hrv_card_txt_average_norm_hrv= (TextView) view.findViewById(R.id.hrc_card_txt_norm_hrv);
    }



    private void setFonts(){
        Typeface futura = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/futura_light.ttf");
        Typeface corbel = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Corbel Bold.ttf");

        frequency_chart.setCenterTextTypeface(futura);
        health_index_chart.setCenterTextTypeface(futura);


        //FrequencyCardView
        freq_card_txt_freq_band.setTypeface(futura);
        freq_card_txt_freq_band_date.setTypeface(futura);
        freq_card_txt_after_this_measure.setTypeface(futura);
        freq_card_txt_hf_after_measurament.setTypeface(futura);
        freq_card_txt_lf_after_measurement.setTypeface(futura);
        freq_card_txt_vlf_after_measurement.setTypeface(futura);
        freq_card_txt_norm.setTypeface(futura);
        freq_card_txt_norm_hf.setTypeface(futura);
        freq_card_txt_norm_vhf.setTypeface(futura);
        freq_card_txt_norm_lf.setTypeface(futura);


        //HrvCardView
        hrv_card_txt_hrv.setTypeface(futura);
        hrv_card_txt_hrv_band_date.setTypeface(futura);
        hrv_card_txt_bpm.setTypeface(futura);
        hrv_card_txt_bpm_after_measurament.setTypeface(futura);
        hrv_card_txt_bpm_norm_after_measurement.setTypeface(futura);
        hrv_card_txt_hrv_after.setTypeface(futura);
        hrv_card_txt_average_hrv.setTypeface(futura);
        hrv_card_txt_average_norm_hrv.setTypeface(futura);
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

    private void hrv_pieChart(){

        //Casual modifications
        hrv_chart.setUsePercentValues(true);
        hrv_chart.setDrawSliceText(false);
        hrv_chart.getDescription().setEnabled(false);

        //Space inside chart and color
        hrv_chart.setTransparentCircleRadius(50f);
        hrv_chart.setHoleColor(Color.TRANSPARENT);
        hrv_chart.setHoleRadius(60f);
        hrv_chart.setCenterText("HRV");
        hrv_chart.setCenterTextSize(20f);
        hrv_chart.setCenterTextColor(Color.WHITE);


        //Remove X-axis values
        hrv_chart.setDrawEntryLabels(false);

        //Animate pieChart
        hrv_chart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(65f,"BPM"));

        //Modify Y-axis value
        final PieDataSet dataSet = new PieDataSet(values,"Frequencies");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(3f);
        dataSet.setColor(Color.parseColor("#22A7F0"));
        dataSet.setDrawValues(false);

        //Modify Data looks
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        //Setting data
        hrv_chart.setData(data);

        //Modify data
        Legend legend = hrv_chart.getLegend();
        legend.setEnabled(false);

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



}
