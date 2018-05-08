package com.mabe.productions.hrv_madison.initialInfo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.shawnlin.numberpicker.NumberPicker;

public class IntroInitialWeight extends AppCompatActivity {

    NumberPicker weight_picker;
    private TextView txt_how_heavy;
    private TextView txt_value;
    private TextView txt_text_kg;
    private Button btn_continue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_initial_weight_activity);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());

        initializeViews();
        setFonts();


    }

    private void setFonts(){


        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");
  ;

        txt_value.setTypeface(verdana);
        txt_text_kg.setTypeface(verdana);
        txt_how_heavy.setTypeface(verdana);

    }

    private void initializeViews(){

        Animation anim_txt_text_kg =  AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation anim_txt_value = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation anim_txt_how_heavy = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation anim_weight_picker = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        Animation anim_btn_continue = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);


        txt_text_kg = (TextView) findViewById(R.id.txt_kg_text);
        txt_value = (TextView) findViewById(R.id.txt_weight_value);
        txt_how_heavy = (TextView) findViewById(R.id.txt_what_is_your_weight);
        btn_continue = (Button) findViewById(R.id.initial_btn_continue_weight);

        weight_picker = (NumberPicker) findViewById(R.id.number_picker);
        weight_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                txt_value.setText(String.valueOf(newVal));
            }
        });

        btn_continue.startAnimation(anim_btn_continue);
        txt_value.startAnimation(anim_txt_value);
        txt_text_kg.startAnimation(anim_txt_text_kg);
        txt_how_heavy.startAnimation(anim_txt_how_heavy);
        weight_picker
                .startAnimation(anim_weight_picker);
    }

    public void start(View view) {
        Utils.saveToSharedPrefs(this, FeedReaderDbHelper.FIELD_WEIGHT,Float.parseFloat(txt_value.getText().toString()),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);

        float height = Utils.readFromSharedPrefs_float(this,FeedReaderDbHelper.FIELD_HEIGHT,FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
        float KMI = height/Utils.readFromSharedPrefs_float(this,FeedReaderDbHelper.FIELD_WEIGHT,FeedReaderDbHelper.SHARED_PREFS_USER_DATA);


        Utils.saveToSharedPrefs(this,FeedReaderDbHelper.FIELD_KMI,KMI,FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
        startActivity(new Intent(this, IntroInitialGender.class));
    }
}
