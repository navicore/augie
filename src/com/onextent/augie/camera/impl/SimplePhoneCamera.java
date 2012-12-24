/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.io.IOException;
import java.util.Set;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugShutterCallback;
import com.onextent.augie.camera.CameraName;
import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.JSONCoder;

public class SimplePhoneCamera extends AbstractPhoneCamera {
    
    private static final CodeableName PARAMS_CODEABLE_NAME = new CodeableName("/AUGIE/CAMERA/PARAMS"){};
	protected Camera camera;
	
	protected final int cameraId;
	protected final CameraName cameraName;
	
	private Params params;
	
	SimplePhoneCamera(int id) {
	    cameraId = id;
        cameraName = new CameraName("/AUGIE/CAMERA_ID_" + getId());
	}

    @Override
	public void open() throws AugCameraException {
    	
        if (camera != null) return;
        try {
            Log.d(TAG, "open camera with id: " + getId());
            camera = Camera.open(getId());
            if (params == null) {
                initParams();
            } else {
                applyParameters();
            }
        
        } catch (Throwable e) {
        	throw new AugCameraException(e);
        }
	}
	
    @Override
    public CameraName getCameraName() {
        return cameraName;
    }
    
    @Override
    public CodeableName getCodeableName() {
        return AugCamera.AUGIENAME;
    }
    
    public final int getId() {
        return cameraId;
    }

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
                if (raw != null) raw.onPictureTaken(data, SimplePhoneCamera.this);
            }
        };
        Camera.PictureCallback jcb = new Camera.PictureCallback() {
            
            @Override
            public void onPictureTaken(byte[] data, Camera c) {
                if (jpeg != null) jpeg.onPictureTaken(data, SimplePhoneCamera.this);
            }
        };
        camera.takePicture(scb, rcb, jcb);
    }

    @Override
    public AugCameraParameters getParameters() {
        
        return params;
    }
    
    protected Params newParams() {
            Params p = new Params();
            return p;
    }
    @Override
    public void initParams() {
        try {
            params = newParams();
            Camera.Parameters cp = camera.getParameters();
            
            //todo: update each setting
            String flashMode = cp.getFlashMode();
            if (flashMode != null) params.setFlashMode(flashMode);
            
            String colorMode = cp.getColorEffect();
            if (colorMode != null) params.setColorMode(colorMode);
            
            String wb = cp.getWhiteBalance();
            if (wb != null) params.setWhiteBalMode(wb);
            
        } catch (Throwable err) {
            params = null;
            Log.e(TAG, err.toString(), err);
        }
    }

    protected class Params implements AugCameraParameters {
        
        private String flashMode, colorMode, whiteBalMode, sceneMode;
        private Code initCode;

        //
        // Codeable ifc
        //
        @Override
        public Code getCode() throws CodeableException {
            Code code = JSONCoder.newCode();
            if (getFlashMode() != null) code.put("flashMode", getFlashMode());
            if (getColorMode() != null) code.put("colorMode", getColorMode());
            if (getWhiteBalMode() != null) code.put("whiteBal", getWhiteBalMode());
            if (getSceneMode() != null) code.put("sceneMode", getSceneMode());
            //todo: update each setting
            return code;
        }
        @Override
        public void setCode(Code code) throws CodeableException {
            if (code != null) {
                if (code.has("flashMode")) setFlashMode(code.getString("flashMode"));
                if (code.has("colorMode")) setColorMode(code.getString("colorMode"));
                if (code.has("whiteBal")) setWhiteBalMode(code.getString("whiteBal"));
                //warning: scene mode changes other params
                if (code.has("sceneMode")) setSceneMode(code.getString("sceneMode"));
                //todo: update each setting
            }
            initCode = code; //for rollback
        }
        @Override
        public CodeableName getCodeableName() {
            return PARAMS_CODEABLE_NAME;
        }

        //
        // ICS ifc
        //
        @Override
        public int getMaxNumFocusAreas() {
            return 0;
        }
        @Override
        public int getMaxNumMeteringAreas() {
            return 0;
        }
        @Override
        public void rollback() throws CodeableException {
            if (initCode != null) {
                setCode(initCode);
            }
        }
        
        //
        // ifc
        //
        @Override
        public String getFlashMode() {
            if (flashMode == null) flashMode = Camera.Parameters.FLASH_MODE_AUTO;
            return flashMode;
        }
        @Override
        public void setFlashMode(String m) {
            this.flashMode = m;
        }
        
        @Override
        public String getColorMode() {
            if (colorMode == null) colorMode = Camera.Parameters.EFFECT_NONE;
            return colorMode;
        }
        @Override
        public void setColorMode(String m) {
            this.colorMode = m;
        }
        
        @Override
        public String getWhiteBalMode() {
            if (whiteBalMode == null) whiteBalMode = Camera.Parameters.WHITE_BALANCE_AUTO;
            return whiteBalMode;
        }
        @Override
        public void setWhiteBalMode(String m) {
            this.whiteBalMode = m;
        }
        @Override
        public String getSceneMode() {
            return sceneMode;
        }
        @Override
        public void setSceneMode(String m) {
           sceneMode = m; 
        }
    }

    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        if (params != null) {
            Code pcode = params.getCode();
            code.put(Codeable.CODEABLE_NAME_KEY, getCodeableName());
            code.put("params", pcode);
        }
        Log.d(TAG, "ejs simple phone code: " + code);
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        if (code.has("params")) {
            params = newParams();
            Code pcode = code.get("params");
            params.setCode(pcode);
        }
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

    @Override
    public String getName() {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void updateCanvas() { }

    @Override
    public void clear() { }

    @Override
    public void stop() { }

    @Override
    public void resume() { }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers)
            throws AugiementException {
    }

    @Override
    public Set<CodeableName> getDependencyNames() {
        return null;
    }

    protected Camera.Parameters getUpdatedCameraParameters() {
        Log.d(TAG, "ejs updating 2.3 params");
        Camera.Parameters cp = null;
        try {
            cp = camera.getParameters();
            AugCameraParameters p = getParameters();
            if (p != null && cp != null) {
                //todo: update with each 2.3 setting
                String fm = p.getFlashMode();
                if (fm != null) cp.setFlashMode(fm);
                
                String cm = p.getColorMode();
                if (cm != null) cp.setColorEffect(cm);
                
                String wb = p.getWhiteBalMode();
                if (wb != null) cp.setWhiteBalance(wb);
                
                String sm = p.getSceneMode();
                if (sm != null) cp.setSceneMode(sm);
            }
        } catch (Throwable err) {
            Log.e(TAG, err.toString(), err);
        }
        
        return cp;
    }
    @Override
    public void applyParameters() throws AugCameraException {
        Camera.Parameters cp = getUpdatedCameraParameters();
        if (cp != null) {
            try {
                camera.setParameters(getUpdatedCameraParameters());
            } catch (Throwable err) {
                Log.w(TAG, "can not set camera parameters");
                try {
                    getParameters().rollback();
                } catch (CodeableException e) {
                    throw new AugCameraException(e);
                }
                throw new AugCameraException("can not set camera parameter");
            }
        }
    }
}
