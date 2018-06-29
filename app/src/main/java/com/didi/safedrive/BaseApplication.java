package com.didi.safedrive;

import android.app.Application;


/**
 * Created by didi on 2018/4/10.
 */

public class BaseApplication extends Application {

    protected static BaseApplication sApp;

    public static BaseApplication getInst() {
        return sApp;
    }

    public BaseApplication() {
        sApp = this;
    }

//    private static final String AppId = "11425732";
//    private static final String AppKey = "X2OlxhZIrBST6Xrfk89exbc6";
//    private static final String AppSecret = "P4G3EHKItlGLBek4Yf4UrHmPei2zdRkk";

    @Override
    public void onCreate() {
        super.onCreate();
//        SpeechSynthesizer mSpeechSynthesizer = SpeechSynthesizer.getInstance();
//        mSpeechSynthesizer.setContext(this); // this 是Context的之类，如Activity
//        mSpeechSynthesizer.setAppId(AppId);
//        mSpeechSynthesizer.setApiKey(AppKey,AppSecret);
//        mSpeechSynthesizer.auth(TtsMode.MIX);
//        mSpeechSynthesizer.initTts(TtsMode.MIX);
    }
}
