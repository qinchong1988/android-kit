package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.CacheInterceptor;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    String BASE_URL = "http://bianmei.istormcity.com";

    String PUBLIC_USERS = "/public/users";

    @GET("/")
    Observable<Home.Resp> home();

    @Headers(CacheInterceptor.CACHE_CONTROL_NO_STORE)
    @GET("/")
    Observable<Home.Resp> home_NoStore();

    @POST(PUBLIC_USERS)
    Observable<CreateUser.Resp> createUser(@Body CreateUser.Req user);

    @GET("/v1/weights")
    Observable<WeightToday> weightToday();
}
