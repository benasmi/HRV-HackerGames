package com.mabe.productions.hrv_madison.initialInfo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class IntroInitialMaxDuration extends AppCompatActivity {

    private Button btn_continue;
    private TextView max_duration_value;
    private TextView txt_min;
    private boolean clickedOnce = false;
    private TextView txt_question;
    private DiscreteSeekBar duration_slider;
    private int maxDur = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_initial_max_duration_activity);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());

        //Saving intro progress
        initializeViews();
        setFonts();
    }


    private void initializeViews(){


        Animation anim_txt_value = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation anim_how_much_time = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation anim_slider_picker = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        Animation anim_btn_continue = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);

        duration_slider = (DiscreteSeekBar) findViewById(R.id.max_duration_slider);
        btn_continue = (Button) findViewById(R.id.initial_btn_continue_max_duration);
        max_duration_value = (TextView) findViewById(R.id.txt_max_duration_value);
        txt_question = (TextView) findViewById(R.id.txt_what_is_your_max_duration);
        txt_min = (TextView) findViewById(R.id.txt_min_text);

        duration_slider.startAnimation(anim_slider_picker);
        txt_question.startAnimation(anim_how_much_time);
        btn_continue.startAnimation(anim_btn_continue);

        max_duration_value.startAnimation(anim_txt_value);
        txt_min.startAnimation(anim_txt_value);


        duration_slider.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                max_duration_value.setText(String.valueOf(value));
                maxDur = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

    }

    private void setFonts(){

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        max_duration_value.setTypeface(verdana);
        txt_min.setTypeface(verdana);
        txt_question.setTypeface(verdana);

    }

    public void startMain(View view) {
    if(!clickedOnce){
        clickedOnce=true;
        if(maxDur<=15){
            clickedOnce = false;
            Toast.makeText(IntroInitialMaxDuration.this,"Pasirinkite didesnį periodą, negu 15min!", Toast.LENGTH_LONG).show();
        }else{
            Utils.saveToSharedPrefs(this,FeedReaderDbHelper.FIELD_BASE_DURATION, (float) maxDur,FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
            Utils.saveToSharedPrefs(this, FeedReaderDbHelper.FIELD_DONE_INITIAL, true, FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
            startActivity(new Intent(this, MainScreenActivity.class));
        }
    }



    }
}
