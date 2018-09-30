package com.mabe.productions.hrv_madison.FingerHRV;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.mabe.productions.hrv_madison.R;
import com.mabe.productions.hrv_madison.measurements.RMSSD;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;


/**
 * This class extends Activity to handle a picture preview, process the preview
 * for a red values and determine a heart beat.
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class HeartRateMonitor extends Activity {


    private ImageView img_back_arrow;

    private static final int CAMERA_PERMISSION_REQUEST = 2;
    private TextView hrv_txt;
    private TextView txt_info;
    private static final String TAG = "HeartRateMonitor";

    private static SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView text = null;
    private static LineChart chart_hr;
    private static long startTime = 0;

    private static WakeLock wakeLock = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static RMSSD rmssd = new RMSSD();
    private static int[] intervals=new int[1];

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;

    /**
     * {@inheritDoc}
     */
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_monitor);

        img_back_arrow = findViewById(R.id.img_back_arrow);
        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HeartRateMonitor.this.finish();
            }
        });

        changeNotifBarColor(Color.parseColor("#2c3e50"), getWindow());
        startTime = System.currentTimeMillis();
        chart_hr = findViewById(R.id.chart_hr);
        txt_info = findViewById(R.id.info_txt);
        //Customizing HR chart
        chart_hr.setData(new LineData());
        chart_hr.getLineData().setDrawValues(false);
        chart_hr.getLegend().setEnabled(false);
        chart_hr.getXAxis().setDrawAxisLine(false);
        chart_hr.getAxisRight().setDrawAxisLine(false);
        chart_hr.getAxisLeft().setDrawAxisLine(false);
        chart_hr.getAxisLeft().setDrawGridLines(false);
        chart_hr.getXAxis().setDrawGridLines(false);
        chart_hr.getAxisRight().setDrawGridLines(false);
        chart_hr.setDescription(null);
        chart_hr.getAxisLeft().setDrawLabels(false);
        chart_hr.getAxisRight().setDrawLabels(false);
        chart_hr.getXAxis().setDrawLabels(false);
        chart_hr.setTouchEnabled(false);
        chart_hr.setViewPortOffsets(0f, 0f, 0f, 0f);
        //chart_hr.setAutoScaleMinMaxEnabled(true);
        preview = (SurfaceView) findViewById(R.id.preview);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            setUpSurfaceHolder();
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        }

        image = findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        hrv_txt = (TextView) findViewById(R.id.hrv_txt);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

    }

    private void setUpSurfaceHolder(){
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }

    public static void changeNotifBarColor(int color, Window window){

        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            wakeLock.acquire();

            camera = Camera.open();

            startTime = System.currentTimeMillis();
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            wakeLock.release();

            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(HeartRateMonitor.this, HeartRateMonitor.class));

            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);


            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private PreviewCallback previewCallback = new PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();




            //if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);


            Log.i("values", imgAvg + " " + Long.valueOf(System.currentTimeMillis()-startTime));

            if(imgAvg < 200){
                txt_info.setText("No finger detected!");
                chart_hr.getLineData().clearValues();
                rmssd.clear();
                samples.clear();
                return;
            }else{
                txt_info.setText("Measuring...\nTry to stay still and quiet!");
            }


            addEntry(imgAvg,getApplicationContext());

            // Log.i(TAG, "imgAvg="+imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    beat();
                    // Log.d(TAG, "BEAT!! beats="+beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                image.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
//
//            double totalTimeInSecs = (endTime - startTime) / 1000d;
//            if (totalTimeInSecs >= 1) {
//
//                double bps = (beats / totalTimeInSecs);
//                int dpm = (int) (bps * 60d);
//                if (dpm < 30 || dpm > 180) {
//                    startTime = System.currentTimeMillis();
//                    beats = 0;
//                    processing.set(false);
//                    return;
//                }
//
//
//                // Log.d(TAG,
//                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);
//
//                if (beatsIndex == beatsArraySize) beatsIndex = 0;
//                beatsArray[beatsIndex] = dpm;
//                beatsIndex++;
//
//                int beatsArrayAvg = 0;
//                int beatsArrayCnt = 0;
//                for (int i = 0; i < beatsArray.length; i++) {
//                    if (beatsArray[i] > 0) {
//                        beatsArrayAvg += beatsArray[i];
//                        beatsArrayCnt++;
//                    }
//                }
//                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
//                text.setText(String.valueOf(beatsAvg));
//                startTime = System.currentTimeMillis();
//                beats = 0;
//            }
            //  processing.set(false);
        }
    };

    private static void addEntry(int hr,Context context) {
        LineData data = chart_hr.getData();
        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            //Creating a line with single hr value
            ArrayList<Entry> singleValueList = new ArrayList<>();
            singleValueList.add(new Entry(0, hr));
            set = new LineDataSet(singleValueList, "HR");
            set.setLineWidth(1);
            set.setDrawValues(false);
            set.setDrawCircleHole(false);
            set.setDrawCircles(false);
            set.setColor(Color.parseColor("#F62459"));
            set.setDrawFilled(false);

            data.addDataSet(set);
        } else {
            set.addEntry(new Entry(set.getEntryCount(), hr));
        }

        data.notifyDataChanged();
        chart_hr.notifyDataSetChanged();


        chart_hr.setVisibleXRangeMaximum(150);
        chart_hr.setVisibleXRangeMinimum(150);
        chart_hr.setVisibleYRangeMinimum(25, YAxis.AxisDependency.RIGHT);
        chart_hr.setVisibleYRangeMaximum(25, YAxis.AxisDependency.RIGHT);

        chart_hr.moveViewTo(set.getEntryCount(), chart_hr.getY(), YAxis.AxisDependency.RIGHT);

    }


    private static ArrayList<Long> samples = new ArrayList();
    private static final int SAMPLE_SIZE = 20;

    private void beat(){
        if(samples.size() < SAMPLE_SIZE){
            samples.add(System.currentTimeMillis()-startTime);
            return;
        }else if(samples.size() == SAMPLE_SIZE){
            samples.add(System.currentTimeMillis()-startTime);
            samples.remove(0);
        }

        String txt = "=================";
        for(int i = 0; i < samples.size(); i++){
            txt += ("\n" + samples.get(i));
        }

        Log.i("TESTAS", txt);



        //Log.i("TEST", "\n" + txt);

        long firstSampleTime = samples.get(0);
        long lastSampleTime = samples.get(samples.size()-1);
        long timePassed = lastSampleTime - firstSampleTime;

        double bpm = (double) samples.size() / ((double) timePassed/60000d );
        int period = (int) (samples.get(samples.size()-1)-samples.get(samples.size()-2));
        intervals[0] = (int) (60000d/(double)bpm);

        if(period < 300){ // 50-220
            return;
        }

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(5, 1));
        }else{
            //deprecated in API 26
            v.vibrate(50);
        }

        rmssd.addIntervals(intervals);
        text.setText(String.valueOf((int) bpm));
        rmssd.calculateRMSSD();
        hrv_txt.setText("" + rmssd.getPURE_HRV());

    }

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @SuppressLint("LongLogTag")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {


                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }
}
