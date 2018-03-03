package com.mabe.productions.hrv_madison.initialInfo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.Utils;

public class IntroInitialMaxDuration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_initial_max_duration_activity);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());
    }

    public void start(View view) {

    }
}
