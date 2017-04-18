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
import retrofit2.HttpException;

public class ExceptionHandler {

  private static final String TAG = "ExceptionHandler";

  private static final SparseArray<HttpInterceptor> HTTP_INTERCEPTOR = new SparseArray<>();

  public interface HttpInterceptor {
    boolean intercept();
  }

  public static void addHttpInterceptor(int code, HttpInterceptor interceptor) {
    HTTP_INTERCEPTOR.put(code, interceptor);
  }

  public interface EObserver<T> {

    void onSuccess(T value);

    void onComplete();
  }

  public static <T> Observer<T> createObserver(final BmbPresenter bmbPresenter,
      final boolean showErrorView, final EObserver<T> observer) {
    return new Observer<T>() {

      @Override public void onSubscribe(Disposable d) {

      }

      @Override public void onNext(T value) {
        if (!bmbPresenter.isValid()) {
          return;
        }
        bmbPresenter.hideLoadingView();
        BaseResponse response = ((BaseResponse) value);
        if (response.isValid()) {
          observer.onSuccess(value);
        } else {
          ExceptionHandler.handleSystemException(response, true);
        }
      }

      @Override public void onError(Throwable e) {
        if (!bmbPresenter.isValid()) {
          return;
        }
        bmbPresenter.hideLoadingView();
        ExceptionHandler.onError(bmbPresenter, showErrorView, e);
        observer.onComplete();
      }

      @Override public void onComplete() {
        if (!bmbPresenter.isValid()) {
          return;
        }
        observer.onComplete();
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
      error.style = ErrorView.Style.ERROR_NETWOR;
      return error;
    }
    int statusCode;
    if (e instanceof HttpException) {
      HttpException exception = (HttpException) e;
      statusCode = error.errno = exception.code();
    } else if (e instanceof NetException) {
      NetException netException = (NetException) e;
      statusCode = error.errno = netException.getStatusCode();
    } else if (e instanceof IllegalStateException || e instanceof JsonParseException) {
      error.errorMsg = BaseApplication.instance().getString(R.string.status_parse_error);
      return error;
    } else if (e instanceof SocketTimeoutException) {
      error.errorMsg = BaseApplication.instance().getString(R.string.status_network_timeout);
      return error;
    } else {
      error.errorMsg = BaseApplication.instance().getString(R.string.status_unknow_error);
      return error;
    }
    Integer msgResource = StatusCode.mHttpStatusMessageMap.get(statusCode);
    int resId;
    if (msgResource != null) {
      resId = msgResource;
    } else if (statusCode / 100 == 2) {
      resId = StatusCode.mHttpStatusMessageMap.get(200);
    } else if (statusCode / 100 == 4) {
      resId = StatusCode.mHttpStatusMessageMap.get(300);
    } else if (statusCode / 100 == 5) {
      resId = StatusCode.mHttpStatusMessageMap.get(500);
    } else {
      resId = R.string.status_unknow_error;
    }
    error.errorMsg = BaseApplication.instance().getString(resId);
    return error;
  }

  public static void toastMessageByHttpStatusCode(int statusCode) {
    Logger.d("=== HTTP status:" + statusCode);
    Integer msgResource = StatusCode.mHttpStatusMessageMap.get(statusCode);
    int resId;
    if (msgResource != null) {
      resId = msgResource;
    } else if (statusCode / 100 == 2) {
      resId = StatusCode.mHttpStatusMessageMap.get(200);
    } else if (statusCode / 100 == 4) {
      resId = StatusCode.mHttpStatusMessageMap.get(300);
    } else if (statusCode / 100 == 5) {
      resId = StatusCode.mHttpStatusMessageMap.get(500);
    } else {
      resId = R.string.status_network_error;
    }
    ToastUtils.error(resId);
  }

  public static void toastExceptionByType(Throwable e) {
    if (e != null) {
      e.printStackTrace();
    }
    if (!NetworkUtils.isConnected()) {
      ToastUtils.error(R.string.status_network_error);
      return;
    }
    Logger.w(TAG, "---intercept exception:" + e);
    if (e instanceof HttpException) {
      HttpException exception = (HttpException) e;
      int httpStatusCode = exception.code();
      toastMessageByHttpStatusCode(httpStatusCode);
    } else if (e instanceof NetException) {
      NetException netException = (NetException) e;
      int statusCode = netException.getStatusCode();
      toastMessageByHttpStatusCode(statusCode);
    } else if (e instanceof IllegalStateException || e instanceof JsonParseException) {
      ToastUtils.error(R.string.status_parse_error);
    } else if (e instanceof SocketTimeoutException) {
      ToastUtils.error(R.string.status_network_timeout);
    } else {
      ToastUtils.error(R.string.status_unknow_error);
    }
  }

  /**
   * @param response - Base response.
   * @param isShowToast - 异常发生的Activity
   * @return NetError - 这个错误已经被拦截，不需要再处理; false - 可以在统一错误提示的前提下再做相应处理。
   */
  public static NetError handleSystemException(BaseResponse response, boolean isShowToast) {
    if (response == null) {
      return null;
    }
    NetError error = NetError.createError(response);
    if (isShowToast) {
      ToastUtils.info(error.errorMsg);
    }
    return error;
  }

  public static void onError(BmbPresenter bmbPresenter, boolean showErrorView, Throwable e) {
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
  }
}
