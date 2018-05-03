package com.mabe.productions.hrv_madison;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FrequentlyAskedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeNotifBarColor(Color.parseColor("#2c3e50"), getWindow());
        setContentView(R.layout.activity_frequently_asked);
    }
}
