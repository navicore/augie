package com.onextent.augie.camera;

import java.util.Set;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.onextent.augie.AugieScape;
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;

public class TouchShutter implements Augiement, OnTouchListener {
    
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/TOUCH_SHUTTER");
    
    private final CameraShutterFeature shutter;
    
    public TouchShutter() {
        shutter = CameraShutterFeature.getInstance();
    }

    @Override
    public CodeableName getCodeableName() {
        
        return AUGIE_NAME;
    }

    @Override
    public Meta getMeta() {
        
        return shutter.getMeta();
    }

    @Override
    public void edit(Context context, EditCallback cb)
            throws AugieableException {
        
        shutter.edit(context, cb);
    }

    @Override
    public boolean isEditable() {
        
        return shutter.isEditable();
    }

    @Override
    public Code getCode() {
        
        return shutter.getCode();
    }

    @Override
    public void setCode(Code code) {
        
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
}
