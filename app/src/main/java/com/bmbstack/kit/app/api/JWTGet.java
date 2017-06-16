package com.bmbstack.kit.app.api;

import com.blankj.utilcode.util.EmptyUtils;
import com.bmbstack.kit.api.BmbAPI;
import com.bmbstack.kit.api.convert.GsonRequestBodyConverter;
import com.bmbstack.kit.app.account.AccountMgr;
import com.bmbstack.kit.app.storage.CommonTraySp;
import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.util.GsonUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JWTGet implements BmbAPI.JWTGet {

    JWTGet() {
    }

    @Override
    public boolean useJWT() {
        return AccountMgr.getInstance().isLogin();
    }

    @Override
    public String getJWT() {
        return AccountMgr.getInstance().getJWT();
    }

    @Override
    public String refreshJWT() {
        String json = CommonTraySp.getThirdLoginJsonInfo();
        if (EmptyUtils.isEmpty(json)) {
            return null;
        }

        RequestBody body = RequestBody.create(GsonRequestBodyConverter.MEDIA_TYPE, json.getBytes());
        Request request =
                new Request.Builder().url(APIService.BASE_URL + APIService.PUBLIC_USERS).post(body).build();
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            String resBody = response.body().string();
            Logger.d("Okhttp", "need refreshJWT->" + response + " body=" + resBody);
            if (response.isSuccessful()) {
                CreateUser.Resp resp = GsonUtils.fromJson(resBody, CreateUser.Resp.class);
                Logger.v("Okhttp", "refreshJWT resp->" + resp);
                if (resp.isValid()) {
                    resp.data.user.setToken(resp.data.token);
                    AccountMgr.getInstance().saveUser(resp.data.user);
                    return resp.data.token;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
