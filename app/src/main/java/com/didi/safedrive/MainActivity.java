package com.didi.safedrive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.didi.safedrive.fragment.CarFragment;
import com.didi.safedrive.fragment.ManFragment;
import com.didi.safedrive.helper.LocHelper;
import com.didi.safedrive.helper.SensorHelper;
import com.didi.safedrive.sensor.SensorClient;
import com.didi.safedrive.util.SettingSharedPrefHelper;
import com.didi.safedrive.util.SmsUtils;
import com.didi.safedrive.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String AppId = "11425732";
    private static final String AppKey = "X2OlxhZIrBST6Xrfk89exbc6";
    private static final String AppSecret = "P4G3EHKItlGLBek4Yf4UrHmPei2zdRkk";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mSettings;
    private ImageView mCallPhone;
    private Button buttonStart = null;
    private boolean isChecked = false;
    private LocationClient locClient = null;
    private SensorClient senClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkGPSEnable();
        checkPermission();
        mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.vp_holder);
        buttonStart = findViewById(R.id.btn_start);
        mSettings = findViewById(R.id.iv_settings);
        mCallPhone = findViewById(R.id.iv_callphone);
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
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
            }
        });
        mCallPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingSharedPrefHelper helper = new SettingSharedPrefHelper();
                String phone = helper.getString("phone", "10086");
                SmsUtils.callPhone(phone,MainActivity.this);
            }
        });
        initViewPager();
        initSensor();
        initTTSSDK();
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

    private void initTTSSDK(){
        SpeechSynthesizer mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this); // this 是Context的之类，如Activity
        mSpeechSynthesizer.setAppId(AppId);
        mSpeechSynthesizer.setApiKey(AppKey,AppSecret);
        mSpeechSynthesizer.auth(TtsMode.MIX);
        mSpeechSynthesizer.initTts(TtsMode.MIX);
    }

    private void initViewPager() {
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(CarFragment.getInstance(1, false));
        fragments.add(ManFragment.getInstance(2, true));
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
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
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
        CarFragment.getInstance().isPause = false;
    }

    public void stop() {
        if (locClient != null) {
            locClient.stop();
        }
        if (senClient != null) {
            senClient.stop();
        }
        CarFragment.getInstance().isPause = true;
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
