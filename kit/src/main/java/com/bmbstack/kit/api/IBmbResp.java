package com.bmbstack.kit.api;

import com.google.gson.JsonElement;

public interface IBmbResp {
    int code();

    String msg();

    boolean isValid();

    void parseValid(JsonElement jsonElement) throws APIException;
}
