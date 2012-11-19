package com.onextent.augie.camera.impl;

public class SimpleFrontPhoneCamera extends AbstractSimplePhoneCamera {

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
