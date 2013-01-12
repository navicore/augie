/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.impl;

import android.os.Build;
import com.onextent.augie.camera.AugCamera;

/*
 * factory to allow pre-icecreamsandwich touchfocus devices
 */
public abstract class AbstractPhoneCamera implements AugCamera {
   
    public static final Integer BACK_CAMERA_ID = 0;
    public static final Integer FRONT_CAMERA_ID = 1;
    
    public static AbstractPhoneCamera getInstance(int id) {
        
        AbstractPhoneCamera sInstance;

        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            sInstance = new SimplePhoneCamera(id);
        } else  {
            sInstance = new IcsPhoneCamera(id);
        }
        return sInstance;
    }
}
