package com.bmbstack.kit.api;

import com.bmbstack.kit.log.Logger;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * https://github.com/square/okhttp/wiki/Recipes#handling-authentication
 */
public class TokenAuthenticator implements Authenticator {

    private BmbAPI.JWTGet jwt;

    public TokenAuthenticator(BmbAPI.JWTGet jwt) {
        this.jwt = jwt;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Logger.d("OkHttp", "Authenticating for response: " + response);
        Logger.d("OkHttp", "Challenges: " + response.challenges());

        String refreshJWT = jwt.refreshJWT();
        Logger.d("OkHttp", "refreshJWT=" + refreshJWT);
        if (refreshJWT != null && refreshJWT.trim().length() > 0) {

            if (JWTHelper.getAuthorizationVal(refreshJWT).equals(response.request().header(JWTHelper.AUTHORIZATION))) {
                return null;  // If we already failed with these jwt, don't retry.
            }

            return response.request()
                    .newBuilder()
                    .header(JWTHelper.AUTHORIZATION, JWTHelper.getAuthorizationVal(refreshJWT))
                    .build();
        } else {
            return null;
        }
    }
}