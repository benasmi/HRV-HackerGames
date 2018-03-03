package com.mabe.productions.hrv_madison.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.channguyen.rsv.RangeSliderView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.measurements.FrequencyMethod;
import com.mabe.productions.hrv_madison.measurements.RMSSD;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MeasurementFragment extends Fragment {

    private NumberPicker measurement_duration;
    private TextView txt_hr;
    private TextView txt_hrv;
    private TextView txt_hr_value;
    private TextView txt_hrv_value;
    private AppCompatButton btn_start_measuring;

    private TextView txt_measurement_duration_comment;
    private LineChart chart_hr;

    private int[] interval_values;
    private int bpm;
    private int rmssd_value;

    private int lowest_bpm=1000;
    private int highest_bpm=0;

    private int lowest_rmssd=1000;
    private int highest_rmssd=0;

    private double LF = 0;
    private double HF = 0;
    private double VLF = 0;
    private double VHF = 0;


    private int times = 0;
    private int timePassed = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.measurement_fragment, container, false);
        initializeViews(view);
        setFonts();
        return view;
    }




    private void initializeViews(View view) {

        txt_measurement_duration_comment = view.findViewById(R.id.comment_measurement_duration);
        txt_hr = view.findViewById(R.id.txt_hr);
        txt_hr_value = view.findViewById(R.id.txt_hr_value);
        txt_hrv = view.findViewById(R.id.txt_hrv);
        txt_hrv_value = view.findViewById(R.id.txt_hrv_value);
        chart_hr = view.findViewById(R.id.hr_chart);
        //Customizing HR chart
        chart_hr.setData(new LineData());
        chart_hr.getLineData().setDrawValues(false);
        chart_hr.getLegend().setEnabled(false);
        chart_hr.getXAxis().setDrawAxisLine(false);
        chart_hr.getAxisRight().setDrawAxisLine(false);
        chart_hr.getAxisLeft().setDrawAxisLine(false);
        chart_hr.getXAxis().setDrawGridLines(false);
        chart_hr.getAxisLeft().setDrawGridLines(false);
        chart_hr.getAxisRight().setDrawGridLines(false);
        chart_hr.setDescription(null);
        chart_hr.getAxisLeft().setDrawLabels(false);
        chart_hr.getAxisRight().setDrawLabels(false);
        chart_hr.getXAxis().setDrawLabels(false);
        chart_hr.setTouchEnabled(false);
        chart_hr.setViewPortOffsets(0f, 0f, 0f, 0f);
        //chart_hr.setAutoScaleMinMaxEnabled(true);

        btn_start_measuring = view.findViewById(R.id.button_start_measuring);
        btn_start_measuring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePassed++;


                long duration = measurement_duration.getValue()*60000;
                final RMSSD hrv = new RMSSD();
                final FrequencyMethod fft = new FrequencyMethod();

                new CountDownTimer(duration,1000l){
                    @Override
                    public void onTick(long l) {

                        timePassed++;


                        for(int i = 0; i<interval_values.length; i++){
                            times++;
                            fft.add_to_freq_array(interval_values[i]);
                            if(times==16 || times == 64 || times==256){
                                Log.i("DATA",""+times);
                                fft.calculate_frequencies(times);

                            }


                        }


                        Log.i("DATA", "" + timePassed);
                        hrv.addIntervals(interval_values);
                        rmssd_value = hrv.calculateRMSSD();
                        txt_hrv_value.setText(String.valueOf(rmssd_value));

                        //Highest/Lowest BPM
                        highest_bpm = bpm>highest_bpm ? bpm : highest_bpm;
                        lowest_bpm = bpm<lowest_bpm ? bpm : lowest_bpm;

                        //Highest/Lowest RMSSD
                        highest_rmssd = rmssd_value> highest_rmssd ? rmssd_value : highest_rmssd;
                        lowest_rmssd = rmssd_value< lowest_rmssd ? rmssd_value : lowest_bpm;



                        if(timePassed==16 || timePassed==64 || timePassed==256 ){
                            rmssd_value = hrv.calculateRMSSD();

                            LF = fft.getLF_value();
                            HF = fft.getHF_value();
                            VLF = fft.getVLF_value();
                            VHF = fft.getVHF_value();

                            Log.i("DATA", "Highest BPM: " + highest_bpm + "|" +
                                    "Lowest BPM: " + lowest_bpm + "|"+
                                    "Highest HRV: " + highest_rmssd + "|"+
                                    "Lowest HRV: " + lowest_rmssd + "|"+
                                    "LF: " + LF + "|"+
                                    "HF: " + HF + "|"+
                                    "VLF : " + VLF + "|"+
                                    "VHF : " + VHF);

                        }

                    }

                    @Override
                    public void onFinish() {




                        Utils.Vibrate(getContext(),1000);



                    }
                }.start();

            }
        });

        measurement_duration = view.findViewById(R.id.number_picker);

        measurement_duration.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                switch (newVal) {
                    //TODO: change to string resources
                    case 1:
                        txt_measurement_duration_comment.setText("Netikslingi duomenys.");
                        break;

                    case 2:
                        txt_measurement_duration_comment.setText("Pastaba 2");
                        break;

                    case 3:
                        txt_measurement_duration_comment.setText("Pastaba 3");
                        break;

                    case 4:
                        txt_measurement_duration_comment.setText("Tikslūs dažnių duomenys");
                        break;
                }
            }
        });


    }

    private void addEntry(int hr) {

        LineData data = chart_hr.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            //Creating a line with single hr value
            ArrayList<Entry> singleValueList = new ArrayList<>();
            singleValueList.add(new Entry(0, hr));
            set = new LineDataSet(singleValueList, "HR");
            set.setLineWidth(getContext().getResources().getDimension(R.dimen.line_width));
            set.setDrawValues(false);
            set.setDrawCircleHole(false);
            set.setCircleRadius(getContext().getResources().getDimension(R.dimen.circle_radius));
            set.setCircleColor(Color.parseColor("#F62459"));
            set.setColor(Color.parseColor("#F62459"));
            set.setDrawFilled(true);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColors(new int[]{
                    Color.parseColor("#a6f62459"),
                    Color.TRANSPARENT
            });
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setSize(240, 160); // convert to dp
            set.setFillDrawable(drawable);
            data.addDataSet(set);
        } else {
            set.addEntry(new Entry(set.getEntryCount(), hr));
        }

        data.notifyDataChanged();
        chart_hr.notifyDataSetChanged();

        //chart_hr.setData(data);
        //chart_hr.animate();
        //chart_hr.moveViewToX(set.getEntryCount());
        chart_hr.setVisibleXRangeMaximum(6);
        chart_hr.setVisibleXRangeMinimum(6);
        //chart_hr.setAutoScaleMinMaxEnabled(true);

        chart_hr.moveViewToAnimated(set.getEntryCount(), chart_hr.getY(), YAxis.AxisDependency.RIGHT, 800l);

    }

    private void setFonts() {
        Typeface face_slogan = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/CORBEL.TTF");
        Typeface futura = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/futura_light.ttf");

        Typeface verdana = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Verdana.ttf");

        txt_measurement_duration_comment.setTypeface(verdana);
        txt_hr.setTypeface(futura);
        txt_hr_value.setTypeface(futura);
        txt_hrv.setTypeface(futura);
        txt_hrv_value.setTypeface(futura);
    }

    public void onMeasurement(int heartRate, int[] intervals) {
        bpm = heartRate;
        interval_values = intervals;

        txt_hr_value.setText("" + heartRate);
        addEntry(heartRate);

    }

}
