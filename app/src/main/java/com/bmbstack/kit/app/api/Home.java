package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.BmbResponse;
import com.google.gson.annotations.SerializedName;

public class Home {

    public static class Resp extends BmbResponse {
        @SerializedName("data")
        public Data data;

        public static class Data {
            @SerializedName("title")
            public String title;
        }
    }
}
