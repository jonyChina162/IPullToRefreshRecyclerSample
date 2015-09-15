package com.handmark.pulltorefresh.library.recycler;

interface ItemTouchHelperInterface {

	boolean onItemMove(int fromPosition, int toPosition);

	boolean onItemDismiss(int position);
}