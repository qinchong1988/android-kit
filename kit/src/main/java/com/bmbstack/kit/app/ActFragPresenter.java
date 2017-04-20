package com.bmbstack.kit.app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bmbstack.kit.R;
import com.bmbstack.kit.api.ExceptionHandler;
import com.bmbstack.kit.api.NetError;
import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.widget.ErrorView;
import com.bmbstack.kit.widget.LoadingView;

public abstract class ActFragPresenter implements BmbPresenter {

    private ViewGroup.LayoutParams mLoadingViewLayoutParams;
    private ViewGroup.LayoutParams mErrorViewLayoutParams;
    private boolean mLoadingViewClickable;
    private LoadingView mLoadingView;
    private ErrorView mErrorView;
    private ViewGroup mViewGroup;
    private boolean mLoading;

    public void setContentViewGroup(ViewGroup viewGroup) {
        mViewGroup = viewGroup;
    }

    public ViewGroup getContentView() {
        return mViewGroup;
    }

    public void setLoadingViewClickable(boolean clickable) {
        mLoadingViewClickable = clickable;
    }

    public abstract boolean isValid();

    public boolean isLoading() {
        return mLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mLoading = isLoading;
    }

    protected void addLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = new LoadingView(getActivityContext());
            mLoadingView.setVisibility(View.GONE);
            if (mLoadingViewClickable) {
                mLoadingView.setClickable(true);
            }
            ViewGroup parent = (ViewGroup) mLoadingView.getParent();
            if (parent != mViewGroup) {
                if (parent != null) {
                    parent.removeView(mLoadingView);
                }
                mViewGroup.addView(mLoadingView);
            }
            if (mLoadingViewLayoutParams != null) {
                mLoadingView.setLayoutParams(mLoadingViewLayoutParams);
            }
        }
    }

    protected RelativeLayout.LayoutParams genLoadingErrorLayoutParams() {
        return genLoadingErrorLayoutParams(0);
    }

    // create match_parent below anchor
    protected RelativeLayout.LayoutParams genLoadingErrorLayoutParams(int anchor) {
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, anchor == 0 ? R.id.titlebar : anchor);
        return params;
    }

    public void setLoadingErrorRelaLayout() {
        setLoadingErrorRelaLayout(0);
    }

    public void setLoadingErrorRelaLayout(int anchor) {
        RelativeLayout.LayoutParams params = genLoadingErrorLayoutParams(anchor);
        setLoadingViewLayoutParams(params);
        setErrorViewLayoutParams(params);
    }

    public void setLoadingViewLayoutParams(ViewGroup.LayoutParams params) {
        mLoadingViewLayoutParams = params;
        if (mLoadingView != null) {
            mLoadingView.setLayoutParams(params);
        }
    }

    public void setErrorViewLayoutParams(ViewGroup.LayoutParams params) {
        mErrorViewLayoutParams = params;
        if (mErrorView != null) {
            mErrorView.setLayoutParams(params);
        }
    }

    private void addErrorView() {
        Logger.d("addErrorView: " + mErrorView);
        if (mErrorView == null) {
            mErrorView = new ErrorView(getActivityContext());
            mErrorView.setVisibility(View.GONE);
            ViewGroup parent = (ViewGroup) mErrorView.getParent();
            if (parent != mViewGroup) {
                if (parent != null) {
                    parent.removeView(mErrorView);
                }
                Logger.d("mViewGroup.addView...");
                mViewGroup.addView(mErrorView);
            }
            if (mErrorViewLayoutParams != null) {
                mErrorView.setLayoutParams(mErrorViewLayoutParams);
            }
            mErrorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickOfErrorView(v);
                }
            });
        }
    }

    @Override
    public void showErrorView(CharSequence errorString, ErrorView.Style style) {
        showErrorView(errorString, true, null, style);
    }

    @Override
    public void showErrorView(CharSequence errorString, View hideView, ErrorView.Style style) {
        showErrorView(errorString, true, hideView, style);
    }

    @Override
    public void showErrorView(CharSequence errorString, boolean animate, View hideView,
                              ErrorView.Style style) {
        addErrorView();
        mErrorView.show(errorString, animate, hideView, style);
    }

    @Override
    public void hideErrorView() {
        hideErrorView(null);
    }

    @Override
    public void hideErrorView(View showView) {
        hideErrorView(true, showView);
    }

    @Override
    public void hideErrorView(boolean animate, View showView) {
        if (mErrorView != null) {
            mErrorView.hide(animate, showView);
        }
    }

    @Override
    public void showLoadingView() {
        showLoadingView(null, true, null);
    }

    @Override
    public void showLoadingView(View hideView) {
        showLoadingView(null, true, hideView);
    }

    @Override
    public void showLoadingView(String msg, View hideView) {
        showLoadingView(msg, true, hideView);
    }

    @Override
    public void showLoadingView(String msg, boolean animate, View hideView) {
        addLoadingView();
        mLoadingView.show(msg, animate, hideView);
        setIsLoading(true);
    }

    @Override
    public void hideLoadingView() {
        hideLoadingView(true, null);
    }

    @Override
    public void hideLoadingView(View showView) {
        hideLoadingView(true, showView);
    }

    @Override
    public void hideLoadingView(boolean animate, View showView) {
        if (mLoadingView != null) {
            mLoadingView.hide(animate, showView);
        }
        setIsLoading(false);
    }

    @Override
    public LoadingView getLoadingView() {
        return mLoadingView;
    }

    @Override
    public ErrorView getErrorView() {
        return mErrorView;
    }

    @Override
    public abstract Activity getActivityContext();

    @Override
    public void showEmptyView() {
        showErrorView(getActivityContext().getString(R.string.empty_content), ErrorView.Style.EMPTY);
    }

    public void showErrorView(Throwable e) {
        NetError error = ExceptionHandler.getErrorFromException(e);
        showErrorView(error.errorMsg, error.style);
    }
}
