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

public class ErrorView extends RelativeLayout
        implements IKeepPublicFieldName, IKeepPublicMethodName {

    private final static String TAG = ErrorView.class.getSimpleName();

    private TextView mErrorMsgView;

    public ErrorView(Context context) {
        super(context);
        setupViews();
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupViews();
    }

    private void setupViews() {
        inflate(getContext(), R.layout.error_view, this);
        mErrorMsgView = (TextView) findViewById(R.id.error_msg);
    }

    public enum Style {
        ERROR_NETWORK, EMPTY
    }

    public void show(CharSequence errorString, Style style) {
        show(errorString, true, null, style);
    }

    public void show(CharSequence errorString, boolean animate, Style style) {
        show(errorString, animate, null, style);
    }

    public void show(CharSequence errorString, boolean animate, View contentContainer, Style style) {
        if (animate && getVisibility() == View.GONE) { // Visible判断防止闪烁该View
            startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
            if (contentContainer != null) {
                contentContainer.startAnimation(
                        AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
            }
        } else {
            clearAnimation();
            if (contentContainer != null) {
                contentContainer.clearAnimation();
            }
        }
        setVisibility(View.VISIBLE);
        mErrorMsgView.setText(errorString);
        if (contentContainer != null) {
            contentContainer.setVisibility(View.GONE);
        }
        switch (style) {
            case ERROR_NETWORK:
                break;
            case EMPTY:
                break;
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
            startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
            if (contentContainer != null) {
                contentContainer.startAnimation(
                        AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
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

    public void setTextColor(int color) {
        mErrorMsgView.setText(color);
    }

    public void setTextSize(float size) {
        mErrorMsgView.setTextSize(size);
    }

    public void setTextSize(int unit, float size) {
        mErrorMsgView.setTextSize(unit, size);
    }
}