package com.didi.safedrive;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.didi.safedrive.helper.LocHelper;
import com.didi.safedrive.helper.SensorHelper;
import com.didi.safedrive.sensor.OnAccChangeListener;
import com.didi.safedrive.sensor.OnGyrChangeListener;
import com.didi.safedrive.sensor.SensorClient;
import com.didi.safedrive.util.ToastUtils;

public class MainActivity extends AppCompatActivity {
    private LocationClient locClient = null;
    private SensorClient senClient = null;
    private TextView textViewGyr = null;
    private TextView textViewAcc = null;
    private TextView textViewLoc = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkGPSEnable();
        checkPermission();
        textViewLoc = findViewById(R.id.tv_loc);
        textViewAcc = findViewById(R.id.tv_acc);
        textViewGyr = findViewById(R.id.tv_gyr);
        locClient = LocHelper.getLocInstance(this);
        senClient = SensorHelper.getSensorInstance(this)
                .addSensor(Sensor.TYPE_ACCELEROMETER)
                .addSensor(Sensor.TYPE_GYROSCOPE)
                .setOnAccChangeListener(accChangeListener)
                .setOnGyrChangeListener(gyrChangeListener);
        locClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                String text = bdLocation.getTime()+","+bdLocation.getLongitude()+","+
                bdLocation.getLatitude()+","+bdLocation.getRadius()+","+bdLocation.getCity()+bdLocation.getDistrict()+bdLocation.getStreet()+
                ","+bdLocation.getLocationDescribe()+bdLocation.getAddrStr()+","+bdLocation.getSpeed();
                textViewLoc.setText("driver:"+text);
            }
        });
        locClient.start();
        senClient.start();
    }
    private final void checkGPSEnable(){
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            ToastUtils.show(this,"未打开位置开关");
        }
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE
                }, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==0){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locClient!=null)
            locClient.stop();
        if (senClient!=null)
            senClient.stop();
    }
    private OnAccChangeListener accChangeListener = new OnAccChangeListener() {
        @Override
        public void onChange(float x, float y, float z) {
            textViewAcc.setText(String.format("acc,%f,%f,%f,%f,%f",x,y,z,Math.sqrt(x*x+y*y),Math.sqrt(x*x+y*y+z*z)));
        }
    };
    private OnGyrChangeListener gyrChangeListener = new OnGyrChangeListener() {
        @Override
        public void onChange(float x, float y, float z) {
            textViewGyr.setText(String.format("gyr,%f,%f,%f",x,y,z));
        }
    };
}
