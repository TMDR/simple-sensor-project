package com.example.m1project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class SensorAdapter extends ArrayAdapter {

    private final List<Sensor> sensors;
    private final ArrayList<Boolean> checked = new ArrayList<>();
    private final boolean multipleChoice;

    public SensorAdapter(@NonNull Context context,List<Sensor> _sensors,boolean multipleChoice) {
        super(context,0);
        sensors = _sensors;
        for (int i = 0; i < sensors.size(); i++) {
            checked.add(false);
        }
        this.multipleChoice = multipleChoice;
    }


    public ArrayList<String> getCheckedSensors(){
        ArrayList<String> al = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++)
            if(checked.get(i))
                al.add(sensors.get(i).getName());
        return al;
    }

    public Sensor get(int position){
        return sensors.get(position);  
    }

    public void toggleAll(boolean state){
        for (int i = 0; i < checked.size(); i++) {
            checked.set(i,state);
        }
        notifyDataSetChanged();
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent/*ListView*/) {
        if(multipleChoice)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sensor_check_view, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_sensor_info, parent, false);
        Sensor s = sensors.get(position);
        if(multipleChoice)
            ((TextView)convertView.findViewById(R.id.Name)).setText(s.getName());
        else
            ((TextView)convertView.findViewById(R.id.NameNonMultiChoice)).setText(s.getName());
        ((TextView)convertView.findViewById(R.id.MaxRange)).setText(s.getMaximumRange()+"");
        ((TextView)convertView.findViewById(R.id.Power)).setText(s.getPower()+" mA");
        ((TextView)convertView.findViewById(R.id.Resolution)).setText(s.getResolution()+"");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            ((TextView)convertView.findViewById(R.id.Type)).setText(s.getStringType());
        }else{
            ((TextView)convertView.findViewById(R.id.Type)).setText(s.getType());
        }
        ((TextView)convertView.findViewById(R.id.Vendor)).setText(s.getVendor());
        ((TextView)convertView.findViewById(R.id.Version_Mine)).setText("v"+s.getVersion());
        ((TextView)convertView.findViewById(R.id.MinDelay_Mine)).setText(s.getMinDelay()+" ms MINIMUM");
        if(multipleChoice) {
            CheckBox cb = convertView.findViewById(R.id.checked);
            cb.setChecked(checked.get(position));
            cb.setOnClickListener(v -> {
                checked.set(position, !checked.get(position));
                boolean allTrue = true;
                for (Boolean b : checked)
                    if (!b) {
                        allTrue = false;
                        break;
                    }
                ((CheckBox) ((ViewGroup) parent.getParent()).findViewById(R.id.toggler)).setChecked(allTrue);
            });
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return sensors.size();
    }
}
