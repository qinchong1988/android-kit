package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.APIHandler;
import com.bmbstack.kit.api.BmbAPI;
import com.bmbstack.kit.api.cache.Callback;
import com.bmbstack.kit.app.account.AccountMgr;

public enum API {
    INST;
    private APIService mAPIService = null;

    API() {
        APIHandler.setHttpErrorInterceptor(new APIHandler.HttpErrorInterceptor() {
            @Override
            public boolean intercept(int code) {
                if (code == 401) {
                    AccountMgr.getInstance().logout();
                    return true;
                }
                return false;
            }
        });
        mAPIService = (APIService) new BmbAPI.Builder<>(APIService.BASE_URL, APIService.class).useJWT(new JWTGet()).build();
    }

    public void home(boolean careCache, Callback<Home.Resp> callback) {
        mAPIService.home().enqueue(careCache, callback);
    }

    public void createUser(boolean careCache, CreateUser.Req req, Callback<CreateUser.Resp> callback) {
        mAPIService.createUser(req).enqueue(careCache, callback);
    }

    public void weightToday(boolean careCache, Callback<WeightToday> callback) {
        mAPIService.weightToday().enqueue(careCache, callback);
    }
}
