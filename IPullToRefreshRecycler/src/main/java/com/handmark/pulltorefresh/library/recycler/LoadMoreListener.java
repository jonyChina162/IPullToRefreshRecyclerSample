package com.handmark.pulltorefresh.library.recycler;

import android.support.annotation.WorkerThread;

import java.util.List;

public interface LoadMoreListener<T> {
	/**
	 * 只在工作线程中处理，所以不要在这里将得到的数据自己添加到adapter的数据中
	 * 
	 * @return 返回要添加的数据列表
	 */
	@WorkerThread
	List<T> getMoreDataList();
}
