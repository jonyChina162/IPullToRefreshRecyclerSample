package com.handmark.pulltorefresh.library.recycler.animators;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

public class AnimatorUtil {
	public static RecyclerView.ItemAnimator getAnimator(@NonNull String title) {
		for (AnimatorType animatorType : AnimatorType.values()) {
			if (animatorType.getTitle().equalsIgnoreCase(title)) {
				return animatorType.getAnimator();
			}
		}
		return new DefaultItemAnimator();
	}
}
