package com.bmbstack.kit.api;

import android.util.SparseArray;

import com.bmbstack.kit.R;
import com.bmbstack.kit.app.BaseApplication;
import com.bmbstack.kit.app.BmbPresenter;
import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.util.NetworkUtils;
import com.bmbstack.kit.util.ToastUtils;
import com.bmbstack.kit.widget.ErrorView;
import com.google.gson.JsonParseException;

import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import retrofit2.HttpException;

public class APIHandler {

    private static final String TAG = "APIHandler";
    private static final SparseArray<HttpErrorInterceptor> HTTP_ERROR_INTERCEPTORS = new SparseArray<>();

    public static void addHttpErrorInterceptor(int code, HttpErrorInterceptor interceptor) {
        HTTP_ERROR_INTERCEPTORS.put(code, interceptor);
    }

    public static <T> Observer<T> createObserver(final BmbPresenter bmbPresenter, final boolean showErrorView, final OnResultCallback<T> onResultCallback) {
        return new Observer<T>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T value) {
                if (!bmbPresenter.isValid()) {
                    return;
                }
                bmbPresenter.hideLoadingView();
                BaseResponse response = ((BaseResponse) value);
                if (response.isValid()) {
                    onResultCallback.onSuccess(value);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!bmbPresenter.isValid()) {
                    return;
                }
                bmbPresenter.hideLoadingView();

                NetError error = getErrorFromException(e);
                HttpErrorInterceptor interceptor = HTTP_ERROR_INTERCEPTORS.get(error.errorCode);
                if (interceptor != null) {
                    if (interceptor.intercept()) {
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

            @Override
            public void onComplete() {
                if (!bmbPresenter.isValid()) {
                    return;
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
        boolean intercept();
    }

    public interface OnResultCallback<T> {
        void onSuccess(T value);

        void onComplete();
    }
}
