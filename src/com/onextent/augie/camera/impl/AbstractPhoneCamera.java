/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera.impl;

import android.os.Build;
import com.onextent.augie.camera.AugCamera;

/*
 * factory to allow pre-icecreamsandwich touchfocus devices
 */
public abstract class AbstractPhoneCamera implements AugCamera {
   
    protected static final String BACK_CAMERA_NAME = "AUGIE/FEATURES/CAMERA/BACK_CAMERA";
    protected static final String FRONT_CAMERA_NAME = "AUGIE/FEATURES/CAMERA/FRONT_CAMERA";
    
    public static AbstractPhoneCamera getInstance(String name) {
        
        AbstractPhoneCamera sInstance;

        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            if (BACK_CAMERA_NAME.equals(name)) 
                sInstance = new SimpleBackPhoneCamera();
            else 
                sInstance = new SimpleFrontPhoneCamera();
        } else  {
            if (BACK_CAMERA_NAME.equals(name)) 
                sInstance = new IcsBackPhoneCamera();
            else 
                sInstance = new IcsFrontPhoneCamera();
        }
        return sInstance;
    }
}
