package com.handmark.pulltorefresh.library.recycler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.R;
import com.handmark.pulltorefresh.library.recycler.animators.internal.ViewHelper;

import java.util.List;

public class PullToRefreshVerticalRecycler<T> extends PullToRefreshBase<RecyclerView> {
	private VerticalPullRecyclerView<T> mRecyclerView;
	private LoadMoreMode mMode = LoadMoreMode.INNER_REFRESH;
	// 是否允许加载更多
	private boolean mHasNoMore = false;
	private View mLoadMoreLayout; // 正在刷新状态的View
	private ImageView mLoadMoreImg;
	private TextView mLoadMoreTxt;
	private int mLoadMoreHeight;
	private Drawable mLoadingDrawable;
	private String mLoadMoreStr;

	public PullToRefreshVerticalRecycler(Context context) {
		super(context);
		init(context);
	}

	public PullToRefreshVerticalRecycler(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullToRefreshVerticalRecycler(Context context, Mode mode) {
		super(context, mode);
		init(context);
	}

	public PullToRefreshVerticalRecycler(Context context, Mode mode, AnimationStyle animStyle) {
		super(context, mode, animStyle);
		init(context);
	}

	private void init(Context context) {
		mLoadMoreLayout = LayoutInflater.from(context).inflate(R.layout.layout_load_more, this, false);
		mLoadMoreImg = (LoadingView) mLoadMoreLayout.findViewById(R.id.load_more_img);
		mLoadMoreTxt = (TextView) mLoadMoreLayout.findViewById(R.id.load_more_txt);
		mLoadingDrawable = mLoadMoreImg.getDrawable();
		mLoadMoreStr = mLoadMoreTxt.getText().toString();
	}

	@Override
	public Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
		if (mRecyclerView == null) {
			mRecyclerView = new VerticalPullRecyclerView<>(context, this, attrs);

		}
		return mRecyclerView;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		return mMode == LoadMoreMode.PULL && isLastItemVisible();
	}

	@Override
	protected boolean isReadyForPullStart() {
		return isFirstItemVisible();
	}

	private boolean isFirstItemVisible() {
		return mRecyclerView.isFirstViewVisible();
	}

	private boolean isLastItemVisible() {
		return mRecyclerView.isLastItemVisible();
	}

	/**
	 * 设置recyclerView的adapter和layoutManager，保证adapter在layoutManager前设置
	 * 
	 * @param adapter
	 * @param manager
	 */
	public void setAdapterAndLayoutManager(RecyclerView.Adapter adapter, RecyclerView.LayoutManager manager) {
		mRecyclerView.setAdapterAndLayoutManager(adapter, manager);
	}

	public void setOnRefreshListener(final OnRefreshListener2<RecyclerView> listener,
			final LoadMoreListener<T> loadMoreListener) {
		setOnRefreshListener(listener);
		mRecyclerView.setLoadDataListener(loadMoreListener);
	}

	/**
	 * 设置刷新接口时，保证下拉刷新都会回调reset
	 * 
	 * @param listener
	 */
	@Override
	public void setOnRefreshListener(final OnRefreshListener2<RecyclerView> listener) {
		super.setOnRefreshListener(new OnRefreshListener2<RecyclerView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
				reset();
				listener.onPullDownToRefresh(refreshView);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
				listener.onPullUpToRefresh(refreshView);
			}
		});
	}

	private void reset() {
		mLoadMoreImg.setVisibility(VISIBLE);
		mLoadMoreTxt.setText(mLoadMoreStr);
		mHasNoMore = false;
	}

	/**
	 * 设置是否存在更多
	 * 
	 * @param b
	 */
	public void setHasNoMore(boolean b) {
		mHasNoMore = b;
	}

	/**
	 * 设置刷新模式，PULL表示原有拉动刷新模式，INNER_REFRESH表示滑动到底部自动刷新模式
	 * 
	 * @param mode
	 */
	public void setLoadMoreMode(LoadMoreMode mode) {
		mMode = mode;
		if (mMode == LoadMoreMode.PULL) {
			removeView(mLoadMoreLayout);
			super.setMode(Mode.BOTH);
		} else {
			super.setMode(Mode.PULL_FROM_START);
		}
	}

	public LoadMoreMode getLoadMoreMode() {
		return mMode;
	}

	@Override
	public void setMode(Mode mode) {
		throw new UnsupportedOperationException("can not set mode here, please refer setLoadMode ");
	}

	/**
	 * 设置刷新drawable
	 * 
	 * @param drawable
	 */
	public void setLoadMoreDrawable(Drawable drawable) {
		mLoadMoreImg.setImageDrawable(drawable);
		super.removeView(mLoadMoreLayout);
	}

	/**
	 * 设置刷新文字
	 * 
	 * @param txt
	 */
	public void setLoadMoreText(String txt) {
		mLoadMoreStr = txt;
	}

	/**
	 * 展示底部刷新layout，一般不需要主动调用，通常由 {@link VerticalPullRecyclerView} 调用
	 * 
	 * @return
	 */
	public boolean showFoot() {
		if (!mHasNoMore) {
			checkAndAddLoadMoreLayout();
			mLoadMoreLayout.setVisibility(VISIBLE);
			smoothScrollTo((int) (mLoadMoreHeight * 0.5));
		}
		return !mHasNoMore;
	}

	private void checkAndAddLoadMoreLayout() {
		if (indexOfChild(mLoadMoreLayout) >= 0) {
			return;
		}
		int dHeight = mLoadingDrawable.getIntrinsicHeight() + mLoadMoreImg.getPaddingTop()
				+ mLoadMoreImg.getPaddingBottom() + 5;
		int tHeight = (int) (mLoadMoreTxt.getTextSize() + 0.5f)
				+ ((MarginLayoutParams) mLoadMoreTxt.getLayoutParams()).topMargin
				+ ((MarginLayoutParams) mLoadMoreTxt.getLayoutParams()).bottomMargin + 5;
		mLoadMoreHeight = (dHeight > tHeight) ? dHeight : tHeight + mLoadMoreLayout.getPaddingTop()
				+ mLoadMoreLayout.getPaddingBottom();
		addViewInternal(mLoadMoreLayout, getChildCount(), new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, mLoadMoreHeight));
	}

	/**
	 * 隐藏底部刷新layout，一般不需要主动调用，通常由 {@link VerticalPullRecyclerView} 调用
	 */
	public void hideFoot() {
		hideFoot(false);
	}

	public void hideFoot(boolean hasNoMore) {
		if (mLoadMoreLayout.getVisibility() == VISIBLE) {
			if (hasNoMore) {
				mLoadMoreTxt.setText(R.string.refresh_no_more);
				mLoadMoreImg.setVisibility(GONE);
				ViewCompat.animate(mLoadMoreLayout).translationY((int) (mLoadMoreHeight * 0.8)).setDuration(800)
						.setListener(new ViewPropertyAnimatorListener() {
							@Override
							public void onAnimationStart(View view) {

							}

							@Override
							public void onAnimationEnd(View view) {
								mLoadMoreLayout.setVisibility(GONE);
								smoothScrollTo(0);
								ViewHelper.clear(view);
							}

							@Override
							public void onAnimationCancel(View view) {
							}
						}).start();

			} else {
				mLoadMoreTxt.setText(mLoadMoreStr);
				mLoadMoreLayout.setVisibility(GONE);
				smoothScrollTo(0);
			}

		}

		mHasNoMore = hasNoMore;
	}

	public enum LoadMoreMode {
		INNER_REFRESH, PULL
	}

	/**
	 * 增加数据
	 * 
	 * @param list
	 */
	public void addList(List<T> list) {
		mRecyclerView.addList(list);
	}

	/**
	 * 清空数据
	 */
	public void clear() {
		mRecyclerView.clear();
	}

	/**
	 * 设置动画
	 * 
	 * @param animator
	 */
	public void setAnimator(@NonNull RecyclerView.ItemAnimator animator) {
		mRecyclerView.setAnimator(animator);
	}

	/**
	 * 设置item的动画时间
	 * 
	 * @param duration
	 */
	public void setAniDuration(long duration) {
		mRecyclerView.setAniDuration(duration);
	}

	/**
	 * 设置item在增加或删除的时候是否展示动画
	 * 
	 * @param showAnimation
	 */
	public void setShowAnimation(boolean showAnimation) {
		mRecyclerView.setShowAnimation(showAnimation);
	}

	/**
	 * 设置item是否只显示一次动画
	 * 
	 * @param b
	 */
	public void setAlwaysShowAni(boolean b) {
		mRecyclerView.setAlwaysShowAni(b);
	}

	/**
	 * 如果item只显示一次，设置该值后，只有position在此之后的才能显示
	 * 
	 * @param lastDisAniPosition
	 */
	public void setLastDisAniPosition(int lastDisAniPosition) {
		mRecyclerView.setLastDisAniPosition(lastDisAniPosition);
	}

	/**
	 * 是否可移动
	 * 
	 * @param canMove
	 */
	public void setCanMove(boolean canMove) {
		mRecyclerView.setCanMove(canMove);
	}

	/**
	 * 是否可滑动删除一个item
	 * 
	 * @param canSwipe
	 */
	public void setCanSwipe(boolean canSwipe) {
		mRecyclerView.setCanSwipe(canSwipe);
	}
}
