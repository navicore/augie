package com.onextent.augie.camera.impl;

import java.util.Set;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.CameraName;
import com.onextent.util.codeable.CodeableName;

public class SimpleBackPhoneCamera extends AbstractSimplePhoneCamera {

    @Override
    public CodeableName getCodeableName() {
        
        return AUGIENAME;
    }

    @Override
    protected int getId() {
        //note, todo:
        return 0;
    }

    @Override
    public void updateCanvas() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers)
            throws AugiementException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Set<CodeableName> getDependencyNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CameraName getCameraName() {
        
        return BACK_CAMERA_NAME;
    }

}
