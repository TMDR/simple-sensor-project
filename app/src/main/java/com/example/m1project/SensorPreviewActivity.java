package com.example.m1project;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Set;

public class SensorPreviewActivity extends AppCompatActivity {
    public static ArrayList<Sensor> sensors;
    public static SensorManager mSensorManager;
    private ViewPager2 sensorPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_preview);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ArrayList<Sensor> allSensors = new ArrayList<>(mSensorManager.getSensorList(Sensor.TYPE_ALL));
        SharedPreferences sharedPref = this.getSharedPreferences("sensorsSP",Context.MODE_PRIVATE);
        Set<String> SensorNames = sharedPref.getStringSet("sensors",null);
        sensors = new ArrayList<>();
        for( String sName : SensorNames )
            for(Sensor s : allSensors)
                if(s.getName().equals(sName))
                    sensors.add(s);
        sensorPager = findViewById(R.id.SensorPager);
        SensorFragmentStateAdapter pagerAdapter = new SensorFragmentStateAdapter(this,sensors);
        sensorPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (sensorPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            sensorPager.setCurrentItem(sensorPager.getCurrentItem() - 1);
        }
    }
}