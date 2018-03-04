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

        //todo: remove this dummy data
        Utils.saveToSharedPrefs(this, FeedReaderDbHelper.FIELD_WEEK_DAYS, new boolean[]{true, true, true, true, true, true, true}, FeedReaderDbHelper.SHARED_PREFS_USER_DATA);


        //todo: this crashes on several phone models or versions
        //Checking if android version supports animated vector drawables. If it doesn't, regular drawable is set.
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //    final AnimatedVectorDrawable d = (AnimatedVectorDrawable) img_login_appicon.getDrawable();
        //    d.start();
        //}else{
        //    img_login_appicon.setImageResource(R.drawable.ic_appicon_rectangle);
        //}

        
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
