package com.handmark.pulltorefresh.library.recycler;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.handmark.pulltorefresh.library.recycler.animators.AnimatorUtil;
import com.handmark.pulltorefresh.library.recycler.animators.internal.ViewHelper;
import com.handmark.pulltorefresh.library.recycler.util.LogUtils;

import java.util.List;

class VerticalPullRecyclerView<T> extends RecyclerView {
	private PullToRefreshVerticalRecycler mParent;
	private AnimatorWrapAdapter mAdapter;
	private Context mContext;

	private boolean mIsLoadMore = false; // 是否正在加载数据

	private LoadMoreListener<T> mLoadMoreListener;

	private ItemAnimator mAnimator = AnimatorUtil.getAnimator("ScaleInLeft");
	private long mAniDuration = 300;
	private boolean mShowAni = true, mAlwaysShowAni;
	private int mLastDisAniPosition = 5;
	private HorizontalItemHelperCallBack mHorizontalItemHelperCallBack;
	private boolean mCanSwipe;
	private boolean mCanMove;

	private class LoadMoreTask extends AsyncTask<Void, Void, List<T>> {
		@Override
		protected List<T> doInBackground(Void... params) {
			List<T> result = null;
			try {
				result = mLoadMoreListener.getMoreDataList();
			} catch (Exception e) {
				LogUtils.e("VerticalFootRecyclerView", e);
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<T> result) {
			if (result != null && result.size() != 0) {
				addList(result);
				mParent.hideFoot(false);
			} else {
				mParent.hideFoot(true);
			}
			mIsLoadMore = false;
		}
	}

	public VerticalPullRecyclerView(Context context, PullToRefreshVerticalRecycler parent, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mParent = parent;
		init();
	}

	private void init() {
		setOverScrollMode(OVER_SCROLL_NEVER);
		mAnimator.setAddDuration(mAniDuration);
		mAnimator.setRemoveDuration(mAniDuration);
		setItemAnimator(mAnimator);
	}

	/**
	 * 加载更多数据完成后调用
	 */
	void loadMoreComplete() {
		mIsLoadMore = false;
		mParent.hideFoot();
	}

	private void setWrapAdapter(Adapter adapter) {
		mAdapter = new AnimatorWrapAdapter(adapter);
		super.setAdapter(mAdapter);
		mHorizontalItemHelperCallBack = new HorizontalItemHelperCallBack(mContext, mAdapter);
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mHorizontalItemHelperCallBack);
		itemTouchHelper.attachToRecyclerView(this);
	}

	@Override
	public void onScrollStateChanged(int state) {
		super.onScrollStateChanged(state);
		// 当前不滚动，且不是正在刷新或加载数据
		if (mParent.getLoadMoreMode() == PullToRefreshVerticalRecycler.LoadMoreMode.INNER_REFRESH
				&& state == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreListener != null && !mIsLoadMore
				&& isLastItemVisible()) {
			if (mParent.showFoot()) {
				mIsLoadMore = true;
				new LoadMoreTask().execute();
			}
		}
	}

	boolean isFirstViewVisible() {
		if (null == mAdapter || mAdapter.getItemCount() == 0) {
			return true;
		}

		if (getFirstVisiblePosition() <= 1) {
			final View firstVisibleChild = getChildAt(0);
			if (firstVisibleChild != null) {
				return firstVisibleChild.getTop() >= getTop();
			}
		}

		return false;
	}

	private int getFirstVisiblePosition() {
		return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
	}

	boolean isLastItemVisible() {
		if (null == mAdapter || mAdapter.getItemCount() == 0) {
			return true;
		} else {
			LayoutManager layoutManager = getLayoutManager();
			final int lastItemPosition = mAdapter.getItemCount() - 1;
			int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

			if (lastVisibleItemPosition >= lastItemPosition) {
				final int childIndex = lastItemPosition - getFirstVisiblePosition();
				final View lastVisibleChild = getChildAt(childIndex);
				if (lastVisibleChild != null) {
					return lastVisibleChild.getBottom() <= getBottom();
				}
			}
		}

		return false;
	}

	class AnimatorWrapAdapter extends LoadMoreAdapter<ViewHolder, T> {
		private RecyclerView.Adapter adapter;
		private List<T> mContents;

		public AnimatorWrapAdapter(@NonNull RecyclerView.Adapter adapter) {
			this.adapter = adapter;
			if (adapter instanceof LoadMoreAdapter) {
				mContents = ((LoadMoreAdapter) adapter).getContents();
			} else {
				mContents = getContents();
			}
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return adapter.onCreateViewHolder(parent, viewType);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
			if (position >= 0 && position < getItemCount()) {
				adapter.onBindViewHolder(holder, position);
			}

			if (mShowAni && (mAlwaysShowAni || position > mLastDisAniPosition)) {
				mAnimator.animateAdd(holder);
				mAnimator.runPendingAnimations();
				mLastDisAniPosition = position;
			} else {
				ViewHelper.clear(holder.itemView);
			}
		}

		@Override
		public int getItemCount() {
			return adapter.getItemCount();
		}

		@Override
		public long getItemId(int position) {
			return adapter.getItemId(position);
		}

		@Override
		public void addList(List<T> list) {
			int position = getItemCount();
			mContents.addAll(list);
			notifyItemRangeInserted(position, list.size() - 1);
			scrollToPosition(position);
		}

		public void clear() {
			mContents.clear();
			notifyItemRangeRemoved(0, getItemCount() - 1);
		}

		@Override
		public boolean onItemMove(int fromPosition, int toPosition) {
			boolean b = (adapter instanceof ItemTouchHelperInterface)
					&& (((ItemTouchHelperInterface) adapter).onItemMove(fromPosition, toPosition));
			if (b) {
				notifyItemMoved(fromPosition, toPosition);
			}
			return b;
		}

		@Override
		public boolean onItemDismiss(int position) {
			boolean b = (adapter instanceof ItemTouchHelperInterface)
					&& (((ItemTouchHelperInterface) adapter).onItemDismiss(position));
			if (b) {
				notifyItemRemoved(position);
			}
			return b;
		}
	}

	void setAdapterAndLayoutManager(@NonNull Adapter adapter, LayoutManager manager) {
		setAdapter(adapter);
		setLayoutManager(manager);
	}

	@Override
	public void setAdapter(@NonNull Adapter adapter) {
		setWrapAdapter(adapter);
	}

	/**
	 * this method should invoke after setAdapter
	 *
	 * @param manager
	 */
	@Override
	public void setLayoutManager(LayoutManager manager) {
		if (mAdapter == null) {
			throw new IllegalStateException("adapter must be set first");
		}

		super.setLayoutManager(manager);

		if (manager instanceof GridLayoutManager) {
			layoutGridAttach((GridLayoutManager) manager);
		}
	}

	/**
	 * 给GridLayoutManager附加脚部和滑动过度监听
	 *
	 * @param manager
	 */
	private void layoutGridAttach(final GridLayoutManager manager) {
		// GridView布局
		manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return (mAdapter.getItemCount() == position - 1) ? manager.getSpanCount() : 1;
			}
		});
		requestLayout();
	}

	/**
	 * 设置加载更多数据的监听
	 *
	 * @param listener
	 */
	public void setLoadDataListener(LoadMoreListener<T> listener) {
		mLoadMoreListener = listener;
	}

	void addList(List<T> list) {
		mAdapter.addList(list);
	}

	void clear() {
		mAdapter.clear();
		mLastDisAniPosition = 5;
	}

	void setAnimator(@NonNull ItemAnimator animator) {
		mAnimator = animator;
		mAnimator.setAddDuration(mAniDuration);
		mAnimator.setRemoveDuration(mAniDuration);
		setItemAnimator(mAnimator);
	}

	void setAniDuration(long duration) {
		mAniDuration = duration;
		mAnimator.setAddDuration(duration);
		mAnimator.setRemoveDuration(duration);
	}

	void setShowAnimation(boolean showAnimation) {
		mShowAni = showAnimation;
	}

	void setAlwaysShowAni(boolean b) {
		mAlwaysShowAni = b;
	}

	void setLastDisAniPosition(int lastDisAniPosition) {
		mLastDisAniPosition = lastDisAniPosition;
	}

	void setCanSwipe(boolean canSwipe) {
		mHorizontalItemHelperCallBack.setCanSwipe(canSwipe);
	}

	void setCanMove(boolean canMove) {
		mHorizontalItemHelperCallBack.setCanMove(canMove);
	}
}
