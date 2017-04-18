package com.bmbstack.kit.api;

import com.bmbstack.kit.app.BaseApplication;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

class OkHttpHelper {

  static final String CACHE_DIR = "httpcache";
  static final int CACHE_MAX_SIZE = 1024 * 1024 * 50;
  static final int TIMEOUT_SECONDS = 30;

  static OkHttpClient.Builder addComm() {
    File cacheFile = new File(BaseApplication.instance().getCacheDir(), CACHE_DIR);
    Cache cache = new Cache(cacheFile, CACHE_MAX_SIZE);
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    return new OkHttpClient.Builder().addInterceptor(new CacheInterceptor())
        .addNetworkInterceptor(new StethoInterceptor())
        .addInterceptor(logging)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS) // set timeout to 30 seconds.
        .cache(cache);
  }
}