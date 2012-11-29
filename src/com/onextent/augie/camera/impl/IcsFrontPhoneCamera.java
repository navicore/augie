package com.onextent.augie.camera.impl;

import java.util.Set;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.CameraName;

public class IcsFrontPhoneCamera extends AbstractIcsPhoneCamera {

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
    public void onCreate(AugieView av, Set<Augiement> helpers)
            throws AugiementException {
    }

    @Override
    public CameraName getCameraName() {
        
        return AbstractPhoneCamera.FRONT_CAMERA_NAME;
    }
}
