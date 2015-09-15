package com.handmark.pulltorefresh.library.recycler;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadMoreAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter implements
		ItemTouchHelperInterface {
	private final List<T> mContents = new ArrayList<>();

	public void addList(List<T> list) {
		mContents.addAll(list);
	}

	public List<T> getContents() {
		return mContents;
	}
}
