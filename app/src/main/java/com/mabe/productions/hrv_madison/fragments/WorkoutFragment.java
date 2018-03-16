package com.mabe.productions.hrv_madison.fragments;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.bluetooth.BluetoothGattService;
import com.mabe.productions.hrv_madison.bluetooth.LeDevicesDialog;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

import java.util.Timer;
import java.util.TimerTask;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.workout_fragment, container, false);

        initializeViews(view);
        setState(STATE_BEFORE_WORKOUT);

        return view;
    }

    public void disconnected(){
        txt_connection_status.setText(R.string.failed_connection_status);
        //If user is measuring, pausing the measurement
        if(workout_state != STATE_BEFORE_WORKOUT){
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
            //todo: save data

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
                btn_toggle.setVisibility(View.VISIBLE);
                btn_toggle.setText(R.string.start_training);
                btn_toggle.setOnClickListener(startTrainingButtonListener);

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


                progressbar_duration.setProgress(100f);

                btn_toggle.setText(R.string.end_training);
                btn_toggle.setVisibility(View.VISIBLE);
                btn_toggle.setOnClickListener(reviewProgressButtonListener);
                Utils.vibrate(getContext(), VIBRATE_DURATION_TIME_ENDED);
                //todo: decide how to show extra time user has been working out
                break;

            case STATE_PAUSED:
                pauseTimer();
                img_pause.setImageResource(R.drawable.ic_resume);
                img_pause.setOnClickListener(resumeButtonListener);

                break;

        }
    }




    public void onMeasurement(int bpm){
        txt_bpm.setText(String.valueOf(bpm));

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
