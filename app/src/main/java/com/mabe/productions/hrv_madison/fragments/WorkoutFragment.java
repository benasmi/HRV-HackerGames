package com.mabe.productions.hrv_madison.fragments;


import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.mabe.productions.hrv_madison.Exercise;
import com.mabe.productions.hrv_madison.FrequentlyAskedActivity;
import com.mabe.productions.hrv_madison.GoogleMapService;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.PulseZoneView;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.bluetooth.BluetoothGattService;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebase.FireWorkout;
import com.mabe.productions.hrv_madison.firebase.FirebaseUtils;
import com.mabe.productions.hrv_madison.measurements.BPM;
import com.mabe.productions.hrv_madison.measurements.WorkoutMeasurements;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


import pl.droidsonroids.gif.GifImageView;

public class WorkoutFragment extends Fragment {

    private static final long VIBRATE_DURATION_TIME_ENDED = 1000l;
    private static final long VIBRATE_DURATION_CONNECTION_LOST = 5000l;
    private static final int PERMISSION_GPS_REQUEST = 0;

    public static final long WARMUP_DURATION = 300000L;

    private CircularProgressBar progressbar_duration;
    private TextView txt_calories_burned;
    private TextView txt_current_pace;
    private TextView txt_distance;
    private TextView txt_exercise_indicator;
    private TextView txt_warning_dayoff;
    private TextView txt_bpm;
    private EditText editText_seconds;
    private EditText editText_minutes;
    public TextView txt_connection_status;
    private AppCompatButton btn_toggle;
    private AppCompatButton button_personalised_workout;
    private ImageView img_pause;
    private ImageView img_stop;
    private LinearLayout layout_workout_progress;
    private LinearLayout layout_time;
    private LinearLayout layout_reccomended_workout;
    private LinearLayout layout_bpm;
    private LinearLayout layout_pulse_zone;

    private LinearLayout layout_pulse_by_vibration_switch;
    private LinearLayout layout_workout_name;
    private PulseZoneView pulseZoneView;
    private SwitchCompat pulseZone_switch;

    private TextView txt_reccomended_duration;
    private TextView txt_reccomended_pulse;
    private TextView reccomended_pulse;
    private TextView reccomended_duration;
    private TextView txt_minutes;
    private TextView txt_pulse_zone;
    private TextView txt_personolized_workout;
    private AppCompatImageButton imgButton_view_duration_info;
    private AppCompatImageButton imgButton_view_pulse_info;
    private AppCompatImageButton imgButton_info_about_vibration;


    private TextView txt_workout_name;
    private TextView txt_workout_name_explanation;


    private TextView txt_intensity;
    private TextView txt_intensity_status;
    private TextView txt_vibrate_or_not;

    public Tooltip infoDuration = null;
    public Tooltip infoPulse = null;
    public Tooltip infoVibration = null;
    private GifImageView workout_tab_running_gif;

    private Thread pauseThread;
    private Timer timer = null;
    private static final long TIMER_STEP = 1000;

    public static final int STATE_BEFORE_WORKOUT = 0;
    public static final int STATE_WORKING_OUT = 1;
    public static final int STATE_TIME_ENDED = 2;
    public static final int STATE_PAUSED = 3;

    public static final double MAX_REASONABLE_SPEED = 35; //in km/h
    public static final double MIN_REASONABLE_SPEED = 1; //in km/h

    private long timePassed = 0;

    private double calories_burned = 0;
    private int pulse_zone = 0;
    private long userSpecifiedWorkoutDuration = 0;
    private boolean isTimerRunning = false;
    private boolean runThread;
    int workout_state = STATE_BEFORE_WORKOUT;


    private float totalDistance = 0;

    private static final long VIBRATION_DURATION = 50;
    private static final long MIN_VIBRATION_COOLDOWN = 100;

    /*
     * A variable that defines how far away must user's pulse be from the required pulse zone to not alter vibration cooldown anymore.
     * If pulse difference is higher, vibration cooldowns are not altered anymore.
     */
    private static final int MAX_PULSE_DIFFERENCE_TO_ALTER_VIBRATION = 20;

    private Timer vibrationTimer;


    private ArrayList<LatLng> route = new ArrayList<>();
    private BPM pulseTracker = new BPM();
    private ArrayList<Float> paceData = new ArrayList<Float>();

    private Animation anim_left_to_right;
    private Animation anim_right_to_left;
    private Animation anim_bottom_top_delay;
    private Animation anim_top_to_bottom_delay;
    private Animation anim_running_man_left_to_right;
    private Animation anim_top_to_bottom;
    private Animation anim_fade_out;
    private ObjectAnimator blinking_anim;
    private float HRMax;
    public static boolean vibrateState = true;
    private Vibrator mVibrator;
    private boolean isReceiverRegistered = false;

    private MediaPlayer mediaPlayer;

    private Exercise exercise;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.workout_fragment, container, false);

        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        initializeViews(view);
        updateData();
        initializeAnimations();
        setState(STATE_BEFORE_WORKOUT);
        setFonts();
        registerReceiver();

        //TODO: change sound resource
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.measurement_notification);


        return view;
    }

    public void disconnected() {
        txt_connection_status.setText(R.string.failed_connection_status);
        //If user is measuring, pausing the measurement
        if (workout_state == STATE_WORKING_OUT || workout_state == STATE_TIME_ENDED) {
            txt_connection_status.setText(R.string.lost_connection);
            Utils.vibrate(getContext(), VIBRATE_DURATION_CONNECTION_LOST);
            setState(STATE_PAUSED);
        }
    }

    private void initializeViews(View rootView) {
        txt_workout_name = rootView.findViewById(R.id.txt_workout_name);
        txt_workout_name_explanation = rootView.findViewById(R.id.txt_workout_name_explaining);
        txt_vibrate_or_not = rootView.findViewById(R.id.txt_vibrate_or_not);
        txt_warning_dayoff = rootView.findViewById(R.id.txt_warning_day_off);
        layout_pulse_by_vibration_switch = rootView.findViewById(R.id.layout_pulse_by_vibration_switch);
        layout_workout_name = rootView.findViewById(R.id.layout_workout_name);
        txt_intensity = rootView.findViewById(R.id.txt_intensity);
        txt_intensity_status = rootView.findViewById(R.id.txt_intensity_status);
        pulseZoneView = rootView.findViewById(R.id.pulse_zone_progress);
        layout_pulse_zone = rootView.findViewById(R.id.layout_pulse_zone);
        imgButton_view_pulse_info = rootView.findViewById(R.id.imgButton_view_pulse_info);
        imgButton_info_about_vibration = rootView.findViewById(R.id.imgButton_info_about_vibration);
        imgButton_view_duration_info = rootView.findViewById(R.id.imgButton_view_duration_info);
        layout_bpm = rootView.findViewById(R.id.layout_bpm);
        txt_personolized_workout = rootView.findViewById(R.id.txt_personolized_workout);
        button_personalised_workout = rootView.findViewById(R.id.button_personalised_workout);
        layout_reccomended_workout = rootView.findViewById(R.id.layout_reccomended_workout);
        txt_reccomended_duration = rootView.findViewById(R.id.txt_reccomended_duration);
        txt_reccomended_pulse = rootView.findViewById(R.id.txt_reccomended_pulse);
        reccomended_pulse = rootView.findViewById(R.id.reccomended_pulse_zone);
        reccomended_duration = rootView.findViewById(R.id.reccomended_duration);
        txt_minutes = rootView.findViewById(R.id.txt_minutes);
        txt_pulse_zone = rootView.findViewById(R.id.txt_pulse_zone);
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
        layout_time = rootView.findViewById(R.id.time_layout);
        pulseZone_switch = rootView.findViewById(R.id.switch_pulse_zone);
        img_stop.setOnClickListener(stopButtonListener);
        workout_tab_running_gif = rootView.findViewById(R.id.workout_tab_running_gif);
        txt_exercise_indicator = rootView.findViewById(R.id.exercise_textview);

        setupEditTextBehavior();
        imgButton_view_duration_info.setOnClickListener(durationInfoListener);
        imgButton_view_pulse_info.setOnClickListener(durationPulseListener);
        imgButton_info_about_vibration.setOnClickListener(vibrationInfoListener);
        btn_toggle = rootView.findViewById(R.id.button_start_workout);
        button_personalised_workout.setOnClickListener(personaliseInfo);
        pulseZone_switch.setOnCheckedChangeListener(selectVibrationListener);
        pulseZone_switch.setChecked(Utils.readFromSharedPrefs_bool(getContext(),FeedReaderDbHelper.FIELD_VIBRATION_INDICATION,FeedReaderDbHelper.SHARED_PREFS_USER_DATA));
        vibrateState = pulseZone_switch.isChecked();
    }


    public void updateData() {
        MainScreenActivity.user = User.getUser(getContext());
        txt_workout_name.setText(MainScreenActivity.user.getWorkoutSessionType());
        txt_workout_name_explanation.setText(MainScreenActivity.user.getVerbalSessionExplanation());

        //pulseZoneView.setRequiredPulseZones(required_pulse_zone);
        editText_minutes.setText("" + (int) MainScreenActivity.user.getWorkoutDuration());
        editText_seconds.setText("00");
        reccomended_duration.setText("" + (int) MainScreenActivity.user.getWorkoutDuration());

        int minPulseZone = MainScreenActivity.user.getExercise().getMinimumPulseZone();
        int maxPulseZone = MainScreenActivity.user.getExercise().getMaximumPulseZone();

        if(minPulseZone == maxPulseZone){
            reccomended_pulse.setText("" + minPulseZone);
        }else{
            reccomended_pulse.setText(minPulseZone + "-" + maxPulseZone);
        }

        exercise = MainScreenActivity.user.getExercise();
    }

    private void initializeAnimations() {
        anim_left_to_right = AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right);
        anim_right_to_left = AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left);
        anim_running_man_left_to_right = AnimationUtils.loadAnimation(getContext(), R.anim.running_man_left_to_right);
        anim_bottom_top_delay = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_to_top_delay);
        anim_top_to_bottom_delay = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom_delay);
        anim_top_to_bottom = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom);
        anim_fade_out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        blinking_anim = (ObjectAnimator) AnimatorInflater.loadAnimator(getContext(), R.animator.blinking_anim);

    }

    private void startedWorkoutAnimations() {

        workout_tab_running_gif.startAnimation(anim_running_man_left_to_right);
        img_stop.startAnimation(anim_left_to_right);
        img_pause.startAnimation(anim_right_to_left);
        layout_workout_progress.startAnimation(anim_top_to_bottom_delay);
        layout_pulse_zone.startAnimation(anim_right_to_left);
        layout_bpm.startAnimation(anim_left_to_right);
    }

    private void timeEndedAnimations() {
        btn_toggle.startAnimation(anim_top_to_bottom_delay);
    }


    //REGISTERS RECEIVER
    private void registerReceiver(){
        if(!isReceiverRegistered) {

            IntentFilter filter = new IntentFilter();
            filter.addAction(GoogleMapService.ACTION_SEND_GPS_DATA);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, filter);

            isReceiverRegistered = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_GPS_REQUEST && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            startWorkout();
        }
    }

    /**
     * Checks if gps enabled and gps permission is granted.
     * If gps is not enabled, user is notified via a toast.
     * If user has not granted gps permission yet, a permission is requested.
     * After the FireUser has granted a permission, {@link #startWorkout()} is called;
     *
     * @return returns true if gps is enabled and ready to use
     */
    private boolean checkForGPS() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    WorkoutFragment.PERMISSION_GPS_REQUEST);
            return false;
        }

        if (!Utils.isGPSEnabled(getContext())) {
            Toast.makeText(getContext(), "Please enable GPS!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /**
     * Checks if:
     *   FireUser has granted a GPS permission
     *   GPS is on
     *   Pulsometer is connected
     *     If pulsometer is not connected, prompting user with a dialog.
     *
     *   If all conditions are met, workout is started via {@link #setState(int)}
     */
    private void startWorkout(){

        if(!checkForGPS()){
            return;
        }

        if(Utils.isDeviceInPowerSavingMode(getContext())){
            Toast.makeText(getContext(), "Please disable power saving mode!", Toast.LENGTH_LONG).show();
            return;
        }

        if (BluetoothGattService.isGattDeviceConnected) {
            setState(STATE_WORKING_OUT);

        } else {
            Utils.buildAlertDialogPrompt(
                    getContext(),
                    "Please wait!",
                    "Do you want to proceed to workout without HR monitor?",
                    "Yes",
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setState(STATE_WORKING_OUT);
                        }
                    },
                    null
            );
        }

    }



    private View.OnClickListener durationInfoListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if (infoPulse != null) {
                infoPulse.dismiss();
            }
            if (infoVibration != null) {
                infoVibration.dismiss();
            }

            if (infoDuration == null) {
                infoDuration = new Tooltip.Builder(view)
                        .setText("This is reccomended duration of your workout")
                        .setDismissOnClick(true)
                        .setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent))
                        .setTextColor(getActivity().getResources().getColor(R.color.white))
                        .setCornerRadius(7f)
                        .setGravity(Gravity.TOP)
                        .show();

            }

            if (!infoDuration.isShowing()) {
                infoDuration.show();
            }

        }
    };


    private View.OnClickListener vibrationInfoListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if (infoDuration != null) {
                infoDuration.dismiss();
            }
            if (infoPulse != null) {
                infoPulse.dismiss();
            }

            if (infoVibration == null) {
                infoVibration = new Tooltip.Builder(view)
                        .setText("If you turn this feature on, throughout your workout we will help you to stay in reccommended pulse zone by vibration indications!")
                        .setDismissOnClick(true)
                        .setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent))
                        .setTextColor(getActivity().getResources().getColor(R.color.white))
                        .setCornerRadius(7f)
                        .setGravity(Gravity.TOP)
                        .show();

            }

            if (!infoVibration.isShowing()) {
                infoVibration.show();
            }

        }
    };

    private View.OnClickListener personaliseInfo = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getContext(), R.style.AppThemeDialog);
            } else {
                builder = new AlertDialog.Builder(getContext());
            }
            builder.setTitle("What is personalized workout?")
                    .setMessage("Right now your workout is generated based on Classical Aerobics Theory. If you want unique workout plan generated for you, please measure your 'HRV' and comeback!")
                    .setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Got it!
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton(R.string.measure, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //goes to measure fragment

                            Utils.setViewpagerTab(getActivity(),0);
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton(R.string.hrv, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            getActivity().startActivity(new Intent(getActivity(),FrequentlyAskedActivity.class));
                            dialog.dismiss();

                        }
                    })
                    .show();
        }
    };

    private CompoundButton.OnCheckedChangeListener selectVibrationListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            vibrateState = isChecked;
            Utils.saveToSharedPrefs(getContext(),FeedReaderDbHelper.FIELD_VIBRATION_INDICATION,vibrateState,FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
            Log.i("TEST", "VIBRATE_STATE: " + vibrateState);
        }
    };

    private View.OnClickListener durationPulseListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if (infoDuration != null) {
                infoDuration.dismiss();
            }
            if (infoVibration != null) {
                infoVibration.dismiss();
            }


            if (infoPulse == null) {
                infoPulse = new Tooltip.Builder(view)
                        .setText("This is recommended pulse zone of your workout")
                        .setDismissOnClick(true)
                        .setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent))
                        .setTextColor(getActivity().getResources().getColor(R.color.white))
                        .setCornerRadius(7f)
                        .setGravity(Gravity.TOP)
                        .show();

            }

            if (!infoPulse.isShowing()) {
                infoPulse.show();
            }

        }
    };

    private View.OnClickListener resumeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runThread = false;
            setState(STATE_WORKING_OUT);
        }
    };

    private View.OnClickListener pauseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timePauseFlashing();
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

                            runThread = false;

                            setState(STATE_BEFORE_WORKOUT);
                        }
                    },
                    null
            );
        }
    };

    private View.OnClickListener reviewProgressButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            calories_burned = Math.round(calories_burned * 100.0) / 100.0;
            totalDistance = (float) (Math.round(totalDistance * 100.0) / 100.0);



            WorkoutMeasurements workout = new WorkoutMeasurements(
                    exercise,
                    Calendar.getInstance().getTime(),
                    userSpecifiedWorkoutDuration,
                    pulseTracker.getAverageBpm(),
                    pulseTracker.getBpmValues(),
                    Utils.convertFloatArrayListToArray(paceData),
                    Utils.convertLatLngArrayListToArray(route),
                    (float) calories_burned,
                    totalDistance
            );

            User.addWorkoutData(getContext(), workout, true);
            FirebaseUtils.addWorkout(new FireWorkout(workout));

            setState(STATE_BEFORE_WORKOUT);
            ViewPager parentViewPager = getActivity().findViewById(R.id.viewpager);
            ViewPagerAdapter adapter = (ViewPagerAdapter) parentViewPager.getAdapter();
            adapter.dataTodayFragment.updateData(getContext());
            parentViewPager.setCurrentItem(1);


        }
    };

    private View.OnClickListener startTrainingButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           startWorkout();
        }
    };


    /**
     * Transitions the fragment from one state to another.
     *
     * @param state the state to change to.
     */
    public void setState(final int state) {
        int previous_state = workout_state;
        workout_state = state;

        switch (workout_state) {

            case STATE_BEFORE_WORKOUT:
                MainScreenActivity.setDisplayOnLockscreen(false, getActivity());
                if (vibrationTimer != null) {
                    vibrationTimer.cancel();
                }
                cancelTimer();
                txt_exercise_indicator.setVisibility(View.GONE);
                layout_workout_name.setVisibility(View.VISIBLE);
                workout_tab_running_gif.setVisibility(View.INVISIBLE);
                layout_pulse_zone.setVisibility(View.GONE);
                txt_warning_dayoff.setVisibility(Utils.checkDayOffStatus(getContext())== true ? View.GONE : View.VISIBLE);
                pulseTracker.clear();
                paceData.clear();
                stopLocationListener();
                txt_distance.setText(String.valueOf(0f));
                btn_toggle.setVisibility(View.VISIBLE);
                layout_pulse_by_vibration_switch.setVisibility(View.VISIBLE);
                btn_toggle.setText(R.string.start_training);
                btn_toggle.setOnClickListener(startTrainingButtonListener);

                layout_bpm.setVisibility(View.GONE);
                layout_reccomended_workout.setVisibility(View.VISIBLE);
                img_pause.setVisibility(View.GONE);
                img_stop.setVisibility(View.GONE);
                layout_workout_progress.setVisibility(View.GONE);
                Log.i("TEST", "Setting progressBar duration in setState()...");
                setProgressBarDuration(1, 1, true);
                editText_minutes.setEnabled(true);
                editText_seconds.setEnabled(true);
                updateData();


                //Todo: fix that, it's ugly now :(
                if (MainScreenActivity.user.getTodaysMeasurement() == null) {
                    button_personalised_workout.setVisibility(View.VISIBLE);
                    txt_personolized_workout.setVisibility(View.GONE);
                } else {
                    button_personalised_workout.setVisibility(View.GONE);
                    txt_personolized_workout.setVisibility(View.VISIBLE);
                }
                //I suspect that disabling editTexts removes their listeners
                setupEditTextBehavior();

                break;

            case STATE_WORKING_OUT:

                MainScreenActivity.setDisplayOnLockscreen(true, getActivity());
                if (infoDuration != null) {
                    infoDuration.dismiss();
                }
                if (infoPulse != null) {
                    infoPulse.dismiss();
                }
                workout_tab_running_gif.setVisibility(View.VISIBLE);
                if (previous_state == STATE_BEFORE_WORKOUT) {
                    startedWorkoutAnimations();
                }

                setWalkingRunningState(0L);
                txt_exercise_indicator.setVisibility(View.VISIBLE);
                layout_workout_name.setVisibility(View.GONE);
                startLocationListener();
                layout_bpm.setVisibility(View.VISIBLE);
                layout_reccomended_workout.setVisibility(View.GONE);
                txt_warning_dayoff.setVisibility(View.GONE);
                txt_personolized_workout.setVisibility(View.GONE);
                button_personalised_workout.setVisibility(View.GONE);
                layout_pulse_by_vibration_switch.setVisibility(View.GONE);
                btn_toggle.setVisibility(View.GONE);
                img_pause.setVisibility(View.VISIBLE);
                img_pause.setImageResource(R.drawable.ic_pause_button);
                img_stop.setVisibility(View.VISIBLE);
                img_pause.setOnClickListener(pauseButtonListener);
                layout_workout_progress.setVisibility(View.VISIBLE);
                editText_minutes.setEnabled(false);
                editText_seconds.setEnabled(false);
                layout_pulse_zone.setVisibility(BluetoothGattService.isGattDeviceConnected == true ? View.VISIBLE : View.GONE);
                int minutes = Integer.valueOf(editText_minutes.getText().toString());
                int seconds = Integer.valueOf(editText_seconds.getText().toString());

                if (previous_state == STATE_BEFORE_WORKOUT) {
                    userSpecifiedWorkoutDuration = minutes * 60000 + seconds * 1000;

                }

                startTimer();


                break;

            case STATE_TIME_ENDED:
                if (previous_state == STATE_WORKING_OUT) {
                    timeEndedAnimations();
                }
                startLocationListener();

                //progressbar_duration.setProgress(100f);

                btn_toggle.setText(R.string.end_training);
                btn_toggle.setVisibility(View.VISIBLE);
                btn_toggle.setOnClickListener(reviewProgressButtonListener);
                Utils.vibrate(getContext(), VIBRATE_DURATION_TIME_ENDED);
                break;

            case STATE_PAUSED:
                pauseTimer();
                if (vibrationTimer != null) {
                    vibrationTimer.cancel();
                }

                workout_tab_running_gif.setVisibility(View.INVISIBLE);
                img_pause.setImageResource(R.drawable.ic_resume);
                img_pause.setOnClickListener(resumeButtonListener);

                break;

        }
    }


    BroadcastReceiver  broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case GoogleMapService.ACTION_SEND_GPS_DATA:

                    LocationResult[] arrayLocationResults = (LocationResult[]) intent.getParcelableArrayExtra(GoogleMapService.GPS_DATA);
                    if (workout_state == STATE_WORKING_OUT || workout_state == STATE_TIME_ENDED) {

                        for(int i = 0; i<arrayLocationResults.length; i++){

                            LocationResult currentResult = arrayLocationResults[i];

                            double latitude = currentResult.getLastLocation().getLatitude();
                            double longitude = currentResult.getLastLocation().getLongitude();
                            LatLng lastLocation = new LatLng(latitude, longitude);

                            if (route.size() > 0) {
                                double distance = distance(lastLocation, route.get(route.size() - 1));
                                //double speed = ((System.currentTimeMillis() - lastLocationUpdate) / (1000d * 60d * 60d)) / distance;
                                double speed = currentResult.getLastLocation().getSpeed()*3.6d; //in km/h
                                txt_current_pace.setText(String.valueOf( Math.round(currentResult.getLastLocation().getSpeed()*100d)/100d));
                                //Log.i("TEST", "Estimated movement speed: " + speed + "km/h\nGiven movement speed: " + locationResult.getLastLocation().getSpeed()*3.6d + "km/h");
                                if (speed <= MAX_REASONABLE_SPEED && speed >= MIN_REASONABLE_SPEED) {
                                    route.add(lastLocation);
                                    paceData.add(currentResult.getLastLocation().getSpeed());
                                    totalDistance += distance;
                                    Log.i("TEST", "total distance: " + totalDistance);
                                    txt_distance.setText(String.valueOf(Math.round(totalDistance * 100d) / 100d));
                                }
                            } else {
                                route.add(lastLocation);
                            }
                        }
                        }
                    break;


            }
        }
    };



    private void startLocationListener() {
        if (GoogleMapService.isLocationListeningEnabled) {
            return;
        }

        getActivity().startService(new Intent(getActivity(),GoogleMapService.class));


        GoogleMapService.isLocationListeningEnabled = true;
    }



    private void stopLocationListener() {
        getActivity().stopService(new Intent(getActivity(),GoogleMapService.class));
        route.clear();
        paceData.clear();
        totalDistance = 0f;
    }

    public void onMeasurement(int bpm) {

        txt_bpm.setText(String.valueOf(bpm));

        if (workout_state == STATE_WORKING_OUT || workout_state == STATE_TIME_ENDED) {
            pulseTracker.addBPM(bpm);
            int gender = MainScreenActivity.user.getGender();
            int age = Utils.getAgeFromDate(MainScreenActivity.user.getBirthday());
            int weight = (int) MainScreenActivity.user.getWeight();

            calories_burned = calories_burned + calculateCalories(gender, age, weight, bpm, 1f / 60f);
            pulse_zone = pulseZone(gender, age, bpm);
            float realPercentage = calculateUIMultiplier(HRMax * 0.5f, HRMax, bpm);
            pulseZoneView.setProgressPercentageWithAnim(realPercentage);
            setIntensityStatus(txt_intensity, pulseZoneView.getRequiredPulseZones(), pulse_zone);
            txt_calories_burned.setText(String.valueOf(Math.round(calories_burned * 100.0) / 100.0)); //Rounding and displaying calories

            if(vibrateState){
                vibrateByPulseZone(pulseZoneView.getRequiredPulseZones(), HRMax, bpm);
            }
        }
    }

    /**
     * Starts a timer that vibrates the phone based on given pulse data
     * If a timer is currently running, it is cancelled, and the new one with updated data is started.
     * Timer is stored in vibrationTimer field.
     *
     * @param required_pulse_zones The pulse zone user has to be in. Can be an integer from 1 to 5.
     * @param HRMax               The maximum BPM user can withstand. Can be calculated from age and gender.
     * @param heartRate           The current user's heart rate in BPM
     */
    private void vibrateByPulseZone(int[] required_pulse_zones, float HRMax, int heartRate) {

        if(required_pulse_zones.length == 0){
            if (vibrationTimer != null)
                vibrationTimer.cancel();
            return;
        }

        int min_pulse = Utils.getPulseZoneBounds(required_pulse_zones, HRMax)[0];
        int max_pulse = Utils.getPulseZoneBounds(required_pulse_zones, HRMax)[1];

        final long vibrationPeriod;

        if (heartRate > max_pulse) {
            float difference = Math.min(heartRate - max_pulse, MAX_PULSE_DIFFERENCE_TO_ALTER_VIBRATION);
            vibrationPeriod = (long) ((MIN_VIBRATION_COOLDOWN * (float) MAX_PULSE_DIFFERENCE_TO_ALTER_VIBRATION) / difference);
        } else if (heartRate < min_pulse) {
            int difference = Math.min(min_pulse - heartRate, MAX_PULSE_DIFFERENCE_TO_ALTER_VIBRATION);
            vibrationPeriod = (long) ((MIN_VIBRATION_COOLDOWN * (float) MAX_PULSE_DIFFERENCE_TO_ALTER_VIBRATION) / difference);
        } else {

            if (vibrationTimer != null)
                vibrationTimer.cancel();

            vibrationTimer = new Timer();
            return;
        }

        if (vibrationTimer == null) {
            vibrationTimer = new Timer();
        } else {
            vibrationTimer.cancel();
            vibrationTimer = new Timer();
        }

        vibrationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    VibrationEffect effect = VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE);
                    mVibrator.vibrate(effect);
                } else {
                    mVibrator.vibrate(50);
                }
            }
        }, 0, vibrationPeriod + VIBRATION_DURATION);

    }



    /**
     * Sets the time circle progress.
     *
     * @param timeTotal  the time workout is going to last
     * @param timeProgress time, that has already passed
     * @param reverse If true, progress is displayed in opposite colors
     */
    private void setProgressBarDuration(int timeTotal, int timeProgress, boolean reverse) {
        float multiplier = ((float) timeProgress) / ((float) timeTotal);

        if(reverse){
            progressbar_duration.setProgress(100 - multiplier * 100);
        }else{
            progressbar_duration.setProgress(multiplier * 100);
        }
    }

    //Timer gets started. It does so based on timePassed value.
    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.cancel();
            timer = new Timer();
        }

        isTimerRunning = true;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(workout_state != STATE_WORKING_OUT && workout_state != STATE_TIME_ENDED){
                            return;
                        }

                        setWalkingRunningState(timePassed);


                        if (timePassed-WARMUP_DURATION > userSpecifiedWorkoutDuration) { //The user has exceeded it's designated workout duration
                            int minutes = (int) (timePassed - WARMUP_DURATION - userSpecifiedWorkoutDuration) / 60000;
                            int seconds = Math.round((timePassed - WARMUP_DURATION - userSpecifiedWorkoutDuration) / 1000 - (minutes * 60));

                            if (seconds < 10) {
                                editText_minutes.setText("+" + minutes + "");
                                editText_seconds.setText("0" + seconds);
                            } else {
                                editText_seconds.setText(seconds + "");
                                editText_minutes.setText("+" + minutes + "");
                            }

                        } else if(timePassed-WARMUP_DURATION < userSpecifiedWorkoutDuration){ //The user has not exceeded the designated workout duration yet

                            if(!isIntervalProgram() && current_exercise != EXERCISE_WARMUP){
                                setProgressBarDuration((int) userSpecifiedWorkoutDuration, (int) (userSpecifiedWorkoutDuration - timePassed + WARMUP_DURATION), true);
                            }

                            int minutes = 0;
                            int seconds = 0;

                            //Setting timer numbers
                            if(timePassed <= WARMUP_DURATION){
                                //It's warmup my dudes
                                //The timer shows how much time is left for the warmup to end
                                minutes = (int) (WARMUP_DURATION - timePassed) / 60000;
                                seconds = Math.round((WARMUP_DURATION - timePassed) / 1000 - (minutes * 60));
                            }else{
                                //The warmup has ended, but the user has not finished the workout yet
                                //The timer shows how much time before user completes their workout
                                minutes = (int) (userSpecifiedWorkoutDuration - timePassed + WARMUP_DURATION) / 60000;
                                seconds = Math.round((userSpecifiedWorkoutDuration - timePassed + WARMUP_DURATION) / 1000 - (minutes * 60));
                            }

                            //Setting timer numbers accordingly
                            if (seconds < 10) {
                                editText_minutes.setText(minutes + "");
                                editText_seconds.setText("0" + seconds);
                            } else {
                                editText_seconds.setText(seconds + "");
                                editText_minutes.setText(minutes + "");
                            }
                        }else{ //The user has just reached the designated workout duration

                            //Checking if intervals are enabled.
                            if(!isIntervalProgram()){
                                setProgressBarDuration(1,1, false);
                            }
                            setState(STATE_TIME_ENDED);
                        }

                        timePassed += TIMER_STEP;

                    }
                });
            }
        }, 0, TIMER_STEP);
    }

    //Cancels the timer, but doesn't reset timepassed, so the timer can be resumed using startTimer()
    private void pauseTimer() {
        if (timer == null || !isTimerRunning) {
            return;
        }
        timer.cancel();
        isTimerRunning = false;

    }

    //Cancels the timer, and sets timepassed to 0
    private void cancelTimer() {
        timePassed = 0;
        if (timer == null || !isTimerRunning) {
            return;
        }

        timer.cancel();
        isTimerRunning = false;

    }

    private void setupEditTextBehavior() {
        View.OnClickListener editTextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textView = ((EditText) view);
                textView.setSelection(0, textView.getText().length());
            }
        };

        KeyListener keyListener = new KeyListener() {

            @Override
            public int getInputType() {
                return 0;
            }

            @Override
            public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    return true;
                }

                return false;
            }

            @Override
            public boolean onKeyUp(View view, Editable editable, int i, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public void clearMetaKeyState(View view, Editable editable, int i) {

            }
        };

        editText_minutes.setOnClickListener(editTextClickListener);
        editText_seconds.setOnClickListener(editTextClickListener);
        //editText_minutes.setKeyListener(keyListener);
        //editText_seconds.setKeyListener(keyListener);

        editText_minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText_minutes.getSelectionStart() == 2) {
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

                if (s.length() == 0) {
                    return;
                }

                if (Integer.parseInt(s.subSequence(0, 1).toString()) >= 6) {
                    editText_seconds.setText("00");
                }

                if (editText_seconds.getSelectionStart() == 2) {
                    editText_seconds.clearFocus();
                    editText_minutes.clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /*
     * Calculates the calories burned.
     *
     * @param gender     The gender of the user. Either FireUser.GENDER_MALE or FireUser.GENDER_FEMALE
     * @param weight     The weight of the user in kilograms.
     * @param heartRate  The current heartRate in BPM.
     * @param timePassed The amount of time that has passed since last heart rate measurement.
     * @return Calories burned (in KCal)
     */
    private double calculateCalories(int gender, int age, int weight, int heartRate, double timePassed) {

        double calories = 0;

        switch (gender) {
            case User.GENDER_FEMALE:
                calories = ((((double) age * 0.074d) - (((double) weight) * 0.05741d)) + (((double) heartRate) * 0.4472d) - 20.4022d) * timePassed / 4.184d;
                break;

            case User.GENDER_MALE:
                calories = ((((double) age * 0.2017d) - (((double) weight) * 0.09036d)) + (((double) heartRate) * 0.6309d) - 20.4022d) * timePassed / 4.184d;
                break;
        }

        return calories;
    }


    /**
     * Gets the current pulse zone user is in
     *
     * @param gender The gender of the user. Either FireUser.GENDER_MALE or FireUser.GENDER_FEMALE
     * @param age    The age of the user.
     * @return The pulse zone user is in. If user does not fit pulse zone bounds, returns 0.
     * @heartRate The heart rate of the user in BPM.
     */
    private int pulseZone(int gender, int age, int heartRate) {

        HRMax = ((gender == 0 ? 202 : 216) - (gender == 0 ? 0.55f : 1.09f) * age);
        int pulseZone = 0;

        float hrPercentage = ((float) heartRate) / HRMax * 100f;


        if (hrPercentage >= 50 && hrPercentage <= 60) {
            pulseZone = 1;
        } else if (hrPercentage > 60 && hrPercentage <= 70) {
            pulseZone = 2;
        } else if (hrPercentage > 70 && hrPercentage <= 80) {
            pulseZone = 3;
        } else if (hrPercentage > 80 && hrPercentage <= 90) {
            pulseZone = 4;
        } else if (hrPercentage > 90 && hrPercentage <= 100) {
            pulseZone = 5;
        }
        Log.i("TEST", "HRMAX: " + HRMax + " | " + "BPM: " + heartRate + " | " + "hrPercentage: " + hrPercentage + " | " + "pulseZone: " + pulseZone);
        return pulseZone;
    }


    /**
     * Calculates the ortional position of user's heart rate in the given HR bounds.
     * This is intended to be used with the {@link PulseZoneView} to set progress.
     *
     * @param minimumHR The lower bounds of the lowest pulse zone.
     * @param maximumHR The upper bounds of the highest pulse zone.
     * @return A number from 0 to 1. If FireUser's HR is not in the min/max HR bounds, returns 0.05f
     */
    private float calculateUIMultiplier(float minimumHR, float maximumHR, int currentHR) {
        if (currentHR < minimumHR) {
            return 0.05f;
        }
        return Math.max(((currentHR - minimumHR) / (maximumHR - minimumHR)), 0.05f);
    }


    /**
     * Calculates the distance between two given LatLng points.
     *
     * @param pointA The first point.
     * @param pointB The second point.
     * @return Distance between point A and point B in kilometers.
     */
    private float distance(LatLng pointA, LatLng pointB) {

        float lat_a = (float) pointA.latitude;
        float lng_a = (float) pointA.longitude;

        float lat_b = (float) pointB.latitude;
        float lng_b = (float) pointB.longitude;

        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue() / 1000f;
    }


    /**
     * Starts the thread that flashes duration textviews when paused.
     */
    private void timePauseFlashing() {
        final boolean[] visibility = {true};
        runThread = true;
        pauseThread = new Thread() {
            @Override
            public void run() {
                while (runThread) {
                    try {
                        Thread.sleep(400);  //1000ms = 1 sec
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (runThread) {
                                    if (visibility[0]) {
                                        layout_time.setVisibility(View.INVISIBLE);
                                    } else {
                                        layout_time.setVisibility(View.VISIBLE);
                                    }
                                    visibility[0] = !visibility[0];
                                } else {
                                    layout_time.setVisibility(View.VISIBLE);
                                }


                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        pauseThread.start();

    }

    private void setFonts() {

        Typeface futura = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/futura_light.ttf");

        txt_reccomended_duration.setTypeface(futura);
        txt_reccomended_pulse.setTypeface(futura);
        reccomended_pulse.setTypeface(futura);
        reccomended_duration.setTypeface(futura);
        txt_minutes.setTypeface(futura);
        txt_pulse_zone.setTypeface(futura);
        txt_vibrate_or_not.setTypeface(futura);
        txt_intensity_status.setTypeface(futura);
        txt_intensity.setTypeface(futura);
        txt_warning_dayoff.setTypeface(futura);
        txt_workout_name_explanation.setTypeface(futura);

    }

    private void setIntensityStatus(TextView intensityStatusView, int[] requiredPulseZones, int currentPulseZone) {

        if(requiredPulseZones.length == 0){
            intensityStatusView.setText("Optional intensity");
            return;
        }

        int lowest_pulse_zone = Utils.minNum(requiredPulseZones);
        int highest_pulse_zone = Utils.maxNum(requiredPulseZones);

        if (currentPulseZone >= lowest_pulse_zone && currentPulseZone <= highest_pulse_zone) {
            intensityStatusView.setText("Intensity: Optimal");
            blinking_anim.end();
            blinking_anim.cancel();
        } else if (currentPulseZone < lowest_pulse_zone) {
            intensityStatusView.setText("Intensity: Too low!");
            blinking_anim.setTarget(intensityStatusView);
            blinking_anim.start();
        } else if (currentPulseZone > highest_pulse_zone) {
            intensityStatusView.setText("Intensity: Too high!");
            blinking_anim.setTarget(intensityStatusView);
            blinking_anim.start();
        }

    }

    public static final int EXERCISE_WALKING = 0;
    public static final int EXERCISE_JOGGING = 1;
    public static final int EXERCISE_WARMUP = 2;
    private int current_exercise = 3;

    /**
     * Sets the current exercise state based on the amount of time that has passed.
     * Also sets progressbar duration accordingly via setProgressBarDuration()
     *
     * Intended to be used in a timer.
     * @param timePassed The amount of time that has passed from the start of workout.
     */
    private void setWalkingRunningState(long timePassed){

        if(timePassed <= WARMUP_DURATION){
            if(current_exercise != EXERCISE_WARMUP){
                setExercise(EXERCISE_WARMUP);
                pulseZoneView.setRequiredPulseZones(new int[0]);
            }
            setProgressBarDuration((int) WARMUP_DURATION, (int) timePassed, true);
            return;
        }

        //Calculating the total duration of a cycle
        long cycleDuration = 0;
        for(long interval : exercise.getWorkoutIntervals()){
            cycleDuration+=interval*1000L;
        }
        if(cycleDuration == 0){
            if(exercise.getWorkoutIntervals().length % 2 == 0){
                if(current_exercise != EXERCISE_JOGGING){
                    setExercise(EXERCISE_JOGGING);
                    pulseZoneView.setRequiredPulseZones(exercise.getRunningPulseZones());
                }

            }else {
                if(current_exercise != EXERCISE_WALKING){
                    setExercise(EXERCISE_WALKING);
                    pulseZoneView.setRequiredPulseZones(exercise.getWalkingPulseZones());
                }
            }
            return;
        }
        long workoutTime = timePassed - WARMUP_DURATION; //workout time is the workout time that has passed without warmup
        int timesCycleCompleted = (int) (workoutTime/cycleDuration);
        long currentCycleProgress = workoutTime - cycleDuration*timesCycleCompleted;
        long intervalSum = 0;
        for(int i = 0; i < exercise.getWorkoutIntervals().length; i++){
            long lowerBound = intervalSum;
            intervalSum+=exercise.getWorkoutIntervals()[i]*1000L;
            long upperBound = intervalSum;
            if(currentCycleProgress <= upperBound && currentCycleProgress >= lowerBound){

                if(i % 2 == 0){
                    if(current_exercise != EXERCISE_WALKING){
                        setExercise(EXERCISE_WALKING);
                    }
                    pulseZoneView.setRequiredPulseZones(exercise.getWalkingPulseZones());
                    setProgressBarDuration((int) (upperBound-lowerBound), (int) (currentCycleProgress-lowerBound), false);
                }else{
                    if(current_exercise != EXERCISE_JOGGING){
                        setExercise(EXERCISE_JOGGING);
                    }
                    pulseZoneView.setRequiredPulseZones(exercise.getRunningPulseZones());
                    setProgressBarDuration((int) (upperBound-lowerBound), (int) (currentCycleProgress-lowerBound), true);

                }
                break;
            }
        }


    }

    private boolean isIntervalProgram(){
        //Calculating the total duration of a cycle
        long cycleDuration = 0;
        for(long interval : exercise.getWorkoutIntervals()){
            cycleDuration+=interval*1000L;
        }
        if(cycleDuration == 0){

            return false;
        }
        return true;
    }


    public void setExercise(final int exercise){
        final int icon_resource;
        final int textResource;

        //TODO: find appropriate walking gif
        if(exercise == EXERCISE_JOGGING){
            icon_resource = R.drawable.human;
            textResource = R.string.jogging;
            progressbar_duration.setForegroundStrokeColor(getResources().getColor(R.color.colorAccent));
        }else if(exercise == EXERCISE_WALKING){
            icon_resource = R.drawable.ic_appicon_rectangle;
            progressbar_duration.setForegroundStrokeColor(getResources().getColor(R.color.colorAccent));
            textResource = R.string.walking;
        }else if(exercise == EXERCISE_WARMUP) {
            icon_resource = R.drawable.ic_warmup;
            progressbar_duration.setForegroundStrokeColor(getResources().getColor(R.color.warmup_timer));
            textResource = R.string.warmup;
        }else{
            icon_resource = R.drawable.ic_stopwatch;
            progressbar_duration.setForegroundStrokeColor(getResources().getColor(R.color.colorAccent));
            textResource = R.string.walking;
        }

        mediaPlayer.start();
        txt_exercise_indicator.setText(textResource);
        workout_tab_running_gif.setImageResource(icon_resource);
        current_exercise = exercise;
    }


}
