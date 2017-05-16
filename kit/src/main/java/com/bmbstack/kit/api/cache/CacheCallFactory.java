package com.bmbstack.kit.api.cache;

import android.text.TextUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CacheCallFactory extends CallAdapter.Factory {
    private final CachingSystem cachingSystem;
    private final Executor asyncExecutor;

    public CacheCallFactory(CachingSystem cachingSystem) {
        this.cachingSystem = cachingSystem;
        this.asyncExecutor = new AndroidExecutor();
    }

    public CacheCallFactory(CachingSystem cachingSystem, Executor executor) {
        this.cachingSystem = cachingSystem;
        this.asyncExecutor = executor;
    }

    @Override
    public CallAdapter<?, ?> get(final Type returnType, final Annotation[] annotations,
                                 final Retrofit retrofit) {

        if (getRawType(returnType) != CacheCall.class) {
            return null;
        }

        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException(
                    "CacheCall must have generic type (e.g., CacheCall<ResponseBody>)");
        }

        final Type responseType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
        final Executor callbackExecutor = asyncExecutor;

        return new CacheCallAdapter(callbackExecutor, responseType, annotations, retrofit, cachingSystem);
    }

    private static class CacheCallAdapter<R> implements CallAdapter<R, CacheCall<?>> {
        Executor callbackExecutor;
        Type responseType;
        final Annotation[] annotations;
        final Retrofit retrofit;
        final CachingSystem cachingSystem;

        CacheCallAdapter(Executor callbackExecutor, Type responseType, final Annotation[] annotations,
                         final Retrofit retrofit, final CachingSystem cachingSystem) {
            this.callbackExecutor = callbackExecutor;
            this.responseType = responseType;
            this.annotations = annotations;
            this.retrofit = retrofit;
            this.cachingSystem = cachingSystem;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public CacheCall<?> adapt(Call<R> call) {
            return new CacheCallImpl<>(callbackExecutor, call, responseType(), annotations,
                    retrofit, cachingSystem);
        }
    }

    private static class CacheCallImpl<T> implements CacheCall<T> {
        private final Executor callbackExecutor;
        private final Call<T> baseCall;
        private final Type responseType;
        private final Annotation[] methodAnnotations;
        private final Retrofit retrofit;
        private final CachingSystem cachingSystem;
        private final Request request;
        private CacheMode cacheMode = CacheMode.DEFAULT;

        CacheCallImpl(Executor callbackExecutor, Call<T> baseCall, Type responseType,
                      Annotation[] methodAnnotations, Retrofit retrofit, CachingSystem cachingSystem) {
            this.callbackExecutor = callbackExecutor;
            this.baseCall = baseCall;
            this.responseType = responseType;
            this.methodAnnotations = methodAnnotations;
            this.retrofit = retrofit;
            this.cachingSystem = cachingSystem;
            this.request = baseCall.request();

            for (Annotation annotation : this.methodAnnotations) {
                parseMethodAnnotation(annotation);
            }
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof Cache) {
                cacheMode = ((Cache) annotation).value();
            }
        }

        public void enqueueWithCache(final Callback<T> callback) {
            Runnable enqueueRunnable = new Runnable() {
                @Override
                public void run() {

                    if (cacheMode == CacheMode.REQUEST_FAILED_READ_CACHE) {
                        readFromNet(null, callback);
                        return;
                    }

                    if (cacheMode == CacheMode.IF_NONE_CACHE_REQUEST ||
                            cacheMode == CacheMode.FIRST_CACHE_THEN_REQUEST) {
                        ReadFromCache readFromCache = new ReadFromCache(callback).call();
                        if (cacheMode == CacheMode.IF_NONE_CACHE_REQUEST && readFromCache.haveCache()) {
                            return;
                        }
                        readFromNet(readFromCache.getCacheMd5(), callback);
                    }
                }
            };
            Thread enqueueThread = new Thread(enqueueRunnable);
            enqueueThread.start();
        }

        @Override
        public void enqueue(boolean careCache, final Callback<T> callback) {
            if (!careCache || cacheMode == CacheMode.DEFAULT
                    || cacheMode == CacheMode.NO_CACHE) {
                baseCall.enqueue(new retrofit2.Callback<T>() {

                    @Override
                    public void onResponse(final Call<T> call, final Response<T> response) {
                        callbackExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(call, response, false);
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Call<T> call, final Throwable t) {
                        callbackExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(call, t);
                            }
                        });
                    }

                });
            } else {
                enqueueWithCache(callback);
            }
        }

        @Override
        public void enqueue(final Callback<T> callback) {
            enqueue(true, callback);
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public Request buildRequest() {
            return request.newBuilder().build();
        }

        @Override
        public CacheCall<T> clone() {
            return new CacheCallImpl<>(callbackExecutor, baseCall.clone(), responseType(),
                    methodAnnotations, retrofit, cachingSystem);
        }

        @Override
        public Response<T> execute() throws IOException {
            return baseCall.execute();
        }

        @Override
        public void cancel() {
            baseCall.cancel();
        }

        private void readFromNet(String cacheMd5, final Callback<T> callback) {
             /* Enqueue actual network call */
            final String finalCacheMd = cacheMd5;
            baseCall.enqueue(new retrofit2.Callback<T>() {
                @Override
                public void onResponse(Call<T> call, final Response<T> response) {
                    // Make a main thread runnable
                    Runnable responseRunnable = new Runnable() {
                        @Override
                        public void run() {
                            // for mobile app, we only care of 200
                            if (response.isSuccessful()) {
                                boolean ignore = false;
                                byte[] rawData = CacheUtils.responseToBytes(retrofit, response.body(),
                                        responseType(), methodAnnotations);

                                String netMd5 = CacheUtils.md5(rawData);
                                if (TextUtils.equals(netMd5, finalCacheMd)) {
                                    ignore = true;
                                } else {
                                    cachingSystem.addInCache(response, rawData);
                                }
                                if (!ignore) {
                                    callback.onResponse(baseCall, response, false);
                                }
                            } else {
                                callback.onFailure(baseCall, new HttpException(response));
                            }

                        }
                    };
                    // Run it on the proper thread
                    callbackExecutor.execute(responseRunnable);
                }

                @Override
                public void onFailure(final Call<T> call, final Throwable t) {
                    boolean postOnFailure = true;
                    if (cacheMode == CacheMode.REQUEST_FAILED_READ_CACHE) {
                        ReadFromCache readFromCache = new ReadFromCache(callback).call();
                        if (readFromCache.haveCache()) {
                            postOnFailure = false;
                        }
                    }

                    if (postOnFailure) {
                        Runnable failureRunnable = new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(call, t);
                            }
                        };
                        callbackExecutor.execute(failureRunnable);
                    }

                }
            });
        }

        private class ReadFromCache {
            private final Callback<T> callback;
            private String cacheMd5;
            private boolean haveCache;

            public ReadFromCache(Callback<T> callback) {
                this.callback = callback;
            }

            public String getCacheMd5() {
                return cacheMd5;
            }

            boolean haveCache() {
                return haveCache;
            }

            public ReadFromCache call() {
                byte[] data = cachingSystem.getFromCache(buildRequest());
                if (data != null) {
                    final T convertedData = CacheUtils.bytesToResponse(retrofit, responseType, methodAnnotations,
                            data);
                    haveCache = true;
                    cacheMd5 = CacheUtils.md5(data);
                    Runnable cacheCallbackRunnable = new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(baseCall, Response.success(convertedData), true);
                        }
                    };
                    callbackExecutor.execute(cacheCallbackRunnable);
                }
                return this;
            }
        }
    }
}