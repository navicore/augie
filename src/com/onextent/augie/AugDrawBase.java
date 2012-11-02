/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawBase implements AugmentedViewFeature {

    protected static final String TAG = AugmentedView.TAG;
    protected final AugmentedView augview;

    protected enum LINE_TYPE {HORIZONTAL_LINE, VERTICAL_LINE, BAD_LINE}

	
	protected class Line {
		Point p1;
    	Point p2;
    	Point center;
    	Line(Point p1, Point p2) {
	        this.p1 = p1;
	        this.p2 = p2;
	        this.center = null;
        }
    }
	protected class VLine extends Line {

		VLine(int top, int bottom, int x) {
		    super(new Point(x, top), new Point(x, bottom));
		    center = new Point(x, bottom / 2);
        }
	}
	protected class HLine extends Line {

		HLine(int left, int right, int y) {
		    super(new Point(left, y), new Point(right, y));
		    center = new Point(right / 2, y);
        }
	}
	
	public AugDrawBase(AugmentedView v) {
		super();
		augview = v;
	}

	public boolean onTouch(View v, MotionEvent event) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public void updateBmp() {
	    // TODO Auto-generated method stub
	    
    }

	public void clear() {
	    // TODO Auto-generated method stub
	    
    }

	public void stop() {
	    // TODO Auto-generated method stub
	    
    }

	public void resume() {
	    // TODO Auto-generated method stub
	    
    }
}
