package com.example.m1project;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "SensorIndex";
    private SensorHistogram sh;
    private HandlerThread mSensorThread;
    private Handler mSensorHandler;

    // TODO: Rename and change types of parameters
    private Sensor mSensor;

    private final SensorEventListener mLightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("Thread_debugging","sensor listening on : "+Thread.currentThread().getName());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sh.setValues(event.values);
                    Log.d("Thread_debugging","ui update : "+Thread.currentThread().getName());
                }
            });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            Log.d("MY_APP", sensor.toString() + " - " + accuracy);
        }
    };

    public SensorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sIndex Parameter 1.
     * @return A new instance of fragment SensorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorFragment newInstance(int sIndex) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1,sIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSensor = SensorPreviewActivity.sensors.get(getArguments().getInt(ARG_PARAM1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_sensor, container, false);
        sh = v.findViewById(R.id.sensorHistogram);
        sh.setSensor(mSensor);
        ((TextView)v.findViewById(R.id.SensorTitle)).setText(mSensor.getName());
        return v;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensor != null) {
            mSensorThread = new HandlerThread("Sensor thread "+mSensor.getName(), Thread.MAX_PRIORITY);
            mSensorThread.start();
            mSensorHandler = new Handler(mSensorThread.getLooper()); //Blocks until looper is prepared, which is fairly quick
            SensorPreviewActivity.mSensorManager.registerListener(mLightSensorListener, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL,mSensorHandler);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSensor != null) {
            SensorPreviewActivity.mSensorManager.unregisterListener(mLightSensorListener);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mSensorThread.quitSafely();
        }
    }
}