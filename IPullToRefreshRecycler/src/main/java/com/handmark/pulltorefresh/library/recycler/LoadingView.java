package com.handmark.pulltorefresh.library.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * 数据加载中旋转视图,代替ProgressBar解决在低配机器上的卡顿问题
 */
public class LoadingView extends ImageView {
    private static final int ANIMATION_TIME = 1000;

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    private RotateAnimation ra;

    /**
     * 当本视图或者任意父视图被隐藏时将触发此方法
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (v == GONE || v == INVISIBLE) {
                stopAnimation();
            } else {
                startAnimation();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (getVisibility() == GONE || getVisibility() == INVISIBLE) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    private void startAnimation() {
        //如果不可见则返回
        if (getVisibility() != View.VISIBLE || getDrawable() == null) return;
        if (ra == null) {
            ra = new RotateAnimation(0.0f, 360.0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(ANIMATION_TIME);
            ra.setRepeatCount(Animation.INFINITE);
            ra.setRepeatMode(Animation.RESTART);
            ra.setInterpolator(new LinearInterpolator());
        }
        startAnimation(ra);
    }

    private void stopAnimation() {
        clearAnimation();
    }

}
