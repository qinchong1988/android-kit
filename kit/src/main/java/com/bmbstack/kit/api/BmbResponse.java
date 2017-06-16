package com.bmbstack.kit.api;

import com.bmbstack.kit.proguard.IKeepClass;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BmbResponse implements IBmbResp, IKeepClass {

    protected int code = -100;
    protected String msg;

    public static final int CODE_SUCCESS = 20000;

    public boolean isValid() {
        return code == CODE_SUCCESS;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append("{");
        sb.append("code=").append(code);
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }

    @Override
    public void parseValid(JsonElement jsonElement) throws APIException {
        if (jsonElement.isJsonObject()) {
            JsonObject json = jsonElement.getAsJsonObject();
            if (json.has("code")) {
                code = json.get("code").getAsInt();
            }
            if (json.has("msg")) {
                msg = json.get("msg").getAsString();
            }
            if (!isValid()) {
                throw new APIException(code(), msg());
            }
        }
    }
}
