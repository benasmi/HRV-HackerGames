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
import android.widget.DatePicker;
import android.widget.TextView;

import com.mabe.productions.hrv_madison.MainScreenActivity;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.User;
import com.mabe.productions.hrv_madison.Utils;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IntroInitialInfoBirthday extends AppCompatActivity {

    private DatePicker picker_date;
    private Button btn_continue;
    private TextView txt_when_is_your_birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_initial_info_birthday_activity);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());

        initializeViews();
        setFonts();

    }


    private void initializeViews(){

        Animation anim_txt_when_is_your_birthday =  AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);
        Animation anim_picker_date = AnimationUtils.loadAnimation(this, R.anim.fade_in_delay);
        Animation anim_btn_continue = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top_delay);


        picker_date = (DatePicker) findViewById(R.id.datePicker);
        btn_continue = (Button) findViewById(R.id.initial_btn_continue_birthday);
        txt_when_is_your_birthday = (TextView) findViewById(R.id.txt_when_is_your_birthday);

        picker_date.updateDate(1991, 12, 26);

        picker_date.startAnimation(anim_picker_date);
        btn_continue.startAnimation(anim_btn_continue);
        txt_when_is_your_birthday.startAnimation(anim_txt_when_is_your_birthday);
    }


    private void setFonts(){

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        txt_when_is_your_birthday.setTypeface(verdana);
    }


    public void start(View view) {
        int day = picker_date.getDayOfMonth();
        int month = picker_date.getMonth();
        int year =  picker_date.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        Utils.saveToSharedPrefs(this, FeedReaderDbHelper.FIELD_BIRTHDAY,Utils.getStringFromDate(calendar.getTime()),FeedReaderDbHelper.SHARED_PREFS_USER_DATA);

//        startActivity(new Intent(this, IntroInitialBaseProgram.class));
        //todo: remove this dummy program
        User.saveProgram(this, 30f, 2, null, null);
        startActivity(new Intent(this, IntroInitialDaySelection.class));
    }

}
