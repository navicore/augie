/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.util.List;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;

import com.onextent.augie.camera.AugCameraException;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class IcsPhoneCamera extends SimplePhoneCamera {

    public IcsPhoneCamera(int id) {
        super(id);
    }

    @Override
    protected CamParams newParams() {
            CamParams p = new IcsParams(this);
            return p;
    }
   
    @Override
    public void applyParameters() throws AugCameraException {
        
        Camera.Parameters cp = getUpdatedCameraParameters();
        __applyParameters(cp);
        
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(getId(), info);
        //todo: ICS 17+ only
        //if (info.canDisableShutterSound) {
        //}
    }

    @Override
    protected Parameters getUpdatedCameraParameters() {
        
        Parameters p = super.getUpdatedCameraParameters();
        
        List<Camera.Area> fa = getParameters().getFocusAreas();
        if (fa != null) p.setFocusAreas(fa);
        
        List<Camera.Area> ma = getParameters().getMeteringAreas();
        if (ma != null) p.setMeteringAreas(ma);
        
        return p;
    }
}
