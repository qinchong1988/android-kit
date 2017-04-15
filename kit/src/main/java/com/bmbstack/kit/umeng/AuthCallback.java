package com.bmbstack.kit.umeng;


public interface AuthCallback<T extends BaseInfo> {
    void onComplete(int var2, T info);

    void onError(int var2, Throwable throwable);

    void onCancel(int var2);
}
