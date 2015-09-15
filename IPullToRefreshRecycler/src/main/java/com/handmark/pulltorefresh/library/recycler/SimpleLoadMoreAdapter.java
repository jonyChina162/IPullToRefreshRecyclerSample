package com.handmark.pulltorefresh.library.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Collections;

public class SimpleLoadMoreAdapter<VH extends RecyclerView.ViewHolder, T> extends LoadMoreAdapter<VH, T> {
	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		if (fromPosition < toPosition) {
			for (int i = fromPosition; i < toPosition; i++) {
				Collections.swap(getContents(), i, i + 1);
			}
		} else {
			for (int i = fromPosition; i > toPosition; i--) {
				Collections.swap(getContents(), i, i - 1);
			}
		}

		return true;
	}

	@Override
	public boolean onItemDismiss(int position) {
		getContents().remove(position);
		return true;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

	}

	@Override
	public int getItemCount() {
		return 0;
	}
}
