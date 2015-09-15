package cn.pwrd.ipulltorefreshrecyclersample.sample.util;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cn.pwrd.ipulltorefreshrecyclersample.sample.R;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            R.attr.dividerDrawable, R.attr.dividerHeight, R.attr.dividerWidth
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;
    private Drawable mLastDivider;
    private int mHeight;
    private int mWidth;

    private int mOrientation;

    public DividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            mDivider = ViewUtil.getDrawable(context, R.drawable.recycler_divider);
        }

        mHeight = a.getDimensionPixelSize(1, mDivider.getIntrinsicHeight());
        mWidth = a.getDimensionPixelSize(1, mDivider.getIntrinsicWidth());

        a.recycle();
        setOrientation(orientation);
        mLastDivider = ViewUtil.getDrawable(context, android.R.color.transparent);
    }

    public DividerItemDecoration(Context context, int orientation, @DrawableRes int resId) {
        mDivider = ViewUtil.getDrawable(context, resId);
        if (mDivider == null) {
            mDivider = ViewUtil.getDrawable(context, R.drawable.recycler_divider);
        }

        mHeight = mDivider.getIntrinsicHeight();
        mWidth = mDivider.getIntrinsicWidth();

        setOrientation(orientation);
        mLastDivider = ViewUtil.getDrawable(context, android.R.color.transparent);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + mHeight;
            if (i == childCount - 1) {
                mLastDivider.setBounds(left, top, right, bottom);
                mLastDivider.draw(c);
            } else {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }


    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin +
                    Math.round(ViewCompat.getTranslationX(child));
            final int right = left + mWidth;
            if (i == childCount - 1) {
                mLastDivider.setBounds(left, top, right, bottom);
                mLastDivider.draw(c);
            } else {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mHeight);
        } else {
            outRect.set(0, 0, mWidth, 0);
        }
    }

    public DividerItemDecoration setWidth(int px) {
        mWidth = px;
        return this;
    }

    public DividerItemDecoration setHeight(int px) {
        mHeight = px;
        return this;
    }
}

