package com.onextent.augie.camera.impl;

import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class IcsPhoneCamera extends SimplePhoneCamera {

    private final AugCameraParameters params;
    
    public IcsPhoneCamera(int id) {
        super(id);
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
                IcsPhoneCamera.this.open();
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
                IcsPhoneCamera.this.open();
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
        public CodeableName getCodeableName() {
            throw new java.lang.UnsupportedOperationException("abstract");
        }
    }

    @Override
    public Code getCode() {
        Code code = super.getCode();
        // TODO Auto-generated method stub
        // TODO add ics stuff
        //todo: override augiename
        //ARGH!!!!!!!!!!!!!!!!
        //ARGH!!!!!!!!!!!!!!!!
        //ARGH!!!!!!!!!!!!!!!!
        //ARGH!!!!!!!!!!!!!!!!
        //ARGH!!!!!!!!!!!!!!!!
        //ARGH!!!!!!!!!!!!!!!!
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        // need a single instanciatable impl
        
        //create helper impls
        
        return code;
    }

    @Override
    public void setCode(Code code) {
        super.setCode(code);
        // TODO Auto-generated method stub
        // TODO add ics stuff
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
}
