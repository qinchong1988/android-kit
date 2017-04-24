package com.bmbstack.kit.app.api;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {
    String BASE_URL = "http://bianmei.istormcity.com";

    String HOME = "/";
    String PUBLIC_USERS = "/public/users";

    @GET(HOME)
    Observable<Home.Resp> home();

    @POST(PUBLIC_USERS)
    Observable<CreateUser.Resp> createUser(@Body CreateUser.Req user);
}
