package com.mabe.productions.hrv_madison;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.mabe.productions.hrv_madison.bluetooth.BluetoothGattService;
import com.mabe.productions.hrv_madison.bluetooth.LeDevicesDialog;
import com.mabe.productions.hrv_madison.database.FeedReaderDbHelper;
import com.mabe.productions.hrv_madison.fragments.ViewPagerAdapter;
import com.mabe.productions.hrv_madison.measurements.RMSSD;

import java.util.Calendar;
import java.util.Date;

public class MainScreenActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private TextView txt_toolbar_title;
    private ImageView img_toolbar_login;
    private ImageView img_toolbar_ask;
    private ImageView img_toolbar_add_device;
    private Toolbar toolbar;
    private RMSSD hrv;
    private boolean isReceiverRegistered = false;

    private ViewPagerAdapter viewPagerAdapter;

    private AHBottomNavigation bottomNavigation;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Utils.changeNotifBarColor(Color.parseColor("#2c3e50"),getWindow());

        initializeViews();
        setupBottomBar();
        setFonts();
        autoConnectDevice();
        registerReceiver();


        //Testing sqlite
        SQLiteDatabase db = new FeedReaderDbHelper(this).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderDbHelper.COL_RMSSD, 10000);
        Date date = Calendar.getInstance().getTime();
        values.put(FeedReaderDbHelper.COL_DATE, Utils.getStringFromDate(date));

        db.insert(FeedReaderDbHelper.HRV_DATA_TABLE_NAME, null, values);
        Log.i("TEST", "inserted i guess");

    }

    private void setupBottomBar(){
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.measure, R.drawable.ic_stopwatch,R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.today, R.drawable.ic_circle_chart, R.color.colorAccent);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.history, R.drawable.ic_sand_clock, R.color.colorAccent);

        viewpager.setOffscreenPageLimit(4);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        viewpager.setAdapter(viewPagerAdapter);

        //TODO: set fonts
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#2c3e50"));
       // bottomNavigation.setBehaviorTranslationEnabled(true);

        //bottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);

        bottomNavigation.setAccentColor(Color.parseColor("#F62459"));
        bottomNavigation.setInactiveColor(Color.parseColor("#ffffff"));


   //     bottomNavigation.setForceTint(true);
    //    bottomNavigation.setTranslucentNavigationEnabled(true);
        //bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        //bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigation.setColored(false);
        bottomNavigation.setCurrentItem(1);
        viewpager.setCurrentItem(1);


        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                viewpager.setCurrentItem(position);
                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override public void onPositionChange(int y) {
                viewpager.setCurrentItem(y);
            }
        });
    }


    BroadcastReceiver  broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothGattService.ACTION_CONNECTED:
                    hrv = new RMSSD();
                    BluetoothDevice device = intent.getParcelableExtra("BT_DEVICE");
                    txt_toolbar_title.setText(device.getName());
                    Log.i("TEST", "ACTION_CONNECTED");
                    break;

                case BluetoothGattService.ACTION_DISCONNECTED:
                    Log.i("TEST", "ACTION_DISCONNECTED");
                    //TODO: resources
                    txt_toolbar_title.setText("No device");
                    break;

                case BluetoothGattService.ACTION_RECEIVING_DATA:

                    Log.i("TEST", "ACTION_RECEIVING_DATA");

                    int intervals[] = intent.getExtras().getIntArray("RR_intervals");
                    hrv.addIntervals(intervals);
                    int bpm = intent.getExtras().getInt("BPM");
                    viewPagerAdapter.measurementFragment.onMeasurement(bpm, intervals);
                    //Log.i("TEST", "ACTION_RECEIVING_DATA" + "BPM: " + bpm + " | " + "RMSSD: " + String.valueOf(hrv.calculateRMSSD()));
                    break;

            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews(){

        Animation top_down_device  = AnimationUtils.loadAnimation(this,R.anim.top_down_device);
        Animation top_down_login  = AnimationUtils.loadAnimation(this,R.anim.top_down_login);
        Animation top_down_add  = AnimationUtils.loadAnimation(this,R.anim.top_down_add);
        Animation top_down_question  = AnimationUtils.loadAnimation(this,R.anim.top_down_ask);



        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        txt_toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        img_toolbar_login =  toolbar.findViewById(R.id.img_login);
        img_toolbar_add_device = toolbar.findViewById(R.id.img_add_device);
        img_toolbar_ask =  toolbar.findViewById(R.id.imag_ask);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewpager = (ViewPager) findViewById(R.id.viewpager);

        txt_toolbar_title.startAnimation(top_down_device);
        img_toolbar_login.startAnimation(top_down_login);
        img_toolbar_add_device.startAnimation(top_down_add);
        img_toolbar_ask.startAnimation(top_down_question);

    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothGattService.ACTION_CONNECTED);
            filter.addAction(BluetoothGattService.ACTION_DISCONNECTED);
            filter.addAction(BluetoothGattService.ACTION_RECEIVING_DATA);
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);

            isReceiverRegistered = true;
        }
    }




    private void setFonts(){
        Typeface futura = Typeface.createFromAsset(getAssets(),
                "fonts/futura_light.ttf");
        Typeface corbel = Typeface.createFromAsset(getAssets(),
                "fonts/Corbel Bold.ttf");

        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        txt_toolbar_title.setTypeface(futura);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            new LeDevicesDialog(this);
        }
    }

    public void addDevice(View view) {

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) !=  PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }else{
            new LeDevicesDialog(this);
        }


    }

    private void autoConnectDevice(){
        SharedPreferences devicePreference = getSharedPreferences("SavedDevice", Context.MODE_PRIVATE);
        String MAC_adress = devicePreference.getString("MAC_adress", "");
        String device_name = devicePreference.getString("device_name", "");

        if(!MAC_adress.equals("") && Utils.isBluetoothEnabled()){
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(MAC_adress);
            startService(new Intent(this, BluetoothGattService.class).putExtra("device",device));
        }else{
            //No saved devices
        }


    }
}
