package com.bmbstack.kit.api;

import com.bmbstack.kit.widget.ErrorView;

public class NetError {
    public int errorCode = -1000;
    public String errorMsg;
    public ErrorView.Style style = ErrorView.Style.ERROR_NETWORK;
    public boolean needReload;

    public static NetError createError(int code, String msg) {
        NetError error = new NetError();
        error.errorCode = code;
        error.errorMsg = msg;
        return error;
    }

}
