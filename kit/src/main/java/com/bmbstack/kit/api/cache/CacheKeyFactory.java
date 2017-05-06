package com.bmbstack.kit.api.cache;

import com.bmbstack.kit.log.Logger;

import okhttp3.Request;

public interface CacheKeyFactory {

    String createCacheKey(Request request);

    class DefaultCacheKeyFactory implements CacheKeyFactory {
        @Override
        public String createCacheKey(Request request) {
            StringBuilder sb = new StringBuilder();
            sb.append(request.method()).append(" ");
            sb.append(request.url()).append("\n");
            sb.append(request.headers().toString());
            String key = sb.toString();
            Logger.v(CacheUtils.LOG_TAG, "key=> " + key);
            return CacheUtils.hashKey(key);
        }
    }

}
