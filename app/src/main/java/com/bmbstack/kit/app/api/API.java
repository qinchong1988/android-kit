package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.APIHandler;
import com.bmbstack.kit.api.BmbAPI;
import com.bmbstack.kit.app.App;
import com.bmbstack.kit.app.account.AccountMgr;

import io.reactivex.Observer;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictProvider;

public enum API {
    INST;
    private APIService mAPIService = null;
    private CacheProviders mCacheProviders = null;

    API() {
        APIHandler.addHttpErrorInterceptor(401, new APIHandler.HttpErrorInterceptor() {
            @Override
            public boolean intercept() {
                AccountMgr.getInstance().logout();
                return true;
            }
        });
        mAPIService = (APIService) new BmbAPI.Builder<>(APIService.BASE_URL, APIService.class).useJWT(new JWTGet()).build();
        mCacheProviders = new BmbAPI.CacheBuilder<>(App.instance(), CacheProviders.class).build();
    }

    public void home(Observer<Home.Resp> observer) {
        BmbAPI.rx(mAPIService.home(), observer);
    }

    public void createUser(CreateUser.Req req, Observer<CreateUser.Resp> observer) {
        BmbAPI.rx(mAPIService.createUser(req), observer);
    }

    public void weightToday(Observer<WeightToday> observer) {
        BmbAPI.rx(
                mCacheProviders.weightToday(
                        mAPIService.weightToday(),
                        new DynamicKey(0),
                        new EvictProvider(true)
                ),
                observer);
    }
}
