package com.bmbstack.kit.api;

import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.util.Base64;
import com.bmbstack.kit.util.GsonUtils;

import java.io.UnsupportedEncodingException;

public class JWTHelper {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";

    public static Payload getPayLoad(String jwt) {
        String[] jwts = jwt.split("\\.");
        if (jwts.length != 3) {
            return null;
        }
        String payload = null;
        try {
            payload = new String(Base64.decode(jwts[1]));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        Logger.v("payload=>" + payload);
        return GsonUtils.fromJson(payload, Payload.class);
    }

    public static boolean isJWTExpired(String jwt) {
        Payload payload = getPayLoad(jwt);
        if (payload == null) {
            return true;
        }
        return System.currentTimeMillis() / 1000 >= payload.exp;
    }

    public static String getAuthorizationVal(String jwt) {
        return BEARER + " " + jwt;
    }
}
