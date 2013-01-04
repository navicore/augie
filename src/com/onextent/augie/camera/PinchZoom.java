package com.onextent.augie.camera;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.onextent.augie.AugieScape;
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;

public class PinchZoom implements Augiement, OnTouchListener {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/PINCH_ZOOM");
    
    private AugieScape augview;
    private Point p1, p2;
    private AugCamera camera;
    double initDist = -1;
    enum DIRECTION {NONE, IN, OUT};
    DIRECTION direction = DIRECTION.NONE;
    
    @Override
    public Meta getMeta() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void edit(Context context, EditCallback cb)
            throws AugieableException {
    }

    @Override
    public boolean isEditable() {
        return false;
    }

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
        
        augview = av;

        for (Augiement a : helpers) {
            
            if (a instanceof AugCamera) camera = (AugCamera) a;
            
        }
            
        if (camera == null) throw new AugiementException("camera feature is null");
    }

    private final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIENAME);
    }

	@Override
    public Set<CodeableName> getDependencyNames() {
        return deps;
    }
	
	private void zoom(double dist) {
	    /*
	     * todo: fix VERY VERY buggy, barely works at all
	     */
	    
	    if (initDist < 0) {
	        initDist = dist;
	        return;
	    }
	    
	    if (direction == DIRECTION.NONE) {
	        if (dist < initDist) {
	            direction = DIRECTION.IN;
	        } else {
	            direction = DIRECTION.OUT;
	        }
	    }
	    
	    if (dist < initDist && direction != DIRECTION.IN) {
	        return;
	    }
	    
	    try {
	        int max = camera.getParameters().getMaxZoom(); 
	        int czoom = camera.getParameters().getZoom(); 
	        double change = Math.abs(dist - initDist);
	        if (change < 50) return;
	        double scale = change / augview.getHeight();
	        double newzoom = 1 - (max * scale);
	        int z = (int) Math.abs(newzoom);
	        if (z > max) z = max;
	        if ((max - czoom) - (max - z) > max / 2) return; //dom't make big adjustments
	        camera.getParameters().setZoom(z);
	        camera.applyParameters();
	    } catch (Throwable e) {
	        Log.e(TAG, e.toString(), e);
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
            Log.e(TAG, e.toString(), e);
        }
        return false;
    }
}
