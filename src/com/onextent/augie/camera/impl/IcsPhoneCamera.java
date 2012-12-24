package com.onextent.augie.camera.impl;

import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class IcsPhoneCamera extends SimplePhoneCamera {

    public IcsPhoneCamera(int id) {
        super(id);
    }

    @Override
    public void edit(Context context, EditCallback cb) {
        // TODO Auto-generated method stub
        
    }
    
    //NOOP stubs just here so that dependency manager can 
    // give cameras to shutter and config augiements
    @Override
    public void updateCanvas() { }

    @Override
    public void clear() { }

    @Override
    public void stop() { }

    @Override
    public void resume() { }

    @Override
    public Set<CodeableName> getDependencyNames() { return null; }
    
    protected Params newParams() {
            Params p = new IcsParams();
            return p;
    }
   
    protected class IcsParams extends Params {

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
            return IcsPhoneCamera.this.camera.getParameters().getMaxNumFocusAreas();
        }

        @Override
        public int getMaxNumMeteringAreas() {
            return IcsPhoneCamera.this.camera.getParameters().getMaxNumMeteringAreas();
        }
        
    }
    
    public void initParams() {
        super.initParams();
        //todo: update with each ics setting
    }

    protected Camera.Parameters getUpdatedCameraParameters() {
        Log.d(TAG, "ejs updating ICS params");
        Camera.Parameters cp = super.getUpdatedCameraParameters();
        if (cp != null) {
            
            AugCameraParameters p = getParameters();
            if (p != null) {
                //todo: update with each ics setting
            }
        }
        
        return cp;
    }
}
