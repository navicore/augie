package com.onextent.augie.camera.impl;

import java.util.List;

import android.annotation.TargetApi;
import android.hardware.Camera.Area;
import android.os.Build;

import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;

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
    public List<Area> getFocusAreas() {
        //always get the real camera areas
        //getFocusAreas on htc tablet very broken, has spaces in ( 0, 0, ... that don't parse
        //return this.augcamera.camera.getParameters().getFocusAreas();
        return super.getFocusAreas();
    }

    @Override
    public List<Area> getMeteringAreas() {
        //always get the real camera areas
        //return this.augcamera.camera.getParameters().getMeteringAreas();
        return super.getMeteringAreas();
    }

    @Override
    public Code getCode() throws CodeableException {
        return super.getCode();
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        super.setCode(code);
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