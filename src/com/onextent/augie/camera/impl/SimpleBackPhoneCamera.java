package com.onextent.augie.camera.impl;

public class SimpleBackPhoneCamera extends AbstractSimplePhoneCamera {

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
