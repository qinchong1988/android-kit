package com.bmbstack.kit.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmbstack.kit.R;
import com.bmbstack.kit.log.Logger;

public class LoadMoreView extends RelativeLayout implements View.OnClickListener {

    private ProgressBar mCircularProgress;
    private TextView mLoadMoreText;
    private boolean mIsLoading = false;
    private OnClickListener mFootViewOnClick;
    private OnMyItemVisibleListener mLastItemVisibleListener;
    private FootViewState mState;

    public enum FootViewState {
        /******
         * 点击加载更多,默认初始化状态
         ********/
        CLICK_TO_LOADMORE, // 在Pad上一次展现的条目没有达到屏幕的高度，应该展示为该状态,本版本不适配Pad了改为正在载入

        /**
         * 正在加载
         */
        ISLOADING, //

        /**
         * 加载失败，点击重试
         */
        FAIL_TO_RELOAD, //

        /**
         * 无更多内容
         */
        NO_MORE, //

        /**
         * 只是在底部显示正在加载，不会记录状态
         */
        VIEW_STATE_ISLOADING // 供视图体验的效果,刷新或加载更多之后设置
    }

    public LoadMoreView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(getContext(), R.layout.list_load_more_footview, this);
        mCircularProgress = (ProgressBar) findViewById(R.id.load_more_progress_view);
        mLoadMoreText = (TextView) findViewById(R.id.list_loadmore_state_tv);
        changeState(FootViewState.CLICK_TO_LOADMORE);
        setOnClickListener(this);
        hideFootView(); // List没有数据的时候不让其显示
    }

    public void hideFootView() {
        setVisibility(View.INVISIBLE);
    }

    public void setFootOnClickListener(OnClickListener footListener) {
        mFootViewOnClick = footListener;
    }

    public void setOnLastItemVisibleListener(OnMyItemVisibleListener lastItemVisibleListener) {
        mLastItemVisibleListener = lastItemVisibleListener;
    }

    private void hideProgressBar() {
        mCircularProgress.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        mCircularProgress.setVisibility(View.VISIBLE);
        mLoadMoreText.setText(R.string.loading_view_isLoading);
    }

    private void hideProgressBar(int stateText) {
        hideProgressBar();
        mLoadMoreText.setText(stateText);
        mIsLoading = false;
    }

    public boolean isLoadingMore() {
        return mIsLoading;
    }

    public void addAttachRecyclerView(RecyclerView recycview,
                                      final LinearLayoutManager layoutManager) {
        recycview.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isNoMoreState()) {
                    Logger.v(" is no More state !!!!!!!,no need to load more");
                    return;
                }
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                // Logger.fv("LoadingMoreView", "lastVisibleItem =", lastVisibleItem, " , totalItemCount = ",
                // totalItemCount, " ,mIsLoading=", mIsLoading);
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                    if (!mIsLoading) {
                        changeState(FootViewState.ISLOADING);
                        if (mLastItemVisibleListener != null) {
                            mLastItemVisibleListener.onMyLastItemVisible(lastVisibleItem, totalItemCount);
                        }
                    }
                }
            }
        });
    }

    public void changeState(FootViewState state) {
        ensureVisible();
        switch (state) {
            case CLICK_TO_LOADMORE:
                hideProgressBar(R.string.click_to_loadmore);
                break;
            case ISLOADING:
                showProgressBar();
                mIsLoading = true;
                break;
            case FAIL_TO_RELOAD:
                hideProgressBar(R.string.fail_to_reload);
                break;
            case NO_MORE:
                hideProgressBar(R.string.no_more_items);
                break;
            case VIEW_STATE_ISLOADING:
                showProgressBar();
                mIsLoading = false;
                break;
            default:
                break;
        }
        mState = state;
    }

    private void ensureVisible() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
    }

    public boolean isNoMoreState() {
        return mState == FootViewState.NO_MORE;
    }

    public FootViewState getViewState() {
        return mState;
    }

    public interface OnMyItemVisibleListener {
        void onMyLastItemVisible(int lastVisibleItem, int totalItemCount);
    }

    @Override
    public void onClick(View v) {
        if (isNoMoreState()) {
            return;
        }
        if (mFootViewOnClick != null && !mIsLoading) {
            changeState(FootViewState.ISLOADING);
            mFootViewOnClick.onClick(v);
        }
    }
}