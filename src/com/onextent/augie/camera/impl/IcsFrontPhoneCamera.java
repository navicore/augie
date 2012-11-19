package com.onextent.augie.camera.impl;

public class IcsFrontPhoneCamera extends AbstractIcsPhoneCamera {

    @Override
    public String getAugieName() {
        
        return FRONT_CAMERA_NAME;
    }

    @Override
    protected int getId() {
        //note, todo:
        return 1;
    }
}
