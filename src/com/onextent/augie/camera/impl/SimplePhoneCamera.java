/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.hardware.Camera;
import android.text.TextUtils;
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
import com.onextent.augie.camera.ImageFmt;
import com.onextent.augie.camera.NamedInt;
import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.JSONCoder;
import com.onextent.util.codeable.Size;

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
            
        } catch (Throwable err) {
            params = null;
            Log.e(TAG, err.toString(), err);
        }
    }

    protected class Params implements AugCameraParameters {
        
        private String flashMode, colorMode, whiteBalMode, sceneMode, focusMode, antibanding;
        private String xpictureFmt;
        private NamedInt pictureFmt, previewFmt;
        private Size pictureSize, previewSize;
        private Code initCode;
        private boolean shutterSnd = true;
        private int jpegQuality = 0;
        private int jpegThumbnailQuality = 0;

        //
        // Codeable ifc
        //
        @Override
        public Code getCode() throws CodeableException {
            Code code = JSONCoder.newCode();
            //todo: update each setting
            if (getFlashMode() != null) code.put("flashMode", getFlashMode());
            if (getColorMode() != null) code.put("colorMode", getColorMode());
            if (getWhiteBalance() != null) code.put("whiteBal", getWhiteBalance());
            if (getSceneMode() != null) code.put("sceneMode", getSceneMode());
            if (getFocusMode() != null) code.put("focusMode", getFocusMode());
            if (getAntibanding() != null) code.put("antibanding", getAntibanding());
            if (getPictureFmt() != null && getXPictureFmt() == null) code.put("pictureFmt", getPictureFmt().toInt());
            if (getPreviewFmt() != null) code.put("previewFmt", getPreviewFmt().toInt());
            if (getXPictureFmt() != null) code.put("xpictureFmt", getXPictureFmt());
            code.put("shutterSnd", shutterSnd);
            if (getPictureSize() != null) code.put("pictureSize", getPictureSize().getCode());
            if (getPreviewSize() != null) code.put("previewSize", getPreviewSize().getCode());
            if (getJpegQuality() != 0) code.put("jpegQuality", getJpegQuality());
            if (getJpegThumbnailQuality() != 0) code.put("jpegThumbnailQuality", getJpegThumbnailQuality());
            return code;
        }
        public void setCode(Code code) throws CodeableException {
            if (code != null) {
                //todo: update each setting
                if (code.has("flashMode")) setFlashMode(code.getString("flashMode"));
                if (code.has("colorMode")) setColorMode(code.getString("colorMode"));
                if (code.has("whiteBal")) setWhiteBalance(code.getString("whiteBal"));
                if (code.has("sceneMode")) setSceneMode(code.getString("sceneMode"));
                if (code.has("focusMode")) setFocusMode(code.getString("focusMode"));
                if (code.has("antibanding")) setAntibanding(code.getString("antibanding"));
                if (code.has("pictureFmt")) setPictureFmt(new ImageFmt(code.getInt("pictureFmt")));
                if (code.has("previewFmt")) setPreviewFmt(new ImageFmt(code.getInt("previewFmt")));
                if (code.has("shutterSnd")) setShutterSound(code.getBoolean("shutterSnd"));
                if (code.has("xpictureFmt")) setXPictureFmt(code.getString("xpictureFmt"));
                if (code.has("pictureSize")) {
                    Size sz = new Size();
                    sz.setCode(code.get("pictureSize"));
                    setPictureSize(sz);
                }
                if (code.has("previewSize")) {
                    Size sz = new Size();
                    sz.setCode(code.get("previewSize"));
                    setPreviewSize(sz);
                }
                if (code.has("jpegQuality")) setJpegQuality(code.getInt("jpegQuality"));
                if (code.has("jpegThumbnailQuality")) setJpegThumbnailQuality(code.getInt("jpegThumbnailQuality"));
            }
            initCode = code; //save for rollback
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
            return flashMode;
        }
        @Override
        public void setFlashMode(String m) {
            this.flashMode = m;
        }
        @Override
        public List<String> getSupportedFlashModes() {
            return camera.getParameters().getSupportedFlashModes();
        }
        
        @Override
        public String getColorMode() {
            return colorMode;
        }
        @Override
        public void setColorMode(String m) {
            this.colorMode = m;
        }
        @Override
        public List<String> getSupportedColorModes() {
            return camera.getParameters().getSupportedColorEffects();
        }
        
        @Override
        public String getWhiteBalance() {
            return whiteBalMode;
        }
        @Override
        public void setWhiteBalance(String m) {
            this.whiteBalMode = m;
        }
        @Override
        public List<String> getSupportedWhiteBalances() {
            return camera.getParameters().getSupportedWhiteBalance();
        }
        
        @Override
        public String getSceneMode() {
            return sceneMode;
        }
        @Override
        public void setSceneMode(String m) {
           sceneMode = m; 
        }
        @Override
        public List<String> getSupportedSceneModes() {
            return camera.getParameters().getSupportedSceneModes();
        }
        
        @Override
        public String getFocusMode() {
            return focusMode;
        }
        @Override
        public void setFocusMode(String m) {
           focusMode = m; 
        }
        @Override
        public List<String> getSupportedFocusModes() {
            return camera.getParameters().getSupportedFocusModes();
        }
        
        @Override
        public String getAntibanding() {
            return antibanding;
        }
        @Override
        public void setAntibanding(String m) {
            antibanding = m; 
        }
        @Override
        public List<String> getSupportedAntibanding() {
            return camera.getParameters().getSupportedAntibanding();
        }
        
        @Override
        public NamedInt getPictureFmt() {
            return pictureFmt;
        }
        @Override
        public void setPictureFmt(NamedInt f) {
            pictureFmt = f;
        }
        @Override
        public List<NamedInt> getSupportedPictureFmts() {
            List<Integer> cfmts = camera.getParameters().getSupportedPictureFormats();
            List<NamedInt> list = new ArrayList<NamedInt>();
            for (int i : cfmts) {
                NamedInt f = new ImageFmt(i);
                list.add(f);
            }
            return list;
        }
        
        @Override
        public NamedInt getPreviewFmt() {
            return previewFmt;
        }
        @Override
        public void setPreviewFmt(NamedInt f) {
            previewFmt = f;            
        }
        @Override
        public List<NamedInt> getSupportedPreviewFmts() {
            List<Integer> cfmts = camera.getParameters().getSupportedPreviewFormats();
            List<NamedInt> list = new ArrayList<NamedInt>();
            for (int i : cfmts) {
                NamedInt f = new ImageFmt(i);
                list.add(f);
            }
            return list;
        }
        @Override
        public void setShutterSound(boolean enable) {
            shutterSnd = enable;
        }
        @Override
        public boolean getShutterSound() {
            return shutterSnd;
        }
        
        //
        //begin X methods... 
        // warning: these keys are not android api safe and may change in the future
        //
        @Override
        public String getXPictureFmt() {
            return xpictureFmt;
        }
        @Override
        public void setXPictureFmt(String f) {
            xpictureFmt = f;
        }
        @Override
        public List<String> getXSupportedPictureFmts() {
            String sl = camera.getParameters().get("picture-format-values");
            return split(sl, ',');
        }
       
        @Override
        public Size getPictureSize() {
           return pictureSize; 
        }
        @Override
        public void setPictureSize(Size sz) {
            pictureSize = sz;
        }
        @Override
        public List<Size> getSupportedPictureSizes() {
            List<Camera.Size> csz = camera.getParameters().getSupportedPictureSizes();
            List<Size> l = new ArrayList<Size>();
            for (Camera.Size s : csz) {
                Size sz = new Size(s);
                l.add(sz);
            }
            return l;
        }
        
        @Override
        public Size getPreviewSize() {
           return previewSize; 
        }
        @Override
        public void setPreviewSize(Size sz) {
            previewSize = sz;
        }
        @Override
        public List<Size> getSupportedPreviewSizes() {
            List<Camera.Size> csz = camera.getParameters().getSupportedPreviewSizes();
            List<Size> l = new ArrayList<Size>();
            for (Camera.Size s : csz) {
                Size sz = new Size(s);
                l.add(sz);
            }
            return l;
        }
        
        @Override
        public int getJpegQuality() {
            return jpegQuality;
        }
        @Override
        public void setJpegQuality(int q) {
           jpegQuality = q;
        }
        
        @Override
        public int getJpegThumbnailQuality() {
            return jpegThumbnailQuality;
        }
        @Override
        public void setJpegThumbnailQuality(int q) {
           jpegThumbnailQuality = q;
        }
    }

    private static ArrayList<String> split(String str, Character c) {
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
                Log.d(TAG, "ejs flattened params: " + cp.flatten());
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

    @Override
    public String flatten() {
        return camera.getParameters().flatten();
    }
}
