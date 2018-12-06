package com.mabe.productions.hrv_madison.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mabe.productions.hrv_madison.FingerHRV.HeartRateMonitor;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.bluetooth.BluetoothGattService;
import com.mabe.productions.hrv_madison.bluetooth.LeDevicesDialog;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebase.FireMeasurement;
import com.mabe.productions.hrv_madison.firebase.FirebaseUtils;
import com.mabe.productions.hrv_madison.measurements.BPM;
import com.mabe.productions.hrv_madison.measurements.FrequencyMethod;
import com.mabe.productions.hrv_madison.measurements.Measurement;
import com.mabe.productions.hrv_madison.measurements.RMSSD;
import com.shawnlin.numberpicker.NumberPicker;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MeasurementFragment extends Fragment {

    public static final int MY_CAMERA_REQUEST_CODE = 5;
    private NumberPicker measurement_duration;
    private TextView txt_hr;
    private TextView txt_hrv;
    private TextView txt_hr_value;
    private ImageView imgButton_view_hrv_finger_info;
    private TextView txt_hrv_value;
    public TextView txt_connection_status;
    private AppCompatButton btn_start_measuring;
    private ProgressBar progressbar_measurement;
    private ImageView img_breathing_indicator;
    private LineChart chart_hr;
    private TextView txt_time_left;
    private MediaPlayer mediaPlayer;
    private TextView txt_line_chart_label;
    private TextView measure_with_camera;

    private int failureIntervalTimes = 0;

    public Tooltip infoHrvFinger = null;
    private int[] interval_values;

    private User user;
    //Calculation objects
    final RMSSD hrv = new RMSSD();
    final FrequencyMethod fft = new FrequencyMethod();
    final BPM bpm = new BPM();

    private int times = 0;
    private int timePassed = 0;
    private CountDownTimer countDownTimer = null;

    public boolean shouldStartMeasurementImmediately = false;

    private int currentMeasurementState = STATE_WAITING_TO_MEASURE;

    private static final int STATE_MEASURING = 0;
    private static final int STATE_WAITING_TO_MEASURE = 1;
    private static final int STATE_REVIEW_DATA = 2;
    private int hearRate=0;
    private AnimatedVectorDrawableCompat animatedBreathingVector;
    private TextView txt_duration_picker_text;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.measurement_fragment, container, false);
        //Initializing the mediaplayer in this method, so that we don't need to reload the sound later
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.measurement_notification);
        initializeViews(view);
        setFonts();
        return view;
    }




    private void initializeViews(View view) {
        img_breathing_indicator = view.findViewById(R.id.breathing_indicator);
        txt_line_chart_label = view.findViewById(R.id.txt_line_chart_label);
        txt_line_chart_label.setVisibility(View.INVISIBLE);
        measure_with_camera = view.findViewById(R.id.measure_with_camera);
        //Initializing animated vector drawable only once
        animatedBreathingVector = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.breathing_indicator_anim);
        //A weird way to make the animation loop. I could not find a better one
        animatedBreathingVector.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                //Yes, using this method is somewhy nessecary
                img_breathing_indicator.post(new Runnable() {
                    @Override
                    public void run() {
                        animatedBreathingVector.start();
                    }
                });
            }
        });
        img_breathing_indicator.setImageDrawable(animatedBreathingVector);
        imgButton_view_hrv_finger_info = view.findViewById(R.id.imgButton_view_hrv_finger_info);
        txt_time_left = view.findViewById(R.id.txt_duration_left);
        txt_connection_status = view.findViewById(R.id.txt_connection_status);
        txt_hr = view.findViewById(R.id.txt_hr);
        txt_hr_value = view.findViewById(R.id.txt_hr_value);
        txt_hrv = view.findViewById(R.id.txt_hrv);
        txt_hrv_value = view.findViewById(R.id.txt_hrv_value);
        txt_duration_picker_text = view.findViewById(R.id.duration_picker_min_txt);
        chart_hr = view.findViewById(R.id.hr_chart);
        //Customizing HR chart
        chart_hr.setData(new LineData());
        chart_hr.getLineData().setDrawValues(false);
        chart_hr.getLegend().setEnabled(false);
        chart_hr.getXAxis().setDrawAxisLine(false);
        chart_hr.getAxisRight().setDrawAxisLine(false);
        chart_hr.getAxisLeft().setDrawAxisLine(false);
        chart_hr.getAxisLeft().setDrawGridLines(false);
        chart_hr.getXAxis().setDrawGridLines(false);
        chart_hr.getAxisRight().setDrawGridLines(false);
        chart_hr.setDescription(null);
        chart_hr.getAxisLeft().setDrawLabels(false);
        chart_hr.getAxisRight().setDrawLabels(false);
        chart_hr.getXAxis().setDrawLabels(false);
        chart_hr.setTouchEnabled(false);
        chart_hr.setViewPortOffsets(0f, 0f, 0f, 0f);
        //chart_hr.setAutoScaleMinMaxEnabled(true);


        imgButton_view_hrv_finger_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (infoHrvFinger == null) {
                    infoHrvFinger = new Tooltip.Builder(view)
                            .setText("From now on you can measure HRV using your phone camera. Keep in mind that measurements with sensors provide more accurate and reasonable data!!!")
                            .setDismissOnClick(true)
                            .setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent))
                            .setTextColor(getActivity().getResources().getColor(R.color.white))
                            .setCornerRadius(7f)
                            .setGravity(Gravity.TOP)
                            .show();

                }

                if (!infoHrvFinger.isShowing()) {
                    infoHrvFinger.show();
                }
            }
        });

        measure_with_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MeasurementFragment.this.getContext(), android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);
                }else{
                    startActivity(new Intent(MeasurementFragment.this.getContext(), HeartRateMonitor.class));
                }


            }
        });

        btn_start_measuring = view.findViewById(R.id.button_start_measuring);

        btn_start_measuring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cia tiesiog tuos state patikrinti ir paziureti, ka tas mygtukas turi daryti dabar is vienos is triju galimybiu
                switch(currentMeasurementState){

                    case STATE_MEASURING:
                        cancelMeasurement();
                        break;

                    case STATE_WAITING_TO_MEASURE:

                        Date lastMeasurementDate = Utils.getDateFromString(Utils.readFromSharedPrefs_string(getContext(), FeedReaderDbHelper.FIELD_LAST_MEASUREMENT_DATE, FeedReaderDbHelper.SHARED_PREFS_USER_DATA));

                        boolean hasMeasuredToday = false;

                        if(lastMeasurementDate != null){
                            Calendar calendar = Calendar.getInstance();
                            int today = calendar.get(Calendar.DAY_OF_YEAR);
                            calendar.setTime(lastMeasurementDate);
                            int measurementDay = calendar.get(Calendar.DAY_OF_YEAR);
                            hasMeasuredToday = today == measurementDay ? true : false;

                        }else{
                            //user is about to measure for the first time. We may want to add a tutorial or something
                        }


                        if(hasMeasuredToday){

                            Utils.buildAlertDialogPrompt(
                                    getContext(),
                                    getString(R.string.please_wait),
                                    getString(R.string.already_measured_today_message),
                                    getString(R.string.measure),
                                    getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            //if device is connected
                                            if(BluetoothGattService.isGattDeviceConnected){
                                                startMeasuring();
                                            }else{
                                                autoConnectDevice();
                                            }
                                        }
                                    },
                                    null
                            );


                        }else{
                            //if device is connected
                            if(BluetoothGattService.isGattDeviceConnected){
                                startMeasuring();
                            }else{
                                autoConnectDevice();
                            }
                        }


                        break;

                    case STATE_REVIEW_DATA:
                        currentMeasurementState = STATE_WAITING_TO_MEASURE;
                        cancelMeasurement();

                        //todo: use static viewpager variable
                        //Switching to today's tab and updating data

                        ViewPager parentViewPager = getActivity().findViewById(R.id.viewpager);
                        ViewPagerAdapter adapter = (ViewPagerAdapter) parentViewPager.getAdapter();
                        adapter.dataTodayFragment.updateData(getContext());
                        adapter.workoutFragment.updateData();
                        parentViewPager.setCurrentItem(1);
                        break;
                }

            }
        });

        measurement_duration = view.findViewById(R.id.number_picker);
        progressbar_measurement = view.findViewById(R.id.measurement_progress_bar);
        progressbar_measurement.setMax(100);

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
            drawable.setSize(240, 160);
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


        txt_hr.setTypeface(futura);
        txt_hr_value.setTypeface(futura);
        txt_hrv.setTypeface(futura);
        txt_connection_status.setTypeface(futura);
        txt_hrv_value.setTypeface(futura);
        txt_line_chart_label.setTypeface(futura);
    }

    //Method to receive data from GAT SERVER if connected.
    public void onMeasurement(int heartRate, int[] intervals) {
        this.hearRate = heartRate;
        interval_values = intervals;


        if(intervals.length==0){
            failureIntervalTimes++;
            Log.i("TEST", "FailureIntervals: " +failureIntervalTimes);
        }
        if(failureIntervalTimes==10){

            cancelMeasurement();
            Utils.buildAlertDialogPrompt(MeasurementFragment.this.getContext(), "R-R intervals Missing", "The app is not currently receiving R-R intervals from your heart rate monitor. For accurate HRV calculations it is important to receive consistent and accurate R-R intervals from the monitor", "Dismiss", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            },null);

        }



        txt_hr_value.setText("" + heartRate);
        addEntry(heartRate);


    }

    public void startMeasuring(){
        Crashlytics.log("Starting the measurement.");
        failureIntervalTimes=0;
        currentMeasurementState = STATE_MEASURING;
        txt_connection_status.setText(R.string.measuring);
        btn_start_measuring.setText(R.string.cancel);
        measurement_duration.setEnabled(false);

        MainScreenActivity.setDisplayOnLockscreen(true, getActivity());

        measurement_duration.setVisibility(View.GONE);
        imgButton_view_hrv_finger_info.setVisibility(View.GONE);
        txt_duration_picker_text.setVisibility(View.GONE);
        img_breathing_indicator.setVisibility(View.VISIBLE);

        //Again, a weird way to restart the animation
        Animation top_to_bottom = AnimationUtils.loadAnimation(getContext(),R.anim.top_to_bottom);
        Animation left_to_right = AnimationUtils.loadAnimation(getContext(),R.anim.right_to_left);

        img_breathing_indicator.startAnimation(top_to_bottom);
        txt_line_chart_label.setVisibility(View.VISIBLE);
        measure_with_camera.setVisibility(View.GONE);
        txt_line_chart_label.startAnimation(left_to_right);
        img_breathing_indicator.post(new Runnable() {
            @Override
            public void run() {
                animatedBreathingVector.start();

            }
        });

                countDownTimer = new CountDownTimer(measurement_duration.getValue()*60000,1000l){
                    @Override
                    public void onTick(long l) {
                        Crashlytics.setLong("measurement_time_passed", l);

                        hrv.calculateRMSSD();
                        timePassed++;



                        if(interval_values == null){
                            return;
                        }

                        //Calculating FrequencyDomainMethod: 'times' has to be power of 2
                        for(int i = 0; i<interval_values.length; i++){
//                            times++;
                            fft.add_to_freq_array(interval_values[i]);
//                            if(times==16 || times == 64 || times==256){
//                                Log.i("DATA",""+times);
//                                fft.calculate_frequencies(times);
//
//                            }
                        }

                        long duration = measurement_duration.getValue()*60000;

                        progressbar_measurement.setProgress((int) ( ( timePassed*1000d / (double) duration) * 100d));

                        int minutes = (int) l/60000;
                        int seconds = Math.round(l/1000 - (minutes*60));

                        if(seconds < 10){
                            txt_time_left.setText(minutes + ":0" + seconds);
                        }else{
                            txt_time_left.setText(minutes + ":" + seconds);
                        }


                        //Calculating HRV
                        hrv.addIntervals(interval_values);

                        for(int interval : interval_values){
                            Log.i("RRTEST", String.valueOf(interval));
                        }
                        bpm.addBPM(hearRate);
                        //Seting values
                        txt_hrv_value.setText(String.valueOf(hrv.getHrv()));
                    }

                    @Override
                    public void onFinish() {
                        Crashlytics.log("Measurement is finished.");

                        //Playing the ending sound
                        mediaPlayer.start();

                        Measurement measurement = new Measurement(hrv, fft, bpm, measurement_duration.getValue(), Calendar.getInstance().getTime());

                        //Saving to local db
                        Crashlytics.log("Saving measurement locally.");
                        User.addMeasurementData(getContext(), measurement, true);
                        //Saving to remote db
                        Crashlytics.log("Saving measurement remotely");
                        FirebaseUtils.addMeasurement(new FireMeasurement(measurement), getContext());


                        Calendar calendar = Calendar.getInstance();
                        String todayInString = Utils.getStringFromDate(calendar.getTime());

                        Utils.vibrate(getContext(), 1000);
                        Utils.saveToSharedPrefs(getContext(), FeedReaderDbHelper.FIELD_LAST_MEASUREMENT_DATE, todayInString, FeedReaderDbHelper.SHARED_PREFS_USER_DATA);

                        txt_connection_status.setText(R.string.measurement_is_over);
                        currentMeasurementState = STATE_REVIEW_DATA;
                        btn_start_measuring.setText(R.string.review_btn);
                        txt_time_left.setText("");

                    }
                }.start();
    }


    //TODO: change text to measuring.... while measuring
    public void disconnected(){
        currentMeasurementState = STATE_WAITING_TO_MEASURE;
        cancelMeasurement();
        txt_connection_status.setText(R.string.failed_connection_status);
        txt_hr_value.setText("-");
        btn_start_measuring.setText(R.string.measure_btn);

    }


    //Connection to bt device and gatt server
    private void autoConnectDevice(){

        String MAC_adress = Utils.readFromSharedPrefs_string(getContext(), FeedReaderDbHelper.BT_FIELD_MAC_ADRESS,FeedReaderDbHelper.SHARED_PREFS_DEVICES);
        String device_name = Utils.readFromSharedPrefs_string(getContext(),FeedReaderDbHelper.BT_FIELD_DEVICE_NAME,FeedReaderDbHelper.SHARED_PREFS_DEVICES);


        //If there is saved device --> connect
        if(!MAC_adress.equals("") && Utils.isBluetoothEnabled()){
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(MAC_adress);
            shouldStartMeasurementImmediately = true;
            getContext().startService(new Intent(getContext(), BluetoothGattService.class).putExtra("device",device));
            txt_connection_status.setText(getString(R.string.connecting_to) + " " + device_name);
        }else{

            //If there is no saved device --> add one
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=  PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MainScreenActivity.PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }else{


                if(Utils.isBluetoothEnabled()){
                    shouldStartMeasurementImmediately = true;
                    LeDevicesDialog dialog = new LeDevicesDialog(getContext());
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if(!BluetoothGattService.isGattDeviceConnected){
                                shouldStartMeasurementImmediately = false;
                            }
                        }
                    });

                }else{
                    Toast.makeText(getContext(), "Please enable bluetooth!", Toast.LENGTH_LONG).show(); //TODO: add a nice dialog or something
                }
            }
        }

    }

    private void cancelMeasurement(){
        Crashlytics.log("Measurement is cancelled.");

        currentMeasurementState = STATE_WAITING_TO_MEASURE;
        MainScreenActivity.setDisplayOnLockscreen(false, getActivity());
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        hrv.clear();
        bpm.clear();;
        fft.clearData();
        measurement_duration.setEnabled(true);
        btn_start_measuring.setText(R.string.measure_btn);
        txt_connection_status.setText("");
        progressbar_measurement.setProgress(0);
        txt_hrv_value.setText("-");
        timePassed = 0;
        times=0;
        txt_time_left.setText("");
        measurement_duration.setVisibility(View.VISIBLE);
        measure_with_camera.setVisibility(View.VISIBLE);
        imgButton_view_hrv_finger_info.setVisibility(View.VISIBLE);
        img_breathing_indicator.setVisibility(View.GONE);
        txt_duration_picker_text.setVisibility(View.GONE);

    }



}