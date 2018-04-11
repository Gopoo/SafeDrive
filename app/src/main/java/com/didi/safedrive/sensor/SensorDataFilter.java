package com.didi.safedrive.sensor;

/**float
 * Created by didi on 2018/4/11.
 */

public class SensorDataFilter {
    private static final int WINDOWS = 10;
    private float[] mTemp = null; // 只声明暂时不初始化,用来记录最后得不到均值处理的点
    private float[] mBufout = null;
    private int mWindowSize = WINDOWS;
    public SensorDataFilter() {
    }
    public SensorDataFilter(int size) {
        mWindowSize = size;
    }

    // 均值滤波方法，输入一个buf数组，返回一个buf1数组，两者下表不一样，所以定义不同的下表，buf的下表为i，buf1的下表为buf1Sub.
    // 同理，临时的winArray数组下表为winArraySub
    public float[] movingAverageFilter(float[] buf) {
        int bufoutSub = 0;
        int winArraySub = 0;
        float[] winArray = new float[mWindowSize];

        if (mTemp == null) {
            mBufout = new float[buf.length - mWindowSize + 1];
            for (int i = 0; i < buf.length; i++) {
                if ((i + mWindowSize) > buf.length) {
                    break;
                } else {
                    for (int j = i; j < (mWindowSize + i); j++) {
                        winArray[winArraySub] = buf[j];
                        winArraySub = winArraySub + 1;
                    }

                    mBufout[bufoutSub] = mean(winArray);
                    bufoutSub = bufoutSub + 1;
                    winArraySub = 0;
                }
            }
            mTemp = new float[mWindowSize - 1];
            System.arraycopy(buf, buf.length - mWindowSize + 1, mTemp, 0,
                    mWindowSize - 1);
            return mBufout;
        } else {
            float[] bufadd = new float[buf.length + mTemp.length];
            mBufout = new float[bufadd.length - mWindowSize + 1];
            System.arraycopy(mTemp, 0, bufadd, 0, mTemp.length);
            System.arraycopy(buf, 0, bufadd, mTemp.length, buf.length); // 将temp和buf拼接到一块
            for (int i = 0; i < bufadd.length; i++) {
                if ((i + mWindowSize) > bufadd.length)
                    break;
                else {
                    for (int j = i; j < (mWindowSize + i); j++) {
                        winArray[winArraySub] = bufadd[j];
                        winArraySub = winArraySub + 1;
                    }
                    mBufout[bufoutSub] = mean(winArray);
                    bufoutSub = bufoutSub + 1;
                    winArraySub = 0;
                    System.arraycopy(bufadd, bufadd.length - mWindowSize + 1,
                            mTemp, 0, mWindowSize - 1);
                }
            }
            return mBufout;
        }
    }

    public float mean(float[] array) {
        long sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return (float) (sum / array.length);
    }
}
