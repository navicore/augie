package com.onextent.augie.camera;

import java.util.Set;

import org.json.JSONObject;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieView;
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;

public class TouchShutter implements Augiement, OnTouchListener {
    
    public static final AugieName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/TOUCH_SHUTTER");
    
    private final CameraShutterFeature shutter;
    
    public TouchShutter() {
        shutter = CameraShutterFeature.getInstance();
    }

    @Override
    public AugieName getAugieName() {
        
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
    public JSONObject getCode() {
        
        return shutter.getCode();
    }

    @Override
    public void setCode(JSONObject code) {
        
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
    public void onCreate(AugieView av, Set<Augiement> helpers)
            throws AugiementException {

        shutter.onCreate(av, helpers);
    }

    @Override
    public Set<AugieName> getDependencyNames() {

        return shutter.getDependencyNames();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return shutter.onTouch(v, event);
    }
}
