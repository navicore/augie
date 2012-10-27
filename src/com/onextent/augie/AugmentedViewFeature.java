/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import android.view.View.OnTouchListener;

//todo: rethink touch listener requirement, a feature may not need touch input
public interface AugmentedViewFeature extends OnTouchListener {

	//the view calls this when bmp has been rebuilt from scratch
	//and we need each feature to redraw
	void redraw();
	void clear();
	void stop();
	void resume();
}
