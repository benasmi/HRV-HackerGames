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
import com.qindachang.widget.RulerView;

public class IntroInitialHeight extends AppCompatActivity {

    private TextView txt_text_cm;
    private TextView txt_how_tall;
    private TextView txt_value;
    private RulerView heightValuePicker;
    private Button btn_continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_initial_height_activity);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());

        initializeViews();
        setFonts();

    }


    private void initializeViews(){

        Animation anim_txt_how_tall = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        Animation anim_heightValuePicker = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        Animation anim_btn = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top_delay);
        Animation anim_txt_value = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);
        Animation anim_txt_text_cm = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);

        txt_text_cm = (TextView) findViewById(R.id.txt_cm_text);
        txt_value = (TextView) findViewById(R.id.txt_height_value);
        txt_how_tall = (TextView) findViewById(R.id.txt_what_is_your_height);
        btn_continue = (Button) findViewById(R.id.initiral_continue_height);

        heightValuePicker = (RulerView) findViewById(R.id.height_value_picker);

        heightValuePicker.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                txt_value.setText(String.valueOf( (int) value));
            }
        });


        heightValuePicker.startAnimation(anim_heightValuePicker);
        txt_how_tall.startAnimation(anim_txt_how_tall);
        btn_continue.startAnimation(anim_btn);
        txt_value.startAnimation(anim_txt_value);
        txt_text_cm.startAnimation(anim_txt_text_cm);
    }

    private void setFonts(){


        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/Verdana.ttf");

        txt_text_cm.setTypeface(verdana);
        txt_how_tall.setTypeface(verdana);

    }


    public void start(View view) {

        Utils.saveToSharedPrefs(this, FeedReaderDbHelper.FIELD_HEIGHT,Float.parseFloat(txt_value.getText().toString()),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
        startActivity(new Intent(this, IntroInitialWeight.class));
    }
}
