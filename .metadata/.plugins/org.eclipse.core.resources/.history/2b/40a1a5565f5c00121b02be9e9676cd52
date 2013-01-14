/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.Set;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.impl.AugLineImpl;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class AugDrawBase implements Augiement, OnTouchListener {

    protected AugieScape augview;
    public static float CLOSE_PIXELS = 25;
	
	static class VLine extends AugLineImpl {

		VLine(int top, int bottom, int x, float width) {
		    super(new Point(x, top), new Point(x, bottom));
	        setWidth(width);
        }
	}
	static class HLine extends AugLineImpl {

		HLine(int left, int right, int y, float width) {
		    super(new Point(left, y), new Point(right, y));
	        setWidth ( width );
        }
	}
	
	public AugDrawBase() {
		super();
	}
	
	static public boolean isVerticalLine(AugLine l) {
	    
	    if (l.getP1().x == l.getP2().x) return true;
	    return false;
	}
	
	@Override
    public Set<CodeableName> getDependencyNames() {
        return null;
    }    
	
    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
		augview = av;
    }

    protected boolean xcloseToEdge(MotionEvent e) {
        float x = e.getX();
        if ( x < CLOSE_PIXELS ) return true;
        if ( x > (augview.getWidth() - CLOSE_PIXELS )) return true;
        
        return false;
    }
    protected boolean ycloseToEdge(MotionEvent e) {
        float y = e.getY();
        if ( y < CLOSE_PIXELS ) return true;
        if ( y > (augview.getHeight() - CLOSE_PIXELS )) return true;
        
        return false;
    }
    protected boolean closeToEdge(MotionEvent e) {
        return xcloseToEdge(e) || ycloseToEdge(e);
    }
    
    @Override
	public boolean onTouch(View v, MotionEvent event) {
	    // TODO Auto-generated method stub
	    return false;
    }

    @Override
	public void updateCanvas() {
	    // TODO Auto-generated method stub
	    
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

	@Override
	public void clear() {
        Log.d(TAG, "clearing " + getClass().getName());
		//noop
    }
	
}
