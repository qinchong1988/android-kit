package com.bmbstack.kit.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmbstack.kit.api.APIHandler;
import com.bmbstack.kit.api.NetError;
import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.widget.ErrorView;
import com.bmbstack.kit.widget.LoadingView;

import java.lang.reflect.Field;

public abstract class BaseFragment extends Fragment implements BmbPresenter {

    FragPresenter mDecorate = new FragPresenter(this);

    public boolean onBackPressed() {
        return false;
    }

    public boolean isValid() {
        return mDecorate.isValid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mDecorate.getContentView() != null) {
            if (mDecorate.getContentView().getParent() != null) {
                ((ViewGroup) mDecorate.getContentView().getParent()).removeView(mDecorate.getContentView());
            }
        } else {
            setContentViewGroup((ViewGroup) inflater.inflate(getLayouId(), container, false));
            initCreateView(mDecorate.getContentView(), savedInstanceState);
        }
        return mDecorate.getContentView();
    }

    public void setContentViewGroup(ViewGroup viewGroup) {
        mDecorate.setContentViewGroup(viewGroup);
    }

    protected abstract void initCreateView(ViewGroup viewGroup, Bundle savedInstanceState);

    protected abstract int getLayouId();

    public BaseActivity getActivityContext() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.startActivityIfNeeded(intent, -1);
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.startActivityIfNeeded(intent, requestCode);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (mDecorate.getContentView() != null) {
            if (mDecorate.getContentView().getParent() != null) {
                ((ViewGroup) mDecorate.getContentView().getParent()).removeView(mDecorate.getContentView());
            }
            mDecorate.getContentView().removeAllViews();
        }
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

    public void onWindowFocusChanged(boolean hasFocus) {
    }

    public void onNewIntent(Intent intent) {

    }

    public void showEmptyView() {
        mDecorate.showEmptyView();
    }

    public void showErrorView(Throwable e) {
        NetError error = APIHandler.getErrorFromException(e);
        showErrorView(error.errorMsg, error.style);
    }
}