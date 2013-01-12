/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.impl;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class IcsParams extends CamParams {

    /**
     * 
     */
    private final IcsPhoneCamera augcamera;

    IcsParams(IcsPhoneCamera icsPhoneCamera) {
        super(icsPhoneCamera);
        this.augcamera = icsPhoneCamera;
    }

    @Override
    public int getMaxNumFocusAreas() {
        return this.augcamera.camera.getParameters().getMaxNumFocusAreas();
    }

    @Override
    public int getMaxNumMeteringAreas() {
        return this.augcamera.camera.getParameters().getMaxNumMeteringAreas();
    }
    
}