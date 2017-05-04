package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.APIHandler;
import com.bmbstack.kit.api.BmbAPI;
import com.bmbstack.kit.app.account.AccountMgr;

import io.reactivex.Observer;

public enum API {
    INST;
    private APIService mAPIService = null;

    API() {
        APIHandler.addHttpErrorInterceptor(401, new APIHandler.HttpErrorInterceptor() {
            @Override
            public boolean intercept() {
                AccountMgr.getInstance().logout();
                return true;
            }
        });
        mAPIService = (APIService) new BmbAPI.Builder<>(APIService.BASE_URL, APIService.class).useJWT(new JWTGet()).build();
    }

    public void home(Observer<Home.Resp> observer) {
        BmbAPI.rx(mAPIService.home(), observer);
    }

    public void home_NoStore(Observer<Home.Resp> observer) {
        BmbAPI.rx(mAPIService.home_NoStore(), observer);
    }

    public void createUser(CreateUser.Req req, Observer<CreateUser.Resp> observer) {
        BmbAPI.rx(mAPIService.createUser(req), observer);
    }

    public void weightToday(Observer<WeightToday> observer) {
        BmbAPI.rx(mAPIService.weightToday(), observer);
    }
}
