package com.bmbstack.kit.app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.bmbstack.kit.widget.ErrorView;
import com.bmbstack.kit.widget.LoadingView;

public interface BmbPresenter {

  void setContentViewGroup(ViewGroup viewGroup);

  void setLoadingViewClickable(boolean clickable);

  void setLoadingErrorRelaLayout();

  void setLoadingErrorRelaLayout(int anchor);

  void setLoadingViewLayoutParams(ViewGroup.LayoutParams params);

  void setErrorViewLayoutParams(ViewGroup.LayoutParams params);

  boolean isValid();

  void showErrorView(CharSequence errorString, ErrorView.Style style);

  void showErrorView(CharSequence errorString, View hideView, ErrorView.Style style);

  void showErrorView(CharSequence errorString, boolean animate, View hideView,
                     ErrorView.Style style);

  void hideErrorView();

  void hideErrorView(View showView);

  void hideErrorView(boolean animate, View showView);

  void onClickOfErrorView(View v);

  boolean isLoading();

  void setIsLoading(boolean isLoading);

  void showLoadingView();

  void showLoadingView(View hideView);

  void showLoadingView(String msg, View hideView);

  void showLoadingView(String msg, boolean animate, View hideView);

  void hideLoadingView();

  void hideLoadingView(View showView);

  void hideLoadingView(boolean animate, View showView);

  LoadingView getLoadingView();

  ErrorView getErrorView();

  Activity getActivityContext();

  void showEmptyView();

  void showErrorView(Throwable e);

}
