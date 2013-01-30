/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.fonecam;

import android.os.Build;
import com.onextent.augie.camera.AugCamera;

/*
 * factory to allow pre-icecreamsandwich touchfocus devices
 */
public abstract class AbstractFoneCam implements AugCamera {
   
    public static final Integer BACK_CAMERA_ID = 0;
    public static final Integer FRONT_CAMERA_ID = 1;
    
    public static AbstractFoneCam getInstance(int id) {
        
        AbstractFoneCam sInstance;

        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            sInstance = new SimpleFoneCam(id);
        } else  {
            sInstance = new IcsFoneCam(id);
        }
        return sInstance;
    }
}
