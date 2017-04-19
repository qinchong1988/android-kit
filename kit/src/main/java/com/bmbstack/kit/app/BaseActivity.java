package com.bmbstack.kit.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.bmbstack.kit.R;
import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.umeng.UmengUtils;
import com.bmbstack.kit.widget.ErrorView;
import com.bmbstack.kit.widget.LoadingView;
import com.jaeger.library.StatusBarUtil;


public abstract class BaseActivity extends AppCompatActivity implements BmbPresenter {

  private ActPresenter mDecorate = new ActPresenter(this);

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityRecordMgr.getInstance().onCreate(this);
  }

  @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    setStatusBar();
  }

  public void setStatusBar() {
    Logger.v("onPost Create setStatusBar setColorNoTranslucent");
    StatusBarUtil.setColorNoTranslucent(this, getStatusBarColor());
  }

  protected int getStatusBarColor() {
    return getColorPrimaryDark();
  }

  protected int getColorPrimaryDark() {
    return getResources().getColor(R.color.colorPrimaryDark);
  }

  public FragmentActivity getActivityContext() {
    return mDecorate.getActivityContext();
  }

  @Override protected void onResume() {
    super.onResume();
    UmengUtils.analysisOnResume(this);
  }

  @Override protected void onPause() {
    super.onPause();
    UmengUtils.analysisOnPause(this);
  }

  /**
   * 兼容OPHONE
   */
  @Override public void startActivity(Intent intent) {
    try {
      super.startActivity(intent);
    } catch (Exception e) {
      Logger.e("startActivity.error=" + e.toString());
      e.printStackTrace();
      try {
        startActivityIfNeeded(intent, -1);
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
  }

  /**
   * 兼容OPHONE
   */
  @Override public void startActivityForResult(Intent intent, int requestCode) {
    try {
      super.startActivityForResult(intent, requestCode);
    } catch (Exception e) {
      Logger.e("startActivity.error=" + e.toString());
      e.printStackTrace();
      try {
        startActivityIfNeeded(intent, -1);
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ActivityRecordMgr.getInstance().onDestroy(this);
    mDecorate.onDestory();
  }

  @Override public boolean isValid() {
    return mDecorate.isValid();
  }

  public void setContentViewGroup(ViewGroup viewGroup) {
    mDecorate.setContentViewGroup(viewGroup);
  }

  public void setLoadingViewClickable(boolean clickable) {
    mDecorate.setLoadingViewClickable(clickable);
  }

  public void setLoadingErrorRelaLayout() {
    mDecorate.setLoadingErrorRelaLayout(0);
  }

  public void setLoadingErrorRelaLayout(int anchor) {
    mDecorate.setLoadingErrorRelaLayout(anchor);
  }

  public void setLoadingViewLayoutParams(ViewGroup.LayoutParams params) {
    mDecorate.setLoadingViewLayoutParams(params);
  }

  public LoadingView getLoadingView() {
    return mDecorate.getLoadingView();
  }

  public boolean isLoading() {
    return mDecorate.isLoading();
  }

  public void setIsLoading(boolean isLoading) {
    mDecorate.setIsLoading(isLoading);
  }

  public void showLoadingView() {
    mDecorate.showLoadingView(null, true, null);
  }

  public void showLoadingView(View hideView) {
    mDecorate.showLoadingView(null, true, hideView);
  }

  public void showLoadingView(String msg, View hideView) {
    mDecorate.showLoadingView(msg, true, hideView);
  }

  public void showLoadingView(String msg, boolean animate, View hideView) {
    mDecorate.showLoadingView(msg, animate, hideView);
  }

  public void hideLoadingView() {
    mDecorate.hideLoadingView(true, null);
  }

  public void hideLoadingView(View showView) {
    mDecorate.hideLoadingView(true, showView);
  }

  public void hideLoadingView(boolean animate, View showView) {
    mDecorate.hideLoadingView(animate, showView);
  }

  public void setErrorViewLayoutParams(ViewGroup.LayoutParams params) {
    mDecorate.setErrorViewLayoutParams(params);
  }

  public void onClickOfErrorView(View v) {
    Logger.v("onClickOfErrorView");
    // Non't call mDecorate.onClickOfErrorView(), stackOver
  }

  public ErrorView getErrorView() {
    return mDecorate.getErrorView();
  }

  public void showErrorView(CharSequence errorString, ErrorView.Style style) {
    mDecorate.showErrorView(errorString, true, null, style);
  }

  public void showErrorView(CharSequence errorString, View hideView, ErrorView.Style style) {
    mDecorate.showErrorView(errorString, true, hideView, style);
  }

  public void showErrorView(CharSequence errorString, boolean animate, View hideView,
      ErrorView.Style style) {
    mDecorate.showErrorView(errorString, animate, hideView, style);
  }

  public void hideErrorView() {
    mDecorate.hideErrorView();
  }

  public void hideErrorView(View showView) {
    mDecorate.hideErrorView(showView);
  }

  public void hideErrorView(boolean animate, View showView) {
    mDecorate.hideErrorView(animate, showView);
  }

  public void showEmptyView() {
    mDecorate.showEmptyView();
  }

  public void showErrorView(Throwable e) {
    mDecorate.showErrorView(e);
  }
}
