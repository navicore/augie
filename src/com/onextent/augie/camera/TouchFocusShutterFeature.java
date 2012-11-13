package com.onextent.augie.camera;

import java.util.HashSet;
import java.util.Set;

import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.AugmentedView;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchFocusShutterFeature extends SimpleCameraShutterFeature {

    final AugmentedView augview;
    final Set<ScribleHolder> focus_areas, meter_areas;
    final int max_focus_areas;
    final int max_metering_areas;
    
	@TargetApi(14)
    public TouchFocusShutterFeature(Context ctx, 
                                    AugCamera c, 
                                    AugDrawFeature d, 
                                    SharedPreferences p,
                                    AugmentedView v) {
        super(ctx, c, d, p);
        augview = v;
        focus_areas = new HashSet<ScribleHolder>();
        meter_areas = new HashSet<ScribleHolder>();
	    Camera cam = augcamera.getCamera();
	    max_focus_areas = cam.getParameters().getMaxNumFocusAreas();
	    max_metering_areas = cam.getParameters().getMaxNumMeteringAreas();
	    Log.d(TAG, "focus areas: " + max_focus_areas);
	    Log.d(TAG, "metering areas: " + max_metering_areas);
    }
    
    private class ScribleHolder {
        AugScrible scrible;
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
	private void saveArea(AugScrible s, Set<ScribleHolder> areas) {
        ScribleHolder h = new ScribleHolder();
        h.scrible = s;
        h.rect = new Rect(s.getMinX(), s.getMinY(), s.getMaxX(), s.getMaxY());
        areas.add(h);
        augdraw.undoCurrentScrible();
	}
    private void saveFocusArea(AugScrible s) {
        saveArea(s, focus_areas);
        //todo: set focus area(s) after converting to -1000 x 1000 system
	}
	
    private void saveMeterArea(AugScrible s) {
        saveArea(s, meter_areas);
        //todo: set meter area(s) after converting to -1000 x 1000 system
	}
	
	public boolean onTouch(View v, MotionEvent event) {

		try {
			
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		
		case MotionEvent.ACTION_DOWN:
		    //todo: move rect
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
		    //todo: move rect
			break;
		case MotionEvent.ACTION_UP:
		    //todo: move rect
		    //else

            AugScrible scrible = augdraw.getCurrentScrible();
            if (scrible.getGestureType() == GESTURE_TYPE.TAP) takePicture();
            if (scrible.getGestureType() == GESTURE_TYPE.CLOCKWISE_AREA) saveFocusArea(scrible);
            if (scrible.getGestureType() == GESTURE_TYPE.COUNTER_CLOCKWISE_AREA) saveMeterArea(scrible);
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
            p.setColor(Color.GREEN);
            augview.getCanvas().drawRect(h.rect, augview.getPaint());
        }
        for (ScribleHolder h : focus_areas) {
            p.setColor(Color.BLUE);
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
}
