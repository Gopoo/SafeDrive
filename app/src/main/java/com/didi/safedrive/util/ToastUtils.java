package com.didi.safedrive.util;

import android.content.Context;
import android.content.IntentFilter;
import android.widget.Toast;

/**
 * Created by didi on 2018/4/10.
 */

public class ToastUtils {
    private static  Toast TOAST = null;

    public static void show(Context context,String content){
        if (TOAST==null){
            synchronized (ToastUtils.class){
                if (TOAST==null){
                    TOAST = Toast.makeText(context.getApplicationContext(),content,Toast.LENGTH_SHORT);
                }
            }
        }else{
            TOAST.setText(content);
            TOAST.show();
        }
    }
}
