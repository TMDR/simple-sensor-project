package com.example.m1project;

import android.hardware.Sensor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class SensorFragmentStateAdapter extends FragmentStateAdapter {

    ArrayList<Sensor> sensors;

    public SensorFragmentStateAdapter(FragmentActivity fa,ArrayList<Sensor> _sensors) {
        super(fa);
        sensors = _sensors;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //        fragments.add(sf);
        return SensorFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }
}
