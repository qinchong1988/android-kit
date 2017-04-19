package com.bmbstack.kit.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class PublicParamInterceptor implements Interceptor {

  private BmbAPI.JWTGet jwtGet;

  PublicParamInterceptor() {
  }

  void setApiJwtGet(BmbAPI.JWTGet jwt) {
    jwtGet = jwt;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request.Builder builder = chain.request().newBuilder();
    // JWT :Authorization: Bearer tokenValue
    if (jwtGet != null && jwtGet.useJWT()) {
      builder.header(JWTHelper.AUTHORIZATION, JWTHelper.getAuthorizationVal(jwtGet.getJWT()));
    }
    return chain.proceed(builder.build());
  }
}