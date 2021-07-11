
package com.example.m1project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

public class SensorHistogram extends View {
    private final Paint barsPaint;
    private final Paint axesPaint;
    private Sensor mSensor;
    private float[] values;
    private String[] colNames;
    private String unit = "";
    private float maxRange;
    public SensorHistogram(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SensorHistogram,
                0, 0);
        barsPaint = new Paint();
        barsPaint.setColor(a.getColor(R.styleable.SensorHistogram_BarsColor, Color.BLUE));
        barsPaint.setTextSize(50);
        axesPaint = new Paint();
        axesPaint.setColor(a.getColor(R.styleable.SensorHistogram_AxesColor,Color.RED));
        axesPaint.setStrokeWidth(10);
        axesPaint.setTextSize(50);
    }

    public void setSensor(Sensor s){
        mSensor = s;
        maxRange = mSensor.getMaximumRange();
        invalidate();
        requestLayout();
    }

    public void setValues(float[] _values){
        values = _values;
        colNames = new String[values.length];
        switch (mSensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD:
                unit = "uT";
                break;
            case Sensor.TYPE_GYROSCOPE:
                unit = "rd/s";
                colNames[0] = "W around x";
                colNames[1] = "W around y";
                colNames[2] = "W around z";
                break;
            case Sensor.TYPE_LIGHT:
                unit = "lux";
                break;
            case Sensor.TYPE_PRESSURE:
                unit = "hPa";
                break;
            case Sensor.TYPE_PROXIMITY:
                unit = "cm";
                break;
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_LINEAR_ACCELERATION:
                unit = "m/s^2";

                break;
            case Sensor.TYPE_ROTATION_VECTOR://y points to north
            case Sensor.TYPE_GAME_ROTATION_VECTOR://same but different convention so y does not point to north
                colNames[0] = "x*sin(θ/2)";
                colNames[1] = "y*sin(θ/2)";
                colNames[2] = "z*sin(θ/2)";
                colNames[3] = "cos(θ/2)";
                try {
                    colNames[4] = "~Accuracy(in rd)";
                }catch (ArrayIndexOutOfBoundsException ignored){}//may be unavailable depends on sdk version
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                unit = "%";
                colNames[0] = "Relative ambient air humidity";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                unit = "°C";
                colNames[0] = "ambient (room) temperature";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                unit = "uT";
                colNames[0] = "x_uncalib";
                colNames[1] = "y_uncalib";
                colNames[2] = "z_uncalib";
                colNames[3] = "x_bias";
                colNames[4] = "y_bias";
                colNames[5] = "z_bias";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                unit = "rd/s";
                colNames[0] = "W/o around x";
                colNames[1] = "W/o around y";
                colNames[2] = "W/o around z";
                colNames[3] = "estimated drift around X";
                colNames[4] = "estimated drift around y";
                colNames[5] = "estimated drift around z";
                break;
            case Sensor.TYPE_POSE_6DOF:
                unit = "SI";
                colNames[0] =  "x*sin(θ/2)";
                colNames[1] = "y*sin(θ/2)";
                colNames[2] = "z*sin(θ/2)";
                colNames[3] = "cos(θ/2)";
                colNames[4] = "Translation along x axis from an arbitrary origin";
                colNames[5] = "Translation along y axis from an arbitrary origin";
                colNames[6] = "Translation along z axis from an arbitrary origin";
                colNames[7] = "Delta quaternion rotation x*sin(θ/2)";
                colNames[8] = "Delta quaternion rotation y*sin(θ/2)";
                colNames[9] = "Delta quaternion rotation z*sin(θ/2)";
                colNames[10] = "Delta quaternion rotation cos(θ/2)";
                colNames[11] = "Delta translation along x axis";
                colNames[12] = "Delta translation along y axis";
                colNames[13] = "Delta translation along z axis";
                colNames[14] = "Sequence number";
                break;
            case Sensor.TYPE_STATIONARY_DETECT:
            case Sensor.TYPE_MOTION_DETECT:
                values[0] = 1;
                break;
            case Sensor.TYPE_HEART_BEAT:
                colNames[0] = "confidence";
                break;
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                colNames[0] = "is on-body";
                break;
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                unit = "(m/s^2)";
                colNames[0] = "x_uncalib without bias compensation";
                colNames[1] = "y_uncalib without bias compensation";
                colNames[2] = "z_uncalib without bias compensation";
                colNames[3] = "estimated x_bias";
                colNames[4] = "estimated y_bias";
                colNames[5] = "estimated z_bias";
                break;
            case Sensor.TYPE_HINGE_ANGLE:
                unit = "degree";
                colNames[0] = "Measured hinge angle";
                break;
        }
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float axesMargin = 100;
        int height = getHeight();
        canvas.drawLine(axesMargin,0,axesMargin,height-axesMargin,axesPaint);//x axe
        canvas.drawLine(axesMargin,height-axesMargin,getWidth(),height-axesMargin,axesPaint);//y axe
        canvas.drawLine(axesMargin,0,axesMargin/2,axesMargin,axesPaint);//left of x arrow
        canvas.drawLine(axesMargin,0,3*axesMargin/2,axesMargin,axesPaint);//right of x arrow
        canvas.drawText("0",axesMargin+10,height-axesMargin-10,axesPaint);
        if(mSensor == null)
            return;
        float value = maxRange/5;
        float step = (height-axesMargin*4)/5;
        @SuppressLint("DrawAllocation") DecimalFormat decimalFormat = new DecimalFormat("#.00");
        for(int i = 1 ; i <= 5 ; i++){
            canvas.drawLine(3*axesMargin/4,height-axesMargin-i*step,5*axesMargin/4,height-axesMargin-i*step,axesPaint);
            canvas.drawText(""+decimalFormat.format(value),5*axesMargin/4,height-axesMargin-i*step+10,axesPaint);
            value+=maxRange/5;
        }
        if(values == null)
            return;
        float BarXStep = (getWidth()-axesMargin*5)/(float)values.length;
        float maxVal = maxRange;
        float maxHeight = height-axesMargin*4;

        for(int i = 0 ; i < values.length ; i++) {
            float val = values[i];
            float left = axesMargin + (height - axesMargin * 4) / 5 + BarXStep * i;
            float right = axesMargin + (height - axesMargin * 4) / 5 + BarXStep * (i + 1);
            float top = axesMargin * 3 + maxHeight - (((val > 0 ? val : 0) / maxVal) * maxHeight);
            canvas.drawRect(left, top, right, height - axesMargin, barsPaint);
            String text = decimalFormat.format(val);
            float textWidth = barsPaint.measureText(text, 0, text.length()-1);
            canvas.drawText(text,left+(right-left)/2-textWidth/2,top,axesPaint);
        }
        for(int i = 0; i < values.length; i++) {
            float startX = axesMargin + (height - axesMargin * 4) / 5 + BarXStep * (i + 1);
            canvas.drawLine(startX,axesMargin*3+maxHeight-(((values[i]>0?values[i]:0)/maxVal)*maxHeight), startX,height-axesMargin+50,axesPaint);
        }
        canvas.drawText(unit,getWidth()-unit.length()*10-axesMargin,height-axesMargin*2-20,axesPaint);
        if(colNames == null)
            return;
        BarXStep = (getWidth()-axesMargin*5)/(float)values.length;
        for(int i = 0 ; i < values.length; i++)
            if(colNames[i] != null)
                canvas.drawText(colNames[i],axesMargin+(height-axesMargin*4)/5 + BarXStep * i+5,height-axesMargin+50,barsPaint);
    }
}
