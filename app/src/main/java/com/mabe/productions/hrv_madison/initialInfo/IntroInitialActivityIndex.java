package com.mabe.productions.hrv_madison.initialInfo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.github.channguyen.rsv.RangeSliderView;
import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

public class IntroInitialActivityIndex extends AppCompatActivity {

    RangeSliderView rng_activity_index;
    RangeSliderView rng_training_duration;
    RangeSliderView rng_training_frequency;

    TextView txt_training_duration_question;
    TextView txt_training_duration_explanation;

    TextView txt_training_frequency_question;
    TextView txt_training_frequency_explanation;

    TextView txt_training_intensity_question;
    TextView txt_training_intensity_explanation;

    Button btn_next;

    int i_training_intensity = 0;
    int i_training_frequency = 0;
    int i_training_duration = 0;

    //todo: change sliders

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_initial_activity_index);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());

        initializeViews();


        rng_activity_index.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                i_training_intensity = index+1;

                switch(index){
                    case 0:
                        txt_training_intensity_explanation.setText(R.string.training_intensity_0);
                        break;
                    case 1:
                        txt_training_intensity_explanation.setText(R.string.training_intensity_1);
                        break;
                    case 2:
                        txt_training_intensity_explanation.setText(R.string.training_intensity_2);
                        break;
                    case 3:
                        txt_training_intensity_explanation.setText(R.string.training_intensity_3);
                        break;
                    case 4:
                        txt_training_intensity_explanation.setText(R.string.training_intensity_4);
                        break;
                }
            }
        });


        rng_training_frequency.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                i_training_frequency = index+1;
                switch(index){
                    case 0:
                        txt_training_frequency_explanation.setText(R.string.training_frequency_0);
                        break;
                    case 1:
                        txt_training_frequency_explanation.setText(R.string.training_frequency_1);
                        break;
                    case 2:
                        txt_training_frequency_explanation.setText(R.string.training_frequency_2);
                        break;
                    case 3:
                        txt_training_frequency_explanation.setText(R.string.training_frequency_3);
                        break;
                    case 4:
                        txt_training_frequency_explanation.setText(R.string.training_frequency_4);
                        break;
                }
            }
        });

        rng_training_duration.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                i_training_duration = index+1;
                switch(index){
                    case 0:
                        txt_training_duration_explanation.setText(R.string.training_duration_0);
                        break;
                    case 1:
                        txt_training_duration_explanation.setText(R.string.training_duration_1);
                        break;
                    case 2:
                        txt_training_duration_explanation.setText(R.string.training_duration_2);
                        break;
                    case 3:
                        txt_training_duration_explanation.setText(R.string.training_duration_3);
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

        btn_next = (Button) findViewById(R.id.initial_continue_activity_index);

        rng_activity_index = (RangeSliderView) findViewById(R.id.activity_index);
        rng_training_duration = (RangeSliderView) findViewById(R.id.training_duration);
        rng_training_frequency = (RangeSliderView) findViewById(R.id.training_frequency);

        txt_training_duration_question = (TextView) findViewById(R.id.training_duration_question);
        txt_training_duration_explanation = (TextView) findViewById(R.id.training_duration_explanation);

        txt_training_frequency_question = (TextView) findViewById(R.id.training_frequency_question);
        txt_training_frequency_explanation = (TextView) findViewById(R.id.training_frequency_explanation);

        txt_training_intensity_question = (TextView) findViewById(R.id.activity_index_question);
        txt_training_intensity_explanation = (TextView) findViewById(R.id.activity_index_explanation);

        //Anims
        txt_training_duration_question.startAnimation(anim_txt_top_down);
        txt_training_frequency_question.startAnimation(anim_txt_top_down);
        txt_training_intensity_question.startAnimation(anim_txt_top_down);

        rng_activity_index.startAnimation(anim_left_to_right);
        rng_training_duration.startAnimation(anim_left_to_right);
        rng_training_frequency.startAnimation(anim_left_to_right);

        txt_training_duration_explanation.startAnimation(anim_right_to_left);
        txt_training_frequency_explanation.startAnimation(anim_right_to_left);
        txt_training_intensity_explanation.startAnimation(anim_right_to_left);


        btn_next.startAnimation(anim_next);

    }


    public void next(View view) {
        int activity_index = i_training_duration*i_training_frequency*i_training_intensity;
        Utils.saveToSharedPrefs(this, FeedReaderDbHelper.FIELD_ACTIVITY_INDEX, activity_index,FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
        Utils.saveToSharedPrefs(this, FeedReaderDbHelper.FIELD_DONE_INITIAL,true,FeedReaderDbHelper.SHARED_PREFS_USER_DATA);

        startActivity(new Intent(this, IntroInitialInfoBirthday.class));
    }
}
