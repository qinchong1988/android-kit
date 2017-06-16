package com.bmbstack.kit.app.storage;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bmbstack.kit.app.App;
import com.bmbstack.kit.app.api.CreateUser;
import com.bmbstack.kit.util.GsonUtils;

import net.grandcentrix.tray.TrayPreferences;

public class CommonTraySp extends TrayPreferences {

    private static final String NAME = "common";
    public static final int VERSION = 1;

    private static final String THIRD_LOGIN_INFO_JSON = "third_login_info_json";

    private static class SingleInstance {
        private static CommonTraySp INSTANCE = new CommonTraySp(App.instance());
    }

    public static CommonTraySp sp() {
        return SingleInstance.INSTANCE;
    }

    public CommonTraySp(@NonNull Context context) {
        super(context, NAME, VERSION);
    }

    public static boolean saveThirdLoginInfo(CreateUser.Req req) {
        return sp().put(THIRD_LOGIN_INFO_JSON, GsonUtils.toJson(req));
    }

    public static boolean removeThirdLoginInfo() {
        return sp().remove(THIRD_LOGIN_INFO_JSON);
    }

    public static String getThirdLoginJsonInfo() {
        String json = sp().getString(THIRD_LOGIN_INFO_JSON, null);
        if (json == null) {
            return null;
        }
        return json;
    }

    public static CreateUser.Req getThirdLoginInfo() {
        String json = sp().getString(THIRD_LOGIN_INFO_JSON, null);
        if (json == null) {
            return null;
        }
        return GsonUtils.fromJson(json, CreateUser.Req.class);
    }
}
