package com.bmbstack.kit.api;

import com.bmbstack.kit.widget.ErrorView;

public class NetError {
    public int errno = -1000;
    public String errorMsg;
    public ErrorView.Style style = ErrorView.Style.ERROR_NETWORK;
    public boolean needReload;

    public static NetError createError(int errno, String msg) {
        NetError error = new NetError();
        error.errno = errno;
        error.errorMsg = msg;
        return error;
    }

    public static NetError createError(BaseResponse response) {
        NetError error = new NetError();
        error.errno = response.code;
        error.errorMsg = response.msg;
        return error;
    }
}
