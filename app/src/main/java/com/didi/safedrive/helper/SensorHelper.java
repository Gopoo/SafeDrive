package com.didi.safedrive.helper;

import android.content.Context;

import com.baidu.location.LocationClient;
import com.didi.safedrive.sensor.SensorClient;

/**
 * Created by didi on 2018/4/10.
 */

public class SensorHelper {
    private static SensorClient SENSOR_CLIENT = null;
    public static final SensorClient getSensorInstance (Context context){
        if (SENSOR_CLIENT==null){
            synchronized (SensorHelper.class){
                if (SENSOR_CLIENT==null){
                    SENSOR_CLIENT = new SensorClient(context.getApplicationContext());
                }
            }
        }
        return SENSOR_CLIENT;
    }
}
