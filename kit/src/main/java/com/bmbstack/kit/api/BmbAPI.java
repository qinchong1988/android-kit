package com.bmbstack.kit.api;

import android.content.Context;

import com.bmbstack.kit.api.cache.BasicCaching;
import com.bmbstack.kit.api.cache.CacheCallFactory;
import com.bmbstack.kit.api.convert.GsonConverterFactory;
import com.bmbstack.kit.app.BaseApplication;

import java.io.File;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class BmbAPI {

    private BmbAPI() {
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
        Class<T> apiServiceClass;
        OkHttpClient.Builder clientBuilder;
        PublicParamInterceptor publicParamInterceptor;
        CacheCallFactory cacheCallFactory;

        public Builder(String baseUrl, Class<T> apiServiceClass) {
            this.baseUrl = baseUrl;
            this.apiServiceClass = apiServiceClass;
            clientBuilder = OkHttpHelper.create();
            publicParamInterceptor = new PublicParamInterceptor();

            File file = BaseApplication.instance().getDir("retrofit_cache_call", Context.MODE_PRIVATE);
            cacheCallFactory = new CacheCallFactory(new BasicCaching(file, 5 * 1024 * 1024, 50));
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
                    .addCallAdapterFactory(cacheCallFactory)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientBuilder.build())
                    .build()
                    .create(apiServiceClass);
        }
    }
}
