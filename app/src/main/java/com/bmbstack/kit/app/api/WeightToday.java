package com.bmbstack.kit.app.api;

import com.bmbstack.kit.api.BmbResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeightToday extends BmbResponse {
    @SerializedName("data")
    public WeightBean data;
}
