/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.shutter;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.impl.AugDrawFeature;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;

public class SimpleCameraShutterFeature extends TouchShutterBase implements OnTouchListener {
	
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/SIMPLE_SHUTTER");
    
    protected Shutter shutter;
    protected AugieScape augieScape;
	protected AugCamera camera;
	protected AugDrawFeature augdraw;
	
    private int meterAreaColor = Color.GRAY;
    private int focusAreaColor = Color.GREEN;
    private boolean always_set_focus_area = true;

	private int touchFocusSz = 10;

	final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIENAME);
        deps.add(Shutter.AUGIE_NAME);
        deps.add(AugDrawFeature.AUGIE_NAME);
    }

	@Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
	    
	    augieScape = av;
	    
        for (Augiement a : helpers) {
            if (a instanceof AugCamera) {
                camera = (AugCamera) a;
            }
            else if (a instanceof Shutter) {
                shutter = (Shutter) a;
            }
            else if (a instanceof AugDrawFeature) {
                augdraw = (AugDrawFeature) a;
            }
        }
        if (camera == null) throw new AugiementException("camera feature is null");
        if (augdraw == null) throw new AugiementException("draw feature is null");
        if (shutter == null) throw new AugiementException("shutter feature is null");
    }
	
	@Override
	public void updateCanvas() {
		//noop	
	}

    protected void takePicture(AugPictureCallback userCb) throws AugCameraException {
    	shutter.takePicture(userCb);
	}
    private void takePicture() throws AugCameraException {
    	shutter.takePicture();
	}
    
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		try {
			
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:

            AugScrible scrible = augdraw.getCurrentScrible();
            if (scrible.getGestureType() == GESTURE_TYPE.TAP) takePicture();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		default:
			return false;
		}
		} catch (Exception e) {
			AugLog.e( e.toString(), e);
		}
	    return true;
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
	
	@Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    private static final String DEFAULT_FOCUS_SZ_KEY 	= "defaultFocusAreaSize";
    private static final String ALWAYS_FOCUS_AREA_KEY 	= "alwaysSetFocusArea";
    private static final String FOCUS_AREA_COLOR_KEY 	= "focusAreaColor";
    private static final String METER_AREA_COLOR_KEY 	= "meterAreaColor";
    
    @Override
    public Code getCode() throws CodeableException {

        Code code = JSONCoder.newCode();
        code.put(FOCUS_AREA_COLOR_KEY, getFocusAreaColor());
        code.put(METER_AREA_COLOR_KEY, getMeterAreaColor());
        code.put(ALWAYS_FOCUS_AREA_KEY, isAlways_set_focus_area());
        code.put(DEFAULT_FOCUS_SZ_KEY, getTouchFocusSz());
        
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {

    	if (code.has(FOCUS_AREA_COLOR_KEY)) 
    		setFocusAreaColor(code.getInt(FOCUS_AREA_COLOR_KEY));
    	if (code.has(METER_AREA_COLOR_KEY)) 
    		setMeterAreaColor(code.getInt(METER_AREA_COLOR_KEY));
    	if (code.has(ALWAYS_FOCUS_AREA_KEY)) 
    		setAlways_set_focus_area(code.getBoolean(ALWAYS_FOCUS_AREA_KEY));
    	if (code.has(DEFAULT_FOCUS_SZ_KEY))
    		setTouchFocusSz(code.getInt(DEFAULT_FOCUS_SZ_KEY));
    }

    @Override
    public DialogFragment getUI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meta getMeta() {
        return null;
    }
    
    public int getMeterAreaColor() {
        return meterAreaColor;
    }
    public void setMeterAreaColor(int meterAreaColor) {
        this.meterAreaColor = meterAreaColor;
    }
    public int getFocusAreaColor() {
        return focusAreaColor;
    }
    public void setFocusAreaColor(int focusAreaColor) {
        this.focusAreaColor = focusAreaColor;
    }
    public boolean isAlways_set_focus_area() {
        return always_set_focus_area;
    }
    public void setAlways_set_focus_area(boolean always_set_focus_area) {
        this.always_set_focus_area = always_set_focus_area;
    }

    public int getTouchFocusSz() {
        return touchFocusSz;
    }
    public void setTouchFocusSz(int sz) {
        this.touchFocusSz = sz;
    }
}
