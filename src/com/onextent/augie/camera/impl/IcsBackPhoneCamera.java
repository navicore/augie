package com.onextent.augie.camera.impl;

public class IcsBackPhoneCamera extends AbstractIcsPhoneCamera {

    @Override
    public String getAugieName() {
        
        return BACK_CAMERA_NAME;
    }

    @Override
    protected int getId() {
        //note, todo:
        return 0;
    }
}
