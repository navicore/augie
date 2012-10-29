/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.testcamera.TestCameraActivity;

import android.app.Activity;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawFeature extends AugDrawBase {
	
	static final String TAG = TestCameraActivity.TAG;

	int lastX;
	int lastY;
    final AugmentedView augview;
    final List<List<Line>> scribles;
    List<Line> currentScrible;

	public AugDrawFeature(AugmentedView v, Activity activity) {
		
		super(v);
		augview = v;
	    lastX = -1;
	    scribles = new ArrayList<List<Line>>();
	    currentScrible = null;
	}
	
	public void undoCurrentScrible() {
	    currentScrible.clear();
	    augview.reset();
	}
	
	public void undoLastScrible() {
		int last = scribles.size();
		int lastidx = last - 1;
		if (last > 0) {
			scribles.remove(lastidx);
			augview.reset();
		}
	}

	private void scrible(int lx, int ly, int x, int y) {
		Point p1 = new Point(lx, ly);
		Point p2 = new Point(x, y);
		Line l = new Line(p1, p2);
		currentScrible.add(l);
		augview.getCanvas().drawLine(lx, ly, x, y, augview.getPaint());
	}
	
    @Override
	public boolean onTouch(View v, MotionEvent event) {
	    int action = event.getAction();
	    int x = (int) event.getX();
	    int y = (int) event.getY();
	    switch (action) {
	    case MotionEvent.ACTION_UP:
	    	lastX = -1;
	    	break;
	    case MotionEvent.ACTION_DOWN:
	    	
	    	currentScrible = new ArrayList<Line>();
	    	scribles.add(currentScrible);
	    	
	    	if (lastX != -1) {
	    		if ((int) event.getX() != lastX) {
	    			scrible(lastX, lastY, x, y);
	    		}
	    	}
	        lastX = (int) event.getX();
	        lastY = (int) event.getY();
	    	break;
	    case MotionEvent.ACTION_MOVE:
	    	if (lastX != -1) {
	    		scrible(lastX, lastY, x, y);
	    	}
	        lastX = (int) event.getX();
	        lastY = (int) event.getY();
	    	break;
	    default:
	    	return false;
	    }
	    return true;
    }
    
    @Override
	public void clear() {
    	scribles.clear();
	}

    @Override
	public void redraw() {
    	for (List<Line> s : scribles) {
    		for (Line l : s) {
    			augview.getCanvas().drawLine(l.p1.x, l.p1.y, l.p2.x, l.p2.y, augview.getPaint());
    		}
    	}
	}
}
