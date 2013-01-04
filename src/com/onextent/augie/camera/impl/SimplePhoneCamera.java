/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugFocusCallback;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugShutterCallback;
import com.onextent.augie.camera.CameraName;
import com.onextent.augie.camera.ImageFmt;
import com.onextent.augie.camera.NamedInt;
import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.JSONCoder;
import com.onextent.util.codeable.Size;

public class SimplePhoneCamera extends AbstractPhoneCamera {
    
    static final CodeableName PARAMS_CODEABLE_NAME = new CodeableName("/AUGIE/CAMERA/PARAMS"){};
	protected Camera camera;
	
	protected final int cameraId;
	protected final CameraName cameraName;
	
	private CamParams params;
	
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
    public void focus(final AugFocusCallback cb) {
       
        AutoFocusCallback fcb = new AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                
                cb.onFocus(success);
            }
        };
        
        camera.autoFocus(fcb);
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
    
    protected CamParams newParams() {
            CamParams p = new CamParams(this);
            return p;
    }
    
    @Override
    public void initParams() {
        try {
            params = newParams();
            Camera.Parameters cp = camera.getParameters();
            
            //todo: update each setting
            String m = cp.getFlashMode();
            if (m != null) params.setFlashMode(m);
            
            m = cp.getColorEffect();
            if (m != null) params.setColorMode(m);
            
            m = cp.getWhiteBalance();
            if (m != null) params.setWhiteBalance(m);
            
            m = cp.getFocusMode();
            if (m != null) params.setFocusMode(m);
            
            m = cp.getAntibanding();
            if (m != null) params.setAntibanding(m);
            
            int v = cp.getPictureFormat();
            NamedInt f = new ImageFmt(v);
            params.setPictureFmt(f);
            
            v = cp.getPreviewFormat();
            f = new ImageFmt(v);
            params.setPreviewFmt(f);
            
            m = cp.get("picture-format");
            if (m != null) {
                params.setPictureFmt(null);
                params.setXPictureFmt(m);
            }
            Camera.Size sz = cp.getPictureSize();
            if (sz != null) params.setPictureSize(new Size(sz));
            
            sz = cp.getPreviewSize();
            if (sz != null) params.setPreviewSize(new Size(sz));
            
            int i = cp.getJpegQuality();
            if (i > 0) params.setJpegQuality(i);
            
            i = cp.getJpegThumbnailQuality();
            if (i > 0) params.setJpegThumbnailQuality(i);
            
            i = cp.getExposureCompensation();
            if (i != 0) params.setExposureCompensation(i);
            
            i = cp.getZoom();
            if (i != 0) params.setZoom(i);
            
        } catch (Throwable err) {
            params = null;
            Log.e(TAG, err.toString(), err);
        }
    }

    static ArrayList<String> split(String str, Character c) {
        if (str == null) return null;
        TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(c);
        splitter.setString(str);
        ArrayList<String> substrings = new ArrayList<String>();
        for (String s : splitter) {
            substrings.add(s);
        }
        return substrings;
    }
    
    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        if (params != null) {
            Code pcode = params.getCode();
            code.put(Codeable.CODEABLE_NAME_KEY, getCodeableName());
            code.put("params", pcode);
        }
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
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public Meta getMeta() {
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
        Camera.Parameters cp = null;
        try {
            cp = camera.getParameters();
            AugCameraParameters p = getParameters();
            if (p != null && cp != null) {
                //todo: update with each 2.3 setting
                String m;
                m = p.getFlashMode();
                if (m != null) cp.setFlashMode(m);
                
                m = p.getColorMode();
                if (m != null) cp.setColorEffect(m);
                
                m = p.getWhiteBalance();
                if (m != null) cp.setWhiteBalance(m);
                
                m = p.getSceneMode();
                if (m != null) cp.setSceneMode(m);
                
                m = p.getFocusMode();
                if (m != null) cp.setFocusMode(m);
                
                m = p.getAntibanding();
                if (m != null) cp.setAntibanding(m);
                
                NamedInt f = p.getPictureFmt();
                if (f != null) cp.setPictureFormat(f.toInt());
                
                f = p.getPreviewFmt();
                if (f != null) cp.setPreviewFormat(f.toInt());
                
                m = p.getXPictureFmt();
                if (m != null) cp.set("picture-format", m);
                
                Size sz = p.getPictureSize();
                if (sz != null) cp.setPictureSize(sz.getWidth(), sz.getHeight());
                
                sz = p.getPreviewSize();
                if (sz != null) cp.setPreviewSize(sz.getWidth(), sz.getHeight());
                
                int i = p.getJpegQuality();
                if (i > 0) cp.setJpegQuality(i);
                
                i = p.getJpegThumbnailQuality();
                if (i > 0) cp.setJpegThumbnailQuality(i);
                
                i = p.getExposureCompensation();
                if (i != 0) cp.setExposureCompensation(i);
                
                i = p.getZoom();
                if (i != 0) cp.setZoom(i);
            }
            
        } catch (Throwable err) {
            Log.e(TAG, err.toString(), err);
        }
        
        return cp;
    }
    
    protected final void __applyParameters(Camera.Parameters cp) throws AugCameraException {
        if (cp != null) {
            try {
                //Log.d(TAG, "flattened params: " + cp.flatten());
                camera.setParameters(getUpdatedCameraParameters());
            } catch (Throwable err) {
                Log.w(TAG, "can not set camera parameters: " + err);
                try {
                    getParameters().rollback();
                } catch (CodeableException e) {
                    throw new AugCameraException(e);
                }
                throw new AugCameraException(err);
            }
        }
    }

    @Override
    public void applyParameters() throws AugCameraException {
        Camera.Parameters cp = getUpdatedCameraParameters();
        __applyParameters(cp);
    }

    @Override
    public String flatten() {
        return camera.getParameters().flatten();
    }
}
