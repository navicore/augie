/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.testcamera.TestCameraActivity;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawFeature extends AugDrawBase {
	
	static final String TAG = TestCameraActivity.TAG;

	int lastX;
	int lastY;
    final AugmentedView augview;
    final List<Line> lines;
    Line currentLine;

	public AugDrawFeature(AugmentedView v, Activity activity) {
		
		super(v);
		augview = v;
	    lastX = -1;
	    lines = new ArrayList<Line>();
	    currentLine = null;
	}
	
	public void undoLastLine() {
		int last = lines.size();
		if (last > 0) lines.remove(last);
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
	    	if (lastX != -1) {
	    		if ((int) event.getX() != lastX) {
	    			augview.getCanvas().drawLine(lastX, lastY, x, y, augview.getPaint());
	    		}
	    	}
	        lastX = (int) event.getX();
	        lastY = (int) event.getY();
	    	break;
	    case MotionEvent.ACTION_MOVE:
	    	if (lastX != -1) {
	    		augview.getCanvas().drawLine(lastX, lastY, x, y, augview.getPaint());
	    		
	    		//warning, expensive ...
	    		//todo: scribles
	    		/*
	    		Point p1 = new Point(lastX, lastY);
	    		Point p2 = new Point(x, y);
	    		Line l = new Line(p1, p2);
	    		lines.add(l);
	    		 */
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
    	lines.clear();
	}

    @Override
	public void redraw() {
    	//for (Scrible s : scribles) {
    		
    	//}
	}
}
