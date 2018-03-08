package com.mabe.productions.hrv_madison;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.initialInfo.IntroInitialPage;

public class LoginActivity extends AppCompatActivity {

    private TextView txt_slogan;
    private TextView txt_noAccount;
    private TextView txt_noRegistration;


    private EditText editText_username;
    private EditText editText_password;
    private Button btn_login;
    private ImageButton imgBtn_facebook;
    private ImageButton imgBtn_google;
    private ImageView circle;
    private ImageView img_login_appicon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Utils.changeNotifBarColor(Color.parseColor("#3e5266"),getWindow());

        initializeViews();
        setFonts();



        //Starting the pre-loop animation for the app icon, and after it ends, starting the loop
        AnimatedVectorDrawableCompat animatedIconVectorIntro = AnimatedVectorDrawableCompat.create(this, R.drawable.intro_screen_anim_2);
        final AnimatedVectorDrawableCompat animatedIconVectorLoop = AnimatedVectorDrawableCompat.create(this, R.drawable.app_icon_anim_loop);

        //Basically just a listener that loops the animation
        animatedIconVectorLoop.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                img_login_appicon.post(new Runnable() {
                    @Override
                    public void run() {
                        animatedIconVectorLoop.start();
                    }
                });
            }
        });

        img_login_appicon.setImageDrawable(animatedIconVectorIntro);
        animatedIconVectorIntro.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                img_login_appicon.post(new Runnable() {
                    @Override
                    public void run() {
                        img_login_appicon.setImageDrawable(animatedIconVectorLoop);
                        animatedIconVectorLoop.start();
                    }
                });
            }
        });

        animatedIconVectorIntro.start();


        
    }

    private void initializeViews(){


        Animation left_to_right = AnimationUtils.loadAnimation(this,R.anim.left_to_right);
        Animation right_to_left = AnimationUtils.loadAnimation(this,R.anim.right_to_left);
        Animation bottom_to_top = AnimationUtils.loadAnimation(this,R.anim.bottom_to_top);
        Animation top_to_bottom  = AnimationUtils.loadAnimation(this,R.anim.bottom_to_top);
        Animation fade_in  = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        Animation fade_in_delay  = AnimationUtils.loadAnimation(this,R.anim.fade_in_delay);
        Animation top_to_bottom_delay  = AnimationUtils.loadAnimation(this,R.anim.top_to_bottom_delay);


        txt_slogan = (TextView) findViewById(R.id.slogan);
        txt_noAccount = (TextView) findViewById(R.id.noAccount);
        txt_noRegistration = (TextView) findViewById(R.id.noRegistration);
        editText_username = (EditText) findViewById(R.id.usernameEditText);
        editText_password = (EditText) findViewById(R.id.passwordEditText);
        imgBtn_facebook = (ImageButton) findViewById(R.id.login_facebook);
        imgBtn_google = (ImageButton) findViewById(R.id.login_googlePlus);
        btn_login = (Button) findViewById(R.id.buttonLogin);
        circle = (ImageView) findViewById(R.id.circleDot);
        img_login_appicon = (ImageView) findViewById(R.id.imageView);

        imgBtn_facebook.startAnimation(left_to_right);
        imgBtn_google.startAnimation(right_to_left);
        btn_login.startAnimation(bottom_to_top);
        txt_noAccount.startAnimation(top_to_bottom);
        circle.startAnimation(fade_in);
        editText_username.startAnimation(fade_in_delay);
        editText_password.startAnimation(fade_in_delay);
        txt_noRegistration.startAnimation(fade_in_delay);
        txt_slogan.startAnimation(top_to_bottom_delay);

    }

    private void setFonts(){
        Typeface face_slogan = Typeface.createFromAsset(getAssets(),
                "fonts/CORBEL.TTF");
        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");

        Typeface verdana = Typeface.createFromAsset(getAssets(),
                "fonts/Verdana.ttf");

        txt_slogan.setTypeface(face_slogan);
        txt_noRegistration.setTypeface(verdana);
        btn_login.setTypeface(futura);
        editText_username.setTypeface(futura);
        editText_password.setTypeface(futura);
        txt_noAccount.setTypeface(verdana);
    }

    public void login(View view) {

            if(Utils.readFromSharedPrefs_bool(this,FeedReaderDbHelper.FIELD_DONE_INITIAL,FeedReaderDbHelper.SHARED_PREFS_USER_DATA)){
                startActivity(new Intent(LoginActivity.this, MainScreenActivity.class));
            }else{
                startActivity(new Intent(LoginActivity.this, IntroInitialPage.class));
            }

    }
}
