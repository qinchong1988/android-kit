package com.bmbstack.kit.api;

import android.util.SparseArray;

import com.bmbstack.kit.R;

public class StatusCode {

    public static SparseArray<Integer> mHttpStatusMessageMap = new SparseArray<>();

    static {
        // http status init
        mHttpStatusMessageMap.put(200, R.string.status_ok);
        mHttpStatusMessageMap.put(201, R.string.status_ok);
        mHttpStatusMessageMap.put(202, R.string.status_ok);
        mHttpStatusMessageMap.put(400, R.string.status_resource_not_found);
        mHttpStatusMessageMap.put(401, R.string.status_401_unauthorized);
        mHttpStatusMessageMap.put(300, R.string.status_resource_not_found);
        mHttpStatusMessageMap.put(404, R.string.status_resource_not_found);
        mHttpStatusMessageMap.put(500, R.string.status_server_error);
        mHttpStatusMessageMap.put(501, R.string.status_server_error);
        mHttpStatusMessageMap.put(502, R.string.status_server_error);
    }
}
