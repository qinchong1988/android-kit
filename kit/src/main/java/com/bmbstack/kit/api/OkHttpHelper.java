package com.bmbstack.kit.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

class OkHttpHelper {

    static final int TIMEOUT_SECONDS = 30;

    static OkHttpClient.Builder create() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(logging)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

}