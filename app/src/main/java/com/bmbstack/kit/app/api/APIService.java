package com.bmbstack.kit.app.api;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    String BASE_URL = "http://bianmei.istormcity.com";

    String PUBLIC_USERS = "/public/users";

    @GET("/")
    Observable<Home.Resp> home();

    @POST(PUBLIC_USERS)
    Observable<CreateUser.Resp> createUser(@Body CreateUser.Req user);

    @GET("/v1/weights")
    Observable<WeightToday> weightToday();
}
