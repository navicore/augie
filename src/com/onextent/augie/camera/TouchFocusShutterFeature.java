package com.onextent.augie.camera;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.AugieView;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchFocusShutterFeature extends SimpleCameraShutterFeature {

    final AugieView augview;
    final List<ScribleHolder> focus_areas, meter_areas;
    final int max_focus_areas;
    final int max_metering_areas;
    
    private ScribleHolder movingRect;
    private Point startP;
    
	@TargetApi(14)
    public TouchFocusShutterFeature(Context ctx, 
                                    AugCamera c, 
                                    AugDrawFeature d, 
                                    SharedPreferences p,
                                    AugieView v) {
        super(ctx, c, d, p);
        augview = v;
        focus_areas = new ArrayList<ScribleHolder>();
        meter_areas = new ArrayList<ScribleHolder>();
	    Camera cam = augcamera.getCamera();
	    max_focus_areas = cam.getParameters().getMaxNumFocusAreas();
	    max_metering_areas = cam.getParameters().getMaxNumMeteringAreas();
	    Log.d(TAG, "focus areas: " + max_focus_areas);
	    Log.d(TAG, "metering areas: " + max_metering_areas);
    }
    
    private class ScribleHolder {
        //AugScrible scrible;
        Rect rect;
    }

    protected void takePicture() {
	    Log.d(TAG, "trying to focus...");
	    Camera c = augcamera.getCamera();
	    if (c == null)  return;
	    if (max_focus_areas > 0 && prefs.getBoolean("TOUCH_FOCUS_ENABLED", true)) {
	        if (focus_areas.size() == 0) {
	            //todo: focus then take pic
	            super.takePicture();
	        } else {
	            super.takePicture();
	        }
	    } else {
	        super.takePicture();
	    }
	}
	private void saveArea(AugScrible s, List<ScribleHolder> areas) {
        ScribleHolder h = new ScribleHolder();
        //h.scrible = s;
        h.rect = new Rect(s.getMinX(), s.getMinY(), s.getMaxX(), s.getMaxY());
        areas.add(h);
        augdraw.undoCurrentScrible();
	}
    private void saveFocusArea(AugScrible s) {
        if (max_focus_areas <= focus_areas.size()) {
            focus_areas.remove(0);
        }
        saveArea(s, focus_areas);
        //todo: set focus area(s) after converting to -1000 x 1000 system
	}
	
    private void saveMeterArea(AugScrible s) {
        if (max_metering_areas <= meter_areas.size()) {
            meter_areas.remove(0);
        }
        saveArea(s, meter_areas);
        //todo: set meter area(s) after converting to -1000 x 1000 system
	}
    
    private ScribleHolder getRect(Point p) {
        //todo: deal with rect inside rect
        for (ScribleHolder h : focus_areas) 
            if (h.rect.contains(p.x, p.y)) return h;
        for (ScribleHolder h : meter_areas) 
            if (h.rect.contains(p.x, p.y)) return h;
        
        return null;
    }
    
    private void deleteRect(ScribleHolder h) {
        focus_areas.remove(h);
        meter_areas.remove(h);
    }
    private void moveRect(MotionEvent event) {
        Point endP = new Point((int) event.getX(), (int) event.getY());
        movingRect.rect.offset(endP.x - startP.x, endP.y - startP.y);
        augdraw.undoCurrentScrible();
    }
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		try {
			
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		
		case MotionEvent.ACTION_DOWN:
		    //todo: 2 finger move should be property
            startP = new Point((int) event.getX(), (int) event.getY());
            movingRect = getRect(startP);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		    //todo: 2 finger move should be property
            //startP = new Point((int) event.getX(), (int) event.getY());
            //movingRect = getRect(startP);
			break;
		case MotionEvent.ACTION_MOVE:
		    if (movingRect != null) {
		        moveRect(event);
		        startP = new Point((int) event.getX(), (int) event.getY()); //reset startP
		    }
			break;
		case MotionEvent.ACTION_UP:
		    if (movingRect != null) {
		        moveRect(event);
		        if (closeToEdge(event)) deleteRect(movingRect);
		        startP = null;
		        movingRect = null;
		    } else {
		        AugScrible scrible = augdraw.getCurrentScrible();
                if (scrible.getGestureType() == GESTURE_TYPE.TAP) takePicture();
                if (scrible.getGestureType() == GESTURE_TYPE.CLOCKWISE_AREA) saveFocusArea(scrible);
                if (scrible.getGestureType() == GESTURE_TYPE.COUNTER_CLOCKWISE_AREA) saveMeterArea(scrible);
		    }
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		default:
			return false;
		}
		} catch (Exception e) {
			Log.e(TAG, e.toString(), e);
		}
	    return true;
    }

	@Override
	public void resume() {
        Log.d(TAG, "TouchFocusShutterFeature resume");
		//noop
	}

    @Override
    public void updateBmp() {
        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        for (ScribleHolder h : meter_areas) {
            p.setColor(Color.GRAY);
            augview.getCanvas().drawRect(h.rect, augview.getPaint());
        }
        for (ScribleHolder h : focus_areas) {
            p.setColor(Color.GREEN);
            augview.getCanvas().drawRect(h.rect, augview.getPaint());
        }
        p.setStrokeWidth(orig_w);
        p.setColor(old_color);
    }

    @Override
    public void clear() {
        focus_areas.clear();
        meter_areas.clear();
    }
    
    private static float CLOSE_PIXELS = 25;
    private boolean xcloseToEdge(MotionEvent e) {
        float x = e.getX();
        if ( x < CLOSE_PIXELS ) return true;
        if ( x > (augview.getWidth() - CLOSE_PIXELS )) return true;
        
        return false;
    }
    private boolean ycloseToEdge(MotionEvent e) {
        float y = e.getY();
        if ( y < CLOSE_PIXELS ) return true;
        if ( y > (augview.getHeight() - CLOSE_PIXELS )) return true;
        
        return false;
    }
    protected boolean closeToEdge(MotionEvent e) {
        return xcloseToEdge(e) || ycloseToEdge(e);
    }
}
