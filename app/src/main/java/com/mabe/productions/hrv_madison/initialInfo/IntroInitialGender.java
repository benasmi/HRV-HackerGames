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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

public class IntroInitialGender extends AppCompatActivity {

    private ImageView img_male;
    private ImageView img_female;

    int selectedGender = -1;
    private TextView txt_question;
    private Button btn_continue;
    private ImageView img_divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_initial_gender_activity);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());

        initializeViews();
        setFonts();






    }

    private void initializeViews(){

        Animation anim_img_male = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);
        Animation anim_img_female = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        Animation anim_txt_question = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);
        Animation anim_btn_continue = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);
        Animation anim_divider = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        img_male = (ImageView) findViewById(R.id.male_icon);
        img_female = (ImageView) findViewById(R.id.female_icon);
        txt_question = (TextView) findViewById(R.id.txt_what_is_your_gender);
        btn_continue = (Button) findViewById(R.id.initial_btn_continue_gender);
        img_divider = (ImageView) findViewById(R.id.initial_img_divider);

        img_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_male.setImageResource(R.drawable.ic_superior_selected);
                img_female.setImageResource(R.drawable.ic_female);
                selectedGender = User.GENDER_MALE;
            }
        });

        img_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_male.setImageResource(R.drawable.ic_superior);
                img_female.setImageResource(R.drawable.ic_female_selected);
                selectedGender = User.GENDER_FEMALE;
            }
        });

        img_female.startAnimation(anim_img_female);
        img_male.startAnimation(anim_img_male);
        txt_question.startAnimation(anim_txt_question);
        btn_continue.startAnimation(anim_btn_continue);
        img_divider.startAnimation(anim_divider);
    }


    private void setFonts(){

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        txt_question.setTypeface(verdana);



    }

    public void start(View view) {
        if(selectedGender!=-1){
            Utils.saveToSharedPrefs(this, FeedReaderDbHelper.FIELD_GENDER, selectedGender,FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
            startActivity(new Intent(this, IntroInitialActivityIndex.class));
        }else{
            Toast.makeText(this, R.string.please_select_gender, Toast.LENGTH_LONG).show();
        }
    }
}
