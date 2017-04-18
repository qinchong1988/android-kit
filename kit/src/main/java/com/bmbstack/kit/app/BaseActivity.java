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

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

public abstract class BaseActivity extends AppCompatActivity implements BGASwipeBackHelper.Delegate, BmbPresenter {
  protected BGASwipeBackHelper mSwipeBackHelper;

  private ActPresenter mDecorate = new ActPresenter(this);

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityRecordMgr.getInstance().onCreate(this);
  }

  @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // setStatusBar();

    if (ActivityRecordMgr.getInstance().size() > 1) {
      initSwipeBackFinish();
    }
  }

  /**
   * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
   */
  private void initSwipeBackFinish() {
    mSwipeBackHelper = new BGASwipeBackHelper(this, this);
    // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackManager.getInstance().init(this) 来初始化滑动返回」
    // 下面几项可以不配置，这里只是为了讲述接口用法。
    // 设置滑动返回是否可用。默认值为 true
    mSwipeBackHelper.setSwipeBackEnable(true);
    // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
    mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
    // 设置是否是微信滑动返回样式。默认值为 true
    mSwipeBackHelper.setIsWeChatStyle(true);
    // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
    mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
    // 设置是否显示滑动返回的阴影效果。默认值为 true
    mSwipeBackHelper.setIsNeedShowShadow(true);
    // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
    mSwipeBackHelper.setIsShadowAlphaGradient(true);
    // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
    mSwipeBackHelper.setSwipeBackThreshold(0.3f);
  }

  @Override
  public boolean isSupportSwipeBack() {
    return true;
  }

  /**
   * 正在滑动返回
   *
   * @param slideOffset 从 0 到 1
   */
  @Override
  public void onSwipeBackLayoutSlide(float slideOffset) {
  }

  /**
   * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
   */
  @Override
  public void onSwipeBackLayoutCancel() {
  }

  /**
   * 滑动返回执行完毕，销毁当前 Activity
   */
  @Override
  public void onSwipeBackLayoutExecuted() {
    mSwipeBackHelper.swipeBackward();
  }

  @Override
  public void onBackPressed() {
    // 正在滑动返回的时候取消返回按钮事件
    if (mSwipeBackHelper.isSliding()) {
      return;
    }
    mSwipeBackHelper.backward();
  }

  protected void setStatusBar() {
    Logger.v("onPost Create setStatusBar setColorNoTranslucent");
    StatusBarUtil.setColorNoTranslucent(this, getStatusBarColor());
  }

  protected int getStatusBarColor() {
    return getPrimaryColor();
  }

  public int getBlackColor() {
    return getResources().getColor(android.R.color.black);
  }

  protected int getPrimaryColor() {
    return getResources().getColor(R.color.colorPrimary);
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
