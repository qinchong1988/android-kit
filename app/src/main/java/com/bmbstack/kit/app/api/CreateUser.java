package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.BaseResponse;
import com.bmbstack.kit.app.dao.User;
import com.google.gson.annotations.SerializedName;

public class CreateUser {

    public static class Req {
        @SerializedName("accountType")
        public String accountType;
        @SerializedName("openID")
        public String openID;
        @SerializedName("nickname")
        public String nickname;
        @SerializedName("headPhoto")
        public String headPhoto;
        @SerializedName("sex")
        public int sex;
        @SerializedName("height")
        public int height = 165;
        @SerializedName("weightStart")
        public float weightStart = 58.0f;
        @SerializedName("weightCurrent")
        public float weightCurrent = 55.0f;
        @SerializedName("weightTarget")
        public float weightTarget = 45.0f;
        @SerializedName("city")
        public String city;
    }

    public static class Resp extends BaseResponse {
        @SerializedName("data")
        public DataBean data;

        public static class DataBean {
            @SerializedName("token")
            public String token;
            @SerializedName("user")
            public User user;
        }

        public boolean isValid() {
            return super.isValid() && data != null && data.token != null && data.user != null;
        }
    }
}
