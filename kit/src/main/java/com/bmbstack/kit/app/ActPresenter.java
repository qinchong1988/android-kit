package com.bmbstack.kit.app;

import android.support.v4.app.FragmentActivity;
import android.view.View;

public class ActPresenter extends ActFragPresenter {

  private boolean mDestory = false;
  BaseActivity mActivity;

  ActPresenter(BaseActivity activity) {
    mActivity = activity;
  }

  @Override public boolean isValid() {
    return !mDestory && !getActivityContext().isFinishing();
  }

  @Override public void onClickOfErrorView(View v) {
    mActivity.onClickOfErrorView(v);
  }

  @Override public FragmentActivity getActivityContext() {
    return mActivity;
  }

  public void onDestory() {
    mDestory = true;
  }
}
