package com.mabe.productions.hrv_madison;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.firebaseDatase.FireUser;
import com.mabe.productions.hrv_madison.firebaseDatase.FirebaseUtils;
import com.mabe.productions.hrv_madison.fragments.DataTodayFragment;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialPage;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView img_login_appicon;
    private TextView txt_slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        initializeViews();
        setFonts();
    }


    private void initializeViews(){

        img_login_appicon = (ImageView) findViewById(R.id.intro_appicon);
        txt_slogan = (TextView) findViewById(R.id.slogan);

        //Starting the pre-loop animation for the app icon, and after it ends, starting the loop
        AnimatedVectorDrawableCompat animatedIconVectorIntro = AnimatedVectorDrawableCompat.create(this, R.drawable.intro_screen_anim_2);
        final AnimatedVectorDrawableCompat animatedIconVectorLoop = AnimatedVectorDrawableCompat.create(this, R.drawable.app_icon_anim_loop);


        img_login_appicon.setImageDrawable(animatedIconVectorIntro);
        Animation titleAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.app_logo_fade_in);
        titleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                /*
                boolean doneInitial = Utils.readFromSharedPrefs_bool(SplashScreenActivity.this, FeedReaderDbHelper.FIELD_DONE_INITIAL, FeedReaderDbHelper.SHARED_PREFS_USER_DATA);
                Log.i("TEST", String.valueOf(doneInitial));
                startActivity(new Intent(SplashScreenActivity.this, doneInitial == true? MainScreenActivity.class : LoginActivity.class)); //kazkada pakeisim px
            */
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser!=null){

                FirebaseUtils.isInitialDone(new FirebaseUtils.OnInitialDoneFetchListener() {
                    @Override
                    public void onSuccess(boolean isInitialDone) {
                        Log.i("auth", "SplashOnInitial: " + String.valueOf(isInitialDone));
                        if(isInitialDone){
                            startActivity(new Intent(SplashScreenActivity.this, MainScreenActivity.class));
                        }else{
                            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                        }
                    }
                    @Override
                    public void onFailure(DatabaseError error) {

                    }
                });



            }else{
                startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));

            }


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        txt_slogan.startAnimation(titleAnim);


        animatedIconVectorIntro.start();
    }

    private void setFonts(){

        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");
        txt_slogan.setTypeface(futura);



    }
}
