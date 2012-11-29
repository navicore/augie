package com.onextent.augie.camera.impl;

import java.util.Set;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;

public class IcsBackPhoneCamera extends AbstractIcsPhoneCamera {

    @Override
    public AugieName getAugieName() {
        
        return AUGIENAME;
    }

    @Override
    protected int getId() {
        //note, todo:
        return 0;
    }

    @Override
    public void onCreate(AugieView av, Set<Augiement> helpers)
            throws AugiementException {
    }

    @Override
    public String getCameraName() {
        
        return AbstractPhoneCamera.BACK_CAMERA_NAME;
    }
}
