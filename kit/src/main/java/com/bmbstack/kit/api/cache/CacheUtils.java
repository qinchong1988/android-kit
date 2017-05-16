package com.bmbstack.kit.api.cache;

import com.bmbstack.kit.api.APIException;
import com.bmbstack.kit.log.Logger;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class CacheUtils {

    public static final String LOG_TAG = "CacheCall";

    public static String hashKey(String key) {
        return Hashing.sha1().hashString(key, Charset.defaultCharset()).toString();
    }

    public static String md5(byte[] bytes) {
        return Hashing.md5().hashBytes(bytes).toString();
    }

    /*
     * TODO: Do an inverse iteration instead so that the latest Factory that supports <T>
     * does the job?
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] responseToBytes(Retrofit retrofit, T data, Type dataType,
                                             Annotation[] annotations) throws APIException {
        for (Converter.Factory factory : retrofit.converterFactories()) {
            if (factory == null) continue;
            Converter<T, RequestBody> converter =
                    (Converter<T, RequestBody>) factory.requestBodyConverter(dataType, null, annotations, retrofit);
            if (converter != null) {
                Buffer buff = new Buffer();
                try {
                    converter.convert(data).writeTo(buff);
                } catch (IOException ioException) {
                    continue;
                }
                return buff.readByteArray();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T bytesToResponse(Retrofit retrofit, Type dataType, Annotation[] annotations,
                                        byte[] data) {
        for (Converter.Factory factory : retrofit.converterFactories()) {
            if (factory == null) continue;
            Converter<ResponseBody, T> converter =
                    (Converter<ResponseBody, T>) factory.responseBodyConverter(dataType, annotations, retrofit);

            if (converter != null) {
                try {
                    return converter.convert(ResponseBody.create(null, data));
                } catch (IOException | NullPointerException exc) {
                    Logger.e(LOG_TAG, "", exc);
                }
            }
        }

        return null;
    }
}
