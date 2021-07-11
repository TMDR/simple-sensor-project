package com.example.m1project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;

public class ChooseSensorsActivity extends AppCompatActivity {

    public static SensorAdapter sensorAdapter;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref =
                this.getSharedPreferences("sensorsSP",Context.MODE_PRIVATE);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ArrayList<Sensor> sensors = new ArrayList<>(sensorManager.getSensorList(Sensor.TYPE_ALL));
        sensorAdapter = new SensorAdapter(this,sensors,true);
        ListView lv = findViewById(R.id.SensorsList);
        lv.setAdapter(sensorAdapter);
    }

    public void clicked(View v){
        SharedPreferences.Editor e = sharedPref.edit();
        e.putBoolean("sensors set",true);
        e.putStringSet("sensors",new HashSet<>(sensorAdapter.getCheckedSensors()));
        e.apply();
        Intent intent = new Intent(ChooseSensorsActivity.this, SensorPreviewActivity.class);
        startActivity(intent);
    }

    public void checkAll(View v){
        sensorAdapter.toggleAll(((CheckBox)v).isChecked());
    }

}