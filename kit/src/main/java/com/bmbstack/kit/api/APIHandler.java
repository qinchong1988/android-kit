package com.bmbstack.kit.api;

import android.util.SparseArray;

import com.bmbstack.kit.R;
import com.bmbstack.kit.app.BaseApplication;
import com.bmbstack.kit.app.BmbPresenter;
import com.bmbstack.kit.util.NetworkUtils;
import com.bmbstack.kit.util.ToastUtils;
import com.bmbstack.kit.widget.ErrorView;
import com.google.gson.JsonParseException;

import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public class APIHandler {

    private static final String TAG = "ApiHandler";

    private static final SparseArray<HttpInterceptor> HTTP_INTERCEPTOR = new SparseArray<>();

    public interface HttpInterceptor {
        boolean intercept();
    }

    public static void addHttpInterceptor(int code, HttpInterceptor interceptor) {
        HTTP_INTERCEPTOR.put(code, interceptor);
    }

    public interface APIObserver<T> {

        void onSuccess(T value);

        void onComplete();
    }

    public static <T> Observer<T> createObserver(final BmbPresenter bmbPresenter, final boolean showErrorView, final APIObserver<T> apiObserver) {
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
                    apiObserver.onSuccess(value);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!bmbPresenter.isValid()) {
                    return;
                }
                bmbPresenter.hideLoadingView();

                NetError error = getErrorFromException(e);
                HttpInterceptor interceptor = HTTP_INTERCEPTOR.get(error.errno);
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
                apiObserver.onComplete();
            }

            @Override
            public void onComplete() {
                if (!bmbPresenter.isValid()) {
                    return;
                }
                apiObserver.onComplete();
            }
        };
    }

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
        if (e instanceof IllegalStateException || e instanceof JsonParseException) {
            error.errorMsg = BaseApplication.instance().getString(R.string.status_parse_error);
        } else if (e instanceof SocketTimeoutException) {
            error.errorMsg = BaseApplication.instance().getString(R.string.status_network_timeout);
        } else if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            int resId = StatusCode.mHttpStatusMessageMap.get(exception.code());
            error.errorMsg = BaseApplication.instance().getString(resId);
        } else if (e instanceof APIException) {
            error.errorMsg = e.getMessage();
        } else {
            error.errorMsg = BaseApplication.instance().getString(R.string.status_unknow_error);
        }
        return error;
    }
}
