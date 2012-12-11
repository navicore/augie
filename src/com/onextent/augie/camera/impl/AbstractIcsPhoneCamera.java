package com.onextent.augie.camera.impl;

import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.onextent.augie.AugieName;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.util.codeable.Code;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public abstract class AbstractIcsPhoneCamera extends AbstractSimplePhoneCamera {

    private final AugCameraParameters params;
    
    public AbstractIcsPhoneCamera() {
        params = new IcsParams();
    }
    
    @Override
    public AugCameraParameters getParameters() {
       
        return params;
    }
    
    private class IcsParams implements AugCameraParameters {

        @Override
        public int getMaxNumFocusAreas() {
            try {
                AbstractIcsPhoneCamera.this.open();
                int n = camera.getParameters().getMaxNumFocusAreas();
                return n;
            } catch (AugCameraException e) {
                Log.e(TAG, e.getMessage() ,e);
            }
            return 0;
        }

        @Override
        public int getMaxNumMeteringAreas() {
            try {
                AbstractIcsPhoneCamera.this.open();
                int n = camera.getParameters().getMaxNumMeteringAreas();
                return n;
            } catch (AugCameraException e) {
                Log.e(TAG, e.getMessage() ,e);
            }
            return 0;
        }

        @Override
        public Code getCode() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setCode(Code code) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public AugieName getAugieName() {
            throw new java.lang.UnsupportedOperationException("abstract");
        }
    }

    @Override
    public abstract AugieName getAugieName();

    @Override
    protected abstract int getId();
    
    @Override
    public Code getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(Code state) {
        // TODO Auto-generated method stub
        
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
    public Set<AugieName> getDependencyNames() { return null; }
}
