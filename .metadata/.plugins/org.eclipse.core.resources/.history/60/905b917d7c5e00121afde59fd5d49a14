/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.shutter;

import java.util.Set;

import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;

public class TouchShutter implements Augiement, OnTouchListener {
    
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/TOUCH_SHUTTER");
    public static final String UI_NAME = "Touch Shutter";
    public static final String DESCRIPTION = "Trigger camera shutter by touching the screen.";
    
    private final CameraShutterFeature shutter;
   
    /**
     * idiotic class is just here to give a no-argument constructor
     */
    public TouchShutter() {
        shutter = CameraShutterFeature.getInstance();
    }

    @Override
    public CodeableName getCodeableName() {
        
        return AUGIE_NAME;
    }

    @Override
    public Code getCode() throws CodeableException {
        
        return shutter.getCode();
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        
        shutter.setCode(code);
    }

    @Override
    public void updateCanvas() {

        shutter.updateCanvas();
    }

    @Override
    public void clear() {

        shutter.clear();
    }

    @Override
    public void stop() {

        shutter.stop();
    }

    @Override
    public void resume() {

        shutter.resume();
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers)
            throws AugiementException {

        shutter.onCreate(av, helpers);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return shutter.onTouch(v, event);
    }

    public static final Meta META =
        new Augiement.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return TouchShutter.class;
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
                return SimpleCameraShutterFeature.deps;
            }
        };

    @Override
    public DialogFragment getUI() {
        
        return shutter.getUI();
    }
    

    public int getMeterAreaColor() {
        return shutter.getMeterAreaColor();
    }

    public void setMeterAreaColor(int meterAreaColor) {
        shutter.setMeterAreaColor(meterAreaColor);
    }

    public int getFocusAreaColor() {
        return shutter.getFocusAreaColor();
    }

    public void setFocusAreaColor(int focusAreaColor) {
        shutter.setFocusAreaColor(focusAreaColor);
    }

    @Override
    public Meta getMeta() {
        
        return META;
    }
}
