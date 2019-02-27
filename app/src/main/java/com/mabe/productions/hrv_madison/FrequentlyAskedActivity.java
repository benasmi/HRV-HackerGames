package com.mabe.productions.hrv_madison;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FrequentlyAskedActivity extends AppCompatActivity {

    private TextView txt_what_is_hrv_faq;
    private TextView txt_what_is_hrv_answer_faq;
    private TextView txt_what_hrv_represents_answer_faq;
    private TextView txt_what_hrv_represents_faq;
    private TextView txt_where_hrv_used_faq;
    private TextView txt_where_hrv_used_answer_faq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeNotifBarColor(Color.parseColor("#2c3e50"), getWindow());
        setContentView(R.layout.activity_frequently_asked);
        initializeViews();
        setFonts();
    }

    private void initializeViews() {
        txt_what_is_hrv_faq = (TextView) findViewById(R.id.txt_what_is_hrv_faq);
        txt_what_is_hrv_answer_faq = (TextView) findViewById(R.id.txt_what_is_hrv_answer_faq);
        txt_what_hrv_represents_answer_faq = (TextView) findViewById(R.id.txt_what_hrv_represents_answer_faq);
        txt_what_hrv_represents_faq = (TextView) findViewById(R.id.txt_what_hrv_represents_faq);
        txt_where_hrv_used_faq = (TextView) findViewById(R.id.txt_where_hrv_used_faq);
        txt_where_hrv_used_answer_faq = (TextView) findViewById(R.id.txt_where_hrv_used_answer_faq);
    }

    private void setFonts() {
        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        txt_what_is_hrv_faq.setTypeface(futura);
        txt_what_is_hrv_answer_faq.setTypeface(futura);
        txt_what_hrv_represents_answer_faq.setTypeface(futura);
        txt_what_hrv_represents_faq.setTypeface(futura);
        txt_where_hrv_used_faq.setTypeface(futura);
        txt_where_hrv_used_answer_faq.setTypeface(futura);
    }
}
