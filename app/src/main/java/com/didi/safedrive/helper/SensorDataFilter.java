package com.didi.safedrive.sensor;

import java.util.LinkedList;
import java.util.Queue;

/**float
 * Created by didi on 2018/4/11.
 */

public class SensorDataFilter {
    private static final int WINDOWS = 10;
    private int mWindowSize = WINDOWS;
    private Queue queue;
    private float sum;
    public SensorDataFilter() {
        this.queue = new LinkedList();
        this.sum = 0;
    }
    public SensorDataFilter(int size) {
        this.mWindowSize = size;
        this.queue = new LinkedList();
        this.sum = 0;
    }
    public float filte(float n) {
        if (queue.size() > mWindowSize - 1) {
            sum = sum - (float)queue.poll();
        }
        queue.offer(n);
        sum = sum + n;
        return (float) sum / queue.size();
    }
}
