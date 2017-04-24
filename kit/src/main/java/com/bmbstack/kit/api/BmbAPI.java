package com.bmbstack.kit.api;

import com.bmbstack.kit.api.convert.GsonConverterFactory;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class BmbAPI {

    private BmbAPI() {
    }

    public static void rx(Observable<?> observable, Observer observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public interface JWTGet {

        boolean useJWT();

        /**
         * 从缓存中读取jwt
         */
        String getJWT();

        /**
         * 从网络刷新jwt信息，用于401的自动重试机制
         */
        String refreshJWT();
    }

    public static class Builder<T> {
        String baseUrl;
        Class<T> api;
        OkHttpClient.Builder clientBuilder;
        PublicParamInterceptor publicParamInterceptor;

        public Builder(String baseUrl, Class<T> api) {
            this.baseUrl = baseUrl;
            this.api = api;
            clientBuilder = OkHttpHelper.addComm();
            publicParamInterceptor = new PublicParamInterceptor();
        }

        public Builder addInterceptor(Interceptor interceptor) {
            clientBuilder.addInterceptor(interceptor);
            return this;
        }

        public Builder useJWT(JWTGet jwt) {
            publicParamInterceptor.setApiJwtGet(jwt);
            clientBuilder.addInterceptor(publicParamInterceptor);
            clientBuilder.authenticator(new TokenAuthenticator(jwt));
            return this;
        }

        public T build() {
            return new Retrofit.Builder().baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientBuilder.build())
                    .build()
                    .create(api);
        }
    }
}
