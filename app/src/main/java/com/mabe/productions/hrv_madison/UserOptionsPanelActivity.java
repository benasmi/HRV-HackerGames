package com.mabe.productions.hrv_madison;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialDaySelection;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialHeight;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialWeight;

public class UserOptionsPanelActivity extends AppCompatActivity {


    private ImageView img_back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_options_panel);

        initializeViews();
        Utils.changeNotifBarColor(Color.parseColor("#2c3e50"), getWindow());
        //Back
        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserOptionsPanelActivity.this.finish();
            }
        });


    }

    private void initializeViews() {
        img_back_arrow = (ImageView) findViewById(R.id.img_back_arrow);

    }


    public void logout(View view) {
        Utils.buildAlertDialogPrompt(this,
                "Warning",
                "Do you really want to log out?",
                "Yes",
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth fAuth = FirebaseAuth.getInstance();
                        fAuth.signOut();

                        //Clearing all local data
                        User.removeAllMeasurements(UserOptionsPanelActivity.this);
                        User.removeAllWorkouts(UserOptionsPanelActivity.this);
                        User.removeAllPersonalData(UserOptionsPanelActivity.this);
                        UserOptionsPanelActivity.this.finish();
                        startActivity(new Intent(UserOptionsPanelActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                },
                null);
    }


    public void changeWorkoutDays(View view) {
        UserOptionsPanelActivity.this.finish();
        startActivity(new Intent(UserOptionsPanelActivity.this, IntroInitialDaySelection.class).putExtra("FromOptions", true));
    }

    public void changeHeight(View view) {

        startActivity(new Intent(UserOptionsPanelActivity.this, IntroInitialHeight.class).putExtra("FromOptions", true));
        UserOptionsPanelActivity.this.finish();
    }

    public void changeWeight(View view) {
        startActivity(new Intent(UserOptionsPanelActivity.this, IntroInitialWeight.class).putExtra("FromOptions", true));
        UserOptionsPanelActivity.this.finish();

    }
}
