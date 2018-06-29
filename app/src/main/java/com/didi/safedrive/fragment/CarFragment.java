package com.didi.safedrive.fragment;

import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.didi.safedrive.R;
import com.didi.safedrive.util.SettingSharedPrefHelper;
import com.didi.safedrive.util.SmsUtils;
import com.didi.safedrive.view.CarDialog;
import com.didi.safedrive.helper.IDialogListener;
import com.didi.safedrive.helper.OnLocationChangeListener;
import com.didi.safedrive.helper.SensorDataProcesser;
import com.didi.safedrive.sensor.OnAccChangeListener;
import com.didi.safedrive.sensor.SensorDataFilter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class CarFragment extends LazyFragment implements OnAccChangeListener, OnLocationChangeListener {
    public static final String INTENT_INT_INDEX = "index";
    public static String sCity = null;
    private static CarFragment sInstance;
    private LineChart mChart;
    private View mRootView;
    private int i = 0;
    private TextView mTVAddress;
    private TextView mTVSpeed;
    private TextView mTVTime;
    private TextView mTVRoad;
    private TextView mTVLonLat;
    private TextView mTVContent;
    private BDLocation mLocation;

    public static CarFragment getInstance() {
        return sInstance;
    }

    public static CarFragment getInstance(int tabIndex, boolean isLazyLoad) {
        if (sInstance == null) {
            Bundle args = new Bundle();
            args.putInt(INTENT_INT_INDEX, tabIndex);
            args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
            sInstance = new CarFragment();
            sInstance.setArguments(args);
        }
        return sInstance;
    }

    private void initChart() {
        mChart = mRootView.findViewById(R.id.lineChart);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(getResources().getColor(R.color.colorGray));

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(getResources().getColor(R.color.colorGray));
        l.setEnabled(false);

        XAxis xl = mChart.getXAxis();
        xl.setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

    }


    private void addEntry(float y) {
        LineData data = mChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            if (i > 100) {
                set.removeFirst();
                data.addEntry(new Entry(set.getEntryCount() + (float) (i - 100), y), 0);
            } else {
                if (i < 50) {
                    data.addEntry(new Entry(set.getEntryCount(), 0), 0);
                } else {
                    data.addEntry(new Entry(set.getEntryCount(), y), 0);
                }
            }
            i++;
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(100);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.WHITE);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(3f);
        set.setDrawCircles(false);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_car);
        mRootView = findViewById(R.id.root);
        mTVAddress = mRootView.findViewById(R.id.tv_address);
        mTVSpeed = mRootView.findViewById(R.id.tv_speed);
        mTVTime = mRootView.findViewById(R.id.tv_time);
        mTVLonLat = mRootView.findViewById(R.id.tv_lonlat);
        mTVRoad = mRootView.findViewById(R.id.tv_road);
        mTVContent = mRootView.findViewById(R.id.tv_content);
        initChart();
    }

    private SensorDataFilter mSpeedFilter = new SensorDataFilter();

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (currentSecond == 0) {
            mhandle.post(timeRunable);
        }
        mLocation = bdLocation;
        sCity = bdLocation.getCity();
        mTVAddress.setText(bdLocation.getAddrStr());
        mTVSpeed.setText(String.valueOf(mSpeedFilter.filte(bdLocation.getSpeed())) + "km/h");
        mTVLonLat.setText(bdLocation.getLatitude() + "," + bdLocation.getLongitude());
        if (TextUtils.isEmpty(bdLocation.getLocationDescribe())) {
            mTVContent.setText("暂无");
        } else {
            mTVContent.setText(bdLocation.getLocationDescribe());
        }
    }

    @Override
    public void onChange(float x, float y, float z) {
        float car_yz = y * y + z * z;
        float car_xyz = x * x + y * y + z * z;
        addEntry((float) Math.sqrt(car_xyz));
        long nowTimeStamp = System.currentTimeMillis();
        //500ms更新一次UI
        if (nowTimeStamp - mLastChangeTextTimestamp > 500) {
            mTVRoad.setText(SensorDataProcesser.getCarLevel(x, y, z));
            mLastChangeTextTimestamp = nowTimeStamp;
        }
        int level = mProcesser.checkLevel((float) Math.sqrt(car_yz));
        if ((mTipDialog != null && mTipDialog.isShowing()) || (mWarnDialog != null && mWarnDialog.isShowing())) {
            return;
        }
        switch (level) {
            case 2:
                if (mTipDialog == null) {
                    mTipDialog = new CarDialog(getContext());
                    mTipDialog.listener(new IDialogListener() {
                        @Override
                        public void onAction() {

                        }
                    });
                    mTipDialog.title("提示").content("当前汽车加速度过大").tips("秒后自动消失");
                }
                mTipDialog.show(5);
                break;
            case 3:
                if (mWarnDialog == null) {
                    mWarnDialog = new CarDialog(getContext());
                    mWarnDialog.listener(new IDialogListener() {
                        @Override
                        public void onAction() {
                            if (mLocation != null) {
                                SettingSharedPrefHelper helper = new SettingSharedPrefHelper();
                                String phone = helper.getString("phone", "10086");
                                String name = helper.getString("name", "亲爱的");
                                SmsUtils.SendMsg("[CarCar]" + name + ",我当前可能出现交通事故,位置在：" + mLocation.getAddrStr()
                                        + "(百度地图:" + mLocation.getLongitude() + "," + mLocation.getLatitude() + ")"
                                        + ",请给我打电话进行确认。", phone);
                            }
                        }
                    });
                    mWarnDialog.title("警告").content("当前可能出现事故").tips("秒后自动发送求救短信");
                }
                mWarnDialog.show(10);
                break;
        }
    }

    private CarDialog mTipDialog;
    private CarDialog mWarnDialog;
    private SensorDataProcesser mProcesser = new SensorDataProcesser();

    private long mLastChangeTextTimestamp = -1;

    /*****************计时器*******************/
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            if (!isPause) {
                currentSecond = currentSecond + 1000;
                mTVTime.setText(getFormatHMS(currentSecond));
                //递归调用本runable对象，实现每隔一秒一次执行任务
                mhandle.postDelayed(this, 1000);
            }else {
                mhandle.postDelayed(this, 1000);
            }
        }
    };
    //计时器
    private Handler mhandle = new Handler();
    public volatile boolean isPause = false;//是否暂停
    private long currentSecond = 0;//当前毫秒数

    public static String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        int h = (int) (time / 3600);//秒
        return "" + String.format("%02d:%02d:%02d", h, m, s);
    }
/*****************计时器*******************/
}
