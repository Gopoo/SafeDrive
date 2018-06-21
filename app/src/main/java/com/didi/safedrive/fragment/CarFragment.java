package com.didi.safedrive.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.didi.safedrive.MainActivity;
import com.didi.safedrive.R;
import com.didi.safedrive.dialog.CarDialog;
import com.didi.safedrive.helper.IDialogListener;
import com.didi.safedrive.helper.OnLocationChangeListener;
import com.didi.safedrive.helper.SensorDataProcesser;
import com.didi.safedrive.sensor.OnAccChangeListener;
import com.didi.safedrive.sensor.SensorClient;
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

public class CarFragment extends Fragment implements OnAccChangeListener, OnLocationChangeListener {
    private LocationClient locClient = null;
    private SensorClient senClient = null;
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

    public static CarFragment getInstance() {
        if (sInstance == null) {
            sInstance = new CarFragment();
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
            if (i > 200) {
                set.removeFirst();
                data.addEntry(new Entry(set.getEntryCount() + (float) (i - 200), y), 0);
            } else {
                data.addEntry(new Entry(set.getEntryCount(), y), 0);
            }
            i++;
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(200);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_car, container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTVAddress = mRootView.findViewById(R.id.tv_address);
        mTVSpeed = mRootView.findViewById(R.id.tv_speed);
        mTVTime = mRootView.findViewById(R.id.tv_time);
        mTVLonLat = mRootView.findViewById(R.id.tv_lonlat);
        mTVRoad = mRootView.findViewById(R.id.tv_road);
        mTVContent = mRootView.findViewById(R.id.tv_content);
        initChart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locClient != null)
            locClient.stop();
        if (senClient != null)
            senClient.stop();
    }

    private SensorDataFilter mSpeedFilter = new SensorDataFilter();

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        mTVAddress.setText(bdLocation.getAddrStr());
        mTVSpeed.setText(String.valueOf(mSpeedFilter.filte(bdLocation.getSpeed())) + "km/h");
        mTVTime.setText(bdLocation.getTime());
        mTVLonLat.setText(bdLocation.getLatitude() + "," + bdLocation.getLongitude());
        mTVContent.setText(bdLocation.getLocationDescribe());
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
        if (mTipDialog != null && mWarnDialog != null && (mTipDialog.isShowing() || mWarnDialog.isShowing())) {
            return;
        }
        switch (level) {
            case 2:
                if (mTipDialog == null) {
                    mTipDialog = new CarDialog(getContext());
                    mTipDialog.listener(new IDialogListener() {
                        @Override
                        public void onShow() {
                        }

                        @Override
                        public void onDismiss() {
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
                        public void onShow() {
                        }

                        @Override
                        public void onDismiss() {
                        }
                    });
                    mWarnDialog.title("警告").content("当前可能出现事故").tips("后自动发送求救短信");
                }
                mWarnDialog.show(20);
                break;
        }
    }

    private CarDialog mTipDialog;
    private CarDialog mWarnDialog;
    private SensorDataProcesser mProcesser = new SensorDataProcesser();

    private long mLastChangeTextTimestamp = -1;
}
