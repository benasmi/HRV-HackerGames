package com.mabe.productions.pr_ipulsus_running.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;

public class ViewPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    public MeasurementFragment measurementFragment;
    public DataTodayFragment dataTodayFragment;
    public WorkoutFragment workoutFragment;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        measurementFragment = new MeasurementFragment();
        dataTodayFragment = new DataTodayFragment();
        workoutFragment = new WorkoutFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return measurementFragment;

            case 1:
                return dataTodayFragment;

            case 2:
                return workoutFragment;

        }

        return null;
    }


    @Override
    public int getCount() {

        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}