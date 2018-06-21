package com.didi.safedrive;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.didi.safedrive.fragment.CarFragment;
import com.didi.safedrive.fragment.ManFragment;
import com.didi.safedrive.helper.LocHelper;
import com.didi.safedrive.helper.SensorHelper;
import com.didi.safedrive.sensor.SensorClient;
import com.didi.safedrive.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Button buttonStart = null;
    private boolean isChecked = false;
    private LocationClient locClient = null;
    private SensorClient senClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkGPSEnable();
        checkPermission();
        mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.vp_holder);
        buttonStart = findViewById(R.id.btn_start);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked) {
                    buttonStart.setText("结束检测");
                    start();
                    isChecked = true;
                } else {
                    buttonStart.setText("开始检测");
                    stop();
                    isChecked = false;
                }
            }
        });
        initViewPager();
        initSensor();
    }

    private void initSensor() {
        locClient = LocHelper.getLocInstance(this);
        senClient = SensorHelper.getSensorInstance(this)
                .addSensor(Sensor.TYPE_LINEAR_ACCELERATION)
                .setOnAccChangeListener(CarFragment.getInstance());
        locClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                CarFragment.getInstance().onReceiveLocation(bdLocation);
            }
        });
    }

    ;

    private void initViewPager() {
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(CarFragment.getInstance());
        fragments.add(ManFragment.getInstance());
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("车");
        mTabLayout.getTabAt(1).setText("人");
    }

    private final void checkGPSEnable() {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ToastUtils.show(this, "未打开位置开关");
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
        if (requestCode == 0) {
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
                    finish();
                }
        }
    }

    public void start() {
        if (locClient != null) {
            locClient.start();
        }
        if (senClient != null) {
            senClient.start();
        }
    }

    public void stop() {
        if (locClient != null) {
            locClient.stop();
        }
        if (senClient != null) {
            senClient.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position >= 0 && position < mFragmentList.size()) {
                fragment = mFragmentList.get(position);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
