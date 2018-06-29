package com.didi.safedrive;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpHelper {
    private static final int TIME_OUT = 6;
    private static final OkHttpClient.Builder BUILDER = new OkHttpClient.Builder();
    public static final OkHttpClient CLIENT = BUILDER
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build();
}