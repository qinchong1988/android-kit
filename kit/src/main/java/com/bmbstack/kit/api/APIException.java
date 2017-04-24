package com.bmbstack.kit.api;

public class APIException extends RuntimeException {

    private int code;
    private String msg;

    public APIException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("APIException{");
        sb.append("code=").append(this.code);
        sb.append(", msg=").append(this.msg);
        sb.append('}');
        return sb.toString();
    }
}
