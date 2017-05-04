package com.bmbstack.kit.api;

import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.util.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {

    public static final String CACHE_CONTROL_NO_STORE = "Cache-Control: no-store";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean netConnected = NetworkUtils.isConnected();
        CacheControl reqControl = request.cacheControl();
        if ((!reqControl.noCache() && !reqControl.noStore()) && !netConnected) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            Logger.w("no network,get from cache" + " noCache=" + reqControl.noCache() + ",noStore=" + reqControl.noStore());
        }
        Response originalResponse = chain.proceed(request);
        if (netConnected) {
            //有网的时候读接口上的@Headers里的配置,这里进行统一的设置
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                    .removeHeader("Pragma")
                    .build();
        }
    }
}
