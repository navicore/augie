/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import com.onextent.augie.testcamera.TestCameraActivity;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawBase implements AugmentedViewFeature {

	protected static final String TAG = TestCameraActivity.TAG;
	protected final AugmentedView augview;

	protected enum LINE_TYPE {HORIZONTAL_LINE, VERTICAL_LINE, BAD_LINE}

	
	protected class Line {
		Point p1;
    	Point p2;
    	Line(Point p1, Point p2) {
	        this.p1 = p1;
	        this.p2 = p2;
        }
    }
	protected class VLine extends Line {

		VLine(Point p1, Point p2) {
	        super(p1, p2);
        }
	}
	protected class HLine extends Line {

		HLine(Point p1, Point p2) {
	        super(p1, p2);
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

	public void redraw() {
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
