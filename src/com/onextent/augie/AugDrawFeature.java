/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.MarkerFactory;
import com.onextent.augie.marker.impl.AugLineImpl;
import com.onextent.augie.testcamera.TestCameraActivity;

import android.app.Activity;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawFeature extends AugDrawBase {
	
    protected static final String TAG = AugmentedView.TAG;

	int lastX;
	int lastY;
    final AugmentedView augview;
    final List<AugScrible> scribles;
    AugScrible currentScrible;

	public AugDrawFeature(AugmentedView v, Activity activity) {
		
		super(v, PreferenceManager.getDefaultSharedPreferences(activity));
		augview = v;
	    lastX = -1;
	    scribles = new ArrayList<AugScrible>();
	    currentScrible = null;
	}
	
	public AugScrible getCurrentScrible() {
	    return currentScrible;
	}
	/*
	public int getScribleLength() {
	    if (currentScrible == null) return 0;
	    return currentScrible.size();
	}
	 */
	
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
		AugLine l = MarkerFactory.createLine(p1, p2);
		currentScrible.add(l);
		augview.getCanvas().drawLine(lx, ly, x, y, augview.getPaint());
	}
	
    @Override
	public boolean onTouch(View v, MotionEvent event) {
        if (!prefs.getBoolean("ETCHA_ENABLED", true)) return false;
	    int action = event.getAction();
	    int x = (int) event.getX();
	    int y = (int) event.getY();
	    switch (action) {
	    case MotionEvent.ACTION_UP:
	    	lastX = -1;
	    	break;
	    case MotionEvent.ACTION_DOWN:
	    	
	    	currentScrible = MarkerFactory.createScrible(augview);
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
	public void updateBmp() {
        if (!prefs.getBoolean("ETCHA_ENABLED", true)) return;
    	for (AugScrible s : scribles) {
    		for (AugLine l : s) {
    			augview.getCanvas().drawLine(l.getP1().x, l.getP1().y, l.getP2().x, l.getP2().y, augview.getPaint());
    		}
    	}
	}
}
