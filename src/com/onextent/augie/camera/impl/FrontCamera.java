package com.onextent.augie.camera.impl;

public class FrontCamera extends BackCamera {

    public static final String AUGIE_NAME = AbstractPhoneCamera.FRONT_CAMERA_NAME;
    /*
     * wrapper to provide no-arg constructor for factory
     */
    public FrontCamera() {
        
        super(AbstractPhoneCamera.BACK_CAMERA_NAME);
    }
}
