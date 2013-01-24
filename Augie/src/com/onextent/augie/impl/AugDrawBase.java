/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.impl;

import java.util.Set;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.impl.AugLineImpl;

public abstract class AugDrawBase implements Augiement, OnTouchListener {

    protected AugieScape augieScape;
    public float closePixelDist = 25;
	
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
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
		augieScape = av;
    }

    protected boolean xcloseToEdge(MotionEvent e) {
        float x = e.getX();
        if ( x < closePixelDist ) return true;
        if ( x > (augieScape.getWidth() - closePixelDist )) return true;
        
        return false;
    }
    protected boolean ycloseToEdge(MotionEvent e) {
        float y = e.getY();
        if ( y < closePixelDist ) return true;
        if ( y > (augieScape.getHeight() - closePixelDist )) return true;
        
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
        AugLog.d( "stopping " + getClass().getName());
		//noop
	}
	
	@Override
	public void resume() {
        AugLog.d( "resuming " + getClass().getName());
		//noop
	}

	@Override
	public void clear() {
        AugLog.d( "clearing " + getClass().getName());
		//noop
    }
	
	public float getClosePixelDist() {
		return closePixelDist;
	}

	public void setClosePixelDist(float sz) {
		closePixelDist = sz;
	}
}
