package com.bmbstack.kit.app;

import android.app.Activity;
import android.view.View;

import com.bmbstack.kit.log.Logger;

public class FragPresenter extends ActFragPresenter {

  BaseFragment mFragment;

  FragPresenter(BaseFragment fragment) {
    mFragment = fragment;
  }

  @Override public boolean isValid() {
    return mFragment.isAdded()
        && !mFragment.isDetached()
        && mFragment.getActivity() != null
        && !mFragment.getActivity().isFinishing();
  }

  @Override public void onClickOfErrorView(View v) {
    Logger.v("onClickOfErrorView");
    mFragment.onClickOfErrorView(v);
  }

  @Override public Activity getActivityContext() {
    return mFragment.getActivity();
  }
}
