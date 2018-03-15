package com.mabe.productions.hrv_madison.fragments;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class WorkoutFragment extends Fragment {

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

    private CountDownTimer countDownTimer = null;

    public boolean shouldStartWorkoutImmediately = false;

    private static final int STATE_BEFORE_WORKOUT = 0;
    private static final int STATE_WORKING_OUT = 1;
    private static final int STATE_TIME_ENDED = 2;

    int workout_state = STATE_BEFORE_WORKOUT;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.workout_fragment, container, false);

        initializeViews(view);

        return view;
    }

    public void disconnected(){
        txt_connection_status.setText(R.string.failed_connection_status);
        workout_state = STATE_BEFORE_WORKOUT;
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
        setupEditTextBehavior();

        btn_toggle = rootView.findViewById(R.id.button_start_workout);
        btn_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (workout_state){

                    case STATE_BEFORE_WORKOUT:

                        //if device is connected
                        if(BluetoothGattService.isGattDeviceConnected){
                            startWorkout();

                        }else{
                            autoConnectDevice();
                        }




                        break;

                    case STATE_TIME_ENDED:
                        workout_state = STATE_BEFORE_WORKOUT;
                        //TODO: Save data to db, since workout is over


                        //todo: use static variable to switch tabs
                        ViewPager parentViewPager = getActivity().findViewById(R.id.viewpager);
                        ViewPagerAdapter adapter = (ViewPagerAdapter) parentViewPager.getAdapter();
                        adapter.dataTodayFragment.updateData();
                        parentViewPager.setCurrentItem(1);

                        break;
                }
            }
        });
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


    public void startWorkout(){
        workout_state = STATE_WORKING_OUT;
        btn_toggle.setText(R.string.start_training);
        btn_toggle.setVisibility(View.GONE);
        img_pause.setVisibility(View.VISIBLE);
        img_stop.setVisibility(View.VISIBLE);
        layout_workout_progress.setVisibility(View.VISIBLE);
        editText_minutes.setEnabled(false);
        editText_seconds.setEnabled(false);

        int minutes = Integer.valueOf(editText_minutes.getText().toString());
        int seconds = Integer.valueOf(editText_seconds.getText().toString());
        final int duration = minutes * 60000 + seconds * 1000;


        //Starting measuring
        countDownTimer = new CountDownTimer(duration,1000l){
            @Override
            public void onTick(long l) {
                setProgressBarDuration(duration, (int) l);

                int minutes = (int) l/60000;
                int seconds = Math.round(l/1000 - (minutes*60));
                if(seconds < 10){
                    editText_minutes.setText(minutes + "");
                    editText_seconds.setText("0" + seconds);
                }else{
                    editText_seconds.setText(seconds + "");
                    editText_minutes.setText(minutes + "");
                }
            }

            @Override
            public void onFinish() {
                timeEnded();
            }
        }.start();


    }

    private void timeEnded(){
        workout_state = STATE_TIME_ENDED;
        editText_minutes.setText("00");
        editText_seconds.setText("00");
        progressbar_duration.setProgress(100f);

        btn_toggle.setText(R.string.end_training);
        btn_toggle.setVisibility(View.VISIBLE);
    }



    public void onMeasurement(int bpm, int[] intervals){
        txt_bpm.setText(bpm + "");
    }


    private void setProgressBarDuration(int duration, int timePassed){
        float percentage = ( (float) timePassed) / ((float) duration);
        progressbar_duration.setProgress(100-percentage*100);

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
