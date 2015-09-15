package com.handmark.pulltorefresh.library.recycler.animators.internal;

import android.view.View;

public interface DoAnimationViewPropertyListener {
	boolean onAnimationStart(View view);

	boolean onAnimationEnd(View view);

	boolean onAnimationCancel(View view);
}