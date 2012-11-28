package com.onextent.augie.camera.impl;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;

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
        public JSONObject getCode() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setCode(JSONObject code) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String getAugieName() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    @Override
    public abstract String getAugieName();

    @Override
    protected abstract int getId();
    
    @Override
    public JSONObject getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(JSONObject state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void edit(Context context, EditCallback cb) {
        // TODO Auto-generated method stub
        
    }
}
