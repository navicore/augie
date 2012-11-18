/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.MarkerFactory;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawFeature extends AugDrawBase {
	
    protected static final String TAG = AugieView.TAG;

	int lastX;
	int lastY;
    List<AugScrible> scribles;
    AugScrible currentScrible;

	public AugDrawFeature() {
	}
	
	@Override
    public void init() throws AugiementException {
    
        super.init();
	    scribles = new ArrayList<AugScrible>();
	    currentScrible = null;
    }
	    
	public AugScrible getCurrentScrible() {
	    return currentScrible;
	}
	
	public void undoCurrentScrible() {
	    if (currentScrible == null) return;
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
	    int action = event.getAction();
	    int x = (int) event.getX();
	    int y = (int) event.getY();
	    switch (action) {
	    case MotionEvent.ACTION_UP:
	    	lastX = -1;
	    	//todo: setting that discards current scrible and makes 'undolastscrible' func a noop
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
	public void updateCanvas() {
        if (!prefs.getBoolean("ETCHA_ENABLED", false) && lastX == -1) {
        //if (lastX == -1) {
            if (currentScrible != null) currentScrible.clear();
            scribles.clear();
        }
    	for (AugScrible s : scribles) {
    		for (AugLine l : s) {
    			augview.getCanvas().drawLine(l.getP1().x, l.getP1().y, l.getP2().x, l.getP2().y, augview.getPaint());
    		}
    	}
	}

    public static final String AUGIE_NAME = "AUGIE/FEATURES/DRAW";
    @Override
    public String getAugieName() {
        return AUGIE_NAME;
    }
}
