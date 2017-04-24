package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.BaseResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeightToday extends BaseResponse {
    @SerializedName("data")
    @Expose(deserialize = false, serialize = false)
    public WeightBean data;
}
