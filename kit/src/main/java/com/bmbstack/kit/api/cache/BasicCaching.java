package com.bmbstack.kit.api.cache;

import android.content.Context;
import android.util.LruCache;

import com.bmbstack.kit.log.Logger;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Request;
import retrofit2.Response;

/**
 * A basic caching system that stores responses in RAM & disk
 * It uses {@link DiskLruCache} and {@link LruCache} to do the former.
 */
public class BasicCaching implements CachingSystem {

    private static final long REASONABLE_DISK_SIZE = 1024 * 1024; // 1 MB
    private static final int REASONABLE_MEM_ENTRIES = 50; // 50 entries

    private DiskLruCache diskCache;
    private LruCache<String, Object> memoryCache;
    private CacheKeyFactory cacheKeyFactory;

    public BasicCaching(CacheKeyFactory cacheKeyFactory, File diskDirectory, long maxDiskSize, int memoryEntries) {
        if (cacheKeyFactory != null) {
            this.cacheKeyFactory = cacheKeyFactory;
        } else {
            this.cacheKeyFactory = new CacheKeyFactory.DefaultCacheKeyFactory();
        }
        try {
            diskCache = DiskLruCache.open(diskDirectory, 1, 1, maxDiskSize);
        } catch (IOException exc) {
            Logger.e(CacheUtils.LOG_TAG, "", exc);
            diskCache = null;
        }
        memoryCache = new LruCache<>(memoryEntries);
    }

    public BasicCaching(File diskDirectory, long maxDiskSize, int memoryEntries) {
        this(null, diskDirectory, maxDiskSize, memoryEntries);
    }

    /***
     * Constructs a BasicCaching system using settings that should work for everyone
     *
     * @param context
     * @return
     */
    public static BasicCaching fromCtx(Context context) {
        return new BasicCaching(
                new File(context.getCacheDir(), "retrofit_cache_call"),
                REASONABLE_DISK_SIZE,
                REASONABLE_MEM_ENTRIES);
    }

    @Override
    public <T> void addInCache(Response<T> response, byte[] rawResponse) {
        String cacheKey = cacheKeyFactory.createCacheKey(response.raw().request());
        memoryCache.put(cacheKey, rawResponse);
        try {
            DiskLruCache.Editor editor = diskCache.edit(cacheKey);
            editor.set(0, new String(rawResponse, Charset.defaultCharset()));
            editor.commit();
        } catch (IOException exc) {
            Logger.e(CacheUtils.LOG_TAG, "", exc);
        }
    }

    @Override
    public <T> byte[] getFromCache(Request request) {
        String cacheKey = cacheKeyFactory.createCacheKey(request);
        byte[] memoryResponse = (byte[]) memoryCache.get(cacheKey);
        if (memoryResponse != null) {
            Logger.d(CacheUtils.LOG_TAG, "Memory hit!");
            return memoryResponse;
        }

        try {
            DiskLruCache.Snapshot cacheSnapshot = diskCache.get(cacheKey);
            if (cacheSnapshot != null) {
                Logger.d(CacheUtils.LOG_TAG, "Disk hit!");
                return cacheSnapshot.getString(0).getBytes();
            } else {
                return null;
            }
        } catch (IOException exc) {
            return null;
        }
    }
}
