package com.didi.safedrive.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.didi.safedrive.BaseApplication;

public class SettingSharedPrefHelper {
    public static final String PREF_NAME = "adfloat_lottie_info_pref";
    private SharedPreferences mLottiePreference;

    public SettingSharedPrefHelper() {
        mLottiePreference = BaseApplication.getInst().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mLottiePreference.edit();
        editor.putInt(getFullKey(key), value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mLottiePreference.edit();
        editor.putBoolean(getFullKey(key), value);
        editor.apply();
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mLottiePreference.edit();
        editor.putString(getFullKey(key), value);
        editor.apply();
    }

    public int getInt(String key, int defValue) {
        return mLottiePreference.getInt(getFullKey(key), defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mLottiePreference.getBoolean(getFullKey(key), defValue);
    }

    public String getString(String key, String defValue) {
        return mLottiePreference.getString(getFullKey(key), defValue);
    }

    public boolean contains(String key) {
        return mLottiePreference.contains(getFullKey(key));
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = mLottiePreference.edit();
        editor.remove(getFullKey(key));
        editor.apply();
    }

    private String getFullKey(String key) {
        return "hgp:" + key;
    }

    public static void clearAll() {
        BaseApplication.getInst().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
