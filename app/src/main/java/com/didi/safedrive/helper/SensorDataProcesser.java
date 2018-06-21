package com.didi.safedrive.helper;

import java.util.Iterator;
import java.util.LinkedList;

public class SensorDataProcesser {

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
                if (num > 12f) {
                    danger++;
                } else if (num > 5f) {
                    normal++;
                } else {
                    easy++;
                }
            }
            mDataQueue.poll();
            if (sum >= 100f || danger > normal || danger > easy || danger >= 2) {
                result = 3;  //突然出现碰撞
            } else if (sum > 50f || normal > easy || normal > danger) {
                result = 2;  //加速度过大
            } else if (easy >= 6) {
                result = 1;   //平稳
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
