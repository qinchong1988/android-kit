package com.bmbstack.kit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmbstack.kit.R;
import com.bmbstack.kit.proguard.IKeepPublicFieldName;
import com.bmbstack.kit.proguard.IKeepPublicMethodName;

public class LoadingView extends RelativeLayout
    implements IKeepPublicFieldName, IKeepPublicMethodName {

  private TextView mLoadingMsgView;
  private String mLoadingMsg;

  public LoadingView(Context context) {
    super(context);
    setupViews();
  }

  public LoadingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setupViews();
  }

  public LoadingView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setupViews();
  }

  private void setupViews() {
    inflate(getContext(), R.layout.loading_view, this);
    mLoadingMsgView = (TextView) findViewById(R.id.loading_msg);
    mLoadingMsg = getResources().getString(R.string.loading_view_isLoading);
  }

  public void show() {
    show(null, true, null);
  }

  public void show(String msg) {
    show(msg, true, null);
  }

  public void show(String msg, boolean animate) {
    show(msg, animate, null);
  }

  public void show(String msg, boolean animate, View contentContainer) {
    if (msg == null) {
      msg = mLoadingMsg;
    }
    if (animate && getVisibility() == View.GONE) { // 防止闪烁处理
      startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
      if (contentContainer != null && contentContainer.getVisibility() == View.VISIBLE) {
        contentContainer.startAnimation(
            AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
      }
    } else {
      clearAnimation();
      if (contentContainer != null) {
        contentContainer.clearAnimation();
      }
    }
    setVisibility(View.VISIBLE);
    if (contentContainer != null && contentContainer.getVisibility() == View.VISIBLE) {
      contentContainer.setVisibility(View.GONE);
    }
  }

  public void hide() {
    hide(true, null);
  }

  public void hide(boolean animate) {
    hide(true, null);
  }

  public void hide(boolean animate, View contentContainer) {
    if (animate && getVisibility() == View.VISIBLE) {
      startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
      if (contentContainer != null) {
        contentContainer.startAnimation(
            AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
      }
    } else {
      clearAnimation();
      if (contentContainer != null) {
        contentContainer.clearAnimation();
      }
    }
    setVisibility(View.GONE);
    if (contentContainer != null) {
      contentContainer.setVisibility(View.VISIBLE);
    }
  }

  public void setText(String msg) {
    mLoadingMsg = msg;
    mLoadingMsgView.setText(msg);
  }

  public void setText(int resId) {
    mLoadingMsg = getContext().getString(resId);
    mLoadingMsgView.setText(mLoadingMsg);
  }

  public void setTextColor(int color) {
    mLoadingMsgView.setText(color);
  }

  public void setTextSize(float size) {
    mLoadingMsgView.setTextSize(size);
  }

  public void setTextSize(int unit, float size) {
    mLoadingMsgView.setTextSize(unit, size);
  }
}