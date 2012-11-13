/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import com.onextent.augie.marker.impl.AugLineImpl;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawBase implements AugmentedViewFeature {

    protected static final String TAG = AugmentedView.TAG;
    protected final AugmentedView augview;
    protected final SharedPreferences prefs;
    public static float CLOSE_PIXELS = 25;
	
	static class VLine extends AugLineImpl {

		VLine(int top, int bottom, int x, float width) {
		    super(new Point(x, top), new Point(x, bottom));
		    setCenter(new Point(x, bottom / 2));
	        //this.width = Float.parseFloat(prefs.getString("VERTICAL_LINE_WIDTH", "9"));
	        setWidth(width);
        }
	}
	static class HLine extends AugLineImpl {

		HLine(int left, int right, int y, float width) {
		    super(new Point(left, y), new Point(right, y));
		    setCenter ( new Point(right / 2, y) );
	        //this.width = Float.parseFloat(prefs.getString("HORIZON_LINE_WIDTH", "9"));
	        setWidth ( width );
        }
	}
	
	public AugDrawBase(AugmentedView v, SharedPreferences p) {
		super();
		augview = v;
		prefs = p;
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
