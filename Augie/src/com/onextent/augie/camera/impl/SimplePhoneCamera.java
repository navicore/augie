/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.android.codeable.Size;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugFocusCallback;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugPreviewCallback;
import com.onextent.augie.camera.AugShutterCallback;
import com.onextent.augie.camera.CameraName;
import com.onextent.augie.camera.ImageFmt;
import com.onextent.augie.camera.NamedInt;

public class SimplePhoneCamera extends AbstractPhoneCamera {
    
    static final CodeableName PARAMS_CODEABLE_NAME = new CodeableName("/AUGIE/CAMERA/PARAMS"){};
	protected Camera camera;
	
	protected final int cameraId;
	protected final CameraName cameraName;
	protected AugPreviewCallback previewCb;
	protected AugPreviewCallback previewCbWB;
	
	private CamParams params;
	private List<byte[]> buffers;
	
	SimplePhoneCamera(int id) {
	    cameraId = id;
        cameraName = new CameraName("/AUGIE/CAMERA_ID_" + getId());
	}

	private final static int NBUFFERS = 3;
	private void initBuffers() {
        Parameters p = camera.getParameters();
                
        android.hardware.Camera.Size sz = p.getPreviewSize();
        int format = p.getPreviewFormat();
        int bsz;
        if (format == ImageFormat.YV12) {
            int width = sz.width;
            int height = sz.height;
            int yStride   = (int) Math.ceil(width / 16.0) * 16;
            int uvStride  = (int) Math.ceil( (yStride / 2) / 16.0) * 16;
            int ySize     = yStride * height;
            int uvSize    = uvStride * height / 2;
            bsz = ySize + uvSize * 2;
        } else {
            float bpp = ImageFormat.getBitsPerPixel(format);
            float bytepp = bpp / 8;
            bsz = (int) (sz.width * sz.height * bytepp);
        }
        for (int i = 0; i < NBUFFERS; i++ ) {
            byte b[] = new byte[bsz];
            camera.addCallbackBuffer(b);
        }
	}

    @Override
	public void open() throws AugCameraException {
    
        if (camera != null) return;
        try {
            AugLog.d( "open camera with id: " + getId());
            camera = Camera.open(getId());

            if (previewCbWB != null) {
                initBuffers();
                camera.setPreviewCallbackWithBuffer(previewCbWB);
                previewCbWB = null;
            }
            if (previewCb != null) {
                camera.setPreviewCallback(previewCb);
                previewCb = null;
            }
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
        camera.setPreviewCallback(null);
        camera.stopPreview();
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
        if (camera == null) return;
        try {
            camera.setPreviewCallback(null);
            camera.stopPreview();
        } catch (Exception e) {
            throw new AugCameraException(e);
        }
    }
    
    @Override
    public void focus(final AugFocusCallback cb) throws AugCameraException {
    	
    	if (camera == null) throw new AugCameraException("camera is null");
       
        AutoFocusCallback fcb = new AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
               
                try {
                    
                    cb.onFocus(success);
                } catch (Throwable err) {
                    AugLog.e(err);
                }
            }
        };
      
        try {
        	camera.autoFocus(fcb);
        } catch (Throwable err) {
        	throw new AugCameraException(err);
        }
    }
    
    @Override
    public void takePicture(final AugShutterCallback shutter, 
                            final AugPictureCallback raw,
                            final AugPictureCallback jpeg) throws AugCameraException {
        Camera.ShutterCallback scb = null;
        Camera.PictureCallback rcb = null;
        Camera.PictureCallback jcb = null;
        
        if (shutter != null)
        scb = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                
                AugLog.d( "shutter callback");
                if (shutter != null) shutter.onShutter();
            }
        };
        if (raw != null)
        rcb = new Camera.PictureCallback() {
            
            @Override
            public void onPictureTaken(byte[] data, Camera c) {
                try {
                    AugLog.d( "raw callback");
                    if (raw != null) raw.onPictureTaken(data, SimplePhoneCamera.this);
                } finally {
                    unlock();
                }
            }
        };
        if (jpeg != null)
        jcb = new Camera.PictureCallback() {
            
            @Override
            public void onPictureTaken(byte[] data, Camera c) {
                try {
                    AugLog.d( "jpg callback");
                    if (jpeg != null) jpeg.onPictureTaken(data, SimplePhoneCamera.this);
                } finally {
                    unlock();
                }
            }
        };
        if (rcb == null && jcb == null) throw new AugCameraException("no callbacks");
        boolean locked = lock();
        if (!locked) throw new AugCameraException("camera busy");
        try {
            AugLog.d( "camera taking picture");
            camera.takePicture(scb, rcb, jcb);
        } catch (Throwable err) {
            unlock();
        }
    }
    
    private boolean isLocked = false;
    private synchronized boolean lock() {
        if (isLocked) return false;
        AugLog.d( "locking camera");
        isLocked = true;
        return true;
    }
    private synchronized void unlock() {
        AugLog.d( "unlocking camera");
        isLocked = false;
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
            m = cp.get("iso");
            if (m != null) {
                params.setXISO(m);
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
            
            int[] range = new int[2];
            cp.getPreviewFpsRange(range);
            params.setPreviewFPSRange(range[Parameters.PREVIEW_FPS_MIN_INDEX], 
                                      range[Parameters.PREVIEW_FPS_MAX_INDEX]);
            
        } catch (Throwable err) {
            params = null;
            AugLog.e( err.toString(), err);
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
                
                m = p.getXISO();
                if (m != null) cp.set("iso", m);
                
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
                
                int[] r = p.getPreviewFPSRange();
                if (r != null) p.setPreviewFPSRange(r[0], r[1]);
            }
            
        } catch (Throwable err) {
            AugLog.e( err.toString(), err);
        }
        
        return cp;
    }
    
    protected final void __applyParameters(Camera.Parameters cp) throws AugCameraException {
        if (cp != null) {
            try {

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

    @Override
    public boolean isOpen() {
        return camera != null;
    }
    
    @Override
    public void setPreviewCallback(AugPreviewCallback cb) {
        if (camera != null) {
            camera.setPreviewCallback(cb);
        } else {
            previewCb = cb;
        }
    }

    @Override
    public void setPreviewCallbackWithBuffer(AugPreviewCallback cb) {
        if (camera != null) {
            camera.setPreviewCallbackWithBuffer(cb);
        } else {
            previewCbWB = cb;
        }
    }

    @Override
    public void addCallbackBuffer(byte[] b) {
        if (camera == null) {
            buffers = new ArrayList<byte[]>();
            buffers.add(b);
        } else {
            camera.addCallbackBuffer(b);
        }
    }

    @Override
    public DialogFragment getUI() {
        return null;
    }
    
    public static final Meta META =
            new Meta() {

        @Override
        public Class<? extends Augiement> getAugiementClass() {
            return null;
        }

        @Override
        public CodeableName getCodeableName() {
            return AugCamera.AUGIENAME;
        }

        @Override
        public String getUIName() {
            return null;
        }

        @Override
        public String getDescription() {
            return "Phone Camera";
        }

        @Override
        public Set<CodeableName> getDependencyNames() {
            return null;
        }
    };

    @Override
    public Meta getMeta() {
        return null;
    }
}
