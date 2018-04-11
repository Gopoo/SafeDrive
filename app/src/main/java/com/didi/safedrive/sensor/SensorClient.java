package com.didi.safedrive.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by didi on 2018/4/10.
 */

public class SensorClient {
    private OnAccChangeListener mAccChangeListener = null;
    private OnGyrChangeListener mGyrChangeListener = null;
    private SensorManager mSensorManager = null;
    private List<Sensor> sensorList = null;
    public SensorClient(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorList = new ArrayList<>();
    }
    public SensorClient addSensor(int type){
        try{
            sensorList.add(mSensorManager.getDefaultSensor(type));
        }catch (Exception e){

        }
        return this;
    }
    public SensorClient setOnAccChangeListener(OnAccChangeListener listener){
        mAccChangeListener = listener;
        return this;
    }
    public SensorClient setOnGyrChangeListener(OnGyrChangeListener listener){
        mGyrChangeListener = listener;
        return this;
    }
    /*
     *注册所有传感器
     */
    public void start(){
        for (Sensor sensor:sensorList){
            mSensorManager.registerListener(sensorListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    /*
     *释放所有传感器
     */
    public void stop(){
        for (Sensor sensor:sensorList){
            mSensorManager.unregisterListener(sensorListener,sensor);
        }
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float xyz[] = event.values;
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    mAccChangeListener.onChange(xyz[0],xyz[1],xyz[2]);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    mGyrChangeListener.onChange(xyz[0],xyz[1],xyz[2]);
                    break;

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
