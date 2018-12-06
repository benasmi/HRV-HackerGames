package com.mabe.productions.hrv_madison.initialInfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.PulseZoneView;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.UserOptionsPanelActivity;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebase.FirebaseUtils;

public class IntroInitialDaySelection extends AppCompatActivity {

    private AppCompatButton btn_monday;
    private AppCompatButton btn_tuesday;
    private AppCompatButton btn_wednesday;
    private AppCompatButton btn_thursday;
    private AppCompatButton btn_friday;
    private AppCompatButton btn_saturday;
    private AppCompatButton btn_sunday;
    private TextView txt_question;
    private Button btn_continue;

    boolean fromOptions = false;

    boolean[] week_days = {true, true, false, true, true, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_initial_day_selection);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());
        fromOptions = getIntent().getExtras().getBoolean("FromOptions");
        initializeViews();
        setFonts();
        if(fromOptions){
            for(int i = 0; i<week_days.length; i++){
                Log.i("Weeks", "Pasirinkta: " + week_days[i]);
            }

            week_days = Utils.readFromSharedPrefs_boolarray(IntroInitialDaySelection.this,FeedReaderDbHelper.FIELD_WEEK_DAYS, FeedReaderDbHelper.SHARED_PREFS_USER_DATA);

            Log.i("Weeks", "Po nuskaitymo");

            for(int i = 0; i<week_days.length; i++){
                Log.i("Weeks", "Pasirinkta: " + week_days[i]);
            }


            btn_monday.setBackgroundResource(week_days[0] ?  R.drawable.login_button_gradient: R.drawable.login_socialmedia);
            btn_tuesday.setBackgroundResource(week_days[1] ? R.drawable.login_button_gradient: R.drawable.login_socialmedia );
            btn_wednesday.setBackgroundResource(week_days[2] ? R.drawable.login_button_gradient :  R.drawable.login_socialmedia);
            btn_thursday.setBackgroundResource(week_days[3] ?  R.drawable.login_button_gradient : R.drawable.login_socialmedia );
            btn_friday.setBackgroundResource(week_days[4] ? R.drawable.login_button_gradient : R.drawable.login_socialmedia );
            btn_saturday.setBackgroundResource(week_days[5] ? R.drawable.login_button_gradient : R.drawable.login_socialmedia );
            btn_sunday.setBackgroundResource(week_days[6] ? R.drawable.login_button_gradient : R.drawable.login_socialmedia );

            btn_continue.setText("Done");
        }

        btn_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(week_days[0] ? R.drawable.login_socialmedia : R.drawable.login_button_gradient);
                week_days[0]=!week_days[0];
            }
        });

        btn_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(week_days[1] ? R.drawable.login_socialmedia : R.drawable.login_button_gradient);
                week_days[1]=!week_days[1];
            }
        });

        btn_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(week_days[2] ? R.drawable.login_socialmedia : R.drawable.login_button_gradient);
                week_days[2]=!week_days[2];
            }
        });

        btn_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(week_days[3] ? R.drawable.login_socialmedia : R.drawable.login_button_gradient);
                week_days[3]=!week_days[3];
            }
        });

        btn_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(week_days[4] ? R.drawable.login_socialmedia : R.drawable.login_button_gradient);
                week_days[4]=!week_days[4];
            }
        });

        btn_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(week_days[5] ? R.drawable.login_socialmedia : R.drawable.login_button_gradient);
                week_days[5]=!week_days[5];
            }
        });

        btn_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(week_days[6] ? R.drawable.login_socialmedia : R.drawable.login_button_gradient);
                week_days[6]=!week_days[6];
            }
        });

    }




    private void setFonts(){

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");
        btn_monday.setTypeface(verdana);
        btn_tuesday.setTypeface(verdana);
        btn_wednesday.setTypeface(verdana);
        btn_thursday.setTypeface(verdana);
        btn_friday.setTypeface(verdana);
        btn_saturday.setTypeface(verdana);
        btn_sunday.setTypeface(verdana);

        txt_question.setTypeface(verdana);

    }

    private void initializeViews(){

        Animation anim_question_what_days =  AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);
        Animation anim_right_to_left = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        Animation anim_left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation anim_btn_continue = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);

        txt_question = (TextView) findViewById(R.id.txt_what_is_your_workout_routine);

        btn_monday = (AppCompatButton) findViewById(R.id.btn_monday);
        btn_tuesday = (AppCompatButton) findViewById(R.id.btn_tuesday);
        btn_wednesday = (AppCompatButton) findViewById(R.id.btn_wednesday);
        btn_thursday = (AppCompatButton) findViewById(R.id.btn_thursday);
        btn_friday = (AppCompatButton) findViewById(R.id.btn_friday);
        btn_saturday = (AppCompatButton) findViewById(R.id.btn_saturday);
        btn_sunday = (AppCompatButton) findViewById(R.id.btn_sunday);
        btn_continue = (Button) findViewById(R.id.initial_btn_continue_day_selection);

        txt_question.startAnimation(anim_question_what_days);

        btn_monday.startAnimation(anim_right_to_left);
        btn_tuesday.startAnimation(anim_left_to_right);
        btn_wednesday.startAnimation(anim_right_to_left);
        btn_thursday.startAnimation(anim_left_to_right);
        btn_friday.startAnimation(anim_right_to_left);
        btn_saturday.startAnimation(anim_left_to_right);
        btn_sunday.startAnimation(anim_right_to_left);

        btn_continue.startAnimation(anim_btn_continue);



    }

    public void start(View view) {
        int selectedCount = 0;
        for(int i = 0; i < week_days.length; i++){
            if(week_days[i]){
                selectedCount++;
            }
        }


        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final DatabaseReference fireDatabase = FirebaseDatabase.getInstance().getReference(FirebaseUtils.USERS_TABLE_RUNNING + "/" + user.getUid());

        if(selectedCount < 2) {
            Toast.makeText(this, R.string.select_at_least_two_days, Toast.LENGTH_LONG).show();
            return;
        }

        if(selectedCount>4){
            Utils.buildAlertDialogPrompt(this, "Are you sure with your days selection?", "It's not healthy to workout more than 4 days for begginers. Are you sure with your selections?", "Continue", "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fireDatabase.child("workout_days").setValue(FeedReaderDbHelper.weekDaysToString(week_days));
                    Utils.saveToSharedPrefs(IntroInitialDaySelection.this, FeedReaderDbHelper.FIELD_WEEK_DAYS, week_days, FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                    if(fromOptions){
                        IntroInitialDaySelection.this.finish();
                        startActivity(new Intent(IntroInitialDaySelection.this, UserOptionsPanelActivity.class));
                    }else{
                        startActivity(new Intent(IntroInitialDaySelection.this, IntroInitialMaxDuration.class));

                    }
                }
            },null);
            return;
        }

        for(int i = 0; i<week_days.length; i++){
            Log.i("Weeks", "Pasirinkta: " + week_days[i]);
        }

        Utils.saveToSharedPrefs(IntroInitialDaySelection.this, FeedReaderDbHelper.FIELD_WEEK_DAYS, week_days, FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
        fireDatabase.child("workout_days").setValue(FeedReaderDbHelper.weekDaysToString(week_days));
        if(fromOptions){
            IntroInitialDaySelection.this.finish();
            startActivity(new Intent(IntroInitialDaySelection.this, UserOptionsPanelActivity.class));
        }else{
            startActivity(new Intent(IntroInitialDaySelection.this, IntroInitialMaxDuration.class));

        }




    }
}
