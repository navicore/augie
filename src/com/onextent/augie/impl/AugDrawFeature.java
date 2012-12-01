/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.MarkerFactory;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawFeature extends AugDrawBase {
	
    protected static final String TAG = Augiement.TAG;

	int lastX;
	int lastY;
    List<AugScrible> scribles;
    AugScrible currentScrible;

	@Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
    
        super.onCreate(av, helpers);
	    scribles = new ArrayList<AugScrible>();
	    currentScrible = null;
	    lastX = -1;
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
        Log.d(TAG, "clearing " + getClass().getName());
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

    public static final AugieName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/DRAW");
    @Override
    public AugieName getAugieName() {
        return AUGIE_NAME;
    }

    @Override
    public JSONObject getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(JSONObject state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void edit(Context context, EditCallback cb) throws AugieableException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Meta getMeta() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
	public void stop() {
        Log.d(TAG, "stopping " + getClass().getName());
		//noop
	}
	
	@Override
	public void resume() {
        Log.d(TAG, "resuming " + getClass().getName());
		//noop
	}
}
