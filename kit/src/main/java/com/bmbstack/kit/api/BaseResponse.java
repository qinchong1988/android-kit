package com.bmbstack.kit.api;

import com.bmbstack.kit.R;
import com.bmbstack.kit.app.BaseApplication;
import com.bmbstack.kit.proguard.IKeepClass;

public class BaseResponse implements IKeepClass {

    public int code = -100;
    public String msg;

    public static final int CODE_SUCCESS = BaseApplication.instance().getResources().getInteger(R.integer.code_success);

    public static final int CODE_ERROR_TOKEN = 3;

    public boolean isValid() {
        return code == CODE_SUCCESS;
    }

    public boolean reLogin() {
        return code == CODE_ERROR_TOKEN;
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
}
