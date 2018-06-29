package com.didi.safedrive.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.didi.safedrive.OkHttpHelper;
import com.didi.safedrive.R;
import com.didi.safedrive.bean.Weather;
import com.didi.safedrive.helper.WeakHandler;
import com.didi.safedrive.util.SettingSharedPrefHelper;
import com.didi.safedrive.utils.VUIUtils;
import com.didi.safedrive.view.CarDialog;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ManFragment extends LazyFragment {

    public static final String INTENT_INT_INDEX = "index";
    private static ManFragment sInstance;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mTVTime;
    private TextView mTVLastUpdate;
    private TextView mTVWeatherState;
    private TextView mTVTips;
    private TextView mTVTempatureBody;
    private TextView mTVHeartRate;
    private TextView mTVWeight;
    private TextView mTVPressure;
    private TextView mTVTempratureDay;
    private TextView mTVWeather;
    private TextView mTVPM2;
    private TextView mPM10;
    private TextView mTVFl;
    private TextView mTVFx;
    private TextView mTVNotice;
    private WeakHandler mHandler;
    private Weather mWeather;
    private boolean mShowCityDialog;

    public static ManFragment getInstance(int tabIndex, boolean isLazyLoad) {
        if (sInstance == null) {
            Bundle args = new Bundle();
            args.putInt(INTENT_INT_INDEX, tabIndex);
            args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
            sInstance = new ManFragment();
            sInstance.setArguments(args);
        }
        return sInstance;
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_man);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sr_refresh);
        mTVTime = (TextView) findViewById(R.id.tv_time);
        mTVLastUpdate = (TextView) findViewById(R.id.tv_lastupdate);
        mTVWeatherState = (TextView) findViewById(R.id.tv_weather_state);
        mTVTempatureBody = (TextView) findViewById(R.id.tv_tempature_body);
        mTVTips = (TextView) findViewById(R.id.tv_tips);
        mTVHeartRate = (TextView) findViewById(R.id.tv_heart_rate);
        mTVWeight = (TextView) findViewById(R.id.tv_weight);
        mTVPressure = (TextView) findViewById(R.id.tv_pressure);
        mTVTempratureDay = (TextView) findViewById(R.id.tv_temprature_day);
        mTVWeather = (TextView) findViewById(R.id.tv_weather);
        mTVPM2 = (TextView) findViewById(R.id.tv_pm2_5);
        mPM10 = (TextView) findViewById(R.id.tv_pm10);
        mTVFl = (TextView) findViewById(R.id.tv_fl);
        mTVFx = (TextView) findViewById(R.id.tv_fx);
        mTVNotice = (TextView) findViewById(R.id.tv_notice);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeather();
            }
        });
        mHandler = new WeakHandler(new WeakHandler.IHandler() {
            @Override
            public void handleMsg(Message msg) {
                switch (msg.what) {
                    case 0:
                        mWeather = (Weather) msg.obj;
                        mRefreshLayout.setRefreshing(false);
                        updateUI();
                        break;
                    case 1:
                        mRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        });
        refreshWeather();
    }

    void updateUI() {
        if (mWeather == null || mWeather.getStatus() != 200) {
            return;
        }
        if (mTVTime != null) {
            mTVTime.setText(mWeather.getDate());
        }
        if (mTVLastUpdate != null) {
            mTVLastUpdate.setText("最近更新:" + mWeather.getDate());
        }
        if (mTVWeatherState != null) {
            mTVWeatherState.setText(mWeather.getData().getQuality());
        }
        if (mTVTips != null) {
            mTVTips.setText(mWeather.getData().getGanmao());
        }
        if (mTVTempatureBody != null) {
            mTVTempatureBody.setText(String.format("%.2f", 35 + Math.random() * 4));
        }
        if (mTVHeartRate != null) {
            mTVHeartRate.setText(String.format("%.2f", 75 + Math.random() * 25));
        }
        if (mTVWeight != null) {
            mTVWeight.setText(String.format("%.2f", 65 + Math.random() * 5));
        }
        if (mTVPressure != null) {
            mTVPressure.setText(String.valueOf(70 + (int) (Math.random() * 10)) + "~" + String.valueOf(100 + (int) (Math.random() * 20)));
        }
        if (mTVTempratureDay != null) {
            mTVTempratureDay.setText(mWeather.getData().getWendu() + "°C");
        }
        if (mTVWeather != null) {
            mTVWeather.setText(mWeather.getData().getForecast().get(0).getType());
        }
        if (mTVPM2 != null) {
            mTVPM2.setText(String.valueOf(mWeather.getData().getPm25()));
        }
        if (mPM10 != null) {
            mPM10.setText(String.valueOf(mWeather.getData().getPm10()));
        }
        if (mTVFl != null) {
            mTVFl.setText(mWeather.getData().getForecast().get(0).getFl());
        }
        if (mTVFx != null) {
            mTVFx.setText(mWeather.getData().getForecast().get(0).getFx());
        }
        if (mTVNotice != null) {
            mTVNotice.setText(mWeather.getData().getForecast().get(0).getNotice());
        }
    }

    void refreshWeather() {
        SettingSharedPrefHelper helper = new SettingSharedPrefHelper();
        String city = helper.getString("city", null);
        String url = null;
        if (TextUtils.isEmpty(CarFragment.sCity)) {
            if (TextUtils.isEmpty(city)) {
                city = "徐州";
            }
            url = "https://www.sojson.com/open/api/weather/json.shtml?city=" + city;
            if (!mShowCityDialog) {
                new CarDialog(getContext()).title("提示").content("当前定位城市:" + city + ",建议先开始检测进行定位").tips("秒后自动消失").show(4);
                mShowCityDialog = true;
            }
        } else {
            url = "https://www.sojson.com/open/api/weather/json.shtml?city=" + CarFragment.sCity;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = OkHttpHelper.CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && mHandler != null) {
                    String res = response.body().string();
                    Gson gson = new Gson();
                    Weather weather = gson.fromJson(res, Weather.class);
                    Message msg = mHandler.obtainMessage(0);
                    msg.obj = weather;
                    mHandler.sendMessage(msg);
                } else {
                    mHandler.sendEmptyMessage(1);
                }
            }
        });

    }
}
