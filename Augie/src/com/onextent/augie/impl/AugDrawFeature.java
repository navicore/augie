/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.MarkerFactory;

import android.graphics.Point;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AugDrawFeature extends AugDrawBase {
    
    private static String DESCRIPTION = "Captures screen touches for processing by other augiements.";
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/DRAW");
    public static final String UI_NAME = "Augie Draw";
	
	int prevX;
	int prevY;
    List<AugScrible> scribles;
    AugScrible currentScrible;

	@Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
    
        super.onCreate(av, helpers);
	    scribles = new ArrayList<AugScrible>();
	    currentScrible = null;
	    prevX = -1;
    }
	
	public AugScrible getCurrentScrible() {
	    return currentScrible;
	}
	
	public void undoCurrentScrible() {
	    if (currentScrible == null) return;
	    currentScrible.clear();
	    augieScape.reset();
	}
	
	public void undoLastScrible() {
		int last = scribles.size();
		int lastidx = last - 1;
		if (last > 0) {
			scribles.remove(lastidx);
			augieScape.reset();
		}
	}

	private void scrible(int lx, int ly, int x, int y) {
		Point p1 = new Point(lx, ly);
		Point p2 = new Point(x, y);
		AugLine l = MarkerFactory.createLine(p1, p2);
		currentScrible.add(l);
		augieScape.getCanvas().drawLine(lx, ly, x, y, augieScape.getPaint());
	}
	
    @Override
	public boolean onTouch(View v, MotionEvent event) {
	    int action = event.getAction();
	    int x = (int) event.getX();
	    int y = (int) event.getY();
	    switch (action) {
	    case MotionEvent.ACTION_UP:
	        Log.d(TAG, "motion event up");
	       
	        //ejs BUG for zero len tap, trouble
	        //either of these approaches causes 
	        //  dragging off screen to take picture :(
	        //handleZeroLenTap(prevX, prevY, x, y);
	        scrible(prevX, prevY, x, y);
	       
	        /*
	        how about a fake 1 pixel move?
	               
	        better idea, find out:
	        why does touch shutter think a drag off screen
	        is a tap?
	         */
	        
	    	prevX = -1;
	    	//todo: setting that discards current scrible and makes 'undolastscrible' func a noop
	    	break;
	    case MotionEvent.ACTION_DOWN:
	        Log.d(TAG, "motion event down");
	    	
	    	currentScrible = MarkerFactory.createScrible(augieScape);
	    	scribles.add(currentScrible);
	    
	        prevX = (int) event.getX();
	        prevY = (int) event.getY();
	    	break;
	    case MotionEvent.ACTION_MOVE:
	        Log.d(TAG, "motion event move");
	    	if (prevX != -1) {
	    		scrible(prevX, prevY, x, y);
	    	}
	        prevX = (int) event.getX();
	        prevY = (int) event.getY();
	    	break;
	    default:
	        Log.d(TAG, "motion event unknown");
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
        //if (!prefs.getBoolean("ETCHA_ENABLED", false) && lastX == -1) {
        if (prevX == -1) {
            if (currentScrible != null) currentScrible.clear();
            scribles.clear();
        }
    	for (AugScrible s : scribles) {
    		for (AugLine l : s) {
    			augieScape.getCanvas().drawLine(l.getP1().x, l.getP1().y, l.getP2().x, l.getP2().y, augieScape.getPaint());
    		}
    	}
	}
   
    public static final Augiement.Meta META =
        new Augiement.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return AugDrawFeature.class;
            }

            @Override
            public CodeableName getCodeableName() {
                
                return AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return UI_NAME;
            }

            @Override
            public String getDescription() {

                return DESCRIPTION;
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                return null;
            }
        };

    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    @Override
    public Code getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(Code code) {
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
    public DialogFragment getUI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meta getMeta() {

        return META;
    }
}