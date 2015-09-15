package com.handmark.pulltorefresh.library.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextPaint;
import android.view.View;
import com.handmark.pulltorefresh.library.R;
import com.handmark.pulltorefresh.library.recycler.util.LayoutUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class HorizontalItemHelperCallBack extends ItemTouchHelper.Callback {
	private Context mContext;
	private final ItemTouchHelperInterface mAdapter;
	private boolean mCanSwipe;
	private boolean mCanMove;
	private float mSwipeThreshold = 0.4f;
	private float mMinAlphaPer = 0f;
	private @ColorInt int mRemoveBgColor;
	private @ColorInt int mRemoveTextColor;

	@IntDef(flag = true, value = { ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT, ItemTouchHelper.START,
			ItemTouchHelper.END })
	@Retention(RetentionPolicy.SOURCE)
	private @interface SwipeFlag {
	}

	@IntDef(flag = true, value = { ItemTouchHelper.UP, ItemTouchHelper.DOWN })
	@Retention(RetentionPolicy.SOURCE)
	private @interface DragFlag {
	}

	@SwipeFlag
	private int mSwipeFlags;
	@DragFlag
	private int mDragFlags;
	private int mTextMargin;
	private String mRemoveText = "删除";
	private int mTextSize;
	private TextPaint mTextPaint;
	private Paint mBgPaint;
	private float mTextWidth;

	public HorizontalItemHelperCallBack(Context context, ItemTouchHelperInterface mItemTouchHelperInterface) {
		this.mAdapter = mItemTouchHelperInterface;
		mContext = context;
		mRemoveBgColor = context.getResources().getColor(R.color.delete_layout_bg);
		mRemoveTextColor = context.getResources().getColor(R.color.delete_text_color);
		mTextMargin = LayoutUtil.GetPixelByDIP(context, 15);
		mTextSize = LayoutUtil.GetPixelBySP(context, 14);
		initTextPaint();
		mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBgPaint.setColor(mRemoveBgColor);
		mBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);

		mSwipeFlags = ItemTouchHelper.END;
		mDragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
	}

	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		return makeMovementFlags(mDragFlags, mSwipeFlags);
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
		return true;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
	}

	@Override
	public boolean isLongPressDragEnabled() {
		return mCanMove;
	}

	@Override
	public boolean isItemViewSwipeEnabled() {
		return mCanSwipe;
	}

	public void setCanSwipe(boolean mCanSwipe) {
		this.mCanSwipe = mCanSwipe;
	}

	public void setCanMove(boolean canMove) {
		this.mCanMove = canMove;
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX,
			float dY, int actionState, boolean isCurrentlyActive) {
		viewHolder.itemView.setAlpha(countAlpha(dX, recyclerView.getWidth()));
		getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
		onDrawRemove(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

	}

	@Override
	public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX,
			float dY, int actionState, boolean isCurrentlyActive) {
		viewHolder.itemView.setAlpha(countAlpha(dX, recyclerView.getWidth()));
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
		onDrawRemove(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}

	private void onDrawRemove(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX,
			float dY, int actionState, boolean isCurrentlyActive) {
		float a1 = countAlpha(dX, recyclerView.getWidth());
		mTextPaint.setAlpha((int) (a1 * 255));

		View view = viewHolder.itemView;
		int left, right, top = view.getTop(), bottom = view.getBottom();
		if (mSwipeFlags == ItemTouchHelper.START || mSwipeFlags == ItemTouchHelper.LEFT) {
			left = view.getRight();
			right = view.getRight() + view.getWidth();
		} else {
			left = view.getLeft() - view.getWidth();
			right = view.getLeft();
		}

		RectF rectf = new RectF(left, top, right, bottom);
		rectf.offset(dX, dY);

		c.drawRect(rectf, mBgPaint);

		float textY = (view.getHeight() - mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent) / 2
				+ dY + top;
		if (mSwipeFlags == ItemTouchHelper.START || mSwipeFlags == ItemTouchHelper.LEFT) {
			c.drawText(mRemoveText, left + mTextMargin + dX, textY, mTextPaint);
		} else {
			c.drawText(mRemoveText, right - mTextMargin + dX - mTextWidth, textY, mTextPaint);
		}
	}

	private float countAlpha(float x, int width) {
		if (x < 0) {
			x = -x;
		}
		if (x / width < mSwipeThreshold / 2) {
			return 1;
		}
		float a1 = 1 - x / width + mMinAlphaPer;
		if (a1 > 1) {
			a1 = 1;
		}
		return a1;
	}

	private void initTextPaint() {
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(mRemoveTextColor);
		mTextPaint.setTextSize(mTextSize);
		mTextWidth = mTextPaint.measureText(mRemoveText);
	}

	@Override
	public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
		return mSwipeThreshold;
	}

	public void setSwipeThreshold(float threshold) {
		mSwipeThreshold = threshold;
	}

	public void setRemoveBackColor(int mRemoveBackColor) {
		this.mRemoveBgColor = mRemoveBackColor;
	}

	public void setRemoveTextColor(int mRemoveTextColor) {
		this.mRemoveTextColor = mRemoveTextColor;
		mTextPaint.setColor(mRemoveTextColor);
	}

	public void setRemoveText(String mRemoveText) {
		this.mRemoveText = mRemoveText;
		mTextWidth = mTextPaint.measureText(mRemoveText);
	}

	public void setSwipeFlags(@SwipeFlag int mSwipeFlags) {
		this.mSwipeFlags = mSwipeFlags;
	}

	public void setDragFlags(@DragFlag int mDragFlags) {
		this.mDragFlags = mDragFlags;
	}

	public void setTextMargin(int mTextMargin) {
		this.mTextMargin = mTextMargin;
	}

	public void setTextSize(int mTextSize) {
		this.mTextSize = mTextSize;
		mTextPaint.setTextSize(mTextSize);
		mTextWidth = mTextPaint.measureText(mRemoveText);
	}
}
