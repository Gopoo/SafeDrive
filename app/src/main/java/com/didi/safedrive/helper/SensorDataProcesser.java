package com.didi.safedrive.helper;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;

public class SensorDataProcesser {
    private static boolean sDebug = true;
    private static final int WINDOWS = 10;
    private int mWindowSize = WINDOWS;
    private LinkedList mDataQueue;

    public SensorDataProcesser() {
        this.mDataQueue = new LinkedList();
    }

    public int checkLevel(float n) {
        int result = 0;
        if (mDataQueue.size() > mWindowSize - 1) {
            Iterator i = mDataQueue.descendingIterator();
            int easy = 0;
            int normal = 0;
            int danger = 0;
            float sum = 0f;
            while (i.hasNext()) {
                float num = (float) i.next();
                sum += num;
                if (num > 8f) {
                    danger++;
                } else if (num > 5f) {
                    normal++;
                } else {
                    easy++;
                }
            }
            mDataQueue.poll();
            if (sum >= 70f || danger > 1) {
                if (sDebug) {
                    Log.w("hgp", "3," + sum + "," + easy + "," + normal + "," + danger);
                }
                result = 3;  //突然出现碰撞
            } else if (normal > mWindowSize - 2 && danger == 0 && sum > 35) {
                if (sDebug) {
                    Log.w("hgp", "2," + sum + "," + easy + "," + normal + "," + danger);
                }
                result = 2;  //加速度过大
            }

        }
        mDataQueue.offer(n);
        return result;
    }

    public static String getCarLevel(float x, float y, float z) {
        float car_x = x * x;
        float car_y = y * y;
        float car_z = z * z;
        float car_xyz = car_x + car_y + car_z;
        StringBuilder sb = new StringBuilder();
        if (car_xyz < 0.5f) {
            sb.append(" 平稳 ");
        } else {
            sb.append(" 车辆不稳定 ");
            if (car_x > 1.5f) {
                sb.append(" 转弯 ");
            }
            if (car_y > 1.5f) {
                sb.append(" 加速 ");
            }
            if (car_z > 1.5f) {
                sb.append(" 升降 ");
            }
        }
        return sb.toString();
    }
}
