package com.bmbstack.kit.app.api;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.LifeCache;
import io.rx_cache2.Reply;

/**
 * Created by wangming on 4/26/17.
 */
public interface CacheProviders {

    @LifeCache(duration = 5, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<WeightToday>> weightToday(Observable<WeightToday> weightToday, DynamicKey lastID, EvictDynamicKey evictPage);
}
