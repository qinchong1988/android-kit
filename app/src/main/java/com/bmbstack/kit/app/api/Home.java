package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Home {

    public static class Resp extends BaseResponse {
        @SerializedName("data")
        public Data data;

        public static class Data {
            @SerializedName("title")
            public String title;
        }
    }
}
