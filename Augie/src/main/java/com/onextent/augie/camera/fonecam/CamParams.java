/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.fonecam;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Area;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.android.codeable.Size;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugSysLog;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.ImageFmt;
import com.onextent.augie.camera.NamedInt;

class CamParams implements AugCameraParameters {
    
    private final FoneCam augcamera;

    /**
     * @param simplePhoneCamera
     */
    CamParams(FoneCam simplePhoneCamera) {
        augcamera = simplePhoneCamera;
    }
    
    private String      flashMode, colorMode, whiteBalMode, 
                        sceneMode, focusMode, antibanding;
    private String      xpictureFmt, xISO;
    private NamedInt    pictureFmt, previewFmt;
    private Size        pictureSize, previewSize;
    private Code        initCode;
    private boolean     shutterSnd = true;
    private int         jpegQuality = 0;
    private int         exposureCompensation = 0;
    private int         jpegThumbnailQuality = 0;
    private int         zoom = 0;
    private int         minPrevFPS = 0;
    private int         maxPrevFPS = Integer.MAX_VALUE;
    
    private List<Camera.Area> focusAreas; //transient, not codeable
    private List<Camera.Area> meterAreas; //transient, not codeable

    //
    // Codeable ifc
    //
    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();

        code.put("zoom", getZoom());
        if (getFlashMode() != null) code.put("flashMode", getFlashMode());
        if (getColorMode() != null) code.put("colorMode", getColorMode());
        if (getWhiteBalance() != null) code.put("whiteBal", getWhiteBalance());
        if (getSceneMode() != null) code.put("sceneMode", getSceneMode());
        if (getFocusMode() != null) code.put("focusMode", getFocusMode());
        if (getAntibanding() != null) code.put("antibanding", getAntibanding());
        if (getPictureFmt() != null && getXPictureFmt() == null) code.put("pictureFmt", getPictureFmt().toInt());
        if (getPreviewFmt() != null) code.put("previewFmt", getPreviewFmt().toInt());
        if (getXISO() != null) code.put("xISO", getXISO());
        if (getXPictureFmt() != null) code.put("xpictureFmt", getXPictureFmt());
        code.put("shutterSnd", shutterSnd);
        if (getPictureSize() != null) code.put("pictureSize", getPictureSize().getCode());
        if (getPreviewSize() != null) code.put("previewSize", getPreviewSize().getCode());
        if (getJpegQuality() != 0) code.put("jpegQuality", getJpegQuality());
        if (getJpegThumbnailQuality() != 0) code.put("jpegThumbnailQuality", getJpegThumbnailQuality());
        if (getExposureCompensation() != 0) code.put("expComp", getExposureCompensation());
        int[] r = getPreviewFPSRange();
        code.put("minPrevFPS", r[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]);
        code.put("maxPrevFPS", r[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        return code;
    }
    public void setCode(Code code) throws CodeableException {
        if (code != null) {
            
            if (code.has("zoom")) setZoom(code.getInt("zoom"));
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
            if (code.has("xISO")) setXISO(code.getString("xISO"));
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
            if (code.has("expComp")) setExposureCompensation(code.getInt("expComp"));
            if (code.has("minPrevFPS") && code.has("maxPrevFPS")) {
                int min = code.getInt("minPrevFPS");
                int max = code.getInt("maxPrevFPS");
                setPreviewFPSRange(min, max);
            }
        }
        initCode = code; //save for rollback
    }
    @Override
    public CodeableName getCodeableName() {
        return FoneCam.PARAMS_CODEABLE_NAME;
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        return augcamera.camera.getParameters().getSupportedFlashModes();
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        if (augcamera.camera == null) AugLog.e( "ejs null camera");
        if (augcamera.camera.getParameters() == null) AugLog.e( "ejs null params");
        return augcamera.camera.getParameters().getSupportedColorEffects();
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        return augcamera.camera.getParameters().getSupportedWhiteBalance();
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        return augcamera.camera.getParameters().getSupportedSceneModes();
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        return augcamera.camera.getParameters().getSupportedFocusModes();
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        return augcamera.camera.getParameters().getSupportedAntibanding();
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        List<Integer> cfmts = augcamera.camera.getParameters().getSupportedPictureFormats();
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        List<Integer> cfmts = augcamera.camera.getParameters().getSupportedPreviewFormats();
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        String sl = augcamera.camera.getParameters().get("picture-format-values");
        return FoneCam.split(sl, ',');
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
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        List<Camera.Size> csz = augcamera.camera.getParameters().getSupportedPictureSizes();
        List<Size> l = new ArrayList<Size>();
        for (Camera.Size s : csz) {
            Size sz = new Size(s);
            l.add(sz);
        }
        return l;
    }
    
    @Override
    public Size getPreviewSize() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        if (augcamera != null) { //get real preview size
            Camera.Size cs = augcamera.camera.getParameters().getPreviewSize();
            Size rs = new Size(cs);
            previewSize = rs;
            return rs;
        }
       return previewSize; 
    }
    @Override
    public void setPreviewSize(Size sz) {
        previewSize = sz;
    }
    @Override
    public List<Size> getSupportedPreviewSizes() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        List<Camera.Size> csz = augcamera.camera.getParameters().getSupportedPreviewSizes();
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
    @Override
    public List<Area> getFocusAreas() {
        return focusAreas;
    }
    @Override
    public void setFocusAreas(List<Area> areas) {
        if (areas != null && areas.isEmpty()) areas = null;
        focusAreas = areas;
    }
    @Override
    public List<Area> getMeteringAreas() {
        return meterAreas;
    }
    @Override
    public void setMeteringAreas(List<Area> areas) {
        if (areas != null && areas.isEmpty()) areas = null;
        meterAreas = areas;
    }
    @Override
    public int getMinExposureCompensation() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return 0;
        }
        return augcamera.camera.getParameters().getMinExposureCompensation();
    }
    @Override
    public int getMaxExposureCompensation() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return 0;
        }
        return augcamera.camera.getParameters().getMaxExposureCompensation();
    }
    @Override
    public int getExposureCompensation() {
        return exposureCompensation;
    }
    @Override
    public float getExposureCompensationStep() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return 0;
        }
        return augcamera.camera.getParameters().getExposureCompensationStep();
    }
    @Override
    public void setExposureCompensation(int ec) {
        exposureCompensation = ec;
    }
    @Override
    public int getMaxZoom() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return 0;
        }
        return augcamera.camera.getParameters().getMaxZoom();
    }
    @Override
    public int getZoom() {
        return zoom;
    }
    @Override
    public void setZoom(int z) {
        zoom = z;
    }
    @Override
    public boolean isZoomSupported() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return false;
        }
        return augcamera.camera.getParameters().isZoomSupported();
    }
    @Override
    public List<int[]>getSupportedPreviewFPSRanges() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        return augcamera.camera.getParameters().getSupportedPreviewFpsRange();
    }
    @Override
    public int[] getPreviewFPSRange() {
        int[] range = new int[2];
        range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] = minPrevFPS;
        range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] = maxPrevFPS;
        return range;
    }
    @Override 
    public void setPreviewFPSRange(int min, int max) {
        minPrevFPS = min;
        maxPrevFPS = max;
    }
	@Override
	public String getXISO() {

		return xISO;
	}
	@Override
	public void setXISO(String m) {
		xISO = m;
	}
	@Override
	public List<String> getXSupportedISOs() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return null;
        }
        String sl = augcamera.camera.getParameters().get("iso-values");
        if (sl == null) return null;
        return FoneCam.split(sl, ',');
	}
   
	@Override
    public int getMaxNumFocusAreas() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return 0;
        }
        if (this.augcamera.camera != null)
            return this.augcamera.camera.getParameters().getMaxNumFocusAreas();
        else {
            AugLog.w("null camera while looking max focus areas");
            return 0;
        }
    }

    @Override
    public int getMaxNumMeteringAreas() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return 0;
        }
        if (this.augcamera.camera != null)
            return this.augcamera.camera.getParameters().getMaxNumMeteringAreas();
        else {
            AugLog.w("null camera while looking max meter areas");
            return 0;
        }
    }
    
    @Override
    public int getMaxNumDetectedFaces() {
        if (this.augcamera == null || augcamera.camera == null) {
            AugSysLog.w("camera is not set");
            return 0;
        }
        if (this.augcamera.camera != null)
            return this.augcamera.camera.getParameters().getMaxNumDetectedFaces();
        else {
            AugLog.w("null camera while looking for faces");
            return 0;
        }
    }
}
