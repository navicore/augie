/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.onextent.augie.AugieName;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugShutterCallback;
import com.onextent.util.codeable.Code;

public abstract class AbstractSimplePhoneCamera extends AbstractPhoneCamera {

	protected Camera camera;
	
	private final Params params;
	
	AbstractSimplePhoneCamera() {
	    params = new Params();
	}

    @Override
	public void open() throws AugCameraException {
    	
        if (camera != null) return;
        try {
            Log.d(TAG, "open camera with id: " + getId());
            camera = Camera.open(getId());
        
        } catch (Throwable e) {
        	throw new AugCameraException(e);
        }
	}
	
    @Override
    public abstract AugieName getAugieName();
    
    protected abstract int getId();

    @Override
    public void close() throws AugCameraException {
        if (camera == null) return;
        camera.release();
        camera = null;
    }

    @Override
    public void setPreviewDisplay(SurfaceHolder holder) throws AugCameraException {
        open();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            throw new AugCameraException(e);
        }
    }

    @Override
    public void startPreview() throws AugCameraException {
        open();
        try {
            camera.startPreview();
        } catch (Exception e) {
            throw new AugCameraException(e);
        }
    }

    @Override
    public void stopPreview() throws AugCameraException {
        open();
        try {
            camera.stopPreview();
        } catch (Exception e) {
            throw new AugCameraException(e);
        }
    }
    
    @Override
    public void takePicture(final AugShutterCallback shutter, 
                            final AugPictureCallback raw,
                            final AugPictureCallback jpeg) {
        Camera.ShutterCallback scb = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                if (shutter != null) shutter.onShutter();
            }
        };
        Camera.PictureCallback rcb = new Camera.PictureCallback() {
            
            @Override
            public void onPictureTaken(byte[] data, Camera c) {
                if (raw != null) raw.onPictureTaken(data, AbstractSimplePhoneCamera.this);
            }
        };
        Camera.PictureCallback jcb = new Camera.PictureCallback() {
            
            @Override
            public void onPictureTaken(byte[] data, Camera c) {
                if (jpeg != null) jpeg.onPictureTaken(data, AbstractSimplePhoneCamera.this);
            }
        };
        camera.takePicture(scb, rcb, jcb);
    }

    @Override
    public AugCameraParameters getParameters() {

        return params;
    }
    
    private class Params implements AugCameraParameters {

        @Override
        public int getMaxNumFocusAreas() {
            return 0;
        }

        @Override
        public int getMaxNumMeteringAreas() {
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

    @Override
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Meta getMeta() {
        // TODO Auto-generated method stub
        return null;
    }
}
