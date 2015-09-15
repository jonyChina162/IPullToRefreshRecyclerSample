/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class PullToRefreshListView extends PullToRefreshAdapterViewBase<ListView> {

	private LoadingLayout mHeaderLoadingView;
	private LoadingLayout mFooterLoadingView;

	private FrameLayout mLvFooterLoadingFrame;
	private FrameLayout mLoadMoreLayout;
	private Button mLoadMoreBtn;
	private ViewGroup mLoadingLayout;
	private TextView mNoMoreLayout;

	private boolean isNoMore;

	private boolean mListViewExtrasEnabled;

	private boolean hasAddClickFooter = false;
	private OnRefreshListener2<ListView> mRefreshListener;
	private Mode mRealMode;
	private static final String STATE_REAL_MODE = "ptr_real_mode";
	private boolean isFooterButtonMode = true;

	public PullToRefreshListView(Context context) {
		super(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshListView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected void onRefreshing(final boolean doScroll) {
		/**
		 * If we're not showing the Refreshing view, or the list is empty, the
		 * the header/footer views won't show so we use the normal method.
		 */
		ListAdapter adapter = mRefreshableView.getAdapter();
		if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
			super.onRefreshing(doScroll);
			return;
		}

		super.onRefreshing(false);

		final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
		final int selection, scrollToY;

		switch (getCurrentMode()) {
		case MANUAL_REFRESH_ONLY:
		case PULL_FROM_END:
			origLoadingView = getFooterLayout();
			listViewLoadingView = mFooterLoadingView;
			oppositeListViewLoadingView = mHeaderLoadingView;
			selection = mRefreshableView.getCount() - 1;
			scrollToY = getScrollY() - getFooterSize();
			break;
		case PULL_FROM_START:
		default:
			origLoadingView = getHeaderLayout();
			listViewLoadingView = mHeaderLoadingView;
			oppositeListViewLoadingView = mFooterLoadingView;
			selection = 0;
			scrollToY = getScrollY() + getHeaderSize();
			break;
		}

		// Hide our original Loading View
		origLoadingView.reset();
		origLoadingView.hideAllViews();

		// Make sure the opposite end is hidden too
		oppositeListViewLoadingView.setVisibility(View.GONE);

		// Show the ListView Loading View and set it to refresh.
		listViewLoadingView.setVisibility(View.VISIBLE);
		listViewLoadingView.refreshing();

		if (doScroll) {
			// We need to disable the automatic visibility changes for now
			disableLoadingLayoutVisibilityChanges();

			// We scroll slightly so that the ListView's header/footer is at the
			// same Y position as our normal header/footer
			setHeaderScroll(scrollToY);

			// Make sure the ListView is scrolled to show the loading
			// header/footer
			mRefreshableView.setSelection(selection);

			// Smooth scroll as normal
			smoothScrollTo(0);
		}
	}

	@Override
	protected void onReset() {
		/**
		 * If the extras are not enabled, just call up to super and return.
		 */
		if (!mListViewExtrasEnabled) {
			super.onReset();
			return;
		}

		final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
		final int scrollToHeight, selection;
		final boolean scrollLvToEdge;

		switch (getCurrentMode()) {
		case MANUAL_REFRESH_ONLY:
		case PULL_FROM_END:
			originalLoadingLayout = getFooterLayout();
			listViewLoadingLayout = mFooterLoadingView;
			selection = mRefreshableView.getCount() - 1;
			scrollToHeight = getFooterSize();
			scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
			break;
		case PULL_FROM_START:
		default:
			originalLoadingLayout = getHeaderLayout();
			listViewLoadingLayout = mHeaderLoadingView;
			scrollToHeight = -getHeaderSize();
			selection = 0;
			scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
			break;
		}

		// If the ListView header loading layout is showing, then we need to
		// flip so that the original one is showing instead
		if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

			// Set our Original View to Visible
			originalLoadingLayout.showInvisibleViews();

			// Hide the ListView Header/Footer
			listViewLoadingLayout.setVisibility(View.GONE);

			/**
			 * Scroll so the View is at the same Y as the ListView
			 * header/footer, but only scroll if: we've pulled to refresh, it's
			 * positioned correctly
			 */
			if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
				mRefreshableView.setSelection(selection);
				setHeaderScroll(scrollToHeight);
			}
		}

		// Finally, call up to super
		super.onReset();
	}

	@Override
	protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
		LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

		if (mListViewExtrasEnabled) {
			final Mode mode = getMode();

			if (includeStart && mode.showHeaderLoadingLayout()) {
				proxy.addLayout(mHeaderLoadingView);
			}
			if (includeEnd && mode.showFooterLoadingLayout()) {
				proxy.addLayout(mFooterLoadingView);
			}
		}

		return proxy;
	}

	protected ListView createListView(Context context, AttributeSet attrs) {
		final ListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			lv = new InternalListViewSDK9(context, attrs);
		} else {
			lv = new InternalListView(context, attrs);
		}
		return lv;
	}

	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView lv = createListView(context, attrs);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		super.handleStyledAttributes(a);

		mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

		if (mListViewExtrasEnabled) {
			final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

			// Create Loading Views ready for use later
			FrameLayout frame = new FrameLayout(getContext());
			mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
			mHeaderLoadingView.setVisibility(View.GONE);
			frame.addView(mHeaderLoadingView, lp);
			mRefreshableView.addHeaderView(frame, null, false);

			mLvFooterLoadingFrame = new FrameLayout(getContext());
			mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
			mFooterLoadingView.setVisibility(View.GONE);
			mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

			mLoadMoreLayout = new FrameLayout(getContext());
			LayoutInflater.from(getContext()).inflate(R.layout.listview_load_more_foot, mLoadMoreLayout);
			mLoadingLayout = (ViewGroup) mLoadMoreLayout.findViewById(R.id.progress_layout);
			mNoMoreLayout = (TextView) mLoadMoreLayout.findViewById(R.id.no_more_layout);
			mLoadMoreBtn = (Button) mLoadMoreLayout.findViewById(R.id.load_more_btn);
			mLoadMoreBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isRefreshing()) {
						Toast.makeText(getContext(), "正在刷新中...", Toast.LENGTH_SHORT).show();
						return;
					}
					setTemporaryMode(Mode.DISABLED, "正在加载下一页...");
					mLoadingLayout.setVisibility(VISIBLE);
					mLoadMoreBtn.setVisibility(INVISIBLE);
					if (mRefreshListener != null) {
						mRefreshListener.onPullUpToRefresh(PullToRefreshListView.this);
					}
				}
			});
			/**
			 * If the value for Scrolling While Refreshing hasn't been
			 * explicitly set via XML, enable Scrolling While Refreshing.
			 */
			if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
				setScrollingWhileRefreshingEnabled(true);
			}
		}
	}

	@TargetApi(9)
	final class InternalListViewSDK9 extends InternalListView {

		public InternalListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			// OverscrollHelper.overScrollBy(PullToRefreshListView.this, deltaX,
			// scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

	protected class InternalListView extends ListView implements EmptyViewMethodAccessor {

		private boolean mAddedLvFooter = false;

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				super.dispatchDraw(canvas);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				return super.dispatchTouchEvent(ev);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void setAdapter(ListAdapter adapter) {
			// Add the Footer View at the last possible moment
			if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
				addFooterView(mLvFooterLoadingFrame, null, false);
				mAddedLvFooter = true;
			}

			super.setAdapter(adapter);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

	}

	@Override
	public void setMode(Mode mode) {
		if (isFooterButtonMode) {
			mRealMode = mode;
			switch (mode) {
			case BOTH:
				addLoadMoreView();
				mode = Mode.PULL_FROM_START;
				break;
			case PULL_FROM_END:
				addLoadMoreView();
				mode = Mode.DISABLED;
				break;
			default:
				if (hasAddClickFooter) {
					getRefreshableView().removeFooterView(mLoadMoreLayout);
					hasAddClickFooter = false;
				}
				break;
			}
		}
		super.setMode(mode);
	}

	private void addLoadMoreView() {
		ListView lv = getRefreshableView();
		if (!hasAddClickFooter) {
			lv.addFooterView(mLoadMoreLayout);
			hasAddClickFooter = true;
		}
	}

	@Override
	public void setOnRefreshListener(OnRefreshListener2<ListView> listener) {
		if (isFooterButtonMode) {
			this.mRefreshListener = listener;
		}
		super.setOnRefreshListener(listener);
	}

	@Override
	public void onRefreshComplete() {
		if (isFooterButtonMode) {
			setTemporaryMode(Mode.BOTH, null);
			if (!isNoMore) {
				mLoadMoreBtn.setVisibility(VISIBLE);
			}
			mLoadingLayout.setVisibility(INVISIBLE);
		}
		super.onRefreshComplete();
	}

	public void setNoMore(boolean isNoMore) {
		if (isFooterButtonMode) {
			this.isNoMore = isNoMore;
			mNoMoreLayout.setVisibility(isNoMore ? VISIBLE : INVISIBLE);
			mLoadMoreBtn.setVisibility(isNoMore ? INVISIBLE : VISIBLE);
		}
	}

	@Override
	protected void onPtrSaveInstanceState(Bundle saveState) {
		super.onPtrSaveInstanceState(saveState);
		if (isFooterButtonMode) {
			if (mRealMode != null) {
				saveState.putInt(STATE_REAL_MODE, mRealMode.getIntValue());
			}
		}
	}

	@Override
	protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
		super.onPtrRestoreInstanceState(savedInstanceState);
		if (isFooterButtonMode) {
			int modeInt = savedInstanceState.getInt(STATE_REAL_MODE, -1);
			if (modeInt >= 0) {
				setMode(Mode.mapIntToValue(modeInt));
			}
		}
	}

	@Override
	public void setRefreshing() {
        getRefreshableView().post(new Runnable() {
            @Override
            public void run() {
                if (isFooterButtonMode) {
                    getRefreshableView().setSelection(0);
                }
                PullToRefreshListView.super.setRefreshing();
            }
        });
    }

	public void setFooterButtonMode(boolean isFooterButtonMode) {
		this.isFooterButtonMode = isFooterButtonMode;
	}
}