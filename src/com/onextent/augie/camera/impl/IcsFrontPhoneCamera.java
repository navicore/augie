package com.onextent.augie.camera.impl;

import java.util.Set;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.CameraName;
import com.onextent.util.codeable.CodeableName;

public class IcsFrontPhoneCamera extends AbstractIcsPhoneCamera {

    @Override
    public CodeableName getCodeableName() {
        
        return AUGIENAME;
    }

    @Override
    protected int getId() {
        //note, todo:
        return 1;
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers)
            throws AugiementException {
    }

    @Override
    public CameraName getCameraName() {
        
        return AbstractPhoneCamera.FRONT_CAMERA_NAME;
    }
}
