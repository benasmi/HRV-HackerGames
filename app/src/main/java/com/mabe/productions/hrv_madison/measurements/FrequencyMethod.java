package com.mabe.productions.hrv_madison.measurements;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

/**
 * Created by Benas on 2/23/2018.
 */

public class FrequencyMethod {

    public final static double HF_MIN = 0.15;
    public final static double HF_MAX = 0.4;

    public final static double LF_MIN = 0.04;
    public final static double LF_MAX = 0.15;

    public final static double VLF_MIN = 0.0033;
    public final static double VLF_MAX = 0.04;

    public final static double VHF_MIN = 0.4;
    public final static double VHF_MAX = 0.9;

    public final static int BEATS_PER_FFT = 16;

    private int times = 0;


    public ArrayList<Complex> hrv_data = new ArrayList<Complex>();

    double HF_count = 0;
    double LF_count = 0;
    double VLF_count = 0;
    double ULF_count = 0;
    double VHF_count = 0;
    double total = 0;

    double HF_value=0;
    double LF_value=0;
    double VLF_value=0;
    double VHF_value=0;


    public void calculate_frequencies(int beatsFFT){
        double []freqArray = new double[beatsFFT];
        for(int i = 0; i<beatsFFT; i++){
            freqArray[i] = (double)i/(double)beatsFFT;
            //Log.i("TEST", "Const: " + freqArray[i]);
        }

        Complex[] hrv_complexes = new Complex[hrv_data.size()];
        hrv_complexes = hrv_data.toArray(hrv_complexes);

        Complex[] fft_frequencies = FFT.fft(hrv_complexes);


        for (int i = 0; i < fft_frequencies.length; i++) {
            double rootNumber = Math.sqrt(Math.pow(fft_frequencies[i].re(), 2) + Math.pow(fft_frequencies[i].im(), 2));
            //Log.i("TEST","skaicius: " + rootNumber );
            //HF range
            double constArrayFreq = freqArray[i];

            if (constArrayFreq >= HF_MIN && constArrayFreq <= HF_MAX) {
                HF_count += rootNumber;

            }

            if (constArrayFreq > LF_MIN && constArrayFreq <= LF_MAX) {
                LF_count += rootNumber;

            }

            /*
            if (constArrayFreq > VLF_MIN && constArrayFreq <= VLF_MAX) {
                VLF_count += rootNumber;

            }
            if (constArrayFreq > VHF_MIN && constArrayFreq <= VHF_MAX) {
                VHF_count += rootNumber;
            }
            */

        }
        total = LF_count + HF_count;
        LF_value = Math.round((LF_count/total)*100.0);
        VLF_value = Math.round((VLF_count/total)*100.0);
        HF_value = 0;
        VHF_value = 0;


    }

    public double getHF_value() {
        return HF_value;
    }

    public double getLF_value() {
        return LF_value;
    }

    public double getVLF_value() {
        return VLF_value;
    }

    public double getVHF_value() {
        return VHF_value;
    }



    public void add_to_freq_array(int interval){
        double Hz = 0;

        if(interval!=0){
            Hz = 1/((float)interval/1000f);
            Log.i("DATA", "HZ: " + Hz);
        }
        hrv_data.add(new Complex(Hz, 0));

        //Checking if number of data points is power of two
        if(hrv_data.size() > 1 && ((hrv_data.size() & (hrv_data.size() - 1)) == 0)){
            calculate_frequencies(hrv_data.size());
            Crashlytics.log("Calculating frequencies.");
        }

        Crashlytics.setInt("number_of_frequency_intervals", hrv_data.size());


    }

    public void clearData(){
        hrv_data.clear();
    }
}