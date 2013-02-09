/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments;

import java.util.HashSet;
import java.util.Set;

import android.graphics.Point;
import android.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;

public class PinchZoom implements Augiement, OnTouchListener {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/PINCH_ZOOM");
    public static final String UI_NAME = "Pinch Zoom";
    public static final String DESCRIPTION = "Zoom control that recognizes the pinch gesture.";
    
    private AugieScape augieScape;
    private Point p1, p2;
    private AugCamera camera;
    double initDist = -1;
    
    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    @Override
    public Code getCode() throws CodeableException {
        return null;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
    }

    @Override
    public void updateCanvas() { }

    @Override
    public void clear() { }

    @Override
    public void stop() { }

    @Override
    public void resume() { }

    @Override
    public void onCreate(AugieScape av, 
                         Set<Augiement> helpers) 
                         throws AugiementException {
        
        augieScape = av;

        for (Augiement a : helpers) {
            
            if (a instanceof AugCamera) camera = (AugCamera) a;
            
        }
            
        if (camera == null) throw new AugiementException("camera feature is null");
    }

    private final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIE_NAME);
    }

	private void zoom(double dist) {
	    /*
	     * todo: fix buggy, zoom is not smooth and jumps to max too easily
	     */
	   
	    if (initDist < 0) {
	        initDist = dist;
	        return;
	    }
	   
	    try {
	        double max = camera.getParameters().getMaxZoom(); 
	        double newzoom;
	        double max20 = max / 20;
	        if (dist < max20) newzoom = 0;
	        else if (dist > (max - max20)) newzoom = max;
	        else {
	            double czoom = camera.getParameters().getZoom(); 
	            double change = dist - initDist;
	            double scale = change / augieScape.getHeight();
	            double zoomchg = max * scale;
	            newzoom = czoom + (zoomchg);
	            if (newzoom > max) newzoom = max;
	            if (newzoom < 0) newzoom = 0;
	        }
	        camera.getParameters().setZoom((int) newzoom);
	        camera.applyParameters();
	    } catch (Throwable e) {
	        AugLog.e( e.toString(), e);
	    }
	}
	    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {

            switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                p1 = new Point((int) event.getX(), (int) event.getY());
                break;

            case MotionEvent.ACTION_MOVE:

                if (p1 == null || p2 == null) return false;
                float distx = event.getX(0) - event.getX(1);
                float disty = event.getY(0) - event.getY(1);
                double dist= Math.sqrt(distx * distx + disty * disty);
                zoom(dist);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                p2 = new Point((int) event.getX(), (int) event.getY());
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                p1 = null;
                p2 = null;
                initDist = -1;
                
            default:
            }
        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }
        return false;
    }
    
    public Meta getMeta() {
        return META;
    }

    public static final Meta META =
        new Augiement.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return PinchZoom.class;
            }
            @Override
            public CodeableName getCodeableName() {
                
                return AUGIE_NAME;
            }
            @Override
            public int getMinSdkVer() {
                return 0;
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
                return deps;
            }
        };

    @Override
    public DialogFragment getUI() {
        // TODO Auto-generated method stub
        return null;
    }
}
