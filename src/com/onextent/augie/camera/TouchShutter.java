package com.onextent.augie.camera;

import java.util.Set;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.AugiementName;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;

public class TouchShutter implements Augiement, OnTouchListener {
    
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/TOUCH_SHUTTER");
    public static final String UI_NAME = "Touch Shutter";
    
    private final CameraShutterFeature shutter;
    
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
    public Set<CodeableName> getDependencyNames() {

        return shutter.getDependencyNames();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return shutter.onTouch(v, event);
    }

    @Override
    public String getUIName() {

        return UI_NAME;
    }
    
    public static final AugiementFactory.Meta getMeta() {
        return new AugiementFactory.Meta() {

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
        };
    }
}
