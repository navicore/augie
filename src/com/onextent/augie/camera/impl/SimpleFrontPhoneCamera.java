package com.onextent.augie.camera.impl;

import java.util.Set;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.CameraName;

public class SimpleFrontPhoneCamera extends AbstractSimplePhoneCamera {

    @Override
    public AugieName getAugieName() {
        
        return AUGIENAME;
    }

    @Override
    protected int getId() {
        //note, todo:
        return 1;
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
    public void onCreate(AugieView av, Set<Augiement> helpers)
            throws AugiementException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Set<AugieName> getDependencyNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CameraName getCameraName() {
        
        return FRONT_CAMERA_NAME;
    }
}
