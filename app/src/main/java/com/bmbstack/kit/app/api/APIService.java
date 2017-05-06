package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.cache.Cache;
import com.bmbstack.kit.api.cache.CacheCall;
import com.bmbstack.kit.api.cache.CacheMode;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {
    String BASE_URL = "http://bianmei.istormcity.com";

    String PUBLIC_USERS = "/public/users";

    @GET("/")
    @Cache(CacheMode.FIRST_CACHE_THEN_REQUEST)
    CacheCall<Home.Resp> home();

    @POST(PUBLIC_USERS)
    CacheCall<CreateUser.Resp> createUser(@Body CreateUser.Req user);

    @GET("/v1/weights")
    @Cache(CacheMode.FIRST_CACHE_THEN_REQUEST)
    CacheCall<WeightToday> weightToday();
}
