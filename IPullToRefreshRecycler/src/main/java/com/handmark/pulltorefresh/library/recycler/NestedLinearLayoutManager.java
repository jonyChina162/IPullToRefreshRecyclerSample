package com.handmark.pulltorefresh.library.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class NestedLinearLayoutManager extends LinearLayoutManager {
	private int[] mMeasuredDimension = new int[2];

	public NestedLinearLayoutManager(Context context) {
		super(context);
	}

	public NestedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
	}

	public NestedLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
		final int widthMode = View.MeasureSpec.getMode(widthSpec);
		final int heightMode = View.MeasureSpec.getMode(heightSpec);
		final int widthSize = View.MeasureSpec.getSize(widthSpec);
		final int heightSize = View.MeasureSpec.getSize(heightSpec);
		int width = 0;
		int height = 0;

		for (int i = 0; i < getItemCount(); i++) {
			measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);

			if (getOrientation() == HORIZONTAL) {
				width = width + mMeasuredDimension[0];
				if (i == 0) {
					height = mMeasuredDimension[1];
				}
			} else {
				height = height + mMeasuredDimension[1];
				if (i == 0) {
					width = mMeasuredDimension[0];
				}
			}
		}
		switch (widthMode) {
		case View.MeasureSpec.EXACTLY:
			width = widthSize;
			break;
		case View.MeasureSpec.AT_MOST:
		case View.MeasureSpec.UNSPECIFIED:
			break;
		}

		switch (heightMode) {
		case View.MeasureSpec.EXACTLY:
			height = heightSize;
			break;
		case View.MeasureSpec.UNSPECIFIED:
		case View.MeasureSpec.AT_MOST:
			break;
		}

		setMeasuredDimension(width, height);
	}

	private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec,
			int[] measuredDimension) {
		View view = null;
		try {
			view = recycler.getViewForPosition(position);
		} catch (IndexOutOfBoundsException e) {

		}
		if (view != null && view.getVisibility() == View.VISIBLE) {
			RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
			int childWidthSpec = ViewGroup
					.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width);
			int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(),
					p.height);
			view.measure(childWidthSpec, childHeightSpec);
			measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
			measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
			if (view.getParent() == null) {
				recycler.recycleView(view);
			}
		}
	}
}
