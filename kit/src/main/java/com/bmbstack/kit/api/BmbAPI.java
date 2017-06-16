package com.bmbstack.kit.api;

import android.content.Context;

import com.bmbstack.kit.api.cache.BasicCaching;
import com.bmbstack.kit.api.cache.CacheCallFactory;
import com.bmbstack.kit.api.convert.GsonConverterFactory;
import com.bmbstack.kit.app.BaseApplication;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;

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


    private static class SerializationExclusion implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            final Expose expose = f.getAnnotation(Expose.class);
            return expose != null && !expose.serialize();
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    private static Gson createGsonConverter(IBmbResp resp) {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory(resp))
                .addSerializationExclusionStrategy(new SerializationExclusion())
                .create();
    }

    private static class ItemTypeAdapterFactory implements TypeAdapterFactory {
        IBmbResp resp;

        ItemTypeAdapterFactory(IBmbResp resp) {
            this.resp = resp;
        }

        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            return new TypeAdapter<T>() {
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                public T read(JsonReader in) throws IOException {
                    JsonElement jsonElement = elementAdapter.read(in);
                    resp.parseValid(jsonElement);
                    return delegate.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }
    }

    public static class Builder<T> {
        String baseUrl;
        Class<T> apiServiceClass;
        OkHttpClient.Builder clientBuilder;
        PublicParamInterceptor publicParamInterceptor;
        CacheCallFactory cacheCallFactory;
        IBmbResp resp;

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

        public Builder baseResponse(IBmbResp resp) {
            this.resp = resp;
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
                    .addConverterFactory(GsonConverterFactory.create(
                            createGsonConverter(this.resp == null ? new BmbResponse() : this.resp)
                    ))
                    .client(clientBuilder.build())
                    .build()
                    .create(apiServiceClass);
        }
    }
}
