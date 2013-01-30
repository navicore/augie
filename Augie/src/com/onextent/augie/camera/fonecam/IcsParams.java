/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.fonecam;

import com.onextent.augie.AugLog;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class IcsParams extends CamParams {

    /**
     * 
     */
    private final IcsFoneCam augcamera;

    IcsParams(IcsFoneCam icsPhoneCamera) {
        super(icsPhoneCamera);
        this.augcamera = icsPhoneCamera;
    }

    @Override
    public int getMaxNumFocusAreas() {
        if (this.augcamera.camera != null)
            return this.augcamera.camera.getParameters().getMaxNumFocusAreas();
        else {
            AugLog.w("null camera while looking max focus areas");
            return 0;
        }
    }

    @Override
    public int getMaxNumMeteringAreas() {
        if (this.augcamera.camera != null)
            return this.augcamera.camera.getParameters().getMaxNumMeteringAreas();
        else {
            AugLog.w("null camera while looking max meter areas");
            return 0;
        }
    }
    
    @Override
    public int getMaxNumDetectedFaces() {
        if (this.augcamera.camera != null)
            return this.augcamera.camera.getParameters().getMaxNumDetectedFaces();
        else {
            AugLog.w("null camera while looking for faces");
            return 0;
        }
    }
}