package com.bmbstack.kit.api;

import com.bmbstack.kit.proguard.IKeepClass;

public class BaseResponse implements IKeepClass {

  public int code = -100;
  public String msg;

  public static final int ERRNO_SUCCESS = 20000;

  public static final int ERRNO_TOKEN_ERROR = 3;

  public boolean isValid() {
    return code == ERRNO_SUCCESS;
  }

  public boolean reLogin() {
    return code == ERRNO_TOKEN_ERROR;
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder(this.getClass().getName());
    sb.append("{");
    sb.append("code=").append(code);
    sb.append(", msg='").append(msg).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
