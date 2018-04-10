package com.didi.safedrive.loc;

import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by didi on 2018/4/10.
 */

public class LocHelper {
    private static  LocationClient LOC_CLIENT = null;

    public static final LocationClient getLocInstance (Context context){
        if (LOC_CLIENT==null){
            synchronized (LocHelper.class){
                if (LOC_CLIENT==null){
                    LOC_CLIENT = new LocationClient(context.getApplicationContext());
                    LOC_CLIENT.setLocOption(getOption());
                }
            }
        }
        return LOC_CLIENT;
    }
    private static final LocationClientOption getOption(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setLocationNotify(true);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        return option;
    }

}
