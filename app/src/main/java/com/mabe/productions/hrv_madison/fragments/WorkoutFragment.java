package com.mabe.productions.hrv_madison.fragments;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.bluetooth.BluetoothGattService;
import com.mabe.productions.hrv_madison.bluetooth.LeDevicesDialog;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class WorkoutFragment extends Fragment {

    private static final long VIBRATE_DURATION_TIME_ENDED = 1000l;
    private static final long VIBRATE_DURATION_CONNECTION_LOST = 5000l;

    private CircularProgressBar progressbar_duration;
    private TextView txt_calories_burned;
    private TextView txt_current_pace;
    private TextView txt_distance;
    private TextView txt_bpm;
    private EditText editText_seconds;
    private EditText editText_minutes;
    public TextView txt_connection_status;
    private AppCompatButton btn_toggle;
    private ImageView img_pause;
    private ImageView img_stop;
    private LinearLayout layout_workout_progress;

    private Timer timer = null;


    public boolean shouldStartWorkoutImmediately = false;

    private static final long TIMER_STEP = 1000;

    public static final int STATE_BEFORE_WORKOUT = 0;
    public static final int STATE_WORKING_OUT = 1;
    public static final int STATE_TIME_ENDED = 2;
    public static final int STATE_PAUSED = 3;

    private long timePassed = 0;
    private long userSpecifiedWorkoutDuration = 0;
    private boolean isTimerRunning = false;

    int workout_state = STATE_BEFORE_WORKOUT;
    private boolean isLocationListeningEnabled= false;

    private float calories_burned = 0;


    private ArrayList<LatLng> route = new ArrayList<>();
    private ArrayList<Integer> bpmData = new ArrayList<Integer>();
    private ArrayList<Float> paceData = new ArrayList<Float>();

    private FusedLocationProviderClient mFusedLocationClient;

    private Animation anim_left_to_right;
    private Animation anim_right_to_left;
    private Animation anim_bottom_top_delay;
    private Animation anim_top_to_bottom_delay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.workout_fragment, container, false);

        initializeViews(view);
        initializeAnimations();
        setState(STATE_BEFORE_WORKOUT);

        return view;
    }

    public void disconnected(){
        txt_connection_status.setText(R.string.failed_connection_status);
        //If user is measuring, pausing the measurement
        if(workout_state == STATE_WORKING_OUT || workout_state == STATE_TIME_ENDED){
            txt_connection_status.setText(R.string.lost_connection);
            Utils.vibrate(getContext(), VIBRATE_DURATION_CONNECTION_LOST);
            setState(STATE_PAUSED);
        }
    }

    private void initializeViews(View rootView){
        progressbar_duration = rootView.findViewById(R.id.progress_bar_duration);
        txt_calories_burned = rootView.findViewById(R.id.calories_burned);
        txt_current_pace = rootView.findViewById(R.id.running_pace);
        txt_distance = rootView.findViewById(R.id.distance_run);
        txt_bpm = rootView.findViewById(R.id.workout_bpm);
        txt_connection_status = rootView.findViewById(R.id.txt_connection_status_workout);
        img_pause = rootView.findViewById(R.id.img_pause_workout);
        img_stop = rootView.findViewById(R.id.img_stop_workout);
        layout_workout_progress = rootView.findViewById(R.id.workout_progress_layout);
        editText_minutes = rootView.findViewById(R.id.edittext_minutes);
        editText_seconds = rootView.findViewById(R.id.edittext_seconds);
        img_stop.setOnClickListener(stopButtonListener);
        setupEditTextBehavior();

        btn_toggle = rootView.findViewById(R.id.button_start_workout);
    }

    private void initializeAnimations(){
        anim_left_to_right = AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right);
        anim_right_to_left = AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left);
        anim_bottom_top_delay = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_to_top_delay);
        anim_top_to_bottom_delay = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom_delay);
    }

    private void startedWorkoutAnimations(){
        img_stop.startAnimation(anim_left_to_right);
        img_pause.startAnimation(anim_right_to_left);
        layout_workout_progress.startAnimation(anim_top_to_bottom_delay);
    }

    private void timeEndedAnimations(){
        btn_toggle.startAnimation(anim_top_to_bottom_delay);
    }


    private void autoConnectDevice(){
        String MAC_adress = Utils.readFromSharedPrefs_string(getContext(), FeedReaderDbHelper.BT_FIELD_MAC_ADRESS, FeedReaderDbHelper.SHARED_PREFS_DEVICES);
        String device_name = Utils.readFromSharedPrefs_string(getContext(),FeedReaderDbHelper.BT_FIELD_DEVICE_NAME,FeedReaderDbHelper.SHARED_PREFS_DEVICES);


        //If there is saved device --> connect
        if(!MAC_adress.equals("") && Utils.isBluetoothEnabled()){
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(MAC_adress);
            shouldStartWorkoutImmediately = true;
            getContext().startService(new Intent(getContext(), BluetoothGattService.class).putExtra("device", device));
            txt_connection_status.setText(getString(R.string.connecting_to) + " " + device_name);
        }else{

            //If there is no saved device --> add one
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=  PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                                                  new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                  MainScreenActivity.PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }else{


                if(Utils.isBluetoothEnabled()){
                    shouldStartWorkoutImmediately = true;
                    LeDevicesDialog dialog = new LeDevicesDialog(getContext());
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if(!BluetoothGattService.isGattDeviceConnected){
                                shouldStartWorkoutImmediately = false;
                            }
                        }
                    });

                }else{
                    //todo: fix to open a dialog
                    Toast.makeText(getContext(), "Please enable bluetooth!", Toast.LENGTH_LONG).show(); //TODO: add a nice dialog or something
                }
            }
        }
    }


    private View.OnClickListener resumeButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            if(BluetoothGattService.isGattDeviceConnected){
                setState(STATE_WORKING_OUT);
            }else{
                Toast.makeText(getContext(), "Please connect heart rate monitor!", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener pauseButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            setState(STATE_PAUSED);
        }
    };

    private View.OnClickListener stopButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.buildAlertDialogPrompt(getContext(),
                                         getString(R.string.please_wait),
                                         getString(R.string.do_you_really_want_to_stop_training_prompt),
                                         getString(R.string.end),
                                         getString(R.string.cancel),
                                         new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialog, int which) {
                                                 setState(STATE_BEFORE_WORKOUT);
                                             }
                                         },
                                         null
            );
        }
    };

    private View.OnClickListener reviewProgressButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            User user = User.getUser(getContext());

            WorkoutMeasurements workout = new WorkoutMeasurements(
                    Calendar.getInstance().getTime(),
                    userSpecifiedWorkoutDuration,
                    0, /*todo: calculate */
                    0,
                    Utils.convertIntArrayListToArray(bpmData),
                    Utils.convertFloatArrayListToArray(paceData),
                    Utils.convertLatLngArrayListToArray(route),
                    calories_burned,
                    user.getPulseZone()
            );

            User.addWorkoutData(getContext(), workout, true);



            setState(STATE_BEFORE_WORKOUT);
            ViewPager parentViewPager = getActivity().findViewById(R.id.viewpager);
            ViewPagerAdapter adapter = (ViewPagerAdapter) parentViewPager.getAdapter();
            adapter.dataTodayFragment.updateData();
            parentViewPager.setCurrentItem(1);



        }
    };

    private View.OnClickListener startTrainingButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //todo: check if permission is granted and gps is on
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

            //if device is connected
            if(BluetoothGattService.isGattDeviceConnected){
                setState(STATE_WORKING_OUT);

            }else{
                autoConnectDevice();
            }
        }
    };


    //Transitions the fragment from one state to another
    public void setState(int state){
        int previous_state = workout_state;
        workout_state = state;

        switch(workout_state){

            case STATE_BEFORE_WORKOUT:
                bpmData.clear();
                btn_toggle.setVisibility(View.VISIBLE);
                btn_toggle.setText(R.string.start_training);
                btn_toggle.setOnClickListener(startTrainingButtonListener);
                stopLocationListener();
                //todo: set texts based on reccomended workout duration
                editText_seconds.setText("00");
                editText_minutes.setText("00");

                img_pause.setVisibility(View.GONE);
                img_stop.setVisibility(View.GONE);
                layout_workout_progress.setVisibility(View.GONE);
                setProgressBarDuration(1,1);
                editText_minutes.setEnabled(true);
                editText_seconds.setEnabled(true);

                //I suspect that disabling editTexts removes their listeners
                setupEditTextBehavior();
                cancelTimer();
                break;

            case STATE_WORKING_OUT:
                if(previous_state == STATE_BEFORE_WORKOUT){
                    startedWorkoutAnimations();
                }

                startLocationListener();

                btn_toggle.setVisibility(View.GONE);
                img_pause.setVisibility(View.VISIBLE);
                img_pause.setImageResource(R.drawable.ic_pause_button);
                img_stop.setVisibility(View.VISIBLE);
                img_pause.setOnClickListener(pauseButtonListener);
                layout_workout_progress.setVisibility(View.VISIBLE);
                editText_minutes.setEnabled(false);
                editText_seconds.setEnabled(false);

                int minutes = Integer.valueOf(editText_minutes.getText().toString());
                int seconds = Integer.valueOf(editText_seconds.getText().toString());

                if(previous_state == STATE_BEFORE_WORKOUT){
                    userSpecifiedWorkoutDuration = minutes * 60000 + seconds * 1000;

                }

                startTimer();


                break;

            case STATE_TIME_ENDED:
                if(previous_state == STATE_WORKING_OUT){
                    timeEndedAnimations();
                }
                startLocationListener();

                progressbar_duration.setProgress(100f);

                btn_toggle.setText(R.string.end_training);
                btn_toggle.setVisibility(View.VISIBLE);
                btn_toggle.setOnClickListener(reviewProgressButtonListener);
                Utils.vibrate(getContext(), VIBRATE_DURATION_TIME_ENDED);
                //todo: decide how to show extra time user has been working out
                break;

            case STATE_PAUSED:
                pauseTimer();
                pauseLocationListener();
                img_pause.setImageResource(R.drawable.ic_resume);
                img_pause.setOnClickListener(resumeButtonListener);

                break;

        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            route.add(new LatLng(location.getLatitude(), location.getLongitude()));
            paceData.add(location.getSpeed());
            Log.i("TEST", "latitude: " + location.getLatitude() + " longtitude: " + location.getLongitude() + " speed: " + location.getSpeed());
        };
    };


    private void startLocationListener() {
        if(isLocationListeningEnabled){
            return;
        }

        isLocationListeningEnabled = true;

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        //noinspection MissingPermission
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                                    mLocationCallback,
                                                    null /* Looper */);

    }

    private void pauseLocationListener(){
        if(!isLocationListeningEnabled){
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        isLocationListeningEnabled = false;
    }

    private void stopLocationListener(){
        pauseLocationListener();
        route.clear();
    }

    public void onMeasurement(int bpm){
        txt_bpm.setText(String.valueOf(bpm));
        if(workout_state == STATE_WORKING_OUT || workout_state == STATE_TIME_ENDED){
            bpmData.add(bpm);
        }

        //todo: calculate calories and stuff. Also, will we calculate burnt calories using bpm, or gps?
    }


    private void setProgressBarDuration(int duration, int timePassed){
        float percentage = ( (float) timePassed) / ((float) duration);
        progressbar_duration.setProgress(100-percentage*100);

    }

    //Timer gets started. It does so based on timePassed value.
    private void startTimer(){
        if(timer == null){
            timer = new Timer();
        }else{
            timer.cancel();
            timer = new Timer();
        }

        isTimerRunning = true;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timePassed+=TIMER_STEP;




                        if(timePassed == userSpecifiedWorkoutDuration){
                            setState(STATE_TIME_ENDED);
                        }

                        //User is working out longer, than specified duration
                        if(timePassed > userSpecifiedWorkoutDuration){

                            int minutes = (int) (timePassed - userSpecifiedWorkoutDuration)/60000;
                            int seconds = Math.round((timePassed - userSpecifiedWorkoutDuration)/1000 - (minutes*60));

                            if(seconds < 10){
                                editText_minutes.setText("+"+minutes + "");
                                editText_seconds.setText("0" + seconds);
                            }else{
                                editText_seconds.setText(seconds + "");
                                editText_minutes.setText("+"+minutes + "");
                            }
                        }else{ //User is within his specified time limits
                            setProgressBarDuration((int) userSpecifiedWorkoutDuration, (int) (userSpecifiedWorkoutDuration -timePassed));

                            int minutes = (int) (userSpecifiedWorkoutDuration -timePassed)/60000;
                            int seconds = Math.round((userSpecifiedWorkoutDuration -timePassed)/1000 - (minutes*60));
                            if(seconds < 10){
                                editText_minutes.setText(minutes + "");
                                editText_seconds.setText("0" + seconds);
                            }else{
                                editText_seconds.setText(seconds + "");
                                editText_minutes.setText(minutes + "");
                            }
                        }
                    }
                });
            }
        }, 0, TIMER_STEP);
    }

    //Cancels the timer, but doesn't reset timepassed, so the timer can be resumed using startTimer()
    private void pauseTimer(){
        if(timer == null || !isTimerRunning){
            return;
        }
        timer.cancel();
        isTimerRunning = false;

    }

    //Cancels the timer, and sets timepassed to 0
    private void cancelTimer(){
        timePassed = 0;
        if(timer == null || !isTimerRunning){
            return;
        }
        timer.cancel();
        isTimerRunning = false;

    }



    private void setupEditTextBehavior(){
        editText_minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText_minutes.getSelectionStart() == 2){
                    editText_seconds.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText_seconds.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 0){
                    return;
                }

                if(Integer.parseInt(s.subSequence(0, 1).toString()) >= 6){
                    editText_seconds.setText("00");
                }

                if(editText_seconds.getSelectionStart() == 2){
                    editText_seconds.clearFocus();
                    editText_minutes.clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }




}
