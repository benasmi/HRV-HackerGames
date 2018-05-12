package com.mabe.productions.hrv_madison.initialInfo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.github.channguyen.rsv.RangeSliderView;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

public class IntroInitialBaseProgram extends AppCompatActivity {


    private RangeSliderView workout_level_slider;
    private TextView txt_what_is_your_base_workout;
    private TextView txt_workout_level;
    private TextView txt_info_about_workout;
    private TextView txt_rec_workout_plan;
    private TextView txt_reccomended_base_workout_time;
    private TextView txt_reccomended_base_workout_pulse_zone;

    private Button btn_next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_initial_base_program);

        initializeViews();
        setFonts();


        workout_level_slider.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {

                //TODO: set workout definitions and programs
                switch(index){
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                }

            }
        });

    }


    private void initializeViews(){


        Animation anim_txt_top_down = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);
        Animation anim_left_to_right = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation anim_right_to_left = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        Animation anim_next = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top_delay);


        workout_level_slider = (RangeSliderView) findViewById(R.id.workout_level_slider);
        btn_next = (Button) findViewById(R.id.initial_continue_base_program);
        txt_what_is_your_base_workout = (TextView) findViewById(R.id.txt_what_is_your_base_workout);
        txt_workout_level = (TextView) findViewById(R.id.txt_workout_level);
        txt_info_about_workout = (TextView) findViewById(R.id.txt_info_about_workout);
        txt_rec_workout_plan = (TextView) findViewById(R.id.txt_rec_workout_plan);
        txt_reccomended_base_workout_time = (TextView) findViewById(R.id.txt_reccomended_base_workout_time);
        txt_reccomended_base_workout_pulse_zone = (TextView) findViewById(R.id.txt_reccomended_base_workout_pulse_zone);



        txt_what_is_your_base_workout.startAnimation(anim_txt_top_down);
        txt_workout_level.startAnimation(anim_txt_top_down);
        workout_level_slider.startAnimation(anim_left_to_right);
        txt_info_about_workout.startAnimation(anim_left_to_right);

        txt_rec_workout_plan.startAnimation(anim_right_to_left);
        txt_reccomended_base_workout_time.startAnimation(anim_right_to_left);
        txt_reccomended_base_workout_pulse_zone.startAnimation(anim_right_to_left);


        btn_next.startAnimation(anim_next);

    }


    public void next(View view) {

        User.saveProgram(this, 30f, 2, null);
        startActivity(new Intent(this, IntroInitialDaySelection.class));
    }


    private void setFonts(){


        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        txt_what_is_your_base_workout.setTypeface(futura);
        txt_workout_level.setTypeface(futura);
        txt_info_about_workout.setTypeface(futura);
        txt_rec_workout_plan.setTypeface(futura);
        txt_reccomended_base_workout_time.setTypeface(futura);
        txt_reccomended_base_workout_pulse_zone.setTypeface(futura);

    }
}
