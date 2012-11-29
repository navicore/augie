package com.onextent.augie.camera.impl;

import com.onextent.augie.Augieable;

public class FrontCamera extends BackCamera {

    public static final String CAMERA_NAME = AbstractPhoneCamera.FRONT_CAMERA_NAME;
    
    /*
     * wrapper to provide no-arg constructor for factory
     */
    public FrontCamera() {
        
        super(CAMERA_NAME);
    }
    
    static private final class BackMeta implements Augieable.Meta {

        @Override
        public String getTitle() {
            return "Front Camera";
        }

        @Override
        public String getDescription() {
            return "Interal Selfie Camera Facing You";
        }

        @Override
        public String getCatagory() {
            return "Camera";
        }
        
    }
    static private final Augieable.Meta mymeta;
    static {
        mymeta = new BackMeta();
    }
    @Override
    public Meta getMeta() {
        return mymeta;
    }
}
