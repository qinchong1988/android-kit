package com.bmbstack.kit.api;

import com.bmbstack.kit.R;
import com.bmbstack.kit.api.cache.Callback;
import com.bmbstack.kit.app.BaseApplication;
import com.bmbstack.kit.app.BmbPresenter;
import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.util.NetworkUtils;
import com.bmbstack.kit.util.ToastUtils;
import com.bmbstack.kit.widget.ErrorView;
import com.google.gson.JsonParseException;

import java.net.SocketTimeoutException;

import io.reactivex.exceptions.CompositeException;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

public class APIHandler {

    private static final String TAG = "APIHandler";
    private static HttpErrorInterceptor httpErrorInterceptor;

    public static void setHttpErrorInterceptor(HttpErrorInterceptor interceptor) {
        httpErrorInterceptor = interceptor;
    }

    public static <T> Callback<T> createCallback(final BmbPresenter bmbPresenter, final boolean showErrorView, final OnResultCallback<T> onResultCallback) {
        return new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, Response<T> response, boolean fromCache) {
                if (!bmbPresenter.isValid()) {
                    return;
                }
                bmbPresenter.hideLoadingView();
                onResultCallback.onSuccess(response.body(), fromCache);
                onResultCallback.onComplete();
            }

            @Override
            public void onFailure(Call<T> call, Throwable e) {
                if (!bmbPresenter.isValid()) {
                    return;
                }
                bmbPresenter.hideLoadingView();

                NetError error = getErrorFromException(e);
                if (httpErrorInterceptor != null) {
                    if (httpErrorInterceptor.intercept(error.errorCode)) {
                        return;
                    }
                }
                if (showErrorView) {
                    bmbPresenter.showErrorView(error.errorMsg, error.style);
                } else {
                    ToastUtils.warning(error.errorMsg);
                }
                onResultCallback.onComplete();
            }

        };
    }

    /**
     * 网络请求异常处理
     *
     * @param e Throwable
     * @return NetError
     */
    public static NetError getErrorFromException(Throwable e) {
        if (e != null) {
            e.printStackTrace();
        }

        NetError error = new NetError();
        if (!NetworkUtils.isConnected()) {
            error.errorMsg = BaseApplication.instance().getString(R.string.status_network_error);
            error.style = ErrorView.Style.ERROR_NETWORK;
            return error;
        }

        if (e instanceof CompositeException) {
            CompositeException compositeException = (CompositeException) e;
            for (Throwable t : compositeException.getExceptions()) {
                if (t instanceof APIException) {
                    APIException exception = (APIException) t;
                    error.errorCode = exception.getCode();
                    error.errorMsg = exception.getMsg();
                } else {
                    Logger.e(TAG, t.getClass().getSimpleName() + ":" + t.getMessage());
                }
            }
        } else if (e instanceof IllegalStateException || e instanceof JsonParseException) {
            error.errorMsg = BaseApplication.instance().getString(R.string.status_parse_error);
        } else if (e instanceof SocketTimeoutException) {
            error.errorMsg = BaseApplication.instance().getString(R.string.status_network_timeout);
        } else if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            int resId = StatusCode.mHttpStatusMessageMap.get(exception.code());
            error.errorMsg = BaseApplication.instance().getString(resId);
        } else if (e instanceof APIException) {
            APIException exception = (APIException) e;
            error.errorCode = exception.getCode();
            error.errorMsg = exception.getMsg();
        } else {
            error.errorMsg = BaseApplication.instance().getString(R.string.status_unknow_error);
        }
        return error;
    }

    public interface HttpErrorInterceptor {
        boolean intercept(int code);
    }

    public interface OnResultCallback<T> {
        void onSuccess(T value, boolean fromCache);

        void onComplete();
    }
}
